package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IDisasterEvaluateMapper;
import com.spd.service.IDisasterEvaluate;

@Component("DisasterEvaluateImpl")
public class DisasterEvaluateImpl implements IDisasterEvaluate {

	@Resource
	private IDisasterEvaluateMapper disasterEvaluateMapper;

	public List<LinkedHashMap> areaHighTmpSiByRange(HashMap paramMap) {
		return disasterEvaluateMapper.areaHighTmpSiByRange(paramMap);
	}

	public List<LinkedHashMap> areaHighAreaResultSiByRange(HashMap paramMap) {
		return disasterEvaluateMapper.areaHighAreaResultSiByRange(paramMap);
	}

	public List<LinkedHashMap> areaHighAreaResultByYears(HashMap paramMap) {
		return disasterEvaluateMapper.areaHighAreaResultByYears(paramMap);
	}

	public List<LinkedHashMap> YHIareaHighTmpYearResult(HashMap paramMap) {
		return disasterEvaluateMapper.YHIareaHighTmpYearResult(paramMap);
	}

	public List<LinkedHashMap> autumnRains(HashMap paramMap) {
		return disasterEvaluateMapper.autumnRains(paramMap);
	}

	public List<LinkedHashMap> autumnRainsByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.autumnRainsByTimes(paramMap);
	}

	public List<LinkedHashMap> autumnRainsByYear(HashMap paramMap) {
		return disasterEvaluateMapper.autumnRainsByYear(paramMap);
	}

	public List<LinkedHashMap> autumnTimesRangeByYear(HashMap paramMap) {
		return disasterEvaluateMapper.autumnTimesRangeByYear(paramMap);
	}

	public List<LinkedHashMap> autumnSeqRangeByYear(HashMap paramMap) {
		return disasterEvaluateMapper.autumnSeqRangeByYear(paramMap);
	}

	public List<LinkedHashMap> mciStationByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.mciStationByTimes(paramMap);
	}

	public List<LinkedHashMap> mciSumStrength(HashMap paramMap) {
		return disasterEvaluateMapper.mciSumStrength(paramMap);
	}

	public List<LinkedHashMap> mciStdStrength(HashMap paramMap) {
		return disasterEvaluateMapper.mciStdStrength(paramMap);
	}

	public List<LinkedHashMap> mciStationByYears(HashMap paramMap) {
		return disasterEvaluateMapper.mciStationByYears(paramMap);
	}

	public List<LinkedHashMap> mciAreaByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.mciAreaByTimes(paramMap);
	}

	public List<LinkedHashMap> mciAreaByYears(HashMap paramMap) {
		return disasterEvaluateMapper.mciAreaByYears(paramMap);
	}

	public List<LinkedHashMap> areaStormByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.areaStormByTimes(paramMap);
	}

	public List<LinkedHashMap> areaStormByTime(HashMap paramMap) {
		return disasterEvaluateMapper.areaStormByTime(paramMap);
	}

	public List<LinkedHashMap> areaStormStatisticsByTime(HashMap paramMap) {
		return disasterEvaluateMapper.areaStormStatisticsByTime(paramMap);
	}

	public List<LinkedHashMap> continueRainStatiionByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.continueRainStatiionByTimes(paramMap);
	}

	public List<LinkedHashMap> continueRainAreaByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.continueRainAreaByTimes(paramMap);
	}

	public List<LinkedHashMap> strongCoolingStationByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.strongCoolingStationByTimes(paramMap);
	}

	public List<LinkedHashMap> strongCoolingAreaByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.strongCoolingAreaByTimes(paramMap);
	}

	public List<LinkedHashMap> lowTmpStationByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.lowTmpStationByTimes(paramMap);
	}

	public List<LinkedHashMap> lowTmpAreaByTimes(HashMap paramMap) {
		return disasterEvaluateMapper.lowTmpAreaByTimes(paramMap);
	}

	public List<LinkedHashMap> mciStationBySingleStrength(HashMap paramMap) {
		return disasterEvaluateMapper.mciStationBySingleStrength(paramMap);
	}

	public List<LinkedHashMap> querySumMCI(HashMap paramMap) {
		return disasterEvaluateMapper.querySumMCI(paramMap);
	}

	public List<LinkedHashMap> strongCoolingAreaByYears(HashMap paramMap) {
		return disasterEvaluateMapper.strongCoolingAreaByYears(paramMap);
	}

	public List<LinkedHashMap> strongCoolingStationByYears(HashMap paramMap) {
		return disasterEvaluateMapper.strongCoolingStationByYears(paramMap);
	}

	public List<LinkedHashMap> continueRainAreaByYears(HashMap paramMap) {
		return disasterEvaluateMapper.continueRainAreaByYears(paramMap);
	}

	public List<LinkedHashMap> continueRainStatiionByYears(HashMap paramMap) {
		return disasterEvaluateMapper.continueRainStatiionByYears(paramMap);
	}

}
