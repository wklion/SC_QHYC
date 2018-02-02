package com.spd.common.evaluate;
/**
 * 年度连阴雨结果类
 * @author Administrator
 *
 */
public class ContinueRainYearResult implements Comparable<ContinueRainYearResult>{
//	//开始时间
//	private String startTime;
//	//结束时间
//	private String endTime;
	//年份
	private int year;
	//发生次数
	private int times;
	//常年次数
	private double yearsCnt;
	//连阴雨单站累积强度
	private double stationStrength;
	//连阴雨区域累积强度
	private double areaStrength;
	//等权集成
	private double result;
	//常年等权集成
	private double yearsResult;
	//级别
	private String level;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public double getYearsCnt() {
		return yearsCnt;
	}
	public void setYearsCnt(double yearsCnt) {
		this.yearsCnt = yearsCnt;
	}
	public double getYearsResult() {
		return yearsResult;
	}
	public void setYearsResult(double yearsResult) {
		this.yearsResult = yearsResult;
	}
	//	public String getStartTime() {
//		return startTime;
//	}
//	public void setStartTime(String startTime) {
//		this.startTime = startTime;
//	}
//	public String getEndTime() {
//		return endTime;
//	}
//	public void setEndTime(String endTime) {
//		this.endTime = endTime;
//	}
	public double getStationStrength() {
		return stationStrength;
	}
	public void setStationStrength(double stationStrength) {
		this.stationStrength = stationStrength;
	}
	public double getAreaStrength() {
		return areaStrength;
	}
	public void setAreaStrength(double areaStrength) {
		this.areaStrength = areaStrength;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int compareTo(ContinueRainYearResult o) {
		return  year - o.getYear();
	}
	
}
