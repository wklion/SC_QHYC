package com.spd.common.evaluate;
/**
 * 降雪区域评估结果
 * @author Administrator
 *
 */
public class SnowAreaResult {
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续时间
	private int persistDays;
	//最大影响范围
	private int maxStations;
	//平均积雪深度
	private double avgDepth;
	//最大积雪深度
	private double maxDepth;
	//综合强度
	private double strength;
	//等级
	private String level;
	
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
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getMaxStations() {
		return maxStations;
	}
	public void setMaxStations(int maxStations) {
		this.maxStations = maxStations;
	}
	public double getAvgDepth() {
		return avgDepth;
	}
	public void setAvgDepth(double avgDepth) {
		this.avgDepth = avgDepth;
	}
	public double getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(double maxDepth) {
		this.maxDepth = maxDepth;
	}
	public double getStrength() {
		return strength;
	}
	public void setStrength(double strength) {
		this.strength = strength;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
