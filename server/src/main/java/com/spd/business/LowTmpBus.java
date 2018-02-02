package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.spd.common.LowTmpByRangeParam;
import com.spd.common.LowTmpResult;
import com.spd.common.LowTmpResultHous;
import com.spd.common.LowTmpResultTimes;
import com.spd.common.LowTmpSequenceResult;
import com.spd.common.LowTmpYearsResult;
import com.spd.common.TimesRangeParam;
import com.spd.common.LowTmpResultHous.HouSequence;
import com.spd.service.IHou;
import com.spd.tool.CommonTool;

/**
 * 灾害中的低温统计
 * @author Administrator
 *
 */
public class LowTmpBus {

	//常年开始、结束年
	private int constatStartYear = 1981;

	private int constatEndYear = 2010;
	
	/**
	 * 候低温统计
	 * @param lowTmpByRangeParam
	 * @return
	 */
	public LowTmpResult lowTmpByRange(LowTmpByRangeParam lowTmpByRangeParam) {
		constatStartYear = lowTmpByRangeParam.getConstatStartYear();
		constatEndYear = lowTmpByRangeParam.getConstatEndYear();
		//查询startYear到endYear之间的结果
		TimesRangeParam timesRangeParam = lowTmpByRangeParam.getTimesRangeParam();
		//TODO 不做处理，客户端进行候对应的日期的运算。
//		String startTimeStr = CommonTool.disposeHouStartTimes(timesRangeParam.getStartDate());
//		String endTimeStr = CommonTool.disposeHouEndTimes(timesRangeParam.getEndDate());
//		timesRangeParam.setStartTimeStr(startTimeStr);
//		timesRangeParam.setEndTimeStr(endTimeStr);
		lowTmpByRangeParam.setTimesRangeParam(timesRangeParam);
		List<String> yearMonHouList = CommonTool.getYearMonHou(timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
		List<Integer> monthList = new ArrayList<Integer>();
		List<Integer> houList = new ArrayList<Integer>();
		
		Set<Integer> monthSet = new HashSet<Integer>();
		Set<Integer> houSet = new HashSet<Integer>();
		
		for(String yearMonHouItem : yearMonHouList) {
			String[] tempArray = yearMonHouItem.split("_");
			int year = Integer.parseInt(tempArray[0]);
			int month = Integer.parseInt(tempArray[1]);
			int hou = Integer.parseInt(tempArray[2]);
			monthSet.add(month);
			houSet.add(hou);
		}
		Iterator<Integer> monIt = monthSet.iterator();
		while(monIt.hasNext()) {
			monthList.add(monIt.next());
		}
		
		Iterator<Integer> houIt = houSet.iterator();
		while(houIt.hasNext()) {
			houList.add(houIt.next());
		}
		
		IHou houService = (IHou)ContextLoader.getCurrentWebApplicationContext().getBean("HouImpl");
		HashMap paramMap = new HashMap();
		int startMonth = 0, endMonth = 0, startHou = 0, endHou = 0;
		paramMap.put("startYear", constatStartYear);
		paramMap.put("endYear", constatEndYear);
		
		paramMap.put("month", monthList);
		paramMap.put("hou", houList);
		List<LinkedHashMap> houResultList = houService.queryHouTmpData(paramMap);
		//查询指定时间的统计结果
		HashMap currentMap = new HashMap();
		//创建claus
		String claus = "";
		for(int i = 0; i < yearMonHouList.size(); i++) {
			String yearMonHouItem = yearMonHouList.get(i);
			String[] tempArray = yearMonHouItem.split("_");
			int year = Integer.parseInt(tempArray[0]);
			int month = Integer.parseInt(tempArray[1]);
			int hou = Integer.parseInt(tempArray[2]);
			claus += " year = " + year + " and month = " + month + " and hou = " + hou;
			if(i != yearMonHouList.size() - 1) {
				claus += " or ";
			}
		}
		currentMap.put("claus", claus);
		List<LinkedHashMap> currentHouResultList = houService.queryHouTmpDataByTimes(currentMap);
		//对比结果 houResultList 和 currentHouResultList
		List<LowTmpResultHous> housList = new ArrayList<LowTmpResultHous>(); // 逐候结果
		HashMap<String, LowTmpResultHous> housMap = new HashMap<String, LowTmpResultHous>();
		for(int i=0; i<currentHouResultList.size(); i++) {
			LinkedHashMap currentHouResultMap = currentHouResultList.get(i);
			String station_Id_C = (String) currentHouResultMap.get("Station_Id_C");
			String station_Name = (String) currentHouResultMap.get("Station_Name");
			String area = (String) currentHouResultMap.get("area");
			LowTmpResultHous lowTmpResultHos = housMap.get(station_Id_C);
			if(lowTmpResultHos == null) {
				lowTmpResultHos = new LowTmpResultHous();
			}
			List<LowTmpResultHous.HouSequence> houSeqList = lowTmpResultHos.getList();
			if(houSeqList == null) {
				houSeqList = new ArrayList<LowTmpResultHous.HouSequence>();
			}
			LowTmpResultHous.HouSequence houSequence = lowTmpResultHos.new HouSequence();
			lowTmpResultHos.setStation_Id_C(station_Id_C);
			lowTmpResultHos.setStation_Name(station_Name);
			lowTmpResultHos.setArea(area);
			int year = (Integer) currentHouResultMap.get("year");
			int month = (Integer) currentHouResultMap.get("month");
			int hou = (Integer) currentHouResultMap.get("hou");
			double avgTmp = (Double) currentHouResultMap.get("avgTmp");
			houSequence.setTime(year + "-" + month + "-" + hou);
			houSequence.setValue(avgTmp);
			//距平值
			for(int j = 0; j < houResultList.size(); j++) {
				LinkedHashMap houResultMap = houResultList.get(j);
				String station_Id_CItem = (String) houResultMap.get("Station_Id_C");
				int monthItem = (Integer) houResultMap.get("month");
				int houItem = (Integer) houResultMap.get("hou");
				if(station_Id_C.equals(station_Id_CItem) && month == monthItem && hou == houItem) {
					double avgTmpItem = (Double) houResultMap.get("avgTmp");
					double anomaly = CommonTool.roundDouble(avgTmp - avgTmpItem);
					houSequence.setAnomaly(anomaly);
					break;
				}
			}
			houSeqList.add(houSequence);
			lowTmpResultHos.setList(houSeqList);
			housMap.put(station_Id_C, lowTmpResultHos);
		}
		//
		Set<String> set = housMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			housList.add(housMap.get(it.next()));
		}
		//逐次
		List<LowTmpResultTimes>  lowTmpResultTimesListResult = calcSeqhousMap(housMap, lowTmpByRangeParam);
		
		LowTmpResult lowTmpResult = new LowTmpResult();
		lowTmpResult.setLowTmpResultHousResult(housList);
		//过滤
		int[] level1ExceptionMonthes = lowTmpByRangeParam.getLevel1ExceptMonthes();
		int[] level2ExceptionMonthes = lowTmpByRangeParam.getLevel2ExceptMonthes();
		if(level1ExceptionMonthes != null) {
			for(int i = lowTmpResultTimesListResult.size() - 1; i >=0; i--) {
				LowTmpResultTimes lowTmpResultTimes = lowTmpResultTimesListResult.get(i);
				String level = lowTmpResultTimes.getLevel();
				String startDateTime = lowTmpResultTimes.getStartDatetime();
				String endDateTime = lowTmpResultTimes.getEndDatetime();
				int startDateTimeMonth = Integer.parseInt(startDateTime.substring(5, 7));
				int endDateTimeMonth = Integer.parseInt(endDateTime.substring(5, 7));
				if("一般".equals(level)) {
					for(int j = 0; j < level1ExceptionMonthes.length; j++) {
						if(startDateTimeMonth == level1ExceptionMonthes[j] || endDateTimeMonth == level1ExceptionMonthes[j]) {
							lowTmpResultTimesListResult.remove(i);
							break;
						}
					}
				}
			}
		}
		
		if(level2ExceptionMonthes != null) {
			for(int i = lowTmpResultTimesListResult.size() - 1; i >=0; i--) {
				LowTmpResultTimes lowTmpResultTimes = lowTmpResultTimesListResult.get(i);
				String level = lowTmpResultTimes.getLevel();
				String startDateTime = lowTmpResultTimes.getStartDatetime();
				String endDateTime = lowTmpResultTimes.getEndDatetime();
				int startDateTimeMonth = Integer.parseInt(startDateTime.substring(5, 7));
				int endDateTimeMonth = Integer.parseInt(endDateTime.substring(5, 7));
				if("严重".equals(level)) {
					for(int j = 0; j < level2ExceptionMonthes.length; j++) {
						if(startDateTimeMonth == level2ExceptionMonthes[j] || endDateTimeMonth == level2ExceptionMonthes[j]) {
							lowTmpResultTimesListResult.remove(i);
							break;
						}
					}
				}
			}
		}
		//合计
		List<LowTmpSequenceResult> lowTmpSequenceResult = calcSequence(lowTmpResultTimesListResult);
		lowTmpResult.setLowTmpResultTimesListResult(lowTmpResultTimesListResult);
		lowTmpResult.setLowTmpSequenceResult(lowTmpSequenceResult);
		return lowTmpResult;
	}
	
