package com.spd.common;

import java.util.Comparator;

/**
 * 历年同期统计结果
 * @author Administrator
 *
 */
public class RainStormYearsResult implements Comparator<RainStormYearsResult> {
	//年份
	private int year;
	//总次数
	private double cnt;
	//常年值
	private double yearsCnt;
	//距平
	private double cntAnomaly;
	//距平率
	private double cntAnomalyRate;
	//降水极值
	private double extValue;
	//出现日期
	private String extDatetime;
	//暴雨次数
	private double level1Cnt;
	//常年值
	private double yearsLevel1Cnt;
	//大暴雨次数
	private double level2Cnt;
	//常年值
	private double yearsLevel2Cnt;
	//特大暴雨次数
	private double level3Cnt;
	//常年值
	private double yearsLevel3Cnt;
	
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

	public double getCntAnomaly() {
		return cntAnomaly;
	}

	public void setCntAnomaly(double cntAnomaly) {
		this.cntAnomaly = cntAnomaly;
	}

	public double getCntAnomalyRate() {
		return cntAnomalyRate;
	}

	public void setCntAnomalyRate(double cntAnomalyRate) {
		this.cntAnomalyRate = cntAnomalyRate;
	}

	public double getExtValue() {
		return extValue;
	}

	public void setExtValue(double extValue) {
		this.extValue = extValue;
	}

	public String getExtDatetime() {
		return extDatetime;
	}

	public void setExtDatetime(String extDatetime) {
		this.extDatetime = extDatetime;
	}

	public double getLevel1Cnt() {
		return level1Cnt;
	}

	public void setLevel1Cnt(double level1Cnt) {
		this.level1Cnt = level1Cnt;
	}

	public double getYearsLevel1Cnt() {
		return yearsLevel1Cnt;
	}

	public void setYearsLevel1Cnt(double yearsLevel1Cnt) {
		this.yearsLevel1Cnt = yearsLevel1Cnt;
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

	public double getLevel3Cnt() {
		return level3Cnt;
	}

	public void setLevel3Cnt(double level3Cnt) {
		this.level3Cnt = level3Cnt;
	}

	public double getYearsLevel3Cnt() {
		return yearsLevel3Cnt;
	}

	public void setYearsLevel3Cnt(double yearsLevel3Cnt) {
		this.yearsLevel3Cnt = yearsLevel3Cnt;
	}

	public int compare(RainStormYearsResult o1, RainStormYearsResult o2) {
		int year1 = o1.getYear();
		int year2 = o2.getYear();
		if(year1 < year2) return -1;
		if(year1 == year2) return 0;
		if(year1 > year2) return 1;
		return 0;
	}
	
	
}
