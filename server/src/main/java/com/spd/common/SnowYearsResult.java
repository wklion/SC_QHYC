package com.spd.common;

import java.util.Comparator;

/**
 * 降雪历年同期结果类
 * @author Administrator
 *
 */
public class SnowYearsResult implements Comparator {
	//年份
	private int year;
	//降雪日数
	private double snowDays;
	//积雪日数
	private double gssDays;
	//常年值
	private double gssYearsDays;
	//距平率
	private double gssDaysAnomalyRate;
	//最大积雪深度
	private double maxSnowDepth;
	
	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public double getSnowDays() {
		return snowDays;
	}


	public void setSnowDays(double snowDays) {
		this.snowDays = snowDays;
	}


	public double getGssDays() {
		return gssDays;
	}


	public void setGssDays(double gssDays) {
		this.gssDays = gssDays;
	}


	public double getGssYearsDays() {
		return gssYearsDays;
	}


	public void setGssYearsDays(double gssYearsDays) {
		this.gssYearsDays = gssYearsDays;
	}


	public double getGssDaysAnomalyRate() {
		return gssDaysAnomalyRate;
	}


	public void setGssDaysAnomalyRate(double gssDaysAnomalyRate) {
		this.gssDaysAnomalyRate = gssDaysAnomalyRate;
	}


	public double getMaxSnowDepth() {
		return maxSnowDepth;
	}


	public void setMaxSnowDepth(double maxSnowDepth) {
		this.maxSnowDepth = maxSnowDepth;
	}


	public int compare(Object o1, Object o2) {
		SnowYearsResult snowYearsResult1 = (SnowYearsResult) o1;
		SnowYearsResult snowYearsResult2 = (SnowYearsResult) o2;
		if(snowYearsResult1.getYear() < snowYearsResult2.getYear()) return -1;
		if(snowYearsResult1.getYear() == snowYearsResult2.getYear()) return 0;
		if(snowYearsResult1.getYear() > snowYearsResult2.getYear()) return 1;
		return 0;
	}
	
}
