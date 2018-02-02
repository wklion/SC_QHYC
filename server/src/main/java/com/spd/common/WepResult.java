package com.spd.common;
/**
 * 天气现象结果类
 * @author Administrator
 *
 */
public class WepResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//日期
	private String datetime;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//代码
	private String code;
	//现象
	private String name;
	//天气现象
	private String wepRecord;
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWepRecord() {
		return wepRecord;
	}
	public void setWepRecord(String wepRecord) {
		this.wepRecord = wepRecord;
	}
	
}
