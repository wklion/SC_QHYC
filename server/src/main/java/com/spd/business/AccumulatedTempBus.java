package com.spd.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.spd.common.AccumulatedTempParam;
import com.spd.common.AccumulatedTempResult;
import com.spd.common.AccumulatedTempYearResult;
import com.spd.common.ActiveAccumulatedTemp;
import com.spd.common.ActiveAccumulatedYearTemp;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.common.ValidAccumulatedTemp;
import com.spd.common.ValidAccumulatedYearTemp;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 积温计算
 * @author Administrator
 *
 */
public class AccumulatedTempBus {

	/**
	 * 计算逐年的积温
	 * @param accumulatedTempParam
	 * @return
	 */
	public AccumulatedTempYearResult accumulatedTempByYeaer(AccumulatedTempParam accumulatedTempParam) {
		AccumulatedTempYearResult accumulatedTempYearResult = new AccumulatedTempYearResult();
		List<ActiveAccumulatedYearTemp> activeAccumulatedTempList = new ArrayList<ActiveAccumulatedYearTemp>();
		List<ValidAccumulatedYearTemp> validAccumulatedTempList = new ArrayList<ValidAccumulatedYearTemp>();
		TimesParam timesParam = accumulatedTempParam.getTimesParam();
		//查询历年
		DBTable dbTable = new DBTable();
		TimesYearsParam timesYearsParam = new TimesYearsParam(timesParam.getStartMon(), timesParam.getStartDay(), 
				timesParam.getEndMon(), timesParam.getEndDay(), accumulatedTempParam.getPerennialStartYear(), accumulatedTempParam.getPerennialEndYear());
		dbTable.queryDataByYears(timesYearsParam, accumulatedTempParam.getStation_Id_C(), "t_tem_avg");
		List<SequenceTimeValue> yearsTemAvgValueList = dbTable.getSequenceTimeValueList();
		// key : year value: 有效积温
		LinkedHashMap<Integer, Double> activeAccumulatedTempStationMap = new LinkedHashMap<Integer, Double>();
		LinkedHashMap<Integer, Double> validAccumulatedTempStationMap = new LinkedHashMap<Integer, Double>();
		for(int i = 0; i < yearsTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = yearsTemAvgValueList.get(i);
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			int year = sequenceTimeValue.getYear();
			List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
			Double activeAccumulatedTempValue = 0.0, validAccumulatedTempValue = 0.0;
			for(int j = 0; j < timeValues.size(); j++) {
				TimeValue timeValue = timeValues.get(j);
				Double value = timeValue.getValue();
				if(value >= accumulatedTempParam.getMinTmp()) {
					activeAccumulatedTempValue += value;
					validAccumulatedTempValue += (value - accumulatedTempParam.getMinTmp());
				}
			}
			
			Double activeValue = activeAccumulatedTempStationMap.get(year);
			if(activeValue == null) {
				activeAccumulatedTempStationMap.put(year , activeAccumulatedTempValue);
			} else {
				activeAccumulatedTempStationMap.put(year, activeAccumulatedTempValue + activeValue);
			}
			
			Double validValue = validAccumulatedTempStationMap.get(year);
			if(activeValue == null) {
				validAccumulatedTempStationMap.put(year, validAccumulatedTempValue);
			} else {
				validAccumulatedTempStationMap.put(year, validAccumulatedTempValue + validValue);
			}
		}
		//统计结果
		for(int i = accumulatedTempParam.getPerennialStartYear(); i <= accumulatedTempParam.getPerennialEndYear(); i++) {
			ActiveAccumulatedYearTemp activeAccumulatedYearTemp = new ActiveAccumulatedYearTemp();
			Double value = activeAccumulatedTempStationMap.get(i);
			if(value == null) continue;
			activeAccumulatedYearTemp.setAccumulatedTemp(CommonTool.roundDouble(value));
			activeAccumulatedYearTemp.setYear(i);
			activeAccumulatedTempList.add(activeAccumulatedYearTemp);
			
			ValidAccumulatedYearTemp validAccumulatedYearTemp = new ValidAccumulatedYearTemp();
			validAccumulatedYearTemp.setAccumulatedTemp(CommonTool.roundDouble(validAccumulatedTempStationMap.get(i)));
			validAccumulatedYearTemp.setYear(i);
			validAccumulatedTempList.add(validAccumulatedYearTemp);
		}
		accumulatedTempYearResult.setActiveAccumulatedTempList(activeAccumulatedTempList);
		accumulatedTempYearResult.setValidAccumulatedTempList(validAccumulatedTempList);
		return accumulatedTempYearResult;
	}
	
