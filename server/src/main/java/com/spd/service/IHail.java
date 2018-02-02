package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public interface IHail {

	public List<LinkedHashMap> queryByTimes(HashMap paramMap);
	
}
