package com.spd.common;
/**
 * 过程降水的逐次降水过程
 * @author Administrator
 *
 */
public class HourRainSequenceResult {
//	站号、
	private String station_Id_C;
	//站名
	private String station_Name;
//	开始时间、
	private String startTime;
//	结束时间、
	private String endTime;
//	持续时数、
	private int sumHours;
//	降水总量
	private double sumRain;
	//地区
	private String area;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getSumHours() {
		return sumHours;
	}
	public void setSumHours(int sumHours) {
		this.sumHours = sumHours;
	}
	public double getSumRain() {
		return sumRain;
	}
	public void setSumRain(double sumRain) {
		this.sumRain = sumRain;
	}
	
}
