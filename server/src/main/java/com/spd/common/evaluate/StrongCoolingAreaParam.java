package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 区域降温参数
 * @author Administrator
 *
 */
public class StrongCoolingAreaParam {
	//时间
	private TimesParam timesParam;
	//站数最大值
	private int maxStations;
	//站数最小值
	private int minStations;
	//持续天数最大值
	private int maxPersistDays;
	//持续天数最小值
	private int minPersistDays;
	//过程降温极大值
	private double maxCoolingTmp;
	//过程降温极小值
	private double minCoolingTmp;
	//站数权重
	private double weight1;
	//持续天数权重
	private double weight2;
	//降温极大值权重
	private double weight3;
	//均值权重
	private double weight4;
	//中度等级
	private double level1;
	//重度等级
	private double level2;
	//特重等级
	private double level3;
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
	public double getMaxCoolingTmp() {
		return maxCoolingTmp;
	}
	public void setMaxCoolingTmp(double maxCoolingTmp) {
		this.maxCoolingTmp = maxCoolingTmp;
	}
	public double getMinCoolingTmp() {
		return minCoolingTmp;
	}
	public void setMinCoolingTmp(double minCoolingTmp) {
		this.minCoolingTmp = minCoolingTmp;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public int getMaxStations() {
		return maxStations;
	}
	public void setMaxStations(int maxStations) {
		this.maxStations = maxStations;
	}
	public int getMinStations() {
		return minStations;
	}
	public void setMinStations(int minStations) {
		this.minStations = minStations;
	}
	public int getMaxPersistDays() {
		return maxPersistDays;
	}
	public void setMaxPersistDays(int maxPersistDays) {
		this.maxPersistDays = maxPersistDays;
	}
	public int getMinPersistDays() {
		return minPersistDays;
	}
	public void setMinPersistDays(int minPersistDays) {
		this.minPersistDays = minPersistDays;
	}
}
