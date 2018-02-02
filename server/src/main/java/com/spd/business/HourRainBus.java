package com.spd.business;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import com.spd.common.HourRainAccumulateResult;
import com.spd.common.HourRainExtParam;
import com.spd.common.HourRainExtResult;
import com.spd.common.HourRainExtValues;
import com.spd.common.HourRainHisExtParam;
import com.spd.common.HourRainHisExtResult;
import com.spd.common.HourRainHisRankParam;
import com.spd.common.HourRainHisRankResult;
import com.spd.common.HourRainMaxResult;
import com.spd.common.HourRainProcessResult;
import com.spd.common.HourRainRangeParam;
import com.spd.common.HourRainRangeResult;
import com.spd.common.HourRainSequenceItemResult;
import com.spd.common.HourRainSequenceParam;
import com.spd.common.HourRainSequenceResult;
import com.spd.common.HourRainSortParam;
import com.spd.common.HourRainSortResult;
import com.spd.common.HourRainStation;
import com.spd.common.HourTimesParam;
import com.spd.common.StationType;
import com.spd.common.TimesParam;
import com.spd.service.ICommon;
import com.spd.service.IHourRain;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 小时降水
 * @author Administrator
 *
 */
public class HourRainBus {

//	private static HashMap<String, String> stationAreaMap = new HashMap<String, String>();
//
//	private static HashMap<String, String> stationNameMap = new HashMap<String, String>();
	
	private CommonUtil commonUtil = CommonUtil.getInstance();
	
	private static int ACCUMULATEDAYS = 15; //定义做降水累积的天数界限，大于这个天数的，就先查统计表
	
//	public HourRainBus() {
//		initStationAreaMap();
//	}
//	
//	public void initStationAreaMap() {
//		if(stationAreaMap.size() > 0 && stationNameMap.size() > 0) return;
//		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
//		List<LinkedHashMap> resultMapList = iCommon.getAllStations();
//		for(int i = 0; i < resultMapList.size(); i++) {
//			LinkedHashMap itemMap = resultMapList.get(i);
//			String Station_Id_C = (String) itemMap.get("Station_Id_C");
//			String Station_Name = (String) itemMap.get("Station_Name");
//			String area = (String) itemMap.get("area");
//			stationAreaMap.put(Station_Id_C, area);
//			stationNameMap.put(Station_Id_C, Station_Name);
//		}
//	}
	
	/**
	 * 降水极值
	 */
	public List<HourRainExtResult> hourRainExtAnalyst(HourTimesParam hourTimesParam) {
		//1. 判断时间段，如果在15天以内，直接调用统计表。
		int days = CommonTool.caleDays(hourTimesParam.getStartTimeStr(), hourTimesParam.getEndTimeStr());
		List<HourRainExtResult> hourRainExtResultList = new ArrayList<HourRainExtResult>();
		if(days < ACCUMULATEDAYS) {
			hourRainExtResultList = hourRainExt(hourTimesParam);
			return hourRainExtResultList;
		}
		//2. 如果超过15天，则先查统计表
		String[] times = hourRainStatisticsExt(hourTimesParam, hourRainExtResultList);
		if(times[0] == null && times[1] == null) {
			hourRainExtResultList = hourRainExt(hourTimesParam);
			return hourRainExtResultList;
		}
		//3. 把不在统计表的时间段范围内的时间查出来结果
		List<HourRainExtResult> hourRainAccumulateResultList1 = null, hourRainAccumulateResultList2 = null;
		HourTimesParam startHourTimesParam = hourTimesParam.copy();
		if(times != null && times[0] != null) {
			startHourTimesParam.setEndTimeStr(CommonTool.addHours(times[0], -1));
			int flag = CommonTool.compareDateHours(startHourTimesParam.getStartTimeStr(), startHourTimesParam.getEndTimeStr());
			if(flag == -1) {
				hourRainAccumulateResultList1 = hourRainExt(startHourTimesParam);
			}
		}
		
		HourTimesParam endHourTimesParam = hourTimesParam.copy();
		if(times != null && times[1] != null) {
			endHourTimesParam.setStartTimeStr(CommonTool.addHours(times[1], 1));
			int flag = CommonTool.compareDateHours(endHourTimesParam.getStartTimeStr(), endHourTimesParam.getEndTimeStr());
			if(flag == -1) {
				hourRainAccumulateResultList2 = hourRainExt(endHourTimesParam);
			}
			
		}
		List<HourRainExtResult> resultList = addExtAll(hourRainExtResultList, hourRainAccumulateResultList1, hourRainAccumulateResultList2);
		return resultList;
	}
	
	public List<HourRainExtResult> addExtAll(List<HourRainExtResult> list1, List<HourRainExtResult> list2, List<HourRainExtResult> list3) {
		List<HourRainExtResult> hourRainAccumulateResultList = new ArrayList<HourRainExtResult>();
		if(list1 != null) {
			for(int i = 0; i < list1.size(); i++) {
				HourRainExtResult item = list1.get(i);
				hourRainAccumulateResultList.add(item);
			}
		}
		if(list2 != null) {
			for(int i = 0; i < list2.size(); i++) {
				HourRainExtResult iItem = list2.get(i);
				Double iR1 = iItem.getHour1();
				Double iR3 = iItem.getHour3();
				Double iR6 = iItem.getHour6();
				Double iR12 = iItem.getHour12();
				Double iR24 = iItem.getHour24();
				
				String iStation_Id_C = iItem.getStation_Id_C();
				boolean flag = false;
				for(int j = 0; j < hourRainAccumulateResultList.size(); j++) {
					HourRainExtResult jItem = hourRainAccumulateResultList.get(j);
					Double jR1 = jItem.getHour1();
					Double jR3 = jItem.getHour3();
					Double jR6 = jItem.getHour6();
					Double jR12 = jItem.getHour12();
					Double jR24 = jItem.getHour24();
					String jStation_Id_C = jItem.getStation_Id_C();
					if(iStation_Id_C.equals(jStation_Id_C)) {
						if(iR1 > jR1) {
							jItem.setHour1(iR1);
						}
						if(iR3 > jR3) {
							jItem.setHour3(iR3);
						}
						if(iR6 > jR6) {
							jItem.setHour6(iR6);
						}
						if(iR12 > jR12) {
							jItem.setHour12(iR12);
						}
						if(iR24 > jR24) {
							jItem.setHour24(iR24);
						}
						flag = true;
						break;
					}
				}
				//
				if(!flag) {
					hourRainAccumulateResultList.add(iItem);
				}
			}
		}
		if(list3 != null) {
			for(int i = 0; i < list3.size(); i++) {
				HourRainExtResult iItem = list3.get(i);
				String iStation_Id_C = iItem.getStation_Id_C();
				Double iR1 = iItem.getHour1();
				Double iR3 = iItem.getHour3();
				Double iR6 = iItem.getHour6();
				Double iR12 = iItem.getHour12();
				Double iR24 = iItem.getHour24();
				boolean flag = false;
				for(int j = 0; j < hourRainAccumulateResultList.size(); j++) {
					HourRainExtResult jItem = hourRainAccumulateResultList.get(j);
					String jStation_Id_C = jItem.getStation_Id_C();
					Double jR1 = jItem.getHour1();
					Double jR3 = jItem.getHour3();
					Double jR6 = jItem.getHour6();
					Double jR12 = jItem.getHour12();
					Double jR24 = jItem.getHour24();
					if(iStation_Id_C.equals(jStation_Id_C)) {
						if(iR1 > jR1) {
							jItem.setHour1(iR1);
						}
						if(iR3 > jR3) {
							jItem.setHour3(iR3);
						}
						if(iR6 > jR6) {
							jItem.setHour6(iR6);
						}
						if(iR12 > jR12) {
							jItem.setHour12(iR12);
						}
						if(iR24 > jR24) {
							jItem.setHour24(iR24);
						}
						flag = true;
						break;
					}
				}
				//
				if(!flag) {
					hourRainAccumulateResultList.add(iItem);
				}
			}
		}
		return hourRainAccumulateResultList;
	}
	
