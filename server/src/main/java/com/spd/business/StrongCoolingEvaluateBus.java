package com.spd.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.TimesParam;
import com.spd.common.evaluate.StrongCoolingAreaParam;
import com.spd.common.evaluate.StrongCoolingAreaResult;
import com.spd.common.evaluate.StrongCoolingStationParam;
import com.spd.common.evaluate.StrongCoolingStationResult;
import com.spd.common.evaluate.StrongCoolingYearResult;
import com.spd.service.IDisasterEvaluate;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 强降温评估
 * @author Administrator
 *
 */
public class StrongCoolingEvaluateBus {

	private IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
	
	private static int D = 10;//历史上单站降温最长持续时间
	
	private static double TMIN = 6;//单站历史上最低强降温值

	private static double TMAX = 19.4;//单站历史上最低强降温值
	
	public List<StrongCoolingStationResult> strongCoolingStationByYears(StrongCoolingAreaParam strongCoolingStationParam) {
		List<StrongCoolingStationResult> resultList = new ArrayList<StrongCoolingStationResult>();
		//查询数据
		HashMap paramMap = new HashMap();
		TimesParam timesParam = strongCoolingStationParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		Integer startTimeInt = Integer.parseInt(startTimeStr.substring(4).replaceAll("-", ""));
		Integer endTimeInt = Integer.parseInt(endTimeStr.substring(4).replaceAll("-", ""));
		paramMap.put("startTime", startTimeInt);
		paramMap.put("endTime", endTimeInt);
		List<LinkedHashMap> strongCoolingStationResultList = disasterEvaluate.strongCoolingStationByYears(paramMap);
		for(int i = 0; i < strongCoolingStationResultList.size(); i++) {
			LinkedHashMap itemMap = strongCoolingStationResultList.get(i);
			StrongCoolingStationResult strongCoolingStationResult = new StrongCoolingStationResult();
			double coolTmp = (Double) itemMap.get("CoolTmp");
			strongCoolingStationResult.setCoolingTmps(coolTmp);
			strongCoolingStationResult.setCoolingTmps72Hours((Double) itemMap.get("Cool72HTmp"));
			String startTime = (String) itemMap.get("StartTime");
			int year = Integer.parseInt(startTime.substring(0, 4));
			if((year > strongCoolingStationParam.getEndYear() && year > strongCoolingStationParam.getPerennialEndYear())
					|| (year < strongCoolingStationParam.getStartYear() && year < strongCoolingStationParam.getPerennialStartYear())) {
				continue;
			}
			strongCoolingStationResult.setStartTime(startTime);
			strongCoolingStationResult.setEndTime((String) itemMap.get("EndTime"));
			strongCoolingStationResult.setLevel((String) itemMap.get("level"));
			int persistDays = ((Long) itemMap.get("PersistDays")).intValue();
			strongCoolingStationResult.setPersistDays(persistDays);
			double IM = (persistDays - 1.0) / (D - 1);
			double IN = (coolTmp - TMIN) / (TMAX - TMIN);
			double CI = IM * 0.5 + IN * 0.5; 
			strongCoolingStationResult.setCI(CI);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			strongCoolingStationResult.setStation_Id_C(station_Id_C);
			strongCoolingStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			resultList.add(strongCoolingStationResult);
		}
		return resultList;
	}
	
	public List<StrongCoolingStationResult> strongCoolingStationByTimes(StrongCoolingStationParam strongCoolingStationParam) {
		List<StrongCoolingStationResult> resultList = new ArrayList<StrongCoolingStationResult>();
		//查询数据
		HashMap paramMap = new HashMap();
		TimesParam timesParam = strongCoolingStationParam.getTimesParam();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> strongCoolingStationResultList = disasterEvaluate.strongCoolingStationByTimes(paramMap);
		for(int i = 0; i < strongCoolingStationResultList.size(); i++) {
			LinkedHashMap itemMap = strongCoolingStationResultList.get(i);
			StrongCoolingStationResult strongCoolingStationResult = new StrongCoolingStationResult();
			double coolTmp = (Double) itemMap.get("CoolTmp");
			strongCoolingStationResult.setCoolingTmps(coolTmp);
			strongCoolingStationResult.setCoolingTmps72Hours((Double) itemMap.get("Cool72HTmp"));
			strongCoolingStationResult.setStartTime((String) itemMap.get("StartTime"));
			strongCoolingStationResult.setEndTime((String) itemMap.get("EndTime"));
			strongCoolingStationResult.setLevel((String) itemMap.get("level"));
			int persistDays = ((Long) itemMap.get("PersistDays")).intValue();
			strongCoolingStationResult.setPersistDays(persistDays);
			double IM = (persistDays - 1.0) / (D - 1);
			double IN = (coolTmp - TMIN) / (TMAX - TMIN);
			double CI = IM * 0.5 + IN * 0.5; 
			strongCoolingStationResult.setCI(CI);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			strongCoolingStationResult.setStation_Id_C(station_Id_C);
			strongCoolingStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			resultList.add(strongCoolingStationResult);
		}
		return resultList;
	}
	
