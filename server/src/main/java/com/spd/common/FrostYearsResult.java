package com.spd.common;

import java.util.Comparator;

/**
 * 霜冻历年统计结果
 * @author Administrator
 *
 */
public class FrostYearsResult implements Comparator<FrostYearsResult> {
	//年份
	private int year;
	//总次数
	private double cnt;
	//常年值
	private double yearCnt;
	//距平率
	private double anomalyRate;
	//严重次数
	private double level2Cnt;
	//常年值
	private double yearsLevel2Cnt;
	//距平率
	private double level2AnomalyRate;
	
	
	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public double getCnt() {
		return cnt;
	}


	public void setCnt(double cnt) {
		this.cnt = cnt;
	}


	public double getYearCnt() {
		return yearCnt;
	}


	public void setYearCnt(double yearCnt) {
		this.yearCnt = yearCnt;
	}


	public double getAnomalyRate() {
		return anomalyRate;
	}


	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}


	public double getLevel2Cnt() {
		return level2Cnt;
	}


	public void setLevel2Cnt(double level2Cnt) {
		this.level2Cnt = level2Cnt;
	}


	public double getYearsLevel2Cnt() {
		return yearsLevel2Cnt;
	}


	public void setYearsLevel2Cnt(double yearsLevel2Cnt) {
		this.yearsLevel2Cnt = yearsLevel2Cnt;
	}


	public double getLevel2AnomalyRate() {
		return level2AnomalyRate;
	}


	public void setLevel2AnomalyRate(double level2AnomalyRate) {
		this.level2AnomalyRate = level2AnomalyRate;
	}


	public int compare(FrostYearsResult o1, FrostYearsResult o2) {
		int year1 = o1.getYear();
		int year2 = o2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return 0;
	}
	
}
