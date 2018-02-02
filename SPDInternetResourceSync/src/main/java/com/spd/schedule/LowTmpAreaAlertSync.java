package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.LowTmpAreaAlertDaoImpl;
import com.spd.dao.cq.impl.LowTmpAreaDaoImpl;
import com.spd.dao.cq.impl.LowTmpStationAlertDaoImpl;
import com.spd.dao.cq.impl.LowTmpStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域低温预警
 * @author Administrator
 *
 */
public class LowTmpAreaAlertSync {

	private LowTmpStationAlertDaoImpl lowTmpStationDaoImpl = new LowTmpStationAlertDaoImpl();

	private LowTmpAreaAlertDaoImpl lowTmpAreaDaoImpl = new LowTmpAreaAlertDaoImpl();
	
	private static int STATIONCNT = 7;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 根据结束时间，查找开始时间，满足的条件是，该日中总共有超过7个以上的站。
	 * @param endTime
	 * @return
	 */
	private String getStartTimeByEndTime(String datetime, String forecastDatetime) {
		int stationCnt = lowTmpStationDaoImpl.getCntByDateTime(datetime, forecastDatetime);
		if(stationCnt >= STATIONCNT) {
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
			String preDateTime = sdf.format(preDate);
			return getStartTimeByEndTime(preDateTime, forecastDatetime);
		} else {
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date forDate = new Date(date.getTime() + CommonConstant.DAYTIMES);
			return sdf.format(forDate);
		}
	}
	
	private String getEndTimeByForecastTime(String forecastTime) {
		return lowTmpStationDaoImpl.getEndTimeByForecastTime(forecastTime);
	}
	/**
	 * 根据开始时间、结束时间，计算累计站点、累计气温距平
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private Object[] caleDaysAndAnomalys(String startTime, String endTime, String forecastDatetime) {
		Object[] result = lowTmpStationDaoImpl.caleDaysAndAnomalys(startTime, endTime, forecastDatetime);
		return result;
	}
	
	public void sync(String forecastDatetime) {
		String datetime = getEndTimeByForecastTime(forecastDatetime); //需要计算，根据预报的时间，计算对应的
		if(datetime == null) return;
		String startTime = getStartTimeByEndTime(datetime, forecastDatetime);
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(startDate.getTime() >= endDate.getTime()) {
			return;
		}
		Object[] result = caleDaysAndAnomalys(startTime, datetime, forecastDatetime);
		List dataList = new ArrayList();
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", startTime + " 00:00:00");
		dataMap.put("EndTime", datetime + " 00:00:00");
		dataMap.put("SumStations", (Integer) result[0]);
		dataMap.put("SumAnomaly", (Double) result[1]);
		dataMap.put("ForecastDate", forecastDatetime + " 00:00:00");
		dataList.add(dataMap);
		lowTmpAreaDaoImpl.insertLowTmpStationValue(dataList, forecastDatetime);
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		LowTmpAreaAlertSync lowTmpAreaSync = new LowTmpAreaAlertSync();
		lowTmpAreaSync.sync("2016-09-03");
	}
}