	public AccumulatedTempResult accumulatedTempByTimes(AccumulatedTempParam accumulatedTempParam) {
		AccumulatedTempResult accumulatedTempResult = new AccumulatedTempResult();
		//活动积温
		List<ActiveAccumulatedTemp> activeAccumulatedTempList = new ArrayList<ActiveAccumulatedTemp>();
		//有效积温
		List<ValidAccumulatedTemp> validAccumulatedTempList = new ArrayList<ValidAccumulatedTemp>();
		TimesParam timesParam = accumulatedTempParam.getTimesParam();
		TimesRangeParam timesRangeParam = new TimesRangeParam();
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		DBTable dbTable = new DBTable();
		//查询当年 
		dbTable.queryDataByRangeTimes(timesRangeParam, "5%", "t_tem_avg");
		List<SequenceTimeValue> sequenceTemAvgValueList = dbTable.getSequenceTimeValueList();
		//查询历年
		TimesYearsParam timesYearsParam = new TimesYearsParam(timesParam.getStartMon(), timesParam.getStartDay(), 
				timesParam.getEndMon(), timesParam.getEndDay(), accumulatedTempParam.getPerennialStartYear(), accumulatedTempParam.getPerennialEndYear());
		dbTable.queryDataByYears(timesYearsParam, "5%", "t_tem_avg");
		List<SequenceTimeValue> yearsTemAvgValueList = dbTable.getSequenceTimeValueList();
		//计算当年
		for(int i = 0; i < sequenceTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = sequenceTemAvgValueList.get(i);
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			String station_Name = sequenceTimeValue.getStation_Name();
			Double activeAccumulatedTempValue = 0.0, validAccumulatedTempValue = 0.0;
			ActiveAccumulatedTemp activeAccumulatedTemp = new ActiveAccumulatedTemp();
			ValidAccumulatedTemp validAccumulatedTemp = new ValidAccumulatedTemp();
			List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
			for(int j = 0; j < timeValues.size(); j++) {
				TimeValue timeValue = timeValues.get(j);
				Double value = timeValue.getValue();
				if(value >= accumulatedTempParam.getMinTmp()) {
					activeAccumulatedTempValue += value;
					validAccumulatedTempValue += (value - accumulatedTempParam.getMinTmp());
				}
			}
			activeAccumulatedTemp.setStation_Id_C(station_Id_C);
			activeAccumulatedTemp.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			activeAccumulatedTemp.setAccumulatedTemp(CommonTool.roundDouble(activeAccumulatedTempValue));
			activeAccumulatedTempList.add(activeAccumulatedTemp);
			validAccumulatedTemp.setStation_Id_C(station_Id_C);
			validAccumulatedTemp.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			validAccumulatedTemp.setAccumulatedTemp(CommonTool.roundDouble(validAccumulatedTempValue));
			validAccumulatedTempList.add(validAccumulatedTemp);
		}
		//计算历年
		Map<String, Double> activeAccumulatedTempStationMap = new HashMap<String, Double>();
		Map<String, Double> validAccumulatedTempStationMap = new HashMap<String, Double>();
		for(int i = 0; i < yearsTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = yearsTemAvgValueList.get(i);
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
			Double activeAccumulatedTempValue = 0.0, validAccumulatedTempValue = 0.0;
			for(int j = 0; j < timeValues.size(); j++) {
				TimeValue timeValue = timeValues.get(j);
				Double value = timeValue.getValue();
				if(value >= accumulatedTempParam.getMinTmp()) {
					activeAccumulatedTempValue += value;
					validAccumulatedTempValue += (value - accumulatedTempParam.getMinTmp());
				}
			}
			
			Double activeValue = activeAccumulatedTempStationMap.get(station_Id_C);
			if(activeValue == null) {
				activeAccumulatedTempStationMap.put(station_Id_C , activeAccumulatedTempValue);
			} else {
				activeAccumulatedTempStationMap.put(station_Id_C, activeAccumulatedTempValue + activeValue);
			}
			
			Double validValue = validAccumulatedTempStationMap.get(station_Id_C);
			if(activeValue == null) {
				validAccumulatedTempStationMap.put(station_Id_C, validAccumulatedTempValue);
			} else {
				validAccumulatedTempStationMap.put(station_Id_C, validAccumulatedTempValue + validValue);
			}
		}
		//遍历，求平均
		Iterator<String> it = activeAccumulatedTempStationMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Double validAccumulatedTempValue = activeAccumulatedTempStationMap.get(key);
			validAccumulatedTempValue = CommonTool.roundDouble(validAccumulatedTempValue /= (accumulatedTempParam.getPerennialEndYear() - accumulatedTempParam.getPerennialStartYear() + 1));
			activeAccumulatedTempStationMap.put(key, validAccumulatedTempValue);
		}
		Iterator<String> it2 = validAccumulatedTempStationMap.keySet().iterator();
		while(it2.hasNext()) {
			String key = it2.next();
			Double validAccumulatedTempValue = validAccumulatedTempStationMap.get(key);
			validAccumulatedTempValue = CommonTool.roundDouble(validAccumulatedTempValue /= (accumulatedTempParam.getPerennialEndYear() - accumulatedTempParam.getPerennialStartYear() + 1));
			validAccumulatedTempStationMap.put(key, validAccumulatedTempValue);
		}
		//遍历，组装结果
		for(int i = 0; i < activeAccumulatedTempList.size(); i++) {
			ActiveAccumulatedTemp activeAccumulatedTemp = activeAccumulatedTempList.get(i);
			String station_Id_C = activeAccumulatedTemp.getStation_Id_C();
			Double value = activeAccumulatedTempStationMap.get(station_Id_C);
			activeAccumulatedTemp.setYearsAvg(value);
			activeAccumulatedTemp.setAnomaly(CommonTool.roundDouble(activeAccumulatedTemp.getAccumulatedTemp() - value));
		}
		
		for(int i = 0; i < validAccumulatedTempList.size(); i++) {
			ValidAccumulatedTemp validAccumulatedTemp = validAccumulatedTempList.get(i);
			String station_Id_C = validAccumulatedTemp.getStation_Id_C();
			Double value = validAccumulatedTempStationMap.get(station_Id_C);
			validAccumulatedTemp.setYearsAvg(value);
			validAccumulatedTemp.setAnomaly(CommonTool.roundDouble(validAccumulatedTemp.getAccumulatedTemp() - value));
		}
		accumulatedTempResult.setActiveAccumulatedTempList(activeAccumulatedTempList);
		accumulatedTempResult.setValidAccumulatedTempList(validAccumulatedTempList);
		return accumulatedTempResult;
	}
}
