package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IHouMapper;
import com.spd.service.IHou;

@Component("HouImpl")
public class HouImpl implements IHou {

	@Resource
	private IHouMapper houMapper;
	
	public List<LinkedHashMap> queryHouTmpData(HashMap paramMap) {
		return houMapper.queryHouTmpData(paramMap);
	}

	public List<LinkedHashMap> queryHouTmpDataByYears(HashMap paramMap) {
		return houMapper.queryHouTmpDataByYears(paramMap);
	}
	
	public List<LinkedHashMap> queryHouTmpDataByTimes(HashMap paramMap) {
		return houMapper.queryHouTmpDataByTimes(paramMap);
	}

}