	private Map<String, List<LinkedHashMap>> chgHouList2HouMap(List<LinkedHashMap> houResultList) {
		Map<String, List<LinkedHashMap>> map = new HashMap<String, List<LinkedHashMap>>();
		for(int j = 0; j < houResultList.size(); j++) {
			LinkedHashMap houResultMap = houResultList.get(j);
			String station_Id_CItem = (String) houResultMap.get("Station_Id_C");
			int monthItem = (Integer) houResultMap.get("month");
			int houItem = (Integer) houResultMap.get("hou");
			String key = station_Id_CItem + "_" + monthItem + "_" + houItem;
			List<LinkedHashMap> list = map.get(key);
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(houResultMap);
			map.put(key, list);
		}
		return map;
	}
	
	/**
	 * 候低温统计
	 * @param lowTmpByRangeParam
	 * @return
	 */
	public List<LowTmpYearsResult> lowTmpByYears(LowTmpByRangeParam lowTmpByRangeParam) {
		constatStartYear = lowTmpByRangeParam.getConstatStartYear();
		constatEndYear = lowTmpByRangeParam.getConstatEndYear();
		int startYear = lowTmpByRangeParam.getStartYear();
		int endYear = lowTmpByRangeParam.getEndYear();
		List<LowTmpYearsResult> resultList = new ArrayList<LowTmpYearsResult>();
		//查询startYear到endYear之间的结果
		TimesRangeParam timesRangeParam = lowTmpByRangeParam.getTimesRangeParam();
//		int endYear = timesRangeParam.getEndYear();
		String startTimeStr = CommonTool.disposeHouStartTimes(timesRangeParam.getStartDate());
		String endTimeStr = CommonTool.disposeHouEndTimes(timesRangeParam.getEndDate());
		timesRangeParam.setStartTimeStr(startTimeStr);
		timesRangeParam.setEndTimeStr(endTimeStr);
		lowTmpByRangeParam.setTimesRangeParam(timesRangeParam);
		List<String> yearMonHouList = CommonTool.getYearMonHou(timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
		List<Integer> monthList = new ArrayList<Integer>();
		List<Integer> houList = new ArrayList<Integer>();
		
		Set<Integer> monthSet = new HashSet<Integer>();
		Set<Integer> houSet = new HashSet<Integer>();
		
		for(String yearMonHouItem : yearMonHouList) {
			String[] tempArray = yearMonHouItem.split("_");
			int year = Integer.parseInt(tempArray[0]);
			int month = Integer.parseInt(tempArray[1]);
			int hou = Integer.parseInt(tempArray[2]);
			monthSet.add(month);
			houSet.add(hou);
		}
		Iterator<Integer> monIt = monthSet.iterator();
		while(monIt.hasNext()) {
			monthList.add(monIt.next());
		}
		
		Iterator<Integer> houIt = houSet.iterator();
		while(houIt.hasNext()) {
			houList.add(houIt.next());
		}
		
		IHou houService = (IHou)ContextLoader.getCurrentWebApplicationContext().getBean("HouImpl");
		HashMap paramMap = new HashMap();
		int startMonth = 0, endMonth = 0, startHou = 0, endHou = 0;
		//保证能把常年值放进去
		if(startYear > constatStartYear) {
			paramMap.put("startYear", constatStartYear);
		} else {
			paramMap.put("startYear", startYear);
		}
		if(endYear < constatEndYear) {
			paramMap.put("endYear", constatEndYear);
		} else {
			paramMap.put("endYear", endYear);
		}
		
		paramMap.put("month", monthList);
		paramMap.put("hou", houList);
		String station_Id_Cs = lowTmpByRangeParam.getStation_Id_Cs();
		if(station_Id_Cs != null && !"".equals(station_Id_Cs)) {
			List<String> station_id_CList = new ArrayList<String>();
			String[] station_id_CArray = station_Id_Cs.split(",");
			for(int i = 0; i < station_id_CArray.length; i++) {
				station_id_CList.add(station_id_CArray[i]);
			}
			paramMap.put("station_Id_Cs", station_id_CList);
		}
		List<LinkedHashMap> houResultList = houService.queryHouTmpDataByYears(paramMap);
		Map<String, List<LinkedHashMap>> mapResult = chgHouList2HouMap(houResultList);
		//平均常年发生的严重次数
		double seriousnessSum = 0;
		//平均常年发生的一般次数
		double normalSum = 0;
		//平均常年总计发生次数
		double totalSum = 0;
		//统计到每一个年，历史中总计发生一般的次数
		HashMap<Integer, Integer> yearsSum = new HashMap<Integer, Integer>(); 
		HashMap<Integer, Integer> yearsNormalSum = new HashMap<Integer, Integer>(); 
		HashMap<Integer, Integer> yearsSeriousnessSum = new HashMap<Integer, Integer>(); 
		for(int i = (Integer) paramMap.get("startYear"); i <= (Integer) paramMap.get("endYear"); i++) {
			long start = System.currentTimeMillis();
			List<LowTmpResultTimes> lowTmpResultTimesList = disposeByYear(mapResult, houResultList, i, lowTmpByRangeParam);
			long start2 = System.currentTimeMillis();
//			System.out.println("disposeByYear时间【" + (start2 - start) + "】");
			for(int j=0; j<lowTmpResultTimesList.size(); j++) {
				LowTmpResultTimes lowTmpResultTimes = lowTmpResultTimesList.get(j);
				int year = lowTmpResultTimes.getYear();
				Integer yearCnt = yearsSum.get(year);
				if(yearCnt == null) {
					yearsSum.put(year, 1);
				} else {
					yearsSum.put(year, yearsSum.get(year) + 1);
				}
				String level = lowTmpResultTimes.getLevel();
				if("一般".equals(level)) {
					if(year >= constatStartYear && year <= constatEndYear) {
						normalSum ++;
					}
					Integer yearNormalCnt = yearsNormalSum.get(year);
					if(yearNormalCnt == null) {
						yearsNormalSum.put(year, 1);
					} else {
						yearsNormalSum.put(year, yearNormalCnt + 1);	
					}
				} else if("严重".equals(level)) {
					if(year >= constatStartYear && year <= constatEndYear) {
						seriousnessSum ++;
					}
					Integer yearsSeriousnessCnt = yearsSeriousnessSum.get(year);
					if(yearsSeriousnessCnt == null) {
						yearsSeriousnessSum.put(year, 1);
					} else {
						yearsSeriousnessSum.put(year, yearsSeriousnessCnt + 1);	
					}
				}
				if(year >= constatStartYear && year <= constatEndYear) {
					totalSum++;
				}
			}
			long end = System.currentTimeMillis();
//			System.out.println("一年循环时间：【" + (end - start) + "】");
		}
		seriousnessSum /= (constatEndYear - constatStartYear + 1); 
		seriousnessSum = CommonTool.roundDouble(seriousnessSum);
		normalSum /= (constatEndYear - constatStartYear + 1); 
		normalSum = CommonTool.roundDouble(normalSum);
		totalSum /= (constatEndYear - constatStartYear + 1); 
		totalSum = CommonTool.roundDouble(totalSum);
		for(int i = startYear; i <= endYear; i++) {
			LowTmpYearsResult lowTmpYearsResult = new LowTmpYearsResult();
			lowTmpYearsResult.setYear(i);
			lowTmpYearsResult.setSumYears(totalSum); //常年总次数
			lowTmpYearsResult.setNormalSumYears(normalSum); //程度一般出现总次数
			lowTmpYearsResult.setSeriousnessSumYears(seriousnessSum);//程度严重出现总次数
			if(yearsSum.get(i) != null) {
				lowTmpYearsResult.setSum(yearsSum.get(i));//指定年出现次数
				lowTmpYearsResult.setSumAnomalyRate(CommonTool.roundDouble((yearsSum.get(i) / (totalSum)) * 100)); // 总次数距平率
			}
			if(yearsNormalSum.get(i) != null) {
				lowTmpYearsResult.setNormalSum(yearsNormalSum.get(i)); // i年一般次数
				lowTmpYearsResult.setNormalAnomalyRate(CommonTool.roundDouble(yearsNormalSum.get(i) / (normalSum) * 100));//一般次数距平率
			}
			
			if(yearsSeriousnessSum.get(i) != null) {
				lowTmpYearsResult.setSeriousnessSum(yearsSeriousnessSum.get(i)); // i年严重出现次数
				lowTmpYearsResult.setSeriousnessAnomalyRate(CommonTool.roundDouble(yearsSeriousnessSum.get(i) / (seriousnessSum) * 100));//一般次数距平率
			}
			
			resultList.add(lowTmpYearsResult);
		}
		return resultList;
	}
	
	private List<LinkedHashMap> getResultByYear(List<LinkedHashMap> houList, int year) {
		List<LinkedHashMap> currentHouResultList = new ArrayList<LinkedHashMap>();
		for(LinkedHashMap item : houList) {
			int currentYear = (Integer) item.get("year");
			if(year == currentYear) {
				currentHouResultList.add(item);
			}
		}
		return currentHouResultList;
	}
	
	/**
	 * 按年份处理结果
	 * @param houResultList
	 * @param year
	 * @return
	 */
	private List<LowTmpResultTimes> disposeByYear(Map<String, List<LinkedHashMap>> houResultMap, List<LinkedHashMap> houResultList, int year, LowTmpByRangeParam lowTmpByRangeParam) {
//		long start = System.currentTimeMillis();
		List<LinkedHashMap> currentHouResultList = getResultByYear(houResultList, year);// houResultList  中按年份取到
//		long start2 = System.currentTimeMillis();
//		System.out.println("getResultByYear花费时间【" + (start2 - start) + "】");
		//对比结果 houResultList 和 currentHouResultList
		List<LowTmpResultHous> housList = new ArrayList<LowTmpResultHous>(); // 逐候结果
		HashMap<String, LowTmpResultHous> housMap = new HashMap<String, LowTmpResultHous>();
		for(int i=0; i<currentHouResultList.size(); i++) {
			long start1 = System.currentTimeMillis();
			LinkedHashMap currentHouResultMap = currentHouResultList.get(i);
			String station_Id_C = (String) currentHouResultMap.get("Station_Id_C");
			String station_Name = (String) currentHouResultMap.get("Station_Name");
			String area = (String) currentHouResultMap.get("area");
			LowTmpResultHous lowTmpResultHos = housMap.get(station_Id_C);
			if(lowTmpResultHos == null) {
				lowTmpResultHos = new LowTmpResultHous();
			}
			List<LowTmpResultHous.HouSequence> houSeqList = lowTmpResultHos.getList();
			if(houSeqList == null) {
				houSeqList = new ArrayList<LowTmpResultHous.HouSequence>();
			}
			LowTmpResultHous.HouSequence houSequence = lowTmpResultHos.new HouSequence();
			lowTmpResultHos.setStation_Id_C(station_Id_C);
			lowTmpResultHos.setStation_Name(station_Name);
			lowTmpResultHos.setArea(area);
//			int year = (Integer) currentHouResultMap.get("year");
			int month = (Integer) currentHouResultMap.get("month");
			int hou = (Integer) currentHouResultMap.get("hou");
			double avgTmp = (Double) currentHouResultMap.get("avgTmp");
			houSequence.setTime(year + "-" + month + "-" + hou);
			houSequence.setValue(avgTmp);
			long start2 = System.currentTimeMillis();
//			System.out.println("currentHouResultList循环时间【" + (start2 - start1) + "】");
			//距平值
			String key = station_Id_C + "_" + month + "_" + hou;
			List<LinkedHashMap> itemList = houResultMap.get(key);
			int anomalyCnt = 0;
			double anomalySum = 0;
			for(int j=0; j<itemList.size(); j++) {
				LinkedHashMap itemMap = itemList.get(j);
				Double avgTmpItem = (Double) itemMap.get("avgTmp");
				if(avgTmpItem != null) {
					anomalySum += avgTmpItem;
					anomalyCnt++;
				}
//				int itemYear = (Integer) itemMap.get("year");
//				if(year == itemYear) {
//					double avgTmpItem = (Double) itemMap.get("avgTmp");
//					double anomaly = CommonTool.roundDouble(avgTmp - avgTmpItem);
//					houSequence.setAnomaly(anomaly);
//					break;
//				}
			}
			double anomaly = CommonTool.roundDouble(avgTmp - anomalySum / anomalyCnt);
			houSequence.setAnomaly(anomaly);
//			for(int j = 0; j < houResultList.size(); j++) {
//				LinkedHashMap houResultMap = houResultList.get(j);
//				String station_Id_CItem = (String) houResultMap.get("Station_Id_C");
//				int monthItem = (Integer) houResultMap.get("month");
//				int houItem = (Integer) houResultMap.get("hou");
//				if(station_Id_C.equals(station_Id_CItem) && month == monthItem && hou == houItem) {
//					double avgTmpItem = (Double) houResultMap.get("avgTmp");
////					double anomaly = CommonTool.roundDouble(avgTmp - avgTmpItem);
//					double anomaly = avgTmp - avgTmpItem;
//					houSequence.setAnomaly(anomaly);
//					break;
//				}
//			}
			long start3 = System.currentTimeMillis();
//			System.out.println("houResultList循环时间【" + (start3 - start2) + "】");
			houSeqList.add(houSequence);
			lowTmpResultHos.setList(houSeqList);
			housMap.put(station_Id_C, lowTmpResultHos);
		}
//		long start3 = System.currentTimeMillis();
//		System.out.println("disposeByYear中循环时间【" + (start3 - start2) + "】");
		//
		Set<String> set = housMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			housList.add(housMap.get(it.next()));
		}
		//逐次
//		long start4 = System.currentTimeMillis();
		List<LowTmpResultTimes>  lowTmpResultTimesListResult = calcSeqhousMap(housMap, lowTmpByRangeParam);
//		long start5 = System.currentTimeMillis();
//		System.out.println("calcSeqhousMap花费时间：【" + (start5 - start4) + "】");
		return lowTmpResultTimesListResult;
	}
	/**
	 * 计算逐次
	 * @param housMap
	 * @return
	 */
	private List<LowTmpResultTimes> calcSeqhousMap(HashMap<String, LowTmpResultHous> housMap, LowTmpByRangeParam lowTmpByRangeParam) {
		HashMap<String, LowTmpResultHous> lowTmpResultHosMap = new HashMap<String, LowTmpResultHous>();
		// 1. 去掉不满足一般低温情况的数据
		Set<String> set = housMap.keySet();
		Iterator<String> it = set.iterator();
		//算合计
		while(it.hasNext()) {
			String station_Id_C = it.next();
			LowTmpResultHous lowTmpSourceHosItem = housMap.get(station_Id_C);
			LowTmpResultHous lowTmpResultHosItem = lowTmpResultHosMap.get(station_Id_C);
			if(lowTmpResultHosItem == null) {
				lowTmpResultHosItem = new LowTmpResultHous();
			}
			List<HouSequence> listHouSequence = lowTmpResultHosItem.getList();
			if(listHouSequence == null) {
				listHouSequence = new ArrayList<HouSequence>();
			}
			List<HouSequence> listHous = lowTmpSourceHosItem.getList();
			for(int i=0; i < listHous.size(); i++) {
				HouSequence houSequence1 = listHous.get(i);
				double anomaly1 = houSequence1.getAnomaly(); // -2.4
				double level1Tmp = lowTmpByRangeParam.getLevel1SequenceTmp(); // 1
				if(anomaly1 < 0 && Math.abs(anomaly1) > level1Tmp) {
					listHouSequence.add(houSequence1);
				}
			}
			lowTmpResultHosItem.setList(listHouSequence);
			lowTmpResultHosItem.setStation_Name(lowTmpSourceHosItem.getStation_Name());
			lowTmpResultHosItem.setArea(lowTmpSourceHosItem.getArea());
			lowTmpResultHosMap.put(station_Id_C, lowTmpResultHosItem);
		}
		// 2. 判断连续，判断一般低温的情况
		List<LowTmpResultTimes> resultList = new ArrayList<LowTmpResultTimes>();
		Set<String> set2 = lowTmpResultHosMap.keySet();
		Iterator<String> it2 = set2.iterator();
		while(it2.hasNext()) {
			String station_Id_C = it2.next();
			LowTmpResultHous lowTmpResultHosItem = lowTmpResultHosMap.get(station_Id_C);
			if(lowTmpResultHosItem == null) {
				continue;
			}
			String stationName = lowTmpResultHosItem.getStation_Name();
			String area = lowTmpResultHosItem.getArea();
			List<HouSequence> list =  lowTmpResultHosItem.getList();
			if(list == null) {
				continue;
			}
			int cnt = 1;
			String startTime = "", endTime = "";
			double tmp = 0, anomaly = 0;
			for(int i=0; i<list.size()-1; i++) {
				HouSequence item1 = list.get(i);
				HouSequence item2 = list.get(i+1);
				String time1 = item1.getTime();
				String time2 = item2.getTime();
				boolean flag = CommonTool.isHouSuccession(time1, time2);
				if(cnt == 1) {
					startTime = time1;
					tmp += item1.getValue();
					anomaly += item1.getAnomaly();
				}
				if(flag) {
					cnt++;
					endTime = time2;
					tmp += item2.getValue();
					anomaly += item2.getAnomaly();
				} 
				if(!flag || i == list.size() - 2)	{
					if(cnt >= lowTmpByRangeParam.getLevel1SequenceSeason()) {
						LowTmpResultTimes lowTmpResultTimes = new LowTmpResultTimes();
						lowTmpResultTimes.setPersistHous(cnt);
						lowTmpResultTimes.setStation_Id_C(station_Id_C);
						lowTmpResultTimes.setStation_Name(stationName);
						lowTmpResultTimes.setArea(area);
						//需要把候转换为具体时间
						String startDateTime = CommonTool.chgStartHouStr2Time(startTime);
						lowTmpResultTimes.setStartDatetime(startDateTime);
						lowTmpResultTimes.setYear(Integer.parseInt(startDateTime.substring(0, 4)));
						lowTmpResultTimes.setEndDatetime(CommonTool.chgEndHouStr2Time(endTime));
						lowTmpResultTimes.setAvgTmp(CommonTool.roundDouble(tmp / cnt));
						tmp = 0;
						lowTmpResultTimes.setAnomaly(CommonTool.roundDouble(anomaly / cnt));
						anomaly = 0;
						if(cnt >= lowTmpByRangeParam.getLevel2SequenceSeason()) {
							lowTmpResultTimes.setLevel("严重");
						} else {
							lowTmpResultTimes.setLevel("一般");
						}
						cnt = 1;
						resultList.add(lowTmpResultTimes);
					}
				}
			}
		}
		// 3. 在一般低温的情况下，判断严重低温的情况。
		return resultList;
	}
	
