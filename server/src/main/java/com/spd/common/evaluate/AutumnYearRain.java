package com.spd.common.evaluate;
/**
 * 年度查询结果
 * @author Administrator
 *
 */
public class AutumnYearRain {
	//日期
	private String datetime;
	//平均雨量
	private Double avgPre;
	//大于0.1站数
	private Integer cnt;
	//是否在多雨期
	private boolean isInRainRange;
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public Double getAvgPre() {
		return avgPre;
	}
	public void setAvgPre(Double avgPre) {
		this.avgPre = avgPre;
	}
	public Integer getCnt() {
		return cnt;
	}
	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}
	public boolean isInRainRange() {
		return isInRainRange;
	}
	public void setInRainRange(boolean isInRainRange) {
		this.isInRainRange = isInRainRange;
	}
	
}
