package com.spd.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.AlertContinuousRainsAreaDaoImpl;
import com.spd.dao.cq.impl.AlertContinuousRainsStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域连阴雨预警
 * @author Administrator
 *
 */
public class ContinueAreaRainAlertSync {
	
	private static int STATIONCNT = 7; //满足区域连阴雨的站数
	
	private AlertContinuousRainsStationDaoImpl alertContinuousRainsStationDaoImpl = new AlertContinuousRainsStationDaoImpl();

	private AlertContinuousRainsAreaDaoImpl alertContinuousRainsAreaDaoImpl = new AlertContinuousRainsAreaDaoImpl();
	
	public void sync(String datetime) {
		//1. 查询在此预报时间的预报结果中，是否满足 >= STATIONCNT
		boolean isAreaContinueRain = isAreaContinueRain(datetime);
		if(!isAreaContinueRain) return;
		//2. 在满足的条件下，查询最早的开始时间，最晚的结束时间
		String[] minMaxTime = getMinMaxTimeByForecast(datetime);
		if(minMaxTime == null) return;
		//3. 循环遍历，找到区域的开始时间，区域的结束时间。
		String areaStartTime = getAreaStartTime(minMaxTime[0], minMaxTime[1]);
		if(areaStartTime == null) return;
		String areaEndTime = getAreaEndTime(minMaxTime[0], minMaxTime[1]);
		if(areaEndTime == null) return;
		//4. 根据区域的开始，结束时间，计算累计站点数
		int[] days = getSumStationRainDaysByTimes(areaStartTime, areaEndTime);
		//5. 整理，入库
		addData(days, areaStartTime, areaEndTime, datetime);
	}
	
	private void addData(int[] days, String areaStartTime, String areaEndTime, String forecastDate) {
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", areaStartTime + " 00:00:00");
		dataMap.put("EndTime", areaEndTime + " 00:00:00");
		dataMap.put("ForecastDate", forecastDate + " 00:00:00");
		dataMap.put("SumStations", days[0]);
		dataMap.put("PreDays", days[1]);
		List dataList = new ArrayList();
		dataList.add(dataMap);
		alertContinuousRainsAreaDaoImpl.insert(dataList, areaStartTime + " 00:00:00");
	}
	private int[] getSumStationRainDaysByTimes(String startTime, String endTime) {
		int[] days = alertContinuousRainsStationDaoImpl.getSumStationSumRainDaysByTimes(startTime, endTime);
		return days;
	}
	
	private boolean isAreaContinueRain(String datetime) {
		int cnt = alertContinuousRainsStationDaoImpl.getCntByForecastTime(datetime);
		if(cnt >= STATIONCNT) return true;
		return false;
	}
	
	private String[] getMinMaxTimeByForecast(String datetime) {
		String[] minMaxTime = alertContinuousRainsStationDaoImpl.getMinMaxTimeByForecast(datetime);
		return minMaxTime;
	}
	
	private String getAreaStartTime(String startTime, String endTime) {
		String areaStartTime = null;
		Date startDate = CommonTool.chgStr2Date(startTime);
		Date endDate = CommonTool.chgStr2Date(endTime);
		if(startDate == null || endDate == null) return null;
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			Date currentDate = new Date(i);
			areaStartTime = alertContinuousRainsStationDaoImpl.getAreaStartTime(CommonTool.chgDate2Str(currentDate), STATIONCNT);
			if(areaStartTime != null) {
				return areaStartTime;
			}
		}
		return null;
	}
	
	private String getAreaEndTime(String startTime, String endTime) {
		String areaEndTime = null;
		Date startDate = CommonTool.chgStr2Date(startTime);
		Date endDate = CommonTool.chgStr2Date(endTime);
		if(startDate == null || endDate == null) return null;
		for(long i = endDate.getTime(); i >= startDate.getTime(); i -= CommonConstant.DAYTIMES) {
			Date currentDate = new Date(i);
			areaEndTime = alertContinuousRainsStationDaoImpl.getAreaEndTime(CommonTool.chgDate2Str(currentDate), STATIONCNT);
			if(areaEndTime != null) {
				return areaEndTime;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		ContinueAreaRainAlertSync continueAreaRainAlertSync = new ContinueAreaRainAlertSync();
		continueAreaRainAlertSync.sync("2016-11-01");
	}
}
