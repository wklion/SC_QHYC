package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IMaxWindMapper;
import com.spd.service.IMaxWind;

@Component("MaxWindImpl")
public class MaxWindImpl implements IMaxWind {

	@Resource
	private IMaxWindMapper maxWindMapper;
	
	public List<LinkedHashMap> queryMaxWindByRanges(HashMap paramMap) {
		return maxWindMapper.queryMaxWindByRanges(paramMap);
	}

	public List<LinkedHashMap> queryMaxWindByOverYear(HashMap paramMap) {
		return maxWindMapper.queryMaxWindByOverYear(paramMap);
	}

	public List<LinkedHashMap> queryMaxWindBySameYear(HashMap paramMap) {
		return maxWindMapper.queryMaxWindBySameYear(paramMap);
	}

}
