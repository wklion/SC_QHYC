package com.spd.common.evaluate;

/**
 * 时间段范围评估高温结果类（单站）
 * @author Administrator
 *
 */
public class HighTmpRangeStationResult {
	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//si
	private double si;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//高温等级
	private String level;
	
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
	public double getSi() {
		return si;
	}
	public void setSi(double si) {
		this.si = si;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
