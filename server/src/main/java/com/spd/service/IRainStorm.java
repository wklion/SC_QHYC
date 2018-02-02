package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IRainStorm {

	public List<LinkedHashMap> queryRainStormByTimes(HashMap paramMap); 

	public List<LinkedHashMap> queryRainStormByTimesAndStations(HashMap paramMap); 

	public List<LinkedHashMap> queryRainStormBySameYearAndStations(HashMap paramMap); 

	public List<LinkedHashMap> queryRainStormByOverYearAndStations(HashMap paramMap); 

	public List<LinkedHashMap> rainstormByRange(HashMap paramMap); 

	public List<LinkedHashMap> queryRainStormStationsByTime(HashMap paramMap); 

	public List<LinkedHashMap> queryRainStormByTimeAndStations(HashMap paramMap); 

	public List<LinkedHashMap> queryStatisticsRainStormByTimes(HashMap paramMap); 
	
}
