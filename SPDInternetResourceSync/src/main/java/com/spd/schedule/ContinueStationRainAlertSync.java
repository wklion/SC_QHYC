package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.cq.impl.AlertContinuousRainsStationDaoImpl;
import com.spd.dao.cq.impl.ForecastDataDao;
import com.spd.dao.cq.impl.T_pre_time_0820DaoImpl;
import com.spd.dao.cq.impl.T_sshDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 单站连阴雨预警同步
 * @author Administrator
 *
 */
public class ContinueStationRainAlertSync {

	public static int NOSUNDAYS = 6; //无日照时数

	private static int RAINDAYS = 4; //降水日数
	
	private T_sshDaoImpl sshDaoImpl = new T_sshDaoImpl();

	private T_pre_time_0820DaoImpl preTime0820DaoImpl = new T_pre_time_0820DaoImpl();
	
	private ForecastDataDao forecastDataDao = new ForecastDataDao();
	
	private AlertContinuousRainsStationDaoImpl alertContinuousRainsStationDaoImpl = new AlertContinuousRainsStationDaoImpl();
	
	public void sync(String datetime) {
		// 1. 计算预报时间内的SSH满足条件的结果
		List<HashMap<String, String>> forecastSSHList = getForecasteSSHList(datetime);
		// 2. 计算实况中从datetime往前查找，找到全部符合条件的数据。
		List<HashMap<String, String>> realTimeSSHList = getRealTimeSSH(datetime);
		// 3. 合并上述两个数据，整理出新的结果序列
		List<HashMap<String, String>> sshList = addRealForecastSSHList(realTimeSSHList, forecastSSHList);
		// 4. 在上述结果序列时间内，查找实况的符合条件的降水序列
		List<LinkedHashMap> realTimePreList = getRealPreList(sshList, datetime);
		// 5. 在上述结果序列时间内，查找预报降水，找到符合条件的降水预报序列
		List<LinkedHashMap> forecastTimePreList = getForecastPreList(sshList, datetime);
		// 6. 把上述的实况、预报降水序列合并成新的降水序列
		List<LinkedHashMap> preList = addRealForecastPreList(realTimePreList, forecastTimePreList);
		// 7. 根据降水、实况序列，计算连阴雨，结果入库
		List<HashMap> dataList = analyst(sshList, preList, datetime);
		//8. 结果入库
		alertContinuousRainsStationDaoImpl.insert(dataList, datetime);
	}
	
