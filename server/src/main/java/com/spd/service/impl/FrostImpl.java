package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IFrostMapper;
import com.spd.service.IFrost;

@Component("FrostImpl")
public class FrostImpl implements IFrost {

	@Resource
	private IFrostMapper frostMapper;
	
	public List<LinkedHashMap> queryFrostByOverYears(HashMap paramMap) {
		return frostMapper.queryFrostByOverYears(paramMap);
	}

	public List<LinkedHashMap> queryFrostByRangesAndStations(HashMap paramMap) {
		return frostMapper.queryFrostByRangesAndStations(paramMap);
	}

	public List<LinkedHashMap> queryFrostBySameYears(HashMap paramMap) {
		return frostMapper.queryFrostBySameYears(paramMap);
	}

	public List<LinkedHashMap> queryFrostByTimes(HashMap paramMap) {
		return frostMapper.queryFrostByTimes(paramMap);
	}

}
