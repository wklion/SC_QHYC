package com.spd.common;
/**
 * 雷暴历年统计参数类
 * @author Administrator
 *
 */
public class ThundYearsParam {

	//站号数组
	private String[] station_Id_Cs;
	
	private TimesParam timesParam;
	
	private int startYear;

	private int endYear;
	//常年开始年
	private int perennialStartYear;
	//常年结束年
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
	
	
}
