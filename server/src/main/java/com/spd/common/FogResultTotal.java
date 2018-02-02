package com.spd.common;
/**
 * 雾结果。合计
 * @author Administrator
 *
 */
public class FogResultTotal {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//最小能见度
	private double vis_Min;
	//最小能见度日期
	private String vis_Min_Time;
	//雾次数
	private double cnt;
	//轻雾次数
	private double mistCnt;
	
	public double getMistCnt() {
		return mistCnt;
	}
	public void setMistCnt(double mistCnt) {
		this.mistCnt = mistCnt;
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
	public double getVis_Min() {
		return vis_Min;
	}
	public void setVis_Min(double visMin) {
		vis_Min = visMin;
	}
	public String getVis_Min_Time() {
		return vis_Min_Time;
	}
	public void setVis_Min_Time(String visMinTime) {
		vis_Min_Time = visMinTime;
	}
	public double getCnt() {
		return cnt;
	}
	public void setCnt(double cnt) {
		this.cnt = cnt;
	}
	
}
