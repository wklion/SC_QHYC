package com.spd.common;
/**
 * 积温参数类
 * @author Administrator
 *
 */
public class AccumulatedTempParam {
	//积温最低值
	private double minTmp;
	//时间参数
	private TimesParam timesParam;
	//多年均值开始年
	private int perennialStartYear;
	//多年均值结束年
	private int perennialEndYear;
	//站点类型
	private String station_Id_C;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public double getMinTmp() {
		return minTmp;
	}
	public void setMinTmp(double minTmp) {
		this.minTmp = minTmp;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public int getPerennialStartYear() {
		return perennialStartYear;
	}
	public void setPerennialStartYear(int perennialStartYear) {
		this.perennialStartYear = perennialStartYear;
	}
	public int getPerennialEndYear() {
		return perennialEndYear;
	}
	public void setPerennialEndYear(int perennialEndYear) {
		this.perennialEndYear = perennialEndYear;
	}
	
}
