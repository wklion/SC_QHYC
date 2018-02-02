package com.spd.common.evaluate;
/**
 * 秋雨结果
 * @author Administrator
 *
 */
public class AutumnRainsItemResult {
	//年份
	private Integer year;
	//开始日期
	private String startTime;
	//结束时间
	private String endTime;
	//持续时间
	private int persistDays;
	//长度指数
	private Double lengthIndexI;
	//长度等级
	private String lengthLevel;
	//秋雨量
	private Double pre;
	//雨量指数
	private Double preIndex;
	//雨量等级
	private String preLevel;
	//综合强度指数
	private Double intensityIndex;
	//综合等级
	private String intensityLevel;
	
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Double getLengthIndexI() {
		return lengthIndexI;
	}
	public void setLengthIndexI(Double lengthIndexI) {
		this.lengthIndexI = lengthIndexI;
	}
	public String getLengthLevel() {
		return lengthLevel;
	}
	public void setLengthLevel(String lengthLevel) {
		this.lengthLevel = lengthLevel;
	}
	public Double getPre() {
		return pre;
	}
	public void setPre(Double pre) {
		this.pre = pre;
	}
	public Double getPreIndex() {
		return preIndex;
	}
	public void setPreIndex(Double preIndex) {
		this.preIndex = preIndex;
	}
	public String getPreLevel() {
		return preLevel;
	}
	public void setPreLevel(String preLevel) {
		this.preLevel = preLevel;
	}
	public Double getIntensityIndex() {
		return intensityIndex;
	}
	public void setIntensityIndex(Double intensityIndex) {
		this.intensityIndex = intensityIndex;
	}
	public String getIntensityLevel() {
		return intensityLevel;
	}
	public void setIntensityLevel(String intensityLevel) {
		this.intensityLevel = intensityLevel;
	}
}
