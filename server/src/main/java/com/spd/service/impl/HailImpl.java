package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IHailMapper;
import com.spd.service.IHail;

@Component("HailImpl")
public class HailImpl implements IHail {

	@Resource
	private IHailMapper hailMapper;
	
	public List<LinkedHashMap> queryByTimes(HashMap paramMap) {
		return hailMapper.queryByTimes(paramMap);
	}

}
