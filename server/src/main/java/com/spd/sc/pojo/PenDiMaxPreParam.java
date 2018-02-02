package com.spd.sc.pojo;

/**
 * 盆地大雨开始期的参数类
 * @author Administrator
 *
 */
public class PenDiMaxPreParam {

	//年份
	private int year;
	//站
	private String station_Id_Cs;
	//临界雨量
	private double minPre;
	
	public double getMinPre() {
		return minPre;
	}
	public void setMinPre(double minPre) {
		this.minPre = minPre;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String stationIdCs) {
		station_Id_Cs = stationIdCs;
	}
}
