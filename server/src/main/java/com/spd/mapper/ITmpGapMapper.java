package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ITmpGapMapper {

	public List<Map> getLowTmpByTimes(HashMap paramMap);

	public List<Map> getMaxTmpByTimes(HashMap paramMap);

	public List<Map> getGapTmpByTimes(HashMap paramMap);

	public List<Map> getGapTmpByYears(HashMap paramMap);

	public List<Map> getTmpByYear(HashMap paramMap);

	public List<Map> getTmpGapByYears(HashMap paramMap);

	public List<Map> getAvgTmpGapByYears(HashMap paramMap);
	
}
