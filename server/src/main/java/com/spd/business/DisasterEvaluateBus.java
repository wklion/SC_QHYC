package com.spd.business;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.RainStormYearsParam;
import com.spd.common.TimesParam;
import com.spd.common.evaluate.AutumnAreaRainsResult;
import com.spd.common.evaluate.AutumnRainsParam;
import com.spd.common.evaluate.AutumnRainsItemResult;
import com.spd.common.evaluate.AutumnRainsResult;
import com.spd.common.evaluate.AutumnYearRain;
import com.spd.common.evaluate.HighTmpAreaRangeResult;
import com.spd.common.evaluate.HighTmpAreaYearsParam;
import com.spd.common.evaluate.HighTmpAreaYearsResult;
import com.spd.common.evaluate.HighTmpRangeAreaResult;
import com.spd.common.evaluate.HighTmpRangeStationResult;
import com.spd.common.evaluate.MCIAreaTimesResult;
import com.spd.common.evaluate.MCIAreaYearsResult;
import com.spd.common.evaluate.MCIStationTimesResult;
import com.spd.common.evaluate.MCIStationTimesResultSortByStd;
import com.spd.common.evaluate.MCIStationTimesResultSortSum;
import com.spd.common.evaluate.MCIStationYearsResult;
import com.spd.service.IDisasterEvaluate;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 灾害评估
 * @author Administrator
 *
 */
public class DisasterEvaluateBus {

