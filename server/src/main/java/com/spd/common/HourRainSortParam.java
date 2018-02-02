package com.spd.common;
/**
 * 小时降水排序参数
 * @author Administrator
 *
 */
public class HourRainSortParam {
	//站号
	private String station_Id_C;
	//返回记录数
	private int limit;
	//降水类型,R1, R3, R6, R12, R24
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
