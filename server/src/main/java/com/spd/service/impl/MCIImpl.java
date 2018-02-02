package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IMCIMapper;
import com.spd.service.IMCI;

@Component("MCIImpl")
public class MCIImpl implements IMCI {

	@Resource
	private IMCIMapper mciMapper;
	
	public List<LinkedHashMap> queryMCIByTime(HashMap paramMap) {
		return mciMapper.queryMCIByTime(paramMap);
	}

	public List<LinkedHashMap> mciStatisticsByTime(HashMap paramMap) {
		return mciMapper.mciStatisticsByTime(paramMap);
	}

	public List<LinkedHashMap> mciStatisticsByTimeAndStation(HashMap paramMap) {
		return mciMapper.mciStatisticsByTimeAndStation(paramMap);
	}

	public List<LinkedHashMap> agmesoilStatisticsByTime(HashMap paramMap) {
		return mciMapper.agmesoilStatisticsByTime(paramMap);
	}

}
