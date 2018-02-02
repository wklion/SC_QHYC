package com.spd.pojo;
/**
 * 高温日数
 * @author Administrator
 *
 */
public class TmpMaxCnt {

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
	// >= 35.0的天数
	private double gte35;
	// >= 37
	private double gte37;
	// >=35 && < 37
	private double gte35lt37;
	// >= 37 && < 40
	private double gte37lt40;
	// >= 40
	private double gte40;
	
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

	public double getGte35() {
		return gte35;
	}

	public void setGte35(double gte35) {
		this.gte35 = gte35;
	}

	public double getGte35lt37() {
		return gte35lt37;
	}

	public void setGte35lt37(double gte35lt37) {
		this.gte35lt37 = gte35lt37;
	}

	public double getGte37lt40() {
		return gte37lt40;
	}

	public void setGte37lt40(double gte37lt40) {
		this.gte37lt40 = gte37lt40;
	}

	public double getGte40() {
		return gte40;
	}

	public void setGte40(double gte40) {
		this.gte40 = gte40;
	}

	public double getGte37() {
		return gte37;
	}

	public void setGte37(double gte37) {
		this.gte37 = gte37;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
