package com.spd.pojo;

/**
 * 计算类，按年，站 求和
 * @author Administrator
 *
 */
public class SumByYear {

	private String Station_Id_C;
	
	private int year;
	
	private double sum;

	public String getStation_Id_C() {
		return Station_Id_C;
	}

	public void setStation_Id_C(String stationIdC) {
		Station_Id_C = stationIdC;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}
	
}
