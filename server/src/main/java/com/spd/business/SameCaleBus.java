package com.spd.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonTable;
import com.spd.common.LinearByStationResult;
import com.spd.common.SameByStationResult;
import com.spd.common.SameCaleParam;
import com.spd.common.SameCaleResult;
import com.spd.common.Station;
import com.spd.common.TenYearsSameCaleResult;
import com.spd.pojo.StationValue;
import com.spd.pojo.StationYearValue;
import com.spd.service.ISameCale;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.util.CommonUtil;

public class SameCaleBus {
	
	public Object linearByStation(SameCaleParam sameCaleParam) {
		//1. 查询历年同期结果，标准年份结果
		boolean isOverYear = false;
		if(sameCaleParam.getStartMon() != null && sameCaleParam.getStartDay() != null && sameCaleParam.getEndMon() != null && sameCaleParam.getEndDay() != null) {
			isOverYear = CommonTool.isOverYear(sameCaleParam.getStartMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndMon(), sameCaleParam.getEndDay());
		}
		//格式：Station_Id_C_year:value
		Map<String, StationYearValue> standardDataMap = queryStandardDataByYears(sameCaleParam, isOverYear);
		Map<String, StationYearValue> dataMap = queryDataByYears(sameCaleParam, isOverYear);
		//2. 按照站号进行分组
		Map<String, Double> standardResultMap = new HashMap<String, Double>();
		Map<String, Integer> standardResultCntMap = new HashMap<String, Integer>();

		Iterator<String> standardIt = standardDataMap.keySet().iterator();
		while(standardIt.hasNext()) {
			String key = standardIt.next();
			String station_Id_C = key.split("_")[0];
			StationYearValue stationYearValue = standardDataMap.get(key);
			Double value = stationYearValue.getValue();
			Double standardValue = standardResultMap.get(station_Id_C);
			if(standardValue == null) {
				standardValue = 0.0;
			}
			standardValue += value;
			standardResultMap.put(station_Id_C, standardValue);
			Integer cnt = standardResultCntMap.get(station_Id_C);
			if(cnt == null) {
				cnt = 0;
			}
			cnt++;
			standardResultCntMap.put(station_Id_C, cnt);
		}
		//3. 这个是每个站的标准值
		List<LinearByStationResult> resultList = new ArrayList<LinearByStationResult>();
		Iterator<String> standardResultIt = standardResultMap.keySet().iterator();
		while(standardResultIt.hasNext()) {
			String key = standardResultIt.next();
			Double value = standardResultMap.get(key);
			standardResultMap.put(key, CommonTool.roundDouble(value / (standardResultCntMap.get(key))));
			LinearByStationResult linearByStationResult = new LinearByStationResult();
			linearByStationResult.setStation_Id_C(key);
			linearByStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
			int cnt = standardResultCntMap.get(key);
			linearByStationResult.setStandardValue(CommonTool.roundDouble(value /cnt));
			resultList.add(linearByStationResult);
		}
		//4. 遍历历年的值，按照站号进行分组
		Iterator<String> dataIt = dataMap.keySet().iterator();
		while(dataIt.hasNext()) {
			String key = dataIt.next();
			String station_Id_C = key.split("_")[0];
			Integer year = Integer.parseInt(key.split("_")[1]);
			StationYearValue stationYearValue = dataMap.get(key);
			for(int i = 0; i < resultList.size(); i++) {
				LinearByStationResult linearByStationResult = resultList.get(i);
				String itemStation_Id_C = linearByStationResult.getStation_Id_C();
				if(station_Id_C.equals(itemStation_Id_C)) {
					Map<String, Double> yearValuesMap = linearByStationResult.getYearValuesMap();
					if(yearValuesMap == null) {
						yearValuesMap = new HashMap<String, Double>();
					}
					yearValuesMap.put(year + "", stationYearValue.getValue());
					linearByStationResult.setYearValuesMap(yearValuesMap);
					break;
				}
			}
		}
		return resultList;
	}
	public Object sameByStation(SameCaleParam sameCaleParam) {
		//1. 查询历年同期结果，标准年份结果
		boolean isOverYear = false;
		if(sameCaleParam.getStartMon() != null && sameCaleParam.getStartDay() != null && sameCaleParam.getEndMon() != null && sameCaleParam.getEndDay() != null) {
			isOverYear = CommonTool.isOverYear(sameCaleParam.getStartMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndMon(), sameCaleParam.getEndDay());
		}
		//格式：Station_Id_C_year:value
		Map<String, StationYearValue> standardDataMap = queryStandardDataByYears(sameCaleParam, isOverYear);
		Map<String, StationYearValue> dataMap = queryDataByYears(sameCaleParam, isOverYear);
		//2. 按照站号进行分组
		Map<String, Double> standardResultMap = new HashMap<String, Double>();
		Map<String, Integer> standardResultCntMap = new HashMap<String, Integer>();
		Map<String, Double> dataResultMap = new HashMap<String, Double>();
		Map<String, Integer> dataResultCntMap = new HashMap<String, Integer>();
		Iterator<String> standardIt = standardDataMap.keySet().iterator();
		while(standardIt.hasNext()) {
			String key = standardIt.next();
			String station_Id_C = key.split("_")[0];
			StationYearValue stationYearValue = standardDataMap.get(key);
			Double value = stationYearValue.getValue();
			Double standardValue = standardResultMap.get(station_Id_C);
			if(standardValue == null) {
				standardValue = 0.0;
			}
			standardValue += value;
			standardResultMap.put(station_Id_C, standardValue);
			Integer cnt = standardResultCntMap.get(station_Id_C);
			if(cnt == null) {
				cnt = 0;
			}
			cnt++;
			standardResultCntMap.put(station_Id_C, cnt);
		}
		
		Iterator<String> dataIt = dataMap.keySet().iterator();
		while(dataIt.hasNext()) {
			String key = dataIt.next();
			String station_Id_C = key.split("_")[0];
			StationYearValue stationYearValue = dataMap.get(key);
			Double value = stationYearValue.getValue();
			if(value == null) {
				continue;
			}
			Double standardValue = dataResultMap.get(station_Id_C);
			if(standardValue == null) {
				standardValue = 0.0;
			}
			standardValue += value;
			dataResultMap.put(station_Id_C, standardValue);
			Integer cnt = dataResultCntMap.get(station_Id_C);
			if(cnt == null) {
				cnt = 0;
			}
			cnt++;
			dataResultCntMap.put(station_Id_C, cnt);
		}
		//3. 组织结果
		Iterator<String> standardResultIt = standardResultMap.keySet().iterator();
		while(standardResultIt.hasNext()) {
			String key = standardResultIt.next();
			Double value = standardResultMap.get(key);
			standardResultMap.put(key, CommonTool.roundDouble(value / (standardResultCntMap.get(key))));
		}
		
		Iterator<String> dataResultCntIt = dataResultMap.keySet().iterator();
		while(dataResultCntIt.hasNext()) {
			String key = dataResultCntIt.next();
			Double value = dataResultMap.get(key);
			dataResultMap.put(key, CommonTool.roundDouble(value / (dataResultCntMap.get(key))));
		}
		List<SameByStationResult> resultList = new ArrayList<SameByStationResult>();
		
		standardResultIt = standardResultMap.keySet().iterator();
		while(standardResultIt.hasNext()) {
			String key = standardResultIt.next();
			Double standardValue = standardResultMap.get(key);
			Double value = dataResultMap.get(key);
			SameByStationResult sameByStationResult = new SameByStationResult();
			sameByStationResult.setStation_Id_C(key);
			sameByStationResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
			sameByStationResult.setStandardValue(standardValue);
			sameByStationResult.setAvgValue(value);
			sameByStationResult.setAnomaly(CommonTool.roundDouble(standardValue - value));
			resultList.add(sameByStationResult);
		}
		return resultList;
	}
	
