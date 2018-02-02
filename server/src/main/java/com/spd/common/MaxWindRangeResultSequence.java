package com.spd.common;

/**
 * 按时间段统计大风结果类，逐次
 * @author Administrator
 *
 */
public class MaxWindRangeResultSequence {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//出现时间
	private String maxWindTime;
	//极大风速
	private double wIN_S_Inst_Max;
	//极大风速对应的风向
	private double wIN_D_INST_Max;
	//风力级别
	private int level;
	
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
	public String getMaxWindTime() {
		return maxWindTime;
	}
	public void setMaxWindTime(String maxWindTime) {
		this.maxWindTime = maxWindTime;
	}
	public double getwIN_S_Inst_Max() {
		return wIN_S_Inst_Max;
	}
	public void setwIN_S_Inst_Max(double wINSInstMax) {
		wIN_S_Inst_Max = wINSInstMax;
	}
	public double getwIN_D_INST_Max() {
		return wIN_D_INST_Max;
	}
	public void setwIN_D_INST_Max(double wINDINSTMax) {
		wIN_D_INST_Max = wINDINSTMax;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
}
