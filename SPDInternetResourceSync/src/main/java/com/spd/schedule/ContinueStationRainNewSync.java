package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.cq.impl.ContinuousRainsStationDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_0820DaoImpl;
import com.spd.dao.cq.impl.T_sshDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 连阴雨单站统计
 * @author Administrator
 *
 */
public class ContinueStationRainNewSync {

	public static int NOSUNDAYS = 6; //无日照时数

	private static int RAINDAYS = 4; //降水日数
	
	private T_sshDaoImpl sshDaoImpl = new T_sshDaoImpl();

	private T_pre_time_0820DaoImpl preTime0820DaoImpl = new T_pre_time_0820DaoImpl();

	private ContinuousRainsStationDaoImpl continuousRainsStationDaoImpl = new ContinuousRainsStationDaoImpl();
	
	/**
	 * 同步
	 * @param datetime
	 */
	public void sync(String datetime) {
		//1. 查询已经存在的最新结果连阴雨
		HashMap<String, Date> lastedResultMap = getLastedContinueRain(datetime);
		//2. 找到所有符合条件的ssh数据
		List<HashMap<String, String>> sshList = querySSHList(datetime, lastedResultMap);
		for(int i = 0; i < sshList.size(); i++) {
			HashMap<String, String> sshMap = sshList.get(i);
			getAllNoSunDays(sshMap, lastedResultMap);
		}
		//3.在此基础上查找降水资料
		List<LinkedHashMap> preList = new ArrayList<LinkedHashMap>();
		for(int i = 0; i < sshList.size(); i++) {
			HashMap<String, String> itemMap = sshList.get(i);
			String station_Id_C = itemMap.get("Station_Id_C");
			String startTime = itemMap.get("StartTime");
			String endTime = itemMap.get("EndTime");
			LinkedHashMap preMap = preMap(startTime, endTime, station_Id_C);
			preList.add(preMap);
		}
		//4. 根据sshList， preList计算连阴雨
		List dataList = new ArrayList();
		for(int i = 0; i < preList.size(); i++) {
			LinkedHashMap preMap = preList.get(i);
			HashMap<String, String> sshMap = sshList.get(i);
			HashMap dataMap = analystContinueRains(preMap);
			if(dataMap != null) {
				dataList.add(dataMap);
			}
		}
		//5. 结果入库
		continuousRainsStationDaoImpl.insertOrUpdate(dataList);
	}

