package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISameCaleMapper {

	public List<Map> queryAllEle(HashMap paramMap);

	public List<Map> queryAWSEle(HashMap paramMap);

	public List<Map> queryAllEleOverYear(HashMap paramMap);

	public List<Map> queryEleByStations(HashMap paramMap);

	public List<Map> queryEleOverYearByStations(HashMap paramMap);
}
