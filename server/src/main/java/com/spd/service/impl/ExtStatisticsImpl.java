package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IExtStatisticsMapper;
import com.spd.service.IExtStatistics;

@Component("ExtStatisticsImpl")
public class ExtStatisticsImpl implements IExtStatistics {

	@Resource
	private IExtStatisticsMapper extStatisticsMapper;
	
	public List<Map> statisticsHisRangTime(HashMap paramMap) {
		return extStatisticsMapper.statisticsHisRangTime(paramMap);
	}

	public List<Map> statisticsRangTime(HashMap paramMap) {
		return extStatisticsMapper.statisticsRangTime(paramMap);
	}

}
