package com.spd.common;
/**
 * 小时雨量 历史极值 参数
 * @author Administrator
 *
 */
public class HourRainHisExtParam {
	//开始结束时间
	private HourTimesParam hourTimesParam;
	//开始年限
	private int startYear;
	//结束年限
	private int endYear;
	//站点
	private String station_Id_C;
	//时长1,3,6,12,24
	private int hour;
	
	public HourTimesParam getHourTimesParam() {
		return hourTimesParam;
	}
	public void setHourTimesParam(HourTimesParam hourTimesParam) {
		this.hourTimesParam = hourTimesParam;
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
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	
}
