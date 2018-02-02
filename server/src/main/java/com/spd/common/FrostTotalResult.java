package com.spd.common;

/**
 * 霜冻，序列
 * @author Administrator
 *
 */
public class FrostTotalResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//出现次数
	private int cnt;
	//最重程度
	private String maxLevel;
	//极端低温
	private double extLowTmp;
	//开始期Last
	private String startDatetimeLast;
	//结束期Last
	private String endDatetimeLast;
	//持续天数Last
	private int persistDaysLast;
	//程度Last
	private String levelLast;
	
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(String maxLevel) {
		this.maxLevel = maxLevel;
	}
	public double getExtLowTmp() {
		return extLowTmp;
	}
	public void setExtLowTmp(double extLowTmp) {
		this.extLowTmp = extLowTmp;
	}
	public String getStartDatetimeLast() {
		return startDatetimeLast;
	}
	public void setStartDatetimeLast(String startDatetimeLast) {
		this.startDatetimeLast = startDatetimeLast;
	}
	public String getEndDatetimeLast() {
		return endDatetimeLast;
	}
	public void setEndDatetimeLast(String endDatetimeLast) {
		this.endDatetimeLast = endDatetimeLast;
	}
	public int getPersistDaysLast() {
		return persistDaysLast;
	}
	public void setPersistDaysLast(int persistDaysLast) {
		this.persistDaysLast = persistDaysLast;
	}
	public String getLevelLast() {
		return levelLast;
	}
	public void setLevelLast(String levelLast) {
		this.levelLast = levelLast;
	}
	
}
