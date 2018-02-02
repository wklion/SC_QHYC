package com.spd.sc.pojo;

/**
 * 单站季节结果类
 * @author Administrator
 *
 */
public class SeasonResultItem implements Comparable<SeasonResultItem> {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//滑动开始索引，前端不展示
	private int huadongStartId;
	//开始日期
	private String startTime;
	//年
	private int year;
	
	public int getYear() {
		return year;
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
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public int getHuadongStartId() {
		return huadongStartId;
	}
	public void setHuadongStartId(int huadongStartId) {
		this.huadongStartId = huadongStartId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int compareTo(SeasonResultItem o) {
		return year - o.getYear();
	}
	
}
