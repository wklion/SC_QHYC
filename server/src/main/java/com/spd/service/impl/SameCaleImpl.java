package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.ISameCaleMapper;
import com.spd.service.ISameCale;

@Component("SameCaleImpl")
public class SameCaleImpl implements ISameCale {

	@Resource
	private ISameCaleMapper sameCaleMapper;

	public List<Map> queryAllEle(HashMap paramMap) {
		return sameCaleMapper.queryAllEle(paramMap);
	}

	public List<Map> queryAllEleOverYear(HashMap paramMap) {
		return sameCaleMapper.queryAllEleOverYear(paramMap);
	}

	public List<Map> queryEleByStations(HashMap paramMap) {
		return sameCaleMapper.queryEleByStations(paramMap);
	}

	public List<Map> queryEleOverYearByStations(HashMap paramMap) {
		return sameCaleMapper.queryEleOverYearByStations(paramMap);
	}

	public List<Map> queryAWSEle(HashMap paramMap) {
		return sameCaleMapper.queryAWSEle(paramMap);
	}
	

}
