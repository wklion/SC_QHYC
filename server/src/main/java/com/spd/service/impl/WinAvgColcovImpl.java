package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IWinAvgCloCovMapper;
import com.spd.service.IWinAvgColcov;

@Component("WinAvgColcovImpl")
public class WinAvgColcovImpl implements IWinAvgColcov {

	@Resource
	private IWinAvgCloCovMapper winAvgCloCovMapper;
	
	public List<LinkedHashMap> queryWinAvg2MinByTimeRange(HashMap paramMap) {
		return winAvgCloCovMapper.queryWinAvg2MinByTimeRange(paramMap);
	}

	public List<LinkedHashMap> queryCloCovByTimeRange(HashMap paramMap) {
		return winAvgCloCovMapper.queryCloCovByTimeRange(paramMap);
	}

}
