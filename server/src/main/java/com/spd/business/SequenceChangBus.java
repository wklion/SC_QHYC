package com.spd.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.ClimTime;
import com.spd.common.ClimTimeType;
import com.spd.common.CommonConstant;
import com.spd.common.EleTypes;
import com.spd.common.SequenceChangeParam;
import com.spd.common.SequenceChangeResult;
import com.spd.common.SequenceChangeYearResult;
import com.spd.common.StatisticsTypes;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.util.CommonUtil;

/**
 * 连续变化
 * @author Administrator
 *
 */
public class SequenceChangBus {

	public List<SequenceChangeYearResult> sequenceChangeStationsByTimes(SequenceChangeParam sequenceChangeParam) {
		//1. 查询时段结果
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(sequenceChangeParam.getStatisticsType());
		//按照当前时间段进行查询
		TimesParam timesParam = sequenceChangeParam.getTimesParam();
		String climTimeTypeStr = sequenceChangeParam.getClimTimeType();
		ClimTimeType climTimeType = ClimTimeType.getClimTimeType(climTimeTypeStr);
		TimesRangeParam timesRangeParam = new TimesRangeParam(); 
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		DBTable dbTable = new DBTable();
		String eleTypesStr = sequenceChangeParam.getEleTypes();
		String tableName = EleTypes.getTableName(eleTypesStr);
		dbTable.queryDataByRangeTimes(timesRangeParam, sequenceChangeParam.getStation_Id_C(), tableName);
		//结果序列
		List<SequenceTimeValue> currentTimeList = dbTable.getSequenceTimeValueList();
		//2. 查询历年同期结果
		boolean isMoreThan1Year = CommonTool.isMoreThan1Year(timesRangeParam);
		TimesYearsParam timesYearsParam = null;
		if(isMoreThan1Year) {
			timesYearsParam = new TimesYearsParam(1, 1, 12, 31, sequenceChangeParam.getStandardStartYear(), sequenceChangeParam.getStandardEndYear());
		} else {
			timesYearsParam = new TimesYearsParam(timesParam.getStartMon(), timesParam.getStartDay(), 
					timesParam.getEndMon(), timesParam.getEndDay(), sequenceChangeParam.getStandardStartYear(), sequenceChangeParam.getStandardEndYear());
		}
		
		dbTable.queryDataByYears(timesYearsParam, sequenceChangeParam.getStation_Id_C(), tableName);
		List<SequenceTimeValue> yearsList = dbTable.getSequenceTimeValueList();
		//3. 统计当年统计结果
		List<SequenceChangeYearResult> currentSquenceChangYearResultList = analystCurrent(currentTimeList, timesParam, climTimeType, statisticsTypes, 1);
//		Map<String, Map<String, Double>> currentResultMap = anlystCurrent(currentTimeList, timesParam, climTimeType, statisticsTypes);
		//4. 统计历年统计结果
		List<SequenceChangeYearResult> yearsSquenceChangYearResultList = analystCurrent(yearsList, timesParam, climTimeType, statisticsTypes,
				(sequenceChangeParam.getStandardEndYear() - sequenceChangeParam.getStandardStartYear() + 1));
//		Map<String, Double> yearsResultMap = analystYear(yearsList, sequenceChangeParam.getTimesParam().getStartDate(), sequenceChangeParam.getTimesParam().getEndDate(), statisticsTypes);
		//5. 统计结果
		compare(currentSquenceChangYearResultList,yearsSquenceChangYearResultList); 
		return currentSquenceChangYearResultList;
	}
	
	private void compare(List<SequenceChangeYearResult> currentSquenceChangYearResultList, List<SequenceChangeYearResult> yearsSquenceChangYearResultList) {
		for(int i = 0; i < currentSquenceChangYearResultList.size(); i++) {
			SequenceChangeYearResult iSequenceChangeYearResult = currentSquenceChangYearResultList.get(i);
			String iStation_id_C = iSequenceChangeYearResult.getStation_Id_C();
			String iDatetime = iSequenceChangeYearResult.getDatetime();
			for(int j = 0; j < yearsSquenceChangYearResultList.size(); j++) {
				SequenceChangeYearResult jSequenceChangeYearResult = yearsSquenceChangYearResultList.get(j);
				String jStation_id_C = jSequenceChangeYearResult.getStation_Id_C();
				String jDatetime = jSequenceChangeYearResult.getDatetime();
				Double jValue = jSequenceChangeYearResult.getValue();
				if(iStation_id_C.equals(jStation_id_C) && iDatetime.equals(jDatetime)) {
					iSequenceChangeYearResult.setYearsValue(jValue);
				}
			}
		}
	}
	
