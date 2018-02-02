package com.spd.sc.pojo;
/**
 * 盆地大雨开始日期结果类
 * @author Administrator
 *
 */
public class PenDiMaxPreSeasonYearsResult {
	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始日期
	private String startTime;
	//结束时间 （保留）
	private String endTime;
	//持续天数 （保留）
	private int persistDays;
	//年份
	private int year;
	
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
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
}
