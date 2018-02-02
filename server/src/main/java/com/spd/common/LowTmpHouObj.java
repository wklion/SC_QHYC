package com.spd.common;

import java.util.List;

public class LowTmpHouObj {

	private int year;
	
	private String station_Id_C;
	
	private String stationName;
	
	private List<Integer> hou; // 候的序列
	
	private List<Double> houSum; // 候的值

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getStation_Id_C() {
		return station_Id_C;
	}

	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<Integer> getHou() {
		return hou;
	}

	public void setHou(List<Integer> hou) {
		this.hou = hou;
	}

	public List<Double> getHouSum() {
		return houSum;
	}

	public void setHouSum(List<Double> houSum) {
		this.houSum = houSum;
	}

}
