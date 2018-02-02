package com.spd.rain.day;

public class Tmp {

	private double maxTemp;
	
	private double minTemp;
	
	private String maxTempTime;
	
	private String minTempTime;
	
	private double avgTemp;
	
	private String insertTime;
	
	private String updateTime;
	
	private String fileTime;

	private Station station;
	
	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}

	public String getMaxTempTime() {
		return maxTempTime;
	}

	public void setMaxTempTime(String maxTempTime) {
		this.maxTempTime = maxTempTime;
	}

	public String getMinTempTime() {
		return minTempTime;
	}

	public void setMinTempTime(String minTempTime) {
		this.minTempTime = minTempTime;
	}

	public double getAvgTemp() {
		return avgTemp;
	}

	public void setAvgTemp(double avgTemp) {
		this.avgTemp = avgTemp;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getFileTime() {
		return fileTime;
	}

	public void setFileTime(String fileTime) {
		this.fileTime = fileTime;
	}
	
}
