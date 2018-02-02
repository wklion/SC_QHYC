package com.spd.pojo;

public class PrsAvg {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private String area;
	
	//气压
	private double PRS_Avg;
	//气压，对比值
	private double contrastPRS_Avg;
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

	public double getPRS_Avg() {
		return PRS_Avg;
	}

	public void setPRS_Avg(double pRSAvg) {
		PRS_Avg = pRSAvg;
	}

	public double getContrastPRS_Avg() {
		return contrastPRS_Avg;
	}

	public void setContrastPRS_Avg(double contrastPRSAvg) {
		contrastPRS_Avg = contrastPRSAvg;
	}

	
}
