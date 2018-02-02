package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IDataCompleteMapper;
import com.spd.service.IDataComplete;

@Component("DataCompleteImpl")
public class DataCompleteImpl implements IDataComplete {

	@Resource
	private IDataCompleteMapper dataCompleteMapper;
	
	public List<LinkedHashMap> getDataComplete(HashMap paramMap) {
		return dataCompleteMapper.getDataComplete(paramMap);
	}

}