	/**
	 * 查询极值降水统计表
	 * @param hourTimesParam
	 * @param hourRainExtResultList
	 * @return
	 */
	public String[] hourRainStatisticsExt(HourTimesParam hourTimesParam, List<HourRainExtResult> hourRainExtResultList) {
		String startTime = null, endTime = null;
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("StartTime", hourTimesParam.getStartTimeStr());
		paramMap.put("EndTime", hourTimesParam.getEndTimeStr());
		paramMap.put("type", hourTimesParam.getType());
		List<LinkedHashMap> resultList = hourRain.hourRainExtStatistics(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HourRainExtResult hourRainExtResult = new HourRainExtResult();
				LinkedHashMap itemMap = resultList.get(i);
				String itemStartTime = (String) itemMap.get("StartTime");
				String itemEndTime = (String) itemMap.get("EndTime");
				//starttime取最小
				if(startTime == null) {
					startTime = itemStartTime;
				} else {
					int flag = CommonTool.compareDateHours(startTime, itemStartTime);
					if(flag == 1) {
						startTime = itemStartTime;
					}
				}
				//endTime取最大
				if(endTime == null) {
					endTime = itemEndTime;
				} else {
					int flag = CommonTool.compareDateHours(endTime, itemEndTime);
					if(flag == -1) {
						endTime = itemEndTime;
					}
				}
				
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				hourRainExtResult.setStation_Id_C(station_Id_C);
				hourRainExtResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
				hourRainExtResult.setStation_Name(commonUtil.getInstance().stationNameMap.get(station_Id_C));
				hourRainExtResult.setHour1((Double) itemMap.get("R1"));
				hourRainExtResult.setHour3((Double) itemMap.get("R3"));
				hourRainExtResult.setHour6((Double) itemMap.get("R6"));
				hourRainExtResult.setHour12((Double) itemMap.get("R12"));
				hourRainExtResult.setHour24((Double) itemMap.get("R24"));
				hourRainExtResultList.add(hourRainExtResult);
			}
		}
		return new String[]{startTime, endTime};
	}
	
	/**
	 * 降水极值
	 * @param hourTimesParam
	 * @return
	 */
	public List<HourRainExtResult> hourRainExt(HourTimesParam hourTimesParam) {
		StationType stationType = StationType.getStationType(hourTimesParam.getType());
		List<HourRainExtResult> hourRainExtResultList = new ArrayList<HourRainExtResult>();
		List<HourRainExtResult> hourRainExtResultList2 = new ArrayList<HourRainExtResult>();
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", hourTimesParam.getStartTimeStr());
		paramMap.put("endTime", hourTimesParam.getEndTimeStr());
		int startYear = hourTimesParam.getStartYear();
		int endYear = hourTimesParam.getEndYear();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		//TODO xianchao 暂时这么处理
//		String tableName = "t_mwshourrain" + endYear;
		String tableName1 = createTable(startYear);
		paramMap.put("tableName1", tableName1);
		String tableName2 = createTable(endYear);
		paramMap.put("tableName2", tableName2);
		List<LinkedHashMap> resultList = null;
		long start1 = System.currentTimeMillis();
		switch(stationType) {
		case ALL:
			resultList = hourRain.hourRainExtAll(paramMap);
			break;
		case AWS:
			resultList = hourRain.hourRainExtAWS(paramMap);
			break;
		case MWS:
			resultList = hourRain.hourRainExtMWS(paramMap);
			break;
		case AREA:
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			resultList = hourRain.hourRainExtAREA(paramMap);
			break;
		default:
			break;
		}
		long start2 = System.currentTimeMillis();
		for(int i = 0; i < resultList.size(); i++) {
			HourRainExtResult hourRainExtResult = new HourRainExtResult();
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
//			String station_Name = (String) itemMap.get("Station_Name");
//			String area = (String) itemMap.get("area");
			String station_Name = commonUtil.stationNameMap.get(station_Id_C);
			String area = commonUtil.stationAreaMap.get(station_Id_C);
			hourRainExtResult.setStation_Id_C(station_Id_C);
			hourRainExtResult.setStation_Name(station_Name);
			hourRainExtResult.setArea(area);
			Double R1 = (Double) itemMap.get("R1");
			if(R1 != null) {
				R1 = CommonTool.roundDouble(R1);
			}
			Double R3 = (Double) itemMap.get("R3");
			if(R3 != null) {
				R3 = CommonTool.roundDouble(R3);
			}
			Double R6 = (Double) itemMap.get("R6");
			if(R6 != null) {
				R6 = CommonTool.roundDouble(R6);
			}
			Double R12 = (Double) itemMap.get("R12");
			if(R12 != null) {
				R12 = CommonTool.roundDouble(R12);
			}
			Double R24 = (Double) itemMap.get("R24");
			if(R24 != null) {
				R24 = CommonTool.roundDouble(R24);
			}
			hourRainExtResult.setHour1(R1);
			hourRainExtResult.setHour3(R3);
			hourRainExtResult.setHour6(R6);
			hourRainExtResult.setHour12(R12);
			hourRainExtResult.setHour24(R24);
			hourRainExtResultList.add(hourRainExtResult);
		}
		//
		String resultType = hourTimesParam.getResultType(); 
		if(resultType != null && !resultType.equals("ALL")) {
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			for(int i = 0; i < station_Id_Cs.size(); i++) {
				String station_Id_C = station_Id_Cs.get(i);
				if("AWS".equals(resultType)) {
					if(station_Id_C.startsWith("A")) {
						continue;
					}
				} else if("MWS".equals(resultType)) {
					if(station_Id_C.startsWith("5")) {
						continue;
					}
				}
				for(int j = 0; j < hourRainExtResultList.size(); j++) {
					HourRainExtResult hourRainExtResult = hourRainExtResultList.get(j);
					String itemStation_Id_c = hourRainExtResult.getStation_Id_C();
					if(station_Id_C.endsWith(itemStation_Id_c)) {
						hourRainExtResultList2.add(hourRainExtResult);
						break;
					}
				}
			}
		} else {
			hourRainExtResultList2 = hourRainExtResultList;
		}
		long start3 = System.currentTimeMillis();
		System.out.println("查询花费：" + (start2 - start1) + ",结果处理花费：" + (start3 - start2));
		return hourRainExtResultList2;
	}
	/**
	 * 查询统计表
	 * @param hourTimesParam
	 * @return
	 */
	public String[] hourRainStatisticsAccumulate(HourTimesParam hourTimesParam, List<HourRainAccumulateResult> hourRainAccumulateResultList) {
		String startTime = null, endTime = null;
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("StartTime", hourTimesParam.getStartTimeStr());
		paramMap.put("EndTime", hourTimesParam.getEndTimeStr());
		paramMap.put("type", hourTimesParam.getType());
		List<LinkedHashMap> resultList = hourRain.hourRainAccumulateStatistics(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HourRainAccumulateResult hourRainAccumulateResult = new HourRainAccumulateResult();
				LinkedHashMap itemMap = resultList.get(i);
				String itemStartTime = (String) itemMap.get("StartTime");
				String itemEndTime = (String) itemMap.get("EndTime");
				//starttime取最小
				if(startTime == null) {
					startTime = itemStartTime;
				} else {
					int flag = CommonTool.compareDateHours(startTime, itemStartTime);
					if(flag == 1) {
						startTime = itemStartTime;
					}
				}
				//endTime取最大
				if(endTime == null) {
					endTime = itemEndTime;
				} else {
					int flag = CommonTool.compareDateHours(endTime, itemEndTime);
					if(flag == -1) {
						endTime = itemEndTime;
					}
				}
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				hourRainAccumulateResult.setStation_Name(commonUtil.stationNameMap.get(station_Id_C));
				hourRainAccumulateResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
				hourRainAccumulateResult.setStation_Id_C(station_Id_C);
				java.math.BigDecimal count = (java.math.BigDecimal) itemMap.get("SumHours");
				hourRainAccumulateResult.setSumHours(count.intValue());
				hourRainAccumulateResult.setSumRain((Double)itemMap.get("SumRain"));
				hourRainAccumulateResultList.add(hourRainAccumulateResult);
			}
		}
		return new String[]{startTime, endTime};
	}
	
	public List<HourRainAccumulateResult> hourRainAccumulateAnalyst(HourTimesParam hourTimesParam) {
		//1. 判断时间段，如果在15天以内，直接调用统计表。
		int days = CommonTool.caleDays(hourTimesParam.getStartTimeStr(), hourTimesParam.getEndTimeStr());
		List<HourRainAccumulateResult> hourRainResultList = null;
		if(days < ACCUMULATEDAYS) {
			hourRainResultList = hourRainAccumulate(hourTimesParam);
			return hourRainResultList;
		}
		//2. 如果超过15天，则先查统计表
		List<HourRainAccumulateResult> hourRainStatistatistResultList = new ArrayList<HourRainAccumulateResult>();
		String[] times = hourRainStatisticsAccumulate(hourTimesParam, hourRainStatistatistResultList);
		if(times[0] == null && times[1] == null) {
			hourRainResultList = hourRainAccumulate(hourTimesParam);
			return hourRainResultList;
		}
		//3. 把不在统计表的时间段范围内的时间查出来结果
		List<HourRainAccumulateResult> hourRainAccumulateResultList1 = null, hourRainAccumulateResultList2 = null;
		HourTimesParam startHourTimesParam = hourTimesParam.copy();
		if(times != null && times[0] != null) {
			startHourTimesParam.setEndTimeStr(CommonTool.addHours(times[0], -1));
			int flag = CommonTool.compareDateHours(startHourTimesParam.getStartTimeStr(), startHourTimesParam.getEndTimeStr());
			if(flag == -1) {
				hourRainAccumulateResultList1 = hourRainAccumulate(startHourTimesParam);
			}
		}
		
		
		HourTimesParam endHourTimesParam = hourTimesParam.copy();
		
		if(times != null && times[1] != null) {
			endHourTimesParam.setStartTimeStr(CommonTool.addHours(times[1], 1));
			int flag = CommonTool.compareDateHours(endHourTimesParam.getStartTimeStr(), endHourTimesParam.getEndTimeStr());
			if(flag == -1) {
				hourRainAccumulateResultList2 = hourRainAccumulate(endHourTimesParam);
			}
			
		}
		
		//4. 把统计表的结果和非统计表的结果合并
		return addAll(hourRainStatistatistResultList, hourRainAccumulateResultList1, hourRainAccumulateResultList2);
	}
	
	public List<HourRainAccumulateResult> addAll(List<HourRainAccumulateResult> list1, List<HourRainAccumulateResult> list2, List<HourRainAccumulateResult> list3) {
		List<HourRainAccumulateResult> hourRainAccumulateResultList = new ArrayList<HourRainAccumulateResult>();
		if(list1 != null) {
			for(int i = 0; i < list1.size(); i++) {
				HourRainAccumulateResult item = list1.get(i);
				hourRainAccumulateResultList.add(item);
			}
		}
		if(list2 != null) {
			for(int i = 0; i < list2.size(); i++) {
				HourRainAccumulateResult iItem = list2.get(i);
				String iStation_Id_C = iItem.getStation_Id_C();
				boolean flag = false;
				for(int j = 0; j < hourRainAccumulateResultList.size(); j++) {
					HourRainAccumulateResult jItem = hourRainAccumulateResultList.get(j);
					String jStation_Id_C = jItem.getStation_Id_C();
					if(iStation_Id_C.equals(jStation_Id_C)) {
						jItem.setSumHours(jItem.getSumHours() + iItem.getSumHours());
						jItem.setSumRain(jItem.getSumRain() + iItem.getSumRain());
						flag = true;
						break;
					}
				}
				//
				if(!flag) {
					hourRainAccumulateResultList.add(iItem);
				}
			}
		}
		if(list3 != null) {
			for(int i = 0; i < list3.size(); i++) {
				HourRainAccumulateResult iItem = list3.get(i);
				String iStation_Id_C = iItem.getStation_Id_C();
				boolean flag = false;
				for(int j = 0; j < hourRainAccumulateResultList.size(); j++) {
					HourRainAccumulateResult jItem = hourRainAccumulateResultList.get(j);
					String jStation_Id_C = jItem.getStation_Id_C();
					if(iStation_Id_C.equals(jStation_Id_C)) {
						jItem.setSumHours(jItem.getSumHours() + iItem.getSumHours());
						jItem.setSumRain(jItem.getSumRain() + iItem.getSumRain());
						flag = true;
						break;
					}
				}
				//
				if(!flag) {
					hourRainAccumulateResultList.add(iItem);
				}
			}
		}
		return hourRainAccumulateResultList;
	}
	
	public List<HourRainAccumulateResult> hourRainAccumulate(HourTimesParam hourTimesParam) {
		long start1 = System.currentTimeMillis();
		StationType stationType = StationType.getStationType(hourTimesParam.getType());
		List<HourRainAccumulateResult> hourRainAccumulateResultList = new ArrayList<HourRainAccumulateResult>();
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", hourTimesParam.getStartTimeStr());
		paramMap.put("endTime", hourTimesParam.getEndTimeStr());
		int startYear = hourTimesParam.getStartYear();
		int endYear = hourTimesParam.getEndYear();
		String tableName1 = createTable(startYear);
		paramMap.put("tableName1", tableName1);
		String tableName2 = createTable(endYear);
		paramMap.put("tableName2", tableName2);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		List<LinkedHashMap> resultList = null;
		switch(stationType) {
		case ALL:
			resultList = hourRain.hourRainAccumulateAll(paramMap);
			break;
		case AWS:
			resultList = hourRain.hourRainAccumulateAWS(paramMap);
			break;
		case MWS:
			resultList = hourRain.hourRainAccumulateMWS(paramMap);
			break;
		case AREA:
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			resultList = hourRain.hourRainAccumulateAREA(paramMap);
			break;
		default:
			break;
		}
		long start2 = System.currentTimeMillis();
		for(int i = 0; i < resultList.size(); i++) {
			HourRainAccumulateResult hourRainAccumulateResult = new HourRainAccumulateResult();
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
//			hourRainAccumulateResult.setStation_Name((String) itemMap.get("Station_Name"));
//			hourRainAccumulateResult.setArea((String) itemMap.get("area"));
			hourRainAccumulateResult.setStation_Name(commonUtil.stationNameMap.get(station_Id_C));
			hourRainAccumulateResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
			hourRainAccumulateResult.setStation_Id_C(station_Id_C);
			Long countLong = (Long) itemMap.get("count");
			hourRainAccumulateResult.setSumHours(countLong.intValue());
			hourRainAccumulateResult.setSumRain((Double)itemMap.get("sum"));
			
			hourRainAccumulateResultList.add(hourRainAccumulateResult);
		}
		//过滤
		List<HourRainAccumulateResult> hourRainAccumulateResultList2 = new ArrayList<HourRainAccumulateResult>();
		List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
		String resultType = hourTimesParam.getResultType(); 
		if(resultType != null && !resultType.equals("ALL")) {
			for(int i = 0; i < station_Id_Cs.size(); i++) {
				String station_Id_C = station_Id_Cs.get(i);
				if("AWS".equals(resultType)) {
					if(station_Id_C.startsWith("A")) {
						continue;
					}
				} else if("MWS".equals(resultType)) {
					if(station_Id_C.startsWith("5")) {
						continue;
					}
				}
				for(int j = 0; j < hourRainAccumulateResultList.size(); j++) {
					HourRainAccumulateResult hourRainAccumulateResult = hourRainAccumulateResultList.get(j);
					String itemStation_Id_c = hourRainAccumulateResult.getStation_Id_C();
					if(station_Id_C.endsWith(itemStation_Id_c)) {
						hourRainAccumulateResultList2.add(hourRainAccumulateResult);
						break;
					}
				}
			}
		} else {
			hourRainAccumulateResultList2 = hourRainAccumulateResultList;
		}
		//把结果中没有，但是包含的站点也添加进去
//		List<HourRainAccumulateResult> hourRainAccumulateResultList3 = new ArrayList<HourRainAccumulateResult>();
//		String type = hourTimesParam.getType();
//		for(int i = 0; i < station_Id_Cs.size(); i++) {
//			String station_Id_C = station_Id_Cs.get(i);
//			if(type != null && type.equals("AWS")) {
//				if(station_Id_C.startsWith("A")) {
//					continue;
//				}
//			} else if(type != null && type.equals("MWS")) {
//				if(station_Id_C.startsWith("5")) {
//					continue;
//				}
//			}
//			boolean flag = false;
//			for(int j = 0; j < hourRainAccumulateResultList2.size(); j++) {
//				HourRainAccumulateResult item = hourRainAccumulateResultList2.get(j);
//				String itemStation_Id_C = item.getStation_Id_C();
//				if(station_Id_C.equals(itemStation_Id_C)) {
//					flag = true;
//					hourRainAccumulateResultList3.add(item);
//					break;
//				}
//			}
//			if(!flag) {
//				HourRainAccumulateResult item = new HourRainAccumulateResult();
//				item.setStation_Id_C(station_Id_C);
//				item.setStation_Name(commonUtil.stationNameMap.get(station_Id_C));
//				item.setArea(commonUtil.stationAreaMap.get(station_Id_C));
//				item.setSumHours(0);
//				item.setSumRain(0.0);
//				hourRainAccumulateResultList3.add(item);
//			}
//		}
		
		long start3 = System.currentTimeMillis();
		System.out.println("查询花费：" + (start2 - start1) + ",结果处理花费：" + (start3 - start2));
		return hourRainAccumulateResultList2;
	}
	
	public HourRainProcessResult hourRainSequence(HourTimesParam hourTimesParam) {
		long start1 = System.currentTimeMillis();
		StationType stationType = StationType.getStationType(hourTimesParam.getType());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HourRainProcessResult hourRainProcessResult = new HourRainProcessResult();
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", hourTimesParam.getStartTimeStr());
		paramMap.put("endTime", hourTimesParam.getEndTimeStr());
		int startYear = hourTimesParam.getStartYear();
		int endYear = hourTimesParam.getEndYear();
		//TODO xianchao 暂时这么处理
		String tableName1 = createTable(startYear);
		String tableName2 = createTable(endYear);
		paramMap.put("ritem", "R1");
		paramMap.put("tableName1", tableName1);
		paramMap.put("tableName2", tableName2);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		List<LinkedHashMap> resultList = null;
		switch(stationType) {
		case ALL:
			resultList = hourRain.hourRainSequenceAll(paramMap);
			break;
		case AWS:
			resultList = hourRain.hourRainSequenceAWS(paramMap);
			break;
		case MWS:
			resultList = hourRain.hourRainSequenceMWS(paramMap);
			break;
		case AREA:
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			resultList = hourRain.hourRainSequenceAREA(paramMap);
			break;
		default:
			break;
		}
		long start2 = System.currentTimeMillis();
		List<HourRainSequenceResult> hourRainSequenceResultList = new ArrayList<HourRainSequenceResult>();
		Map<String, List> map = new HashMap<String, List>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String datetimeStr = (String) itemMap.get("datetime");
			Date datetime = null;
			try {
				datetime = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			List list = map.get(station_Id_C);
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(itemMap);
			map.put(station_Id_C, list);
		}
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> list = map.get(key);
			LinkedHashMap firItemMap = list.get(0);
			Date firDateTime = null, preDateTime = null;
			try {
				firDateTime = sdf.parse((String) firItemMap.get("datetime"));
				preDateTime = firDateTime;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			double firRain = (Double) firItemMap.get("R1");
			int sumHours = 1;
			double sumRain = firRain;
			if(list.size() == 1) {
				HourRainSequenceResult hourRainSequenceResult = new HourRainSequenceResult();
				hourRainSequenceResult.setStartTime(sdf.format(firDateTime));
				hourRainSequenceResult.setEndTime(sdf.format(preDateTime));
				hourRainSequenceResult.setStation_Id_C(key);
				hourRainSequenceResult.setSumHours(sumHours);
				hourRainSequenceResult.setSumRain(CommonTool.roundDouble(sumRain));
				hourRainSequenceResult.setStation_Name(commonUtil.stationNameMap.get(key));
				hourRainSequenceResult.setArea(commonUtil.stationAreaMap.get(key));
				hourRainSequenceResultList.add(hourRainSequenceResult);
			} else {
				for(int i = 1; i < list.size(); i++) {
					LinkedHashMap itemMap = list.get(i);
					Date itemDateTime = null;
					try {
						itemDateTime = sdf.parse((String) itemMap.get("datetime"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					double itemSumRain = (Double) itemMap.get("R1");
					boolean isSequence = itemDateTime.getTime() - preDateTime.getTime() == CommonConstant.HOURTIMES;
					if(isSequence) {
						sumRain += itemSumRain;
						sumHours++;
						preDateTime = itemDateTime;
					}
					
					if(!isSequence) {
						HourRainSequenceResult hourRainSequenceResult = new HourRainSequenceResult();
						hourRainSequenceResult.setStartTime(sdf.format(firDateTime));
						hourRainSequenceResult.setEndTime(sdf.format(preDateTime));
						hourRainSequenceResult.setStation_Id_C(key);
						hourRainSequenceResult.setSumHours(sumHours);
						hourRainSequenceResult.setSumRain(CommonTool.roundDouble(sumRain));
						hourRainSequenceResult.setStation_Name(commonUtil.stationNameMap.get(key));
						hourRainSequenceResult.setArea(commonUtil.stationAreaMap.get(key));
	//					hourRainSequenceResult.setStation_Name((String) itemMap.get("Station_Name"));
	//					hourRainSequenceResult.setArea((String) itemMap.get("area"));
						hourRainSequenceResultList.add(hourRainSequenceResult);
						//数据重置
						sumHours = 1;
						sumRain = itemSumRain;
						preDateTime = itemDateTime;
						firDateTime = itemDateTime;
					}
					if(i == list.size() - 1) {
						HourRainSequenceResult hourRainSequenceResult = new HourRainSequenceResult();
						hourRainSequenceResult.setStartTime(sdf.format(firDateTime));
						hourRainSequenceResult.setEndTime(sdf.format(preDateTime));
						hourRainSequenceResult.setStation_Id_C(key);
						hourRainSequenceResult.setSumHours(sumHours);
						hourRainSequenceResult.setSumRain(CommonTool.roundDouble(sumRain));
	//					hourRainSequenceResult.setStation_Name((String) itemMap.get("Station_Name"));
	//					hourRainSequenceResult.setArea((String) itemMap.get("area"));
						hourRainSequenceResult.setStation_Name(commonUtil.stationNameMap.get(key));
						hourRainSequenceResult.setArea(commonUtil.stationAreaMap.get(key));
						hourRainSequenceResultList.add(hourRainSequenceResult);
					}
				}
			}
			hourRainProcessResult.setHourRainSequenceResultList(hourRainSequenceResultList);
		}
		
		List<HourRainMaxResult> hourRainMaxResultList = new ArrayList<HourRainMaxResult>();
		Map<String, List<HourRainSequenceResult>> hourRainMaxResultMap = new HashMap<String, List<HourRainSequenceResult>>();
		for(int i = 0; i < hourRainSequenceResultList.size(); i++) {
			HourRainSequenceResult hourRainMaxResult = hourRainSequenceResultList.get(i); 
			String station_Id_C = hourRainMaxResult.getStation_Id_C();
			List<HourRainSequenceResult> list = hourRainMaxResultMap.get(station_Id_C);
			if(list == null) {
				list = new ArrayList<HourRainSequenceResult>();
			}
			list.add(hourRainMaxResult);
			hourRainMaxResultMap.put(station_Id_C, list);
		}
		Iterator<String> hourRainMaxResultIt = hourRainMaxResultMap.keySet().iterator();
		while(hourRainMaxResultIt.hasNext()) {
			String key = hourRainMaxResultIt.next();
			List<HourRainSequenceResult> list = hourRainMaxResultMap.get(key);
			HourRainMaxResult HourRainMaxResult = new HourRainMaxResult(); 
			HourRainMaxResult.setStation_Id_C(key);
			double sumRain = 0;
			String startTimeStr = "", endTimeStr = "", stationName = "", area = "";
			int sumHours = 0;
			for(int i = 0; i < list.size(); i++) {
				HourRainSequenceResult hourRainSequenceResult = list.get(i);
				area = hourRainSequenceResult.getArea();
				stationName = hourRainSequenceResult.getStation_Name();
				double itemSumRain = hourRainSequenceResult.getSumRain();
				if(sumRain < itemSumRain) {
					sumRain = itemSumRain;
					sumHours = hourRainSequenceResult.getSumHours();
					startTimeStr = hourRainSequenceResult.getStartTime();
					endTimeStr = hourRainSequenceResult.getEndTime();
				}
			}
			HourRainMaxResult hourRainMaxResult = new HourRainMaxResult();
			hourRainMaxResult.setStation_Id_C(key);
			hourRainMaxResult.setStartTime(startTimeStr);
			hourRainMaxResult.setEndTime(endTimeStr);
			hourRainMaxResult.setStation_Name(stationName);
			hourRainMaxResult.setSumRain(sumRain);
			hourRainMaxResult.setSumHours(sumHours);
			hourRainMaxResult.setArea(area);
			hourRainMaxResultList.add(hourRainMaxResult);
		}
		hourRainProcessResult.setHourRainMaxResultList(hourRainMaxResultList);
		//过滤
		HourRainProcessResult hourRainProcessResult2 = new HourRainProcessResult();
		String resultType = hourTimesParam.getResultType(); 
		if(resultType != null && !resultType.equals("ALL")) {
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			List<HourRainMaxResult> hourRainMaxResultList2 = new ArrayList<HourRainMaxResult>();
			List<HourRainSequenceResult> hourRainSequenceResultList2 = new ArrayList<HourRainSequenceResult>();
			for(int i = 0; i < station_Id_Cs.size(); i++) {
				String station_Id_C = station_Id_Cs.get(i);
				if("AWS".equals(resultType)) {
					if(station_Id_C.startsWith("A")) {
						continue;
					}
				} else if("MWS".equals(resultType)) {
					if(station_Id_C.startsWith("5")) {
						continue;
					}
				}
				
				
				for(int j = 0; j < hourRainMaxResultList.size(); j++) {
					HourRainMaxResult hourRainMaxResult = hourRainMaxResultList.get(j);
					String itemStation_Id_c = hourRainMaxResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_c)) {
						hourRainMaxResultList2.add(hourRainMaxResult);
						break;
					}
				}
				for(int j = 0; j < hourRainSequenceResultList.size(); j++) {
					HourRainSequenceResult hourRainSequenceResult = hourRainSequenceResultList.get(j);
					String itemStation_Id_c = hourRainSequenceResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_c)) {
						hourRainSequenceResultList2.add(hourRainSequenceResult);
					}
				}
			}
			hourRainProcessResult2.setHourRainMaxResultList(hourRainMaxResultList2);
			hourRainProcessResult2.setHourRainSequenceResultList(hourRainSequenceResultList2);
		} else {
			hourRainProcessResult2 = hourRainProcessResult;
		}
		
		long start3 = System.currentTimeMillis();
		System.out.println("查询花费：" + (start2 - start1) + ",结果处理花费：" + (start3 - start2));
		return hourRainProcessResult2;
	}
	
	private List<HourRainRangeResult> queryHourRainRangeByTimesRange(HourTimesParam extTimesParam, int hour, String type) {
		StationType stationType = StationType.getStationType(type);
		List<HourRainRangeResult> hourRainRangeResultList = new ArrayList<HourRainRangeResult>(); 
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		//1. 查询指定时间段内的站的极值。
//		TimesParam extTimesParam = hourRainRangeParam.getExtTimesParam();
		HashMap extParamMap = new HashMap();
//		final String item =  "R" + hourRainRangeParam.getHour();
		final String item =  "R" + hour;
		extParamMap.put("startTime", extTimesParam.getStartTimeStr());
		extParamMap.put("endTime", extTimesParam.getEndTimeStr());
		extParamMap.put("ritem", item);
		//TODO xianchao 暂时这么处理
		int startYear = extTimesParam.getStartYear();
		String tableName1 = createTable(startYear);
		extParamMap.put("tableName1", tableName1);
		int endYear = extTimesParam.getEndYear();
		String tableName2 = createTable(endYear);
		extParamMap.put("tableName2", tableName2);
		extParamMap.put("startYear", startYear);
		extParamMap.put("endYear", endYear);
		List<LinkedHashMap> extResultList = null;
		switch(stationType) {
		case ALL:
			extResultList = hourRain.hourRainSequenceAll(extParamMap);
			break;
		case AWS:
			extResultList = hourRain.hourRainSequenceAWS(extParamMap);
			break;
		case MWS:
			extResultList = hourRain.hourRainSequenceMWS(extParamMap);
			break;
		case AREA:
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(extTimesParam.getAreaCode());
			extParamMap.put("Station_Id_Cs", station_Id_Cs);
			extResultList = hourRain.hourRainSequenceAREA(extParamMap);
			break;
		default:
			break;
		}
		for(int i = 0; i < extResultList.size(); i++) {
			LinkedHashMap extItemResult = extResultList.get(i);
			String Station_Id_C = (String) extItemResult.get("Station_Id_C");
			String Station_Name = commonUtil.stationNameMap.get(Station_Id_C);
			String area = commonUtil.stationAreaMap.get(Station_Id_C);
//			String Station_Name = (String) extItemResult.get("Station_Name");
//			hourRainRangeResult.setArea((String)extItemResult.get("area"));
			Double sumRain = (Double) extItemResult.get(item);
			HourRainRangeResult hourRainRangeResult = new HourRainRangeResult();
			hourRainRangeResult.setStation_Id_C(Station_Id_C);
			hourRainRangeResult.setStation_Name(Station_Name);
			hourRainRangeResult.setExtValue(sumRain);
			hourRainRangeResult.setArea(area);
			hourRainRangeResultList.add(hourRainRangeResult);
		}
		return hourRainRangeResultList;
	}
	
	private List<HourRainRangeResult> compareResult(List<LinkedHashMap> rankResultList, final String item,
			List<HourRainRangeResult> hourRainRangeResultList) {
		Map<String, List<LinkedHashMap>> rankResultMap = new LinkedHashMap<String, List<LinkedHashMap>>();
		for(int i = 0; i < rankResultList.size(); i++) {
			LinkedHashMap itemMap = rankResultList.get(i);
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
//			String Station_Name = (String) itemMap.get("Station_Name");
			Double sumRain = (Double) itemMap.get(item);
			List<LinkedHashMap> list = rankResultMap.get(Station_Id_C);
			if(rankResultMap.get(Station_Id_C) == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(itemMap);
			rankResultMap.put(Station_Id_C, list);
		}
		//对rankResultMap结果排序
		Iterator<String> it = rankResultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> itemList = rankResultMap.get(key);
			Collections.sort(itemList, new Comparator<LinkedHashMap>() {
				public int compare(LinkedHashMap o1, LinkedHashMap o2) {
					double rain1 = (Double) o1.get(item);
					double rain2 = (Double) o2.get(item);
					if(rain1 > rain2) return -1;
					if(rain1 == rain2) return 0;
					if(rain1 < rain2) return 1;
					return 0;
				}
			});
			rankResultMap.put(key, itemList);
		}
		//3. 进行对比结果
		List<HourRainRangeResult> hourRainRangeResultList2 = new ArrayList<HourRainRangeResult>();
		it = rankResultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			HourRainRangeResult HourRainRangeResult = null;
			double extValue = 0;
			for(int i = 0; i < hourRainRangeResultList.size(); i++) {
				HourRainRangeResult itemHourRainRangeResult = hourRainRangeResultList.get(i);
				String station_Id_C = itemHourRainRangeResult.getStation_Id_C();
				if(station_Id_C.equals(key)) {
					extValue = itemHourRainRangeResult.getExtValue();
				}
			}
			List<LinkedHashMap> itemList = rankResultMap.get(key);
			HourRainRangeResult hourRainRangeResult = new HourRainRangeResult();
			double preValue = 0;
			
			LinkedHashMap firItemMap = itemList.get(0);
			Double firRain = (Double) firItemMap.get(item);
			String Station_Id_C = (String) firItemMap.get("Station_Id_C");
//			String Station_Name = (String) firItemMap.get("Station_Name");
			String Station_Name = commonUtil.stationNameMap.get(Station_Id_C);
			String area = commonUtil.stationAreaMap.get(Station_Id_C);
			String firDatetime = (String) firItemMap.get("datetime");
			hourRainRangeResult.setHisExtValue(CommonTool.roundDouble(firRain));
			hourRainRangeResult.setStation_Id_C(Station_Id_C);
			hourRainRangeResult.setStation_Name(Station_Name);
			hourRainRangeResult.setHisExtTimes(firDatetime);
			hourRainRangeResult.setExtValue(extValue);
			hourRainRangeResult.setArea(area);
			hourRainRangeResult.setRank(1);
			preValue = firRain;
			if(extValue == 0.0) {
				hourRainRangeResult.setRank(itemList.size() + 1);
			} else {
				for(int i = 1; i < itemList.size(); i++) {
					LinkedHashMap itemMap = itemList.get(i);
					Double rain = (Double) itemMap.get(item);
					String datetime = (String) itemMap.get("datetime");
					if(rain == extValue) {
						hourRainRangeResult.setRank(i + 1);
						break;
					} else if(extValue < preValue && extValue > rain) {
						hourRainRangeResult.setRank(i + 1);
						break;
					}
				}
			}
			hourRainRangeResultList2.add(hourRainRangeResult);
		}
		
		return hourRainRangeResultList2;
	}
	
	/**
	 * 时段位次
	 * @param hourRainRangeParam
	 * @return
	 */
	public List<HourRainRangeResult> hourRainRankTimesStatistics(HourRainRangeParam hourRainRangeParam) {
		long start1 = System.currentTimeMillis();
		List<HourRainRangeResult> hourRainRangeResultList = queryHourRainRangeByTimesRange(hourRainRangeParam.getExtTimesParam(), hourRainRangeParam.getHour(), hourRainRangeParam.getType());
		long start2 = System.currentTimeMillis();
//		List<HourRainRangeResult> hourRainRangeResultList = new ArrayList<HourRainRangeResult>(); 
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
//		//1. 查询指定时间段内的站的极值。
		//2. 查询指定时间段内的站的序列。
		final String item =  "R" + hourRainRangeParam.getHour();
//		TimesParam rankTimesParam = hourRainRangeParam.getRankTimesParam();
		HourTimesParam hourTimesParam = hourRainRangeParam.getRankTimesParam();
		HashMap rankTimesParamMap = new HashMap();
		rankTimesParamMap.put("startTime", hourTimesParam.getStartTimeStr());
		rankTimesParamMap.put("endTime", hourTimesParam.getEndTimeStr());
		rankTimesParamMap.put("ritem", item);
		int startYear = hourRainRangeParam.getRankTimesParam().getStartYear();
		int endYear = hourRainRangeParam.getRankTimesParam().getEndYear();
		rankTimesParamMap.put("startYear", startYear);
		rankTimesParamMap.put("endYear", endYear);
		String tableName1 = createTable(startYear);
		rankTimesParamMap.put("tableName1", tableName1);
		String tableName2 = createTable(endYear);
		rankTimesParamMap.put("tableName2", tableName2);
		StationType stationType = StationType.getStationType(hourRainRangeParam.getType());
//		List<LinkedHashMap> rankResultList = hourRain.hourRainSequenceByItem(rankTimesParamMap);
		List<LinkedHashMap> rankResultList = null;
		switch(stationType) {
		case ALL:
			rankResultList = hourRain.hourRainSequenceAll(rankTimesParamMap);
			break;
		case AWS:
			rankResultList = hourRain.hourRainSequenceAWS(rankTimesParamMap);
			break;
		case MWS:
			rankResultList = hourRain.hourRainSequenceMWS(rankTimesParamMap);
			break;
		case AREA:
			rankResultList = hourRain.hourRainSequenceAll(rankTimesParamMap);
			filterByStations(rankResultList, hourTimesParam.getAreaCode());
			break;
//		case AREA:
//			List<String> station_Id_Cs = queryStation_Id_CByCountry(hourTimesParam.getCountry());
//			rankTimesParamMap.put("Station_Id_Cs", station_Id_Cs);
//			rankResultList = hourRain.hourRainSequenceAREA(rankTimesParamMap);
//			break;
		default:
			break;
		}
		long start3 = System.currentTimeMillis();
		List<HourRainRangeResult> hourRainRangeResultList2 = compareResult(rankResultList, item, hourRainRangeResultList);
		long start4 = System.currentTimeMillis();
		System.out.println("查询1:" + (start2 - start1) + ", 查询2：" + (start3 - start2) + ", 处理结果：" + (start4 - start3));
		//过滤
		List<HourRainRangeResult> hourRainRangeResultList3 = new ArrayList<HourRainRangeResult>(); 
		String resultType = hourTimesParam.getResultType(); 
		if(resultType != null && !resultType.equals("ALL")) {
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			for(int i = 0; i < station_Id_Cs.size(); i++) {
				String station_Id_C = station_Id_Cs.get(i);
				if("AWS".equals(resultType)) {
					if(station_Id_C.startsWith("A")) {
						continue;
					}
				} else if("MWS".equals(resultType)) {
					if(station_Id_C.startsWith("5")) {
						continue;
					}
				}
				for(int j = 0; j < hourRainRangeResultList2.size(); j++) {
					HourRainRangeResult hourRainRangeResult = hourRainRangeResultList2.get(j);
					String itemStation_Id_c = hourRainRangeResult.getStation_Id_C();
					if(station_Id_C.endsWith(itemStation_Id_c)) {
						hourRainRangeResultList3.add(hourRainRangeResult);
						break;
					}
				}
			}
		} else {
			hourRainRangeResultList3 = hourRainRangeResultList2;
		}
		return hourRainRangeResultList3;
	}
	
	public List<HourRainHisRankResult> hourRainRankYearsStatistics(HourRainHisRankParam hourRainHisRankParam) {
		long start1 = System.currentTimeMillis();
		final String item =  "R" + hourRainHisRankParam.getHour();
		List<HourRainHisRankResult> hourRainHisRankResultList = new ArrayList<HourRainHisRankResult>();
		//1. 查询当年
		List<HourRainRangeResult> hourRainRangeResultList = queryHourRainRangeByTimesRange(hourRainHisRankParam.getHourTimesParam(), hourRainHisRankParam.getHour(), hourRainHisRankParam.getType());
		long start2 = System.currentTimeMillis();
		//2 .查询历史
		HashMap rankTimesParamMap = new HashMap();
		HourTimesParam extTimesParam = hourRainHisRankParam.getHourTimesParam();
//		String startMMdd = extTimesParam.getStartTimeStr().substring(4, extTimesParam.getStartTimeStr().length());
//		String endMMdd = extTimesParam.getEndTimeStr().substring(4, extTimesParam.getEndTimeStr().length());
//		rankTimesParamMap.put("startYear", hourRainHisRankParam.getStartYear());
//		rankTimesParamMap.put("endYear", hourRainHisRankParam.getEndYear());
//		rankTimesParamMap.put("startDay", extTimesParam.getStartMon() * 10000 + extTimesParam.getStartDay() * 100 + extTimesParam.getStartHour());
//		rankTimesParamMap.put("endDay", extTimesParam.getEndMon() * 10000 + extTimesParam.getEndDay() * 100 + extTimesParam.getEndHour());
		rankTimesParamMap.put("ritem", item);
		String datetimeclaus = createDateTimeClaus(extTimesParam.getStartTimeStr(), extTimesParam.getEndTimeStr(), hourRainHisRankParam.getStartYear(), hourRainHisRankParam.getEndYear());
		rankTimesParamMap.put("datetimeclaus", datetimeclaus);
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		StationType stationType = StationType.getStationType(hourRainHisRankParam.getType());
		List<LinkedHashMap> rankResultList = null;
		switch(stationType) {
		case ALL:
		case AREA:
			rankResultList = hourRain.hourRainSequenceBySameYearsAll(rankTimesParamMap);
			break;
		case AWS:
			rankResultList = hourRain.hourRainSequenceBySameYearsAWS(rankTimesParamMap);
			break;
		case MWS:
			rankResultList = hourRain.hourRainSequenceBySameYearsMWS(rankTimesParamMap);
			break;
		default:
			break;
		}
		if(stationType == StationType.AREA) {
			filterByStations(rankResultList, extTimesParam.getAreaCode());
		}
		long start3 = System.currentTimeMillis();
		//3. 对比结果
		List<HourRainRangeResult> hourRainRangeResultList2 = compareResult(rankResultList, item, hourRainRangeResultList);
		for(int i = 0; i < hourRainRangeResultList2.size(); i++) {
			HourRainRangeResult hourRainRangeResultItem = hourRainRangeResultList2.get(i);
			HourRainHisRankResult hourRainHisRankResult = new HourRainHisRankResult();
			hourRainHisRankResult.setArea(hourRainRangeResultItem.getArea());
			hourRainHisRankResult.setExtValue(hourRainRangeResultItem.getExtValue());
			hourRainHisRankResult.setHisExtTime(hourRainRangeResultItem.getHisExtTimes());
			hourRainHisRankResult.setHisExtValue(hourRainRangeResultItem.getHisExtValue());
			hourRainHisRankResult.setRank(hourRainRangeResultItem.getRank());
			hourRainHisRankResult.setStation_Id_C(hourRainRangeResultItem.getStation_Id_C());
			hourRainHisRankResult.setStation_Name(hourRainRangeResultItem.getStation_Name());
			hourRainHisRankResultList.add(hourRainHisRankResult);
		}
		long start4 = System.currentTimeMillis();
		System.out.println("查询当年：" + (start2 - start1) + ", 查询历史：" + (start3 - start2) + "处理结果：" + (start4 - start3));
		//过滤
		List<HourRainHisRankResult> hourRainHisRankResultList2 = new ArrayList<HourRainHisRankResult>();
		String resultType = extTimesParam.getResultType();
		if(resultType != null && !resultType.equals("ALL")) {
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(extTimesParam.getAreaCode());
			for(int i = 0; i < station_Id_Cs.size(); i++) {
				String station_Id_C = station_Id_Cs.get(i);
				if("AWS".equals(resultType)) {
					if(station_Id_C.startsWith("A")) {
						continue;
					}
				} else if("MWS".equals(resultType)) {
					if(station_Id_C.startsWith("5")) {
						continue;
					}
				}
				for(int j = 0; j < hourRainRangeResultList2.size(); j++) {
					HourRainHisRankResult hourRainHisRankResult = hourRainHisRankResultList.get(j);
					String itemStation_Id_c = hourRainHisRankResult.getStation_Id_C();
					if(station_Id_C.endsWith(itemStation_Id_c)) {
						hourRainHisRankResultList2.add(hourRainHisRankResult);
						break;
					}
				}
			}
		} else {
			hourRainHisRankResultList2 = hourRainHisRankResultList;
		}
		return hourRainHisRankResultList2;
	}
	
	public List<HourRainHisExtResult> hourRainExtYearsStatistics(HourRainHisExtParam hourRainHisExtParam) {
		long start1 = System.currentTimeMillis();
		String station_Id_C = hourRainHisExtParam.getStation_Id_C();
		//1. 查询结果
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		final String item =  "R" + hourRainHisExtParam.getHour();
		HashMap hourRainHisParamMap = new HashMap();
		HourTimesParam extTimesParam = hourRainHisExtParam.getHourTimesParam();
//		hourRainHisParamMap.put("startYear", hourRainHisExtParam.getStartYear());
//		hourRainHisParamMap.put("endYear", hourRainHisExtParam.getEndYear());
//		hourRainHisParamMap.put("startDay", extTimesParam.getStartMon() * 10000 + extTimesParam.getStartDay() * 100 + extTimesParam.getStartHour());
//		hourRainHisParamMap.put("endDay", extTimesParam.getEndMon() * 10000 + extTimesParam.getEndDay() * 100 + extTimesParam.getEndHour());
		hourRainHisParamMap.put("item", item);
		String datetimeclaus = createDateTimeClaus(extTimesParam.getStartTimeStr(), extTimesParam.getEndTimeStr(), hourRainHisExtParam.getStartYear(), hourRainHisExtParam.getEndYear());
		hourRainHisParamMap.put("datetimeclaus", datetimeclaus);
		List<LinkedHashMap>  resultList = null;//
		String country = extTimesParam.getAreaCode();
		String type =extTimesParam.getType();
		if(type != null && type.equals("AREA")) {
			resultList = hourRain.hourRainExtYearsStatisticsALL(hourRainHisParamMap);
			filterByStations(resultList, extTimesParam.getAreaCode());
		} else if(station_Id_C.startsWith("5")) {
			hourRainHisParamMap.put("Station_Id_C", station_Id_C);
			resultList = hourRain.hourRainExtYearsStatisticsAWS(hourRainHisParamMap);
		} else if(station_Id_C.startsWith("A")) {
			hourRainHisParamMap.put("Station_Id_C", station_Id_C);
			resultList = hourRain.hourRainExtYearsStatisticsMWS(hourRainHisParamMap);
		} else if(station_Id_C.equals("*")) {
			resultList = hourRain.hourRainExtYearsStatisticsALL(hourRainHisParamMap);
		}
		
		long start2 = System.currentTimeMillis();
		//2. 遍历结果
		Map<Integer, List<LinkedHashMap>> rankResultMap = new HashMap<Integer, List<LinkedHashMap>>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String year = (String) itemMap.get("year");
			Double sumRain = (Double) itemMap.get(item);
			List<LinkedHashMap> list = rankResultMap.get(Integer.parseInt(year));
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(itemMap);
			rankResultMap.put(Integer.parseInt(year), list);
		}
		Iterator<Integer> it = rankResultMap.keySet().iterator();
		List<HourRainHisExtResult> hourRainHisExtResultList = new ArrayList<HourRainHisExtResult>();
		while(it.hasNext()) {
			Integer key = it.next();
			List<LinkedHashMap> list = rankResultMap.get(key);
			HourRainHisExtResult hourRainHisExtResult = new HourRainHisExtResult();
			double maxRain = 0;
			String datetimeStr = "";
			for(int i = 0; i < list.size(); i++) {
				LinkedHashMap itemMap = list.get(i);
				Double itemRain = (Double) itemMap.get(item);
				if(itemRain > maxRain) {
					maxRain = itemRain;
					datetimeStr = (String) itemMap.get("datetime");
				}
			}
			hourRainHisExtResult.setExtValue(CommonTool.roundDouble(maxRain));
			hourRainHisExtResult.setExtTimes(datetimeStr);
			hourRainHisExtResult.setYear(key);
			hourRainHisExtResultList.add(hourRainHisExtResult);
		}
		List<HourRainHisExtResult> hourRainHisExtResultList2 = new ArrayList<HourRainHisExtResult>();
		for(int i = hourRainHisExtParam.getStartYear(); i <= hourRainHisExtParam.getEndYear(); i++) {
			boolean flag = false;
			for(int j = 0; j < hourRainHisExtResultList.size(); j++) {
				HourRainHisExtResult hourRainHisExtResult = hourRainHisExtResultList.get(j);
				int year = hourRainHisExtResult.getYear();
				if(i == year) {
					hourRainHisExtResultList2.add(hourRainHisExtResult);
					flag = true;
					break;
				}
			}
			if(flag == false) {
				HourRainHisExtResult hourRainHisExtResult = new HourRainHisExtResult();
				hourRainHisExtResult.setExtValue(0);
				hourRainHisExtResult.setExtTimes("");
				hourRainHisExtResult.setYear(i);
				hourRainHisExtResultList2.add(hourRainHisExtResult);
			}
		}
		long start3 = System.currentTimeMillis();
		System.out.println("查询花费：" + (start2 - start1) + ", 处理花费：" + (start3 - start2));
		return hourRainHisExtResultList2;
	}
	
	public List<HourRainExtValues> hourRainExtByTimes(HourRainExtParam hourRainExtParam) {
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		List<HourRainExtValues> resultList = new ArrayList<HourRainExtValues>();
		//1 . 查询数据库。
		HourTimesParam hourTimesParam = hourRainExtParam.getHourTimesParam();
		StationType stationType = StationType.getStationType(hourRainExtParam.getType());
		List<LinkedHashMap> hourrainResultList = null;
		HashMap paramMap = new HashMap();
		paramMap.put("startDatetime", hourTimesParam.getStartTimeStr());
		paramMap.put("endDatetime", hourTimesParam.getEndTimeStr());
		String tableName = createTable(hourTimesParam.getStartYear());
		paramMap.put("tableName", tableName);
		long start1 = System.currentTimeMillis();
		switch(stationType) {
		case ALL:
			hourrainResultList = hourRain.hourRainExtByTimesAll(paramMap);
			break;
//		case AREA:
//			hourrainResultList = hourRain.hourRainExtByTimesAll(paramMap);
//			filterByStations(hourrainResultList, hourTimesParam.getCountry());
//			break;
		case AWS:
			hourrainResultList = hourRain.hourRainExtByTimesAWS(paramMap);
			break;
		case MWS:
			hourrainResultList = hourRain.hourRainExtByTimesMWS(paramMap);
			break;
		case AREA:
			List<String> station_Id_Cs = queryStation_Id_CByAreaCode(hourTimesParam.getAreaCode());
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			hourrainResultList = hourRain.hourRainExtByTimesAREA(paramMap);
			break;
		default:
			break;
		}
		long start2 = System.currentTimeMillis();
		Double maxR1 = 0.0, maxR3 = 0.0, maxR6 = 0.0, maxR12 = 0.0, maxR24 = 0.0;
		// 处理结果，遍历，找到最大的R1,R3,R6,R12,R24
		for(int i = 0; i < hourrainResultList.size(); i++) {
			LinkedHashMap itemMap = hourrainResultList.get(i);
			Double R1 = (Double) itemMap.get("R1");
			Double R3 = (Double) itemMap.get("R3");
			Double R6 = (Double) itemMap.get("R6");
			Double R12 = (Double) itemMap.get("R12");
			Double R24 = (Double) itemMap.get("R24");
			if(R1 != null && R1 > maxR1) {
				maxR1 = R1;
			}
			if(R3 != null && R3 > maxR3) {
				maxR3 = R3;
			}
			if(R6 != null && R6 > maxR6) {
				maxR6 = R6;
			}
			if(R12 != null && R12 > maxR12) {
				maxR12 = R12;
			}
			if(R24 != null && R24 > maxR24) {
				maxR24 = R24;
			}
		}
		
		HashMap sortParamMap = new HashMap();
		for(int i = 0; i < hourrainResultList.size(); i++) {
			LinkedHashMap itemMap = hourrainResultList.get(i);
			Double R1 = (Double) itemMap.get("R1");
			Double R3 = (Double) itemMap.get("R3");
			Double R6 = (Double) itemMap.get("R6");
			Double R12 = (Double) itemMap.get("R12");
			Double R24 = (Double) itemMap.get("R24");
			String datetime = (String) itemMap.get("datetime");
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
//			String Station_Name = (String) itemMap.get("Station_Name");
//			String area = (String) itemMap.get("area");
			String Station_Name = commonUtil.stationNameMap.get(Station_Id_C);
			String area = commonUtil.stationAreaMap.get(Station_Id_C);
			
			if(R1 == maxR1) {
				HourRainExtValues hourRainExtValues = new HourRainExtValues();
				hourRainExtValues.setExtTimes(datetime);
				hourRainExtValues.setExtValue(CommonTool.roundDouble(maxR1));
				hourRainExtValues.setStation_Id_C(Station_Id_C);
				hourRainExtValues.setStation_Name(Station_Name);
				hourRainExtValues.setType("R1");
				hourRainExtValues.setArea(area);
				resultList.add(hourRainExtValues);
				sortParamMap.put("Station_Id_C1", Station_Id_C);
				sortParamMap.put("r1", CommonTool.roundDouble(maxR1));
				if(Station_Id_C.startsWith("5")) {
					sortParamMap.put("tableName1", "t_awshourrain");
				} else {
					sortParamMap.put("tableName1", "t_mwshourrain");
				}
			}
			if(R3 == maxR3) {
				HourRainExtValues hourRainExtValues = new HourRainExtValues();
				hourRainExtValues.setExtTimes(datetime);
				hourRainExtValues.setExtValue(CommonTool.roundDouble(maxR3));
				hourRainExtValues.setStation_Id_C(Station_Id_C);
				hourRainExtValues.setStation_Name(Station_Name);
				hourRainExtValues.setType("R3");
				hourRainExtValues.setArea(area);
				resultList.add(hourRainExtValues);
				sortParamMap.put("Station_Id_C3", Station_Id_C);
				sortParamMap.put("r3", CommonTool.roundDouble(maxR3));
				if(Station_Id_C.startsWith("5")) {
					sortParamMap.put("tableName2", "t_awshourrain");
				} else {
					sortParamMap.put("tableName2", "t_mwshourrain");
				}
			}
			if(R6== maxR6) {
				HourRainExtValues hourRainExtValues = new HourRainExtValues();
				hourRainExtValues.setExtTimes(datetime);
				hourRainExtValues.setExtValue(CommonTool.roundDouble(maxR6));
				hourRainExtValues.setStation_Id_C(Station_Id_C);
				hourRainExtValues.setStation_Name(Station_Name);
				hourRainExtValues.setType("R6");
				hourRainExtValues.setArea(area);
				resultList.add(hourRainExtValues);
				sortParamMap.put("Station_Id_C6", Station_Id_C);
				sortParamMap.put("r6", CommonTool.roundDouble(maxR6));
				if(Station_Id_C.startsWith("5")) {
					sortParamMap.put("tableName3", "t_awshourrain");
				} else {
					sortParamMap.put("tableName3", "t_mwshourrain");
				}
			}
			if(R12 == maxR12) {
				HourRainExtValues hourRainExtValues = new HourRainExtValues();
				hourRainExtValues.setExtTimes(datetime);
				hourRainExtValues.setExtValue(CommonTool.roundDouble(maxR12));
				hourRainExtValues.setStation_Id_C(Station_Id_C);
				hourRainExtValues.setStation_Name(Station_Name);
				hourRainExtValues.setType("R12");
				hourRainExtValues.setArea(area);
				resultList.add(hourRainExtValues);
				sortParamMap.put("Station_Id_C12", Station_Id_C);
				sortParamMap.put("r12", CommonTool.roundDouble(maxR12));
				if(Station_Id_C.startsWith("5")) {
					sortParamMap.put("tableName4", "t_awshourrain");
				} else {
					sortParamMap.put("tableName4", "t_mwshourrain");
				}
			}
			if(R24 == maxR24) {
				HourRainExtValues hourRainExtValues = new HourRainExtValues();
				hourRainExtValues.setExtTimes(datetime);
				hourRainExtValues.setExtValue(CommonTool.roundDouble(maxR24));
				hourRainExtValues.setStation_Id_C(Station_Id_C);
				hourRainExtValues.setStation_Name(Station_Name);
				hourRainExtValues.setType("R24");
				hourRainExtValues.setArea(area);
				resultList.add(hourRainExtValues);
				sortParamMap.put("Station_Id_C24", Station_Id_C);
				sortParamMap.put("r24", CommonTool.roundDouble(maxR24));
				if(Station_Id_C.startsWith("5")) {
					sortParamMap.put("tableName5", "t_awshourrain");
				} else {
					sortParamMap.put("tableName5", "t_mwshourrain");
				}
			}
		}
		
		List<String> station_id_Cs = new ArrayList<String>();
		if(resultList.size() == 0) return null;
		for(int i = 0; i < resultList.size(); i++) {
			HourRainExtValues hourRainExtValues = resultList.get(i);
			String Station_Id_C = hourRainExtValues.getStation_Id_C();
			station_id_Cs.add(Station_Id_C);
		}
		HashMap hourRainStationByStationsParamMap = new HashMap();
		hourRainStationByStationsParamMap.put("Station_Id_C", station_id_Cs);
		List<LinkedHashMap> stationsResult = hourRain.hourRainStationByStations(hourRainStationByStationsParamMap);
		long start3 = System.currentTimeMillis();
		HashMap<String, String> stationBuildDateMap = new HashMap<String, String>();
		for(int i = 0; i < stationsResult.size(); i++) {
			LinkedHashMap itemMap = stationsResult.get(i);
			stationBuildDateMap.put((String)itemMap.get("Station_Id_C"), (String)itemMap.get("buildDate"));
		}
		
		List<LinkedHashMap> sortListResult = hourRain.hourRainSort(sortParamMap);
		for(int j = 0; j < resultList.size(); j++) {
			HourRainExtValues hourRainExtValues = resultList.get(j);
			String station_Id_C = hourRainExtValues.getStation_Id_C();
			hourRainExtValues.setBuildDate(stationBuildDateMap.get(station_Id_C));
			for(int i = 0; i < sortListResult.size(); i++) {
				LinkedHashMap itemMap = sortListResult.get(i);
				String type = (String) itemMap.get("type");
				Long sort = (Long) itemMap.get("sort");
				if(type.equals(hourRainExtValues.getType())) {
					hourRainExtValues.setSort(sort.intValue());
					break;
				}
			}
		}
		//对resultList排序。
		Collections.sort(resultList, new HourRainExtValues());
		long start4 = System.currentTimeMillis();
		System.out.println("查询花费：" + (start2 - start1) + ", 第二次查询：" + (start3 - start2) + ", 处理结果：" + (start4 - start3));
		return resultList;
	}
	
	/**
	 * 逐时演变
	 * @param hourRainSequenceParam
	 * @return
	 */
	public Object hourRainChange(HourRainSequenceParam hourRainSequenceParam) {
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		List<HourRainExtValues> resultList = new ArrayList<HourRainExtValues>();
		//1 . 查询数据库。
		HourTimesParam hourTimesParam = hourRainSequenceParam.getHourTimesParam();
		String startTimeStr = hourTimesParam.getStartTimeStr();
		String endTimeStr = hourTimesParam.getEndTimeStr();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0L, endTime = 0L;
		try {
			startTime = sdf.parse(startTimeStr).getTime();
			endTime = sdf.parse(endTimeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String station_Id_C = hourRainSequenceParam.getStation_Id_C();
		String type = "";
		if(station_Id_C.startsWith("5")) {
			type = "AWS";
		} else if(station_Id_C.startsWith("A")) {
			type = "MWS";
		}
		StationType stationType = StationType.getStationType(type);
		List<LinkedHashMap> hourrainResultList = null;
		HashMap paramMap = new HashMap();
		paramMap.put("startDatetime", hourTimesParam.getStartTimeStr());
		paramMap.put("endDatetime", hourTimesParam.getEndTimeStr());
		paramMap.put("Station_Id_C", hourRainSequenceParam.getStation_Id_C());
		int startYear = hourTimesParam.getStartYear();
		int endYear = hourTimesParam.getEndYear();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String tableName1 = createTable(startYear);
		paramMap.put("tableName1", tableName1);
		String tableName2 = createTable(endYear);
		paramMap.put("tableName2", tableName2);
		switch(stationType) {
		case ALL:
			hourrainResultList = hourRain.hourRainChangeAll(paramMap);
			break;
		case AWS:
			hourrainResultList = hourRain.hourRainChangeAWS(paramMap);
			break;
		case MWS:
			hourrainResultList = hourRain.hourRainChangeMWS(paramMap);
			break;
		default:
			break;
		}
		
		List<HourRainSequenceItemResult> result = new ArrayList<HourRainSequenceItemResult>();
		for(long time = startTime; time <= endTime; time += CommonConstant.HOURTIMES) {
			String timeStr = sdf.format(new Date(time));
			boolean flag = false;
			for(int i = 0; i < hourrainResultList.size(); i++) {
				LinkedHashMap itemMap = hourrainResultList.get(i);
				String datetime = (String) itemMap.get("datetime");
				if(timeStr.equals(datetime)) {
					Double R1 = (Double) itemMap.get("R1");
					R1 = R1 == null ? 0 : R1; 
					Double R3 = (Double) itemMap.get("R3");
					R3 = R3 == null ? 0 : R3;
					Double R6 = (Double) itemMap.get("R6");
					R6 = R6 == null ? 0 : R6;
					Double R12 = (Double) itemMap.get("R12");
					R12 = R12 == null ? 0 : R12;
					Double R24 = (Double) itemMap.get("R24");
					R24 = R24 == null ? 0 : R24;
					String Station_Id_C = (String) itemMap.get("Station_Id_C");
					HourRainSequenceItemResult hourRainSequenceItemResult = new HourRainSequenceItemResult();
					hourRainSequenceItemResult.setArea(commonUtil.stationAreaMap.get(Station_Id_C));
					hourRainSequenceItemResult.setDatetime(datetime);
					hourRainSequenceItemResult.setR1(CommonTool.roundDouble(R1));
					hourRainSequenceItemResult.setR3(CommonTool.roundDouble(R3));
					hourRainSequenceItemResult.setR6(CommonTool.roundDouble(R6));
					hourRainSequenceItemResult.setR12(CommonTool.roundDouble(R12));
					hourRainSequenceItemResult.setR24(CommonTool.roundDouble(R24));
					hourRainSequenceItemResult.setStation_Id_C(Station_Id_C);
					hourRainSequenceItemResult.setStation_Name(commonUtil.stationNameMap.get(Station_Id_C));
					result.add(hourRainSequenceItemResult);
					flag = true;
					break;
				}
			}
			if(!flag) {
				HourRainSequenceItemResult hourRainSequenceItemResult = new HourRainSequenceItemResult();
				hourRainSequenceItemResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
				hourRainSequenceItemResult.setDatetime(timeStr);
				hourRainSequenceItemResult.setR1(0);
				hourRainSequenceItemResult.setR3(0);
				hourRainSequenceItemResult.setR6(0);
				hourRainSequenceItemResult.setR12(0);
				hourRainSequenceItemResult.setR24(0);
				hourRainSequenceItemResult.setStation_Id_C(hourRainSequenceParam.getStation_Id_C());
				hourRainSequenceItemResult.setStation_Name(commonUtil.stationNameMap.get(station_Id_C));
				result.add(hourRainSequenceItemResult);
			}
		}
		return result;
	}
	
	public List<HourRainStation> hourRainStation(List<String> stationList) {
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> resultList = hourRain.hourRainStation(paramMap);
		List<HourRainStation> hourRainStationList = new ArrayList<HourRainStation>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			HourRainStation hourRainStation = new HourRainStation();
			String station_Id_C = (String)itemMap.get("Station_Id_C");
			if(stationList != null && stationList.size() > 0) {
				for(int j = 0; j < stationList.size(); j++) {
					if(station_Id_C.equals(stationList.get(j))) {
						String station_Name = (String)itemMap.get("Station_Name");
						String buildDate = (String)itemMap.get("buildDate");
						hourRainStation.setStation_Id_C(station_Id_C);
						hourRainStation.setStation_Name(station_Name);
						hourRainStation.setBuildDate(buildDate);
						hourRainStationList.add(hourRainStation);
						break;
					}
				}
			} else {
				String station_Name = (String)itemMap.get("Station_Name");
				String buildDate = (String)itemMap.get("buildDate");
				hourRainStation.setStation_Id_C(station_Id_C);
				hourRainStation.setStation_Name(station_Name);
				hourRainStation.setBuildDate(buildDate);
				hourRainStationList.add(hourRainStation);
			}
		}
		return hourRainStationList;
	}
	
	public Object hourRainSortByStation(HourRainSortParam hourRainSortParam) {
		List<HourRainSortResult> hourRainSortResultList = new ArrayList<HourRainSortResult>();
		IHourRain hourRain = (IHourRain)ContextLoader.getCurrentWebApplicationContext().getBean("HourrainImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("item", hourRainSortParam.getType());
		paramMap.put("limit", hourRainSortParam.getLimit());
		String station_Id_C = hourRainSortParam.getStation_Id_C();
		paramMap.put("Station_Id_C", station_Id_C);
		if(station_Id_C.startsWith("5")) {
			paramMap.put("tableName", "t_awshourrain");
		} else {
			paramMap.put("tableName", "t_mwshourrain");
		}
		List<LinkedHashMap> resultList = hourRain.hourRainSortByStation(paramMap);
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap item = resultList.get(i);
			HourRainSortResult hourRainSortResult = new HourRainSortResult();
			hourRainSortResult.setStation_Id_C(station_Id_C);
			hourRainSortResult.setStation_Name(commonUtil.stationNameMap.get(station_Id_C));
			hourRainSortResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
			hourRainSortResult.setDatetime((String) item.get("datetime"));
			hourRainSortResult.setValue(CommonTool.roundDouble((Double) item.get("value")));
			hourRainSortResult.setIndex(i + 1);
			hourRainSortResultList.add(hourRainSortResult);
		}
		
		return hourRainSortResultList;
	}
	
	private String createTable(int year) {
		String tableName = "t_mwshourrain" + year;
		if(year < 2007) {
			tableName = "t_mwshourrain_tmp";
		}
		return tableName;
	}
	
	private String createDateTimeClaus(String startTimeStr, String endTimeStr, int hisStartYear, int hisEndYear) {
		//考虑时间跨年的问题，如果跨年，则把结束时间对应上年份，上一年对应开始年，比如开始时间：2015-12-29 10:00 结束时间：2016-01-01 10:00, 
		//hisStartYear:1991, hisEndYear:2016， 则1991对应的历史时间，则为1990-12-29 10:00 到1991-01-01 10:00
		int startYear = Integer.parseInt(startTimeStr.substring(0, 4));
		int endYear = Integer.parseInt(endTimeStr.substring(0, 4));
		String datetimeclaus = "";
		if(startYear == endYear) {
			datetimeclaus = "("; //构造关于时间的查询条件,主要是为了能用上索引
			String startMMdd = startTimeStr.substring(4, startTimeStr.length());
			String endMMdd = endTimeStr.substring(4, endTimeStr.length());
			for(int i = hisStartYear; i <= hisEndYear; i++) {
				String clausItem = "(a.datetime >= '" + i + startMMdd + "' and a.datetime <= '" + i + endMMdd + "')";
				if(i != hisEndYear) {
					clausItem += " or ";
				}
				datetimeclaus += clausItem;
			}
			datetimeclaus += ")";
		} else {
			datetimeclaus = "("; //构造关于时间的查询条件,主要是为了能用上索引
			String startMMdd = startTimeStr.substring(4, startTimeStr.length());
			String endMMdd = endTimeStr.substring(4, endTimeStr.length());
			for(int i = hisStartYear; i <= hisEndYear; i++) {
				String clausItem = "(a.datetime >= '" + (i - 1) + startMMdd + "' and a.datetime <= '" + i + endMMdd + "')";
				if(i != hisEndYear) {
					clausItem += " or ";
				}
				datetimeclaus += clausItem;
			}
			datetimeclaus += ")";
		}
		return datetimeclaus;
	}
	
	private List<String> queryStation_Id_CByAreaCode(String areaCode) {
		List<String> station_Id_Cs = new ArrayList<String>();
		if("*".equals(areaCode)) {
			return null;
		}
		ICommon common = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap commonParamMap = new HashMap();
		commonParamMap.put("areaCode", areaCode);
		List<LinkedHashMap> contrastStationResult = common.queryStation_Id_CByAreaCode(commonParamMap);
		for(int i = 0; i < contrastStationResult.size(); i++) {
			LinkedHashMap itemMap = contrastStationResult.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			station_Id_Cs.add(station_Id_C);
		}
		return station_Id_Cs;
	}
	
	private void filterByStations(List<LinkedHashMap> rankResultList, String areaCode) {
		List<String> station_Id_Cs = queryStation_Id_CByAreaCode(areaCode);
		Set<String> stationSet = new HashSet<String>();
		for(int i = 0; i < station_Id_Cs.size(); i++) {
			stationSet.add(station_Id_Cs.get(i));
		}
		// 过滤掉一部分
		for(int i = rankResultList.size() - 1; i >=0; i--) {
			LinkedHashMap itemMap = rankResultList.get(i);
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
			if(!stationSet.contains(Station_Id_C)) {
				rankResultList.remove(i);
			}
		}
	}
}
