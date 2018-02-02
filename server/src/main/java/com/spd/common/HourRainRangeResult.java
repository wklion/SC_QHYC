package com.spd.common;
/**
 * 小时降水，时段统计，结果
 * @author Administrator
 *
 */
public class HourRainRangeResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//极值
	private double extValue;
	//位次
	private int rank;
	//历史极值
	private double hisExtValue;
	//极值日期
	private String hisExtTimes;
	
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
	public double getExtValue() {
		return extValue;
	}
	public void setExtValue(double extValue) {
		this.extValue = extValue;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public double getHisExtValue() {
		return hisExtValue;
	}
	public void setHisExtValue(double hisExtValue) {
		this.hisExtValue = hisExtValue;
	}
	public String getHisExtTimes() {
		return hisExtTimes;
	}
	public void setHisExtTimes(String hisExtTimes) {
		this.hisExtTimes = hisExtTimes;
	}
	
}