	/**
	 * 统计合计的情况
	 * @param resultList
	 * @return
	 */
	public List<LowTmpSequenceResult> calcSequence(List<LowTmpResultTimes> lowTmpResultTimesList) {
		StationArea stationArea = new StationArea();
		Map<String, String> stationMap = stationArea.getStationAreaMap();
		// 1. 遍历，取到每个站的合计，并且找打最严重的程度
		List<LowTmpSequenceResult> resultList = new ArrayList<LowTmpSequenceResult>();
		Map<String, ArrayList<LowTmpResultTimes>> map = new HashMap<String, ArrayList<LowTmpResultTimes>>();
		for(int i=0; i<lowTmpResultTimesList.size(); i++) {
			LowTmpResultTimes item = lowTmpResultTimesList.get(i);
			String station_id_C = item.getStation_Id_C();
			ArrayList<LowTmpResultTimes> list = null;
			if(map.containsKey(station_id_C)) {
				list = map.get(station_id_C);
			} else {
				list = new ArrayList<LowTmpResultTimes>();
			}
			list.add(item);
			map.put(station_id_C, list);
		}
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ArrayList<LowTmpResultTimes> list = map.get(station_Id_C);
			String maxLevel = "一般";
			for(int i=0; i<list.size(); i++) {
				LowTmpResultTimes item  = list.get(i);
				String level = item.getLevel();
				if("严重".equals(level)) {
					maxLevel = level;
					break;
				}
			}
			LowTmpSequenceResult lowTmpSequenceResult = new LowTmpSequenceResult();
			int cnt = 0;//出现次数
			for(int i=0; i<list.size(); i++) {
				LowTmpResultTimes item  = list.get(i);
				String level = item.getLevel();
				cnt++;
				if(maxLevel.equals(level)) {
					lowTmpSequenceResult.setLevel(maxLevel);
					Integer persistHos = item.getPersistHous();
					Integer existCnt = lowTmpSequenceResult.getCnt();
					lowTmpSequenceResult.setStation_Id_C(station_Id_C);
					lowTmpSequenceResult.setArea(item.getArea());
					lowTmpSequenceResult.setStation_Name(item.getStation_Name());
					if(existCnt == null || existCnt == 0) {
						lowTmpSequenceResult.setPersistHous(persistHos);
					} else if(existCnt < persistHos) {
						lowTmpSequenceResult.setPersistHous(persistHos);
					}
					lowTmpSequenceResult.setStartDatetime(item.getStartDatetime());
					lowTmpSequenceResult.setEndDatetime(item.getEndDatetime());
				}
			}
			lowTmpSequenceResult.setCnt(cnt);
			resultList.add(lowTmpSequenceResult);
		}
		return resultList;
	}
	
	
	/**
	 * 返回hashMap，key：候序，value：候的开始时间，候的结束时间
	 * @param startDate
	 * @param endDate
	 */
	private HashMap<Integer, Long[]> createHou(Date startDate, Date endDate) {
		HashMap<Integer, Long[]> map = new HashMap<Integer, Long[]>();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdfDD = new SimpleDateFormat("dd");
		int index = 0;
		SimpleDateFormat mmSDF = new SimpleDateFormat("MM");
		while(startTime < endTime) {
			Date tempStartDate = new Date(startTime);
			int day = Integer.parseInt(sdfDD.format(tempStartDate));
			if(day < 26 && day % 5 == 1) {
				map.put(index++, new Long[]{startTime, startTime + 4 * CommonConstant.DAYTIMES});
				startTime += 5 * CommonConstant.DAYTIMES;
			} else {
				// 进入下一个月
				int month = Integer.parseInt(mmSDF.format(startTime));
				String strYear = sdfYYYY.format(new Date(startTime));
				if(month != 12) {
					String strMon = mmSDF.format(new Date(startTime));
					try {
						Date date = sdf.parse(strYear + "-" + strMon + "-01");
						map.put(index++, new Long[]{startTime, date.getTime() - CommonConstant.DAYTIMES});
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				} else {
					Date date = null;
					try {
						date = sdf.parse((Integer.parseInt(strYear) + 1) + "-01-01");
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					map.put(index++, new Long[]{startTime, date.getTime() - CommonConstant.DAYTIMES});
				}
			}
		}
		return map;
	}
	
	/**
	 * 计算候数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private int calcHouSum(Date startDate, Date endDate) {
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		double cntDouble = (double) ((endTime - startTime) / CommonConstant.DAYTIMES);
		int cnt = (int) (cntDouble / 5 + (cntDouble % 5 > 0 ? 1 : 0));
		return cnt;
	}
}

