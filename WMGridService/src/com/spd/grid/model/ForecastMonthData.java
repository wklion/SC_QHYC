package com.spd.grid.model;

public class ForecastMonthData {
	private String method;
	private String forecastDate;
	private String stationNum;
	private double val;
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getForecastDate() {
        return forecastDate;
    }
    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }
    public String getStationNum() {
        return stationNum;
    }
    public void setStationNum(String stationNum) {
        this.stationNum = stationNum;
    }
    public double getVal() {
        return val;
    }
    public void setVal(double val) {
        this.val = val;
    }
	
	
}
