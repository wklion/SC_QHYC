package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IClimDataQuery {

	public List<LinkedHashMap> queryClimByTimesRangeAndElement(HashMap paramMap);
}
