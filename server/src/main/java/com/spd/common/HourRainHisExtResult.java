package com.spd.common;

import java.util.Comparator;

/**
 * 小时雨量 历史极值 结果
 * @author Administrator
 *
 */
public class HourRainHisExtResult implements Comparator<HourRainHisExtResult>{
	//年份
	private int year;
	//极值
	private double extValue;
	//极值日期
	private String extTimes;
	//时长
//	private int persistHours;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
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
//	public int getPersistHours() {
//		return persistHours;
//	}
//	public void setPersistHours(int persistHours) {
//		this.persistHours = persistHours;
//	}
	
	public int compare(HourRainHisExtResult o1, HourRainHisExtResult o2) {
		int year1 = o1.getYear();
		int year2 = o2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return 0;
	}
	
}
