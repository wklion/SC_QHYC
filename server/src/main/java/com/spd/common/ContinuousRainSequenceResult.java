package com.spd.common;

import java.util.Date;

/**
 * 连阴雨逐次结果定义类
 * @author Administrator
 *
 */
public class ContinuousRainSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//年份
	private int year;
	//开始期
	private String startDatetime;
	//开始日期
	private Date startDate;
	//结束期
	private String endDatetime;
	//结束日期
	private Date endDate;
	//持续天数
	private int persistDays;
	//无照日数
	private int noSunDays;
	//有雨日数
	private int preDays;
	//程度
	private String level;
	//降水量
	private double preValue;
	//最大日雨量
	private double maxDayPreValue;
	
	public double getMaxDayPreValue() {
		return maxDayPreValue;
	}
	public void setMaxDayPreValue(double maxDayPreValue) {
		this.maxDayPreValue = maxDayPreValue;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String getStartDatetime() {
		return startDatetime;
	}
	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getEndDatetime() {
		return endDatetime;
	}
	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getNoSunDays() {
		return noSunDays;
	}
	public void setNoSunDays(int noSunDays) {
		this.noSunDays = noSunDays;
	}
	public int getPreDays() {
		return preDays;
	}
	public void setPreDays(int preDays) {
		this.preDays = preDays;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public double getPreValue() {
		return preValue;
	}
	public void setPreValue(double preValue) {
		this.preValue = preValue;
	}
	
	public ContinuousRainSequenceResult copy() {
		ContinuousRainSequenceResult copy = new ContinuousRainSequenceResult();
		copy.setEndDate(this.endDate);
		copy.setEndDatetime(this.endDatetime);
		copy.setLevel(this.level);
		copy.setNoSunDays(this.noSunDays);
		copy.setPersistDays(this.persistDays);
		copy.setPreDays(this.preDays);
		copy.setPreValue(this.preValue);
		copy.setStartDate(this.startDate);
		copy.setStartDatetime(this.startDatetime);
		copy.setStation_Id_C(this.station_Id_C);
		copy.setStation_Name(this.station_Name);
		copy.setYear(this.year);
		return copy;
	}
}
