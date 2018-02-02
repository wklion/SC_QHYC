package com.spd.qhyc.app;
/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:
 */
public class StationVal {
	private String stationNum;
	private String stationName;
	private Double longitude;
	private Double latitude;
	private Double value;
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getStationNum() {
		return stationNum;
	}
	public StationVal() {
		super();
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
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
