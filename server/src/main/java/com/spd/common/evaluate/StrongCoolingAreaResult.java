package com.spd.common.evaluate;

/**
 * 区域强降温结果类
 * @author Administrator
 *
 */
public class StrongCoolingAreaResult {
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续天数
	private int persistDays;
	//影响站数
	private int stationCnt;
	//过程降温极大值
	private double maxTmp;
	//过程降温极小值
	private double minTmp;
	//过程降温均值
	private double avgTmp;
	//综合指数
	private double index;
	//等级
	private String level;
	
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public double getMaxTmp() {
		return maxTmp;
	}
	public void setMaxTmp(double maxTmp) {
		this.maxTmp = maxTmp;
	}
	public double getMinTmp() {
		return minTmp;
	}
	public void setMinTmp(double minTmp) {
		this.minTmp = minTmp;
	}
	public double getAvgTmp() {
		return avgTmp;
	}
	public void setAvgTmp(double avgTmp) {
		this.avgTmp = avgTmp;
	}
	public double getIndex() {
		return index;
	}
	public void setIndex(double index) {
		this.index = index;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getStationCnt() {
		return stationCnt;
	}
	public void setStationCnt(int stationCnt) {
		this.stationCnt = stationCnt;
	}
	
}
