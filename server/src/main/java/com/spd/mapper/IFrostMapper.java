package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IFrostMapper {
	
	public List<LinkedHashMap> queryFrostByOverYears(HashMap paramMap);

	public List<LinkedHashMap> queryFrostByRangesAndStations(HashMap paramMap);

	public List<LinkedHashMap> queryFrostBySameYears(HashMap paramMap);

	public List<LinkedHashMap> queryFrostByTimes(HashMap paramMap);
}
