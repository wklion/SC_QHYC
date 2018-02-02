package com.spd.qhyc.model;

import java.util.Date;

/**
 * @作者:wangkun
 * @日期:2017年12月28日
 * @公司:spd
 * @说明:
*/
public class ForcastData {
	private int id;
	private String method;
	private Date makeDate;
	private String forecastDate;
	private String stationNum;
	private double val;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public Date getMakeDate() {
		return makeDate;
	}
	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}
	
	public String getForecastDate() {
		return forecastDate;
	}
	public void setForecastDate(String forecastDate) {
		this.forecastDate = forecastDate;
	}
	public String getStationNum() {
		return stationNum;
	}
	public void setStationNum(String stationNum) {
		this.stationNum = stationNum;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
	
}
