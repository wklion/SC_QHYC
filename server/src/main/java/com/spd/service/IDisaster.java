package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 灾害相关
 * @author Administrator
 *
 */
public interface IDisaster {

	public List<LinkedHashMap> rainstorm(HashMap paramMap);

	/**
	 * 历年同期，并且过滤一部分站
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> queryByYearsStations(HashMap paramMap);
	
}
