package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IFogMapper {

	public List<LinkedHashMap> queryFogBySameYears(HashMap paramMap);

	public List<LinkedHashMap> queryFogByOverYears(HashMap paramMap);

	public List<LinkedHashMap> queryFogByTimes(HashMap paramMap); 

	public List<LinkedHashMap> queryFogByRangesAndStations(HashMap paramMap); 
	
}
