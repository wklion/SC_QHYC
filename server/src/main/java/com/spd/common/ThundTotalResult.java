package com.spd.common;
/**
 * 雷暴合计结果类。
 * @author Administrator
 *
 */
public class ThundTotalResult {

	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//发生次数
	private int cnt;
	
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
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	
	
}
