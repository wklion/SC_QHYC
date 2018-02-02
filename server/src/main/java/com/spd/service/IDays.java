package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日数统计接口
 * @author Administrator
 *
 */
public interface IDays {

	/**
	 * 统计指定时间范围的日数
	 * @param paramMap
	 * @return
	 */
	public List<Map> statisticsDays(HashMap paramMap);

	/**
	 * 统计全部的历史
	 * @param paramMap
	 * @return
	 */
	public List<Map> statisticsHisDays(HashMap paramMap);
	
}
