package com.spd.common;
/**
 * 轻雾的结果序列
 * @author Administrator
 *
 */
public class MistSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//日期
	private String datetime;
	//最小能见度
	private double vis_Min;
	//平均能见度
	private double vis_Avg;
	//最小相对湿度
	private double rhu_Min;
	//相对湿度
	private double rhu_Avg;
	//轻雾(不在客户端展示)
	private double mist;
	
	public double getMist() {
		return mist;
	}
	public void setMist(double mist) {
		this.mist = mist;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public double getVis_Min() {
		return vis_Min;
	}
	public void setVis_Min(double visMin) {
		vis_Min = visMin;
	}
	public double getVis_Avg() {
		return vis_Avg;
	}
	public void setVis_Avg(double visAvg) {
		vis_Avg = visAvg;
	}
	public double getRhu_Min() {
		return rhu_Min;
	}
	public void setRhu_Min(double rhuMin) {
		rhu_Min = rhuMin;
	}
	public double getRhu_Avg() {
		return rhu_Avg;
	}
	public void setRhu_Avg(double rhuAvg) {
		rhu_Avg = rhuAvg;
	}
	
	
}
