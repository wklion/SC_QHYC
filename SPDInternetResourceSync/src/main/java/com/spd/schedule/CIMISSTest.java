package com.spd.schedule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.spd.cimiss.CIMISSRest;

public class CIMISSTest {

	private static String resultPath = "";
	
	private String hourStr = "http://10.230.89.17:8008/cimiss-web/api?userId=BECQ_QHZX_byy" +
			"&pwd=qhzxbyy&interfaceId=getSurfEleInRegionByTime&dataCode=SURF_CHN_MUL_HOR&times=" +
			"%s&adminCodes=500000&" +
			"elements=Station_Id_C,Lat,Lon,Year,Mon,Day,Hour,WIN_D_Avg_2mi,WIN_S_Avg_2mi,CLO_Cov,CLO_Cov_Low&staLevels=011,012,013,014&dataFormat=json";
	
	private String dayStr = "http://10.230.89.55/cimiss-web/api?userId=BECQ_QHZX_byy&pwd=qhzxbyy&interfaceId=" +
			"getSurfEleInRegionByTimeRange&dataCode=SURF_CHN_MUL_DAY&elements=Station_Id_C,Lat,Lon,PRE_Time_2020,Year,Mon,Day" +
			"&timeRange=[%s,%s]" +
			"&orderby=Datetime:ASC&staLevels=011,012,013,014&adminCodes=500000&dataFormat=json";
	/**
	 * 取到小时数据
	 */
	private String getHourDate(String dateStr) {
		String cimissHourStrURL = String.format(hourStr, dateStr);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(cimissHourStrURL);
		return result;
	}
	
	/**
	 * 取到日值降水数据
	 */
	private String getDayRain(String dateStr) {
		String cimissDayURL = String.format(dayStr, dateStr, dateStr);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(cimissDayURL);
		return result;
	}
	
