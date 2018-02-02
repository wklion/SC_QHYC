package com.spd.common;

import java.util.Comparator;

/**
 * 雷暴历年统计结果类。
 * @author Administrator
 *
 */
public class ThundYearResult implements Comparator<ThundYearResult> {
	//年
	private int year;
	//常年次数
	private double yearsCnt;
	//当年次数
	private double currentCnt;
	//距平
	private double anomaly;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getYearsCnt() {
		return yearsCnt;
	}
	public void setYearsCnt(double yearsCnt) {
		this.yearsCnt = yearsCnt;
	}
	public double getCurrentCnt() {
		return currentCnt;
	}
	public void setCurrentCnt(double currentCnt) {
		this.currentCnt = currentCnt;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}
	public int compare(ThundYearResult o1, ThundYearResult o2) {
		int year1 = o1.getYear();
		int year2 = o2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return 0;
	}
	
}
