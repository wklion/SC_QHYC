package com.spd.schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.cq.impl.FogDaoImpl;
import com.spd.dao.cq.impl.SnowDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 雾
 * @author Administrator
 *
 */
public class FogStatistics {

	private FogDaoImpl fogDao = new FogDaoImpl();
	
	/**
	 * 补录
	 */
	public void record(int startYear, int endYear) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, Object> existData = fogDao.getExistFog(startYear, endYear);
		List resultList = fogDao.getFog(startYear, endYear);
		List dataList = new ArrayList();
		Map<String, String> sameMap = new HashMap<String, String>(); // 去掉重复
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			Fog, Fog_OTime, VIS_Min, VIS_Min_OTime, RHU_Avg, RHU_Min, RHU_Min_OTIME
			Double fog = Double.parseDouble((String) tempMap.get("Fog"));
			String fog_OTime = (String) tempMap.get("Fog_OTime");
			Double vis_Min = (Double) tempMap.get("VIS_Min");
			String vis_Min_OTime = (String) tempMap.get("VIS_Min_OTime");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double rhu_Avg = (Double) tempMap.get("RHU_Avg");
			Double rhu_Min = (Double) tempMap.get("RHU_Min");
			String rhu_Min_OTIME = (String) tempMap.get("RHU_Min_OTIME");
			java.sql.Timestamp timestamp =  (Timestamp) tempMap.get("datetime");
			String datetime = sdf.format(new Date(timestamp.getTime()));
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			if(sameMap.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Fog", fog);
			mapData.put("Fog_OTime", fog_OTime);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("VIS_Min", vis_Min);
			mapData.put("VIS_Min_OTime", vis_Min_OTime);
			mapData.put("RHU_Avg", rhu_Avg);
			mapData.put("RHU_Min", rhu_Min);
			mapData.put("RHU_Min_OTIME", rhu_Min_OTIME);
			mapData.put("datetime", datetime);
			sameMap.put(key, "");
			dataList.add(mapData);
		}
		fogDao.insertFogValue(dataList);
	}
	
	/**
	 * 实况
	 */
	public void sync(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, Object> existData = fogDao.getExistFog(datetime);
		List resultList = fogDao.getFog(datetime);
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			Fog, Fog_OTime, VIS_Min, VIS_Min_OTime, RHU_Avg, RHU_Min, RHU_Min_OTIME
			Double fog = Double.parseDouble((String) tempMap.get("Fog"));
			String fog_OTime = (String) tempMap.get("Fog_OTime");
			Double vis_Min = (Double) tempMap.get("VIS_Min");
			String vis_Min_OTime = (String) tempMap.get("VIS_Min_OTime");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double rhu_Avg = (Double) tempMap.get("RHU_Avg");
			Double rhu_Min = (Double) tempMap.get("RHU_Min");
			String rhu_Min_OTIME = (String) tempMap.get("RHU_Min_OTIME");
			java.sql.Timestamp timestamp =  (Timestamp) tempMap.get("datetime");
//			String datetime = sdf.format(new Date(timestamp.getTime()));
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Fog", fog);
			mapData.put("Fog_OTime", fog_OTime);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("VIS_Min", vis_Min);
			mapData.put("VIS_Min_OTime", vis_Min_OTime);
			mapData.put("RHU_Avg", rhu_Avg);
			mapData.put("RHU_Min", rhu_Min);
			mapData.put("RHU_Min_OTIME", rhu_Min_OTIME);
			mapData.put("datetime", datetime);
			dataList.add(mapData);
		}
		fogDao.insertFogValue(dataList);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		FogStatistics snowStatistics = new FogStatistics();
		for(int i = 1951; i <= 2016; i+=2) {
			System.out.println(i + "," + (i + 1));
			snowStatistics.record(i, i + 1);
		}
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		date = new Date(date.getTime() - CommonConstant.DAYTIMES);
//		String datetime = sdf.format(date);
//		String datetime = "1951-01-10";
//		snowStatistics.sync(datetime);
	}

}
