package com.spd.sc.pojo;


/**
 * 历年要素查询的结果类
 * @author Administrator
 *
 */
public class ElementsByYearsResultItem implements Comparable<ElementsByYearsResultItem> {
	//日期
	private int year;
	//值
	private Double value;
	
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
	public int compareTo(ElementsByYearsResultItem o) {
		return  year - o.getYear();
	}
	
}
