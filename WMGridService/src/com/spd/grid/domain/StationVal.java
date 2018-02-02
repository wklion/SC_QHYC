package com.spd.grid.domain;
/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:
 */
public class StationVal {
	private String stationNum;
	private String stationName;
	private double longitude;
	private double latitude;
	private double value;
    public String getStationNum() {
        return stationNum;
    }
    public void setStationNum(String stationNum) {
        this.stationNum = stationNum;
    }
    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
	
}
