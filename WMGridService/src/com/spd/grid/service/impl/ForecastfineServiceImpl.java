package com.spd.grid.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.grid.mapper.ForecastfineMapper;
import com.spd.grid.service.IForecastfineService;

@Component("ForecastfineService")
public class ForecastfineServiceImpl implements IForecastfineService {

	@Resource
	private ForecastfineMapper forecastfineMapper;
	
	public ForecastfineMapper getForecastfineMapper() {
		return forecastfineMapper;
	}

	public void setForecastfineMapper(ForecastfineMapper forecastfineMapper) {
		this.forecastfineMapper = forecastfineMapper;
	}

	@Override
	public List<Map> getUserStation(HashMap paraMap) {
		return forecastfineMapper.getUserStation(paraMap);
	}

	@Override
	public List<Map> getUserStationNew(HashMap paraMap) {
		return forecastfineMapper.getUserStationNew(paraMap);
	}
	
	@Override
	public List<Map> getZDYBPublishTime(HashMap paraMap) {
		return forecastfineMapper.getZDYBPublishTime(paraMap);
	}
	
	@Override
	public List<Map> getZDYBType(HashMap paraMap) {
		return forecastfineMapper.getZDYBType(paraMap);
	}
	
	@Override
	public List<Map> getZDYBSet(HashMap paraMap) {
		return forecastfineMapper.getZDYBSet(paraMap);
	}

	@Override
	public List<Map> updateZDYBSet(HashMap paraMap) {
		return forecastfineMapper.updateZDYBSet(paraMap);
	}
	
	@Override
	public List<Map> insertZDYBSet(HashMap paraMap) {
		return forecastfineMapper.insertZDYBSet(paraMap);
	}
	
	@Override
	public List<Map> getZDYBElement(HashMap paraMap) {
		return forecastfineMapper.getZDYBElement(paraMap);
	}
	
	@Override
	public List<Map> getZDYBOutType(HashMap paraMap) {
		return forecastfineMapper.getZDYBOutType(paraMap);
	}
	
	@Override
	public List<Map> getZDYBStationType(HashMap paraMap) {
		return forecastfineMapper.getZDYBStationType(paraMap);
	}
	
	@Override
	public List<Map> getGDYBPublishTime(HashMap paraMap) {
		return forecastfineMapper.getGDYBPublishTime(paraMap);
	}
	
	@Override
	public List<Map> deleteProductTime(HashMap paraMap) {
		return forecastfineMapper.deleteProductTime(paraMap);
	}
	
	@Override
	public List<Map> addProductType(HashMap paraMap) {
		return forecastfineMapper.addProductType(paraMap);
	}
	
	@Override
	public List<Map> addStationType(HashMap paraMap) {
		return forecastfineMapper.addStationType(paraMap);
	}

	@Override
	public List<Map> getStation() {
		return forecastfineMapper.getStation();
	}
}
