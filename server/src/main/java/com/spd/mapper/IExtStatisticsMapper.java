package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IExtStatisticsMapper {

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
