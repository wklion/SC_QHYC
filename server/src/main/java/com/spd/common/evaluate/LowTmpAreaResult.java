package com.spd.common.evaluate;
/**
 * 区域低温评估结果类
 * @author Administrator
 *
 */
public class LowTmpAreaResult {
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续天数
	private int persistDays;
	//影响站数
	private int stationCnts;
	//累积气温距平
	private double sumAnomaly;
	//等权集成
	private double result1;
	//不等权集成
	private double result2;
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
	public int getStationCnts() {
		return stationCnts;
	}
	public void setStationCnts(int stationCnts) {
		this.stationCnts = stationCnts;
	}
	public double getSumAnomaly() {
		return sumAnomaly;
	}
	public void setSumAnomaly(double sumAnomaly) {
		this.sumAnomaly = sumAnomaly;
	}
	public double getResult1() {
		return result1;
	}
	public void setResult1(double result1) {
		this.result1 = result1;
	}
	public double getResult2() {
		return result2;
	}
	public void setResult2(double result2) {
		this.result2 = result2;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
