package com.spd.common;
/**
 * 有效积温，年值
 * @author Administrator
 *
 */
public class ValidAccumulatedYearTemp {
	// 积温
	private double accumulatedTemp;
	//年份
	private int year;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getAccumulatedTemp() {
		return accumulatedTemp;
	}
	public void setAccumulatedTemp(double accumulatedTemp) {
		this.accumulatedTemp = accumulatedTemp;
	}
}
