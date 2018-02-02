package com.spd.common;

import java.util.Date;

/**
 * 低温统计逐次结果。
 * @author Administrator
 *
 */
public class LowTmpResultTimes {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//开始期
	private String startDatetime;
	//开始日期
	private Date startDate;
	//结束期
	private String endDatetime;
	//结束日期
	private Date endDate;
	//持续候数
	private int persistHous;
	//平均气温
	private double avgTmp;
	//距平
	private double anomaly;
	//程度
	private String level;
	//年份 不需要在客户端展示
	private int year; 
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getAvgTmp() {
		return avgTmp;
	}
	public void setAvgTmp(double avgTmp) {
		this.avgTmp = avgTmp;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
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
	public int getPersistHous() {
		return persistHous;
	}
	public void setPersistHous(int persistHous) {
		this.persistHous = persistHous;
	}
	
}
