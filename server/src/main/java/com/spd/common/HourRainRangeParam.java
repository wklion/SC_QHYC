package com.spd.common;
/**
 * 小时降水，时段位次参数
 * @author Administrator
 *
 */
public class HourRainRangeParam {

	//极值开始时间、极值结束时间
	private HourTimesParam extTimesParam;
	//位次开始时间、位次结束时间
	private HourTimesParam rankTimesParam;
	//时长1,3,6,12,24
	private int hour;
	//类型
	private String type;
	
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
	public HourTimesParam getExtTimesParam() {
		return extTimesParam;
	}
	public void setExtTimesParam(HourTimesParam extTimesParam) {
		this.extTimesParam = extTimesParam;
	}
	public HourTimesParam getRankTimesParam() {
		return rankTimesParam;
	}
	public void setRankTimesParam(HourTimesParam rankTimesParam) {
		this.rankTimesParam = rankTimesParam;
	}
	
}
