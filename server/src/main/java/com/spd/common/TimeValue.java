package com.spd.common;

import java.util.Date;

/**
 * 定义一个对象，时间和值
 * @author Administrator
 *
 */
public class TimeValue {
	//时间
	private Date date;
	//值
	private double value;
	//时间的字符格式
	private String dateStr;
	
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}
