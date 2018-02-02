package com.spd.common;

/**
 * 按时间段统计高温结果类。合计
 * @author Administrator
 *
 */
public class HighTmpRangeResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//总次数
	private int totalCnt;
	//一般高温次数
	private int level1Cnt;
	//中等高温日数
	private int level2Cnt;
	//严重高温日数
	private int level3Cnt;
	//高温极值
	private double extHighTmp;
	//极值日期
	private String extHighTmpTime;
	
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
	public int getTotalCnt() {
		return totalCnt;
	}
	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
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
	public double getExtHighTmp() {
		return extHighTmp;
	}
	public void setExtHighTmp(double extHighTmp) {
		this.extHighTmp = extHighTmp;
	}
	public String getExtHighTmpTime() {
		return extHighTmpTime;
	}
	public void setExtHighTmpTime(String extHighTmpTime) {
		this.extHighTmpTime = extHighTmpTime;
	}
	
}
