package com.spd.common;

import java.util.List;

/**
 * 空调度日、采暖度日的年度结果类
 * @author Administrator
 *
 */
public class TmpDaysYearResult {
	//类型:采暖：HEAT, 空调：COOL
	private String type;
	//年份
	private int year;
	//结果List
	private List<Double> resultList;
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
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List<Double> getResultList() {
		return resultList;
	}
	public void setResultList(List<Double> resultList) {
		this.resultList = resultList;
	}
	
}
