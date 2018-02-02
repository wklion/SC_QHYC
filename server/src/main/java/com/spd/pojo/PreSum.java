package com.spd.pojo;

public class PreSum {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private String area;
	
	//降水
	private double PRE_Time;
	//时段值，对比值
	private double contrastPRE_Time;
	// 距平值
	private double anomaly;

	private double anomalyRate;

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

	public double getPRE_Time() {
		return PRE_Time;
	}

	public void setPRE_Time(double pRETime) {
		PRE_Time = pRETime;
	}

	public double getContrastPRE_Time() {
		return contrastPRE_Time;
	}

	public void setContrastPRE_Time(double contrastPRETime) {
		contrastPRE_Time = contrastPRETime;
	}

	public double getAnomalyRate() {
		return anomalyRate;
	}

	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}

}
