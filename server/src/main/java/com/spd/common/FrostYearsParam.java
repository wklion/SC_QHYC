package com.spd.common;
/**
 * 霜冻灾害历年同期参数类。
 * @author Administrator
 *
 */
public class FrostYearsParam {
	//站号数组
	private String[] station_Id_Cs;
	//时间段参数
	private TimesParam timesParam;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//常年开始年份
	private int perennialStartYear;
	//常年结束年份
	private int perennialEndYear;
	//一般冻害，连续天数
	private int level1PersistDays;
	//一般冻害，低温下限
	private double level1LowTmp;
	//一般冻害，任意天数气温低于某个界限
	private int level1LTLowTmpDays;
	//一般冻害，任意天数气温低于的界限
	private double level1LTLowTmp;
	//严重冻害，连续天数
	private int level2PersistDays;
	//严重冻害，低温下限
	private double level2LowTmp;
	//严重冻害，任意天数气温低于某个界限
	private int level2LTLowTmpDays;
	//严重冻害，任意天数气温低于的界限
	private double level2LTLowTmp;
	
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
	public String[] getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String[] stationIdCs) {
		station_Id_Cs = stationIdCs;
	}
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
	public int getLevel1PersistDays() {
		return level1PersistDays;
	}
	public void setLevel1PersistDays(int level1PersistDays) {
		this.level1PersistDays = level1PersistDays;
	}
	public double getLevel1LowTmp() {
		return level1LowTmp;
	}
	public void setLevel1LowTmp(double level1LowTmp) {
		this.level1LowTmp = level1LowTmp;
	}
	public int getLevel1LTLowTmpDays() {
		return level1LTLowTmpDays;
	}
	public void setLevel1LTLowTmpDays(int level1ltLowTmpDays) {
		level1LTLowTmpDays = level1ltLowTmpDays;
	}
	public double getLevel1LTLowTmp() {
		return level1LTLowTmp;
	}
	public void setLevel1LTLowTmp(double level1ltLowTmp) {
		level1LTLowTmp = level1ltLowTmp;
	}
	public int getLevel2PersistDays() {
		return level2PersistDays;
	}
	public void setLevel2PersistDays(int level2PersistDays) {
		this.level2PersistDays = level2PersistDays;
	}
	public double getLevel2LowTmp() {
		return level2LowTmp;
	}
	public void setLevel2LowTmp(double level2LowTmp) {
		this.level2LowTmp = level2LowTmp;
	}
	public int getLevel2LTLowTmpDays() {
		return level2LTLowTmpDays;
	}
	public void setLevel2LTLowTmpDays(int level2ltLowTmpDays) {
		level2LTLowTmpDays = level2ltLowTmpDays;
	}
	public double getLevel2LTLowTmp() {
		return level2LTLowTmp;
	}
	public void setLevel2LTLowTmp(double level2ltLowTmp) {
		level2LTLowTmp = level2ltLowTmp;
	}
	
	
}
