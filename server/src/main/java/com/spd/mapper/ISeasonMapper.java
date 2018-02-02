package com.spd.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.sc.pojo.PenDiMaxPreResult;
import com.spd.sc.pojo.PenDiMaxPreSeasonYearsResult;
import com.spd.sc.pojo.RainySeasonResult;


public interface ISeasonMapper {

	public List<LinkedHashMap> querySpringSeason(HashMap paramMap); 
	
	public List<LinkedHashMap> querySummerSeason(HashMap paramMap); 
	
	public List<LinkedHashMap> queryAutumnSeason(HashMap paramMap); 
	
	public List<LinkedHashMap> queryWinderSeason(HashMap paramMap); 

	public List<LinkedHashMap> queryHistorySeason(HashMap paramMap); 

	public List<RainySeasonResult> southWestRainySeason(HashMap paramMap); 

	public List<PenDiMaxPreResult> pendiMaxPreSeason(HashMap paramMap); 

	public List<PenDiMaxPreSeasonYearsResult> pendiYearsMaxPreSeason(HashMap paramMap); 
	
}
