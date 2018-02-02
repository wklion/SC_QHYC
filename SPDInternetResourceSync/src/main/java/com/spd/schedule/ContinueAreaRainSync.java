package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.cq.impl.ContinuerainAreaDaoImpl;
import com.spd.dao.cq.impl.ContinuousRainsStationDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_0820DaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域连阴雨
 * @author Administrator
 *
 */
@Deprecated
public class ContinueAreaRainSync {

	private ContinuousRainsStationDaoImpl continuousRainsStationDaoImpl = new ContinuousRainsStationDaoImpl();

	private ContinuerainAreaDaoImpl continuerainAreaDaoImpl = new ContinuerainAreaDaoImpl();
	
	private T_pre_time_0820DaoImpl preTime0820DaoImpl = new T_pre_time_0820DaoImpl();
	
	private static int STATIONCNT = 7; //满足区域连阴雨的站数
	/**
	 * 根据datetime作为开始时间，计算对应的结束时间，由此获得一个区间
	 * @param datetime
	 */
	public String getEndTimeByStartTime(String datetime) {
		return continuousRainsStationDaoImpl.getLastedDateByStartTime(datetime);
	}
	
	/**
	 * 根据时间段范围，满足开始时间介于它之间，取到所有的满足条件的最大的结束时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String getLastEndTimeByTimes(String startTime, String endTime) {
		return "";
	}
	
	/**
	 * 开始时间，介于两者之间的，都取出来，然后从中找到最大的结束时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String getListEndTimeByStartTime(String startTime, String endTime) {
		return continuousRainsStationDaoImpl.getDataByTimesRange(startTime, endTime);
	}
	
	/**
	 * 根据开始时间，结束时间，获取在该区间范围内满足条件的过程
	 * @param startTime
	 */
	public List getListByStartTime(String startTime, String endTime) {
		return continuousRainsStationDaoImpl.getDataListByTimes(startTime, endTime);
	}
	
	public HashMap cale(List list) {
		if(list.size() <= STATIONCNT) return null;
		//计算开始时间，从最小的开始，排位在STATIONCNT的即为开始时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List startTimeList = new ArrayList();
		List endTimeList = new ArrayList();
		int stationMaxDays = 0;// 单站最长持续时间
		int sumStations = list.size();//累积站点数
		double sumPre = 0;//累积雨量
		int preDays = 0; // 累积有雨日数
		String station_Id_Cs = "";
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String startTime = (String) dataMap.get("StartTime");
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			station_Id_Cs += "'" + station_Id_C + "',"; 
			String endTime = (String) dataMap.get("EndTime");
			Double pre = (Double) dataMap.get("Pre");
			sumPre += pre;
			preDays += (Integer)dataMap.get("RainDays");
//			try {
//				Date startDate = sdf.parse(startTime);
//				Date endDate = sdf.parse(endTime);
//				int persistDay = (int) ((endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES) + 1;
//				if(persistDay > stationMaxDays) {
//					stationMaxDays = persistDay;
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			startTimeList.add(startTime);
			endTimeList.add(endTime);
		}
		if(startTimeList.size() < STATIONCNT || endTimeList.size() < STATIONCNT) return null;
		Collections.sort(startTimeList);
		Collections.sort(endTimeList);
		HashMap resultMap = new HashMap();
		String startTime = (String) startTimeList.get(STATIONCNT - 1);
		resultMap.put("StartTime", startTime);
		String endTime = (String) endTimeList.get(endTimeList.size() - STATIONCNT);
		resultMap.put("EndTime", endTime);
		resultMap.put("SumPre", sumPre);
		resultMap.put("PreDays", preDays);
//		resultMap.put("StationMaxDays", stationMaxDays);
		resultMap.put("SumStations", sumStations);
		//计算过程持续时间
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			int processDays = (int) ((endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES) + 1;
			resultMap.put("ProcessDays", processDays);
		} catch(Exception e) {
			e.printStackTrace();
		}
		station_Id_Cs = station_Id_Cs.substring(0, station_Id_Cs.length() - 1);
		caleDays(station_Id_Cs, (String) resultMap.get("StartTime"), (String) resultMap.get("EndTime"), resultMap);
		return resultMap;
	}
	
	/**
	 * 计算降雨日数，降水总和
	 */
	private void caleDays(String station_Id_Cs, String startTime, String endTime, HashMap resultMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		double sumPre = 0.0;
		int PreDays = 0;
		String items = CommonTool.createItemStrByRangeDate(startTime, endTime);
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List preTime2020DataList = preTime0820DaoImpl.queryDataByStations(startTime, endTime, items, station_Id_Cs);
		if(preTime2020DataList == null || preTime2020DataList.size() == 0) return;
		for(int i = 0; i < preTime2020DataList.size(); i++) {
			HashMap dataMap = (HashMap) preTime2020DataList.get(i);
			String station_id_C = (String) dataMap.get("Station_Id_C"); 
			int year = (Integer) dataMap.get("year");
			Iterator it = dataMap.keySet().iterator();
			int preCnt = 0, preDaysCnt = 0;
			Double preSum = 0.0;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double pre = (Double) dataMap.get(key);
						if(pre != null && pre >= 0.1 && pre < 999) {
							PreDays++;
							sumPre += pre;
						}
					}
				}
			}
		}
		resultMap.put("SumPre", CommonTool.roundDouble(sumPre));
		resultMap.put("PreDays", PreDays);
	}
	
	public void sync(String startTime) {
		//1. 根据开始时间，取到对应的结束时间，如果有相同的开始时间，取最晚结束的
		String endTime = getEndTimeByStartTime(startTime);
		if(endTime == null) return;
		//2. 开始时间，介于两者之间的，都取出来，然后从中找到最大的结束时间
		String lastedEndTime = getListEndTimeByStartTime(startTime, endTime); 
		if(lastedEndTime == null) return;
		List list = getListByStartTime(startTime, lastedEndTime); 
		if(list == null) return;
		//计算结果入库
		HashMap resultMap = cale(list);
		if(resultMap == null) return;
		List dataList = new ArrayList();
		dataList.add(resultMap);
		String processStartTime = (String) resultMap.get("StartTime");
		continuerainAreaDaoImpl.insert(dataList, processStartTime);
	}
	
	public Date getLastedTime() {
		String lastedTime = continuerainAreaDaoImpl.getLastedTime();
		if(lastedTime == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(lastedTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		String firstr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ContinueAreaRainSync continueAreaRainSync = new ContinueAreaRainSync();
		//TODO 实时计算的时候，日期则取表中最大的日期+1天
//		while(true) {
//			String lastedTime = continueAreaRainSync.getLastedTime();
//			if(lastedTime == null) {
//				continueAreaRainSync.sync("1957-09-05");
//			} else {
//				continueAreaRainSync.sync(lastedTime);
//				if(lastedTime.startsWith("2016"));
//			}
//		}
		String startStr = "2015-09-16";
		String endStr = "2015-10-16";
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startStr);
			endDate = sdf.parse(endStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			Date date = new Date(i);
			String dateStr = sdf.format(date);
			System.out.println(dateStr);
			Date lastedDate = continueAreaRainSync.getLastedTime();
			if(lastedDate == null || date.getTime() > lastedDate.getTime()) {
				continueAreaRainSync.sync(dateStr);
			} 
		}
	}

}
