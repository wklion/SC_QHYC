package com.spd.common;
/**
 * 空调度日、采暖度日的年度参数类
 * @author Administrator
 *
 */
public class TmpDaysYearParam {
	//类型:采暖：HEAT, 空调：COOL
	private String type;
	//基础温度
	private double tmp;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//站号
	private String station_Id_C;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getTmp() {
		return tmp;
	}
	public void setTmp(double tmp) {
		this.tmp = tmp;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	
}