	public HighTmpAreaRangeResult highTmpByRange(TimesParam timesParam) {
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("StartTime", timesParam.getStartTimeStr());
		paramMap.put("EndTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> stationResultList = disasterEvaluate.areaHighTmpSiByRange(paramMap);
		List<LinkedHashMap> areaResultList = disasterEvaluate.areaHighAreaResultSiByRange(paramMap);
		
		List<HighTmpRangeStationResult> highTmpRangeStationResultList = new ArrayList<HighTmpRangeStationResult>();
		for(int i = 0; i < stationResultList.size(); i++) {
			LinkedHashMap itemMap = stationResultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String startTime = (String) itemMap.get("StartTime");
			String endTime = (String) itemMap.get("EndTime");
			Double si = (Double) itemMap.get("si");
			String level = (String) itemMap.get("level");
			HighTmpRangeStationResult highTmpRangeStationResult = new HighTmpRangeStationResult();
			highTmpRangeStationResult.setStation_Id_C(station_Id_C);
			highTmpRangeStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			highTmpRangeStationResult.setArea(CommonUtil.getInstance().stationAreaMap.get(station_Id_C));
			highTmpRangeStationResult.setEndTime(endTime);
			highTmpRangeStationResult.setLevel(level);
			highTmpRangeStationResult.setSi(si);
			highTmpRangeStationResult.setStartTime(startTime);
			highTmpRangeStationResultList.add(highTmpRangeStationResult);
		}
		
		List<HighTmpRangeAreaResult> highTmpRangeAreaResultList = new ArrayList<HighTmpRangeAreaResult>();
		for(int i = 0; i < areaResultList.size(); i++) {
			LinkedHashMap itemMap = areaResultList.get(i);
			String startTime = (String) itemMap.get("StartTime");
			String endTime = (String) itemMap.get("EndTime");
			String level = (String) itemMap.get("level");
			int persistDays = (Integer) itemMap.get("persistDays");
			Double RI = (Double) itemMap.get("RI");
			RI = CommonTool.roundDouble3(RI);
			HighTmpRangeAreaResult highTmpRangeAreaResult = new HighTmpRangeAreaResult();
			highTmpRangeAreaResult.setStartTime(startTime);
			highTmpRangeAreaResult.setEndTime(endTime);
			highTmpRangeAreaResult.setLevel(level);
			highTmpRangeAreaResult.setPersistDays(persistDays);
			highTmpRangeAreaResult.setRI(RI);
			highTmpRangeAreaResultList.add(highTmpRangeAreaResult);
		}
		HighTmpAreaRangeResult highTmpAreaRangeResult = new HighTmpAreaRangeResult();
		highTmpAreaRangeResult.setHighTmpRangeAreaResultList(highTmpRangeAreaResultList);
		highTmpAreaRangeResult.setHighTmpRangeStationResultList(highTmpRangeStationResultList);
		return highTmpAreaRangeResult;
	}
	
	/**
	 * 高温历年统计
	 * @param rainStormYearsParam
	 * @return
	 */
	public List<HighTmpAreaYearsResult> highTmpByYears(HighTmpAreaYearsParam highTmpAreaYearsParam) {
		List<HighTmpAreaYearsResult> highTmpAreaYearsResultList = new ArrayList<HighTmpAreaYearsResult>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		TimesParam timesParam = highTmpAreaYearsParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		String tempStartTimeStr = startTimeStr.split("-")[1] + startTimeStr.split("-")[2];
		String tempEndTimeStr = endTimeStr.split("-")[1] + endTimeStr.split("-")[2];
		//历年
		HashMap paramMap = new HashMap();
		paramMap.put("startMMDD", Integer.parseInt(tempStartTimeStr));
		paramMap.put("endMMDD", Integer.parseInt(tempEndTimeStr));
		paramMap.put("startYear", highTmpAreaYearsParam.getStartYear());
		paramMap.put("endYear", highTmpAreaYearsParam.getEndYear());
		List resultList = disasterEvaluate.areaHighAreaResultByYears(paramMap);
		//常年
		HashMap paramMap2 = new HashMap();
		paramMap2.put("startMMDD", Integer.parseInt(tempStartTimeStr));
		paramMap2.put("endMMDD", Integer.parseInt(tempEndTimeStr));
		paramMap2.put("startYear", highTmpAreaYearsParam.getPerennialStartYear());
		paramMap2.put("endYear", highTmpAreaYearsParam.getPerennialEndYear());
		List yearsResultList = disasterEvaluate.areaHighAreaResultByYears(paramMap2);
		//YHI的常年值
		HashMap paramMap3 = new HashMap();
		List YHIYearsList = disasterEvaluate.YHIareaHighTmpYearResult(paramMap3);
		int size = YHIYearsList.size();
		Double level1 = highTmpAreaYearsParam.getYHILevel1();
		Double level2 = highTmpAreaYearsParam.getYHILevel2();
		Double level3 = highTmpAreaYearsParam.getYHILevel3();
		Integer index1 = ((Long)Math.round(size * level1)).intValue();
		Integer index2 = ((Long)Math.round(size * level2)).intValue();
		Integer index3 = ((Long)Math.round(size * level3)).intValue();
		Double YHI1 = (Double) ((HashMap)YHIYearsList.get(index1)).get("YHI");
		Double YHI2 = (Double) ((HashMap)YHIYearsList.get(index2)).get("YHI");
		Double YHI3 = (Double) ((HashMap)YHIYearsList.get(index3)).get("YHI");
		Double YHIYears = 0.0;
		//次数常年值
		Double CntYears = (yearsResultList.size() + 0.0) / (highTmpAreaYearsParam.getPerennialEndYear() - highTmpAreaYearsParam.getPerennialStartYear() + 1);
		CntYears = CommonTool.roundDouble2(CntYears);
		for(int i = 0; i < yearsResultList.size(); i++) {
			HashMap yearMap = (HashMap) yearsResultList.get(i);
			Integer year = Integer.parseInt((String) yearMap.get("year"));
			Integer persistDays = (Integer)yearMap.get("persistDays");
			Double RI = (Double) yearMap.get("RI");
			Double YHI = persistDays / RI;
			YHIYears += YHI;
		}
		YHIYears = YHIYears / (highTmpAreaYearsParam.getPerennialEndYear() - highTmpAreaYearsParam.getPerennialStartYear() + 1);
		YHIYears = CommonTool.roundDouble2(YHIYears);
		//历年
		HashMap<Integer, List> RIYearsMap = new HashMap<Integer, List>(); //YHI历年值
		HashMap<Integer, Double> CntDaysMap = new HashMap<Integer, Double>(); // 次数历年值
		for(int i = 0; i < resultList.size(); i++) {
			HashMap itemMap = (HashMap) resultList.get(i);
			Integer year = Integer.parseInt((String) itemMap.get("year"));
			List RIList = RIYearsMap.get(year);
			if(RIList == null) {
				RIList = new ArrayList();
			}
			RIList.add(itemMap);
			RIYearsMap.put(year, RIList);
		}
		for(int i = highTmpAreaYearsParam.getStartYear(); i <= highTmpAreaYearsParam.getEndYear(); i++) {
			List list = RIYearsMap.get(i);
			if(list == null) {
				HighTmpAreaYearsResult highTmpAreaYearsResult = new HighTmpAreaYearsResult();
				highTmpAreaYearsResult.setYear(i);
				highTmpAreaYearsResult.setYearCnt(CntYears);
				highTmpAreaYearsResult.setYearYHI(YHIYears);
				highTmpAreaYearsResultList.add(highTmpAreaYearsResult);
			} else {
				HighTmpAreaYearsResult highTmpAreaYearsResult = new HighTmpAreaYearsResult();
				highTmpAreaYearsResult.setYear(i);
				highTmpAreaYearsResult.setCnt(list.size());
				highTmpAreaYearsResult.setYearCnt(CntYears);
				highTmpAreaYearsResult.setYearYHI(YHIYears);
				Double YHI = 0.0;
				for(int j = 0; j < list.size(); j++) {
					HashMap dataMap = (HashMap) list.get(j);
					Integer persistDays = (Integer) dataMap.get("persistDays");
					Double RI = (Double) dataMap.get("RI");
					YHI += (persistDays / RI);
				}
				YHI = CommonTool.roundDouble3(YHI);
				highTmpAreaYearsResult.setYHI(YHI);
				if(YHI >= 0 && YHI < YHI1) {
					highTmpAreaYearsResult.setLevel("轻度");
				} else if(YHI >= YHI1 && YHI < YHI2) {
					highTmpAreaYearsResult.setLevel("中度");
				} else if(YHI >= YHI2 && YHI < YHI3) {
					highTmpAreaYearsResult.setLevel("重度");
				} else if(YHI >= YHI3) {
					highTmpAreaYearsResult.setLevel("特重");
				}
				highTmpAreaYearsResultList.add(highTmpAreaYearsResult);
			}
			
		}
		return highTmpAreaYearsResultList;
	}
	
	/**
	 * 秋雨计算
	 * @param autumnRainsParam
	 * @return
	 */
	public AutumnRainsResult autumnRains(AutumnRainsParam autumnRainsParam) {
		AutumnRainsResult autumnRainsResult = new AutumnRainsResult();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		List list = disasterEvaluate.autumnRains(paramMap);
		List<AutumnRainsItemResult> resultList = new ArrayList<AutumnRainsItemResult>();
		//计算常年值
		Double contrastLengthIndexISum = 0.0, contrastPreIndexSum = 0.0, contrastIntensityIndexSum = 0.0;
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			Integer year = (Integer) dataMap.get("year");
			if(year >= 1981 && year <= 2010) {
				Double lengthIndexI = (Double) dataMap.get("LengthIndexI");
				contrastLengthIndexISum += lengthIndexI;
				Double intensityIndex = (Double) dataMap.get("IntensityIndex");
				contrastIntensityIndexSum += intensityIndex;
				Double preIndex = (Double)dataMap.get("PreIndex");
				contrastPreIndexSum += preIndex;
			}
		}
		contrastLengthIndexISum /= 30;
		contrastPreIndexSum /= 30;
		contrastIntensityIndexSum /= 30;
		autumnRainsResult.setContrastIntensityIndex(contrastIntensityIndexSum);
		autumnRainsResult.setContrastLengthIndexI(contrastLengthIndexISum);
		autumnRainsResult.setContrastPreIndex(contrastPreIndexSum);
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			AutumnRainsItemResult autumnRainsItemResult = new AutumnRainsItemResult();
			Integer year = (Integer) dataMap.get("year");
			autumnRainsItemResult.setYear(year);
			String startTime = (String) dataMap.get("StartTime");
			autumnRainsItemResult.setStartTime(startTime);
			String endTime = (String) dataMap.get("EndTime");
			autumnRainsItemResult.setEndTime(endTime);
			autumnRainsItemResult.setPersistDays(CommonTool.caleDays(startTime, endTime) + 1);
			Double lengthIndexI = (Double) dataMap.get("LengthIndexI");
			if(lengthIndexI != null) {
				autumnRainsItemResult.setLengthIndexI(lengthIndexI);
				//lengthLevel
				if(lengthIndexI >= autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setLengthLevel("显著偏强");
				} else if (lengthIndexI >= autumnRainsParam.getLevel2() && lengthIndexI < autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setLengthLevel("偏强");
				} else if (lengthIndexI > autumnRainsParam.getLevel3() && lengthIndexI < autumnRainsParam.getLevel2()) {
					autumnRainsItemResult.setLengthLevel("正常");
				} else if (lengthIndexI > autumnRainsParam.getLevel4() && lengthIndexI <= autumnRainsParam.getLevel3()) {
					autumnRainsItemResult.setLengthLevel("偏弱");
				} else if (lengthIndexI <= autumnRainsParam.getLevel4() ) {
					autumnRainsItemResult.setLengthLevel("显著偏弱");
				}
			}
			Double pre = (Double) dataMap.get("Pre");
			autumnRainsItemResult.setPre(pre);
			Double preIndex = (Double)dataMap.get("PreIndex");
			if(preIndex != null) {
				autumnRainsItemResult.setPreIndex(preIndex);
				if(preIndex >= autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setPreLevel("显著偏强");
				} else if (preIndex >= autumnRainsParam.getLevel2() && preIndex < autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setPreLevel("偏强");
				} else if (preIndex > autumnRainsParam.getLevel3() && preIndex < autumnRainsParam.getLevel2()) {
					autumnRainsItemResult.setPreLevel("正常");
				} else if (preIndex > autumnRainsParam.getLevel4() && preIndex <= autumnRainsParam.getLevel3()) {
					autumnRainsItemResult.setPreLevel("偏弱");
				} else if (preIndex <= autumnRainsParam.getLevel4() ) {
					autumnRainsItemResult.setPreLevel("显著偏弱");
				}
			}
			Double intensityIndex = (Double) dataMap.get("IntensityIndex");
			if(intensityIndex != null) {
				autumnRainsItemResult.setIntensityIndex(intensityIndex);
				if(intensityIndex >= autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setIntensityLevel("显著偏强");
				} else if (intensityIndex >= autumnRainsParam.getLevel2() && intensityIndex < autumnRainsParam.getLevel1()) {
					autumnRainsItemResult.setIntensityLevel("偏强");
				} else if (intensityIndex > autumnRainsParam.getLevel3() && intensityIndex < autumnRainsParam.getLevel2()) {
					autumnRainsItemResult.setIntensityLevel("正常");
				} else if (intensityIndex > autumnRainsParam.getLevel4() && intensityIndex <= autumnRainsParam.getLevel3()) {
					autumnRainsItemResult.setIntensityLevel("偏弱");
				} else if (intensityIndex <= autumnRainsParam.getLevel4() ) {
					autumnRainsItemResult.setIntensityLevel("显著偏弱");
				}
			}
			resultList.add(autumnRainsItemResult);
		}
		autumnRainsResult.setAutumnRainsItemResultList(resultList);
		return autumnRainsResult;
	}
	
	/**
	 * 按时间段查询秋雨对应的雨量等信息
	 * @param timesParam
	 * @return
	 */
	public List<AutumnAreaRainsResult> autumnRainsByTimes(TimesParam timesParam) {
		List<AutumnAreaRainsResult> autumnAreaRainsResultList = new ArrayList<AutumnAreaRainsResult>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.autumnRainsByTimes(paramMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long start = timesParam.getStartDate().getTime();
		long end = timesParam.getEndDate().getTime();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			boolean flag = true;
			for(int j = 0; j < resultList.size(); j++) {
				HashMap dataMap = (HashMap) resultList.get(j);
				String datetimes = (String) dataMap.get("datetimes");
				if(timeStr.equals(datetimes)) {
					AutumnAreaRainsResult autumnAreaRainsResult = new AutumnAreaRainsResult();
					autumnAreaRainsResult.setDatetimeStr(datetimes);
					autumnAreaRainsResult.setPres(CommonTool.roundDouble((Double) dataMap.get("sum")));
					autumnAreaRainsResult.setStationCnt(((Long) dataMap.get("cnt")).intValue());
					flag = false;
					autumnAreaRainsResultList.add(autumnAreaRainsResult);
					break;
				}
			}
			if(flag) {
				AutumnAreaRainsResult autumnAreaRainsResult = new AutumnAreaRainsResult();
				autumnAreaRainsResult.setDatetimeStr(timeStr);
				autumnAreaRainsResult.setPres(0.0);
				autumnAreaRainsResult.setStationCnt(0);
				autumnAreaRainsResultList.add(autumnAreaRainsResult);
			}
		}
		return autumnAreaRainsResultList;
	}
	
	public List<AutumnYearRain> autumnRainsByYear(int year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<AutumnYearRain> autumnYearRainList = new ArrayList<AutumnYearRain>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("year", year);
		//查询当年秋雨的开始结束日期
		List list = disasterEvaluate.autumnTimesRangeByYear(paramMap);
		String startTime = "", endTime = "";
		Date startDate = null, endDate = null;
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			startTime = (String) dataMap.get("StartTime");
			endTime = (String) dataMap.get("EndTime");
			try {
				startDate = sdf.parse(startTime);
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		//查询当年的多雨期
		List seqList = disasterEvaluate.autumnSeqRangeByYear(paramMap);
		List<Long[]> times = new ArrayList<Long[]>();
		if(seqList != null && seqList.size() > 0) {
			for(int i = 0; i < seqList.size(); i++) {
				HashMap dataMap = (HashMap) seqList.get(i);
				String itemStartTime = (String) dataMap.get("StartTime");
				String itemEndTime = (String) dataMap.get("EndTime");
				Long[] rangeTimes = new Long[2];
				try {
					rangeTimes[0] = sdf.parse(itemStartTime).getTime();
					rangeTimes[1] = sdf.parse(itemEndTime).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				times.add(rangeTimes);
			}
		}
		long start = startDate.getTime();
		long end = endDate.getTime();
		//查询指定时间内的雨量值
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		paramMap.put("items", items);
		String[] itemArray = items.split(",");
		List rainYearsList = disasterEvaluate.autumnRainsByYear(paramMap);
		
		LinkedHashMap<String, Integer> cntMap = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, Double> sumMap = new LinkedHashMap<String, Double>();
		for(int i = 0; i < rainYearsList.size(); i++) {
			HashMap dataMap = (HashMap) rainYearsList.get(i);
			for(int j = 0; j < itemArray.length; j++) {
				String item = itemArray[j];
				String dateTimeStr = CommonTool.createTimeStrByColumn(item, year);
				Double itemValue = (Double) dataMap.get(item);
				if(itemValue != null && itemValue >= 0.1 && itemValue <= 999) {
					if(cntMap.get(dateTimeStr) == null) {
						cntMap.put(dateTimeStr, 0);
					}
					cntMap.put(dateTimeStr, cntMap.get(dateTimeStr) + 1);
					Double preSum = sumMap.get(dateTimeStr);
					if(preSum == null) {
						preSum = 0.0;
					}
					preSum += itemValue;
					sumMap.put(dateTimeStr, preSum);
				} 
			}
		}
		List resultList = new ArrayList();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			if(cntMap.containsKey(timeStr)) {
				HashMap itemMap = new HashMap();
				itemMap.put("datetime", timeStr);
				itemMap.put("cnt", cntMap.get(timeStr));
				resultList.add(itemMap);
			} else {
				HashMap itemMap = new HashMap();
				itemMap.put("datetime", timeStr);
				itemMap.put("cnt", 0);
				resultList.add(itemMap);
			}
		}
		//组装结果
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			AutumnYearRain autumnYearRain = new AutumnYearRain();
			autumnYearRain.setDatetime(sdf.format(new Date(i)));
			if(sumMap.get(timeStr) != null) {
				Double avgPre = sumMap.get(timeStr) / rainYearsList.size();
				autumnYearRain.setAvgPre(CommonTool.roundDouble(avgPre));
			} else {
				autumnYearRain.setAvgPre(0.0);
			}
			
			if(cntMap.get(timeStr) != null) {
				autumnYearRain.setCnt(cntMap.get(timeStr));
			} else {
				autumnYearRain.setCnt(0);
			}
			boolean isInRainRange = false;
			for(int j = 0; j < times.size(); j++) {
				Long[] timesRange = times.get(j);
				if(i >= timesRange[0] && i <= timesRange[1]) {
					isInRainRange = true;
					break;
				}
			}
			autumnYearRain.setInRainRange(isInRainRange);
			autumnYearRainList.add(autumnYearRain);
		}
		return autumnYearRainList;
	}
	
	public List<MCIStationTimesResult> mciStationByTimes(TimesParam timesParam) {
		List<MCIStationTimesResult> mciTimesResultList = new ArrayList<MCIStationTimesResult>();
		List<MCIStationTimesResult> completeResultList = new ArrayList<MCIStationTimesResult>(); // 已经结束的过程
		List<MCIStationTimesResult> unCompleteTimesResultList = new ArrayList<MCIStationTimesResult>(); // 没有结束的过程
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		List sumList = disasterEvaluate.mciSumStrength(paramMap);
		Double avg = 0.0;
		if(sumList == null || sumList.size() == 0) return null;
		HashMap sumMap = (HashMap) sumList.get(0);
		avg = (Double) sumMap.get("avg");
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		
		List constResultList = disasterEvaluate.mciSumStrength(new HashMap()); //常量
		if(constResultList == null || constResultList.size() == 0) return null;
		HashMap constDataMap = (HashMap) constResultList.get(0);
		Double constAvg = (Double)constDataMap.get("avg");
		paramMap.put("avg", avg);
		Double constStd = (Double)constDataMap.get("std");
		paramMap.put("std", constStd);
		//mci值对应的级别
		HashMap<Integer, Double> mciStrengthMap = getMCIStrength();
		List resultList = disasterEvaluate.mciStationByTimes(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				MCIStationTimesResult mciTimesResult = new MCIStationTimesResult();
				String resultStartTime = (String) dataMap.get("StartTime");
				mciTimesResult.setStartTime(resultStartTime);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				mciTimesResult.setStation_Id_C(station_Id_C);
				String station_Name = CommonUtil.getInstance().stationNameMap.get(station_Id_C);
				String area = CommonUtil.getInstance().stationAreaMap.get(station_Id_C);
				mciTimesResult.setStation_Name(station_Name);
				mciTimesResult.setArea(area);
				String endTime = (String) dataMap.get("EndTime");
				if(endTime == null) {
					mciTimesResult.setEndTime("还未结束");
					HashMap unCompleteParamMap = new HashMap();
					unCompleteParamMap.put("startTime", resultStartTime);
					unCompleteParamMap.put("endTime", timesParam.getEndTimeStr());
					unCompleteParamMap.put("station_Id_C", station_Id_C);
					mciTimesResult.setStartTime(resultStartTime);
					List<LinkedHashMap> unCompleteList = disasterEvaluate.querySumMCI(unCompleteParamMap);
					if(unCompleteList != null && unCompleteList.size() > 0) {
						HashMap tempMap = (HashMap) unCompleteList.get(0);
						Double singleStrength = (Double) tempMap.get("SingleStrength");
						Double singleSynthStrength = (Double) tempMap.get("SingleSynthStrength");
						Double sumStrength = (Double) tempMap.get("sumStrength");
						if(singleStrength == null || singleSynthStrength == null || sumStrength == null) continue;
						singleStrength = CommonTool.roundDouble2(singleStrength);
						singleSynthStrength = CommonTool.roundDouble2(singleSynthStrength);
						sumStrength = CommonTool.roundDouble2(sumStrength);
//						(SumStrength - (${avg})) /  (${std}) as std
//						Double std = (sumStrength - constAvg) / constStd;
						mciTimesResult.setSumStrength(sumStrength);
//						mciTimesResult.setStandardValue(std);
						mciTimesResult.setSingleStrength(singleStrength);
						mciTimesResult.setSingleSynthStrength(singleSynthStrength);
//						mciTimesResult.setStrengthLevel(createMCIStrengthLevel(mciStrengthMap, singleSynthStrength));
						unCompleteTimesResultList.add(mciTimesResult);
					}
				} else {
					mciTimesResult.setEndTime(endTime);
					mciTimesResult.setSingleStrength((Double) dataMap.get("SingleStrength"));
					double singleSynthStrength = (Double) dataMap.get("SingleSynthStrength");
					mciTimesResult.setSingleSynthStrength(singleSynthStrength);
					int strengthLevel = 0;
					double std = (Double) dataMap.get("std");
					mciTimesResult.setStrengthLevel(createMCIStrengthLevel(mciStrengthMap, std));
					mciTimesResult.setSumStrength((Double) dataMap.get("SumStrength"));
					mciTimesResult.setStandardValue(std);
					completeResultList.add(mciTimesResult);
				}
				
			}
		}
		//排序
