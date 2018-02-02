package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IDaysMapper;
import com.spd.service.IDays;

@Component("DaysImpl")
public class DaysImpl implements IDays {

	@Resource
	private IDaysMapper daysMapper;
	
	public List<Map> statisticsDays(HashMap paramMap) {
		return daysMapper.queryDays(paramMap);
	}

	public List<Map> statisticsHisDays(HashMap paramMap) {
		return daysMapper.statisticsHisDays(paramMap);
	}

}
