package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IStatisticsMapper {

	public List<Map> queryAvgTemByYears(HashMap paramMap); 

	/**
	 * 根据时段统计平均气温
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAvgTemByTimeRange(HashMap paramMap); 

	/**
	 * 高温均值统计，按时间段
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAvgTemMaxByTimeRange(HashMap paramMap); 

	/**
	 * 高温均值统计，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAvgTemMaxByYears(HashMap paramMap); 
	
	/**
	 * 低温均值统计，按时间段
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAvgTemMinByTimeRange(HashMap paramMap); 

	/**
	 * 低温均值统计，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryAvgTemMinByYears(HashMap paramMap); 

	/**
	 * 平均风速，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryWin_s_2mi_avgByYears(HashMap paramMap); 
	
	/**
	 * 平均风速，时段值
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryWin_s_2mi_avgByTimeRange(HashMap paramMap); 

	/**
	 * 平均气压，时段值
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPrsAvgByTimeRange(HashMap paramMap); 
	
	/**
	 * 平均气压，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPrsAvgByYears(HashMap paramMap); 

	/**
	 * 降水总量，时间段
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPreSumByTimeRange(HashMap paramMap); 

	/**
	 * 降水总量，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPreSumByYears(HashMap paramMap); 

	/**
	 * 按时间范围统计日照对数
	 * @param paramMap
	 * @return
	 */
	public List<Map> querySSHByTimeRange(HashMap paramMap); 
	
	/**
	 * 按年统计日照对数
	 * @param paramMap
	 * @return
	 */
	public List<Map> querySSHSumByYears(HashMap paramMap); 

	/**
	 * 按时间范围统计相对湿度
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryRHUAvgByTimeRange(HashMap paramMap); 

	/**
	 * 历年同期相对湿度
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryRHUByYears(HashMap paramMap); 

	/**
	 * 统计能见度低值
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryVisMinByTimeRange(HashMap paramMap); 
	
	/**
	 * 按时间段范围统计能见度低值，和出现时间
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryVisMinByYears(HashMap paramMap);

	/**
	 * 极端高温，按时间段范围
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryExtMaxTmpByTimeRange(HashMap paramMap);

	/**
	 * 极端低温，按时间段范围
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryExtMinTmpByTimeRange(HashMap paramMap);

	/**
	 * 极端高温，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryExtMaxTmpByYears(HashMap paramMap);

	/**
	 * 极端低温，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryExtMinTmpByYears(HashMap paramMap);

	/**
	 * 按时间范围统计降水日数
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPreCntByTimeRange(HashMap paramMap);

	/**
	 * 统计降水日数，历年同期
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryPreCntByYears(HashMap paramMap);
	
	/**
	 * 查询ssh中站点，时间对应的日照时间
	 * @param paramMap
	 * @return
	 */
	public List<Map> querySSHTime(HashMap paramMap);

	/**
	 * 按年份统计平均气温
	 * @param paramMap
	 * @return
	 */
	public List<Map> queryTmpDaysByYear(HashMap paramMap);
}
