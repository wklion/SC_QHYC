package com.spd.common;
/**
 * 小时的云量查询结果类
 * @author Administrator
 *
 */
public class CloCovResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//日期
	private String datetime;
	//02时总云量
	private double cloCov1;
	//02时低云量
	private double cloCovLow1;
	//08时总云量
	private double cloCov2;
	//08时低云量
	private double cloCovLow2;
	//14时总云量
	private double cloCov3;
	//14时低云量
	private double cloCovLow3;
	//20时总云量
	private double cloCov4;
	//20时低云量
	private double cloCovLow4;
	//平均总云量
	private double avgCloCov;
	//平均低云量
	private double avgCloCovLow;
	
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public double getAvgCloCov() {
		return avgCloCov;
	}
	public void setAvgCloCov(double avgCloCov) {
		this.avgCloCov = avgCloCov;
	}
	public double getAvgCloCovLow() {
		return avgCloCovLow;
	}
	public void setAvgCloCovLow(double avgCloCovLow) {
		this.avgCloCovLow = avgCloCovLow;
	}
	public double getCloCov1() {
		return cloCov1;
	}
	public void setCloCov1(double cloCov1) {
		this.cloCov1 = cloCov1;
	}
	public double getCloCovLow1() {
		return cloCovLow1;
	}
	public void setCloCovLow1(double cloCovLow1) {
		this.cloCovLow1 = cloCovLow1;
	}
	public double getCloCov2() {
		return cloCov2;
	}
	public void setCloCov2(double cloCov2) {
		this.cloCov2 = cloCov2;
	}
	public double getCloCovLow2() {
		return cloCovLow2;
	}
	public void setCloCovLow2(double cloCovLow2) {
		this.cloCovLow2 = cloCovLow2;
	}
	public double getCloCov3() {
		return cloCov3;
	}
	public void setCloCov3(double cloCov3) {
		this.cloCov3 = cloCov3;
	}
	public double getCloCovLow3() {
		return cloCovLow3;
	}
	public void setCloCovLow3(double cloCovLow3) {
		this.cloCovLow3 = cloCovLow3;
	}
	public double getCloCov4() {
		return cloCov4;
	}
	public void setCloCov4(double cloCov4) {
		this.cloCov4 = cloCov4;
	}
	public double getCloCovLow4() {
		return cloCovLow4;
	}
	public void setCloCovLow4(double cloCovLow4) {
		this.cloCovLow4 = cloCovLow4;
	}
	
}
