package com.spd.common;
/**
 * 降雪结果。合计
 * @author Administrator
 *
 */
public class SnowResultTotal {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//有积雪日数
	private int gssDays;
	//有降雪日数
	private int snowDays;
	//积雪最深深度
	private double maxSnow_Depth;
	//积雪平均深度
	private double avgSnow_Depth;
	
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public int getGssDays() {
		return gssDays;
	}
	public void setGssDays(int gssDays) {
		this.gssDays = gssDays;
	}
	public int getSnowDays() {
		return snowDays;
	}
	public void setSnowDays(int snowDays) {
		this.snowDays = snowDays;
	}
	public double getMaxSnow_Depth() {
		return maxSnow_Depth;
	}
	public void setMaxSnow_Depth(double maxSnowDepth) {
		maxSnow_Depth = maxSnowDepth;
	}
	public double getAvgSnow_Depth() {
		return avgSnow_Depth;
	}
	public void setAvgSnow_Depth(double avgSnowDepth) {
		avgSnow_Depth = avgSnowDepth;
	}
}
