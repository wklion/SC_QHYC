package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISameCale {

	/**
	 * 不跨年，查询所有站
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAllEle(HashMap paramMap);
	
	/**
	 * 查询自动站
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAWSEle(HashMap paramMap);

	/**
	 * 不跨年，查询指定站
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryEleByStations(HashMap paramMap);

	/**
	 * 跨年，查询所有的自动站
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAllEleOverYear(HashMap paramMap);
	
	/**
	 * 跨年，查询指定站
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryEleOverYearByStations(HashMap paramMap);
	
}
