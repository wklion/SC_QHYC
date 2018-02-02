package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IFogMapper;
import com.spd.service.IFog;

@Component("FogImpl")
public class FogImpl implements IFog {

	@Resource
	private IFogMapper fogMapper;
	
	public List<LinkedHashMap> queryFogByOverYears(HashMap paramMap) {
		return fogMapper.queryFogByOverYears(paramMap);
	}

	public List<LinkedHashMap> queryFogByRangesAndStations(HashMap paramMap) {
		return fogMapper.queryFogByRangesAndStations(paramMap);
	}

	public List<LinkedHashMap> queryFogBySameYears(HashMap paramMap) {
		return fogMapper.queryFogBySameYears(paramMap);
	}

	public List<LinkedHashMap> queryFogByTimes(HashMap paramMap) {
		return fogMapper.queryFogByTimes(paramMap);
	}

}
