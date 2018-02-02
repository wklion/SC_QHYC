package com.spd.common;
/**
 * 干旱过程统计结果
 * @author Administrator
 *
 */
public class MCIProcessSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始期
	private String startTime;
	//结束期
	private String endTime;
	//持续天数
	private int persistDays;
	//CI总和
	private double sumCI;
	//CI极值
	private double extCI;
	//极值日期
	private String extDatetime;
	//极值强度
	private double extValue;
	//CI均值
	private double avgCI;
	//均值强度
	private String avgLevel;
	//干旱类型
	private String type;
	
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
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public double getSumCI() {
		return sumCI;
	}
	public void setSumCI(double sumCI) {
		this.sumCI = sumCI;
	}
	public double getExtCI() {
		return extCI;
	}
	public void setExtCI(double extCI) {
		this.extCI = extCI;
	}
	public String getExtDatetime() {
		return extDatetime;
	}
	public void setExtDatetime(String extDatetime) {
		this.extDatetime = extDatetime;
	}
	public double getExtValue() {
		return extValue;
	}
	public void setExtValue(double extValue) {
		this.extValue = extValue;
	}
	public double getAvgCI() {
		return avgCI;
	}
	public void setAvgCI(double avgCI) {
		this.avgCI = avgCI;
	}
	public String getAvgLevel() {
		return avgLevel;
	}
	public void setAvgLevel(String avgLevel) {
		this.avgLevel = avgLevel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
