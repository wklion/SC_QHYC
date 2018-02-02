package com.spd.common;

/**
 * 灾害统计，低温阴雨参数
 * @author Administrator
 *
 */
public class DisasterLowTmpParam {

	//开始月
	private int startMon;
	//开始日
	private int startDay;
	//结束月
	private int endMon;
	//结束日
	private int endDay;
	//连续天数
	private int sequenceDays;
	//日平均气温
	private double avgTmp;
	//是否过滤白天有雨日数
	private boolean isFilterRainDays;
	//白天有雨日数 默认为6天
	private int rainDays = 6;
	//年份
	private int year;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getStartMon() {
		return startMon;
	}
	public void setStartMon(int startMon) {
		this.startMon = startMon;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getEndMon() {
		return endMon;
	}
	public void setEndMon(int endMon) {
		this.endMon = endMon;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	public int getSequenceDays() {
		return sequenceDays;
	}
	public void setSequenceDays(int sequenceDays) {
		this.sequenceDays = sequenceDays;
	}
	public double getAvgTmp() {
		return avgTmp;
	}
	public void setAvgTmp(double avgTmp) {
		this.avgTmp = avgTmp;
	}
	public boolean isFilterRainDays() {
		return isFilterRainDays;
	}
	public void setFilterRainDays(boolean isFilterRainDays) {
		this.isFilterRainDays = isFilterRainDays;
	}
	public int getRainDays() {
		return rainDays;
	}
	public void setRainDays(int rainDays) {
		this.rainDays = rainDays;
	}
	
}
