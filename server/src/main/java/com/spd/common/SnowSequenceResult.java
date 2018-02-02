package com.spd.common;
/**
 * 降雪的结果序列
 * @author Administrator
 *
 */
public class SnowSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//日期
	private String datetime;
	//积雪
	private Integer GSS;
	//积雪深度
	private Double snow_Depth;
	//降雪
	private Integer snow;
	
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
	public Integer getGSS() {
		return GSS;
	}
	public void setGSS(Integer gSS) {
		GSS = gSS;
	}
	public Double getSnow_Depth() {
		return snow_Depth;
	}
	public void setSnow_Depth(Double snowDepth) {
		snow_Depth = snowDepth;
	}
	public Integer getSnow() {
		return snow;
	}
	public void setSnow(Integer snow) {
		this.snow = snow;
	}
	
	
	
}
