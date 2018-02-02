package com.spd.common;
/**
 * 初日统计参数类
 * @author Administrator
 *
 */
public class FirstDayParam {
	//参与比较的年份
	private int year;
	//表名
	private String tableName;
	//比较的值
	private double value;
	//开始月
	private int startMon;
	//开始日
	private int startDay;
	//结束月
	private int endMon;
	//结束日
	private int endDay;
	//常年开始年
	private int constatStartYear = 1981;
	//常年结束年
	private int constatEndYear = 2010;
	
	public int getConstatStartYear() {
		return constatStartYear;
	}
	public void setConstatStartYear(int constatStartYear) {
		this.constatStartYear = constatStartYear;
	}
	public int getConstatEndYear() {
		return constatEndYear;
	}
	public void setConstatEndYear(int constatEndYear) {
		this.constatEndYear = constatEndYear;
	}
	public int getStartMon() {
		return startMon;
	}
	public void setStartMon(int startMon) {
		this.startMon = startMon;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getEndMon() {
		return endMon;
	}
	public void setEndMon(int endMon) {
		this.endMon = endMon;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
