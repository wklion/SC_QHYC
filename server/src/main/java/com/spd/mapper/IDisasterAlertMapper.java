package com.spd.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDisasterAlertMapper {

	public List<Map> getCurrentLowTmpAreaAlert(HashMap paramMap);

	public List<Map> getCurrentHighTmpAreaAlert(HashMap paramMap);

	public List<Map> getCurrentContinueRainAreaAlert(HashMap paramMap);
	
	public List<Map> getLowTmpStationAlert(HashMap paramMap);

	public List<Map> getHighTmpStationAlert(HashMap paramMap);
	
	public List<Map> getContinueRainStationAlert(HashMap paramMap);

	public List<Map> getForecastByForecastTime(HashMap paramMap);

	public List<Map> getCurrentMCIAreaAlert(HashMap paramMap);

	public List<Map> getMCIAreaAlert(HashMap paramMap);
	
}
