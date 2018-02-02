package com.spd.pojo;

/**
 * 年较差结果类
 * @author Administrator
 *
 */
public class TmpGapAvgYearResult {
	//站号
	private String Station_Id_C;
	//站名
	private String Station_Name;
	//年份
	private int year;
	//最大月份
	private String maxMonth;
	//最小月份
	private String minMonth;
	//最大月平均气温
	private Double maxMonthAvgTmp;
	//最小月平均气温
	private Double minMonthAvgTmp;
	//年较差
	private Double monthTmpGap;
	
	public String getStation_Id_C() {
		return Station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		Station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return Station_Name;
	}
	public void setStation_Name(String stationName) {
		Station_Name = stationName;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Double getMaxMonthAvgTmp() {
		return maxMonthAvgTmp;
	}
	public void setMaxMonthAvgTmp(Double maxMonthAvgTmp) {
		this.maxMonthAvgTmp = maxMonthAvgTmp;
	}
	public Double getMinMonthAvgTmp() {
		return minMonthAvgTmp;
	}
	public void setMinMonthAvgTmp(Double minMonthAvgTmp) {
		this.minMonthAvgTmp = minMonthAvgTmp;
	}
	public Double getMonthTmpGap() {
		return monthTmpGap;
	}
	public void setMonthTmpGap(Double monthTmpGap) {
		this.monthTmpGap = monthTmpGap;
	}
	public String getMaxMonth() {
		return maxMonth;
	}
	public void setMaxMonth(String maxMonth) {
		this.maxMonth = maxMonth;
	}
	public String getMinMonth() {
		return minMonth;
	}
	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}
	
}
