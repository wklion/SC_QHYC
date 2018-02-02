package com.spd.sc.pojo;

public class SeasonByStationYearsParam {
	
	//站号
	private String station_Id_C;
	//开始年
	private int startYear;
	//结束年
	private int endYear;
	//季节：春夏秋冬
	private String season;
	//开始月
	private int startMon;
	//开始日
	private int startDay;
	//结束月
	private int endMon;
	//结束日
	private int endDay;
	
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	public int getStartMon() {
		return startMon;
	}
	public void setStartMon(int startMon) {
		this.startMon = startMon;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getEndMon() {
		return endMon;
	}
	public void setEndMon(int endMon) {
		this.endMon = endMon;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
}
