package com.spd.db;

import java.util.Comparator;
import java.util.Date;

/**
 * 封装要素对应的表
 * @author Administrator
 *
 */
public class DBData implements Comparator<DBData> {

	private int id;
	
	private String Station_Name;
	
	private String Province;
	
	private String City;
	
	private String Cnty;
	
	private String Town;
	
	private String Station_Id_C;
	
	private String Station_Id_d;
	
	private double Lat;
	
	private double Lon;
	
	private double Alti;
	
	private double PRS_Sensor_Alti;
	
	private String Station_levl;
	
	private String Admin_Code_CHN;
	
	private Date date;

	private double value;
	
	private int mon;
	
	private int day;
	//如果涉及到跨年的话，则把该年计算到相应的年份
	private int year;
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMon() {
		return mon;
	}

	public void setMon(int mon) {
		this.mon = mon;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getTown() {
		return Town;
	}

	public void setTown(String town) {
		Town = town;
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

	public double getPRS_Sensor_Alti() {
		return PRS_Sensor_Alti;
	}

	public void setPRS_Sensor_Alti(double pRSSensorAlti) {
		PRS_Sensor_Alti = pRSSensorAlti;
	}

	public String getStation_levl() {
		return Station_levl;
	}

	public void setStation_levl(String stationLevl) {
		Station_levl = stationLevl;
	}

	public String getAdmin_Code_CHN() {
		return Admin_Code_CHN;
	}

	public void setAdmin_Code_CHN(String adminCodeCHN) {
		Admin_Code_CHN = adminCodeCHN;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int compare(DBData dbData1, DBData dbData2) {
		long time1 = dbData1.getDate().getTime();
		long time2 = dbData2.getDate().getTime();
		if(time1 < time2) return -1;
		if(time1 == time2) return 0;
		if(time1 > time2) return 1;
		return 0;
	}
	
}
