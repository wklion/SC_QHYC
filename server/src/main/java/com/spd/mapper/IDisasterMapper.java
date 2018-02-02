package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IDisasterMapper {

	/**
	 * 暴雨统计相关
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> rainstorm(HashMap paramMap);

	public List<LinkedHashMap> queryByYearsStations(HashMap paramMap);

}
