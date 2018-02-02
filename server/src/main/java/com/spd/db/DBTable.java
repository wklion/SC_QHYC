package com.spd.db;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.service.ICommon;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;

/**
 * 封装表，模拟真实的表，以及对表中数据的操作。
 * @author Administrator
 * TODO 后续加上更多的默认SQL的操作。
 */
public class DBTable {

	//模拟记录
	private List<DBData> records = new ArrayList<DBData>();
	
	private static ICommon common = (ICommon) ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//是否是历年同期的查询
	private boolean isSameYears = false;
	
	private List<SequenceTimeValue> sequenceTimeValueList;
	//历年同期查询，判断是否跨年
	private boolean isOverYear = false;
	//历年统计的参数类
	private TimesYearsParam timeYearsParm;
	
	/**
	 * 按时间段查询
	 * @param startDate
	 * @param endDate
	 * @param stations * 表示不过滤站。5%表示过滤自动站。57516,57517...表示只过滤这一部分站
	 */
	public void queryDataByRangeTimes(TimesRangeParam timesRangeParam, String stations, String tableName) {
		long start = System.currentTimeMillis();
		List<LinkedHashMap> resultList = null;
		String items = "";
		try {
			items = CommonTool.createItemStrByTimes(timesRangeParam.getStartYear(), timesRangeParam.getEndYear(),
					timesRangeParam.getStartMon(), timesRangeParam.getEndMon(),
					timesRangeParam.getStartDay(), timesRangeParam.getEndDay());
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", tableName);
		paramMap.put("items", items);
		paramMap.put("startYear", timesRangeParam.getStartYear());
		paramMap.put("endYear", timesRangeParam.getEndYear());
		if(null == stations || "".equals(stations) || "*".equals(stations)) {
			resultList = common.queryData(paramMap);
		} else if(stations.indexOf("%") != -1) {
			paramMap.put("Station_Id_C", stations);
			resultList = common.queryDataByStations(paramMap);
		} else if(stations.indexOf(",") != -1) {
			List<String> stationList = new ArrayList<String>();
			String[] stationArray = stations.split(",");
			for(int i=0; i<stationArray.length; i++) {
				stationList.add(stationArray[i].trim());
			}
			paramMap.put("Station_Id_C", stationList);
			resultList = common.queryDataByStationsSets(paramMap);
		} else {
			paramMap.put("Station_Id_C", stations);
			resultList = common.queryDataByStations(paramMap);
		}
		long end = System.currentTimeMillis();
//		System.out.println("查询花费时间：【" + (end - start) + "】");
		//结果处理。1. 不在时间段范围内的不用。2. 无效值的不用。
		disposeDataByRange(resultList, timesRangeParam, tableName);
		long end2 = System.currentTimeMillis();
//		System.out.println("处理DBData花费时间【" + (end2 - end) + "】");
	}
	
	
	/**
	 * 历年同期查询
	 * @param timeYearsParm
	 * @param stations * 表示不过滤站。5%表示过滤自动站。57516,57517...表示只过滤这一部分站
	 */
	public  void queryDataByYears(TimesYearsParam timeYearsParm, String stations, String tableName) {
		long start = System.currentTimeMillis();
		isSameYears = true;
		List<LinkedHashMap> resultList = null;
//		if(timeYearsParm.getStartYear() < timeYearsParm.getEndYear()) {
//			isOverYear = true;
//		} else {
//			isOverYear = CommonTool.isOverYear(timeYearsParm.getStartMon(), timeYearsParm.getStartDay(), timeYearsParm.getEndMon(), timeYearsParm.getEndDay());
//		}
		isOverYear = CommonTool.isOverYear(timeYearsParm.getStartMon(), timeYearsParm.getStartDay(), timeYearsParm.getEndMon(), timeYearsParm.getEndDay());
		String items = "";
		try {
			if(isOverYear) {
				items = CommonTool.createItemStrByTimes(timeYearsParm.getStartYear(), timeYearsParm.getStartYear() + 1,
						timeYearsParm.getStartMon(), timeYearsParm.getEndMon(),
						timeYearsParm.getStartDay(), timeYearsParm.getEndDay());
			} else {
				items = CommonTool.createItemStrByTimes(timeYearsParm.getYear(), timeYearsParm.getYear(),
					timeYearsParm.getStartMon(), timeYearsParm.getEndMon(),
					timeYearsParm.getStartDay(), timeYearsParm.getEndDay());
			}
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		int startYear = timeYearsParm.getStartYear();
		int endYear = timeYearsParm.getEndYear();
		if(isOverYear) {
			if(startYear <= endYear) {
				//跨年，大约是冬季，默认处理为开始念往前推一年
				startYear -= 1;
			}
		}
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", tableName);
		paramMap.put("items", items);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", timeYearsParm.getEndYear());
		if(null == stations || "".equals(stations) || "*".equals(stations)) {
			resultList = common.queryData(paramMap);
		} else if(stations.indexOf("%") != -1) {
			paramMap.put("Station_Id_C", stations);
			resultList = common.queryDataByStations(paramMap);
		} else if(stations.indexOf(",") != -1) {
			List<String> stationList = new ArrayList<String>();
			String[] stationArray = stations.split(",");
			for(int i=0; i<stationArray.length; i++) {
				stationList.add(stationArray[i].trim());
			}
			paramMap.put("Station_Id_C", stationList);
			resultList = common.queryDataByStationsSets(paramMap);
		} else {
			paramMap.put("Station_Id_C", stations);
			resultList = common.queryDataByStations(paramMap);
		}
		long end = System.currentTimeMillis();
//		System.out.println("by years 查询花费时间：【" + (end - start) + "】");
		//结果处理。1. 不在时间段范围内的不用。2. 无效值的不用。3. 为null的不用。往前推一年，所以第一年的上半部分数据不要。
		disposeDataByYears(resultList, timeYearsParm, chgStr2Set(items), tableName);
		long end2 = System.currentTimeMillis();
//		System.out.println("by years 处理DBData花费时间【" + (end2 - end) + "】");
	}
	
	/**
	 * 返回序列
	 * @return
	 */
	public List<SequenceTimeValue> getSequenceTimeValueList() {
		long start = System.currentTimeMillis();
		if(sequenceTimeValueList != null) {
			return sequenceTimeValueList;
		}
		Map<String, SequenceTimeValue> map = new HashMap<String, SequenceTimeValue>();
		List<SequenceTimeValue> resultList = new ArrayList<SequenceTimeValue>();
		//历年同期，需要处理年。否则是序列的话，就不用处理年份。
		for(DBData dbData : records) {
			String station_Id_C = dbData.getStation_Id_C();
			int year = dbData.getYear();
			String key = station_Id_C + "_" + year;
			SequenceTimeValue sequenceTimeValue = map.get(key);
			if(sequenceTimeValue == null) {
				sequenceTimeValue = new SequenceTimeValue();
				map.put(key, sequenceTimeValue);
			}
			TimeValue timeValue = new TimeValue();
			timeValue.setDate(dbData.getDate());
			timeValue.setValue(dbData.getValue());
			List<TimeValue> list = sequenceTimeValue.getTimeValues();
			if(list == null) {
				list = new ArrayList<TimeValue>();
			}
			list.add(timeValue);
			sequenceTimeValue.setStation_Id_C(dbData.getStation_Id_C());
			sequenceTimeValue.setLon(dbData.getLon());
			sequenceTimeValue.setLat(dbData.getLat());
			sequenceTimeValue.setYear(dbData.getYear());
			sequenceTimeValue.setStation_Name(dbData.getStation_Name());
			sequenceTimeValue.setTimeValues(list);
		}
		//把Map对象处理成List
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			SequenceTimeValue sequenceTimeValue = map.get(key);
			resultList.add(sequenceTimeValue);
		}
		long end = System.currentTimeMillis();
//		System.out.println("处理序列花费时间【" + (end - start) + "】");
		return resultList;
	}
	
	/**
	 * 把 站1,站2...处理成set集合
	 * @param items
	 * @return
	 */
	private Set<String> chgStr2Set(String items) {
		Set<String> stationsSet = new HashSet<String>();
		String[] array = items.split(",");
		for(int i=0; i<array.length; i++) {
			stationsSet.add(array[i]);
		}
		return stationsSet;
	}
	
	
	private void disposeDataByYears(List<LinkedHashMap> resultList, TimesYearsParam timeYearsParm, Set<String> items, String tableName) {
		if(resultList == null) {
			return;
		}
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		records = new ArrayList<DBData>();
		int startYear = timeYearsParm.getStartYear();
		int startMon = timeYearsParm.getStartMon();
		int startDay = timeYearsParm.getStartDay();
//		int startInt = Integer.parseInt(String.format("%02d", startMon + "") + String.format("%02d", startDay + ""));
		int startInt = timeYearsParm.getStartMon() * 100 + timeYearsParm.getStartDay();
		for(LinkedHashMap map : resultList) {
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			String yearStr = (Integer) map.get("year") + "";
			Integer year = Integer.parseInt(yearStr);
			Double lon = (Double) map.get("Lon");
			Double lat = (Double) map.get("Lat");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				DBData dbData = new DBData();
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					//1. 无效值的过滤掉
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					//2. 不在Set范围内的过滤掉
					if(!items.contains(key)) {
						continue;
					}
					String monStr = key.substring(1, 3);
					String dayStr = key.substring(4, 6);
					//3. 把第一年的前半段数据过滤掉
					if(isOverYear) {
						boolean isCurTimeOverYear = CommonTool.isCurTimeOverYear(Integer.parseInt(monStr), Integer.parseInt(dayStr), timeYearsParm.getStartMon(),
								timeYearsParm.getEndMon(), timeYearsParm.getStartDay(), timeYearsParm.getEndDay());
						if(isCurTimeOverYear) {
							dbData.setYear(year);
						} else {
							dbData.setYear(year + 1);
						}
//						if(year == startYear - 1) {
//							int curMonDay = Integer.parseInt(monStr + dayStr);
//							if(curMonDay < startInt) {
//								continue;
//							}
//						}
//						//大于开始月日，则算作第二年
//						int currentInt = dbData.getMon() * 100 + dbData.getDay();
//						if(currentInt >= startInt) {
//							dbData.setYear(year + 1);
//						}
					} else {
						dbData.setYear(year);
					}
					
					String currentTimeStr = yearStr + "-" + monStr + "-" + dayStr;
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					
					dbData.setStation_Id_C(station_Id_C);
					dbData.setStation_Name(station_Name);
					dbData.setLat(lat);
					dbData.setLon(lon);
					dbData.setDate(date);
					dbData.setValue(value);
					dbData.setMon(Integer.parseInt(monStr));
					dbData.setDay(Integer.parseInt(dayStr));
					records.add(dbData);
				}
			}
		}
		Collections.sort(records, new DBData());
	}
	
	private void disposeDataByRange(List<LinkedHashMap> resultList, TimesRangeParam timesRangeParam, String tableName) {
		if(resultList == null) {
			return;
		}
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		records = new ArrayList<DBData>();
		long start = timesRangeParam.getStartDate().getTime();
		long end = timesRangeParam.getEndDate().getTime();
		int year = timesRangeParam.getEndYear();
		for(LinkedHashMap map : resultList) {
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			String yearStr = (Integer) map.get("year") + "";
			Double lon = (Double) map.get("Lon");
			Double lat = (Double) map.get("Lat");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				DBData dbData = new DBData();
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					//1. 无效值的过滤掉
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					String monStr = key.substring(1, 3);
					String dayStr = key.substring(4, 6);
					//2. 不在指定时间段范围内的过滤掉
					String currentTimeStr = yearStr + "-" + monStr + "-" + dayStr;
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(current < start || current > end) {
						continue;
					}
					dbData.setStation_Id_C(station_Id_C);
					dbData.setStation_Name(station_Name);
					dbData.setLat(lat);
					dbData.setLon(lon);
					dbData.setDate(date);
					dbData.setValue(value);
					dbData.setMon(Integer.parseInt(monStr));
					dbData.setDay(Integer.parseInt(dayStr));
					dbData.setYear(year);
					records.add(dbData);
				}
			}
			Collections.sort(records, new DBData());
		}
	}
	
	/**
	 * 按时间排序
	 * @param records
	 * @return
	 */
	private void sort(List<DBData> sources) {
		for(int i = 0; i < sources.size() - 1; i++) {
			DBData iData = sources.get(i);
			long iTimes = iData.getDate().getTime();
			for(int j = i + 1; j < sources.size(); j++) {
				DBData jData = sources.get(j);
				long jTimes = jData.getDate().getTime();
				if(jTimes < iTimes) {
					DBData tempData = iData;
					iData = jData;
					jData = tempData;
				}
			}
		}
	}
}
