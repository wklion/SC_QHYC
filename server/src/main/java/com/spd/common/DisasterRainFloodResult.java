package com.spd.common;
/**
 * 灾害分析中洪涝结果类
 * @author Administrator
 *
 */
public class DisasterRainFloodResult {

	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始日期
	private String startDatetime;
	//结束日期
	private String endDatetime;
	//降水总量
	private double sum;
	//持续天数
	private int persist;
	//强度
	private String level;
	//1天极值
	private double ext1DayValue;
	//2天极值
	private double ext2DayValue;
	//3天极值
	private double ext3DayValue;
	
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
	public String getStartDatetime() {
		return startDatetime;
	}
	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}
	public String getEndDatetime() {
		return endDatetime;
	}
	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public int getPersist() {
		return persist;
	}
	public void setPersist(int persist) {
		this.persist = persist;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public double getExt1DayValue() {
		return ext1DayValue;
	}
	public void setExt1DayValue(double ext1DayValue) {
		this.ext1DayValue = ext1DayValue;
	}
	public double getExt2DayValue() {
		return ext2DayValue;
	}
	public void setExt2DayValue(double ext2DayValue) {
		this.ext2DayValue = ext2DayValue;
	}
	public double getExt3DayValue() {
		return ext3DayValue;
	}
	public void setExt3DayValue(double ext3DayValue) {
		this.ext3DayValue = ext3DayValue;
	}
	
}
