package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IDisasterEvaluateMapper {

	public List<LinkedHashMap> areaHighTmpSiByRange(HashMap paramMap); 

	public List<LinkedHashMap> areaHighAreaResultSiByRange(HashMap paramMap); 

	public List<LinkedHashMap> areaHighAreaResultByYears(HashMap paramMap); 

	public List<LinkedHashMap> YHIareaHighTmpYearResult(HashMap paramMap); 

	public List<LinkedHashMap> autumnRains(HashMap paramMap); 

	public List<LinkedHashMap> autumnRainsByTimes(HashMap paramMap); 

	public List<LinkedHashMap> autumnRainsByYear(HashMap paramMap); 

	public List<LinkedHashMap> autumnTimesRangeByYear(HashMap paramMap); 

	public List<LinkedHashMap> autumnSeqRangeByYear(HashMap paramMap); 

	public List<LinkedHashMap> mciStationByTimes(HashMap paramMap); 
	
	public List<LinkedHashMap> mciSumStrength(HashMap paramMap); 

	public List<LinkedHashMap> mciStdStrength(HashMap paramMap); 

	public List<LinkedHashMap> mciStationByYears(HashMap paramMap); 

	public List<LinkedHashMap> mciAreaByTimes(HashMap paramMap); 

	public List<LinkedHashMap> mciAreaByYears(HashMap paramMap); 

	public List<LinkedHashMap> areaStormByTimes(HashMap paramMap); 

	public List<LinkedHashMap> areaStormByTime(HashMap paramMap); 

	public List<LinkedHashMap> areaStormStatisticsByTime(HashMap paramMap); 

	public List<LinkedHashMap> continueRainStatiionByTimes(HashMap paramMap); 

	public List<LinkedHashMap> continueRainAreaByTimes(HashMap paramMap); 

	public List<LinkedHashMap> strongCoolingStationByTimes(HashMap paramMap); 

	public List<LinkedHashMap> strongCoolingAreaByTimes(HashMap paramMap); 

	public List<LinkedHashMap> lowTmpStationByTimes(HashMap paramMap); 

	public List<LinkedHashMap> lowTmpAreaByTimes(HashMap paramMap); 

	public List<LinkedHashMap> mciStationBySingleStrength(HashMap paramMap); 

	public List<LinkedHashMap> querySumMCI(HashMap paramMap); 

	public List<LinkedHashMap> strongCoolingAreaByYears(HashMap paramMap); 

	public List<LinkedHashMap> strongCoolingStationByYears(HashMap paramMap); 

	public List<LinkedHashMap> continueRainAreaByYears(HashMap paramMap); 
	
	public List<LinkedHashMap> continueRainStatiionByYears(HashMap paramMap); 
	
}
