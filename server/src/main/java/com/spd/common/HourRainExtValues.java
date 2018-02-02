package com.spd.common;

import java.util.Comparator;

/**
 * 指定时间段内查询极值，以及极值对应的日期 , 对应的结果类
 * @author Administrator
 *
 */
public class HourRainExtValues implements Comparator<HourRainExtValues> {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//极值
	private double extValue;
	//极值日期
	private String extTimes;
	//极值类型
	private String type;
	//建站时间
	private String buildDate;
	//历史排位
	private int sort;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getBuildDate() {
		return buildDate;
	}
	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
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
	public double getExtValue() {
		return extValue;
	}
	public void setExtValue(double extValue) {
		this.extValue = extValue;
	}
	public String getExtTimes() {
		return extTimes;
	}
	public void setExtTimes(String extTimes) {
		this.extTimes = extTimes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int compare(HourRainExtValues o1, HourRainExtValues o2) {
		String type1 = o1.getType();
		String type2 = o2.getType();
		Integer i1 = Integer.parseInt(type1.substring(1, type1.length()));
		Integer i2 = Integer.parseInt(type2.substring(1, type2.length()));
		if(i1 < i2) return -1;
		if(i1 == i2) return 0;
		if(i1 > i2) return 1;
		return 0;
	}
	
}
