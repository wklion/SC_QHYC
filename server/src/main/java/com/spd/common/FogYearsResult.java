package com.spd.common;

/**
 * 雾历年同期结果类
 * @author Administrator
 *
 */
public class FogYearsResult {
	//年份
	private int year;
	//雾日
	private double fogDays;
	//常年值
	private double yearsFogDays;
	//距平率
	private double anomalyRate;
	//最小能见度
	private double vis_Min;
	//轻雾日数
	private double mistCnt;
	
	public double getMistCnt() {
		return mistCnt;
	}
	public void setMistCnt(double mistCnt) {
		this.mistCnt = mistCnt;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getFogDays() {
		return fogDays;
	}
	public void setFogDays(double fogDays) {
		this.fogDays = fogDays;
	}
	public double getYearsFogDays() {
		return yearsFogDays;
	}
	public void setYearsFogDays(double yearsFogDays) {
		this.yearsFogDays = yearsFogDays;
	}
	public double getAnomalyRate() {
		return anomalyRate;
	}
	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}
	public double getVis_Min() {
		return vis_Min;
	}
	public void setVis_Min(double visMin) {
		vis_Min = visMin;
	}
	
}
