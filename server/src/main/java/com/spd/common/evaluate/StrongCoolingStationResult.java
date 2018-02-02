package com.spd.common.evaluate;

public class StrongCoolingStationResult {
	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续天数
	private int persistDays;
	//过程降温
	private Double coolingTmps;
	//72小时降幅
	private Double coolingTmps72Hours;
	//程度
	private String level;
	//单站强降温综合指数
	private double CI;
	
	public double getCI() {
		return CI;
	}
	public void setCI(double cI) {
		CI = cI;
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
	public Double getCoolingTmps() {
		return coolingTmps;
	}
	public void setCoolingTmps(Double coolingTmps) {
		this.coolingTmps = coolingTmps;
	}
	public Double getCoolingTmps72Hours() {
		return coolingTmps72Hours;
	}
	public void setCoolingTmps72Hours(Double coolingTmps72Hours) {
		this.coolingTmps72Hours = coolingTmps72Hours;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
