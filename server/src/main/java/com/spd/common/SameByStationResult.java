package com.spd.common;
/**
 * 按照站号分组，进行历年同期对比的结果类
 * @author Administrator
 *
 */
public class SameByStationResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//多年均值
	private Double avgValue;
	//标准年份多年均值
	private Double standardValue;
	//距平
	private Double anomaly;
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
	public Double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(Double avgValue) {
		this.avgValue = avgValue;
	}
	public Double getStandardValue() {
		return standardValue;
	}
	public void setStandardValue(Double standardValue) {
		this.standardValue = standardValue;
	}
	public Double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(Double anomaly) {
		this.anomaly = anomaly;
	}
}
