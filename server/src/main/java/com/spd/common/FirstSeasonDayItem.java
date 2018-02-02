package com.spd.common;

/**
 * 季节计算的第一次满足条件的结果类
 * @author Administrator
 *
 */
public class FirstSeasonDayItem {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//原始开始索引
	private int oriStartId;
	//滑动开始索引
	private int huadongStartId;
	//滑动结束索引
	private int huadongEndId;
	//是否满足开始日期到常年开始日期之间一直满足条件
	private boolean isContinue;
	//开始日期
	private String startTime;
	//常年开始日期
	private String hisStartDate;
	
	public String getHisStartDate() {
		return hisStartDate;
	}
	public void setHisStartDate(String hisStartDate) {
		this.hisStartDate = hisStartDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public boolean isContinue() {
		return isContinue;
	}
	public void setContinue(boolean isContinue) {
		this.isContinue = isContinue;
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
	public int getOriStartId() {
		return oriStartId;
	}
	public void setOriStartId(int oriStartId) {
		this.oriStartId = oriStartId;
	}
	public int getHuadongStartId() {
		return huadongStartId;
	}
	public void setHuadongStartId(int huadongStartId) {
		this.huadongStartId = huadongStartId;
	}
	public int getHuadongEndId() {
		return huadongEndId;
	}
	public void setHuadongEndId(int huadongEndId) {
		this.huadongEndId = huadongEndId;
	}
	
}
