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
 * 区域连阴雨的新的同步方式
 * @author Administrator
 *
 */
public class ContinueAreaRainNewSync {

	private ContinuousRainsStationDaoImpl continuousRainsStationDaoImpl = new ContinuousRainsStationDaoImpl();

	private ContinuerainAreaDaoImpl continuerainAreaDaoImpl = new ContinuerainAreaDaoImpl();
	
	private T_pre_time_0820DaoImpl preTime0820DaoImpl = new T_pre_time_0820DaoImpl();
	
	private static int STATIONCNT = 7; //满足区域连阴雨的站数

	private static int DAYSCNT = 6; //满足区域连阴雨的连续天数
	
	/**
	 * 同步，按找datetime查询，满足该天有7个站满足，然后一直往前找，直到找到不符合条件的为止
	 * 查找到同步后的序列，然后去结果中查找，如果有重复的，则更新数据，否则插入数据
	 * @param datetime
	 */
	public void sync(String datetime) {
		//1. 查找datetime的日期，满足条件的天数
		String startTime = getStartTime(datetime);
		if(startTime == null) return;
		startTime = CommonTool.addDays(startTime, 1);
		int days = CommonTool.caleDays(startTime, datetime);
		if(days >= DAYSCNT) {
			//插入数据，需要判断是否需要update
			int id = continuerainAreaDaoImpl.getDataByStartTime(startTime);
			//计算结果入库
			List list = getListByTimes(startTime, datetime);
			if(list == null || list.size() == 0) return;
			HashMap resultMap = cale(list, startTime, datetime);
			if(resultMap == null) return;
			List dataList = new ArrayList();
			dataList.add(resultMap);
			if(-1 == id) {
				// insert 
				continuerainAreaDaoImpl.insert(dataList);
			} else {
				// update
				resultMap.put("id", id);
				continuerainAreaDaoImpl.update(dataList, id);
			}
		}
	}
	
	/**
	 * 根据过程，查询对应的单站序列
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private List getListByTimes(String startTime, String endTime) {
		List dataList = continuousRainsStationDaoImpl.getListByTimes(startTime, endTime);
		return dataList;
	}
	
	private HashMap cale(List list, String startTime, String endTime) {
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
//			String startTime = (String) dataMap.get("StartTime");
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			station_Id_Cs += "'" + station_Id_C + "',"; 
//			String endTime = (String) dataMap.get("EndTime");
//			Double pre = (Double) dataMap.get("Pre");
//			sumPre += pre;
//			preDays += (Integer)dataMap.get("RainDays");
//			startTimeList.add(startTime);
//			endTimeList.add(endTime);
		}
//		if(startTimeList.size() < STATIONCNT || endTimeList.size() < STATIONCNT) return null;
//		Collections.sort(startTimeList);
//		Collections.sort(endTimeList);
		HashMap resultMap = new HashMap();
//		String startTime = (String) startTimeList.get(STATIONCNT - 1);
		resultMap.put("StartTime", startTime + " 00:00:00");
//		String endTime = (String) endTimeList.get(endTimeList.size() - STATIONCNT);
		resultMap.put("EndTime", endTime + " 00:00:00");
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
	/**
	 * 递归查找
	 * @param datetime
	 * @return
	 */
	private String getStartTime(String datetime) {
		int days = continuousRainsStationDaoImpl.getStationCntsByDatetime(datetime);
		if(days >= STATIONCNT) {
			return getStartTime(CommonTool.addDays(datetime, -1));
		} else {
			return datetime;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		String firstr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ContinueAreaRainNewSync continueAreaRainNewSync = new ContinueAreaRainNewSync();
		String startStr = "1990-09-16";
		String endStr = "2000-01-06";
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
			continueAreaRainNewSync.sync(dateStr);
		}
	}

}