//		Arrays.sort((MCIStationTimesResult[])unCompleteTimesResultList.toArray(), MCIStationTimesResult.class);
		Collections.sort(unCompleteTimesResultList, new MCIStationTimesResultSortSum());
		Collections.sort(completeResultList, new MCIStationTimesResultSortByStd());
//		List<MCIStationTimesResult> sortCompleteResultList = new ArrayList<MCIStationTimesResult>(); // 已经结束的过程
//		List<MCIStationTimesResult> unCompleteTimesResultList = new ArrayList<MCIStationTimesResult>(); // 没有结束的过程
		if(unCompleteTimesResultList.size() != 0) {
			for(int i = 0; i < unCompleteTimesResultList.size(); i++) {
				MCIStationTimesResult mciStationTimesResult = unCompleteTimesResultList.get(i);
				mciStationTimesResult.setRank(i + 1);
				mciStationTimesResult.setDays(CommonTool.caleDays(mciStationTimesResult.getStartTime(), mciStationTimesResult.getEndTime()));
				mciTimesResultList.add(mciStationTimesResult);
			}
		}
		if(completeResultList.size() != 0) {
			for(int i = 0; i < completeResultList.size(); i++) {
				MCIStationTimesResult mciStationTimesResult = completeResultList.get(i);
				mciStationTimesResult.setRank(i + 1);
				mciStationTimesResult.setDays(CommonTool.caleDays(mciStationTimesResult.getStartTime(), mciStationTimesResult.getEndTime()));
				mciTimesResultList.add(mciStationTimesResult);
			}
		}
