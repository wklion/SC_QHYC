package com.spd.common;

import java.util.Comparator;

/**
 * 日数统计结果类
 * @author Administrator
 *
 */
public class DaysResult implements Comparator<DaysResult> {
	//序号
	private int index;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//日数
	private double days;
	//多年均值
	private double hisAvgDays;
	//距平
	private double anomaly;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public double getDays() {
		return days;
	}
	public void setDays(double days) {
		this.days = days;
	}
	public double getHisAvgDays() {
		return hisAvgDays;
	}
	public void setHisAvgDays(double hisAvgDays) {
		this.hisAvgDays = hisAvgDays;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}
	/**
	 * 从大到小排序
	 */
	public int compare(DaysResult o1, DaysResult o2) {
		double days1 = o1.getDays();
		double days2 = o2.getDays();
		if(days1 > days2) return -1;
		if(days1 < days2) return 1;
		if(days1 == days2) return 0;
		return 0;
	}
	
}
