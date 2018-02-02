package com.spd.common;
/**
 * 根据时间段查询极值，极值日期等
 * @author Administrator
 *
 */
public class HourRainExtParam {
	//时间参数
	private HourTimesParam hourTimesParam;
	//查询类型
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
	
}
