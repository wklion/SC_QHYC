package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.cq.impl.HouTmpAvgDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.HouCalc;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;
import com.spd.tool.StationArea;

/**
 * 候统计相关
 * @author Administrator
 *
 */
public class HouStatistics {

	private HouTmpAvgDao houTmpAvgDao = new HouTmpAvgDao();
	
	/**
	 * 统计候气温，补录
	 */
	public void houTmpRecordStatistics(int startYear, int endYear) {
		List dataList = new ArrayList();
		StationArea stationArea = new StationArea(); 
		Map<String, String> stationMap = stationArea.getStationAreaMap();
		String items = CommonTool.getAllItems();
		HashMap<String, Object> existData = houTmpAvgDao.getExistTemAvg(startYear, endYear);
		List resultList = houTmpAvgDao.getTmpAvgHou(items, startYear, endYear);
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			for(int month = 1; month <= 12; month++) {
				for(int index = 1; index <= 6; index++) {
					Map<String, Object> mapData = new HashMap<String, Object>();
					mapData.put("Station_Id_C", station_Id_C);
					mapData.put("Station_Name", station_Name);
					mapData.put("Lon", lon);
					mapData.put("Lat", lat);
					mapData.put("area", stationMap.get(station_Id_C));
					mapData.put("year", year);
					mapData.put("month", month);
					mapData.put("hou", index);
					List<String> houNameList = HouCalc.getHouListByMonth(year, month, index);
					int validDayCnt = 0;
					double sum = 0;
					for(String houName : houNameList) {
						Double value = (Double) tempMap.get(houName);
						if(value == null ||value < CommonConstant.MININVALID || value > CommonConstant.MAXINVALID ) {
							continue;
						}
						validDayCnt++;
						sum += value;
					}
					if(validDayCnt == 0) {
						continue;
					}
					double avgTmp = CommonTool.roundDouble(sum / validDayCnt);
					mapData.put("avgTmp", avgTmp);
					String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("year") + "_" + month + "_" + index;
					if(!existData.containsKey(key)) {
						dataList.add(mapData);
					}
				}
			}
		}
		//入库
		houTmpAvgDao.insertTemAvgHouValue(dataList);
	}
	
	/**
	 * 实时同步
	 */
	public void houTmpSyncStatistics() {
		StationArea stationArea = new StationArea(); 
		Map<String, String> stationMap = stationArea.getStationAreaMap();
		SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
//		date = new Date(date.getTime() - CommonConstant.DAYTIMES); // 往前推一天
//		String testStr = "20160421";
//		Date date = null;
//		try {
//			date = sdfyyyyMMdd.parse(testStr);
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		long time = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		SimpleDateFormat sdfMMDD = new SimpleDateFormat("MMdd");
		SimpleDateFormat sdfyyyyMM = new SimpleDateFormat("yyyyMM");
		
		SimpleDateFormat sdfyyyy = new SimpleDateFormat("yyyy");
		int day = Integer.parseInt(sdf.format(date));
		int endYear = Integer.parseInt(sdfyyyy.format(date));
		int startYear = endYear - 1;
		HashMap<String, Object> existData = houTmpAvgDao.getExistTemAvg(startYear, endYear);
		String items = "";
		long start = 0L, end = 0L;
		List dataList = new ArrayList();
		int month = 0, hou = 0;
		if(day == 1) {
			//统计上一个月
			end = time - CommonConstant.DAYTIMES; // 上一个月的最后一天
			//上个月的26号
			String yyyyMM = sdfyyyyMM.format(new Date(end));
			String startStr = yyyyMM + "26";
			try {
				start = sdfyyyyMMdd.parse(startStr).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(day == 6 || day == 11 || day == 16 || day == 21 || day == 26) {
			//需要同步
			start = time - 5 * CommonConstant.DAYTIMES;
			end = time - CommonConstant.DAYTIMES;
		}
		
		if(start !=0L && end != 0L) {
			// 计算月、候
			Date tempStartDate = new Date(start);
			String tempMMdd = sdfMMDD.format(tempStartDate);
			month = Integer.parseInt(tempMMdd.substring(0, 2));
			hou = HouCalc.getHouIndexByDay(Integer.parseInt(tempMMdd.substring(2, 4)));
			
			for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
				Date tempDate = new Date(i);
				String mmdd = sdfMMDD.format(tempDate);
				String field = "m" + mmdd.substring(0, 2) + "d" + mmdd.substring(2, 4);
				items += field;
				items += ",";
			}
			items = items.substring(0, items.length() - 1);
			String[] itemArray = items.split(",");
			List resultList = houTmpAvgDao.getTmpAvgHou(items, startYear, endYear); // 结果
			for(int i = 0; i < resultList.size(); i++) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				HashMap tempMap = (HashMap) resultList.get(i);
				int year = (Integer) tempMap.get("year");
				String station_Id_C = (String) tempMap.get("Station_Id_C");
				String station_Name = (String) tempMap.get("Station_Name");
				double lon = (Double) tempMap.get("Lon");
				double lat = (Double) tempMap.get("Lat");
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("area", stationMap.get(station_Id_C));
				mapData.put("year", year);
				mapData.put("month", month);
				mapData.put("hou", hou);
				int validDayCnt = 0;
				double sum = 0;
				for(int j = 0; j < itemArray.length; j++) {
					Double value = (Double) tempMap.get(itemArray[j]);
					if(value == null || value < CommonConstant.MININVALID || value > CommonConstant.MAXINVALID) {
						continue;
					}
					validDayCnt++;
					sum += value;
				}
				if(validDayCnt == 0) {
					continue;
				}
				double avgTmp = CommonTool.roundDouble(sum / validDayCnt);
				mapData.put("avgTmp", avgTmp);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("year") + "_" + month + "_" + hou;
				if(!existData.containsKey(key)) {
					dataList.add(mapData);
				}
			}
			//入库
			houTmpAvgDao.insertTemAvgHouValue(dataList);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LogTool.logger.error("houstatistics");
		PropertiesUtil.loadSysCofing();
		HouStatistics houStatistics = new HouStatistics();
		//补录
//		for(int i = 1951; i <= 2016; i++) {
//			System.out.println(i);
//			houStatistics.houTmpRecordStatistics(i, i);
//		}
		//实况
		houStatistics.houTmpSyncStatistics();
	}

}
