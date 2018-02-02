package com.spd.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.config.CommonConfig;
import com.spd.dao.cq.impl.ForecastDataDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 预报报文解析
 * @author Administrator
 *
 */
public class ForecastdataSync {

	public void sync(String datetime) {
		//1. 找到需要同步的文件
		List<String> fileNameList = getFileNamesByDatetime(datetime.substring(4, 8));
		//2. 解析文件
		List allList = new ArrayList();
		for(String fileName : fileNameList) {
			List dataList = analyst(fileName);
			allList.addAll(dataList);
		}
		//3 .insert 或者 update数据
		ForecastDataDao forecastDataDao = new ForecastDataDao();
		forecastDataDao.insertValues(allList, datetime.substring(0, 4) + "-" + datetime.substring(4, 6) + "-" + datetime.substring(6, 8));
	}
	
	private List analyst(String fileName) {
		String filePath = CommonConfig.ACQPATH + "/" + fileName;
		BufferedReader br = null;
		int index = 0;
		List dataList = new ArrayList();
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line = null;
			String ForscastDate = null;
			int hourSpan = 0;
			while((line = br.readLine()) != null) {
				index++;
				if(index == 1) continue;
				if(index == 2) {
					String[] array = line.split("\\s+");
					ForscastDate = array[0] + "-" + array[1] + "-" + array[2] + " " + array[3] + ":00:00";
					hourSpan = Integer.parseInt(array[4]);
				} else {
					//解析数据
					String[] array = line.split("\\s+");
					if(array == null || array.length != 12) continue;
					String station_Id_C = array[0];
					Double alti = Double.parseDouble(array[3]);
					int weatherState12 = Integer.parseInt(array[4]);
					int weatherState24 = Integer.parseInt(array[9]);
					Double minTmp = Double.parseDouble(array[7]);
					Double maxTmp = Double.parseDouble(array[8]);
					//TODO 需要计算
//					Double PreTime12, PreTime24;
					HashMap dataMap = new HashMap();
					dataMap.put("Station_Id_C", station_Id_C);
					dataMap.put("Alti", alti);
					dataMap.put("WeatherState12", weatherState12);
					dataMap.put("WeatherState24", weatherState24);
					dataMap.put("MaxTmp", maxTmp);
					dataMap.put("MinTmp", minTmp);
					dataMap.put("ForscastDate", ForscastDate);
					dataMap.put("FutureDate", addHours(ForscastDate, hourSpan));
					dataMap.put("HourSpan", hourSpan);
					//TODO 还需要计算
//					dataMap.put("PreTime12", null);
//					dataMap.put("PreTime24", null);
					dataList.add(dataMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataList;
	}
	
	private String addHours(String forscastDateStr, int hourspan) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date forscastDate = null;
		try {
			forscastDate = sdf.parse(forscastDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date futureDate = new Date(forscastDate.getTime() + hourspan * CommonConstant.HOURTIMES);
		return sdf.format(futureDate);
	}
	
	private List<String> getFileNamesByDatetime(String datetime) {
		List<String> fileNameList = new ArrayList<String>();
		String acqFilePath = CommonConfig.ACQPATH;
		String[] fileNames = new String[]{"Fo" + datetime + "oe.acq", "Fo" + datetime + "oi.acq", "Fo" + datetime + "ok.acq"};
		for(int i = 0; i < fileNames.length; i++) {
			File file = new File(acqFilePath + "/" + fileNames[i]);
			if(file.exists()) {
				fileNameList.add(fileNames[i]);
			}
		}
		return fileNameList;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		ForecastdataSync forecastdataSync = new ForecastdataSync();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
		String datetime = sdf.format(date);
		String preDatetime = sdf.format(preDate);
		forecastdataSync.sync(datetime);
		forecastdataSync.sync(preDatetime);
		//测试
//		ForecastdataSync forecastdataSync = new ForecastdataSync();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		PropertiesUtil.loadSysCofing();
//		String startTime = "20160820";
//		String endTime = "20160921";
//		Date startDate = null, endDate = null;
//		try {
//			startDate = sdf.parse(startTime);
//			endDate = sdf.parse(endTime);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
//			String datetime = sdf.format(new Date(i));
//			System.out.println(datetime);
//			forecastdataSync.sync(datetime);
//		}
	}

}
