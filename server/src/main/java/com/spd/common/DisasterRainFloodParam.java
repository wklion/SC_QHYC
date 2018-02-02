package com.spd.common;

/**
 * 洪涝灾害参数类
 * @author Administrator
 *
 */
public class DisasterRainFloodParam {
	//对应的查询表名
	private String tableName;
	//开始年
	private int startYear;
	//开始月
	private int startMon;
	//开始日
	private int startDay;
	//结束年
	private int endYear;
	//结束月
	private int endMon;
	//结束日
	private int endDay;
	//开始时间字符串表示
	private String startTime;
	//结束时间字符串表示
	private String endTime;
	//一般洪涝日雨量
	private double level11DayRain;
	//一般洪涝连续两日雨量
	private double level12DayRain;
	//一般洪涝连续3日雨量
	private double level13DayRain;
	//中度洪涝日雨量
	private double level21DayRain;
	//中度洪涝连续两日雨量
	private double level22DayRain;
	//中度洪涝连续3日雨量
	private double level23DayRain;
	//严重洪涝日雨量
	private double level31DayRain;
	//严重洪涝连续两日雨量
	private double level32DayRain;
	//严重洪涝连续3日雨量
	private double level33DayRain;
	
	public double getLevel11DayRain() {
		return level11DayRain;
	}
	public void setLevel11DayRain(double level11DayRain) {
		this.level11DayRain = level11DayRain;
	}
	public double getLevel12DayRain() {
		return level12DayRain;
	}
	public void setLevel12DayRain(double level12DayRain) {
		this.level12DayRain = level12DayRain;
	}
	public double getLevel13DayRain() {
		return level13DayRain;
	}
	public void setLevel13DayRain(double level13DayRain) {
		this.level13DayRain = level13DayRain;
	}
	public double getLevel21DayRain() {
		return level21DayRain;
	}
	public void setLevel21DayRain(double level21DayRain) {
		this.level21DayRain = level21DayRain;
	}
	public double getLevel22DayRain() {
		return level22DayRain;
	}
	public void setLevel22DayRain(double level22DayRain) {
		this.level22DayRain = level22DayRain;
	}
	public double getLevel23DayRain() {
		return level23DayRain;
	}
	public void setLevel23DayRain(double level23DayRain) {
		this.level23DayRain = level23DayRain;
	}
	public double getLevel31DayRain() {
		return level31DayRain;
	}
	public void setLevel31DayRain(double level31DayRain) {
		this.level31DayRain = level31DayRain;
	}
	public double getLevel32DayRain() {
		return level32DayRain;
	}
	public void setLevel32DayRain(double level32DayRain) {
		this.level32DayRain = level32DayRain;
	}
	public double getLevel33DayRain() {
		return level33DayRain;
	}
	public void setLevel33DayRain(double level33DayRain) {
		this.level33DayRain = level33DayRain;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
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
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
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
