package com.spd.common;
/**
 * MCI过程次数统计结果
 * @author Administrator
 *
 */
public class MCIProcessTotalResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//总次数
	private int sumTimes;
	//总天数
	private int sumDays;
	//干旱事件
	private String mciEvent;
	//天数Max
	private int maxDays;
	//CI极值
	private double extCI;
	//极值日期
	private String extTime;
	//开始期Last
	private String lastStartTime;
	//结束期Last
	private String lastEndTime;
	//天数Last
	private String lastDays;
	//CI极值Last
	private double lastExtCI;
	//强度Last
	private double lastStrength;
	
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
	public int getSumTimes() {
		return sumTimes;
	}
	public void setSumTimes(int sumTimes) {
		this.sumTimes = sumTimes;
	}
	public int getSumDays() {
		return sumDays;
	}
	public void setSumDays(int sumDays) {
		this.sumDays = sumDays;
	}
	public String getMciEvent() {
		return mciEvent;
	}
	public void setMciEvent(String mciEvent) {
		this.mciEvent = mciEvent;
	}
	public int getMaxDays() {
		return maxDays;
	}
	public void setMaxDays(int maxDays) {
		this.maxDays = maxDays;
	}
	public double getExtCI() {
		return extCI;
	}
	public void setExtCI(double extCI) {
		this.extCI = extCI;
	}
	public String getExtTime() {
		return extTime;
	}
	public void setExtTime(String extTime) {
		this.extTime = extTime;
	}
	public String getLastStartTime() {
		return lastStartTime;
	}
	public void setLastStartTime(String lastStartTime) {
		this.lastStartTime = lastStartTime;
	}
	public String getLastEndTime() {
		return lastEndTime;
	}
	public void setLastEndTime(String lastEndTime) {
		this.lastEndTime = lastEndTime;
	}
	public String getLastDays() {
		return lastDays;
	}
	public void setLastDays(String lastDays) {
		this.lastDays = lastDays;
	}
	public double getLastExtCI() {
		return lastExtCI;
	}
	public void setLastExtCI(double lastExtCI) {
		this.lastExtCI = lastExtCI;
	}
	public double getLastStrength() {
		return lastStrength;
	}
	public void setLastStrength(double lastStrength) {
		this.lastStrength = lastStrength;
	}
	
}
