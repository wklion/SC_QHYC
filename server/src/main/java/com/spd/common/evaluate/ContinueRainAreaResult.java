package com.spd.common.evaluate;

/**
 *  区域连阴雨结果类
 * @author Administrator
 *
 */
public class ContinueRainAreaResult {
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续时间
	private int persistDays;
	//累积站点数
	private int stationCnt;
	//累计有雨日数
	private int preDays;
	//累积白天雨量
	private double pre;
	//等权集成结果
	private double result1;
	//不等权集成结果
	private double result2;
	//级别
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
	public int getStationCnt() {
		return stationCnt;
	}
	public void setStationCnt(int stationCnt) {
		this.stationCnt = stationCnt;
	}
	
	public int getPreDays() {
		return preDays;
	}
	public void setPreDays(int preDays) {
		this.preDays = preDays;
	}
	public double getPre() {
		return pre;
	}
	public void setPre(double pre) {
		this.pre = pre;
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
