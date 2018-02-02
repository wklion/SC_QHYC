package com.spd.common.evaluate;

import java.util.Comparator;

/**
 * MCI过程序列结果
 * @author Administrator
 *
 */
public class MCIStationTimesResult  {

	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续天数
	private Integer days;
	//单站强度
	private Double singleStrength;
	//单站综合强度
	private Double singleSynthStrength;
	//过程累积强度
	private Double sumStrength;
	//强度等级
	private String strengthLevel;
	//标准化数值
	private Double standardValue;
	//位次
	private int rank;
	
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Double getSumStrength() {
		return sumStrength;
	}
	public void setSumStrength(Double sumStrength) {
		this.sumStrength = sumStrength;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
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
	public Double getSingleStrength() {
		return singleStrength;
	}
	public void setSingleStrength(Double singleStrength) {
		this.singleStrength = singleStrength;
	}
	public Double getSingleSynthStrength() {
		return singleSynthStrength;
	}
	public void setSingleSynthStrength(Double singleSynthStrength) {
		this.singleSynthStrength = singleSynthStrength;
	}
	public String getStrengthLevel() {
		return strengthLevel;
	}
	public void setStrengthLevel(String strengthLevel) {
		this.strengthLevel = strengthLevel;
	}
	public Double getStandardValue() {
		return standardValue;
	}
	public void setStandardValue(Double standardValue) {
		this.standardValue = standardValue;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
}
