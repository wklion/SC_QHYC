package com.spd.common;

public class SeasonResult {

	//序号
	private int index;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//年份
	private int year;
	//初日日期
	private String startDate;
	//季节时长
	private Integer persistDays;
	//常年
	private String hisStartDate;
	//距平
	private int anomaly;
	//早晚
	private String description;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getHisStartDate() {
		return hisStartDate;
	}
	public void setHisStartDate(String hisStartDate) {
		this.hisStartDate = hisStartDate;
	}
	public int getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(int anomaly) {
		this.anomaly = anomaly;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(Integer persistDays) {
		this.persistDays = persistDays;
	}
}
