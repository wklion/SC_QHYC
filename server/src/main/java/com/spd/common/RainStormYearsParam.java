package com.spd.common;
/**
 * 历年同期统计暴雨的参数类
 * @author Administrator
 *
 */
public class RainStormYearsParam {
	//站号数组
	private String[] station_Id_Cs;
	//时间
	private TimesParam timesParam;
	//暴雨
	private double level1;
	//大暴雨
	private double level2;
	//特大暴雨
	private double level3;
	//对应的表
	private String tableName;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//常年开始年份
	private int perennialStartYear;
	//常年结束年份
	private int perennialEndYear;
	
	public int getPerennialStartYear() {
		return perennialStartYear;
	}
	public void setPerennialStartYear(int perennialStartYear) {
		this.perennialStartYear = perennialStartYear;
	}
	public int getPerennialEndYear() {
		return perennialEndYear;
	}
	public void setPerennialEndYear(int perennialEndYear) {
		this.perennialEndYear = perennialEndYear;
	}
	public String[] getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String[] stationIdCs) {
		station_Id_Cs = stationIdCs;
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
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getLevel1() {
		return level1;
	}
	public void setLevel1(double level1) {
		this.level1 = level1;
	}
	public double getLevel2() {
		return level2;
	}
	public void setLevel2(double level2) {
		this.level2 = level2;
	}
	public double getLevel3() {
		return level3;
	}
	public void setLevel3(double level3) {
		this.level3 = level3;
	}
	
	
}
