package com.spd.common.evaluate;

import java.util.Comparator;

/**
 * 暴雨时间段统计结果
 * @author Administrator
 *
 */
public class RainStormAreaTimesResult {
	//类型，不在客户端展示。分 PRE、0808、2020。PRE是气候中心标准，0808、2020是气象台标准。气象台标准只用于16年以后的数据
	private String type;
	//总降水量
	private double totalPre;
	//最大降水量
	private double maxPre;
	//范围
	private int stations;
	//持续时间
	private int persistDays;
	//等权指标
	private double index1;
	//不等权指标
	private double index2;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//暴雨强度
	private String level;
	//暴雨次数
	private int times;
	
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getTotalPre() {
		return totalPre;
	}
	public void setTotalPre(double totalPre) {
		this.totalPre = totalPre;
	}
	public double getMaxPre() {
		return maxPre;
	}
	public void setMaxPre(double maxPre) {
		this.maxPre = maxPre;
	}
	public int getStations() {
		return stations;
	}
	public void setStations(int stations) {
		this.stations = stations;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
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
	
}
