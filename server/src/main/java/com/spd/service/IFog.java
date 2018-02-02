package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IFog {

	public List<LinkedHashMap> queryFogBySameYears(HashMap paramMap);

	public List<LinkedHashMap> queryFogByOverYears(HashMap paramMap);

	public List<LinkedHashMap> queryFogByTimes(HashMap paramMap); 

	public List<LinkedHashMap> queryFogByRangesAndStations(HashMap paramMap); 
	
	
}
