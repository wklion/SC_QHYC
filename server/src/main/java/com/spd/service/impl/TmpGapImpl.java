package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.ITmpGapMapper;
import com.spd.service.ITmpGap;

@Component("TmpGapImpl")
public class TmpGapImpl implements ITmpGap {

	@Resource
	private ITmpGapMapper mapGapMapper;
	
	public List<Map> getLowTmpByTimes(HashMap paramMap) {
		return mapGapMapper.getLowTmpByTimes(paramMap);
	}

	public List<Map> getMaxTmpByTimes(HashMap paramMap) {
		return mapGapMapper.getMaxTmpByTimes(paramMap);
	}

	public List<Map> getGapTmpByTimes(HashMap paramMap) {
		return mapGapMapper.getGapTmpByTimes(paramMap);
	}

	public List<Map> getGapTmpByYears(HashMap paramMap) {
		return mapGapMapper.getGapTmpByYears(paramMap);
	}

	public List<Map> getTmpByYear(HashMap paramMap) {
		return mapGapMapper.getTmpByYear(paramMap);
	}

	public List<Map> getTmpGapByYears(HashMap paramMap) {
		return mapGapMapper.getTmpGapByYears(paramMap);
	}

	public List<Map> getAvgTmpGapByYears(HashMap paramMap) {
		return mapGapMapper.getAvgTmpGapByYears(paramMap);
	}

}
