package com.spd.sc.pojo;

public class SeasonByYearParam {

	private int year;
	
	private int startMon;
	
	private int startDay;
	
	private int endMon;
	
	private int endDay;

	private String season;
	
	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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
