package com.spd.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.TimesParam;
import com.spd.common.evaluate.LowTmpAreaParam;
import com.spd.common.evaluate.LowTmpAreaResult;
import com.spd.common.evaluate.LowTmpStationParam;
import com.spd.common.evaluate.LowTmpStationResult;
import com.spd.common.evaluate.LowTmpYearParam;
import com.spd.common.evaluate.LowTmpYearResult;
import com.spd.service.IDisasterEvaluate;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 低温灾害统计
 * @author Administrator
 *
 */
public class LowTmpEvaluateBus {

	private static int AREAPERSISTDAYS = 8;//区域过程最小的持续天数
	
	private static int STARTYEAR = 1951;//1951年有数据以来。
	//判断等级的百分位
	private static double LEVEL1PERCENT = 0.6;
	
	private static double LEVEL2PERCENT = 0.85;
	
	private static double LEVEL3PERCENT = 0.95;
	
	private IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
	
	/**
	 * 单站低温统计
	 * @param lowTmpStationParam
	 * @return
	 */
	public List<LowTmpStationResult> lowTmpStationByTimes(LowTmpStationParam lowTmpStationParam) {
		List<LowTmpStationResult> lowTmpStationResultList = new ArrayList<LowTmpStationResult>();
		TimesParam timesParam = lowTmpStationParam.getTimesParam();
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.lowTmpStationByTimes(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				LowTmpStationResult lowTmpStationResult = new LowTmpStationResult();
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				lowTmpStationResult.setStation_Id_C(station_Id_C);
				lowTmpStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				String startTime = (String) dataMap.get("StartTime");
				lowTmpStationResult.setStartTime(startTime);
				String endTime = (String) dataMap.get("EndTime");
				lowTmpStationResult.setEndTime(endTime);
				int persistDays = ((Long) dataMap.get("PersistDays")).intValue();
				lowTmpStationResult.setPersistDays(persistDays);
				int persistHous = (Integer) dataMap.get("PersistHous");
				lowTmpStationResult.setPersistHous(persistHous);
				Double avgTmp = (Double) dataMap.get("AvgTmp");
				lowTmpStationResult.setAvgTmp(avgTmp);
				Double anomaly = (Double) dataMap.get("Anomaly");
				lowTmpStationResult.setAnomaly(anomaly);
				if(persistHous == 2) {
					lowTmpStationResult.setLevel("一般");
				} else if(persistHous > 2) {
					lowTmpStationResult.setLevel("重度");
				}
				lowTmpStationResultList.add(lowTmpStationResult);
			}
		}
		return lowTmpStationResultList;
	}
	
	/**
	 * 低温区域统计
	 * @param lowTmpAreaParam
	 * @return
	 */
	public List<LowTmpAreaResult> lowTmpAreaByTimes(LowTmpAreaParam lowTmpAreaParam) {
		List<LowTmpAreaResult> lowTmpAreaResultList = new ArrayList<LowTmpAreaResult>();
		HashMap paramMap = new HashMap();
		TimesParam timesParam = lowTmpAreaParam.getTimesParam();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.lowTmpAreaByTimes(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				LowTmpAreaResult lowTmpAreaResult = new LowTmpAreaResult();
				HashMap dataMap = (HashMap) resultList.get(i);
				String startTime = (String) dataMap.get("StartTime");
				lowTmpAreaResult.setStartTime(startTime);
				String endTime = (String) dataMap.get("EndTime");
				lowTmpAreaResult.setEndTime(endTime);
				int persistDays = ((Long) dataMap.get("PersistDays")).intValue();
				if(persistDays < AREAPERSISTDAYS) {
					continue;
				}
				lowTmpAreaResult.setPersistDays(persistDays);
				int sumStations = (Integer) dataMap.get("SumStations");
				lowTmpAreaResult.setStationCnts(sumStations);
				double sumAnomaly = (Double) dataMap.get("SumAnomaly");
				lowTmpAreaResult.setSumAnomaly(CommonTool.roundDouble2(sumAnomaly));
				Double IA = (persistDays - lowTmpAreaParam.getMinPersistDays() + 0.0) / (lowTmpAreaParam.getMaxPersistDays() - lowTmpAreaParam.getMinPersistDays()); 
				Double IC = (sumStations - lowTmpAreaParam.getMinSumStation() + 0.0) / (lowTmpAreaParam.getMaxSumStation() - lowTmpAreaParam.getMinSumStation());
				Double ID = (Math.abs(sumAnomaly) - lowTmpAreaParam.getMinSumAnomaly()) / (lowTmpAreaParam.getMaxSumAnomaly() - lowTmpAreaParam.getMinSumAnomaly());
				//等权集成
				Double result1 = CommonTool.roundDouble2((IA + IC + ID) / 3);
				//不等权集成
				Double result2 = CommonTool.roundDouble2(IA * lowTmpAreaParam.getPersistDayWeight() + IC * lowTmpAreaParam.getSumStationWeight() + 
						ID * lowTmpAreaParam.getSumStationWeight());
				lowTmpAreaResult.setResult1(result1);
				lowTmpAreaResult.setResult2(result2);
				if(result2 > lowTmpAreaParam.getLevel1() && result2 < lowTmpAreaParam.getLevel2()) {
					lowTmpAreaResult.setLevel("轻度");
				} else if(result2 >=lowTmpAreaParam.getLevel2() && result2 < lowTmpAreaParam.getLevel3()) {
					lowTmpAreaResult.setLevel("中度");
				} else if(result2 >= lowTmpAreaParam.getLevel3() && result2 < lowTmpAreaParam.getLevel4()) {
					lowTmpAreaResult.setLevel("重度");
				} else if (result2 >= lowTmpAreaParam.getLevel4()) {
					lowTmpAreaResult.setLevel("特重");
				}
				lowTmpAreaResultList.add(lowTmpAreaResult);
			}
		}
		return lowTmpAreaResultList;
	}

	/**
	 * 低温年度统计
	 * @param lowTmpYearParam
	 * @return
	 */
	public List<LowTmpYearResult> lowTmpByYear(LowTmpYearParam lowTmpYearParam) {
		//指标重新计算。
		//1. 计算从历史有数据以来
		//2. 计算等级
		//3. 排除不在时间段范围内的数据
		List<LowTmpYearResult> lowTmpYearResultList = new ArrayList<LowTmpYearResult>();
		TimesParam timesParam = lowTmpYearParam.getTimesParam();
		int startYear = lowTmpYearParam.getStartYear();
		int endYear = lowTmpYearParam.getEndYear();
		String startTime = timesParam.getStartTimeStr();
		String endTime = timesParam.getEndTimeStr();
		int startMon = Integer.parseInt(startTime.split("-")[1]);
		int startDay = Integer.parseInt(startTime.split("-")[2]);
		int endMon = Integer.parseInt(endTime.split("-")[1]);
		int endDay = Integer.parseInt(endTime.split("-")[2]);
		//历年的最大、最小时间
		String yearStartTime = STARTYEAR + startTime.substring(4);
		SimpleDateFormat yearSDF = new SimpleDateFormat("yyyy");
		int currentYear = Integer.parseInt(yearSDF.format(new Date()));
		String yearEndTime = currentYear + endTime.substring(4);
		//查询全部结果
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", yearStartTime);
		paramMap.put("endTime", yearEndTime);
		List resultList = disasterEvaluate.lowTmpAreaByTimes(paramMap);
		//遍历结果
//		List<LowTmpStationResult> lowTmpStationResultList = new ArrayList<LowTmpStationResult>();
		HashMap<Integer, List<LowTmpAreaResult>> resultMap = new HashMap<Integer, List<LowTmpAreaResult>>();
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				LowTmpAreaResult lowTmpAreaResult = new LowTmpAreaResult();
				HashMap dataMap = (HashMap) resultList.get(i);
				String itemStartTime = (String) dataMap.get("StartTime");
				String itemEndTime = (String) dataMap.get("EndTime");
				//判断是否在指定时间段内
				int itemStartMon = Integer.parseInt(itemStartTime.split("-")[1]);
				int itemStartDay = Integer.parseInt(itemStartTime.split("-")[2]);
				boolean flag = CommonTool.isCurTimeInRanges(itemStartMon, itemStartDay, startMon, startDay, endMon, endDay);
				if(!flag) {
					continue;
				}
				lowTmpAreaResult.setStartTime(itemStartTime);
				lowTmpAreaResult.setEndTime(itemStartTime);
				int persistDays = ((Long) dataMap.get("PersistDays")).intValue();
				lowTmpAreaResult.setPersistDays(persistDays);
				int sumStations = (Integer) dataMap.get("SumStations");
				lowTmpAreaResult.setStationCnts(sumStations);
				double sumAnomaly = (Double) dataMap.get("SumAnomaly");
				lowTmpAreaResult.setSumAnomaly(CommonTool.roundDouble2(sumAnomaly));
				Double IA = (persistDays - lowTmpYearParam.getMinPersistDays() + 0.0) / (lowTmpYearParam.getMaxPersistDays() - lowTmpYearParam.getMinPersistDays()); 
				Double IC = (sumStations - lowTmpYearParam.getMinSumStation() + 0.0) / (lowTmpYearParam.getMaxSumStation() - lowTmpYearParam.getMinSumStation());
				Double ID = (Math.abs(sumAnomaly) - lowTmpYearParam.getMinSumAnomaly()) / (lowTmpYearParam.getMaxSumAnomaly() - lowTmpYearParam.getMinSumAnomaly());
				//等权集成
				Double result1 = CommonTool.roundDouble2((IA + IC + ID) / 3);
				//不等权集成
				Double result2 = CommonTool.roundDouble2(IA * lowTmpYearParam.getPersistDayWeight() + IC * lowTmpYearParam.getSumStationWeight() + 
						ID * lowTmpYearParam.getSumStationWeight());
				lowTmpAreaResult.setResult1(result1);
				lowTmpAreaResult.setResult2(result2);
								
				int year = Integer.parseInt(itemStartTime.split("-")[0]);
				List<LowTmpAreaResult> itemLowTmpStationResultList = resultMap.get(year);
				if(itemLowTmpStationResultList == null) {
					itemLowTmpStationResultList = new ArrayList<LowTmpAreaResult>();
				}
				itemLowTmpStationResultList.add(lowTmpAreaResult);
				resultMap.put(year, itemLowTmpStationResultList);
			}
		} else {
			return null;
		}
		Iterator it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			Integer year = (Integer) it.next();
			List<LowTmpAreaResult> list = resultMap.get(year);
			LowTmpYearResult lowTmpYearResult = new LowTmpYearResult();
			Double result1 = 0.0, result2 = 0.0;
			for(int i = 0; i < list.size(); i++) {
				LowTmpAreaResult item = list.get(i);
				Double itemResult1 = item.getResult1();
				Double itemResult2 = item.getResult2();
				result1 += itemResult1;
				result2 += itemResult2;
			}
			lowTmpYearResult.setYear(year);
			lowTmpYearResult.setCnt(list.size());
