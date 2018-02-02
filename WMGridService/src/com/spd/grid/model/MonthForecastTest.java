package com.spd.grid.model;

import java.sql.Date;

public class MonthForecastTest {
	private int id;
	private String elementID;
	private Date makeDate;
	private String forecastDate;
	private String testName;
	private String forecastName;
	private String areaCode;
	private double val;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getElementID() {
		return elementID;
	}
	public void setElementID(String elementID) {
		this.elementID = elementID;
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
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getForecastName() {
		return forecastName;
	}
	public void setForecastName(String forecastName) {
		this.forecastName = forecastName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
	
}
