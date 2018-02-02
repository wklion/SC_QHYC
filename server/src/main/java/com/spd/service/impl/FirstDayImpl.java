package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import com.spd.mapper.IFirstDayMapper;
import com.spd.service.IFirstDay;

@Component("FirstDayImpl")
public class FirstDayImpl implements IFirstDay {

	@Resource
	private IFirstDayMapper firstDayMapper;
	
	public List<Map> getRainTmpByTimeRange(HashMap paramMap) {
		return firstDayMapper.queryRainTmpByTimeRange(paramMap);
	}

	public List<Map> getRainTmpByYear(HashMap paramMap) {
		return firstDayMapper.queryRainTmpByYear(paramMap);
	}

}
