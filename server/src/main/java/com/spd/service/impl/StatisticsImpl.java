package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IStatisticsMapper;
import com.spd.service.IStatistics;

@Component("StatisticsImpl")
public class StatisticsImpl implements IStatistics {

	@Resource
	private IStatisticsMapper statisticsMapper;
	
	public List<Map> queryAvgTemByYears(HashMap paramMap) {
		return statisticsMapper.queryAvgTemByYears(paramMap);
	}

	public List<Map> queryAvgTemByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryAvgTemByTimeRange(paramMap);
	}

	public List<Map> queryAvgTemMaxByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryAvgTemMaxByTimeRange(paramMap);
	}

	public List<Map> queryAvgTemMaxByYears(HashMap paramMap) {
		return statisticsMapper.queryAvgTemMaxByYears(paramMap);
	}

	public List<Map> queryAvgTemMinByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryAvgTemMinByTimeRange(paramMap);
	}

	public List<Map> queryAvgTemMinByYears(HashMap paramMap) {
		return statisticsMapper.queryAvgTemMinByYears(paramMap);
	}

	public List<Map> queryWin_s_2mi_avgByYears(HashMap paramMap) {
		return statisticsMapper.queryWin_s_2mi_avgByYears(paramMap);
	}

	public List<Map> queryWin_s_2mi_avgByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryWin_s_2mi_avgByTimeRange(paramMap);
	}

	public List<Map> queryPrsAvgByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryPrsAvgByTimeRange(paramMap);
	}

	public List<Map> queryPrsAvgByYears(HashMap paramMap) {
		return statisticsMapper.queryPrsAvgByYears(paramMap);
	}

	public List<Map> queryPreSumByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryPreSumByTimeRange(paramMap);
	}

	public List<Map> queryPreSumByYears(HashMap paramMap) {
		return statisticsMapper.queryPreSumByYears(paramMap);
	}

	public List<Map> querySSHByTimeRange(HashMap paramMap) {
		return statisticsMapper.querySSHByTimeRange(paramMap);
	}

	public List<Map> querySSHSumByYears(HashMap paramMap) {
		return statisticsMapper.querySSHSumByYears(paramMap);
	}

	public List<Map> queryRHUAvgByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryRHUAvgByTimeRange(paramMap);
	}

	public List<Map> queryRHUByYears(HashMap paramMap) {
		return statisticsMapper.queryRHUByYears(paramMap);
	}

	public List<Map> queryVisMinByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryVisMinByTimeRange(paramMap);
	}

	public List<Map> queryVisMinByYears(HashMap paramMap) {
		return statisticsMapper.queryVisMinByYears(paramMap);
	}

	public List<Map> queryExtMaxTmpByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryExtMaxTmpByTimeRange(paramMap);
	}

	public List<Map> queryExtMinTmpByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryExtMinTmpByTimeRange(paramMap);
	}

	public List<Map> queryExtMaxTmpByYears(HashMap paramMap) {
		return statisticsMapper.queryExtMaxTmpByYears(paramMap);
	}

	public List<Map> queryExtMinTmpByYears(HashMap paramMap) {
		return statisticsMapper.queryExtMinTmpByYears(paramMap);
	}

	public List<Map> queryPreCntByTimeRange(HashMap paramMap) {
		return statisticsMapper.queryPreCntByTimeRange(paramMap);
	}

	public List<Map> queryPreCntByYears(HashMap paramMap) {
		return statisticsMapper.queryPreCntByYears(paramMap);
	}

	public List<Map> querySSHTime(HashMap paramMap) {
		return statisticsMapper.querySSHTime(paramMap);
	}

	public List<Map> queryTmpDaysByYear(HashMap paramMap) {
		return statisticsMapper.queryTmpDaysByYear(paramMap);
	}
}
