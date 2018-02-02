package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import com.spd.mapper.ISnowMapper;
import com.spd.service.ISnow;

@Component("SnowImpl")
public class SnowImpl implements ISnow {

	@Resource
	private ISnowMapper snowMapper;
	
	public List<LinkedHashMap> querySnowByTimes(HashMap paramMap) {
		return snowMapper.querySnowByTimes(paramMap);
	}

	public List<LinkedHashMap> querySnowBySameYears(HashMap paramMap) {
		return snowMapper.querySnowBySameYears(paramMap);
	}

	public List<LinkedHashMap> querySnowByOverYears(HashMap paramMap) {
		return snowMapper.querySnowByOverYears(paramMap);
	}
	
	public List<LinkedHashMap> querySnowByRangesAndStations(HashMap paramMap) {
		return snowMapper.querySnowByRangesAndStations(paramMap);
	}

	public List<LinkedHashMap> snowArea(HashMap paramMap) {
		return snowMapper.snowArea(paramMap);
	}
	
}
