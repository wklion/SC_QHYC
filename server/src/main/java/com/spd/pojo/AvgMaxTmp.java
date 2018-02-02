package com.spd.pojo;

public class AvgMaxTmp {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private String area;
	
	//气温
	private double TEM_Max;
	//时段值，对比值
	private double contrastTEM_Max;
	// 距平值
	private double anomaly;

	private double lon;
	
	private double lat;
	
	public String getStation_Id_C() {
		return Station_Id_C;
	}

	public void setStation_Id_C(String StationIdC) {
		Station_Id_C = StationIdC;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String StationName) {
		Station_Name = StationName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public double getAnomaly() {
		return anomaly;
	}

	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
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

	public double getTEM_Max() {
		return TEM_Max;
	}

	public void setTEM_Max(double tEMMax) {
		TEM_Max = tEMMax;
	}

	public double getContrastTEM_Max() {
		return contrastTEM_Max;
	}

	public void setContrastTEM_Max(double contrastTEMMax) {
		contrastTEM_Max = contrastTEMMax;
	}
	
}
