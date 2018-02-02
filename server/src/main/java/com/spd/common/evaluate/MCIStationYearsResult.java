package com.spd.common.evaluate;
/**
 * MCI单站年度结果类
 * @author Administrator
 *
 */
public class MCIStationYearsResult {
	//年份
	private int year;
	//累积强度
	private Double sumStrength;
	//位次
	private int rank;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Double getSumStrength() {
		return sumStrength;
	}
	public void setSumStrength(Double sumStrength) {
		this.sumStrength = sumStrength;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
}