//		if(resultList != null && resultList.size() > 0) {
//			for(int i = 0; i < resultList.size(); i++) {
//				MCIStationTimesResult mciTimesResult = new MCIStationTimesResult();
//				HashMap dataMap = (HashMap) resultList.get(i);
//				mciTimesResult.setEndTime((String) dataMap.get("EndTime"));
//				mciTimesResult.setStartTime((String) dataMap.get("StartTime"));
//				mciTimesResult.setSingleStrength((Double) dataMap.get("SingleStrength"));
//				double singleSynthStrength = (Double) dataMap.get("SingleSynthStrength");
//				mciTimesResult.setSingleSynthStrength(singleSynthStrength);
//				//每次实时计算
////				mciTimesResult.setStrengthLevel((Integer) dataMap.get("StrengthLevel"));
//				int strengthLevel = 0;
//				mciTimesResult.setStrengthLevel(createMCIStrengthLevel(mciStrengthMap, singleSynthStrength));
//				mciTimesResult.setSumStrength((Double) dataMap.get("SumStrength"));
//				String station_Id_C = (String) dataMap.get("Station_Id_C");
//				mciTimesResult.setStation_Id_C(station_Id_C);
//				String station_Name = CommonUtil.getInstance().stationNameMap.get(station_Id_C);
//				String area = CommonUtil.getInstance().stationAreaMap.get(station_Id_C);
//				mciTimesResult.setStation_Name(station_Name);
//				mciTimesResult.setArea(area);
//				mciTimesResult.setStandardValue((Double) dataMap.get("std"));
//				mciTimesResult.setRank(i + 1);
//				mciTimesResultList.add(mciTimesResult);
//			}
//		}
		return mciTimesResultList;
	}
	
	private String createMCIStrengthLevel(HashMap<Integer, Double> mciStrengthMap, double std) {
		if(std <= mciStrengthMap.get(0)) {
			return "特旱";
		} else if(std > mciStrengthMap.get(0) && std <= mciStrengthMap.get(1)){
			return "重旱";
		} else if(std > mciStrengthMap.get(1) && std <= mciStrengthMap.get(2)){
			return "中旱";
		} else if(std > mciStrengthMap.get(2)) {
			return "轻旱";
		}
		return "";
	}
	/**
	 * 干旱年度查询
	 * @param startYear
	 * @param endYear
	 * @return
	 */
	public List<MCIStationYearsResult> mciStationByYears(int startYear, int endYear) {
		List<MCIStationYearsResult> mciYearsResultList = new ArrayList<MCIStationYearsResult>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		List resultList = disasterEvaluate.mciStationByYears(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				MCIStationYearsResult mciYearsResult = new MCIStationYearsResult();
				mciYearsResult.setRank(i + 1);
				mciYearsResult.setSumStrength((Double) dataMap.get("sum"));
				mciYearsResult.setYear(Integer.parseInt((String) dataMap.get("year")));
				mciYearsResultList.add(mciYearsResult);
			}
			//把年份中不存在的结果也到最后面
			LinkedHashSet<Integer> yearsSet = new LinkedHashSet<Integer>();
			for(int i = startYear; i <= endYear; i++) {
				yearsSet.add(i);
			}
			LinkedHashSet<Integer> existYearsSet = new LinkedHashSet<Integer>();
			for(int i = 0; i < mciYearsResultList.size(); i++) {
				MCIStationYearsResult mciYearsResult = mciYearsResultList.get(i);
				existYearsSet.add(mciYearsResult.getYear());
			}
			yearsSet.removeAll(existYearsSet);
			Iterator<Integer> it = yearsSet.iterator();
			while(it.hasNext()) {
				Integer year = it.next();
				MCIStationYearsResult mciYearsResult = new MCIStationYearsResult();
				mciYearsResult.setYear(year);
				mciYearsResultList.add(mciYearsResult);
			}
		}
		return mciYearsResultList;
	}
	
	
	public List<MCIAreaTimesResult> mciAreaByTimes(TimesParam timesParam) {
		List<MCIAreaTimesResult> mciAreaTimesResultList = new ArrayList<MCIAreaTimesResult>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List resultList = disasterEvaluate.mciAreaByTimes(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				MCIAreaTimesResult mciAreaTimesResult = new MCIAreaTimesResult();
				mciAreaTimesResult.setStartTime((String) dataMap.get("StartTime"));
				mciAreaTimesResult.setEndTime((String) dataMap.get("EndTime"));
				mciAreaTimesResult.setDays(CommonTool.caleDays(mciAreaTimesResult.getStartTime(), mciAreaTimesResult.getEndTime()));
				mciAreaTimesResult.setStationCnts((Integer) dataMap.get("StationCnts"));
				mciAreaTimesResult.setSumStrength((Double) dataMap.get("SumStrength"));
				mciAreaTimesResult.setYear((Integer) dataMap.get("year"));
				mciAreaTimesResult.setRank(i + 1);
				mciAreaTimesResultList.add(mciAreaTimesResult);
			}
		}
		return mciAreaTimesResultList;
	}
	
	public List<MCIAreaYearsResult> mciAreaByYears(int startYear, int endYear) {
		List<MCIAreaYearsResult> mciYearsResultList = new ArrayList<MCIAreaYearsResult>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		List resultList = disasterEvaluate.mciAreaByYears(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				MCIAreaYearsResult mciYearsResult = new MCIAreaYearsResult();
				mciYearsResult.setRank(i + 1);
				mciYearsResult.setSumStrength((Double) dataMap.get("sum"));
				mciYearsResult.setYear(Integer.parseInt((String) dataMap.get("year")));
				mciYearsResultList.add(mciYearsResult);
			}
		}
		return mciYearsResultList;
	}
	
	private HashMap<Integer, Double> getMCIStrength() {
		//计算MCI等级的计算
		HashMap<Integer, Double> resultMap = new HashMap<Integer, Double>();
		IDisasterEvaluate disasterEvaluate = (IDisasterEvaluate)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterEvaluateImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> resultList = disasterEvaluate.mciStationBySingleStrength(paramMap);
		// 特旱 <= 5%对应的值
		// 重旱 > 5% && <= 15%
		// 中旱 > 15% && <= 40%
		// 轻旱 > 40% && < 1
		// 无旱 >= 1
		int[] indexes = new int[3]; // 三个节点，分别对应的5%, 15%, 40%
		int size = resultList.size();
		indexes[0] = ((Long)(Math.round(size * 0.05))).intValue();
		indexes[1] = ((Long)(Math.round(size * 0.15))).intValue();
		indexes[2] = ((Long)(Math.round(size * 0.4))).intValue();
		double[] mciCrisis = new double[3]; //
		mciCrisis[0] = (Double) resultList.get(indexes[0]).get("SingleSynthStrength");
		mciCrisis[1] = (Double) resultList.get(indexes[1]).get("SingleSynthStrength");
		mciCrisis[2] = (Double) resultList.get(indexes[2]).get("SingleSynthStrength");
//		for(int i = 0; i < resultList.size(); i++) {
//			LinkedHashMap itemMap = resultList.get(i);
//			if(i == indexes[0]) {
//				mciCrisis[0] = (Double) itemMap.get("SingleSynthStrength");
//			} else if(i == indexes[1]) {
//				mciCrisis[1] = (Double) itemMap.get("SingleSynthStrength");
//			} else if(i == indexes[2]) {
//				mciCrisis[2] = (Double) itemMap.get("SingleSynthStrength");
//			}
//		}
		resultMap.put(0, mciCrisis[0]);
		resultMap.put(1, mciCrisis[1]);
		resultMap.put(2, mciCrisis[2]);
		return resultMap;
	}
}