	public List<StrongCoolingAreaResult> strongCoolingAreaByYears(StrongCoolingAreaParam strongCoolingAreaParam) {
		List<StrongCoolingAreaResult> strongCoolingAreaResultList = new ArrayList<StrongCoolingAreaResult>();
		HashMap paramMap = new HashMap();
		TimesParam timesParam = strongCoolingAreaParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		Integer startTimeInt = Integer.parseInt(startTimeStr.substring(4).replaceAll("-", ""));
		Integer endTimeInt = Integer.parseInt(endTimeStr.substring(4).replaceAll("-", ""));
		paramMap.put("startTime", startTimeInt);
		paramMap.put("endTime", endTimeInt);
		List<LinkedHashMap> resultList = disasterEvaluate.strongCoolingAreaByYears(paramMap);
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			StrongCoolingAreaResult strongCoolingAreaResult = new StrongCoolingAreaResult();
			String startTime = (String) itemMap.get("StartTime");
			strongCoolingAreaResult.setStartTime(startTime);
			strongCoolingAreaResult.setEndTime((String) itemMap.get("EndTime"));
			int year = Integer.parseInt(startTime.substring(0, 4));
			if((year > strongCoolingAreaParam.getEndYear() && year > strongCoolingAreaParam.getPerennialEndYear())
					|| (year < strongCoolingAreaParam.getStartYear() && year < strongCoolingAreaParam.getPerennialStartYear())) {
				continue;
			}
			int persistDays = ((Long) itemMap.get("PersistDays")).intValue();
			strongCoolingAreaResult.setPersistDays(persistDays);
			int stationCnt = (Integer) itemMap.get("StationCnt");
			strongCoolingAreaResult.setStationCnt(stationCnt);
			Double maxTmp = (Double) itemMap.get("MaxTmp");
			strongCoolingAreaResult.setMaxTmp(maxTmp);
			Double minTmp = (Double) itemMap.get("MinTmp");
			strongCoolingAreaResult.setMinTmp(minTmp);
			Double avgTmp = (Double) itemMap.get("AvgTmp");
			strongCoolingAreaResult.setAvgTmp(avgTmp);
			Double IA = (stationCnt - strongCoolingAreaParam.getMinStations() + 0.0) / (strongCoolingAreaParam.getMaxStations() - strongCoolingAreaParam.getMinStations());
			Double IB = (persistDays - strongCoolingAreaParam.getMinPersistDays() + 0.0) / (strongCoolingAreaParam.getMaxPersistDays() - strongCoolingAreaParam.getMinPersistDays());
			Double IC = (maxTmp - strongCoolingAreaParam.getMinCoolingTmp()) / (strongCoolingAreaParam.getMaxCoolingTmp() - strongCoolingAreaParam.getMinCoolingTmp());
			Double ID = (avgTmp - minTmp) / (maxTmp - minTmp);
			Double index = CommonTool.roundDouble2(IA * strongCoolingAreaParam.getWeight1() + IB * strongCoolingAreaParam.getWeight2()
							+ IC * strongCoolingAreaParam.getWeight3() + ID * strongCoolingAreaParam.getWeight4());
			strongCoolingAreaResult.setIndex(index);
			if(index >= 0 && index < strongCoolingAreaParam.getLevel1()) {
				strongCoolingAreaResult.setLevel("轻度");
			} else if (index > strongCoolingAreaParam.getLevel1() && index <= strongCoolingAreaParam.getLevel2()) {
				strongCoolingAreaResult.setLevel("中度");
			} else if (index > strongCoolingAreaParam.getLevel2() && index <= strongCoolingAreaParam.getLevel3()) {
				strongCoolingAreaResult.setLevel("重度");
			} else if(index >= strongCoolingAreaParam.getLevel3()) {
				strongCoolingAreaResult.setLevel("特重");
			}
			strongCoolingAreaResultList.add(strongCoolingAreaResult);
		}
		return strongCoolingAreaResultList;
	}
	public List<StrongCoolingAreaResult> strongCoolingAreaByTimes(StrongCoolingAreaParam strongCoolingAreaParam) {
		List<StrongCoolingAreaResult> strongCoolingAreaResultList = new ArrayList<StrongCoolingAreaResult>();
		HashMap paramMap = new HashMap();
		TimesParam timesParam = strongCoolingAreaParam.getTimesParam();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> resultList = disasterEvaluate.strongCoolingAreaByTimes(paramMap);
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			StrongCoolingAreaResult strongCoolingAreaResult = new StrongCoolingAreaResult();
			strongCoolingAreaResult.setStartTime((String) itemMap.get("StartTime"));
			strongCoolingAreaResult.setEndTime((String) itemMap.get("EndTime"));
			int persistDays = ((Long) itemMap.get("PersistDays")).intValue();
			strongCoolingAreaResult.setPersistDays(persistDays);
			int stationCnt = (Integer) itemMap.get("StationCnt");
			strongCoolingAreaResult.setStationCnt(stationCnt);
			Double maxTmp = (Double) itemMap.get("MaxTmp");
			strongCoolingAreaResult.setMaxTmp(maxTmp);
			Double minTmp = (Double) itemMap.get("MinTmp");
			strongCoolingAreaResult.setMinTmp(minTmp);
			Double avgTmp = (Double) itemMap.get("AvgTmp");
			strongCoolingAreaResult.setAvgTmp(avgTmp);
			Double IA = (stationCnt - strongCoolingAreaParam.getMinStations() + 0.0) / (strongCoolingAreaParam.getMaxStations() - strongCoolingAreaParam.getMinStations());
			Double IB = (persistDays - strongCoolingAreaParam.getMinPersistDays() + 0.0) / (strongCoolingAreaParam.getMaxPersistDays() - strongCoolingAreaParam.getMinPersistDays());
			Double IC = (maxTmp - strongCoolingAreaParam.getMinCoolingTmp()) / (strongCoolingAreaParam.getMaxCoolingTmp() - strongCoolingAreaParam.getMinCoolingTmp());
			Double ID = (avgTmp - minTmp) / (maxTmp - minTmp);
			Double index = CommonTool.roundDouble2(IA * strongCoolingAreaParam.getWeight1() + IB * strongCoolingAreaParam.getWeight2()
							+ IC * strongCoolingAreaParam.getWeight3() + ID * strongCoolingAreaParam.getWeight4());
			strongCoolingAreaResult.setIndex(index);
			if(index >= 0 && index < strongCoolingAreaParam.getLevel1()) {
				strongCoolingAreaResult.setLevel("轻度");
			} else if (index > strongCoolingAreaParam.getLevel1() && index <= strongCoolingAreaParam.getLevel2()) {
				strongCoolingAreaResult.setLevel("中度");
			} else if (index > strongCoolingAreaParam.getLevel2() && index <= strongCoolingAreaParam.getLevel3()) {
				strongCoolingAreaResult.setLevel("重度");
			} else if(index >= strongCoolingAreaParam.getLevel3()) {
				strongCoolingAreaResult.setLevel("特重");
			}
			strongCoolingAreaResultList.add(strongCoolingAreaResult);
		}
		return strongCoolingAreaResultList;
	}
	
	/**
	 * 年度评价
	 * @param startYear
	 * @param endYear
	 * @return
	 */
	public List<StrongCoolingYearResult> strongCoolingByYear(StrongCoolingAreaParam strongCoolingAreaParam) {
		List<StrongCoolingYearResult> strongCoolingYearResultList = new ArrayList<StrongCoolingYearResult>();
		//区域结果
		List<StrongCoolingAreaResult> strongCoolingAreaResultList = strongCoolingAreaByYears(strongCoolingAreaParam);
		//单站结果
		StrongCoolingStationParam strongCoolingStationParam = new StrongCoolingStationParam();
		strongCoolingStationParam.setTimesParam(strongCoolingAreaParam.getTimesParam());
		List<StrongCoolingStationResult> strongCoolingStationResultList = strongCoolingStationByYears(strongCoolingAreaParam);
		
		HashMap<Integer, Double> CIaMap = new HashMap<Integer, Double>(); // 区域
		HashMap<Integer, Integer> timesMap = new HashMap<Integer, Integer>(); // 区域年度次数
		HashMap<Integer, Double> CIsMap = new HashMap<Integer, Double>(); // 单站
		for(int j = 0; j < strongCoolingAreaResultList.size(); j++) {
			StrongCoolingAreaResult strongCoolingAreaResult = strongCoolingAreaResultList.get(j);
			String startTime = strongCoolingAreaResult.getStartTime();
			Double CIa = strongCoolingAreaResult.getIndex();
			int year = Integer.parseInt(startTime.substring(0, 4));
			Integer times = timesMap.get(year);
			if(times == null || times == 0) {
				timesMap.put(year, 1);
			} else {
				timesMap.put(year, times + 1);
			}
			Double sumCIA = CIaMap.get(year);
			if(sumCIA == null) {
				sumCIA = 0.0;
			}
			sumCIA += CIa;
			CIaMap.put(year, sumCIA);
		}
		
		for(int j = 0; j < strongCoolingStationResultList.size(); j++) {
			StrongCoolingStationResult strongCoolingStationResult = strongCoolingStationResultList.get(j);
			String startTime = strongCoolingStationResult.getStartTime();
			Double CIs = strongCoolingStationResult.getCI();
			int year = Integer.parseInt(startTime.substring(0, 4));
			Double sumCIS = CIsMap.get(year);
			if(sumCIS == null) {
				sumCIS = 0.0;
			}
			sumCIS += CIs;
			CIsMap.put(year, sumCIS);
		}
		//计算常年次数,常年值
		int perennialStartYear = strongCoolingAreaParam.getPerennialStartYear();
		int perennialEndYear = strongCoolingAreaParam.getPerennialEndYear();
		Double yearsCI = 0.0; 
		Double yearsCnt = 0.0;
		for(int i = perennialStartYear; i <= perennialEndYear; i++) {
			Double CIa = CIaMap.get(i);
			Double CIs = CIsMap.get(i);
			if(CIa != null && CIs != null) {
				Double CI = CommonTool.roundDouble(0.5 * CIa + 0.5 * CIs);
				yearsCI += CI;
			}
			Integer times = timesMap.get(i);
			if(times != null) {
				yearsCnt += timesMap.get(i);
			}
		}
		yearsCI /= (perennialEndYear - perennialStartYear + 1);
		yearsCnt /= (perennialEndYear - perennialStartYear + 1);
		yearsCI = CommonTool.roundDouble(yearsCI);
		yearsCnt = CommonTool.roundDouble(yearsCnt);
		
		int startYear = strongCoolingAreaParam.getStartYear();
		int endYear = strongCoolingAreaParam.getEndYear();
		for(int i = startYear; i <= endYear; i++) {
			Double CIa = CIaMap.get(i);
			Double CIs = CIsMap.get(i);
			if(CIa != null && CIs != null) {
				StrongCoolingYearResult strongCoolingYearResult = new StrongCoolingYearResult();
				strongCoolingYearResult.setYear(i);
				Double CI = CommonTool.roundDouble(0.5 * CIa + 0.5 * CIs);
				strongCoolingYearResult.setCI(CI);
				strongCoolingYearResult.setTimes(timesMap.get(i));
				if(CI > 0 && CI < 15.9) {
					strongCoolingYearResult.setLevel("轻度");
				} else if(CI >= 15.9 && CI < 20.6) {
					strongCoolingYearResult.setLevel("中度");
				} else if(CI >= 20.6 && CI < 23) {
					strongCoolingYearResult.setLevel("重度");
				} else if(CI >= 23) {
					strongCoolingYearResult.setLevel("特重");
				}
				strongCoolingYearResult.setYearsCnt(yearsCnt);
				strongCoolingYearResult.setYearsCI(yearsCI);
				strongCoolingYearResultList.add(strongCoolingYearResult);
			}
		}
		Collections.sort(strongCoolingYearResultList);
		return strongCoolingYearResultList;
	}
}
