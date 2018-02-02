package com.spd.pojo;

public class ExtTmp {

	private String Station_Name;
	
	private String Province;
	
	private String City;
	
	private String Cnty;
	
	private String Station_Id_C;
	
	private String Station_Id_d;
	
	private double Lat;
	
	private double Lon;
	
	private double Alti;
	//极端低温
	private double TEM_Min;
	//极端低温出现时间，多个时间用,分隔
	private String TEM_Min_OTime;
	//极端高温
	private double TEM_Max;
	//极端高温出现时间，多个时间用,分隔
	private String TEM_Max_OTime;
	
	private String area;
	
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

	public double getTEM_Min() {
		return TEM_Min;
	}

	public void setTEM_Min(double tEMMin) {
		TEM_Min = tEMMin;
	}

	public String getTEM_Min_OTime() {
		return TEM_Min_OTime;
	}

	public void setTEM_Min_OTime(String tEMMinOTime) {
		TEM_Min_OTime = tEMMinOTime;
	}

	public double getTEM_Max() {
		return TEM_Max;
	}

	public void setTEM_Max(double tEMMax) {
		TEM_Max = tEMMax;
	}

	public String getTEM_Max_OTime() {
		return TEM_Max_OTime;
	}

	public void setTEM_Max_OTime(String tEMMaxOTime) {
		TEM_Max_OTime = tEMMaxOTime;
	}

}
