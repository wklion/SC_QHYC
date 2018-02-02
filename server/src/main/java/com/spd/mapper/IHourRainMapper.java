package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IHourRainMapper {

	public List<LinkedHashMap> hourRainExt(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtAWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainExtMWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainAccumulateAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainAccumulateStatistics(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtStatistics(HashMap paramMap);

	public List<LinkedHashMap> hourRainAccumulateAWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainAccumulateMWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainSequenceAWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceMWS (HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceByItemAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainSequenceByItemAWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceByItemMWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainRankByItemAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainRankByItemAWS (HashMap paramMap);
	
	public List<LinkedHashMap> hourRainRankByItemMWS (HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceBySameYearsAll(HashMap paramMap);

	public List<LinkedHashMap> hourRainSequenceBySameYearsAWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainSequenceBySameYearsMWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainExtYearsStatisticsAWS(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtYearsStatisticsMWS(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtYearsStatisticsALL(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainExtByTimesAWS(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtByTimesAll(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainExtByTimesMWS(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainChangeAWS(HashMap paramMap);

	public List<LinkedHashMap> hourRainChangeAll(HashMap paramMap);
	
	public List<LinkedHashMap> hourRainChangeMWS(HashMap paramMap);

	public List<LinkedHashMap> hourRainStation(HashMap paramMap);

	public List<LinkedHashMap> hourRainStationByStations(HashMap paramMap);

	public List<LinkedHashMap> hourRainSort(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtAREA(HashMap paramMap);

	public List<LinkedHashMap> hourRainExtByTimesAREA(HashMap paramMap);

	public List<LinkedHashMap> hourRainAccumulateAREA(HashMap paramMap);

	public List<LinkedHashMap> hourRainSequenceAREA(HashMap paramMap);

	public List<LinkedHashMap> hourRainSequenceBySameYearsAREA(HashMap paramMap);

	public List<LinkedHashMap> hourRainSortByStation(HashMap paramMap);
	
}
