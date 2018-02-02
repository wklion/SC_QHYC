package com.spd.common;
/**
 * 极值统计参数类
 * @author Administrator
 *
 */
public class ExtParam {

	private int startYear;

	private int endYear;
	
	private int startMon;

	private int endMon;
	
	private int startDay;
	
	private int endDay;
	
	private boolean isHistory;

	private String tableName;
	
	private String startDateTime;

	private String endDateTime;
	
	private String stationType;
	
	public String getStationType() {
		return stationType;
	}

	public void setStationType(String stationType) {
		this.stationType = stationType;
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getEndMon() {
		return endMon;
	}

	public void setEndMon(int endMon) {
		this.endMon = endMon;
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

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public boolean isHistory() {
		return isHistory;
	}

	public void setHistory(boolean isHistory) {
		this.isHistory = isHistory;
	}
	
}
