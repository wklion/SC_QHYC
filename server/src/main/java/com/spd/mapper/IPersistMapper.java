package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IPersistMapper {

	public List<Map> queryAll(HashMap paramMap); 

	public List<Map> queryByStations(HashMap paramMap); 

	public List<Map> queryTmp(HashMap paramMap); 

	public List<Map> queryRain(HashMap paramMap); 
	
}
