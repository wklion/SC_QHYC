package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 区域高温统计参数
 * @author Administrator
 *
 */
public class HighTmpAreaYearsParam {
	//时间
	private TimesParam timesParam;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//常年开始年份
	private int perennialStartYear;
	//常年结束年份
	private int perennialEndYear;
	//YHI对应的级别1区分所在百分比
	private double YHILevel1;
	//YHI对应的级别2区分所在百分比
	private double YHILevel2;
	//YHI对应的级别3区分所在百分比
	private double YHILevel3;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
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
	public double getYHILevel1() {
		return YHILevel1;
	}
	public void setYHILevel1(double yHILevel1) {
		YHILevel1 = yHILevel1;
	}
	public double getYHILevel2() {
		return YHILevel2;
	}
	public void setYHILevel2(double yHILevel2) {
		YHILevel2 = yHILevel2;
	}
	public double getYHILevel3() {
		return YHILevel3;
	}
	public void setYHILevel3(double yHILevel3) {
		YHILevel3 = yHILevel3;
	}
	
}
