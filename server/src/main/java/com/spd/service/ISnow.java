package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface ISnow {

	public List<LinkedHashMap> querySnowBySameYears(HashMap paramMap);

	public List<LinkedHashMap> querySnowByOverYears(HashMap paramMap);

	public List<LinkedHashMap> querySnowByTimes(HashMap paramMap); 

	public List<LinkedHashMap> querySnowByRangesAndStations(HashMap paramMap); 

	public List<LinkedHashMap> snowArea(HashMap paramMap); 
	
	
}
