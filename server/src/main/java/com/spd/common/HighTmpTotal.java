package com.spd.common;
/**
 * 高温日数
 * @author Administrator
 *
 */
public class HighTmpTotal {
	// 站号
	private String station_Id_C;
	// 站名
	private String station_Name;
	// >= 35
	private int gt35Days;
	// >= 35 && < 37
	private int gt35lt37Days;
	// >= 37
	private int gt37Days;
	// >= 37 && < 39
	private int gt37lt39Days;
	// >= 40
	private int gt40Days;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public int getGt35Days() {
		return gt35Days;
	}
	public void setGt35Days(int gt35Days) {
		this.gt35Days = gt35Days;
	}
	public int getGt35lt37Days() {
		return gt35lt37Days;
	}
	public void setGt35lt37Days(int gt35lt37Days) {
		this.gt35lt37Days = gt35lt37Days;
	}
	public int getGt37Days() {
		return gt37Days;
	}
	public void setGt37Days(int gt37Days) {
		this.gt37Days = gt37Days;
	}
	public int getGt37lt39Days() {
		return gt37lt39Days;
	}
	public void setGt37lt39Days(int gt37lt39Days) {
		this.gt37lt39Days = gt37lt39Days;
	}
	public int getGt40Days() {
		return gt40Days;
	}
	public void setGt40Days(int gt40Days) {
		this.gt40Days = gt40Days;
	}
	
}
