package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IPersistMapper;
import com.spd.service.IPersist;

@Component("PersistImpl")
public class PersistImpl implements IPersist {

	@Resource
	private IPersistMapper persistMapper;
	
	/**
	 *  持续统计所有站
	 * @param paramMap
	 * @return
	 */
	public List<Map> persistAll(HashMap paramMap) {
		return persistMapper.queryAll(paramMap);
	}

	/**
	 * 根据站名做统计
	 * @param paramMap
	 * @return
	 */
	public List<Map> persistByStations(HashMap paramMap) {
		return persistMapper.queryByStations(paramMap);
	}

	public List<Map> tmp(HashMap paramMap) {
		return persistMapper.queryTmp(paramMap);
	}

	public List<Map> rain(HashMap paramMap) {
		return persistMapper.queryRain(paramMap);
	}

}
