package com.spd.common;

public class Station {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private double Lon;
	
	private double Lat;
	
	private double Alti;
	
	private int ZoomLevel;
	
	private String Province;
	
	private String Country;
	
	private String AreaCode;

	public String getStation_Id_C() {
		return Station_Id_C;
	}

	public void setStation_Id_C(String stationIdC) {
		Station_Id_C = stationIdC;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String stationName) {
		Station_Name = stationName;
	}

	public double getLon() {
		return Lon;
	}

	public void setLon(double lon) {
		Lon = lon;
	}

	public double getLat() {
		return Lat;
	}

	public void setLat(double lat) {
		Lat = lat;
	}

	public double getAlti() {
		return Alti;
	}

	public void setAlti(double alti) {
		Alti = alti;
	}

	public int getZoomLevel() {
		return ZoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		ZoomLevel = zoomLevel;
	}

	public String getProvince() {
		return Province;
	}

	public void setProvince(String province) {
		Province = province;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getAreaCode() {
		return AreaCode;
	}

	public void setAreaCode(String areaCode) {
		AreaCode = areaCode;
	}
}
