package com.spd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spd.mapper.IDisasterAlertMapper;
import com.spd.service.IDisasterAlert;

@Component("DisasterAlert")
public class DisasterAlert implements IDisasterAlert {

	@Resource
	private IDisasterAlertMapper disasterAlertMapper;
	
	public List<Map> getCurrentLowTmpAreaAlert(HashMap paramMap) {
		return disasterAlertMapper.getCurrentLowTmpAreaAlert(paramMap);
	}

	public List<Map> getCurrentHighTmpAreaAlert(HashMap paramMap) {
		return disasterAlertMapper.getCurrentHighTmpAreaAlert(paramMap);
	}
	
	public List<Map> getCurrentContinueRainAreaAlert(HashMap paramMap) {
		return disasterAlertMapper.getCurrentContinueRainAreaAlert(paramMap);
	}

	public List<Map> getContinueRainStationAlert(HashMap paramMap) {
		return disasterAlertMapper.getContinueRainStationAlert(paramMap);
	}

	public List<Map> getHighTmpStationAlert(HashMap paramMap) {
		return disasterAlertMapper.getHighTmpStationAlert(paramMap);
	}

	public List<Map> getLowTmpStationAlert(HashMap paramMap) {
		return disasterAlertMapper.getLowTmpStationAlert(paramMap);
	}

	public List<Map> getForecastByForecastTime(HashMap paramMap) {
		return disasterAlertMapper.getForecastByForecastTime(paramMap);
	}

	public List<Map> getCurrentMCIAreaAlert(HashMap paramMap) {
		return disasterAlertMapper.getCurrentMCIAreaAlert(paramMap);
	}

	public List<Map> getMCIAreaAlert(HashMap paramMap) {
		return disasterAlertMapper.getMCIAreaAlert(paramMap);
	}
}
