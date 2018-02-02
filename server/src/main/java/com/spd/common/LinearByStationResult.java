package com.spd.common;

import java.util.Map;

/**
 * 线性趋势的结果类
 * @author Administrator
 *
 */
public class LinearByStationResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//标准值
	private Double standardValue;
	//年份和值的Map对象
	private Map<String, Double> yearValuesMap;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public Double getStandardValue() {
		return standardValue;
	}
	public void setStandardValue(Double standardValue) {
		this.standardValue = standardValue;
	}
	public Map<String, Double> getYearValuesMap() {
		return yearValuesMap;
	}
	public void setYearValuesMap(Map<String, Double> yearValuesMap) {
		this.yearValuesMap = yearValuesMap;
	}
	
}
