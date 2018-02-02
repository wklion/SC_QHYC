package com.spd.common;
/**
 * 灾害分析中暴雨合计结果类
 * @author Administrator
 *
 */
public class DisasterRainStormTotalResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//总次数
	private int sum;
	//暴雨次数
	private int level1Cnt;
	//大暴雨次数
	private int level2Cnt;
	//特大暴雨次数
	private int level3Cnt;
	//降水极值
	private double extValue;
	//极值日期
	private String extDatetime;
	
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
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int getLevel1Cnt() {
		return level1Cnt;
	}
	public void setLevel1Cnt(int level1Cnt) {
		this.level1Cnt = level1Cnt;
	}
	public int getLevel2Cnt() {
		return level2Cnt;
	}
	public void setLevel2Cnt(int level2Cnt) {
		this.level2Cnt = level2Cnt;
	}
	public int getLevel3Cnt() {
		return level3Cnt;
	}
	public void setLevel3Cnt(int level3Cnt) {
		this.level3Cnt = level3Cnt;
	}
	public double getExtValue() {
		return extValue;
	}
	public void setExtValue(double extValue) {
		this.extValue = extValue;
	}
	public String getExtDatetime() {
		return extDatetime;
	}
	public void setExtDatetime(String extDatetime) {
		this.extDatetime = extDatetime;
	}
}
