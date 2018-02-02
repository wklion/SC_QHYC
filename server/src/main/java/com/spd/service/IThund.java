package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IThund {

	public List<LinkedHashMap> queryThundByRange (HashMap paramMap); 

	public List<LinkedHashMap> queryThundCntByRange (HashMap paramMap); 

	public List<LinkedHashMap> queryThundBySameYears (HashMap paramMap); 

	public List<LinkedHashMap> queryThundByOverYears (HashMap paramMap); 
	
}