	/**
	 * 处理小时数据，日值降水，然后整理写入文件
	 */
	private void dispose(String hourStr, String dateStr) {
		List<Map> resultList = new ArrayList<Map>();
		//小时数据的处理
		Gson gson = new Gson();
		Object o = gson.fromJson(hourStr, Object.class);
		List insertDataList = new ArrayList();
		com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) o;
		ArrayList ds = (ArrayList) linkedTreeMap.get("DS");
		if(ds != null) {
			for(int i=0; i<ds.size(); i++) {
				com.google.gson.internal.LinkedTreeMap item = (com.google.gson.internal.LinkedTreeMap)ds.get(i);
				String Station_Id_C = (String) item.get("Station_Id_C");
				if(Station_Id_C.startsWith("A")) continue;
				Double Lat = Double.parseDouble((String)item.get("Lat"));
				Double Lon = Double.parseDouble((String)item.get("Lon"));
				Integer Year = Integer.parseInt((String)item.get("Year"));
				Integer Mon = Integer.parseInt((String)item.get("Mon"));
				Integer Day = Integer.parseInt((String)item.get("Day"));
				Integer Hour = Integer.parseInt((String)item.get("Hour"));
				Integer WIN_D_Avg_2mi = Integer.parseInt((String)item.get("WIN_D_Avg_2mi"));
				Double WIN_S_Avg_2mi = Double.parseDouble((String)item.get("WIN_S_Avg_2mi"));
				Double CLO_Cov = Double.parseDouble((String)item.get("CLO_Cov"));
				Double CLO_Cov_Low = Double.parseDouble((String)item.get("CLO_Cov_Low"));
				Map map = new HashMap();
				map.put("Station_Id_C", Station_Id_C);
				map.put("Lat", Lat);
				map.put("Lon", Lon);
				map.put("Year", Year);
				map.put("Mon", Mon);
				map.put("Day", Day);
				map.put("Hour", Hour);
				map.put("WIN_D_Avg_2mi", WIN_D_Avg_2mi);
				map.put("WIN_S_Avg_2mi", WIN_S_Avg_2mi);
				map.put("CLO_Cov", CLO_Cov);
				map.put("CLO_Cov_Low", CLO_Cov_Low);
				resultList.add(map);
			}
		}
		// 日值数据处理
		Gson gsonDay = new Gson();
		Object oDay = gsonDay.fromJson(dateStr, Object.class);
		com.google.gson.internal.LinkedTreeMap linkedDayTreeMap = (com.google.gson.internal.LinkedTreeMap) oDay;
		ArrayList dsDay = (ArrayList) linkedDayTreeMap.get("DS");
		if(dsDay == null) return;
		for(int i=0; i<dsDay.size(); i++) {
			com.google.gson.internal.LinkedTreeMap item = (com.google.gson.internal.LinkedTreeMap)dsDay.get(i);
			String Station_Id_C = (String) item.get("Station_Id_C");
			if(Station_Id_C.startsWith("A")) continue;
			String str = (String)item.get("PRE_Time_2020");
			String Hour = (String)item.get("Hour");
			
			Double Lat = Double.parseDouble((String)item.get("Lat"));
			Double Lon = Double.parseDouble((String)item.get("Lon"));
			Integer Year = Integer.parseInt((String)item.get("Year"));
			Integer Mon = Integer.parseInt((String)item.get("Mon"));
			Integer Day = Integer.parseInt((String)item.get("Day"));
			
			if(str == null || "".equals(str)) {
				continue;
			}
			Double PRE_Time_2020 = Double.parseDouble(str);
			boolean flag = false;
			for(int j=0; j<resultList.size(); j++) {
				Map map = resultList.get(j);
				String tempStation = (String) map.get("Station_Id_C");
				if(Station_Id_C.equals(tempStation)) {
					map.put("PRE_Time_2020", PRE_Time_2020);
					flag = true;
					break;
				}
			}
			if(!flag) {
				Map map = new HashMap();
				map.put("PRE_Time_2020", PRE_Time_2020);
				map.put("Station_Id_C", Station_Id_C);
				map.put("Year", Year);
				map.put("Mon", Mon);
				map.put("Day", Day);
				map.put("Mon", Mon);
				map.put("Hour", Hour);
				map.put("Lon", Lon);
				map.put("Lat", Lat);
				resultList.add(map);
			}
		}
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath, true), "GBK")));
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		} 
		File file = new File(resultPath);
		if(file.length() == 0) {
			String title = "站号,纬度,经度,年,月,日,小时,2分钟平均风向,平均2分钟风速,总云量,低云量,20-20时降水量";
			try {
				writer.write(title);
				writer.write("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// 结果写入文本
		for(int i=0; i<resultList.size(); i++) {
			Map map = resultList.get(i);
			String Station_Id_C = (String) map.get("Station_Id_C");
			Double Lat = (Double) map.get("Lat");
			Double Lon = (Double) map.get("Lon");
			Integer Year = (Integer) map.get("Year");
			Integer Mon = (Integer) map.get("Mon");
			Integer Day = (Integer) map.get("Day");
			Integer Hour = (Integer) map.get("Hour");
			Integer WIN_D_Avg_2mi = (Integer) map.get("WIN_D_Avg_2mi");
			Double WIN_S_Avg_2mi = (Double) map.get("WIN_S_Avg_2mi");
			Double CLO_Cov = (Double) map.get("CLO_Cov");
			Double CLO_Cov_Low = (Double) map.get("CLO_Cov_Low");
			Double PRE_Time_2020 = (Double) map.get("PRE_Time_2020");
			String result = Station_Id_C + "," + Lat + "," + Lon + "," + Year + "," + Mon + 
					"," + Day + "," + Hour + "," + WIN_D_Avg_2mi + "," + WIN_S_Avg_2mi + "," + CLO_Cov + "," + CLO_Cov_Low + "," + PRE_Time_2020;
			try {
				writer.write(result);
				writer.write("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		CIMISSTest cimissTest = new CIMISSTest();
//		String startDateStr = "20150101";
//		String endDateStr = "20160612";
		String startDateStr = args[0];
		String endDateStr = args[1];
		resultPath = args[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date startDate = sdf.parse(startDateStr);
		Date endDate = sdf.parse(endDateStr);
		long start = startDate.getTime();
		long end = endDate.getTime();
		for(long i=start; i<=end; i+=86400000) {
			Date date = new Date(i);
			//日值
			String dateStr = sdf.format(date) + "000000";
			String dayData = cimissTest.getDayRain(dateStr);
			//小时
			String hourStr = sdf.format(date) + "060000";
			String hourData = cimissTest.getHourDate(hourStr);
			cimissTest.dispose(hourData, dayData);
			System.out.println(dateStr);
		}
	}

}
