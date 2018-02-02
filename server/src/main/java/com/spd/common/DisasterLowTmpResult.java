package com.spd.common;

import java.util.Date;

/**
 * 低温分析结果类
 * @author Administrator
 *
 */
public class DisasterLowTmpResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//平均气温
	private double avgTmp;
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
	//程度
	private String level;
	//降水量
	private double preValue;
	//白天雨日数
	private int dayRainTimes;
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
	public double getAvgTmp() {
		return avgTmp;
	}
	public void setAvgTmp(double avgTmp) {
		this.avgTmp = avgTmp;
	}
	public String getStartDatetime() {
		return startDatetime;
	}
	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}
	public String getEndDatetime() {
		return endDatetime;
	}
	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getDayRainTimes() {
		return dayRainTimes;
	}
	public void setDayRainTimes(int dayRainTimes) {
		this.dayRainTimes = dayRainTimes;
	}
	public int getNoSunDays() {
		return noSunDays;
	}
	public void setNoSunDays(int noSunDays) {
		this.noSunDays = noSunDays;
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
}
