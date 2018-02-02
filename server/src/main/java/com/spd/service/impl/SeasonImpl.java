package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.ISeasonMapper;
import com.spd.sc.pojo.PenDiMaxPreResult;
import com.spd.sc.pojo.PenDiMaxPreSeasonYearsResult;
import com.spd.sc.pojo.RainySeasonResult;
import com.spd.service.ISeason;

@Component("SeasonImpl")
public class SeasonImpl implements ISeason {

	@Resource
	private ISeasonMapper seasonMapper;
	
	public List<LinkedHashMap> querySpringSeason(HashMap paramMap) {
		return seasonMapper.querySpringSeason(paramMap);
	}

	public List<LinkedHashMap> querySummerSeason(HashMap paramMap) {
		return seasonMapper.querySummerSeason(paramMap);
	}
	
	public List<LinkedHashMap> queryAutumnSeason(HashMap paramMap) {
		return seasonMapper.queryAutumnSeason(paramMap);
	}
	
	public List<LinkedHashMap> queryWinderSeason(HashMap paramMap) {
		return seasonMapper.queryWinderSeason(paramMap);
	}

	public List<LinkedHashMap> queryHistorySeason(HashMap paramMap) {
		return seasonMapper.queryHistorySeason(paramMap);
	}

	public List<PenDiMaxPreResult> pendiMaxPreSeason(HashMap paramMap) {
		return seasonMapper.pendiMaxPreSeason(paramMap);
	}

	public List<RainySeasonResult> southWestRainySeason(HashMap paramMap) {
		return seasonMapper.southWestRainySeason(paramMap);
	}

	public List<PenDiMaxPreSeasonYearsResult> pendiYearsMaxPreSeason(
			HashMap paramMap) {
		return seasonMapper.pendiYearsMaxPreSeason(paramMap);
	}
}
