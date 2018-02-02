package com.spd.pojo;

/**
 * 位次计算的结果对象
 * @author Administrator
 *
 */
public class RankResult {
	// 序号
	private int index;
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//年值
	private Double yearValue;
	//年次
	private Integer yearRanking;
	//极值
	private Double extValue;
	//极值年
	private Integer extYears;
	//极值日期
	private String extDateStr;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getExtDateStr() {
		return extDateStr;
	}
	public void setExtDateStr(String extDateStr) {
		this.extDateStr = extDateStr;
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
	public Double getYearValue() {
		return yearValue;
	}
	public void setYearValue(Double yearValue) {
		this.yearValue = yearValue;
	}
	public Integer getYearRanking() {
		return yearRanking;
	}
	public void setYearRanking(Integer yearRanking) {
		this.yearRanking = yearRanking;
	}
	public Double getExtValue() {
		return extValue;
	}
	public void setExtValue(Double extValue) {
		this.extValue = extValue;
	}
	public Integer getExtYears() {
		return extYears;
	}
	public void setExtYears(Integer extYears) {
		this.extYears = extYears;
	}
	
}
