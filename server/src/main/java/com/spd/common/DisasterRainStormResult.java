package com.spd.common;
/**
 * 灾害分析中逐次暴雨结果类
 * @author Administrator
 *
 */
public class DisasterRainStormResult {

	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//日期
	private String datetime;
	//降水量
	private double value;
	//强度
	private String level;
	//地区
	private String area;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
