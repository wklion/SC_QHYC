package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IHighTmpMapper {

	public List<LinkedHashMap> queryHighTmpByRange (HashMap paramMap); 

	public List<LinkedHashMap> queryHighTmpByStation (HashMap paramMap); 
	
	public List<LinkedHashMap> queryHighTmpByYears (HashMap paramMap); 
	
}
