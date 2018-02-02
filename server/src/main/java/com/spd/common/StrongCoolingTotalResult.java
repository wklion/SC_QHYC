package com.spd.common;
/**
 * 强降温合计结果类
 * @author Administrator
 *
 */
public class StrongCoolingTotalResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//出现次数
	private int cnt;
	//最重程度
	private String mostLevel;
	//最后一次开始日期
	private String startDatetimeLast;
	//最后一次结束日期
	private String endDatetimeLast;
	//持续天数
	private int persistDaysLast;
	//程度
	private String levelLast;
	
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
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getMostLevel() {
		return mostLevel;
	}
	public void setMostLevel(String mostLevel) {
		this.mostLevel = mostLevel;
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
