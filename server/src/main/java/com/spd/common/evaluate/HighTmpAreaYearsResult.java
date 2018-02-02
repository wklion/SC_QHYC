package com.spd.common.evaluate;
/**
 * 历年同期统计暴雨的结果类
 * @author Administrator
 *
 */
public class HighTmpAreaYearsResult {
	//年
	private Integer year;
	//过程次数
	private Integer cnt;
	//常年次数
	private Double yearCnt;
	//综合指数
	private Double YHI;
	//常年指数
	private Double yearYHI;
	//等级
	private String level;
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getCnt() {
		return cnt;
	}
	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}
	public Double getYearCnt() {
		return yearCnt;
	}
	public void setYearCnt(Double yearCnt) {
		this.yearCnt = yearCnt;
	}
	public Double getYHI() {
		return YHI;
	}
	public void setYHI(Double yHI) {
		YHI = yHI;
	}
	public Double getYearYHI() {
		return yearYHI;
	}
	public void setYearYHI(Double yearYHI) {
		this.yearYHI = yearYHI;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
