package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public interface IMaxWindMapper {

	public List<LinkedHashMap> queryMaxWindByRanges(HashMap paramMap); 

	public List<LinkedHashMap> queryMaxWindBySameYear(HashMap paramMap); 
	
	public List<LinkedHashMap> queryMaxWindByOverYear(HashMap paramMap); 

}
