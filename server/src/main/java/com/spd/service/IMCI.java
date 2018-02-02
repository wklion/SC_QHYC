package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IMCI {

	public List<LinkedHashMap> queryMCIByTime(HashMap paramMap);

	public List<LinkedHashMap> mciStatisticsByTime(HashMap paramMap);

	public List<LinkedHashMap> mciStatisticsByTimeAndStation(HashMap paramMap);

	public List<LinkedHashMap> agmesoilStatisticsByTime(HashMap paramMap);

}
