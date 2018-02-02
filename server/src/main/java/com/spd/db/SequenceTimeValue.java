package com.spd.db;

import java.util.List;

/**
 * 对结果表进行处理，得到 年份，站号，TimeValue序列。为了方便各种统计分析
 * @author Administrator
 *
 */
public class SequenceTimeValue {

	private int year;
	
	private String station_Id_C;
	
	private String Station_Name;
	
	private double lat;
	
	private double lon;
	
	private List<TimeValue> timeValues;

	public int getYear() {
		return year;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String stationName) {
		Station_Name = stationName;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public List<TimeValue> getTimeValues() {
		return timeValues;
	}

	public void setTimeValues(List<TimeValue> timeValues) {
		this.timeValues = timeValues;
	}
	
}
