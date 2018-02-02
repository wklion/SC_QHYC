package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDaysMapper {

	public List<Map> queryDays(HashMap paramMap); 

	public List<Map> statisticsHisDays(HashMap paramMap); 
	
}
