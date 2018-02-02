package com.spd.qhyc.model;
/**
 * @作者:wangkun
 * @日期:2017年12月5日
 * @公司:spd
 * @说明:
*/
public class CimissMonthData implements Cloneable{
	private int id;
	private String province;
	private String stationname;
	private String stationnum;
	private int year;
	private double m1;
	private double m2;
	private double m3;
	private double m4;
	private double m5;
	private double m6;
	private double m7;
	private double m8;
	private double m9;
	private double m10;
	private double m11;
	private double m12;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getStationname() {
		return stationname;
	}
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}
	public String getStationnum() {
		return stationnum;
	}
	public void setStationnum(String stationnum) {
		this.stationnum = stationnum;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getM1() {
		return m1;
	}
	public void setM1(double m1) {
		this.m1 = m1;
	}
	public double getM2() {
		return m2;
	}
	public void setM2(double m2) {
		this.m2 = m2;
	}
	public double getM3() {
		return m3;
	}
	public void setM3(double m3) {
		this.m3 = m3;
	}
	public double getM4() {
		return m4;
	}
	public void setM4(double m4) {
		this.m4 = m4;
	}
	public double getM5() {
		return m5;
	}
	public void setM5(double m5) {
		this.m5 = m5;
	}
	public double getM6() {
		return m6;
	}
	public void setM6(double m6) {
		this.m6 = m6;
	}
	public double getM7() {
		return m7;
	}
	public void setM7(double m7) {
		this.m7 = m7;
	}
	public double getM8() {
		return m8;
	}
	public void setM8(double m8) {
		this.m8 = m8;
	}
	public double getM9() {
		return m9;
	}
	public void setM9(double m9) {
		this.m9 = m9;
	}
	public double getM10() {
		return m10;
	}
	public void setM10(double m10) {
		this.m10 = m10;
	}
	public double getM11() {
		return m11;
	}
	public void setM11(double m11) {
		this.m11 = m11;
	}
	public double getM12() {
		return m12;
	}
	public void setM12(double m12) {
		this.m12 = m12;
	}
	@Override
	public CimissMonthData clone() {
		CimissMonthData cmd = null;
		try {
			cmd = (CimissMonthData)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return cmd;
	}
}
