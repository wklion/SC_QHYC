package com.spd.sc.pojo;

/**
 * 西南雨季结果类
 * @author Administrator
 *
 */
public class RainySeasonResult {
	//站号
	private String station_Id_C;
	//站号
	private String station_Name;
	//经度
	private double lon;
	//纬度
	private double lat;
	//省
	private String province;
	//市
	private String city;
	//雨季开始日期
	private String startTime;
	//雨季结束日期
	private String endTime;
	//持续天数
	private int persistDays;
	//年份
	private int year;
	//雨量
	private double preSum;
	
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
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
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
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getPreSum() {
		return preSum;
	}
	public void setPreSum(double preSum) {
		this.preSum = preSum;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
}
