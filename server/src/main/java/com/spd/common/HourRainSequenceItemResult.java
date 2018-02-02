package com.spd.common;
/**
 * 逐时演变，结果类
 * @author Administrator
 *
 */
public class HourRainSequenceItemResult {
	//站名
	private String station_Id_C;
	//站号
	private String Station_Name;
	//地区
	private String area;
	//时间
	private String datetime;
	//1小时降水
	private double R1;
	//3小时降水
	private double R3;
	//6小时降水
	private double R6;
	//12小时降水
	private double R12;
	//24小时降水
	private double R24;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return Station_Name;
	}
	public void setStation_Name(String stationName) {
		Station_Name = stationName;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public double getR1() {
		return R1;
	}
	public void setR1(double r1) {
		R1 = r1;
	}
	public double getR3() {
		return R3;
	}
	public void setR3(double r3) {
		R3 = r3;
	}
	public double getR6() {
		return R6;
	}
	public void setR6(double r6) {
		R6 = r6;
	}
	public double getR12() {
		return R12;
	}
	public void setR12(double r12) {
		R12 = r12;
	}
	public double getR24() {
		return R24;
	}
	public void setR24(double r24) {
		R24 = r24;
	}
	
}
