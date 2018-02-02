package com.spd.common;

/**
 * 暴雨评估结果
 * @author Administrator
 *
 */
public class RainStormEvaluateResult implements Comparable<RainStormEvaluateResult>{
	//年，累计次数，累计评估值，常年次数，常年评估值，次数距平，评估值距平
	//年份
	private int year;
	//累计次数
	private int cnt;
	//常年次数
	private Double yearCnt;
	//次数距平
	private Double anomalyCnt;
	//累计评估值
	private double index;
	//常年评估值
	private double yearIndex;
	//评估值距平
	private double anomalyIndex;
	
	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public int getCnt() {
		return cnt;
	}


	public void setCnt(int cnt) {
		this.cnt = cnt;
	}


	public Double getYearCnt() {
		return yearCnt;
	}


	public void setYearCnt(Double yearCnt) {
		this.yearCnt = yearCnt;
	}


	public Double getAnomalyCnt() {
		return anomalyCnt;
	}


	public void setAnomalyCnt(Double anomalyCnt) {
		this.anomalyCnt = anomalyCnt;
	}


	public double getIndex() {
		return index;
	}


	public void setIndex(double index) {
		this.index = index;
	}


	public double getYearIndex() {
		return yearIndex;
	}


	public void setYearIndex(double yearIndex) {
		this.yearIndex = yearIndex;
	}


	public double getAnomalyIndex() {
		return anomalyIndex;
	}


	public void setAnomalyIndex(double anomalyIndex) {
		this.anomalyIndex = anomalyIndex;
	}


	public int compareTo(RainStormEvaluateResult o) {
		return this.year - o.year;
	}
	
	
}
