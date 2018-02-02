package com.spd.common.evaluate;
/**
 * 秋雨区域内逐日统计雨量
 * @author Administrator
 *
 */
public class AutumnAreaRainsResult {

	//日期
	private String datetimeStr;
	//区域雨量
	private Double pres;
	//站数
	private int stationCnt;
	public String getDatetimeStr() {
		return datetimeStr;
	}
	public void setDatetimeStr(String datetimeStr) {
		this.datetimeStr = datetimeStr;
	}
	public Double getPres() {
		return pres;
	}
	public void setPres(Double pres) {
		this.pres = pres;
	}
	public int getStationCnt() {
		return stationCnt;
	}
	public void setStationCnt(int stationCnt) {
		this.stationCnt = stationCnt;
	}
	
}
