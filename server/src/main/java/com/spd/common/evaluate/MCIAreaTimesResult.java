package com.spd.common.evaluate;
/**
 * 
 * @author Administrator
 *
 */
public class MCIAreaTimesResult {
	//年份
	private int year;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续天数
	private Integer days;
	//影响站数
	private int stationCnts;
	//累积强度
	private Double sumStrength;
	//位次
	private int rank;
	
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getStationCnts() {
		return stationCnts;
	}
	public void setStationCnts(int stationCnts) {
		this.stationCnts = stationCnts;
	}
	public Double getSumStrength() {
		return sumStrength;
	}
	public void setSumStrength(Double sumStrength) {
		this.sumStrength = sumStrength;
	}
	
}
