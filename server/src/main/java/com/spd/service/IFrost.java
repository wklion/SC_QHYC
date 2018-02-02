package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IFrost {

	public List<LinkedHashMap> queryFrostBySameYears(HashMap paramMap);

	public List<LinkedHashMap> queryFrostByOverYears(HashMap paramMap);

	public List<LinkedHashMap> queryFrostByTimes(HashMap paramMap); 

	public List<LinkedHashMap> queryFrostByRangesAndStations(HashMap paramMap); 
	
	
}