	private List<SequenceChangeYearResult> analystCurrent(List<SequenceTimeValue> currentTimeList, TimesParam timesParam, 
			ClimTimeType climTimeType, StatisticsTypes statisticsTypes, int years) {
		List<SequenceChangeYearResult> sequenceChangeYearResultList = new ArrayList<SequenceChangeYearResult>();
		//1. 计算日期的分段
		List<ClimTime> climTimeList = ClimTime.getClimTimeByTimes(timesParam.getStartDate(), timesParam.getEndDate(), climTimeType);
		//2. 遍历分段，找到每一段的开始、结束时间，计算结果
		for(int i = 0; i < climTimeList.size(); i++) {
			ClimTime climTime = climTimeList.get(i);
			Date startDate = climTime.getStartDate();
			Date endDate = climTime.getEndDate();
			HashMap<String, Double> stationValueMap = new HashMap<String, Double>(); //站号，值，在指定的时间里
			HashMap<String, Integer> stationCntMap = new HashMap<String, Integer>(); //站号，计数，在指定的时间里
			for(int j = 0; j < currentTimeList.size(); j++) {
				SequenceTimeValue sequenceTimeValue = currentTimeList.get(j);
				String station_Id_C = sequenceTimeValue.getStation_Id_C();
				List<TimeValue> timevalueList = sequenceTimeValue.getTimeValues();
				Double sum = 0.0;
				int cnt = 0;
				for(int k = 0; k < timevalueList.size(); k++) {
					TimeValue timeValue = timevalueList.get(k);
					Date date = timeValue.getDate();
					int compareResult = CommonTool.currentTimeInRange(startDate, endDate, date);
					if(compareResult == 0) {
						Double value = timeValue.getValue();
						sum += value;
						cnt ++;
					}
					if(compareResult == 1) {
						break;
					}
				}
				if(stationValueMap.get(station_Id_C) != null) {
					stationValueMap.put(station_Id_C, sum + stationValueMap.get(station_Id_C));
				} else {
					stationValueMap.put(station_Id_C, sum);
				}
				if(stationCntMap.get(station_Id_C) != null) {
					stationCntMap.put(station_Id_C, cnt + stationCntMap.get(station_Id_C));
				} else {
					stationCntMap.put(station_Id_C, cnt);
				}
			}
				//处理结果
			switch(statisticsTypes) {
				case AVG:
//					stationValueMap.put(station_Id_C, sum / cnt);
					Iterator<String> it = stationValueMap.keySet().iterator();
					while(it.hasNext()) {
						String station_id_C = it.next();
						Double value = stationValueMap.get(station_id_C);
						Integer cnt = stationCntMap.get(station_id_C);
						if(cnt == 0) continue;
						stationValueMap.put(station_id_C, CommonTool.roundDouble(value / cnt));
					}
					break;
				case SUM:
					Iterator<String> it2 = stationValueMap.keySet().iterator();
					while(it2.hasNext()) {
						String station_id_C = it2.next();
						Double value = stationValueMap.get(station_id_C);
						Integer cnt = stationCntMap.get(station_id_C);
						if(cnt == 0) continue;
						stationValueMap.put(station_id_C, CommonTool.roundDouble(value / years));
					}
					break;
				default:
					break;
			}
			//构造结果对象
			Iterator<String> it = stationValueMap.keySet().iterator();
			while(it.hasNext()) {
				String station_Id_C = it.next();
				SequenceChangeYearResult sequenceChangeYearResult = new SequenceChangeYearResult(); 
				sequenceChangeYearResult.setStation_Id_C(station_Id_C);
				sequenceChangeYearResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				sequenceChangeYearResult.setStartDate(startDate);
				sequenceChangeYearResult.setEndDate(endDate);
				Double value = stationValueMap.get(station_Id_C);
				sequenceChangeYearResult.setValue(value);
				sequenceChangeYearResult.setDatetime(climTime.getClimTimeStr());
				sequenceChangeYearResultList.add(sequenceChangeYearResult);
			}
		}
		//4. 保存结果
		return sequenceChangeYearResultList;
	}
	
	
	/**
	 * 连续变化
	 * @param sequenceChangeParam
	 * @return
	 */
	public List<SequenceChangeResult> sequenceChangByTimes(SequenceChangeParam sequenceChangeParam) {
		String paramStation_Id_C = sequenceChangeParam.getStation_Id_C();
		List<SequenceChangeResult> resultList = new ArrayList<SequenceChangeResult>();
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(sequenceChangeParam.getStatisticsType());
		//按照当前时间段进行查询
		TimesParam timesParam = sequenceChangeParam.getTimesParam();
		String climTimeTypeStr = sequenceChangeParam.getClimTimeType();
		ClimTimeType climTimeType = ClimTimeType.getClimTimeType(climTimeTypeStr);
		TimesRangeParam timesRangeParam = new TimesRangeParam(); 
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		DBTable dbTable = new DBTable();
		String eleTypesStr = sequenceChangeParam.getEleTypes();
		String tableName = EleTypes.getTableName(eleTypesStr);
		dbTable.queryDataByRangeTimes(timesRangeParam, sequenceChangeParam.getStation_Id_C(), tableName);
		//结果序列
		List<SequenceTimeValue> currentTimeList = dbTable.getSequenceTimeValueList();
		//查询历年结果
		//查询历年
		boolean isMoreThan1Year = CommonTool.isMoreThan1Year(timesRangeParam);
		TimesYearsParam timesYearsParam = null;
		if(isMoreThan1Year) {
			timesYearsParam = new TimesYearsParam(1, 1, 12, 31, sequenceChangeParam.getStandardStartYear(), sequenceChangeParam.getStandardEndYear());
		} else {
			timesYearsParam = new TimesYearsParam(timesParam.getStartMon(), timesParam.getStartDay(), 
					timesParam.getEndMon(), timesParam.getEndDay(), sequenceChangeParam.getStandardStartYear(), sequenceChangeParam.getStandardEndYear());
		}
		
		dbTable.queryDataByYears(timesYearsParam, sequenceChangeParam.getStation_Id_C(), tableName);
		List<SequenceTimeValue> yearsList = dbTable.getSequenceTimeValueList();
		//计算站点数
		if(yearsList == null) return null;
		int stationCnt = yearsList.size() / (sequenceChangeParam.getStandardEndYear() - sequenceChangeParam.getStandardStartYear() + 1);
		//统计对比结果
		List<ClimTime> climTimeList = ClimTime.getClimTimeByTimes(timesParam.getStartDate(), timesParam.getEndDate(), climTimeType);
		// 计算当年指定时间段的
		for(int i = 0; i < climTimeList.size(); i++) {
			Double maxValue = CommonConstant.MINDOUBLE, minValue = Double.MAX_VALUE;
			String maxStation_Name = "", minStation_Name = "";
			SequenceChangeResult sequenceChangeResult = new SequenceChangeResult();
			ClimTime climTime = climTimeList.get(i);
			Date startDate = climTime.getStartDate();
			Date endDate = climTime.getEndDate();
			String climTimeStr = climTime.getClimTimeStr();
			sequenceChangeResult.setDatetime(climTimeStr);
			sequenceChangeResult.setStartDate(startDate);
			sequenceChangeResult.setEndDate(endDate);
			
			Double resultValue = 0.0, compareMinValue = Double.MAX_VALUE, compareMaxValue = CommonConstant.MINDOUBLE;
			int cnt = 0;
			for(int j = 0; j < currentTimeList.size(); j++) {
				SequenceTimeValue sequenceTimeValue = currentTimeList.get(j);
				String station_Id_C = sequenceTimeValue.getStation_Id_C();
				String station_Name = CommonUtil.getInstance().stationNameMap.get(station_Id_C);
				List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
				for(int k = 0; k < timeValues.size(); k++) {
					TimeValue timeValue = timeValues.get(k);
					Date date = timeValue.getDate();
					Double value = timeValue.getValue();
					value = Eigenvalue.dispose(value);
					if(date.getTime() > endDate.getTime()) {
						break;
					}
					if(date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
						switch(statisticsTypes) {
							case AVG:
								resultValue += value;
								cnt ++;
								break;
							case SUM:
								resultValue += value;
								break;
							case MAX:
								if(value > compareMaxValue) {
									compareMaxValue = value;
								}
								break;
							case MIN:
								if(value < compareMinValue) {
									compareMinValue = value;
								}
								break;
							default:
								break;
						}
						
						if(paramStation_Id_C.equals("5%") || paramStation_Id_C.equals("*") || paramStation_Id_C.equals("")) {
							if(value > maxValue) {
								maxValue = value;
								maxStation_Name = station_Name;
							}
							if(value < minValue) {
								minValue = value;
								minStation_Name = station_Name;
							}
						}
					}
					
				}
			}
			switch(statisticsTypes) {
				case AVG:
					if(cnt != 0) {
						sequenceChangeResult.setValue(CommonTool.roundDouble(resultValue / cnt ));
					}
					break;
				case SUM:
					sequenceChangeResult.setValue(CommonTool.roundDouble(resultValue));
					break;
				case MAX:
					sequenceChangeResult.setValue(CommonTool.roundDouble(compareMaxValue));
					break;
				case MIN:
					sequenceChangeResult.setValue(CommonTool.roundDouble(compareMinValue));
					break;
				default:
					break;
			}
			if(paramStation_Id_C.equals("5%") || paramStation_Id_C.equals("*") || paramStation_Id_C.equals("")) {
				sequenceChangeResult.setMaxValue(maxValue);
				sequenceChangeResult.setMinValue(minValue);
				sequenceChangeResult.setMaxStation_Name(maxStation_Name);
				sequenceChangeResult.setMinStation_Name(minStation_Name);
			}
			
			resultList.add(sequenceChangeResult);
		}
		//统计历年结果
		for(int k = 0; k < resultList.size(); k++) {
			SequenceChangeResult sequenceChangeResult = resultList.get(k);
			Double currentValue = sequenceChangeResult.getValue();
			if(currentValue == null) {
				continue;
			}
			Date startDate = sequenceChangeResult.getStartDate();
			Date endDate = sequenceChangeResult.getEndDate();
			Double resultValue = 0.0;
			int cnt = 0;
			Double minValue = 0.0, maxValue = 0.0;
			int minCnt = 0, maxCnt = 0;
			Double compareMinValue = Double.MAX_VALUE, compareMaxValue = Double.MIN_VALUE;
			for(int i = 0; i < yearsList.size(); i++) {
				SequenceTimeValue sequenceTimeValue = yearsList.get(i);
				List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
//				Double compareMinValue = Double.MAX_VALUE, compareMaxValue = Double.MIN_VALUE;
				for(int j = 0; j < timeValues.size(); j++) {
					TimeValue timeValue = timeValues.get(j);
					Double value = timeValue.getValue();
					value = Eigenvalue.dispose(value);
					Date date = timeValue.getDate();
					int compareResult = CommonTool.currentTimeInRange(startDate, endDate, date);
					if(compareResult == 0) {
						switch(statisticsTypes) {
						case AVG:
							resultValue += value;
							cnt++;
							break;
						case SUM:
							resultValue += value;
							break;
						case MAX:
							if(value > compareMaxValue) {
								compareMaxValue = value;
							}
							break;
						case MIN:
							if(value < compareMinValue) {
								compareMinValue = value;
							}
							break;
						default:
							break;
					}
					} else if(compareResult == 1) { //保证TimeValues是按照时间先后顺序排序的
						break;
					}
				}
				if(compareMinValue != Double.MAX_VALUE && compareMinValue != null) {
					minValue += compareMinValue;
					minCnt++;
				}
				if(compareMaxValue != Double.MIN_VALUE && compareMaxValue != null) {
					maxValue += compareMaxValue;
					maxCnt++;
				}
			}
			Double yearsValue = 0.0;
			switch(statisticsTypes) {
				case AVG:
					if(cnt != 0) {
						yearsValue = CommonTool.roundDouble(resultValue / cnt);
					}
					break;
				case SUM:
					yearsValue = CommonTool.roundDouble(resultValue / (sequenceChangeParam.getStandardEndYear() - sequenceChangeParam.getStandardStartYear() + 1) / currentTimeList.size());
					break;
				case MAX:
//					yearsValue = CommonTool.roundDouble(maxValue / (sequenceChangeParam.getStandardEndYear() - sequenceChangeParam.getStandardStartYear() + 1) / stationCnt);
					yearsValue = CommonTool.roundDouble(compareMaxValue);
					break;
				case MIN:
//					yearsValue = CommonTool.roundDouble(minValue / (sequenceChangeParam.getStandardEndYear() - sequenceChangeParam.getStandardStartYear() + 1) / stationCnt);
					yearsValue = CommonTool.roundDouble(compareMinValue);
					break;
				default:
					break;
			}
			sequenceChangeResult.setYearsValue(yearsValue);
			sequenceChangeResult.setAnomaly(CommonTool.roundDouble(currentValue - yearsValue));
			if(yearsValue != 0) {
				sequenceChangeResult.setAnomalyRate(CommonTool.roundDouble((currentValue - yearsValue) / yearsValue * 100));
			} else {
				sequenceChangeResult.setAnomalyRate(100.0);
			}
		}
		return resultList;
	}
}
