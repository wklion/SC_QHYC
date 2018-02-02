package com.spd.common;

import java.util.Date;

/**
 * 连续变化结果类
 * @author Administrator
 *
 */
public class SequenceChangeResult {
	
	//日期
	private String datetime;
	//值
	private Double value;
	//多年均值
	private Double yearsValue;
	//距平
	private Double anomaly;
	//距平率
	private Double anomalyRate;
	//下面4个字段只在地区选择全部站的时候才展示
	//最大值
	private Double maxValue;
	//最大值站
	private String maxStation_Name;
	//最小值
	private Double minValue;
	//最小值站
	private String minStation_Name;
	//下面两个字段不展示
	//开始时间
	private Date startDate;
	//结束时间
	private Date endDate;
	
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
	public String getMaxStation_Name() {
		return maxStation_Name;
	}
	public void setMaxStation_Name(String maxStationName) {
		maxStation_Name = maxStationName;
	}
	public Double getMinValue() {
		return minValue;
	}
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getYearsValue() {
		return yearsValue;
	}
	public void setYearsValue(Double yearsValue) {
		this.yearsValue = yearsValue;
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
	public String getMinStation_Name() {
		return minStation_Name;
	}
	public void setMinStation_Name(String minStationName) {
		minStation_Name = minStationName;
	}
	
}
