package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IRainStormMapper;
import com.spd.service.IRainStorm;

@Component("RainStormImpl")
public class RainStormImpl implements IRainStorm {

	@Resource
	private IRainStormMapper rainStormMapper;
	
	public List<LinkedHashMap> queryRainStormByTimes(HashMap paramMap) {
		return rainStormMapper.queryRainStormByTimes(paramMap);
	}

	public List<LinkedHashMap> queryRainStormByTimesAndStations(HashMap paramMap) {
		return rainStormMapper.queryRainStormByTimesAndStations(paramMap);
	}

	public List<LinkedHashMap> queryRainStormByOverYearAndStations(
			HashMap paramMap) {
		return rainStormMapper.queryRainStormByOverYearAndStations(paramMap);
	}

	public List<LinkedHashMap> queryRainStormBySameYearAndStations(
			HashMap paramMap) {
		return rainStormMapper.queryRainStormBySameYearAndStations(paramMap);
	}
	
	public List<LinkedHashMap> rainstormByRange (
			HashMap paramMap) {
		return rainStormMapper.rainstormByRange(paramMap);
	}

	public List<LinkedHashMap> queryRainStormStationsByTime(HashMap paramMap) {
		return rainStormMapper.queryRainStormStationsByTime(paramMap);
	}

	public List<LinkedHashMap> queryRainStormByTimeAndStations(HashMap paramMap) {
		return rainStormMapper.queryRainStormByTimeAndStations(paramMap);
	}

	public List<LinkedHashMap> queryStatisticsRainStormByTimes(HashMap paramMap) {
		return rainStormMapper.queryStatisticsRainStormByTimes(paramMap);
	}
	
}
