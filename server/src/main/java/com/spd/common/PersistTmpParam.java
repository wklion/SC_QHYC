package com.spd.common;
/**
 * 持续统计中的气温统计参数类
 * @author Administrator
 *
 */
public class PersistTmpParam {

	private int startYear;

	private int endYear;
	
	private int startMon;

	private int endMon;
	
	private int startDay;
	
	private int endDay;
	
	private String startDateTime;

	private String endDateTime;
	//统计方式，升温，或者降温,UP,DOWN
	private String type;
	
	private String stationType;
	
	public String getStationType() {
		return stationType;
	}

	public void setStationType(String stationType) {
		this.stationType = stationType;
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
	public int getEndMon() {
		return endMon;
	}
	public void setEndMon(int endMon) {
		this.endMon = endMon;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
