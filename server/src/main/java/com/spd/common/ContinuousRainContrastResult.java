package com.spd.common;

/**
 * 连阴雨逐次对比结果定义类
 * @author Administrator
 *
 */
public class ContinuousRainContrastResult {
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
	//最重程度开始期
	private String startTime;
	//最重程度结束期
	private String endTime;
	//最重程度持续天数
	private int persist;
	
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
	public int getPersist() {
		return persist;
	}
	public void setPersist(int persist) {
		this.persist = persist;
	}
	
	
}
