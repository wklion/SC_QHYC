package com.spd.common;
/**
 * 小时的瞬时风查询结果类
 * @author Administrator
 *
 */
public class WinAvg2MinResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//日期
	private String datetime;
	//时次
	private int hour;
	//02时风向
	private int winDAvg1;
	//02时风向代码
	private String winDCode1;
	//02时风速
	private Double winSAvg1;
	//08时风向
	private int winDAvg2;
	//08时风向代码
	private String winDCode2;
	//08时风速
	private Double winSAvg2;
	//14时风向
	private int winDAvg3;
	//14时风向代码
	private String winDCode3;
	//14时风速
	private Double winSAvg3;
	//20时风向
	private int winDAvg4;
	//20时风向代码
	private String winDCode4;
	//20时风速
	private Double winSAvg4;
	
	public int getWinDAvg1() {
		return winDAvg1;
	}
	public void setWinDAvg1(int winDAvg1) {
		this.winDAvg1 = winDAvg1;
	}
	public String getWinDCode1() {
		return winDCode1;
	}
	public void setWinDCode1(String winDCode1) {
		this.winDCode1 = winDCode1;
	}
	public Double getWinSAvg1() {
		return winSAvg1;
	}
	public void setWinSAvg1(Double winSAvg1) {
		this.winSAvg1 = winSAvg1;
	}
	public int getWinDAvg2() {
		return winDAvg2;
	}
	public void setWinDAvg2(int winDAvg2) {
		this.winDAvg2 = winDAvg2;
	}
	public String getWinDCode2() {
		return winDCode2;
	}
	public void setWinDCode2(String winDCode2) {
		this.winDCode2 = winDCode2;
	}
	public Double getWinSAvg2() {
		return winSAvg2;
	}
	public void setWinSAvg2(Double winSAvg2) {
		this.winSAvg2 = winSAvg2;
	}
	public int getWinDAvg3() {
		return winDAvg3;
	}
	public void setWinDAvg3(int winDAvg3) {
		this.winDAvg3 = winDAvg3;
	}
	public String getWinDCode3() {
		return winDCode3;
	}
	public void setWinDCode3(String winDCode3) {
		this.winDCode3 = winDCode3;
	}
	public Double getWinSAvg3() {
		return winSAvg3;
	}
	public void setWinSAvg3(Double winSAvg3) {
		this.winSAvg3 = winSAvg3;
	}
	public int getWinDAvg4() {
		return winDAvg4;
	}
	public void setWinDAvg4(int winDAvg4) {
		this.winDAvg4 = winDAvg4;
	}
	public String getWinDCode4() {
		return winDCode4;
	}
	public void setWinDCode4(String winDCode4) {
		this.winDCode4 = winDCode4;
	}
	public Double getWinSAvg4() {
		return winSAvg4;
	}
	public void setWinSAvg4(Double winSAvg4) {
		this.winSAvg4 = winSAvg4;
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
}