	public HashMap<String, Date> getLastedContinueRain(String datetime) {
		List resultList = continuousRainsStationDaoImpl.getLastedData(datetime);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap<String, Date> dateMap = new HashMap<String, Date>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < resultList.size(); i++) {
			HashMap<String, String> dataMap = (HashMap<String, String>) resultList.get(i);
			String station_Id_C = (String)(dataMap.get("Station_id_C"));
			String value = dataMap.get("EndTime");
			try {
				dateMap.put(station_Id_C, sdf.parse(value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateMap;
	}
	/**
	 * 分析连阴雨的结束
	 * @param preMap
	 */
	private HashMap analystContinueRains(LinkedHashMap preMap) {
		HashMap dataMap = new HashMap();
		String station_Id_C = (String) preMap.get("Station_Id_C");
		Double[] preValues = new Double[preMap.size() - 1];
		String[] dateValues = new String[preMap.size() - 1];
		//1. 判断是否有4个站大于0.1，有的话，就满足了连阴雨开始。
		Iterator it = preMap.keySet().iterator();
		int preCnt = 0;
		int index = 0;
		String endDateTime = "";
		Double sumPre = 0.0;
		while(it.hasNext()) {
			String key = (String) it.next();
			if(!"Station_Id_C".equals(key)) {
				Double pre = (Double) preMap.get(key);
				if(pre != null && pre >= 0.1 && pre < 999) {
					preCnt++;
					sumPre += pre;
				}
				preValues[index] = pre;
				dateValues[index++] = key;
			}
		}
		if(preCnt >= RAINDAYS) {
			//满足连阴雨。则判断结束日期
			//2. 如果最后3天都是0，则结束。否则，满足条件的最后一天为结束
			int noRainStartIndex = 0;
			if(preValues != null) {
				int cnt = 0;
				for(int i = 0; i < preValues.length; i++) {
					if(preValues[i] != null && preValues[i] >= 0.1 && preValues[i] < 999) {
						cnt++;
					}
					if(cnt == RAINDAYS) {
						noRainStartIndex = i;
						break;
					}
				}
				boolean flag = false;
				if(noRainStartIndex <= preValues.length - 4) { // 3天无降水 + 1
					for(int i = noRainStartIndex + 1; i <= preValues.length - 3; i++) {
						Double i1 = preValues[i];
						Double i2 = preValues[i + 1];
						Double i3 = preValues[i + 2];
						if(i1 == 0 && i2 == 0 && i3 == 0) {
							endDateTime = dateValues[i + 2];
							flag = true;
							break;
						}
					}
				} 
				// 如果不满足连续3天无降水的话，就去判断，比最后一天再晚一天的时间，是否已经有日照，如果有的话，就结束
				if(flag) {
					endDateTime = dateValues[dateValues.length - 1];
				} else  {
					String tempDatetime = dateValues[dateValues.length - 1];
					Double sshValue = sshDaoImpl.querySSHByTimeAndStation(CommonTool.addDays(tempDatetime, 1), station_Id_C);
					if((sshValue == null) || (sshValue != null && sshValue != 0)) {
						endDateTime = tempDatetime;
					} else {
						return null;
					}
				}
				
			} else {
				return null;
			}
//			if(preValues != null && preValues.length > 3 && preValues[preValues.length - 1] == 0.0 &&
//					preValues[preValues.length - 2] == 0.0 &&
//					preValues[preValues.length - 3] == 0.0) {
//				endDateTime = dateValues[dateValues.length - 1];
//				for(int i = preValues.length - 4; i >= 0; i--) {
//					Double pre = preValues[i];
//					if(pre == 0.0) {
//						endDateTime = dateValues[i + 2];
//						break;
//					} else if(pre != 0.0) {
//						break;
//					}
//				}
//			} else {
//				endDateTime = dateValues[dateValues.length - 1];
//			}
			//找到结果
			int persistDays = CommonTool.caleDays(dateValues[0], endDateTime);
			//总体过程不满足6天的，也要过滤掉。
			if(persistDays < NOSUNDAYS) return null;
			dataMap.put("Station_Id_C", station_Id_C);
			dataMap.put("StartTime", dateValues[0] + " 00:00:00");
			dataMap.put("EndTime", endDateTime + " 00:00:00");
			dataMap.put("NoSunDays", CommonTool.caleDays(dateValues[0], dateValues[dateValues.length - 1]));
			dataMap.put("RainDays", preCnt);
			dataMap.put("Pre", CommonTool.roundDouble(sumPre));
			return dataMap;
		}
		return null;
	}
	
	/**
	 * 查找满足条件的日照数据，从datetime往前找NOSUNDAYS，如果满足条件，则循环往前找，直到找到全部的满足条件
	 * @param datetime
	 * @return HashMap包含Station_Id_C startTime, endTime
	 */
	private List<HashMap<String, String>> querySSHList(String datetime, HashMap<String, Date> lastedResultMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = CommonTool.addDays(datetime, 1 - NOSUNDAYS);
		String items = CommonTool.createItemStrByRangeDate(startTime, datetime);
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List sshDataList = sshDaoImpl.queryData(startTime, datetime, items);
		HashMap<String, Integer> sshMap = new HashMap<String, Integer>();
		for(int i = 0; i < sshDataList.size(); i++) {
			HashMap dataMap = (HashMap) sshDataList.get(i);
			String station_id_C = (String) dataMap.get("Station_Id_C"); 
			int year = (Integer) dataMap.get("year");
			Iterator it = dataMap.keySet().iterator();
			int noSSHCnt = 0, preDaysCnt = 0;
			Double preSum = 0.0;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double ssh = (Double) dataMap.get(key);
						if(ssh != null && ssh == 0) {
							noSSHCnt ++;
						}
					}
				}
			}
			Integer preNoSSHCnt = sshMap.get(station_id_C);
			if(preNoSSHCnt != null) {
				sshMap.put(station_id_C, preNoSSHCnt + noSSHCnt);
			} else {
				sshMap.put(station_id_C, noSSHCnt);
			}
		}
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		Iterator it = sshMap.keySet().iterator();
		while(it.hasNext()) {
			String station_Id_C = (String) it.next();
			Integer cnt = sshMap.get(station_Id_C);
			if(cnt == NOSUNDAYS) {
				HashMap<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("Station_Id_C", station_Id_C);
				resultMap.put("StartTime", startTime);
				try {
					Date itemStartDate = sdf.parse(startTime);
					//如果开始时间在上次的结束时间之前，那么就把时间往后后推一天
					if(lastedResultMap != null) {
						Date lastedDate = lastedResultMap.get(station_Id_C);
						if(lastedDate != null && itemStartDate.getTime() <= lastedDate.getTime()) {
							resultMap.put("StartTime", sdf.format(new Date(lastedDate.getTime() + CommonConstant.DAYTIMES)));
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				resultMap.put("EndTime", datetime);
				resultList.add(resultMap);
			}
		}
		return resultList;
	}
	
	/**
	 * 
	 * @param station_Id_C
	 * @param datetime
	 */
	private void getAllNoSunDays(HashMap<String, String> dataMap, HashMap<String, Date> lastedResultMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String station_Id_C = dataMap.get("Station_Id_C");
		String startTime = dataMap.get("StartTime");
		String preTime = CommonTool.addDays(startTime, -1);
		try {
			Date preDate = sdf.parse(preTime);
			if(lastedResultMap != null) {
				Date date = lastedResultMap.get(station_Id_C);
				if(date != null && preDate.getTime() <= date.getTime()) return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String item = CommonTool.createItemStrByRangeDate(preTime, preTime);
		Double result = sshDaoImpl.queryDataByItem(item, preTime, station_Id_C);
		if(result != null && result == 0.0) {
			dataMap.put("StartTime", preTime);
			getAllNoSunDays(dataMap, lastedResultMap);
		}
	}
	
	/**
	 * 根据开始，结束时间，查找对应的降水资料
	 * @param startTime
	 * @param endTime
	 * @return 时间和降水序列，按时间从早到晚排列
	 */
	public LinkedHashMap preMap(String startTime, String endTime, String station_Id_C) {
		String items = CommonTool.createItemStrByRangeDate(startTime, endTime);
		List preTime0820DataList = preTime0820DaoImpl.queryDataByTimeAndStation(startTime, endTime, items, station_Id_C);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		LinkedHashMap resultMap = new LinkedHashMap();
		resultMap.put("Station_Id_C", station_Id_C);
		
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		
		while(!startCalendar.after(endCalendar)) {
			Date itemDate = startCalendar.getTime();
			String itemDateStr = sdf.format(itemDate);
			int iYear = Integer.parseInt(itemDateStr.substring(0, 4));
			Double value = 0.0;
			String itemStr = CommonTool.createItemStrByRangeDate(itemDateStr, itemDateStr);
			for(int j = 0; j < preTime0820DataList.size(); j++) {
				HashMap preMap = (HashMap) preTime0820DataList.get(j);
				String itemStation_Id_C = (String) preMap.get("Station_Id_C");
				int year = (Integer) preMap.get("year");
				if(iYear == year && station_Id_C.equals(itemStation_Id_C)) {
					Double pre = (Double) preMap.get(itemStr);
					resultMap.put(itemDateStr, pre);
				}
			}
			startCalendar.add(Calendar.DATE, 1);
		}
		return resultMap;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		ContinueStationRainNewSync continueStationRainNewSync = new ContinueStationRainNewSync();
		//测试开始
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "1987-04-14";
//		String startTime = "2011-01-20";
		String endTime = "2017-03-19";
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
				String time = sdf.format(new Date(i));
				System.out.println(time);
//				strongCoolingSync.sync(time);
				continueStationRainNewSync.sync(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

}
