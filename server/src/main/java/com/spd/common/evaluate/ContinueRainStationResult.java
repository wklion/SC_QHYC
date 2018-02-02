package com.spd.common.evaluate;
/**
 * 单站连阴雨查询的结果类
 * @author Administrator
 *
 */
public class ContinueRainStationResult {
	//站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	//持续时间
	private int persistDays;
	//有雨日数
	private int rainDays;
	//白天降水量
	private double pre;
	//等权集成值
	private double result1;
	//不等权集成值
	private double result2;
	//级别
	private String level;
	
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String stationName) {
		station_Name = stationName;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
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
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getRainDays() {
		return rainDays;
	}
	public void setRainDays(int rainDays) {
		this.rainDays = rainDays;
	}
	public double getPre() {
		return pre;
	}
	public void setPre(double pre) {
		this.pre = pre;
	}
	public double getResult1() {
		return result1;
	}
	public void setResult1(double result1) {
		this.result1 = result1;
	}
	public double getResult2() {
		return result2;
	}
	public void setResult2(double result2) {
		this.result2 = result2;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
