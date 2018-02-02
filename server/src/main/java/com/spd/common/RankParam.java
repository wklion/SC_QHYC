package com.spd.common;

public class RankParam {

	//查询的表名
	private String tableName;
	//期间 （统计方式）
	private String statisticsType;
	//过滤方式 （日值）
	private String filterType;
	//界限下值
	private double min;
	//界限上值
	private double max;
	//比较的界限值
	private double contrast;
	
	private int startMon;
	
	private int endMon;
	
	private int startDay;
	
	private int endDay;
	
	private int startYear;
	
	private int endYear;
	
	private int currentYear;
	//并列位次
	private boolean tie;
	//缺测率
	private double missingRatio;
	
	private String sortType;
	//站点类型
	private String stationType;
	
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	public double getContrast() {
		return contrast;
	}
	public void setContrast(double contrast) {
		this.contrast = contrast;
	}
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getStatisticsType() {
		return statisticsType;
	}
	public void setStatisticsType(String statisticsType) {
		this.statisticsType = statisticsType;
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
	public int getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}
	public double getMissingRatio() {
		return missingRatio;
	}
	public void setMissingRatio(double missingRatio) {
		this.missingRatio = missingRatio;
	}
	public boolean isTie() {
		return tie;
	}
	public void setTie(boolean tie) {
		this.tie = tie;
	}
	
	
}
