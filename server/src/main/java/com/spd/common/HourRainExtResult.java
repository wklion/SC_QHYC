package com.spd.common;
/**
 * 降水极值结果类
 * @author Administrator
 *
 */
public class HourRainExtResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//1小时
	private Double hour1;
	//3小时
	private Double hour3;
	//6小时
	private Double hour6;
	//12小时
	private Double hour12;
	//24小时
	private Double hour24;
	
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Double getHour1() {
		return hour1;
	}
	public void setHour1(Double hour1) {
		this.hour1 = hour1;
	}
	public Double getHour3() {
		return hour3;
	}
	public void setHour3(Double hour3) {
		this.hour3 = hour3;
	}
	public Double getHour6() {
		return hour6;
	}
	public void setHour6(Double hour6) {
		this.hour6 = hour6;
	}
	public Double getHour12() {
		return hour12;
	}
	public void setHour12(Double hour12) {
		this.hour12 = hour12;
	}
	public Double getHour24() {
		return hour24;
	}
	public void setHour24(Double hour24) {
		this.hour24 = hour24;
	}
	
}
