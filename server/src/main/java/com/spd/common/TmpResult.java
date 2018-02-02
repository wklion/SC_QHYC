package com.spd.common;
/**
 * 持续统计结果类
 * @author Administrator
 *
 */
public class TmpResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始日期
	private String startTime;
	//结束日期
	private String endTime;
	//幅度
	private double scopeValue;
	//持续天数
	private double days;
	
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
	public double getDays() {
		return days;
	}
	public void setDays(double days) {
		this.days = days;
	}
	public double getScopeValue() {
		return scopeValue;
	}
	public void setScopeValue(double scopeValue) {
		this.scopeValue = scopeValue;
	}
	
}
