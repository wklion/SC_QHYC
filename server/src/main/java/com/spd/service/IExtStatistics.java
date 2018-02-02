package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历年同期
 * @author Administrator
 *
 */
public interface IExtStatistics {

	/**
	 * 统计顺序时间范围的结果
	 * @param paramMap
	 * @return
	 */
	public List<Map> statisticsRangTime(HashMap paramMap);
	
	/**
	 * 统计历史同期时段的结果
	 * @param paramMap
	 * @return
	 */
	public List<Map> statisticsHisRangTime(HashMap paramMap);
}
