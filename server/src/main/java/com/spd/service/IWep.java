package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public interface IWep {

	public List<LinkedHashMap> queryByTimes(HashMap paramMap);

	public List<LinkedHashMap> queryAllByTimes(HashMap paramMap);
	
}
