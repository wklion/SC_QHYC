package com.spd.common.evaluate;

/**
 * 时间段范围评估高温结果类（区域）
 * @author Administrator
 *
 */
public class HighTmpRangeAreaResult {
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续时间
	private int persistDays;
	//区域高温指数
	private double RI;
	//高温等级
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
	public double getRI() {
		return RI;
	}
	public void setRI(double rI) {
		RI = rI;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
