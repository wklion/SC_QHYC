package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRank {

	/**
	 * 位次分析涉及到的查询
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryEle(HashMap paramMap);

	/**
	 * 位次分析，跨年查询
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryEleOverYear(HashMap paramMap);
	
}
