package com.spd.common.evaluate;


/**
 * 区域年度评价结果
 * @author Administrator
 *
 */
public class StrongCoolingYearResult implements Comparable<StrongCoolingYearResult>{
	//年份
	private int year;
	//CI指数
	private double CI;
	//常年CI指数
	private double yearsCI;
	//发生次数
	private int times;
	//常年次数
	private double yearsCnt;
	//等级
	private String level;
	
	public double getYearsCnt() {
		return yearsCnt;
	}
	public void setYearsCnt(double yearsCnt) {
		this.yearsCnt = yearsCnt;
	}
	public double getYearsCI() {
		return yearsCI;
	}
	public void setYearsCI(double yearsCI) {
		this.yearsCI = yearsCI;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getCI() {
		return CI;
	}
	public void setCI(double cI) {
		CI = cI;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int compareTo(StrongCoolingYearResult o) {
		return  year - o.getYear();
	}
	
}
