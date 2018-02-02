package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IHighTmpMapper;
import com.spd.service.IHighTmp;

@Component("HighTmpImpl")
public class HighTmpImpl implements IHighTmp {

	@Resource
	private IHighTmpMapper highTmpMapper;
	
	public List<LinkedHashMap> queryHighTmpByRange(HashMap paramMap) {
		return highTmpMapper.queryHighTmpByRange(paramMap);
	}

	public List<LinkedHashMap> queryHighTmpByYears(HashMap paramMap) {
		return highTmpMapper.queryHighTmpByYears(paramMap);
	}

	public List<LinkedHashMap> queryHighTmpByStation(HashMap paramMap) {
		return highTmpMapper.queryHighTmpByStation(paramMap);
	}

}
