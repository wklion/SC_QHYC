package com.spd.common;
/**
 * 日数统计参数类
 * @author Administrator
 *
 */
public class DaysParam {

	private int startYear;

	private int endYear;
	
	private int startMon;

	private int endMon;
	
	private int startDay;
	
	private int endDay;
	
	private String tableName;
	
	private String startDateTime;

	private String endDateTime;
	//界限下值
	private double min;
	//界限上值
	private double max;
	//比较的界限值
	private double contrast;
	
	private String filterType;
	
	private String stationType;
	//常年开始年
	private int constantStartYear;
	//常年结束年
	private int constantEndYear;
	
	public int getConstantStartYear() {
		return constantStartYear;
	}

	public void setConstantStartYear(int constantStartYear) {
		this.constantStartYear = constantStartYear;
	}

	public int getConstantEndYear() {
		return constantEndYear;
	}

	public void setConstantEndYear(int constantEndYear) {
		this.constantEndYear = constantEndYear;
	}

	public String getStationType() {
		return stationType;
	}

	public void setStationType(String stationType) {
		this.stationType = stationType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getContrast() {
		return contrast;
	}

	public void setContrast(double contrast) {
		this.contrast = contrast;
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

}
