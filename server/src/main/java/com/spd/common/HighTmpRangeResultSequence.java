package com.spd.common;

/**
 * 按时间段统计高温结果类。逐次
 * @author Administrator
 *
 */
public class HighTmpRangeResultSequence {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String country;
	//日期
	private String datetime;
	//程度
	private String level;
	//高温
	private Double tem_Max;
	
	public Double getTem_Max() {
		return tem_Max;
	}
	public void setTem_Max(Double temMax) {
		tem_Max = temMax;
	}
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
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	
}
