package com.spd.common;

import java.util.Date;

public class HourRain {

	private String station_Id_C;
	
	private String dateTime;
	
	private Date date;
	
	private double R1;
	
	private double R3;
	
	private double R6;
	
	private double R12;
	
	private double R24;

	public String getStation_Id_C() {
		return station_Id_C;
	}

	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getR1() {
		return R1;
	}

	public void setR1(double r1) {
		R1 = r1;
	}

	public double getR3() {
		return R3;
	}

	public void setR3(double r3) {
		R3 = r3;
	}

	public double getR6() {
		return R6;
	}

	public void setR6(double r6) {
		R6 = r6;
	}

	public double getR12() {
		return R12;
	}

	public void setR12(double r12) {
		R12 = r12;
	}

	public double getR24() {
		return R24;
	}

	public void setR24(double r24) {
		R24 = r24;
	}
	
	
}
