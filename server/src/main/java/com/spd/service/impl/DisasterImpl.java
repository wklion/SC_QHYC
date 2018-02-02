package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IDisasterMapper;
import com.spd.service.IDisaster;

@Component("DisasterImpl")
public class DisasterImpl implements IDisaster{

	@Resource
	private IDisasterMapper disasterMapper;
	
	public List<LinkedHashMap> rainstorm(HashMap paramMap) {
		return disasterMapper.rainstorm(paramMap);
	}

	public List<LinkedHashMap> queryByYearsStations(HashMap paramMap) {
		return disasterMapper.queryByYearsStations(paramMap);
	}

}
