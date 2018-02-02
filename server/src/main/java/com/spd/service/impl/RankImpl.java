package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IRankMapper;
import com.spd.service.IRank;

@Component("RankImpl")
public class RankImpl implements IRank {

	@Resource
	private IRankMapper rankMapper;
	
	public List<Map> queryEle(HashMap paramMap) {
		return rankMapper.queryEle(paramMap);
	}

	public List<Map> queryEleOverYear(HashMap paramMap) {
		return rankMapper.queryEleOverYear(paramMap);
	}

}
