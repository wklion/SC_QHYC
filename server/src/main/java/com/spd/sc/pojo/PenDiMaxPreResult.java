package com.spd.sc.pojo;

/**
 * 盆地大雨结果类
 * @author Administrator
 *
 */
public class PenDiMaxPreResult {
	//日期
	private String datetime;
	//站号
	private String Station_Id_C;
	//站名
	private String Station_Name;
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getStation_Id_C() {
		return Station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		Station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return Station_Name;
	}
	public void setStation_Name(String stationName) {
		Station_Name = stationName;
	}
	
}
