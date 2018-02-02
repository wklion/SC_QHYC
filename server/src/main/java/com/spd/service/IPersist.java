package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IPersist {

	/**
	 *  持续统计所有站
	 * @param paramMap
	 * @return
	 */
	public List<Map> persistAll(HashMap paramMap);
	
	/**
	 * 根据站名做统计
	 * @param paramMap
	 * @return
	 */
	public List<Map> persistByStations(HashMap paramMap);

	/**
	 * 气温变化统计
	 * @param paramMap
	 * @return
	 */
	public List<Map> tmp(HashMap paramMap);

	/**
	 * 连晴连雨统计
	 * @param paramMap
	 * @return
	 */
	public List<Map> rain(HashMap paramMap);
	
}
