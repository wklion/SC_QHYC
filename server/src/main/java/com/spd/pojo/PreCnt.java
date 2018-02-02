package com.spd.pojo;
/**
 * 降水日数
 * @author Administrator
 *
 */
public class PreCnt {

	private String Station_Name;
	
	private String Province;
	
	private String City;
	
	private String Cnty;
	
	private String Station_Id_C;
	
	private String Station_Id_d;
	
	private double Lat;
	
	private double Lon;
	
	private double Alti;
	
	private String area;
	//降水日数
	private double cnt;
	//降水日数，对比值
	private double contrastCnt;
	// >= 25 && < 50
	private double get25lt50cnt;
	// >= 50 && < 100
	private double get50lt100cnt;
	// >= 100
	private double get100;
	//距平
	private double anomaly;
	
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

	public double getCnt() {
		return cnt;
	}

	public void setCnt(double cnt) {
		this.cnt = cnt;
	}

	public double getGet25lt50cnt() {
		return get25lt50cnt;
	}

	public void setGet25lt50cnt(double get25lt50cnt) {
		this.get25lt50cnt = get25lt50cnt;
	}

	public double getGet50lt100cnt() {
		return get50lt100cnt;
	}

	public void setGet50lt100cnt(double get50lt100cnt) {
		this.get50lt100cnt = get50lt100cnt;
	}

	public double getGet100() {
		return get100;
	}

	public void setGet100(double get100) {
		this.get100 = get100;
	}

	public double getContrastCnt() {
		return contrastCnt;
	}

	public void setContrastCnt(double contrastCnt) {
		this.contrastCnt = contrastCnt;
	}

	public double getAnomaly() {
		return anomaly;
	}

	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}

}
