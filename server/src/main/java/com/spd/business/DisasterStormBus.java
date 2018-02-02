package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.RainStormEvaluateResult;
import com.spd.common.TimesParam;
import com.spd.common.evaluate.RainStormAreaTimesRangeParam;
import com.spd.common.evaluate.RainStormAreaTimesResult;
import com.spd.common.evaluate.RainStormStationParam;
import com.spd.common.evaluate.RainStormStationResult;
import com.spd.service.IDisasterEvaluate;
import com.spd.service.IRainStorm;
import com.spd.tool.CommonTool;

/**
 * 灾害统计中的区域暴雨
 * @author Administrator
 *
 */
public class DisasterStormBus {

	private IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");

	private IRainStorm rainStorm = (IRainStorm)ContextLoader.getCurrentWebApplicationContext().getBean("RainStormImpl");
	
	/**
	 * 区域暴雨
	 * @param rainStormAreaTimesRangeParam
	 * @return
	 */
	public List<RainStormAreaTimesResult> areaStormByTimes(RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam) {
		String type = rainStormAreaTimesRangeParam.getType();
		List<RainStormAreaTimesResult> resultList = new ArrayList<RainStormAreaTimesResult>();
		TimesParam timesParam = rainStormAreaTimesRangeParam.getTimesParam();
		String startTime = timesParam.getStartTimeStr();
		String endTime = timesParam.getEndTimeStr();
		List list = queryDataByTimes(startTime, endTime, type);
		//处理结果，把连续的分别进行分组
		if(list == null || list.size() == 0) return null;
		String preStartTimeStr = "", lastEndTimeStr = "";
		preStartTimeStr = (String) ((HashMap) list.get(0)).get("datetime");
		lastEndTimeStr = (String) ((HashMap) list.get(list.size() - 1)).get("datetime");
		String firTimeStr = getDataListByTime(preStartTimeStr, list, type, -1);
		String lastTimeStr = getDataListByTime(lastEndTimeStr, list, type, 1);
		//再对list进行按区段分组
		HashMap resultMap = groupDataList(firTimeStr, lastTimeStr, type);
		Iterator it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			List dataList = (List) resultMap.get(key);
			RainStormAreaTimesResult rainStormAreaTimesResult = cale(dataList, rainStormAreaTimesRangeParam);
			if(rainStormAreaTimesResult != null) resultList.add(rainStormAreaTimesResult);
		}
		return resultList;
	}
	
	/**
	 * 暴雨年度指标
	 * @param rainStormAreaTimesRangeParam
	 */
	public List<RainStormEvaluateResult> rainstormByYears(RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam) {
		//1. 把RainStormAreaTimesRangeParam构造成RainStormAreaTimesRangeParam
		RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam2 = chgParam(rainStormAreaTimesRangeParam);
		//2. 调用areaStormByTimes
		List<RainStormAreaTimesResult> rainStormAreaTimesResultList = areaStormByTimes(rainStormAreaTimesRangeParam2);
		//3. 返回的结果构造RainStormEvaluateResult对象
		boolean isOverYear = isOverYear(rainStormAreaTimesRangeParam);
		List<RainStormEvaluateResult> resultList = chgResult(rainStormAreaTimesRangeParam2, rainStormAreaTimesResultList,
				rainStormAreaTimesRangeParam.getStartYear(), rainStormAreaTimesRangeParam.getEndYear(), 
				rainStormAreaTimesRangeParam.getPerennialStartYear(), rainStormAreaTimesRangeParam.getPerennialEndYear(), isOverYear);
		return resultList;
	}
	
	private boolean isOverYear(RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam) {
		TimesParam timesParam = rainStormAreaTimesRangeParam.getTimesParam();
		String startTimesStr = timesParam.getStartTimeStr();
		String endTimesStr = timesParam.getEndTimeStr();
		Integer startInt = Integer.parseInt(startTimesStr.substring(4).replaceAll("-", ""));
		Integer endInt = Integer.parseInt(endTimesStr.substring(4).replaceAll("-", ""));
		if(startInt < endInt) return false;
		return true;
	}
	private List<RainStormEvaluateResult> chgResult(RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam,
			List<RainStormAreaTimesResult> rainStormAreaTimesResultList, int startYear, int endYear, 
			int perennialStartYear, int perennialEndYear, boolean isOverYear) {
		
		HashMap<Integer, Integer> cntMap = new HashMap<Integer, Integer>(); //记录年份：次数
		HashMap<Integer, Double> indexMap = new HashMap<Integer, Double>(); //记录年份：评估值
		
		TimesParam timesParam = rainStormAreaTimesRangeParam.getTimesParam();
		String startTimesStr = timesParam.getStartTimeStr();
		String endTimesStr = timesParam.getEndTimeStr();
		Integer startInt = Integer.parseInt(startTimesStr.substring(4).replaceAll("-", ""));
		Integer endInt = Integer.parseInt(endTimesStr.substring(4).replaceAll("-", ""));
		
		List<RainStormEvaluateResult> resultList = new ArrayList<RainStormEvaluateResult>();
		Double sumYearsIndex = 0.0; // 常年评估值和
		Double sumYearsCnt = 0.0; //常年次数和
		for(int i = 0; i < rainStormAreaTimesResultList.size(); i++) {
			RainStormAreaTimesResult rainStormAreaTimesResult = rainStormAreaTimesResultList.get(i);
			String itemStartTime = rainStormAreaTimesResult.getStartTime();
			String itemEndTime = rainStormAreaTimesResult.getEndTime();
			Integer itemStartYear = Integer.parseInt(itemStartTime.substring(0, 4));
			Integer itemStartInt = Integer.parseInt(itemStartTime.substring(4).replaceAll("-", ""));
			Integer itemEndInt = Integer.parseInt(itemEndTime.substring(4).replaceAll("-", ""));
			Double index = rainStormAreaTimesResult.getIndex2(); //不等权指标
			if(isOverYear) {
				if(itemStartInt >= startInt) {
					//当年的
					if(cntMap.containsKey(itemStartYear)) {
						Integer cnt = cntMap.get(itemStartYear);
						cntMap.put(itemStartYear, cnt + 1);
					} else {
						cntMap.put(itemStartYear, 1);
					}
					//指标
					if(indexMap.containsKey(itemStartYear)) {
						Double tempIndex = indexMap.get(itemStartYear);
						indexMap.put(itemStartYear, tempIndex + index);
					} else {
						indexMap.put(itemStartYear, index);
					}
				} else if(itemEndInt <= endInt) {
					//前一年的
					if(cntMap.containsKey(itemStartYear - 1)) {
						Integer cnt = cntMap.get(itemStartYear - 1);
						cntMap.put(itemStartYear - 1, cnt + 1);
					} else {
						cntMap.put(itemStartYear - 1, 1);
					}
					//指标
					if(indexMap.containsKey(itemStartYear - 1)) {
						Double tempIndex = indexMap.get(itemStartYear - 1);
						indexMap.put(itemStartYear - 1, tempIndex + index);
					} else {
						indexMap.put(itemStartYear - 1, index);
					}
				}
			} else {
				if(itemStartInt >= startInt && itemEndInt <= endInt) {
					//当年的结果
					if(cntMap.containsKey(itemStartYear)) {
						Integer cnt = cntMap.get(itemStartYear);
						cntMap.put(itemStartYear, cnt + 1);
					} else {
						cntMap.put(itemStartYear, 1);
					}
					//指标
					if(indexMap.containsKey(itemStartYear)) {
						Double tempIndex = indexMap.get(itemStartYear);
						indexMap.put(itemStartYear, tempIndex + index);
					} else {
						indexMap.put(itemStartYear, index);
					}
				}
			}
		}
		//统计常年结果
		for(int i = perennialStartYear; i <= perennialEndYear; i++) {
			Integer yearCnt = cntMap.get(i);
			Double index = indexMap.get(i);
			if(index != null) {
				sumYearsIndex += index;
			}
			if(yearCnt != null) {
				sumYearsCnt += yearCnt;
			}
		}
		
		Double avgYearsIndex = sumYearsIndex / (perennialEndYear - perennialStartYear + 1); // 常年评估值平均
		Double avgYearsCnt = sumYearsCnt / (perennialEndYear - perennialStartYear + 1); //常年次数平均
		avgYearsIndex = CommonTool.roundDouble2(avgYearsIndex);
		avgYearsCnt = CommonTool.roundDouble(avgYearsCnt);
		
		//统计结果
		for(int i = startYear; i <= endYear; i++) {
			RainStormEvaluateResult rainStormEvaluateResult = new RainStormEvaluateResult();
			rainStormEvaluateResult.setYear(i);
			rainStormEvaluateResult.setYearCnt(avgYearsCnt);
			rainStormEvaluateResult.setYearIndex(avgYearsIndex);
			Integer cnt = cntMap.get(i);
			if(cnt == null) {
				cnt = 0;
			} 
			rainStormEvaluateResult.setCnt(cnt);
			Double index = indexMap.get(i);
			if(index == null) {
				index = 0.0;
			} 
			rainStormEvaluateResult.setIndex(CommonTool.roundDouble2(index));
			rainStormEvaluateResult.setAnomalyCnt(CommonTool.roundDouble(cnt - avgYearsCnt));
			rainStormEvaluateResult.setAnomalyIndex(CommonTool.roundDouble2(index - avgYearsIndex));
			resultList.add(rainStormEvaluateResult);
		}
		return resultList;
	}
	
	private RainStormAreaTimesRangeParam chgParam(RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam) {
		//构造开始，结束的时间
		RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam2 = null;
		try {
			rainStormAreaTimesRangeParam2 = (RainStormAreaTimesRangeParam) rainStormAreaTimesRangeParam.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		int startYear = rainStormAreaTimesRangeParam.getStartYear();
		int endYear = rainStormAreaTimesRangeParam.getEndYear();
		int perennialStartYear = rainStormAreaTimesRangeParam.getPerennialStartYear();
		int perennialEndYear = rainStormAreaTimesRangeParam.getPerennialEndYear();
		int minYear = startYear, maxYear = startYear;
		if(endYear < minYear) minYear = endYear;
		if(perennialStartYear < minYear) minYear = perennialStartYear;
		if(perennialEndYear < minYear) minYear = perennialEndYear;
		
		if(endYear > maxYear) maxYear = endYear;
		if(perennialStartYear > maxYear) maxYear = perennialStartYear;
		if(perennialEndYear > maxYear) maxYear = perennialEndYear;
		rainStormAreaTimesRangeParam2.setStartYear(minYear);
		rainStormAreaTimesRangeParam2.setEndYear(maxYear);
		TimesParam timesParam = rainStormAreaTimesRangeParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		startTimeStr = minYear + startTimeStr.substring(4, startTimeStr.length());
		endTimeStr = maxYear + endTimeStr.substring(4, endTimeStr.length());
		timesParam.setStartTimeStr(startTimeStr);
		timesParam.setEndTimeStr(endTimeStr);
		rainStormAreaTimesRangeParam2.setTimesParam(timesParam);
		return rainStormAreaTimesRangeParam2;
	}
	
	private RainStormAreaTimesResult cale(List dataList, RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
		
		TimesParam timesParam = rainStormAreaTimesRangeParam.getTimesParam();
		boolean isOverYear = false;
		String startTimesStr = timesParam.getStartTimeStr();
		String endTimesStr = timesParam.getEndTimeStr();
		Integer startTimeInt = null, endTimeInt = null;
		try {
			Date startDate = sdf.parse(startTimesStr);
			Date endDate = sdf.parse(endTimesStr);
			startTimeInt = Integer.parseInt(sdf2.format(startDate));
			endTimeInt = Integer.parseInt(sdf2.format(endDate));
			if(startTimeInt > endTimeInt) {
				isOverYear = true;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		RainStormAreaTimesResult rainStormAreaTimesResult = new RainStormAreaTimesResult();
		double totalPre = 0, maxPre = 0;
		int stations =  dataList.size(); // 站数
		int persistDays = 0;// 持续时间
		Date minDate = null, maxDate = null;
		int times = 0;
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String datetimeStr = (String) dataMap.get("datetime");
			try {
				Date date = sdf.parse(datetimeStr);
				if(minDate == null) {
					minDate = date;
				}
				if(maxDate == null) {
					maxDate = date;
				}
				if(date.getTime() < minDate.getTime()) {
					minDate = date;
				}
				if(date.getTime() > maxDate.getTime()) {
					maxDate = date;
				}
				//针对时间，进行过滤，把没在这个时间段范围的删除掉，不考虑跨年的问题
				//过滤功能先去掉
//				String minStr = sdf2.format(minDate);
//				String maxStr = sdf2.format(maxDate);
//				Integer minInt = Integer.parseInt(minStr);
//				Integer maxInt = Integer.parseInt(maxStr);
//				if(startTimeInt != null && endTimeInt != null){
//					if(!isOverYear && (minInt < startTimeInt || maxInt > endTimeInt)) {
//						continue;
//					}
//					if(isOverYear && minInt > endTimeInt && maxInt < startTimeInt) {
//						continue;
//					}
//				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			times++;
			Double pre = (Double) dataMap.get("extPre");
			totalPre += pre;
			if(maxPre <= pre) {
				maxPre = pre;
			}
		}
		rainStormAreaTimesResult.setTimes(times);
		totalPre *= 10;
		persistDays = ((Long)((maxDate.getTime() - minDate.getTime()) / CommonConstant.DAYTIMES)).intValue() + 1;
		if(stations < rainStormAreaTimesRangeParam.getMinStationCnt()) return null;
		if(persistDays < rainStormAreaTimesRangeParam.getMinPersistDays()) return null;
		//计算指数
		Double IA = (totalPre - rainStormAreaTimesRangeParam.getMinPre()) / (rainStormAreaTimesRangeParam.getMaxPre() - rainStormAreaTimesRangeParam.getMinPre());
		Double IB = (maxPre - rainStormAreaTimesRangeParam.getMinSignalPre()) / (rainStormAreaTimesRangeParam.getMaxSignalPre() - rainStormAreaTimesRangeParam.getMinSignalPre());
		Double IC = (stations - rainStormAreaTimesRangeParam.getMinStationCnt()) / (rainStormAreaTimesRangeParam.getMaxStationCnt() - rainStormAreaTimesRangeParam.getMinStationCnt() + 0.0);
		Double ID = (persistDays - rainStormAreaTimesRangeParam.getMinPersistDays()) / (rainStormAreaTimesRangeParam.getMaxPersistDays() - rainStormAreaTimesRangeParam.getMinPersistDays() + 0.0);
		Double index1 = (IA + IB + IC + ID) / 4;
		Double index2 = IA * rainStormAreaTimesRangeParam.getWeight1() + IB * rainStormAreaTimesRangeParam.getWeight3() + IC * rainStormAreaTimesRangeParam.getWeight2() + ID * rainStormAreaTimesRangeParam.getWeight4();
		String minDateStr = sdf.format(minDate);
		String maxDateStr = sdf.format(maxDate);
		rainStormAreaTimesResult.setEndTime(maxDateStr);
		rainStormAreaTimesResult.setIndex1(CommonTool.roundDouble3(index1));
		rainStormAreaTimesResult.setIndex2(CommonTool.roundDouble3(index2));
		rainStormAreaTimesResult.setMaxPre(maxPre);
		rainStormAreaTimesResult.setPersistDays(persistDays);
		rainStormAreaTimesResult.setStartTime(minDateStr);
		rainStormAreaTimesResult.setStations(stations);
		rainStormAreaTimesResult.setTotalPre(CommonTool.roundDouble(totalPre / 10.0));
		rainStormAreaTimesResult.setType(rainStormAreaTimesRangeParam.getType());
		if(index2 >= rainStormAreaTimesRangeParam.getLevel1() && index2 < rainStormAreaTimesRangeParam.getLevel2()) {
			rainStormAreaTimesResult.setLevel("轻度");
		} else if (index2 >= rainStormAreaTimesRangeParam.getLevel2() && index2 < rainStormAreaTimesRangeParam.getLevel3()) {
			rainStormAreaTimesResult.setLevel("中度");
		} else if (index2 >= rainStormAreaTimesRangeParam.getLevel3() && index2 < rainStormAreaTimesRangeParam.getLevel4()) {
			rainStormAreaTimesResult.setLevel("重度");
		} else if (index2 >= rainStormAreaTimesRangeParam.getLevel4()) {
			rainStormAreaTimesResult.setLevel("特重");
		}
		return rainStormAreaTimesResult;
	}
	
	private List queryDataByTimes(String startTime, String endTime, String type) {
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("type", type);
		List list = disasterEvaluate.areaStormByTimes(paramMap);
		return list;
	}
	
	/**
	 * 查询并且按照时间分组，连续的分在一组里。
	 * @param list
	 * @return
	 */
	private HashMap groupDataList(String startTime, String endTime, String type) {
		List list = queryDataByTimes(startTime, endTime, type);
		HashMap resultMap = new HashMap();
		Date startDate = null, endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		HashMap firDataMap = (HashMap) list.get(0);
		String startTimeStr = (String) firDataMap.get("datetime");
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Date tempStartDate = startDate;
		int indexDays = 0;
		int index = 0;
		for(int i = 0; i < list.size(); i++) {
			HashMap itemDataMap = (HashMap) list.get(i);
			String itemDateStr = (String) itemDataMap.get("datetime");
			Date itemDate = null;
			try {
				itemDate = sdf.parse(itemDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(itemDate.getTime() - tempStartDate.getTime() == 0 || itemDate.getTime() - tempStartDate.getTime() == CommonConstant.DAYTIMES) {
				tempStartDate = itemDate;
				List itemList = (List) resultMap.get(index);
				if(itemList == null) {
					itemList = new ArrayList();
				}
				itemList.add(itemDataMap);
				resultMap.put(index, itemList);
			} else {
				index++;
				tempStartDate = itemDate;
				List itemList = (List) resultMap.get(index);
				if(itemList == null) {
					itemList = new ArrayList();
				}
				itemList.add(itemDataMap);
				resultMap.put(index, itemList);
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据时间查询这个时间点是否有结果 
	 * @param datetime
	 * @param dirType 当为-1的时候，往前追溯，当为1的时候，往后追溯
	 * @return
	 */
	private String getDataListByTime(String datetime, List preList, String type, int dirType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String preTimeStr = "";
		try {
			Date date = sdf.parse(datetime);
			date = new Date(date.getTime() + dirType * CommonConstant.DAYTIMES);
			preTimeStr = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		HashMap paramMap = new HashMap();
		paramMap.put("datetime", preTimeStr);
		paramMap.put("type", type);
		List resultList = disasterEvaluate.areaStormByTime(paramMap);
		if(resultList == null || resultList.size() == 0) return datetime;
		return getDataListByTime(preTimeStr, preList, type, dirType);
	}
	
	/**
	 * 单点暴雨
	 * @param rainStormStationParam
	 * @return
	 */
	public RainStormStationResult stationStormByTimes(RainStormStationParam rainStormStationParam) {
		RainStormStationResult rainStormStationResult = new RainStormStationResult();
		TimesParam timesParam = rainStormStationParam.getTimesParam();
		//1. 查询年度暴雨总和，年度站点暴雨总和
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List stationResultList = rainStorm.queryStatisticsRainStormByTimes(paramMap);
		//2. 查询区域暴雨的总和，站数
		List areaResultList = disasterEvaluate.areaStormStatisticsByTime(paramMap);
		//3. 计算
		if(stationResultList == null || stationResultList.size() == 0) return null;
		if(areaResultList == null || areaResultList.size() == 0) return null;
		HashMap stationResultMap = (HashMap) stationResultList.get(0);
		Double stationSum = (Double) stationResultMap.get("sumPre");
		Integer stationCnt = ((Long) stationResultMap.get("stationCnt")).intValue();
		
		HashMap areaResultMap = (HashMap) areaResultList.get(0);
		Double areaSum = (Double) areaResultMap.get("sumPre");
		Integer areaCnt = ((Long) areaResultMap.get("stationCnt")).intValue();
		Double singleSum = stationSum - areaSum;
		Integer singleCnt = stationCnt - areaCnt;
		// 3.1 计算极差标准单点总量，极差标准单点站数总量
		double tempIndex1 = (singleSum - rainStormStationParam.getMinStationPreTotal()) / (rainStormStationParam.getMaxStationPreTotal() - rainStormStationParam.getMinStationPreTotal());
		double tempIndex2 = (singleCnt - rainStormStationParam.getMinStationCntTotal()) / (rainStormStationParam.getMaxStationCntTotal() - rainStormStationParam.getMinStationCntTotal());
		double index = CommonTool.roundDouble2(tempIndex1 * 0.5 + tempIndex2 * 0.5);
		rainStormStationResult.setPreTotal(CommonTool.roundDouble2(singleSum));
		rainStormStationResult.setStationCnt(singleCnt);
		rainStormStationResult.setStrength(index);
		// 计算级别
		if(index >= rainStormStationParam.getLevel1() && index < rainStormStationParam.getLevel2()) {
			rainStormStationResult.setLevel("轻度");
		} else if(index >= rainStormStationParam.getLevel2() && index < rainStormStationParam.getLevel3()) {
			rainStormStationResult.setLevel("中度");
		} else if(index >= rainStormStationParam.getLevel3() && index < rainStormStationParam.getLevel4()) {
			rainStormStationResult.setLevel("重度");
		} else if(index >= rainStormStationParam.getLevel4()) {
			rainStormStationResult.setLevel("特重");
		}
		return rainStormStationResult;
	}
}
