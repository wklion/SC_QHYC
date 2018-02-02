package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IClimDataQueryMapper;
import com.spd.service.IClimDataQuery;

@Component("ClimDataQueryImpl")
public class ClimDataQueryImpl implements IClimDataQuery {

	@Resource
	private IClimDataQueryMapper climDataQueryMapper;
	
	public List<LinkedHashMap> queryClimByTimesRangeAndElement(HashMap paramMap) {
		return climDataQueryMapper.queryClimByTimesRangeAndElement(paramMap);
	}

}
