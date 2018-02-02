package com.spd.common.evaluate;

import java.util.Comparator;


/**
 * 低温年度评估结果类
 * @author Administrator
 *
 */
public class LowTmpYearResult implements Comparator<LowTmpYearResult> {
	//年份
	private int year;
	//过程次数
	private int cnt;
	//常年次数
	private double yearsCnt;
//	//等权集成
//	private double result1;
	//综合指数
	private double result;
	//常年指数
	private double yearsResult;
	//等级
	private String level;
	
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public double getYearsCnt() {
		return yearsCnt;
	}
	public void setYearsCnt(double yearsCnt) {
		this.yearsCnt = yearsCnt;
	}
	public double getYearsResult() {
		return yearsResult;
	}
	public void setYearsResult(double yearsResult) {
		this.yearsResult = yearsResult;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int compare(LowTmpYearResult o1, LowTmpYearResult o2) {
		return o1.getYear() - o2.getYear();
	}
	
}
