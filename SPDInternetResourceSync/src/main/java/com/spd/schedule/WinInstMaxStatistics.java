package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.cq.impl.WinInstMaxDaoImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 大风统计
 * @author Administrator
 *
 */
public class WinInstMaxStatistics {

	/**
	 * 补录
	 */
	public void record(int startYear, int endYear) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List dataList = new ArrayList();
		WinInstMaxDaoImpl winInstMaxDao = new WinInstMaxDaoImpl();
		List resultList = winInstMaxDao.getWinAvgHou(startYear, endYear);
		HashMap<String, Object> existData = winInstMaxDao.getExistWinAvg(startYear, endYear);
		for(int i=0; i<resultList.size(); i++) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			mapData.put("Station_Name", (String) tempMap.get("Station_Name"));
			mapData.put("Province", (String) tempMap.get("Province"));
			mapData.put("City", (String) tempMap.get("City"));
			mapData.put("Cnty", (String) tempMap.get("Cnty"));
			mapData.put("Town", (String) tempMap.get("Town"));
			mapData.put("Station_Id_C", (String) tempMap.get("Station_Id_C"));
			mapData.put("Station_Id_d", (String) tempMap.get("Station_Id_d"));
			mapData.put("Station_levl", (String) tempMap.get("Station_levl"));
			mapData.put("Admin_Code_CHN", (String) tempMap.get("Admin_Code_CHN"));
			java.sql.Timestamp timeStamp = (java.sql.Timestamp) tempMap.get("Datetime");
			Date date = new Date(timeStamp.getTime());
			String dateStr = sdf.format(date);
			mapData.put("Datetime", dateStr);
			String key = station_Id_C + "_" + dateStr;
			mapData.put("Lat", (Double) tempMap.get("Lat"));
			mapData.put("Lon", (Double) tempMap.get("Lon"));
			mapData.put("Alti", (Double) tempMap.get("Alti"));
			mapData.put("year", (Integer) tempMap.get("year"));
			double win_S_Inst_Max = (Double) tempMap.get("WIN_S_Inst_Max");
			//风向需要处理
			Double win_D_INST_Max = (Double) tempMap.get("WIN_D_INST_Max");
			if(win_D_INST_Max != 999999 && win_D_INST_Max > 999000) {
				win_D_INST_Max = win_D_INST_Max - 999000;
			}
			mapData.put("WIN_D_INST_Max", win_D_INST_Max);
			//级别需要处理
			if(win_S_Inst_Max >= 13.9 && win_S_Inst_Max < 20.8) {
				mapData.put("Level", 1);
			} else if(win_S_Inst_Max >= 20.8 && win_S_Inst_Max < 28.5) {
				mapData.put("Level", 2);
			} else if(win_S_Inst_Max >= 28.5) {
				mapData.put("Level", 3);
			}
			mapData.put("WIN_S_Inst_Max", (Double) tempMap.get("WIN_S_Inst_Max"));
			if(!existData.containsKey(key)) {
				dataList.add(mapData);
			}
		}
		winInstMaxDao.insertWinValue(dataList);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		WinInstMaxStatistics winInstMaxStatistics = new WinInstMaxStatistics();
		//补录
		winInstMaxStatistics.record(1951, 2016);
	}

}
