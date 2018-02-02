package com.spd.sc.pojo;

import com.spd.common.TimesParam;

/**
 * 常规要素查询的参数类
 * @author Administrator
 *
 */
public class ElementsByTimesParam {
	//开始、结束时间
	private TimesParam timesParam;
	//站，可以是单站，可以是多站
	private String station_Id_Cs;
	//统计要素对应的表
	private String tableName;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public String getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String stationIdCs) {
		station_Id_Cs = stationIdCs;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
