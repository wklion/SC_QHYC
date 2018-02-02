package com.spd.common.evaluate;
/**
 * 单点暴雨结果
 * @author Administrator
 *
 */
public class RainStormStationResult {
	//单点暴雨总量
	private double preTotal;
	//年发生站数
	private int stationCnt;
	//强度
	private Double strength; 
	//等级
	private String level;
	
	public double getPreTotal() {
		return preTotal;
	}
	public void setPreTotal(double preTotal) {
		this.preTotal = preTotal;
	}
	public int getStationCnt() {
		return stationCnt;
	}
	public void setStationCnt(int stationCnt) {
		this.stationCnt = stationCnt;
	}
	public Double getStrength() {
		return strength;
	}
	public void setStrength(Double strength) {
		this.strength = strength;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
}