	/**
	 * 结果按照站进行分组
	 * @param sameCaleParam
	 * @return
	 */
	public Object groupByStationSame(SameCaleParam sameCaleParam) {
		//1. 计算结果
		boolean isOverYear = false;
		if(sameCaleParam.getStartMon() != null && sameCaleParam.getStartDay() != null && sameCaleParam.getEndMon() != null && sameCaleParam.getEndDay() != null) {
			isOverYear = CommonTool.isOverYear(sameCaleParam.getStartMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndMon(), sameCaleParam.getEndDay());
		}
		//格式：Station_Id_C_year:value
		Map<String, StationYearValue> standardDataMap = queryStandardDataByYears(sameCaleParam, isOverYear);
		Map<String, StationYearValue> dataMap = queryDataByYears(sameCaleParam, isOverYear);
		//2. 按站分组
		LinkedHashMap<String, Map<String, StationYearValue>> stationStandardDataMap = new LinkedHashMap<String, Map<String, StationYearValue>>();
		Map<String, Map<String, StationYearValue>> stationDataMap = new HashMap<String, Map<String, StationYearValue>>();
		//2.1 需要把结果的站号排序
		List<Station> awsStations = CommonUtil.getInstance().getAwsStations();
		for(int i = 0; i < awsStations.size(); i++) {
			Iterator<String> standardIt = standardDataMap.keySet().iterator();
			while(standardIt.hasNext()) {
				String key = standardIt.next();
				StationYearValue stationYearValue = standardDataMap.get(key);
				String station_Id_C = key.split("_")[0];
				if(station_Id_C.equals(awsStations.get(i).getStation_Id_C())) {
					Map<String, StationYearValue> itemMap = stationStandardDataMap.get(station_Id_C);
					if(itemMap == null) {
						itemMap = new HashMap<String, StationYearValue>();
					}
					itemMap.put(key, stationYearValue);
					stationStandardDataMap.put(station_Id_C, itemMap);
					break;
				}
			}
		}
		
		
		Iterator<String> dataIt = dataMap.keySet().iterator();
		while(dataIt.hasNext()) {
			String key = dataIt.next();
			StationYearValue stationYearValue = dataMap.get(key);
			String station_Id_C = key.split("_")[0];
			Map<String, StationYearValue> itemMap = stationDataMap.get(station_Id_C);
			if(itemMap == null) {
				itemMap = new HashMap<String, StationYearValue>();
			}
			itemMap.put(key, stationYearValue);
			stationDataMap.put(station_Id_C, itemMap);
		}
		//3. 把按站分组的结果分别计算
		LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
		Iterator<String> stationStandardIt = stationStandardDataMap.keySet().iterator();
		while(stationStandardIt.hasNext()) {
			String key = stationStandardIt.next();
			Map<String, StationYearValue> itemStandardDataMap = stationStandardDataMap.get(key);
			Map<String, StationYearValue> itemDataMap = stationDataMap.get(key);
			if(itemStandardDataMap != null && itemDataMap != null) {
				Object itemResult = caleData(itemStandardDataMap, itemDataMap, sameCaleParam, isOverYear);
				resultMap.put(key, itemResult);
			}
		}
		//4. 整理按站分组的结果
		List<LinkedHashMap> resultList = new ArrayList<LinkedHashMap>();
		
		//4.1 初始化结果的List
//		for(int i = sameCaleParam.getStartYear(); i <= sameCaleParam.getEndYear(); i++) {
//			linkedHashMap.put(i, new ArrayList());
//		}
		LinkedHashMap<String, LinkedHashMap> linkedHashMap = new LinkedHashMap<String, LinkedHashMap>();
//		List groupResultList = new ArrayList();
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<StationValue> stationValueList = (ArrayList<StationValue>) resultMap.get(key);
			for(int i = 0; i < stationValueList.size(); i++) {
				StationValue stationValue = stationValueList.get(i);
				Integer year = stationValue.getYear();
				LinkedHashMap itemMap = linkedHashMap.get(year + "");
				if(itemMap == null) {
					itemMap = new LinkedHashMap();
				}
				String station_Id_C = key;
				String station_Name = CommonUtil.getInstance().stationNameMap.get(station_Id_C);
				Double value = stationValue.getValue();
				itemMap.put(station_Id_C + "_" + station_Name, value);
//				itemList.add(itemMap);
				linkedHashMap.put(year + "", itemMap);
			}
//			groupResultList.add(linkedHashMap);
		}
		return linkedHashMap;
	}
	
	public Object same(SameCaleParam sameCaleParam) {
		boolean isOverYear = false;
		if(sameCaleParam.getStartMon() != null && sameCaleParam.getStartDay() != null && sameCaleParam.getEndMon() != null && sameCaleParam.getEndDay() != null) {
			isOverYear = CommonTool.isOverYear(sameCaleParam.getStartMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndMon(), sameCaleParam.getEndDay());
		}
		// 1. 计算标准值年份
		Map<String, StationYearValue> standardDataMap = queryStandardDataByYears(sameCaleParam, isOverYear);
		Map<String, StationYearValue> dataMap = queryDataByYears(sameCaleParam, isOverYear);
		Object result = caleData(standardDataMap, dataMap, sameCaleParam, isOverYear);
		return result;
	}
	
	public Object caleData(Map<String, StationYearValue> standardDataMap, Map<String, StationYearValue>  dataMap, SameCaleParam sameCaleParam, boolean isOverYear) {
		// 2. 计算比较年份
		List<SameCaleResult> resultList = dispose(standardDataMap, dataMap, sameCaleParam);
		// 3. 把结果做对比，构造结果进行展示。
//		sortByYear(resultList);
		// 4. 如果跨年，把超过 endYear的删除
		for(int i=resultList.size() - 1; i>0; i--) {
			SameCaleResult sameCaleResult = resultList.get(i);
			if(sameCaleResult.getYear() > sameCaleParam.getEndYear()) {
				resultList.remove(i);
			}
		}
		if(isOverYear) {
			for(int i=0; i<resultList.size(); i++) {
				SameCaleResult sameCaleResult = resultList.get(i);
				if(sameCaleResult.getYear() < sameCaleParam.getStartYear()) {
					resultList.remove(i);
				}
			}
		}
		if(sameCaleParam.getResultDisplayType() == 1) {
			return resultList;
		} else if(sameCaleParam.getResultDisplayType() == 2) {
			//处理成年代的结果
			List<TenYearsSameCaleResult> tenYearsResult = new ArrayList<TenYearsSameCaleResult>();
			LinkedHashMap<String, int[]> tenYearMap = new LinkedHashMap<String, int[]>(); //定义年代，和与之对应的开始，结束年份
			int i = 1951;
			while(i <= sameCaleParam.getEndYear()) {
				String key = (i - 1) + "年代";
				if(i + 9 >= sameCaleParam.getEndYear()) {
					tenYearMap.put(key, new int[]{i, sameCaleParam.getEndYear()});
				} else {
					tenYearMap.put(key, new int[]{i, i + 9});
				}
				i += 10;
			}
			Iterator it = tenYearMap.keySet().iterator();
			while(it.hasNext()) {
				TenYearsSameCaleResult tenYearsSameCaleResult = new TenYearsSameCaleResult();
				String key = (String) it.next();
				tenYearsSameCaleResult.setYearsStr(key);
				int[] years = tenYearMap.get(key);
				tenYearsSameCaleResult.setYear(years[0]);
				Double value = 0.0, avgValue = 0.0;
				int cnt = 0;
				for(int j = 0; j < resultList.size(); j++) {
					SameCaleResult itemSameCaleResult = resultList.get(j);
					int year = itemSameCaleResult.getYear();
					if(year >= years[0] && year <= years[1]) {
						value += itemSameCaleResult.getValue();
						avgValue = itemSameCaleResult.getAvgValue();
						cnt++;
					}
				}
				value = CommonTool.roundDouble2(value / cnt);
				avgValue = CommonTool.roundDouble2(avgValue);
				tenYearsSameCaleResult.setValue(CommonTool.roundDouble(value));
				tenYearsSameCaleResult.setAvgValue(CommonTool.roundDouble(avgValue));
				Double anomaly = CommonTool.roundDouble2(value - avgValue);
				tenYearsSameCaleResult.setAnomaly(anomaly);
				tenYearsSameCaleResult.setAnomalyRate(CommonTool.roundDouble2(anomaly / avgValue * 100.0));
				tenYearsResult.add(tenYearsSameCaleResult);
			}
			return tenYearsResult;
		}
		return null;
	}
	
	
	
	private List<SameCaleResult> dispose(Map<String, StationYearValue> standardDataMap, Map<String, StationYearValue> dataMap, SameCaleParam sameCaleParam) {
		String statisticsType = sameCaleParam.getStatisticsType();
		List<SameCaleResult> sameCaleResultList = new ArrayList<SameCaleResult>();
		//最大，最小都处理成平均
		Set<String> standardSet = standardDataMap.keySet();
		int standardCnt = standardSet.size();
		if(standardCnt == 0) return sameCaleResultList;
		double sum = 0;
		double days = 0;
		Iterator<String> standardIt = standardSet.iterator();
		while(standardIt.hasNext()) {
			String key = standardIt.next();
			StationYearValue stationYearValue = standardDataMap.get(key);
			Double value = stationYearValue.getValue();
			Integer daysItem = stationYearValue.getDays();
			if(value != null) {
				sum += value;
			}
			if(daysItem != null) {
				days += daysItem;
			}
		}
		
		// 计算出多年均值
		sum /= standardCnt;
		days /= standardCnt;
		// key : station_year
		Set<String> set = dataMap.keySet();
		Iterator<String> it = set.iterator();
		// key year, value :value
		Map<Integer, Double> tempMap = new HashMap<Integer, Double>();
		Map<Integer, Integer> cntMap = new HashMap<Integer, Integer>();
		while(it.hasNext()) {
			String  key = it.next();
			StationYearValue stationYearValue = dataMap.get(key);
			if(stationYearValue == null) {
				continue;
			}
			Integer year = Integer.parseInt(key.split("_")[1]);
			Double value = tempMap.get(year);
			if(value == null) {
				value = 0.0;
			}
			Integer cnt = cntMap.get(year);
			if(cnt == null) {
				cnt = 0;
			} 
			if(statisticsType.equals("DAYS")) {
				Integer tempDays = stationYearValue.getDays();
				if(tempDays != null) {
					value += tempDays;
				}
			} else {
				Double tempValue = stationYearValue.getValue();
				if(tempValue != null) {
					value += tempValue;
				}
			}
			tempMap.put(year, value);
			cntMap.put(year, ++cnt);
		}
		
		Set<Integer> tempSet = tempMap.keySet();
		Iterator<Integer> tempIt = tempSet.iterator();
		while(tempIt.hasNext()) {
			Integer tempYear = tempIt.next();
			double tempValue = tempMap.get(tempYear);
			tempMap.put(tempYear, tempValue / cntMap.get(tempYear));
		}
		sameCaleResultList = compare(tempMap, sum, days, statisticsType, sameCaleParam);
		return sameCaleResultList;
	}
	/**
	 * 按年份进行排序
	 * @param sameCaleResultList
	 * @return
	 */
	private void sortByYear(List<SameCaleResult> sameCaleResultList) {
		if(sameCaleResultList == null) {
			return;
		}
		int index = 0;
		for(int i=0; i<sameCaleResultList.size()-1; i++) {
			int iYear = sameCaleResultList.get(i).getYear();
			for(int j=i+1; j<sameCaleResultList.size(); j++) {
				int jYear = sameCaleResultList.get(j).getYear();
				if(jYear < iYear) {
					iYear = jYear;
					index = j;
				}
			}
			// i j 交换
			SameCaleResult tempSameCaleResult = sameCaleResultList.get(i);
			sameCaleResultList.set(i, sameCaleResultList.get(index));
			sameCaleResultList.set(index, tempSameCaleResult);
			
		}
	}
	
	private List<SameCaleResult> compare(Map<Integer, Double> map, double sum, double days, String statisticsType, SameCaleParam sameCaleParam) {
		List<SameCaleResult> sameCaleResultList = new ArrayList<SameCaleResult>();
		for(int i = sameCaleParam.getStartYear(); i <= sameCaleParam.getEndYear(); i++) {
			int year = i;
			double value = 0;
			if(map.containsKey(i)) {
				value = map.get(year);
				value = CommonTool.roundDouble2(value);
				sum = CommonTool.roundDouble2(sum);
				SameCaleResult sameCaleResult = new SameCaleResult();
				sameCaleResult.setStation_Id_C(sameCaleParam.getStation_ID_C());
				sameCaleResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(sameCaleParam.getStation_ID_C()));
				if(statisticsType.equals("DAYS")) {
					sameCaleResult.setAvgValue(days);
					sameCaleResult.setAnomaly(CommonTool.roundDouble2(value - days));
					if(days == 0) {
						sameCaleResult.setAnomalyRate(0.0);
					} else {
						sameCaleResult.setAnomalyRate(CommonTool.roundDouble2((value - days) / days * 100));
					}
				} else {
					sameCaleResult.setAvgValue(CommonTool.roundDouble(sum));
					sameCaleResult.setAnomaly(CommonTool.roundDouble2(value - sum));
					if(sum == 0.0) {
						sameCaleResult.setAnomalyRate(0.0);
					} else {
						sameCaleResult.setAnomalyRate(CommonTool.roundDouble2((value - sum) / sum * 100));
					}
				}
				sameCaleResult.setValue(CommonTool.roundDouble(value));
				sameCaleResult.setYear(year);
				sameCaleResultList.add(sameCaleResult);
			}
		}
		return sameCaleResultList;
	}
	
	/**
	 * 根据年份查询标准值相关数据， 要考虑跨年的问题。
	 * @param sameCaleParam
	 * @return
	 */
	private Map<String, StationYearValue> queryStandardDataByYears(SameCaleParam sameCaleParam, boolean isOverYear) {
		HashMap paramMap = new HashMap();
		String items = "";
		if(isOverYear) {
			items = CommonTool.createItemStrByTimes(1999, 2000,
					sameCaleParam.getStartMon(), sameCaleParam.getEndMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndDay());
		} else if(sameCaleParam.getStartMon() != null && sameCaleParam.getEndMon() != null
				&& sameCaleParam.getStartDay() != null && sameCaleParam.getEndDay() != null){
			items = CommonTool.createItemStrByTimes(2000, 2000,
				sameCaleParam.getStartMon(), sameCaleParam.getEndMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndDay());
		} else if(sameCaleParam.getMonthes() != null) {
			items = CommonTool.createItemsByMonthes(sameCaleParam.getMonthes());
		}
		paramMap.put("tableName", sameCaleParam.getTableName());
		paramMap.put("items", items);
		if(isOverYear) {
			paramMap.put("startYear", sameCaleParam.getStandardStartYear() - 1);
			paramMap.put("endYear", sameCaleParam.getStandardEndYear());
		} else {
			paramMap.put("startYear", sameCaleParam.getStandardStartYear());
			paramMap.put("endYear", sameCaleParam.getStandardEndYear());
		}
		
		String station_ID_C = sameCaleParam.getStation_ID_C();
		ISameCale rank = (ISameCale)ContextLoader.getCurrentWebApplicationContext().getBean("SameCaleImpl");
		List<Map> resultList = null;
		if("*".equals(station_ID_C)) {
			resultList = rank.queryAllEle(paramMap);
		} else if("5%".equals(station_ID_C)){
			resultList = rank.queryAWSEle(paramMap);
		} else {
			paramMap.put("stations", station_ID_C);
			resultList = rank.queryEleByStations(paramMap);
		}
		//TODO 跨年的问题
		Map<String, StationYearValue> mapData = disposeData(resultList, sameCaleParam, items.length(), isOverYear);
		return mapData;
	}
	
	/**
	 * 根据年份查询对应要素的值
	 * @param sameCaleParam
	 * @return
	 */
	private Map<String, StationYearValue> queryDataByYears(SameCaleParam sameCaleParam, boolean isOverYear) {
		HashMap paramMap = new HashMap();
		String items = "";
		if(isOverYear) {
			items = CommonTool.createItemStrByTimes(1999, 2000,
					sameCaleParam.getStartMon(), sameCaleParam.getEndMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndDay());
		} else if(sameCaleParam.getStartMon() != null && sameCaleParam.getEndMon() != null
				&& sameCaleParam.getStartDay() != null && sameCaleParam.getEndDay() != null){
			items = CommonTool.createItemStrByTimes(2000, 2000,
				sameCaleParam.getStartMon(), sameCaleParam.getEndMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndDay());
		} else if(sameCaleParam.getMonthes() != null) {
			items = CommonTool.createItemsByMonthes(sameCaleParam.getMonthes());
		}
		paramMap.put("tableName", sameCaleParam.getTableName());
		paramMap.put("items", items);
		if(isOverYear) {
			paramMap.put("startYear", sameCaleParam.getStartYear() - 1);
			paramMap.put("endYear", sameCaleParam.getEndYear());
		} else {
			paramMap.put("startYear", sameCaleParam.getStartYear());
			paramMap.put("endYear", sameCaleParam.getEndYear());
		}
		String station_ID_C = sameCaleParam.getStation_ID_C();
		ISameCale rank = (ISameCale)ContextLoader.getCurrentWebApplicationContext().getBean("SameCaleImpl");
		List<Map> resultList = null;
		if("*".equals(station_ID_C)) {
			resultList = rank.queryAllEle(paramMap);
		} else if("5%".equals(station_ID_C)){
			resultList = rank.queryAWSEle(paramMap);
		} else {
			paramMap.put("stations", station_ID_C);
			resultList = rank.queryEleByStations(paramMap);
		}
		//TODO 跨年的问题
		Map<String, StationYearValue> mapData = disposeData(resultList, sameCaleParam, items.length(), isOverYear);
		return mapData;
	}
	
	
	/**
	 * 处理查询的结果，对多年均值做处理。
	 * @param list
	 * @return
	 */
	private Map<String, StationYearValue> disposeData(List<Map> resultList, SameCaleParam sameCaleParam, int itemLength, boolean isOverYear) {
		// 1. 计算出每年，每站的值
		// 2. 根据每年的站，然后求每年的值
		// 遍历计算
		String tableName = sameCaleParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, StationYearValue> stationYearValueMap = new HashMap<String, StationYearValue>();
		for(Map map : resultList) {
			boolean missing = CommonTool.missingObservData(map, sameCaleParam.getMissingRatio(), itemLength, tableName);
			if(missing) {
				continue;
			}
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer)map.get("year");
			StationYearValue stationYearValue = stationYearValueMap.get(station_Id_C + "_" + year);
			if(stationYearValue == null) {
				stationYearValue = new StationYearValue();
				stationYearValue.setStation_Id_C(station_Id_C);
				stationYearValue.setYear(year);
				stationYearValue.setStation_Name(station_Name);
				stationYearValueMap.put(station_Id_C + "_" + year, stationYearValue);
			}
			// 冬季算到下一年
			StationYearValue stationNextYearValue = stationYearValueMap.get(station_Id_C + "_" + (year + 1));
			if(stationNextYearValue == null) {
				stationNextYearValue = new StationYearValue();
				stationNextYearValue.setStation_Id_C(station_Id_C);
				stationNextYearValue.setYear(year + 1);
				stationNextYearValue.setStation_Name(station_Name);
				if(isOverYear) {
					stationYearValueMap.put(station_Id_C + "_" + (year  + 1), stationNextYearValue);
				}
			}
			Set set = map.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					boolean isFilter = CommonTool.filter(sameCaleParam.getFilterType(), sameCaleParam.getMax(), sameCaleParam.getMin(), sameCaleParam.getContrast(), value);
					if(isFilter) {
						continue;
					}
					value = Eigenvalue.dispose(value);
					if(isOverYear) {
						boolean boolYear = CommonTool.isCurTimeOverYear(Integer.parseInt(key.substring(1, 3)), Integer.parseInt(key.substring(4, 6)),
								sameCaleParam.getStartMon(), sameCaleParam.getEndMon(), sameCaleParam.getStartDay(), sameCaleParam.getEndDay());
						//引用传递
						if(boolYear) {
							CommonTool.statistics(sameCaleParam.getStatisticsType(), year, stationYearValue, value, key);
						} else {
							CommonTool.statistics(sameCaleParam.getStatisticsType(), year + 1, stationNextYearValue, value, key);
						}
					} else {
						CommonTool.statistics(sameCaleParam.getStatisticsType(), year, stationYearValue, value, key);
					}
				}
			}
		}
		CommonTool.disposeResult(sameCaleParam.getStatisticsType(), stationYearValueMap);
		//TODO 跨年的问题
		return stationYearValueMap;
	}
}
