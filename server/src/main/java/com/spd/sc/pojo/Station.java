package com.spd.sc.pojo;

/**
 * 站点
 * @author Administrator
 *
 */
public class Station {
	
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//经度
	private Double lon;
	//纬度
	private Double lat;
	//区域编码
	private String areaCode;
	
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
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
}
