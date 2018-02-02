package com.spd.common;
/**
 * 累积距平参数类
 * @author Administrator
 *
 */
public class DatectDataSumAnomalyParam {
	//要素值
	private Double value;
	//年份
	private int year;
	//年代
	private String yearsStr;
	//距平值
	private Double anomaly;
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getYearsStr() {
		return yearsStr;
	}
	public void setYearsStr(String yearsStr) {
		this.yearsStr = yearsStr;
	}
	public Double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(Double anomaly) {
		this.anomaly = anomaly;
	}
	
}
