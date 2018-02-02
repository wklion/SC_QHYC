package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IFirstDayMapper {

	/**
	 * 查询历年的气温或降水
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryRainTmpByTimeRange(HashMap paramMap);

	/**
	 * 查询指定年的气温或降水
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryRainTmpByYear(HashMap paramMap);
	
}
