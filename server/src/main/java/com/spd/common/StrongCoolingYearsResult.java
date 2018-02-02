package com.spd.common;

import java.util.Comparator;

/**
 * 强降温历年统计结果类。
 * @author Administrator
 *
 */
public class StrongCoolingYearsResult implements Comparator<StrongCoolingYearsResult> {
	//年份
	private int year;
	//总次数
	private double cnt;
	//常年值
	private double yearsCnt;
	//距平率
	private double anomalyRate;
	//最强72小时降温
	private double mostLowerTmp72Hours;
	//特强降温次数
	private double level1LowerTmpCnt;
	//常年值
	private double yearsLevel1LowerTmpCnt;
	//距平率
	private double level1AnomalyRate;
	
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

	public double getAnomalyRate() {
		return anomalyRate;
	}

	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}

	public double getMostLowerTmp72Hours() {
		return mostLowerTmp72Hours;
	}

	public void setMostLowerTmp72Hours(double mostLowerTmp72Hours) {
		this.mostLowerTmp72Hours = mostLowerTmp72Hours;
	}

	public double getLevel1LowerTmpCnt() {
		return level1LowerTmpCnt;
	}

	public void setLevel1LowerTmpCnt(double level1LowerTmpCnt) {
		this.level1LowerTmpCnt = level1LowerTmpCnt;
	}

	public double getYearsLevel1LowerTmpCnt() {
		return yearsLevel1LowerTmpCnt;
	}

	public void setYearsLevel1LowerTmpCnt(double yearsLevel1LowerTmpCnt) {
		this.yearsLevel1LowerTmpCnt = yearsLevel1LowerTmpCnt;
	}

	public double getLevel1AnomalyRate() {
		return level1AnomalyRate;
	}

	public void setLevel1AnomalyRate(double level1AnomalyRate) {
		this.level1AnomalyRate = level1AnomalyRate;
	}

	public int compare(StrongCoolingYearsResult o1, StrongCoolingYearsResult o2) {
		int year1 = o1.getYear();
		int year2 = o2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return -1;
	}
	
	
	
}
