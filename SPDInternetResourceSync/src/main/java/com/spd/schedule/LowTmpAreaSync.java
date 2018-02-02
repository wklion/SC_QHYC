package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.LowTmpAreaDaoImpl;
import com.spd.dao.cq.impl.LowTmpStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域低温统计
 * @author Administrator
 *
 */
public class LowTmpAreaSync {

	private LowTmpStationDaoImpl lowTmpStationDaoImpl = new LowTmpStationDaoImpl();

	private LowTmpAreaDaoImpl lowTmpAreaDaoImpl = new LowTmpAreaDaoImpl();
	
	private static int STATIONCNT = 7;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 根据结束时间，查找开始时间，满足的条件是，该日中总共有超过7个以上的站。
	 * @param endTime
	 * @return
	 */
	private String getStartTimeByEndTime(String datetime) {
		int stationCnt = lowTmpStationDaoImpl.getCntByDateTime(datetime);
		if(stationCnt >= STATIONCNT) {
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
			String preDateTime = sdf.format(preDate);
			return getStartTimeByEndTime(preDateTime);
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
	
	/**
	 * 根据开始时间、结束时间，计算累计站点、累计气温距平
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private Object[] caleDaysAndAnomalys(String startTime, String endTime) {
		Object[] result = lowTmpStationDaoImpl.caleDaysAndAnomalys(startTime, endTime);
		return result;
	}
	
	public void sync(String datetime) {
		String startTime = getStartTimeByEndTime(datetime);
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
		Object[] result = caleDaysAndAnomalys(startTime, datetime);
		List dataList = new ArrayList();
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", startTime);
		dataMap.put("EndTime", datetime);
		dataMap.put("SumStations", (Integer) result[0]);
		dataMap.put("SumAnomaly", (Double) result[1]);
		dataList.add(dataMap);
		lowTmpAreaDaoImpl.insertLowTmpStationValue(dataList);
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		LowTmpAreaSync lowTmpAreaSync = new LowTmpAreaSync();
//		lowTmpAreaSync.sync("1954-12-20");
		//测试开始
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "2012-10-10";
		String endTime = "2016-09-10";
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
				String time = sdf.format(new Date(i));
				System.out.println(time);
				lowTmpAreaSync.sync(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//测试结束
	}
}
