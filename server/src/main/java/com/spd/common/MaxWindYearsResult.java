package com.spd.common;

import java.util.Comparator;

/**
 * 大风灾害历年同期结果
 * @author Administrator
 *
 */
public class MaxWindYearsResult implements Comparator{
	//年份
	private int year;
	//总次数
	private double cnt;
	//常年值
	private double yearsCnt;
	//距平率
	private Double anomalyRate;
	//轻度次数
	private int mildCnt;
	//中度次数
	private int moderateCnt;
	//严重次数
	private int severityCnt;
	

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


	public double getYearsCnt() {
		return yearsCnt;
	}


	public void setYearsCnt(double yearsCnt) {
		this.yearsCnt = yearsCnt;
	}


	public Double getAnomalyRate() {
		return anomalyRate;
	}


	public void setAnomalyRate(Double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}


	public int getMildCnt() {
		return mildCnt;
	}


	public void setMildCnt(int mildCnt) {
		this.mildCnt = mildCnt;
	}


	public int getModerateCnt() {
		return moderateCnt;
	}


	public void setModerateCnt(int moderateCnt) {
		this.moderateCnt = moderateCnt;
	}


	public int getSeverityCnt() {
		return severityCnt;
	}


	public void setSeverityCnt(int severityCnt) {
		this.severityCnt = severityCnt;
	}


	public int compare(Object o1, Object o2) {
		MaxWindYearsResult item1 = (MaxWindYearsResult) o1;
		MaxWindYearsResult item2 = (MaxWindYearsResult) o2;
		int year1 = item1.getYear();
		int year2 = item2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return 0;
	}
	
}
