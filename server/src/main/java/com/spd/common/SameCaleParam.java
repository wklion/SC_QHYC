package com.spd.common;

public class SameCaleParam {
	private boolean groupByStation;
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
	
	private Integer startMon;
	
	private Integer endMon;
	
	private Integer startDay;
	
	private Integer endDay;
	
	private int startYear;
	
	private int endYear;
	
	private int currentYear;
	//缺测率
	private double missingRatio;
	//气候标准值，开始年份
	private int standardStartYear;
	//气候标准值，结束年份
	private int standardEndYear;
	//站点
	private String station_ID_C;
	
	private String stationType;
	//月份，月份数组作为参数，和 startMon, endMon, startDay, endDay互斥
	private int[] monthes;
	//结果展示方式，年、年代两种 1:年 2:年代，默认是年
	private Integer resultDisplayType;
	
	public boolean isGroupByStation() {
		return groupByStation;
	}
	public void setGroupByStation(boolean groupByStation) {
		this.groupByStation = groupByStation;
	}
	public Integer getResultDisplayType() {
		return resultDisplayType;
	}
	public void setResultDisplayType(Integer resultDisplayType) {
		this.resultDisplayType = resultDisplayType;
	}
	public int[] getMonthes() {
		return monthes;
	}
	public void setMonthes(int[] monthes) {
		this.monthes = monthes;
	}
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	public String getStation_ID_C() {
		return station_ID_C;
	}
	public void setStation_ID_C(String stationIDC) {
		station_ID_C = stationIDC;
	}
	public double getContrast() {
		return contrast;
	}
	public void setContrast(double contrast) {
		this.contrast = contrast;
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
	public Integer getStartMon() {
		return startMon;
	}
	public void setStartMon(Integer startMon) {
		this.startMon = startMon;
	}
	public Integer getEndMon() {
		return endMon;
	}
	public void setEndMon(Integer endMon) {
		this.endMon = endMon;
	}
	public Integer getStartDay() {
		return startDay;
	}
	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}
	public Integer getEndDay() {
		return endDay;
	}
	public void setEndDay(Integer endDay) {
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
	public int getStandardStartYear() {
		return standardStartYear;
	}
	public void setStandardStartYear(int standardStartYear) {
		this.standardStartYear = standardStartYear;
	}
	public int getStandardEndYear() {
		return standardEndYear;
	}
	public void setStandardEndYear(int standardEndYear) {
		this.standardEndYear = standardEndYear;
	}
	
}
