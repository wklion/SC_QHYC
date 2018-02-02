package com.spd.common;
/**
 * 累积降水结果
 * @author Administrator
 *
 */
public class HourRainAccumulateResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//降水总量
	private Double sumRain;
	//降水时数
	private Integer sumHours;
	
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
	public double getSumRain() {
		return sumRain;
	}
	public void setSumRain(Double sumRain) {
		this.sumRain = sumRain;
	}
	public int getSumHours() {
		return sumHours;
	}
	public void setSumHours(Integer sumHours) {
		this.sumHours = sumHours;
	}
	
}
