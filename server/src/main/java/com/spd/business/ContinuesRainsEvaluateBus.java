package com.spd.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.TimesParam;
import com.spd.common.evaluate.ContinueRainAreaParam;
import com.spd.common.evaluate.ContinueRainAreaResult;
import com.spd.common.evaluate.ContinueRainStationParam;
import com.spd.common.evaluate.ContinueRainStationResult;
import com.spd.common.evaluate.ContinueRainYearParam;
import com.spd.common.evaluate.ContinueRainYearResult;
import com.spd.service.IDisasterEvaluate;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 连阴雨评估
 * @author Administrator
 *
 */
public class ContinuesRainsEvaluateBus {

	private IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");

	private static double ZONGHEINDEX1 = 0.6;

	private static double ZONGHEINDEX2 = 0.85;
	
	private static double ZONGHEINDEX3 = 0.95;
	
	/**
	 * 单站连阴雨历年同时段查询
	 * @param continueRainStationParam
	 * @return
	 */
	public List<ContinueRainStationResult> continueRainStatiionByYears(ContinueRainStationParam continueRainStationParam) {
		TimesParam timesParam = continueRainStationParam.getTimesParam();
		List<ContinueRainStationResult> continueRainStationResultList = new ArrayList<ContinueRainStationResult>();
		HashMap paramMap = new HashMap();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		Integer startInt = Integer.parseInt(startTimeStr.substring(4).replaceAll("-", ""));
		Integer endInt = Integer.parseInt(endTimeStr.substring(4).replaceAll("-", ""));
		paramMap.put("startTime", startInt);
		paramMap.put("endTime", endInt);
		List resultList = disasterEvaluate.continueRainStatiionByYears(paramMap);
		if(resultList == null || resultList.size() == 0) return null;
		for(int i = 0; i < resultList.size(); i++) {
			ContinueRainStationResult continueRainStationResult = new ContinueRainStationResult();
			HashMap dataMap = (HashMap) resultList.get(i);
			String resultStartTime = (String) dataMap.get("StartTime");
			continueRainStationResult.setStartTime(resultStartTime);
			String resultEndTime = (String) dataMap.get("EndTime");
			continueRainStationResult.setEndTime(resultEndTime);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			continueRainStationResult.setStation_Id_C(station_Id_C);
			continueRainStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			int noSunDays = (Integer) dataMap.get("NoSunDays");
			continueRainStationResult.setPersistDays(noSunDays);
			int rainDays = (Integer) dataMap.get("RainDays");
			continueRainStationResult.setRainDays(rainDays);
			Double pre = (Double) dataMap.get("Pre");
			continueRainStationResult.setPre(pre);
			Double IA = (noSunDays - continueRainStationParam.getMinSingleDays() + 0.0) / (continueRainStationParam.getMaxSingleDays() - continueRainStationParam.getMinSingleDays());
			Double IB = (rainDays - continueRainStationParam.getMinSingleRainDays() + 0.0) / (continueRainStationParam.getMaxSingleRainDays() - continueRainStationParam.getMinSingleRainDays());
			Double IC = (pre - continueRainStationParam.getMinSinglePre()) / (continueRainStationParam.getMaxSinglePre() - continueRainStationParam.getMinSinglePre());
			Double result1 = CommonTool.roundDouble3((IA + IB + IC) / 3);
			continueRainStationResult.setResult1(result1);
			Double result2 = CommonTool.roundDouble3(IA * continueRainStationParam.getPersistDaysIndex() + IB *  continueRainStationParam.getPreDaysIndex() + IC * continueRainStationParam.getPreIndex());
			continueRainStationResult.setResult2(result2);
			if(result2 >= continueRainStationParam.getStrengthIndex1() && result2 < continueRainStationParam.getStrengthIndex2()) {
				continueRainStationResult.setLevel("轻度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex2() && result2 < continueRainStationParam.getStrengthIndex3()) {
				continueRainStationResult.setLevel("中度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex3() && result2 < continueRainStationParam.getStrengthIndex4()) {
				continueRainStationResult.setLevel("重度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex4()) {
				continueRainStationResult.setLevel("特重");
			}
			continueRainStationResultList.add(continueRainStationResult);
		}
		return continueRainStationResultList;
	}
	
	/**
	 * 单站连阴雨按时间段查询
	 * @param continueRainStationParam
	 * @return
	 */
	public List<ContinueRainStationResult> continueRainStatiionByTimes(ContinueRainStationParam continueRainStationParam) {
		TimesParam timesParam = continueRainStationParam.getTimesParam();
		List<ContinueRainStationResult> continueRainStationResultList = new ArrayList<ContinueRainStationResult>();
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.continueRainStatiionByTimes(paramMap);
		if(resultList == null || resultList.size() == 0) return null;
		for(int i = 0; i < resultList.size(); i++) {
			ContinueRainStationResult continueRainStationResult = new ContinueRainStationResult();
			HashMap dataMap = (HashMap) resultList.get(i);
			String resultStartTime = (String) dataMap.get("StartTime");
			continueRainStationResult.setStartTime(resultStartTime);
			String resultEndTime = (String) dataMap.get("EndTime");
			continueRainStationResult.setEndTime(resultEndTime);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			continueRainStationResult.setStation_Id_C(station_Id_C);
			continueRainStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			int noSunDays = (Integer) dataMap.get("NoSunDays");
			continueRainStationResult.setPersistDays(noSunDays);
			int rainDays = (Integer) dataMap.get("RainDays");
			continueRainStationResult.setRainDays(rainDays);
			Double pre = (Double) dataMap.get("Pre");
			continueRainStationResult.setPre(pre);
			Double IA = (noSunDays - continueRainStationParam.getMinSingleDays() + 0.0) / (continueRainStationParam.getMaxSingleDays() - continueRainStationParam.getMinSingleDays());
			Double IB = (rainDays - continueRainStationParam.getMinSingleRainDays() + 0.0) / (continueRainStationParam.getMaxSingleRainDays() - continueRainStationParam.getMinSingleRainDays());
			Double IC = (pre - continueRainStationParam.getMinSinglePre()) / (continueRainStationParam.getMaxSinglePre() - continueRainStationParam.getMinSinglePre());
			Double result1 = CommonTool.roundDouble3((IA + IB + IC) / 3);
			continueRainStationResult.setResult1(result1);
			Double result2 = CommonTool.roundDouble3(IA * continueRainStationParam.getPersistDaysIndex() + IB *  continueRainStationParam.getPreDaysIndex() + IC * continueRainStationParam.getPreIndex());
			continueRainStationResult.setResult2(result2);
			if(result2 >= continueRainStationParam.getStrengthIndex1() && result2 < continueRainStationParam.getStrengthIndex2()) {
				continueRainStationResult.setLevel("轻度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex2() && result2 < continueRainStationParam.getStrengthIndex3()) {
				continueRainStationResult.setLevel("中度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex3() && result2 < continueRainStationParam.getStrengthIndex4()) {
				continueRainStationResult.setLevel("重度");
			} else if(result2 >= continueRainStationParam.getStrengthIndex4()) {
				continueRainStationResult.setLevel("特重");
			}
			continueRainStationResultList.add(continueRainStationResult);
		}
		return continueRainStationResultList;
	}
	
	/**
	 * 区域连阴雨历年查询
	 * @param continueRainAreaParam
	 */
	public List<ContinueRainAreaResult> continueRainAreaByYears(ContinueRainAreaParam continueRainAreaParam) {
		List<ContinueRainAreaResult> continueRainAreaResultList = new ArrayList<ContinueRainAreaResult>();
		TimesParam timesParam = continueRainAreaParam.getTimesParam();
		HashMap paramMap = new HashMap();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		Integer startInt = Integer.parseInt(startTimeStr.substring(4).replaceAll("-", ""));
		Integer endInt = Integer.parseInt(endTimeStr.substring(4).replaceAll("-", ""));
		paramMap.put("startTime", startInt);
		paramMap.put("endTime", endInt);
		
		List resultList = disasterEvaluate.continueRainAreaByYears(paramMap);
		if(resultList == null || resultList.size() == 0) return continueRainAreaResultList;
		for(int i = 0; i < resultList.size(); i++) {
			ContinueRainAreaResult continueRainAreaResult = new ContinueRainAreaResult();
			HashMap dataMap = (HashMap) resultList.get(i);
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			int processDays = (Integer) dataMap.get("ProcessDays"); // 过程累积时间 
			int sumStations = (Integer) dataMap.get("SumStations"); // 累积站点数
			int preDays = (Integer) dataMap.get("PreDays"); //累积有雨日数
			double sumPre = (Double) dataMap.get("SumPre"); //累积雨量
			Double NB1 = (processDays - continueRainAreaParam.getMinPersistDays() + 0.0) / (continueRainAreaParam.getMaxPersistDays() - continueRainAreaParam.getMinPersistDays());
			Double NB2 = (sumStations - continueRainAreaParam.getMinSumStations() + 0.0) / (continueRainAreaParam.getMaxSumStations() - continueRainAreaParam.getMinSumStations());
			Double NB3 = (preDays - continueRainAreaParam.getMinRainDays() - 0.0) / (continueRainAreaParam.getMaxRainDays() - continueRainAreaParam.getMinRainDays());
			Double NB4 = (sumPre - continueRainAreaParam.getMinSumPres()) / (continueRainAreaParam.getMaxSumPres() - continueRainAreaParam.getMinSumPres());
			Double result1 = CommonTool.roundDouble3((NB1 + NB2 + NB3 + NB4) / 4.0);
			Double result2 = CommonTool.roundDouble3(NB1 * continueRainAreaParam.getStrengthIndex1() + NB2 * continueRainAreaParam.getStrengthIndex2()
							+ NB3 * continueRainAreaParam.getStrengthIndex3() + NB4 * continueRainAreaParam.getStrengthIndex4());
			continueRainAreaResult.setStartTime(startTime);
			continueRainAreaResult.setEndTime(endTime);
			continueRainAreaResult.setPersistDays(processDays);
			continueRainAreaResult.setPre(sumPre);
			continueRainAreaResult.setPreDays(preDays);
			continueRainAreaResult.setResult1(result1);
			continueRainAreaResult.setResult2(result2);
			continueRainAreaResult.setStationCnt(sumStations);
			//计算级别
			if(result2 >= continueRainAreaParam.getStrengthIndex1() && result2 < continueRainAreaParam.getStrengthIndex2()) {
				continueRainAreaResult.setLevel("轻度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex2() && result2 < continueRainAreaParam.getStrengthIndex3()) {
				continueRainAreaResult.setLevel("中度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex3() && result2 < continueRainAreaParam.getStrengthIndex4()) {
				continueRainAreaResult.setLevel("重度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex4()) {
				continueRainAreaResult.setLevel("特重");
			}
			continueRainAreaResultList.add(continueRainAreaResult);
		}
		return continueRainAreaResultList;
	}
	
	/**
	 * 区域连阴雨按时间段查询
	 * @param continueRainAreaParam
	 */
	public List<ContinueRainAreaResult> continueRainAreaByTimes(ContinueRainAreaParam continueRainAreaParam) {
		List<ContinueRainAreaResult> continueRainAreaResultList = new ArrayList<ContinueRainAreaResult>();
		TimesParam timesParam = continueRainAreaParam.getTimesParam();
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.continueRainAreaByTimes(paramMap);
		if(resultList == null || resultList.size() == 0) return continueRainAreaResultList;
		for(int i = 0; i < resultList.size(); i++) {
			ContinueRainAreaResult continueRainAreaResult = new ContinueRainAreaResult();
			HashMap dataMap = (HashMap) resultList.get(i);
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			int processDays = (Integer) dataMap.get("ProcessDays"); // 过程累积时间 
			int sumStations = (Integer) dataMap.get("SumStations"); // 累积站点数
			int preDays = (Integer) dataMap.get("PreDays"); //累积有雨日数
			double sumPre = (Double) dataMap.get("SumPre"); //累积雨量
			//NB1:过程持续天数指数，NB2:累积站点指数，NB3：集成有雨日数指数，NB4：白天雨量
			Double NB1 = (processDays - continueRainAreaParam.getMinPersistDays() + 0.0) / (continueRainAreaParam.getMaxPersistDays() - continueRainAreaParam.getMinPersistDays());
			Double NB2 = (sumStations - continueRainAreaParam.getMinSumStations() + 0.0) / (continueRainAreaParam.getMaxSumStations() - continueRainAreaParam.getMinSumStations());
			Double NB3 = (preDays - continueRainAreaParam.getMinRainDays() - 0.0) / (continueRainAreaParam.getMaxRainDays() - continueRainAreaParam.getMinRainDays());
			Double NB4 = (sumPre - continueRainAreaParam.getMinSumPres()) / (continueRainAreaParam.getMaxSumPres() - continueRainAreaParam.getMinSumPres());
			Double result1 = CommonTool.roundDouble3((NB1 + NB2 + NB3 + NB4) / 4.0);
			Double result2 = CommonTool.roundDouble3(NB1 * continueRainAreaParam.getIndex1() + NB2 * continueRainAreaParam.getIndex2()
							+ NB3 * continueRainAreaParam.getIndex3() + NB4 * continueRainAreaParam.getIndex4());
			continueRainAreaResult.setStartTime(startTime);
			continueRainAreaResult.setEndTime(endTime);
			continueRainAreaResult.setPersistDays(processDays);
			continueRainAreaResult.setPre(sumPre);
			continueRainAreaResult.setPreDays(preDays);
			continueRainAreaResult.setResult1(result1);
			continueRainAreaResult.setResult2(result2);
			continueRainAreaResult.setStationCnt(sumStations);
			//计算级别
			if(result2 >= continueRainAreaParam.getStrengthIndex1() && result2 < continueRainAreaParam.getStrengthIndex2()) {
				continueRainAreaResult.setLevel("轻度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex2() && result2 < continueRainAreaParam.getStrengthIndex3()) {
				continueRainAreaResult.setLevel("中度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex3() && result2 < continueRainAreaParam.getStrengthIndex4()) {
				continueRainAreaResult.setLevel("重度");
			} else if(result2 >= continueRainAreaParam.getStrengthIndex4()) {
				continueRainAreaResult.setLevel("特重");
			}
			continueRainAreaResultList.add(continueRainAreaResult);
		}
		return continueRainAreaResultList;
	}
	
	public List<ContinueRainYearResult> continueRainByYear(ContinueRainYearParam continueRainYearParam, ContinueRainStationParam continueRainStationParam, 
			ContinueRainAreaParam continueRainAreaParam) {
		List<ContinueRainYearResult> continueRainYearResultList = new ArrayList<ContinueRainYearResult>();
		List<ContinueRainStationResult> continueRainStationResultList = continueRainStatiionByYears(continueRainStationParam); //单站
		List<ContinueRainAreaResult> continueRainAreaResultList = continueRainAreaByYears(continueRainAreaParam); //区域
		//计算年度，简短处理，年份算在开始年终
		Map<Integer, Double> yearStationMap = new HashMap<Integer, Double>();
		Map<Integer, Double> yearAreaMap = new HashMap<Integer, Double>();
		for(int i = 0; i < continueRainStationResultList.size(); i++) {
			ContinueRainStationResult continueRainStationResult = continueRainStationResultList.get(i);
			Double result = continueRainStationResult.getResult2();
			String startTime = continueRainStationResult.getStartTime();
			int year = Integer.parseInt(startTime.substring(0, 4));
			//过滤掉一部分年
			if((year > continueRainStationParam.getEndYear() && year > continueRainStationParam.getPerennialEndYear())
					|| (year < continueRainStationParam.getStartYear() && year < continueRainStationParam.getPerennialStartYear())) {
				continue;
			}
			Double preSum = yearStationMap.get(year);
			if(preSum == null) {
				yearStationMap.put(year, result);
			} else {
				yearStationMap.put(year, result + preSum);
			}
		}
		//常年发生次数
		Double yearsCnt = 0.0;
		HashMap<Integer, Integer> yearsCntMap = new HashMap<Integer, Integer>(); //历年每年发生次数
		for(int i = 0; i < continueRainAreaResultList.size(); i++) {
			ContinueRainAreaResult continueRainAreaResult = continueRainAreaResultList.get(i);
			Double result = continueRainAreaResult.getResult2();
			String yearStr = continueRainAreaResult.getStartTime().substring(0, 4);
			int year = Integer.parseInt(yearStr);
			//过滤掉一部分年
			if((year > continueRainStationParam.getEndYear() && year > continueRainStationParam.getPerennialEndYear())
					|| (year < continueRainStationParam.getStartYear() && year < continueRainStationParam.getPerennialStartYear())) {
				continue;
			}
			Integer cnt = yearsCntMap.get(year);
			if(cnt == null) {
				yearsCntMap.put(year, 1);
			} else {
				yearsCntMap.put(year, cnt + 1);
			}
			Double preSum = yearAreaMap.get(year);
			if(preSum == null) {
				yearAreaMap.put(year, result);
			} else {
				yearAreaMap.put(year, result + preSum);
			}
			if(year >= continueRainStationParam.getPerennialStartYear() && year <= continueRainStationParam.getPerennialEndYear()) {
				yearsCnt++;
			}
		}
		yearsCnt /= (continueRainStationParam.getPerennialEndYear() - continueRainStationParam.getPerennialStartYear() + 1);
		yearsCnt = CommonTool.roundDouble(yearsCnt);
		//重新计算综合指数，按照百分位来计算，百分位分别是60%，85%,95%
		List<Double> indexList = new ArrayList<Double>();
		Iterator<Integer> it = yearStationMap.keySet().iterator();
		while(it.hasNext()) {
			ContinueRainYearResult continueRainYearResult = new ContinueRainYearResult();
			int year = it.next();
			Double yearStationPre = yearStationMap.get(year);
			Double yearAreaPre = yearAreaMap.get(year);
			if(yearAreaPre == null) continue;
			continueRainYearResult.setStationStrength(CommonTool.roundDouble3(yearStationPre));
			continueRainYearResult.setAreaStrength(CommonTool.roundDouble3(yearAreaPre));
			Double IA = (yearStationPre - continueRainYearParam.getMinStationStrength()) / (continueRainYearParam.getMaxStationStrength() - continueRainYearParam.getMinStationStrength());
			Double IB = (yearAreaPre - continueRainYearParam.getMinAreaStrength()) / (continueRainYearParam.getMaxAreaStrength() - continueRainYearParam.getMinAreaStrength());
			continueRainYearResult.setYear(year);
			//综合指数
			Double result = CommonTool.roundDouble3(IA * 0.5 + IB * 0.5);
			continueRainYearResult.setResult(result);
			indexList.add(result);
//			if(result >= continueRainYearParam.getStrengthIndex1() && result < continueRainYearParam.getStrengthIndex2()) {
//				continueRainYearResult.setLevel("轻度");
//			} else if(result >= continueRainYearParam.getStrengthIndex2() && result < continueRainYearParam.getStrengthIndex3()) {
//				continueRainYearResult.setLevel("中度");
//			} else if(result >= continueRainYearParam.getStrengthIndex3() && result < continueRainYearParam.getStrengthIndex4()) {
//				continueRainYearResult.setLevel("重度");
//			} else if(result >= continueRainYearParam.getStrengthIndex4()) {
//				continueRainYearResult.setLevel("特重");
//			} 
			continueRainYearResult.setYearsCnt(yearsCnt); // 常年发生次数
			continueRainYearResult.setTimes(yearsCntMap.get(year)); // 当年发生次数
			continueRainYearResultList.add(continueRainYearResult);
		}
		Collections.sort(indexList);
		Double index1 = indexList.get(0);
		Double index2 = indexList.get((int)(indexList.size() * ZONGHEINDEX1));
		Double index3 = indexList.get((int)(indexList.size() * ZONGHEINDEX2));
		Double index4 = indexList.get((int)(indexList.size() * ZONGHEINDEX3));
		//计算常年等权集成
		Double sumResult = 0.0;
		for(int i = 0; i < continueRainYearResultList.size(); i++) {
			ContinueRainYearResult continueRainYearResult = continueRainYearResultList.get(i);
			Double result = continueRainYearResult.getResult();
			if(result >= index1 && result < index2) {
				continueRainYearResult.setLevel("轻度");
			} else if(result >= index2 && result < index3) {
				continueRainYearResult.setLevel("中度");
			} else if(result >= index3 && result < index4) {
				continueRainYearResult.setLevel("重度");
			} else if(result >= index4) {
				continueRainYearResult.setLevel("特重");
			}
			int year = continueRainYearResult.getYear();
			if(year >= continueRainStationParam.getPerennialStartYear() && year <= continueRainStationParam.getPerennialEndYear()) {
				sumResult += result;
			}
		}
		sumResult /= (continueRainStationParam.getPerennialEndYear() - continueRainStationParam.getPerennialStartYear() + 1);
		sumResult = CommonTool.roundDouble3(sumResult);
		for(int i = 0; i < continueRainYearResultList.size(); i++) {
			ContinueRainYearResult continueRainYearResult = continueRainYearResultList.get(i);
			continueRainYearResult.setYearsResult(sumResult);
		}
		Collections.sort(continueRainYearResultList);
		return continueRainYearResultList;
	}
}
