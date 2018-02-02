package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IThundMapper;
import com.spd.service.IThund;

@Component("ThundImpl")
public class ThundImpl implements IThund {

	@Resource
	private IThundMapper thundMapper;
	
	public List<LinkedHashMap> queryThundByRange(HashMap paramMap) {
		return thundMapper.queryThundByRange(paramMap);
	}

	public List<LinkedHashMap> queryThundByOverYears(HashMap paramMap) {
		return thundMapper.queryThundByOverYears(paramMap);
	}

	public List<LinkedHashMap> queryThundBySameYears(HashMap paramMap) {
		return thundMapper.queryThundBySameYears(paramMap);
	}

	public List<LinkedHashMap> queryThundCntByRange(HashMap paramMap) {
		return thundMapper.queryThundCntByRange(paramMap);
	}

}
