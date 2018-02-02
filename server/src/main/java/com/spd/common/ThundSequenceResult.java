package com.spd.common;

/**
 * 雷暴结果序列
 * @author Administrator
 *
 */
public class ThundSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//发生事件
	private String datetime;
	
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
}
