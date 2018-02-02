package com.spd.common;
/**
 * 区域暴雨参数类
 * @author Administrator
 *
 */
public class RainStormAreaParam {
	//时间段参数
	private TimesParam timesParam;
	//日降水量最小值
	private double minDayPre;
	//日最小站数
	private int minDayStations;
	//总降水量权重
	private double weight1;
	//最大降水量权重
	private double weight2;
	//范围权重
	private double weight3;
	//持续时间权重
	private double weight4;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getMinDayPre() {
		return minDayPre;
	}
	public void setMinDayPre(double minDayPre) {
		this.minDayPre = minDayPre;
	}
	public int getMinDayStations() {
		return minDayStations;
	}
	public void setMinDayStations(int minDayStations) {
		this.minDayStations = minDayStations;
	}
	public double getWeight1() {
		return weight1;
	}
	public void setWeight1(double weight1) {
		this.weight1 = weight1;
	}
	public double getWeight2() {
		return weight2;
	}
	public void setWeight2(double weight2) {
		this.weight2 = weight2;
	}
	public double getWeight3() {
		return weight3;
	}
	public void setWeight3(double weight3) {
		this.weight3 = weight3;
	}
	public double getWeight4() {
		return weight4;
	}
	public void setWeight4(double weight4) {
		this.weight4 = weight4;
	}
	   
}
