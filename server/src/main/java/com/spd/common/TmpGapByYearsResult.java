package com.spd.common;

/**
 * 气温年较差历年统计结果
 * @author Administrator
 *
 */
public class TmpGapByYearsResult {

	//年份
	private int year;
	//要素值
	private Double value;
	//多年均值
	private Double avgValue;
	//距平
	private Double anomaly;
	//距平率
	private Double anomalyRate;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(Double avgValue) {
		this.avgValue = avgValue;
	}
	public Double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(Double anomaly) {
		this.anomaly = anomaly;
	}
	public Double getAnomalyRate() {
		return anomalyRate;
	}
	public void setAnomalyRate(Double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}
	
}
