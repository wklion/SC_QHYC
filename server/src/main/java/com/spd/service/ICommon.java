package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.common.Station;

public interface ICommon {

	public List<Station> getStationsByLevel(HashMap paramMap);
	
	public List<LinkedHashMap> queryData(HashMap paramMap); 
	
	public List<LinkedHashMap> queryDataByStations(HashMap paramMap); 
	
	public List<LinkedHashMap> queryDataByStationsSets(HashMap paramMap); 

	public List<LinkedHashMap> getAllNationCityStations(HashMap paramMap); 

	public List<LinkedHashMap> getAllStationsByNationStations(HashMap paramMap); 

	public List<LinkedHashMap> getAllContrastStations(HashMap paramMap); 

	public List<LinkedHashMap> getAllStations(); 

	public List<LinkedHashMap> queryByStation_Id_C(HashMap paramMap); 

	public List<LinkedHashMap> queryContrastByStation_Id_C(HashMap paramMap); 

	public List<LinkedHashMap> queryStation_Id_CByCountry(HashMap paramMap); 

	public List<LinkedHashMap> queryStation_Id_CByAreaCode(HashMap paramMap); 

	public List<LinkedHashMap> queryAuthorityByUserName(HashMap paramMap); 

	public List<LinkedHashMap> getStationsByUser(HashMap paramMap); 

	public List<LinkedHashMap> getAWSStationsOrderBySeq(HashMap paramMap); 
	
	public List<LinkedHashMap> getAWSStationsOrderByIdC(HashMap paramMap); 

	public List<LinkedHashMap> queryClimByTime(HashMap paramMap); 

	public List<LinkedHashMap> query(HashMap paramMap); 

	public List<com.spd.sc.pojo.Station> queryPenDiStations(); 

}