//			lowTmpYearResult.setResult1(CommonTool.roundDouble2(result1));
			lowTmpYearResult.setResult(CommonTool.roundDouble2(result2));
			lowTmpYearResultList.add(lowTmpYearResult);
		}
		//设置等级
		sortLevel(lowTmpYearResultList);
		//计算常年值
		setYearsResult(lowTmpYearResultList, lowTmpYearParam.getStandardStartYear(), lowTmpYearParam.getStandardEndYear());
		//排除不在时间段范围内的
		deleteOutTimes(lowTmpYearResultList, startYear, endYear);
		Collections.sort(lowTmpYearResultList, new LowTmpYearResult());
		return lowTmpYearResultList;
	}
	
	private void setYearsResult(List<LowTmpYearResult> lowTmpYearResultList, int standardStartYear, int standardEndYear) {
		double sumCnt = 0;
		double yearsResultSum = 0.0;
		for(int i = 0; i < lowTmpYearResultList.size(); i++) {
			LowTmpYearResult lowTmpYearResult = lowTmpYearResultList.get(i);
			int year = lowTmpYearResult.getYear();
			if(year >= standardStartYear && year <= standardEndYear) {
				int cnt = lowTmpYearResult.getCnt();
				sumCnt += cnt;
				yearsResultSum += lowTmpYearResult.getResult();
			}
		}
		//设置常年值
		for(int i = 0; i < lowTmpYearResultList.size(); i++) {
			LowTmpYearResult lowTmpYearResult = lowTmpYearResultList.get(i);
			lowTmpYearResult.setYearsCnt(CommonTool.roundDouble(sumCnt / (standardEndYear - standardStartYear + 1)));
			lowTmpYearResult.setYearsResult(CommonTool.roundDouble2(yearsResultSum / (standardEndYear - standardStartYear + 1)));
		}
	}
	
	/**
	 * 排除不在时间段范围内的
	 * @param lowTmpYearResultList
	 * @param timesParam
	 */
	private void deleteOutTimes(List<LowTmpYearResult> lowTmpYearResultList, int startYear, int endYear) {
		for(int i = lowTmpYearResultList.size() - 1; i >= 0; i--) {
			LowTmpYearResult lowTmpYearResult = lowTmpYearResultList.get(i);
			int year = lowTmpYearResult.getYear();
			if(year < startYear || year > endYear) {
				lowTmpYearResultList.remove(i);
			}
		}
	}
	
	private void sortLevel(List<LowTmpYearResult> lowTmpYearResultList) {
		//判断等级
		double[] levelArray = new double[lowTmpYearResultList.size()];
		for(int i = 0; i < lowTmpYearResultList.size(); i++) {
			levelArray[i] = lowTmpYearResultList.get(i).getResult();
		}
		//levelArray排序
		Arrays.sort(levelArray);
		double level1Value = levelArray[(int)(levelArray.length * LEVEL1PERCENT)];
		double level2Value = levelArray[(int)(levelArray.length * LEVEL2PERCENT)];
		double level3Value = levelArray[(int)(levelArray.length * LEVEL3PERCENT)];
		for(int i = 0; i < lowTmpYearResultList.size(); i++) {
			LowTmpYearResult lowTmpYearResult = lowTmpYearResultList.get(i);
			Double result2 = lowTmpYearResult.getResult();
			if(result2 < level1Value) {
				lowTmpYearResult.setLevel("轻度");
			} else if(result2 >= level1Value && result2 < level2Value) {
				lowTmpYearResult.setLevel("中度");
			} else if(result2 >= level2Value && result2 < level3Value) {
				lowTmpYearResult.setLevel("重度");
			} else if(result2 >= level3Value) {
				lowTmpYearResult.setLevel("严重");
			}
		}
	}
}
