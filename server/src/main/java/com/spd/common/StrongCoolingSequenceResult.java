package com.spd.common;
/**
 * 强降温逐次结果类
 * @author Administrator
 *
 */
public class StrongCoolingSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始日期
	private String startDatetime;
	//结束日期
	private String endDatetime;
	//持续天数
	private int persistDays;
	//过程降温
	private double totalLowerTmp;
	//72小时内降幅
	private double hours72LowerTmp;
	//程度
	private String level;
	//临时存储1
	private double temp1;
	//临时存储2
	private double temp2;
	
	public double getTemp1() {
		return temp1;
	}
	public void setTemp1(double temp1) {
		this.temp1 = temp1;
	}
	public double getTemp2() {
		return temp2;
	}
	public void setTemp2(double temp2) {
		this.temp2 = temp2;
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
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public double getTotalLowerTmp() {
		return totalLowerTmp;
	}
	public void setTotalLowerTmp(double totalLowerTmp) {
		this.totalLowerTmp = totalLowerTmp;
	}
	public double getHours72LowerTmp() {
		return hours72LowerTmp;
	}
	public void setHours72LowerTmp(double hours72LowerTmp) {
		this.hours72LowerTmp = hours72LowerTmp;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
