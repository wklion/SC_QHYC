package com.spd.common;
/**
 * 不同站名对应着不同的站号，因为会有不同的代表站，所以站号、类型都是数组的方式
 * @author Administrator
 *
 */
public class NationCityStation {
	//站号
	private String[] station_Id_C;
	//站名
	private String station_Name;
	//类型
	private String[] type;
	
	public String[] getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String[] stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	
}
