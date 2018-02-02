package com.spd.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.common.Station;
import com.spd.mapper.ICommonMapper;
import com.spd.service.ICommon;

@Component("CommonImpl")
public class CommonImpl implements ICommon {

	@Resource
	private ICommonMapper commonMapper;
	
	public List<Station> getStationsByLevel(HashMap paramMap) {
		return commonMapper.getStationsByLevel(paramMap);
	}

	public List<LinkedHashMap> queryData(HashMap paramMap) {
		return commonMapper.queryData(paramMap);
	}

	public List<LinkedHashMap> queryDataByStations(HashMap paramMap) {
		return commonMapper.queryDataByStations(paramMap);
	}

	public List<LinkedHashMap> queryDataByStationsSets(HashMap paramMap) {
		return commonMapper.queryDataByStationsSets(paramMap);
	}

	public List<LinkedHashMap> getAllNationCityStations(HashMap paramMap) {
		return commonMapper.getAllNationCityStations(paramMap);
	}

	public List<LinkedHashMap> getAllStations() {
		return commonMapper.getAllStations();
	}

	public List<LinkedHashMap> queryByStation_Id_C(HashMap paramMap) {
		return commonMapper.queryByStation_Id_C(paramMap);
	}

	public List<LinkedHashMap> queryContrastByStation_Id_C(HashMap paramMap) {
		return commonMapper.queryContrastByStation_Id_C(paramMap);
	}

	public List<LinkedHashMap> queryStation_Id_CByCountry(HashMap paramMap) {
		return commonMapper.queryStation_Id_CByCountry(paramMap);
	}

	public List<LinkedHashMap> getAllContrastStations(HashMap paramMap) {
		return commonMapper.getAllContrastStations(paramMap);
	}

	public List<LinkedHashMap> queryStation_Id_CByAreaCode(HashMap paramMap) {
		return commonMapper.queryStation_Id_CByAreaCode(paramMap);
	}

	public List<LinkedHashMap> queryAuthorityByUserName(HashMap paramMap) {
		return commonMapper.queryAuthorityByUserName(paramMap);
	}

	public List<LinkedHashMap> getStationsByUser(HashMap paramMap) {
		return commonMapper.getStationsByUser(paramMap);
	}

	public List<LinkedHashMap> getAWSStationsOrderByIdC(HashMap paramMap) {
		return commonMapper.getAWSStationsOrderByIdC(paramMap);
	}

	public List<LinkedHashMap> getAWSStationsOrderBySeq(HashMap paramMap) {
		return commonMapper.getAWSStationsOrderBySeq(paramMap);
	}

	public List<LinkedHashMap> queryClimByTime(HashMap paramMap) {
		return commonMapper.queryClimByTime(paramMap);
	}

	public List<LinkedHashMap> getAllStationsByNationStations(HashMap paramMap) {
		return commonMapper.getAllStationsByNationStations(paramMap);
	}

	public List<LinkedHashMap> query(HashMap paramMap) {
		return commonMapper.query(paramMap);
	}

	public List<com.spd.sc.pojo.Station> queryPenDiStations() {
		return commonMapper.queryPenDiStations();
	}

}
