package com.spd.pojo;

public class SSH {

	private String Station_Name;
	
	private String Province;

	private String area;
	
	private String City;
	
	private String Cnty;
	
	private String Station_Id_C;
	
	private String Station_Id_d;
	
	private double Lat;
	
	private double Lon;
	
	private double Alti;
	//日照对数
	private double SSH;
	//对比值
	private double contrastSSH;
	// 距平值
	private double anomaly;
	//距平率
	private double anomalyRate;
	//日照百分率
	private double sshRate;
	
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String stationName) {
		Station_Name = stationName;
	}

	public String getProvince() {
		return Province;
	}

	public void setProvince(String province) {
		Province = province;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getCnty() {
		return Cnty;
	}

	public void setCnty(String cnty) {
		Cnty = cnty;
	}

	public String getStation_Id_C() {
		return Station_Id_C;
	}

	public void setStation_Id_C(String stationIdC) {
		Station_Id_C = stationIdC;
	}

	public String getStation_Id_d() {
		return Station_Id_d;
	}

	public void setStation_Id_d(String stationIdD) {
		Station_Id_d = stationIdD;
	}

	public double getLat() {
		return Lat;
	}

	public void setLat(double lat) {
		Lat = lat;
	}

	public double getLon() {
		return Lon;
	}

	public void setLon(double lon) {
		Lon = lon;
	}

	public double getAlti() {
		return Alti;
	}

	public void setAlti(double alti) {
		Alti = alti;
	}

	public double getSSH() {
		return SSH;
	}

	public void setSSH(double sSH) {
		SSH = sSH;
	}

	public double getContrastSSH() {
		return contrastSSH;
	}

	public void setContrastSSH(double contrastSSH) {
		this.contrastSSH = contrastSSH;
	}

	public double getAnomaly() {
		return anomaly;
	}

	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}

	public double getAnomalyRate() {
		return anomalyRate;
	}

	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}

	public double getSshRate() {
		return sshRate;
	}

	public void setSshRate(double sshRate) {
		this.sshRate = sshRate;
	}

}
