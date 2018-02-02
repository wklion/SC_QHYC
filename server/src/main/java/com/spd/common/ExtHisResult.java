package com.spd.common;
/**
 * 极值统计结果类，包含历史对比
 * @author Administrator
 *
 */
public class ExtHisResult {
	
	//序号
	private int index;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//高值
	private double highValue;
	//高值日期
	private String highDate;
	//低值
	private double lowValue;
	//低值日期
	private String lowDate;
	//历史高值
	private double hisHighValue;
	//历史高值日期
	private String hisHighDate;
	//历史低值
	private double hisLowValue;
	//历史低值日期
	private String hisLowDate;
	//平均值
	private Double avgValue;
	
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
	public double getHighValue() {
		return highValue;
	}
	public void setHighValue(double highValue) {
		this.highValue = highValue;
	}
	public String getHighDate() {
		return highDate;
	}
	public void setHighDate(String highDate) {
		this.highDate = highDate;
	}
	public double getLowValue() {
		return lowValue;
	}
	public void setLowValue(double lowValue) {
		this.lowValue = lowValue;
	}
	public String getLowDate() {
		return lowDate;
	}
	public void setLowDate(String lowDate) {
		this.lowDate = lowDate;
	}
	public double getHisHighValue() {
		return hisHighValue;
	}
	public void setHisHighValue(double hisHighValue) {
		this.hisHighValue = hisHighValue;
	}
	public String getHisHighDate() {
		return hisHighDate;
	}
	public void setHisHighDate(String hisHighDate) {
		this.hisHighDate = hisHighDate;
	}
	public double getHisLowValue() {
		return hisLowValue;
	}
	public void setHisLowValue(double hisLowValue) {
		this.hisLowValue = hisLowValue;
	}
	public String getHisLowDate() {
		return hisLowDate;
	}
	public void setHisLowDate(String hisLowDate) {
		this.hisLowDate = hisLowDate;
	}
	
}
