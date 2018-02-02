package com.spd.common.evaluate;
/**
 * 单站低温评估结果类
 * @author Administrator
 *
 */
public class LowTmpStationResult {
	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//平均气温
	private double avgTmp;
	//气温距平
	private double anomaly;
	//程度
	private String level;
	//持续天数
	private int persistDays;
	//持续候数
	private int persistHous;
	
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
	public double getAvgTmp() {
		return avgTmp;
	}
	public void setAvgTmp(double avgTmp) {
		this.avgTmp = avgTmp;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getPersistHous() {
		return persistHous;
	}
	public void setPersistHous(int persistHous) {
		this.persistHous = persistHous;
	}
	
}
