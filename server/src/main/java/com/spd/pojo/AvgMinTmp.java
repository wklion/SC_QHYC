package com.spd.pojo;

public class AvgMinTmp {

	private String Station_Id_C;
	
	private String Station_Name;
	
	private String area;
	
	//气温
	private double TEM_Min;
	//时段值，对比值
	private double contrastTEM_Min;
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

	public double getTEM_Min() {
		return TEM_Min;
	}

	public void setTEM_Min(double tEMMin) {
		TEM_Min = tEMMin;
	}

	public double getContrastTEM_Min() {
		return contrastTEM_Min;
	}

	public void setContrastTEM_Min(double contrastTEMMin) {
		contrastTEM_Min = contrastTEMMin;
	}

}
