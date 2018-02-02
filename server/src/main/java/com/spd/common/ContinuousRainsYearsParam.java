package com.spd.common;

/**
 * 连阴雨，连续时间段查询（历年同期）的参数类。
 * @author Administrator
 *
 */
public class ContinuousRainsYearsParam {
	//开始，结束时间参数类。
	private TimesYearsParam timesYearsParam;
	//连阴雨参数类
	private ContinuousRainsDefineParam continuousRainsDefineParam;
	//站点
	private String stations;
	
	public ContinuousRainsDefineParam getContinuousRainsDefineParam() {
		return continuousRainsDefineParam;
	}
	public void setContinuousRainsDefineParam(
			ContinuousRainsDefineParam continuousRainsDefineParam) {
		this.continuousRainsDefineParam = continuousRainsDefineParam;
	}
	public String getStations() {
		return stations;
	}
	public void setStations(String stations) {
		this.stations = stations;
	}
	public TimesYearsParam getTimesYearsParam() {
		return timesYearsParam;
	}
	public void setTimesYearsParam(TimesYearsParam timesYearsParam) {
		this.timesYearsParam = timesYearsParam;
	}
	
}
