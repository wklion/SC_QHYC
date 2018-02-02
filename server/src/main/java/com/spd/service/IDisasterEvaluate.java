package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 灾害相关
 * @author Administrator
 *
 */
public interface IDisasterEvaluate {
	
	/**
	 * 单站高温时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaHighTmpSiByRange(HashMap paramMap);

	/**
	 * 区域高温时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaHighAreaResultSiByRange(HashMap paramMap);

	/**
	 * 历年高温综合
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaHighAreaResultByYears(HashMap paramMap);

	/**
	 * 查询YHI的值
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> YHIareaHighTmpYearResult(HashMap paramMap);

	/**
	 * 秋雨计算
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> autumnRains(HashMap paramMap);

	/**
	 * 按年份查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> autumnTimesRangeByYear(HashMap paramMap);
	
	/**
	 * 查询年份的多雨期
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> autumnSeqRangeByYear(HashMap paramMap);

	/**
	 * 按时间段查询秋雨对应的雨量等信息
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> autumnRainsByTimes(HashMap paramMap);

	/**
	 * 按年份查询秋雨
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> autumnRainsByYear(HashMap paramMap);
	
	/**
	 * 干旱过程，按时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciStationByTimes(HashMap paramMap);

	/**
	 * 查询过程的累积强度
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciSumStrength(HashMap paramMap);

	/**
	 * 计算标准差
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciStdStrength(HashMap paramMap);

	/**
	 * 干旱年度查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciStationByYears(HashMap paramMap);

	/**
	 * 区域干旱过程，按时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciAreaByTimes(HashMap paramMap);

	/**
	 * 区域干旱过程，年度查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciAreaByYears(HashMap paramMap);
	
	/**
	 * 根据时间段查询区域暴雨
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaStormByTimes(HashMap paramMap);
	
	/**
	 * 根据时间点，查询该时间点是否有暴雨的结果
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaStormByTime(HashMap paramMap);
	
	/**
	 * 按时间段统计站数，雨量和等
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> areaStormStatisticsByTime(HashMap paramMap);

	/**
	 * 单站连阴雨按时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> continueRainStatiionByTimes(HashMap paramMap);

	/**
	 * 单站连阴雨历年同时段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> continueRainStatiionByYears(HashMap paramMap);

	/**
	 * 区域连阴雨按时间段查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> continueRainAreaByTimes(HashMap paramMap);

	/**
	 * 区域连阴雨历年查询
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> continueRainAreaByYears(HashMap paramMap);

	/**
	 * 单站强降温
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> strongCoolingStationByTimes(HashMap paramMap);

	/**
	 * 区域强降温
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> strongCoolingAreaByTimes(HashMap paramMap);

	/**
	 * 单站低温统计
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> lowTmpStationByTimes(HashMap paramMap);
	
	/**
	 * 低温区域统计
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> lowTmpAreaByTimes(HashMap paramMap);

	/**
	 * 根据SingleStrength排序
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> mciStationBySingleStrength(HashMap paramMap);

	/**
	 * 计算MCI综合指数
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> querySumMCI(HashMap paramMap);

	/**
	 * 查询历年强降温
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> strongCoolingAreaByYears(HashMap paramMap);

	/**
	 * 单站历年强降温
	 * @param paramMap
	 * @return
	 */
	public List<LinkedHashMap> strongCoolingStationByYears(HashMap paramMap);
	
}
