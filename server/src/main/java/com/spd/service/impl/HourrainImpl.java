package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IHourRainMapper;
import com.spd.service.IHourRain;

@Component("HourrainImpl")
public class HourrainImpl implements IHourRain {
	
	@Resource
	private IHourRainMapper hourRainMapper;

	public List<LinkedHashMap> hourRainExtAWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtAll(HashMap paramMap) {
		return hourRainMapper.hourRainExtAll(paramMap);
	}

	public List<LinkedHashMap> hourRainExtMWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainAccumulateAWS(HashMap paramMap) {
		return hourRainMapper.hourRainAccumulateAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainAccumulateAll(HashMap paramMap) {
		return hourRainMapper.hourRainAccumulateAll(paramMap);
	}

	public List<LinkedHashMap> hourRainAccumulateMWS(HashMap paramMap) {
		return hourRainMapper.hourRainAccumulateMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceAWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceAll(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceAll(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceMWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceByItemAWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceByItemAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceByItemAll(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceByItemAll(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceByItemMWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceByItemMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainRankByItemAWS(HashMap paramMap) {
		return hourRainMapper.hourRainRankByItemAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainRankByItemAll(HashMap paramMap) {
		return hourRainMapper.hourRainRankByItemAll(paramMap);
	}

	public List<LinkedHashMap> hourRainRankByItemMWS(HashMap paramMap) {
		return hourRainMapper.hourRainRankByItemMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceBySameYearsAWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceBySameYearsAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceBySameYearsAll(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceBySameYearsAll(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceBySameYearsMWS(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceBySameYearsMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtYearsStatisticsAWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtYearsStatisticsAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtYearsStatisticsMWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtYearsStatisticsMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtByTimesAWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtByTimesAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtByTimesAll(HashMap paramMap) {
		return hourRainMapper.hourRainExtByTimesAll(paramMap);
	}

	public List<LinkedHashMap> hourRainExtByTimesMWS(HashMap paramMap) {
		return hourRainMapper.hourRainExtByTimesMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainChangeAWS(HashMap paramMap) {
		return hourRainMapper.hourRainChangeAWS(paramMap);
	}

	public List<LinkedHashMap> hourRainChangeAll(HashMap paramMap) {
		return hourRainMapper.hourRainChangeAll(paramMap);
	}

	public List<LinkedHashMap> hourRainChangeMWS(HashMap paramMap) {
		return hourRainMapper.hourRainChangeMWS(paramMap);
	}

	public List<LinkedHashMap> hourRainExtYearsStatisticsALL(HashMap paramMap) {
		return hourRainMapper.hourRainExtYearsStatisticsALL(paramMap);
	}

	public List<LinkedHashMap> hourRainStation(HashMap paramMap) {
		return hourRainMapper.hourRainStation(paramMap);
	}

	public List<LinkedHashMap> hourRainStationByStations(HashMap paramMap) {
		return hourRainMapper.hourRainStationByStations(paramMap);
	}

	public List<LinkedHashMap> hourRainSort(HashMap paramMap) {
		return hourRainMapper.hourRainSort(paramMap);
	}

	public List<LinkedHashMap> hourRainExtAREA(HashMap paramMap) {
		return hourRainMapper.hourRainExtAREA(paramMap);
	}

	public List<LinkedHashMap> hourRainExtByTimesAREA(HashMap paramMap) {
		return hourRainMapper.hourRainExtByTimesAREA(paramMap);
	}

	public List<LinkedHashMap> hourRainAccumulateAREA(HashMap paramMap) {
		return hourRainMapper.hourRainAccumulateAREA(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceAREA(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceAREA(paramMap);
	}

	public List<LinkedHashMap> hourRainSequenceBySameYearsAREA(HashMap paramMap) {
		return hourRainMapper.hourRainSequenceBySameYearsAREA(paramMap);
	}

	public List<LinkedHashMap> hourRainSortByStation(HashMap paramMap) {
		return hourRainMapper.hourRainSortByStation(paramMap);
	}

	public List<LinkedHashMap> hourRainAccumulateStatistics(HashMap paramMap) {
		return hourRainMapper.hourRainAccumulateStatistics(paramMap);
	}

	public List<LinkedHashMap> hourRainExtStatistics(HashMap paramMap) {
		return hourRainMapper.hourRainExtStatistics(paramMap);
	}
}
