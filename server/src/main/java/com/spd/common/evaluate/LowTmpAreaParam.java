package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 区域低温参数类
 * @author Administrator
 *
 */
public class LowTmpAreaParam {
	//时间
	private TimesParam timesParam;
	//最大持续时间
	private int maxPersistDays;
	//最小持续时间
	private int minPersistDays;
	//最大累积站点
	private int maxSumStation;
	//最小累积站点
	private int minSumStation;
	//最大累积气温距平
	private double maxSumAnomaly;
	//最小累积气温距平
	private double minSumAnomaly;
	//持续时间权重
	private double persistDayWeight;
	//累积站点权重
	private double sumStationWeight;
	//气温距平权重
	private double anomalyWeight;
	//轻度强度值
	private double level1;
	//中度强度值
	private double level2;
	//重度强度值
	private double level3;
	//特重强度值
	private double level4;
	
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
	public int getMinPersistDays() {
		return minPersistDays;
	}
	public void setMinPersistDays(int minPersistDays) {
		this.minPersistDays = minPersistDays;
	}
	public int getMaxSumStation() {
		return maxSumStation;
	}
	public void setMaxSumStation(int maxSumStation) {
		this.maxSumStation = maxSumStation;
	}
	public int getMinSumStation() {
		return minSumStation;
	}
	public void setMinSumStation(int minSumStation) {
		this.minSumStation = minSumStation;
	}
	public double getMaxSumAnomaly() {
		return maxSumAnomaly;
	}
	public void setMaxSumAnomaly(double maxSumAnomaly) {
		this.maxSumAnomaly = maxSumAnomaly;
	}
	public double getMinSumAnomaly() {
		return minSumAnomaly;
	}
	public void setMinSumAnomaly(double minSumAnomaly) {
		this.minSumAnomaly = minSumAnomaly;
	}
	public double getPersistDayWeight() {
		return persistDayWeight;
	}
	public void setPersistDayWeight(double persistDayWeight) {
		this.persistDayWeight = persistDayWeight;
	}
	public double getSumStationWeight() {
		return sumStationWeight;
	}
	public void setSumStationWeight(double sumStationWeight) {
		this.sumStationWeight = sumStationWeight;
	}
	public double getAnomalyWeight() {
		return anomalyWeight;
	}
	public void setAnomalyWeight(double anomalyWeight) {
		this.anomalyWeight = anomalyWeight;
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
	
	
}
