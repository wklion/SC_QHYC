package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ITmpGap {

	/**
	 * 计算高温
	 * @param paramMap
	 * @return
	 */
	public List<Map> getMaxTmpByTimes(HashMap paramMap);
	
	/**
	 * 计算低温
	 * @param paramMap
	 * @return
	 */
	public List<Map> getLowTmpByTimes(HashMap paramMap);

	/**
	 * 计算较差气温
	 * @param paramMap
	 * @return
	 */
	public List<Map> getGapTmpByTimes(HashMap paramMap);

	/**
	 * 按年份
	 * @param paramMap
	 * @return
	 */
	public List<Map> getGapTmpByYears(HashMap paramMap);

	/**
	 * 按年份，统计高温、低温
	 * @param paramMap
	 * @return
	 */
	public List<Map> getTmpByYear(HashMap paramMap);
	
	/**
	 * 查询历年的平均气温年较差，结果按年分组
	 * @param paramMap
	 * @return
	 */
	public List<Map> getTmpGapByYears(HashMap paramMap);

	/**
	 * 查询历年的平均气温年较差，结果只有一个值，多年的均值
	 * @param paramMap
	 * @return
	 */
	public List<Map> getAvgTmpGapByYears(HashMap paramMap);
	
}
