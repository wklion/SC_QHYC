package com.spd.common;
/**
 * 有效积温
 * @author Administrator
 *
 */
public class ValidAccumulatedTemp {
	// 站号
	private String station_Id_C;
	// 站名
	private String station_Name;
	// 积温
	private double accumulatedTemp;
	// 多年均值
	private double yearsAvg;
	// 距平
	private double anomaly;
	
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
	public double getAccumulatedTemp() {
		return accumulatedTemp;
	}
	public void setAccumulatedTemp(double accumulatedTemp) {
		this.accumulatedTemp = accumulatedTemp;
	}
	public double getYearsAvg() {
		return yearsAvg;
	}
	public void setYearsAvg(double yearsAvg) {
		this.yearsAvg = yearsAvg;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}
}
