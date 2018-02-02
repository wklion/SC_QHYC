package com.spd.common;
/**
 * 高温灾害历年同期参数类。
 * @author Administrator
 *
 */
public class HighTmpYearsParam {
	//站号数组
	private String[] station_Id_Cs;
	//时间段参数
	private TimesParam timesParam;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//一般高温
	private double level1HighTmp;
	//中等高温
	private double level2HighTmp;
	//严重高温
	private double level3HighTmp;
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
	public double getLevel1HighTmp() {
		return level1HighTmp;
	}
	public void setLevel1HighTmp(double level1HighTmp) {
		this.level1HighTmp = level1HighTmp;
	}
	public double getLevel2HighTmp() {
		return level2HighTmp;
	}
	public void setLevel2HighTmp(double level2HighTmp) {
		this.level2HighTmp = level2HighTmp;
	}
	public double getLevel3HighTmp() {
		return level3HighTmp;
	}
	public void setLevel3HighTmp(double level3HighTmp) {
		this.level3HighTmp = level3HighTmp;
	}
	public String[] getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String[] stationIdCs) {
		station_Id_Cs = stationIdCs;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
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
	
	public HighTmpYearsParam copy() {
		HighTmpYearsParam highTmpYearsParam = new HighTmpYearsParam();
		highTmpYearsParam.setEndYear(endYear);
		highTmpYearsParam.setLevel1HighTmp(level1HighTmp);
		highTmpYearsParam.setLevel2HighTmp(level2HighTmp);
		highTmpYearsParam.setLevel3HighTmp(level3HighTmp);
		highTmpYearsParam.setPerennialEndYear(perennialEndYear);
		highTmpYearsParam.setPerennialStartYear(perennialStartYear);
		highTmpYearsParam.setStartYear(startYear);
		highTmpYearsParam.setStation_Id_Cs(station_Id_Cs);
		highTmpYearsParam.setTimesParam(timesParam);
		return highTmpYearsParam;
	}
}
