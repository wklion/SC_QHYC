package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 单站连阴雨查询的参数类
 * @author Administrator
 *
 */
public class ContinueRainStationParam {
	//时间参数
	private TimesParam timesParam;
	//最大单站持续时间
	private int maxSingleDays;
	//最大单站有雨日数
	private int maxSingleRainDays;
	//最大单站累计白天降水量
	private double maxSinglePre;
	//最小单站持续时间
	private int minSingleDays;
	//最小单站有雨日数
	private int minSingleRainDays;
	//最小单站累计白天降水量
	private double minSinglePre;
	//不等权集成持续时间指数
	private double persistDaysIndex;
	//不等权集成有雨日数指数
	private double preDaysIndex;
	//不等权集成白天降水指数
	private double preIndex;
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
	public int getMaxSingleDays() {
		return maxSingleDays;
	}
	public void setMaxSingleDays(int maxSingleDays) {
		this.maxSingleDays = maxSingleDays;
	}
	public int getMaxSingleRainDays() {
		return maxSingleRainDays;
	}
	public void setMaxSingleRainDays(int maxSingleRainDays) {
		this.maxSingleRainDays = maxSingleRainDays;
	}
	public double getMaxSinglePre() {
		return maxSinglePre;
	}
	public void setMaxSinglePre(double maxSinglePre) {
		this.maxSinglePre = maxSinglePre;
	}
	public int getMinSingleDays() {
		return minSingleDays;
	}
	public void setMinSingleDays(int minSingleDays) {
		this.minSingleDays = minSingleDays;
	}
	public int getMinSingleRainDays() {
		return minSingleRainDays;
	}
	public void setMinSingleRainDays(int minSingleRainDays) {
		this.minSingleRainDays = minSingleRainDays;
	}
	public double getMinSinglePre() {
		return minSinglePre;
	}
	public void setMinSinglePre(double minSinglePre) {
		this.minSinglePre = minSinglePre;
	}
	public double getPersistDaysIndex() {
		return persistDaysIndex;
	}
	public void setPersistDaysIndex(double persistDaysIndex) {
		this.persistDaysIndex = persistDaysIndex;
	}
	public double getPreDaysIndex() {
		return preDaysIndex;
	}
	public void setPreDaysIndex(double preDaysIndex) {
		this.preDaysIndex = preDaysIndex;
	}
	public double getPreIndex() {
		return preIndex;
	}
	public void setPreIndex(double preIndex) {
		this.preIndex = preIndex;
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
	
}
