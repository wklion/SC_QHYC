package com.spd.common;

import com.spd.pojo.StationValue;

/**
 * 历年同期统计结果类（按年代计算结果）
 * @author Administrator
 *
 */
public class TenYearsSameCaleResult extends StationValue {
	//年代字符表示
	private String yearsStr;
	//多年均值
	private double avgValue;
	//距平
	private double anomaly;
	//距平率
	private double anomalyRate;
	
	public String getYearsStr() {
		return yearsStr;
	}
	public void setYearsStr(String yearsStr) {
		this.yearsStr = yearsStr;
	}
	public double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(double avgValue) {
		this.avgValue = avgValue;
	}
	public double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(double anomaly) {
		this.anomaly = anomaly;
	}
	public double getAnomalyRate() {
		return anomalyRate;
	}
	public void setAnomalyRate(double anomalyRate) {
		this.anomalyRate = anomalyRate;
	}
	
}
