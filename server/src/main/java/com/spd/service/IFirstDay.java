package com.spd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 初日统计
 * @author Administrator
 *
 */
public interface IFirstDay {

	/**
	 * 查询历年的气温或降水
	 * @param paramMap
	 * @return
	 */
	public List<Map> getRainTmpByTimeRange(HashMap paramMap);

	/**
	 * 查询指定年的气温或降水
	 * @param paramMap
	 * @return
	 */
	public List<Map> getRainTmpByYear(HashMap paramMap);
	
}
