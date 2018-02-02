package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IMaxWind {

	public List<LinkedHashMap> queryMaxWindByRanges(HashMap paramMap);

	public List<LinkedHashMap> queryMaxWindBySameYear(HashMap paramMap);

	public List<LinkedHashMap> queryMaxWindByOverYear(HashMap paramMap);
}
