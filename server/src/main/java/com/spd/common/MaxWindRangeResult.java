package com.spd.common;

/**
 * 按时间段统计大风结果类。合计
 * @author Administrator
 *
 */
public class MaxWindRangeResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//大风出现次数
	private int cnt;
	//最严重程度
	private String maxLevel;
	//最大风速
	private double maxWindS;
	//最大风速对应的风向，如果有最大风速相同的时候，则最大风向是一个连接的字符串
	private String maxWindD;
	//最大风速发生的时间 ，如果有多个，则连接字符串
	private String maxWindTimes;
	//轻度风灾次数
	private int mildCnt;
	//中度风灾次数
	private int moderateCnt;
	//严重风灾次数
	private int severityCnt;
	
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
	public String getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(String maxLevel) {
		this.maxLevel = maxLevel;
	}
	public double getMaxWindS() {
		return maxWindS;
	}
	public void setMaxWindS(double maxWindS) {
		this.maxWindS = maxWindS;
	}
	public String getMaxWindD() {
		return maxWindD;
	}
	public void setMaxWindD(String maxWindD) {
		this.maxWindD = maxWindD;
	}
	public String getMaxWindTimes() {
		return maxWindTimes;
	}
	public void setMaxWindTimes(String maxWindTimes) {
		this.maxWindTimes = maxWindTimes;
	}
	public int getMildCnt() {
		return mildCnt;
	}
	public void setMildCnt(int mildCnt) {
		this.mildCnt = mildCnt;
	}
	public int getModerateCnt() {
		return moderateCnt;
	}
	public void setModerateCnt(int moderateCnt) {
		this.moderateCnt = moderateCnt;
	}
	public int getSeverityCnt() {
		return severityCnt;
	}
	public void setSeverityCnt(int severityCnt) {
		this.severityCnt = severityCnt;
	}
	
}
