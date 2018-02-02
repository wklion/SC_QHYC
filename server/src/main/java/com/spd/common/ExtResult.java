package com.spd.common;
/**
 * 极值统计结果类
 * @author Administrator
 *
 */
public class ExtResult {
	
	//序号
	private int index;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//平均值
	private Double avgValue;
	//高值
	private Double highValue;
	//高值日期
	private String highDate;
	//低值
	private Double lowValue;
	//低值日期
	private String lowDate;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(Double avgValue) {
		this.avgValue = avgValue;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public Double getHighValue() {
		return highValue;
	}
	public void setHighValue(Double highValue) {
		this.highValue = highValue;
	}
	public String getHighDate() {
		return highDate;
	}
	public void setHighDate(String highDate) {
		this.highDate = highDate;
	}
	public Double getLowValue() {
		return lowValue;
	}
	public void setLowValue(Double lowValue) {
		this.lowValue = lowValue;
	}
	public String getLowDate() {
		return lowDate;
	}
	public void setLowDate(String lowDate) {
		this.lowDate = lowDate;
	}
}
