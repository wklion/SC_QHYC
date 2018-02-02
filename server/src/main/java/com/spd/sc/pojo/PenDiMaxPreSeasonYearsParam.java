package com.spd.sc.pojo;

public class PenDiMaxPreSeasonYearsParam {
	//站号
	private String station_Id_C;
	//开始年
	private int startYear;
	//结束年
	private int endYear;
	
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
	
}
