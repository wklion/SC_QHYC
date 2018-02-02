package com.spd.schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.cq.impl.SnowDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

public class SnowStatistics {

	private SnowDaoImpl snowDao = new SnowDaoImpl();
	
	/**
	 * 补录
	 */
	public void record(int startYear, int endYear) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, Object> existData = snowDao.getExistSnow(startYear, endYear);
		List resultList = snowDao.getSnow(startYear, endYear);
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String snow = (String) tempMap.get("Snow");
			String snow_OTime = (String) tempMap.get("Snow_OTime");
			String gSS = (String) tempMap.get("GSS");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double snow_Depth = (Double) tempMap.get("Snow_Depth");
			java.sql.Timestamp timestamp =  (Timestamp) tempMap.get("datetime");
			String datetime = sdf.format(new Date(timestamp.getTime()));
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Snow_OTime", snow_OTime);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("Snow_Depth", snow_Depth);
			mapData.put("Snow", Integer.parseInt(snow));
			mapData.put("GSS", Integer.parseInt(gSS));
			mapData.put("datetime", datetime);
			dataList.add(mapData);
		}
		snowDao.insertSnowValue(dataList);
	}
	
	/**
	 * 实况
	 */
	public void sync(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, Object> existData = snowDao.getExistSnow(datetime);
		List resultList = snowDao.getSnow(datetime);
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String snow = (String) tempMap.get("Snow");
			String snow_OTime = (String) tempMap.get("Snow_OTime");
			String gSS = (String) tempMap.get("GSS");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double snow_Depth = (Double) tempMap.get("Snow_Depth");
			java.sql.Timestamp timestamp =  (Timestamp) tempMap.get("datetime");
			String datetimeStr = sdf.format(new Date(timestamp.getTime()));
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetimeStr.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Snow_OTime", snow_OTime);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("Snow_Depth", snow_Depth);
			mapData.put("Snow", Integer.parseInt(snow));
			mapData.put("GSS", Integer.parseInt(gSS));
			mapData.put("datetime", datetimeStr);
			dataList.add(mapData);
		}
		snowDao.insertSnowValue(dataList);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		SnowStatistics snowStatistics = new SnowStatistics();
		//补录
//		snowStatistics.record(1951, 2016);
		//实况
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		date = new Date(date.getTime() - CommonConstant.DAYTIMES);
		String datetime = sdf.format(date);
//		snowStatistics.sync(datetime);
		snowStatistics.sync("2016-03-13");
	}

}
