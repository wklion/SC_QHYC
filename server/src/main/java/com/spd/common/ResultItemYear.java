package com.spd.common;

/**
 * 结果字段、年
 * @author Administrator
 *
 */
public class ResultItemYear {
	//字段组合，例如：m10d01,m10d11...
	private String items;
	//对应的年份
	private int year;
	
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
}
