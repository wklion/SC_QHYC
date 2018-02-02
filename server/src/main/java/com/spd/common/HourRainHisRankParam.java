package com.spd.common;
/**
 * 小时雨量、同期位次、参数
 * @author Administrator
 *
 */
public class HourRainHisRankParam {
	//极值开始时间、极值结束时间
	private HourTimesParam hourTimesParam;
	//比较的开始年
	private int startYear;
	//比较的结束年
	private int endYear;
	//时长1,3,6,12,24
	private int hour;
	//类型
	private String type;
	
	public HourTimesParam getHourTimesParam() {
		return hourTimesParam;
	}
	public void setHourTimesParam(HourTimesParam hourTimesParam) {
		this.hourTimesParam = hourTimesParam;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
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
