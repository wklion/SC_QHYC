package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 区域连阴雨参数
 * @author Administrator
 *
 */
public class ContinueRainAreaParam {
	//时间
	private TimesParam timesParam;
	//最大过程持续天数
	private int maxPersistDays;
	//最大累计站点数
	private int maxSumStations;
	//最大有雨日数
	private int maxRainDays;
	//最大累积白天雨量
	private double maxSumPres;
	//最小过程持续天数
	private int minPersistDays;
	//最小累计站点数
	private int minSumStations;
	//最小有雨日数
	private int minRainDays;
	//最小累积白天雨量
	private double minSumPres;
	//不等权集成过程持续天数指数
	private double index1;
	//不等权集成累计站点数指数
	private double index2;
	//不等权集成有雨日数指数
	private double index3;
	//不等权集成累计白天雨量指数
	private double index4;
	//强度等级1
	private double strengthIndex1;
	//强度等级2
	private double strengthIndex2;
	//强度等级3
	private double strengthIndex3;
	//强度等级4
	private double strengthIndex4;
	//历年开始年
	private int startYear;
	//历年结束年
	private int endYear;
	//常年开始年
	private int perennialStartYear;
	//常年结束年
	private int perennialEndYear;
	
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
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public int getMaxPersistDays() {
		return maxPersistDays;
	}
	public void setMaxPersistDays(int maxPersistDays) {
		this.maxPersistDays = maxPersistDays;
	}
	public int getMaxSumStations() {
		return maxSumStations;
	}
	public void setMaxSumStations(int maxSumStations) {
		this.maxSumStations = maxSumStations;
	}
	public int getMaxRainDays() {
		return maxRainDays;
	}
	public void setMaxRainDays(int maxRainDays) {
		this.maxRainDays = maxRainDays;
	}
	public double getMaxSumPres() {
		return maxSumPres;
	}
	public void setMaxSumPres(double maxSumPres) {
		this.maxSumPres = maxSumPres;
	}
	public int getMinPersistDays() {
		return minPersistDays;
	}
	public void setMinPersistDays(int minPersistDays) {
		this.minPersistDays = minPersistDays;
	}
	public int getMinSumStations() {
		return minSumStations;
	}
	public void setMinSumStations(int minSumStations) {
		this.minSumStations = minSumStations;
	}
	public int getMinRainDays() {
		return minRainDays;
	}
	public void setMinRainDays(int minRainDays) {
		this.minRainDays = minRainDays;
	}
	public double getMinSumPres() {
		return minSumPres;
	}
	public void setMinSumPres(double minSumPres) {
		this.minSumPres = minSumPres;
	}
	public double getStrengthIndex1() {
		return strengthIndex1;
	}
	public void setStrengthIndex1(double strengthIndex1) {
		this.strengthIndex1 = strengthIndex1;
	}
	public double getStrengthIndex2() {
		return strengthIndex2;
	}
	public void setStrengthIndex2(double strengthIndex2) {
		this.strengthIndex2 = strengthIndex2;
	}
	public double getStrengthIndex3() {
		return strengthIndex3;
	}
	public void setStrengthIndex3(double strengthIndex3) {
		this.strengthIndex3 = strengthIndex3;
	}
	public double getStrengthIndex4() {
		return strengthIndex4;
	}
	public void setStrengthIndex4(double strengthIndex4) {
		this.strengthIndex4 = strengthIndex4;
	}
	public double getIndex1() {
		return index1;
	}
	public void setIndex1(double index1) {
		this.index1 = index1;
	}
	public double getIndex2() {
		return index2;
	}
	public void setIndex2(double index2) {
		this.index2 = index2;
	}
	public double getIndex3() {
		return index3;
	}
	public void setIndex3(double index3) {
		this.index3 = index3;
	}
	public double getIndex4() {
		return index4;
	}
	public void setIndex4(double index4) {
		this.index4 = index4;
	}
	
}
