package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IWepMapper;
import com.spd.service.IWep;

@Component("WepImpl")
public class WepImpl implements IWep {

	@Resource
	private IWepMapper wepMapper;
	
	public List<LinkedHashMap> queryAllByTimes(HashMap paramMap) {
		return wepMapper.queryAllByTimes(paramMap);
	}

	public List<LinkedHashMap> queryByTimes(HashMap paramMap) {
		return wepMapper.queryByTimes(paramMap);
	}

}
