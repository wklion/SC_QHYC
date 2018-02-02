package com.spd.common;

import java.util.Date;

/**
 * 连续变化结果类
 * @author Administrator
 *
 */
public class SequenceChangeYearResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//值
	private Double value;
	//多年均值
	private Double yearsValue;
	//日期
	private String datetime;
	//对应的开始日期
	private Date startDate;
	//对应的结束日期
	private Date endDate;
	
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
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getYearsValue() {
		return yearsValue;
	}
	public void setYearsValue(Double yearsValue) {
		this.yearsValue = yearsValue;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
