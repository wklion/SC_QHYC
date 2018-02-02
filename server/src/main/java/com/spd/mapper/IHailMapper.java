package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 冰雹
 * @author Administrator
 *
 */
public interface IHailMapper {

	public List<LinkedHashMap> queryByTimes(HashMap paramMap); 

}
