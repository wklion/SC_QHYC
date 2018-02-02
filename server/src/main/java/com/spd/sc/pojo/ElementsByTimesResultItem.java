package com.spd.sc.pojo;

/**
 * 常规要素查询的结果类
 * @author Administrator
 *
 */
public class ElementsByTimesResultItem {
	//日期
	private String datetime;
	//值
	private Double value;
	
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
	
}
