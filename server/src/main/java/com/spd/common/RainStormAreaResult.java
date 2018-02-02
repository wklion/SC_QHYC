package com.spd.common;
/**
 * 区域暴雨评估结果
 * @author Administrator
 *
 */
public class RainStormAreaResult {
	// 当年暴雨开始期
	private String currentStartTime;
	// 当年发生频次
	private double currentTimes;
	// 当年极值
	private double currentExtPre;
	// 当年综合强度
	private double currentStrength;
	// 历年暴雨开始期
	private String yearsStartTime;
	// 历年发生频次
	private double yearsTimes;
	// 历年年极值
	private double yearsExtPre;
	// 历年综合强度
	private double yearsStrength;
	// 开始期距平
	private double anomalyStartTime;
	// 发生频次距平
	private double anomalyTimes;
	// 极值距平
	private double anomalyExtPre;
	// 综合强度距平
	private double anomalyStrength;
	
	public String getCurrentStartTime() {
		return currentStartTime;
	}
	public void setCurrentStartTime(String currentStartTime) {
		this.currentStartTime = currentStartTime;
	}
	public double getCurrentTimes() {
		return currentTimes;
	}
	public void setCurrentTimes(double currentTimes) {
		this.currentTimes = currentTimes;
	}
	public double getCurrentExtPre() {
		return currentExtPre;
	}
	public void setCurrentExtPre(double currentExtPre) {
		this.currentExtPre = currentExtPre;
	}
	public double getCurrentStrength() {
		return currentStrength;
	}
	public void setCurrentStrength(double currentStrength) {
		this.currentStrength = currentStrength;
	}
	public String getYearsStartTime() {
		return yearsStartTime;
	}
	public void setYearsStartTime(String yearsStartTime) {
		this.yearsStartTime = yearsStartTime;
	}
	public double getYearsTimes() {
		return yearsTimes;
	}
	public void setYearsTimes(double yearsTimes) {
		this.yearsTimes = yearsTimes;
	}
	public double getYearsExtPre() {
		return yearsExtPre;
	}
	public void setYearsExtPre(double yearsExtPre) {
		this.yearsExtPre = yearsExtPre;
	}
	public double getYearsStrength() {
		return yearsStrength;
	}
	public void setYearsStrength(double yearsStrength) {
		this.yearsStrength = yearsStrength;
	}
	public double getAnomalyStartTime() {
		return anomalyStartTime;
	}
	public void setAnomalyStartTime(double anomalyStartTime) {
		this.anomalyStartTime = anomalyStartTime;
	}
	public double getAnomalyTimes() {
		return anomalyTimes;
	}
	public void setAnomalyTimes(double anomalyTimes) {
		this.anomalyTimes = anomalyTimes;
	}
	public double getAnomalyExtPre() {
		return anomalyExtPre;
	}
	public void setAnomalyExtPre(double anomalyExtPre) {
		this.anomalyExtPre = anomalyExtPre;
	}
	public double getAnomalyStrength() {
		return anomalyStrength;
	}
	public void setAnomalyStrength(double anomalyStrength) {
		this.anomalyStrength = anomalyStrength;
	}
	
}
