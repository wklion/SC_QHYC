package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 天气现象
 * @author Administrator
 *
 */
public interface IWepMapper {

	public List<LinkedHashMap> queryByTimes(HashMap paramMap); 

	public List<LinkedHashMap> queryAllByTimes(HashMap paramMap); 

}
