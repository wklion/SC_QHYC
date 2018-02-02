package com.spd.common;

import java.util.List;

/**
 * 连阴雨，连续时间段查询的参数类。
 * @author Administrator
 *
 */
public class ContinuousRainsParam {
	//开始，结束时间参数类。
	private TimesRangeParam timesRangeParam;
	//对比的时间参数类
	private TimesRangeParam contrastTimesRangeParam;
	//对比的开始年
	private int startYear;
	//对比的结束年
	private int endYear;
	//站号
	private String stationIds;
	//连阴雨参数类
	private ContinuousRainsDefineParam continuousRainsDefineParam;
	
	public String getStationIds() {
		return stationIds;
	}
	public void setStationIds(String stationIds) {
		this.stationIds = stationIds;
	}
	public ContinuousRainsDefineParam getContinuousRainsDefineParam() {
		return continuousRainsDefineParam;
	}
	public void setContinuousRainsDefineParam(
			ContinuousRainsDefineParam continuousRainsDefineParam) {
		this.continuousRainsDefineParam = continuousRainsDefineParam;
	}
	public TimesRangeParam getContrastTimesRangeParam() {
		return contrastTimesRangeParam;
	}
	public void setContrastTimesRangeParam(TimesRangeParam contrastTimesRangeParam) {
		this.contrastTimesRangeParam = contrastTimesRangeParam;
	}
	public TimesRangeParam getTimesRangeParam() {
		return timesRangeParam;
	}
	public void setTimesRangeParam(TimesRangeParam timesRangeParam) {
		this.timesRangeParam = timesRangeParam;
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
