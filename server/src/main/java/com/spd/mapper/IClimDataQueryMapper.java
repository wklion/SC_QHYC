package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IClimDataQueryMapper {

	public List<LinkedHashMap> queryClimByTimesRangeAndElement(HashMap paramMap); 
	
}
