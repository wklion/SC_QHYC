package com.spd.common;

import java.util.List;

/**
 * 干旱统计参数类
 * @author Administrator
 *
 */
public class MCIStatisticsParam {

	//时间参数
	private TimesParam timesParam;
	//轻旱
	private double level1;
	//中旱
	private double level2;
	//重旱
	private double level3;
	//特旱
	private double level4;
	//干旱过程，连续多少天为轻度干旱以上，表示有干旱过程
	private int persistDays;
	//干旱过程，持续多少天没有干旱，用以解除干旱
	private int notMCIDays;
	//干旱事件，累积干旱持续时间超过评估时段的比例
	private double mciEventParam;
	//计算干旱日均值时，总天数以达到轻旱级别天数为准
	private boolean flag;
	//站点参数
	private List<String> station_id_Cs;
	
	public List<String> getStation_id_Cs() {
		return station_id_Cs;
	}
	public void setStation_id_Cs(List<String> stationIdCs) {
		station_id_Cs = stationIdCs;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getLevel1() {
		return level1;
	}
	public void setLevel1(double level1) {
		this.level1 = level1;
	}
	public double getLevel2() {
		return level2;
	}
	public void setLevel2(double level2) {
		this.level2 = level2;
	}
	public double getLevel3() {
		return level3;
	}
	public void setLevel3(double level3) {
		this.level3 = level3;
	}
	public double getLevel4() {
		return level4;
	}
	public void setLevel4(double level4) {
		this.level4 = level4;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getNotMCIDays() {
		return notMCIDays;
	}
	public void setNotMCIDays(int notMCIDays) {
		this.notMCIDays = notMCIDays;
	}
	public double getMciEventParam() {
		return mciEventParam;
	}
	public void setMciEventParam(double mciEventParam) {
		this.mciEventParam = mciEventParam;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