	private List<HashMap> analyst(List<HashMap<String, String>> sshList, List<LinkedHashMap> preList, String datetime) {
		List<HashMap> resultList = new ArrayList<HashMap>();
		if(sshList != null && sshList.size() > 0) {
			for(int i = 0; i < sshList.size(); i++) {
				HashMap<String, String> sshMap = sshList.get(i);
				String startTime = sshMap.get("StartTime");
				String endTime = sshMap.get("EndTime");
				String station_Id_C = sshMap.get("Station_Id_C");
				int days = CommonTool.caleDays(startTime, endTime);
				if(days < NOSUNDAYS) continue; // 不满足日照条件
				for(int j = 0; j < preList.size(); j++) {
					LinkedHashMap preMap = preList.get(j);
					String itemStation_Id_C = (String) preMap.get("Station_Id_C");
					if(station_Id_C.equals(itemStation_Id_C)) {
						HashMap resultMap = analystContinueRains(preMap, datetime);
						resultList.add(resultMap);
						break;
					}
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 分析连阴雨的结束
	 * @param preMap
	 */
	private HashMap analystContinueRains(LinkedHashMap preMap, String datetime) {
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
				if(noRainStartIndex <= preValues.length - 4) { // 3天无降水 + 1
					boolean flag = false;
					for(int i = noRainStartIndex + 1; i < preValues.length - 3; i++) {
						Double i1 = preValues[i];
						Double i2 = preValues[i + 1];
						Double i3 = preValues[i + 2];
						if(i1 == 0 && i2 == 0 && i3 == 0) {
							endDateTime = dateValues[i + 2];
							flag = true;
							break;
						}
					}
					if(!flag) {
						endDateTime = dateValues[dateValues.length - 1];
					}
				} else {
					endDateTime = dateValues[dateValues.length - 1];
				}
				
			} else {
				return null;
			}
			//找到结果
			dataMap.put("ForecastDate", datetime + " 00:00:00");
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
	 * 根据预报的SSH序列，查找对应的预报降水序列
	 * @param forecastSSHList
	 * @param datetime
	 * @return
	 */
	private List<LinkedHashMap> getForecastPreList(List<HashMap<String, String>> forecastSSHList, String datetime) {
		List<LinkedHashMap> preList = new ArrayList<LinkedHashMap>();
//		String startTime = CommonTool.addDays(datetime, 1);
		if(forecastSSHList != null && forecastSSHList.size() > 0) {
			for(int i = 0; i < forecastSSHList.size(); i++) {
				HashMap<String, String> itemMap = forecastSSHList.get(i);
				String station_Id_C = itemMap.get("Station_Id_C");
				String endTime = itemMap.get("EndTime");
				LinkedHashMap preMap = forecastPreMap(datetime, endTime, station_Id_C);
				preList.add(preMap);
			}
		}
		return preList;	
	}
	
	private LinkedHashMap forecastPreMap(String datetime, String futureDate, String station_Id_C) {
		LinkedHashMap resultMap = new LinkedHashMap();
		List list = forecastDataDao.getForecastDataByForecastTimeAndStation(datetime, futureDate, station_Id_C);
		if(list == null || list.size() == 0) return null;
		resultMap.put("Station_Id_C", station_Id_C);
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String datetimeStr = (String) dataMap.get("FutureDate");
			Integer weatherState = (Integer) dataMap.get("WeatherState");
			Double preValue = chgWeatherState2Pre(weatherState);
			resultMap.put(datetimeStr, preValue);
		}
		return resultMap;
	}
	
	/**
	 * 根据ssh序列对应查找降水序列
	 * @param realSSHList
	 * @return
	 */
	private List<LinkedHashMap> getRealPreList(List<HashMap<String, String>> realSSHList, String datetime) {
		List<LinkedHashMap> preList = new ArrayList<LinkedHashMap>();
		if(realSSHList != null && realSSHList.size() > 0) {
			for(int i = 0; i < realSSHList.size(); i++) {
				HashMap<String, String> itemMap = realSSHList.get(i);
				String station_Id_C = itemMap.get("Station_Id_C");
				String startTime = itemMap.get("StartTime");
				LinkedHashMap preMap = preMap(startTime, datetime, station_Id_C);
				preList.add(preMap);
			}
		}
		return preList;
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
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			Date itemDate = new Date(i);
			String itemDateStr = sdf.format(itemDate);
			int iYear = Integer.parseInt(itemDateStr.substring(0, 4));
			Double value = 0.0;
			String itemStr = CommonTool.createItemStrByRangeDate(itemDateStr, itemDateStr);
			for(int j = 0; j < preTime0820DataList.size(); j++) {
				HashMap preMap = (HashMap) preTime0820DataList.get(j);
				String itemStation_Id_C = (String) preMap.get("Station_Id_C");
				int year = (Integer) preMap.get("year");
				if(iYear == year) {
					Double pre = (Double) preMap.get(itemStr);
					resultMap.put(itemDateStr, pre);
				}
			}
		}
		return resultMap;
	}
	
	private List<LinkedHashMap> addRealForecastPreList(List<LinkedHashMap> realTimePreList, List<LinkedHashMap> forecastTimePreList) {
		List<LinkedHashMap> resultList = new ArrayList<LinkedHashMap>();
		if(realTimePreList == null || realTimePreList.size() == 0) return null;
		if(forecastTimePreList == null || forecastTimePreList.size() == 0) return null;
		for(int i = 0; i < realTimePreList.size(); i++) {
			LinkedHashMap realTimePreMap = realTimePreList.get(i);
			String station_Id_C = (String) realTimePreMap.get("Station_Id_C");
			for(int j = 0; j < forecastTimePreList.size(); j++) {
				LinkedHashMap forecastPreMap = forecastTimePreList.get(j);
				String forecastStation_Id_C = (String) forecastPreMap.get("Station_Id_C");
				if(station_Id_C.equals(forecastStation_Id_C)) {
					LinkedHashMap resultMap = new LinkedHashMap();
					resultMap.put("Station_Id_C", station_Id_C);
					Iterator realIt = realTimePreMap.keySet().iterator();
					while(realIt.hasNext()) {
						String key = (String) realIt.next();
						resultMap.put(key, realTimePreMap.get(key));
					}
					Iterator forecastIt = forecastPreMap.keySet().iterator();
					while(forecastIt.hasNext()) {
						String key = (String) forecastIt.next();
						resultMap.put(key, forecastPreMap.get(key));
					}
					resultList.add(resultMap);
					break;
				}
			}
		}
		return resultList;
	}
	
	private List<HashMap<String, String>> addRealForecastSSHList(List<HashMap<String, String>> realTimeSSHList, List<HashMap<String, String>> forecastSSHList) {
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		if(realTimeSSHList == null || realTimeSSHList.size() == 0) return null;
		if(forecastSSHList == null || forecastSSHList.size() == 0) return realTimeSSHList;
		for(int i = 0; i < realTimeSSHList.size(); i++) {
			HashMap<String, String> resultMap = new HashMap<String, String>();
			HashMap<String, String> realTimeSSHMap = realTimeSSHList.get(i);
			String station_Id_C = (String) realTimeSSHMap.get("Station_Id_C");
			String startTime = (String) realTimeSSHMap.get("StartTime");
			String endTime = (String) realTimeSSHMap.get("EndTime");
			for(int j = 0; j < forecastSSHList.size(); j++) {
				HashMap<String, String> forecastSSHMap = forecastSSHList.get(j);
				String itemStation_Id_C = (String) forecastSSHMap.get("Station_Id_C");
				String itemStartTime = (String) forecastSSHMap.get("StartTime");
				String itemEndTime = (String) forecastSSHMap.get("EndTime");
				if(station_Id_C.equals(itemStation_Id_C)) {
					resultMap.put("Station_Id_C", station_Id_C);
					resultMap.put("StartTime", startTime);
					resultMap.put("EndTime", itemEndTime);
					Integer persistDays =  CommonTool.caleDays(startTime, itemEndTime);
					if(persistDays < NOSUNDAYS) continue;
					resultMap.put("PersistDays", CommonTool.caleDays(startTime, itemEndTime) + "");
					resultList.add(resultMap);
					break;
				}
			}
		}
		return resultList;
	}
	/**
	 * 查询预报的日照
	 * @param datetime
	 * @return
	 */
	private List<HashMap<String, String>> getForecasteSSHList(String datetime) {
		List list = forecastDataDao.getWeatherStateByDatetime(datetime);
		if(list == null || list.size() == 0) return null;
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < list.size(); i++) {
			HashMap<String, String> resultMap = new HashMap<String, String>();
			HashMap dataMap = (HashMap) list.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			String forscastDate = (String) dataMap.get("ForscastDate");
			Integer hourSpan = (Integer)dataMap.get("HourSpan");
			if(hourSpan == 24) {
				Integer weatherState = (Integer) dataMap.get("WeatherState");
				Integer ssh = chgWeatherState2SSH(weatherState);
				if(ssh == 0) {
					resultMap.put("Station_Id_C", station_Id_C);
					resultMap.put("HourSpan", hourSpan + "");
					resultMap.put("StartTime", CommonTool.addDays(datetime, hourSpan / hourSpan));
					resultMap.put("EndTime", CommonTool.addDays(datetime, hourSpan / hourSpan));
					resultList.add(resultMap);
				}
			}
		}
		analystForecastSSH(resultList, list);
		return resultList;
	}
	
	/**
	 * 递归找到最后一个符合条件的结果
	 * @param resultList
	 * @param list
	 */
	private void analystForecastSSH(List<HashMap<String, String>> resultList, List list) {
		if(resultList ==  null || resultList.size() == 0) return;
		for(int i = 0; i < resultList.size(); i++) {
			HashMap<String, String> resultMap = resultList.get(i);
			String station_Id_C = resultMap.get("Station_Id_C");
			Integer hourSpan = Integer.parseInt(resultMap.get("HourSpan"));
			String endTime = resultMap.get("EndTime");
			for(int j = 0; j < list.size(); j++) {
				HashMap dataMap = (HashMap) list.get(j);
				String itemStation_Id_C = (String) dataMap.get("Station_Id_C");
				Integer itemHourSpan = (Integer)dataMap.get("HourSpan");
				if(itemStation_Id_C.equals(station_Id_C) && (itemHourSpan - hourSpan == 24)) {
					Integer weatherState = (Integer) dataMap.get("WeatherState");
					Integer ssh = chgWeatherState2SSH(weatherState);
					if(ssh == 0) {
						resultMap.put("EndTime", CommonTool.addDays(endTime, 1));
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 转换天气现象到日照，阴天或有雨，则日照为0，否则为1
	 * @param weatherState
	 * @return
	 */
	private int chgWeatherState2SSH(int weatherState) {
		if(weatherState == 0 || weatherState == 1) return 1;
		return 0;
	}
	
	/**
	 * 转换天气现象到降水，阴天或有雨，则日照为0，否则为1
	 * @param weatherState
	 * @return
	 */
	private Double chgWeatherState2Pre(int weatherState) {
		if(weatherState == 0 || weatherState == 1 || weatherState == 2 ||
				weatherState == 20 || weatherState == 29 || weatherState == 30 || weatherState == 31)
			return 0.0;
		return 1.0;
	}
	
	private List<HashMap<String, String>> getRealTimeSSH(String datetime) {
		List<HashMap<String, String>> sshList = querySSHList(datetime);
		for(int i = 0; i < sshList.size(); i++) {
			HashMap<String, String> sshMap = sshList.get(i);
			getAllNoSunDays(sshMap);
		}
		return sshList;
	}
	
	/**
	 * 
	 * @param station_Id_C
	 * @param datetime
	 */
	private void getAllNoSunDays(HashMap<String, String> dataMap) {
		String station_Id_C = dataMap.get("Station_Id_C");
		String startTime = dataMap.get("StartTime");
		String preTime = CommonTool.addDays(startTime, -1);
		String item = CommonTool.createItemStrByRangeDate(preTime, preTime);
		Double result = sshDaoImpl.queryDataByItem(item, preTime, station_Id_C);
		if(result != null && result == 0.0) {
			dataMap.put("StartTime", preTime);
			getAllNoSunDays(dataMap);
		}
	}
	
	private List<HashMap<String, String>> querySSHList(String datetime) {
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		String items = CommonTool.createItemStrByRangeDate(datetime, datetime);
		List sshDataList = sshDaoImpl.queryData(datetime, datetime, items);
		if(sshDataList == null || sshDataList.size() == 0) return null;
		for(int i =0; i < sshDataList.size(); i++) {
			HashMap dataMap = (HashMap) sshDataList.get(i);
			String station_id_C = (String) dataMap.get("Station_Id_C");
			int year = (Integer) dataMap.get("year");
			Double ssh = (Double) dataMap.get(items);
			if(ssh == 0) {
				HashMap<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("Station_Id_C", station_id_C);
				resultMap.put("StartTime", datetime);
				resultMap.put("EndTime", datetime);
				resultList.add(resultMap);
			}
		}
		return resultList;
	}
	
	/**
	 * 查找满足条件的日照数据，从datetime往前找NOSUNDAYS，如果满足条件，则循环往前找，直到找到全部的满足条件
	 * @param datetime
	 * @return HashMap包含Station_Id_C startTime, endTime
	 */
	private List<HashMap<String, String>> querySSHList2(String datetime) {
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
				resultMap.put("EndTime", datetime);
				resultList.add(resultMap);
			}
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		ContinueStationRainAlertSync continueStationRainAlertSync = new ContinueStationRainAlertSync();
		continueStationRainAlertSync.sync("2016-10-31");
	}
}
