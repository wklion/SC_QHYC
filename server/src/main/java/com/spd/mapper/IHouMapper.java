package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IHouMapper {

	public List<LinkedHashMap> queryHouTmpData(HashMap paramMap); 

	public List<LinkedHashMap> queryHouTmpDataByYears(HashMap paramMap); 

	public List<LinkedHashMap> queryHouTmpDataByTimes(HashMap paramMap); 
	
}
