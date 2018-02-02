package com.spd.common;
/**
 * 按时间段统计暴雨的参数类
 * @author Administrator
 *
 */
public class RainStormRangeParam {
	//站号数组
	private String[] station_Id_Cs;
	//时间参数
	private TimesParam timesParam;
	//暴雨
	private double level1;
	//大暴雨
	private double level2;
	//特大暴雨
	private double level3;
	//对应的表
	private String tableName;
	//地区分类
	private String stationType;
	
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	public String[] getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String[] stationIdCs) {
		station_Id_Cs = stationIdCs;
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
