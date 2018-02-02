package com.spd.common;

import java.util.Map;

/**
 * 高温日期结果序列
 * @author Administrator
 *
 */
public class HighTmpSequence {
	// 站号
	private String station_Id_C;
	// 站名
	private String station_Name;
	//日期，值
	private Map valueMap;
	
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
	public Map getValueMap() {
		return valueMap;
	}
	public void setValueMap(Map valueMap) {
		this.valueMap = valueMap;
	}
	
}
