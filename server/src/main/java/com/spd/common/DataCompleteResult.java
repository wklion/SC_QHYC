package com.spd.common;

/**
 * 数据完整度结果类
 * @author Administrator
 *
 */
public class DataCompleteResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//数据开始时间
	private String startTime;
	//数据更新时间
	private String updateTime;
	//应到数
	private int predictCnt;
	//实到数
	private int realCnt;
	//缺测数
	private int missCnt;
	//缺测率
	private double missRate;
	
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
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public int getPredictCnt() {
		return predictCnt;
	}
	public void setPredictCnt(int predictCnt) {
		this.predictCnt = predictCnt;
	}
	public int getRealCnt() {
		return realCnt;
	}
	public void setRealCnt(int realCnt) {
		this.realCnt = realCnt;
	}
	public int getMissCnt() {
		return missCnt;
	}
	public void setMissCnt(int missCnt) {
		this.missCnt = missCnt;
	}
	public double getMissRate() {
		return missRate;
	}
	public void setMissRate(double missRate) {
		this.missRate = missRate;
	}
	
}
