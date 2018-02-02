package com.spd.common;
/**
 * 小时雨量，建站时间
 * @author Administrator
 *
 */
public class HourRainStation {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//建站时间
	private String buildDate;
	
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
	public String getBuildDate() {
		return buildDate;
	}
	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}
	
}
