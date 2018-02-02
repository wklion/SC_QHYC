package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 风云
 * @author Administrator
 *
 */
public interface IWinAvgCloCovMapper {

	public List<LinkedHashMap> queryWinAvg2MinByTimeRange(HashMap paramMap); 

	public List<LinkedHashMap> queryCloCovByTimeRange(HashMap paramMap); 

}
