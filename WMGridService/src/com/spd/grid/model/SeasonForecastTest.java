package com.spd.grid.model;

import java.sql.Date;

/**
 * @author:杠上花
 *	@description:季节预报检验类
 */
public class SeasonForecastTest {
	private int id;
	private String elementID;//要素ID
	private Date makeDate;//制件时间
	private int season;//季度
	private String testName;//检验名称
	private String forecastName;//预报名称
	private String areaCode;//区域Code
	private double val;//值
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
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
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
