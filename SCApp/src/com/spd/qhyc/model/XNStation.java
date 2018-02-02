package com.spd.qhyc.model;

public class XNStation {
	private int id;
	private String Station_Id_C;
	private String Station_Name;
	private String Admin_Code_CHN;
	private String Province;
	private String City;
	private String Cnty;
	private int Station_levl;
	private double Lon;
	private double Lat;
	private double Alti;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStation_Id_C() {
		return Station_Id_C;
	}
	public void setStation_Id_C(String station_Id_C) {
		Station_Id_C = station_Id_C;
	}
	public String getStation_Name() {
		return Station_Name;
	}
	public void setStation_Name(String station_Name) {
		Station_Name = station_Name;
	}
	public String getAdmin_Code_CHN() {
		return Admin_Code_CHN;
	}
	public void setAdmin_Code_CHN(String admin_Code_CHN) {
		Admin_Code_CHN = admin_Code_CHN;
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
	public int getStation_levl() {
		return Station_levl;
	}
	public void setStation_levl(int station_levl) {
		Station_levl = station_levl;
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
}
