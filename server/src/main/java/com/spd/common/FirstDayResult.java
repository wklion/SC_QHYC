package com.spd.common;
/**
 * 初日统计结果类
 * @author Administrator
 *
 */
public class FirstDayResult {
	//序号
	private int index;
	//年份
	private int year;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//比较年份的初日结果值，降水或者气温
	private double firstValue;
	//初日日期
	private String firstDate;
	//比较年份的终日结果值，降水或者气温
	private double lastValue;
	//常年初日日期
	private String normalFirstDate;
	//初日距平
	private int firstAnomaly; 
	//终日日期
	private String lastDate;
	//常年终日日期
	private String normalLastDate;
	//终日距平
	private int lastAnomaly;
	//极端最早初日
	private String extEarlyFirstDay;
	//极端最晚初日
	private String extLateFirstDay;
	//极端最早结束
	private String extEarlyLastDay;
	//极端最晚结束
	private String extLateLastDay;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public double getFirstValue() {
		return firstValue;
	}
	public void setFirstValue(double firstValue) {
		this.firstValue = firstValue;
	}
	public String getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	public double getLastValue() {
		return lastValue;
	}
	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
	}
	public String getNormalFirstDate() {
		return normalFirstDate;
	}
	public void setNormalFirstDate(String normalFirstDate) {
		this.normalFirstDate = normalFirstDate;
	}
	public int getFirstAnomaly() {
		return firstAnomaly;
	}
	public void setFirstAnomaly(int firstAnomaly) {
		this.firstAnomaly = firstAnomaly;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public String getNormalLastDate() {
		return normalLastDate;
	}
	public void setNormalLastDate(String normalLastDate) {
		this.normalLastDate = normalLastDate;
	}
	public int getLastAnomaly() {
		return lastAnomaly;
	}
	public void setLastAnomaly(int lastAnomaly) {
		this.lastAnomaly = lastAnomaly;
	}
	public String getExtEarlyFirstDay() {
		return extEarlyFirstDay;
	}
	public void setExtEarlyFirstDay(String extEarlyFirstDay) {
		this.extEarlyFirstDay = extEarlyFirstDay;
	}
	public String getExtLateFirstDay() {
		return extLateFirstDay;
	}
	public void setExtLateFirstDay(String extLateFirstDay) {
		this.extLateFirstDay = extLateFirstDay;
	}
	public String getExtEarlyLastDay() {
		return extEarlyLastDay;
	}
	public void setExtEarlyLastDay(String extEarlyLastDay) {
		this.extEarlyLastDay = extEarlyLastDay;
	}
	public String getExtLateLastDay() {
		return extLateLastDay;
	}
	public void setExtLateLastDay(String extLateLastDay) {
		this.extLateLastDay = extLateLastDay;
	} 
	
}
