package com.spd.common;
/**
 * 高温时间段统计参数类
 * @author Administrator
 *
 */
public class HighTmpRangeParam {
	
	//站号
	private String[] stations;
	//时间参数
	private TimesParam timesParam;
	//一般高温
	private double level1HighTmp;
	//中等高温
	private double level2HighTmp;
	//严重高温
	private double level3HighTmp;
	
	public String[] getStations() {
		return stations;
	}
	public void setStations(String[] stations) {
		this.stations = stations;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
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
	
	
}
