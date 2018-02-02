package com.spd.pojo;

public class Win_s_2min_avg {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private String area;
	
	//风速
	private double WIN_S_2mi_Avg;
	//风速，对比值
	private double contrastWIN_S_2mi_Avg;
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

	public double getWIN_S_2mi_Avg() {
		return WIN_S_2mi_Avg;
	}

	public void setWIN_S_2mi_Avg(double wINS_2miAvg) {
		WIN_S_2mi_Avg = wINS_2miAvg;
	}

	public double getContrastWIN_S_2mi_Avg() {
		return contrastWIN_S_2mi_Avg;
	}

	public void setContrastWIN_S_2mi_Avg(double contrastWINS_2miAvg) {
		contrastWIN_S_2mi_Avg = contrastWINS_2miAvg;
	}
	
}
