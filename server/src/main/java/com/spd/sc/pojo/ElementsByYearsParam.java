package com.spd.sc.pojo;

import com.spd.common.TimesYearsParam;

/**
 * 历年常规要素查询的参数类
 * @author Administrator
 *
 */
public class ElementsByYearsParam {
	//开始、结束时间
	private TimesYearsParam timesYearsParam;
	//站，可以是单站，可以是多站
	private String station_Id_Cs;
	//统计要素对应的表
	private String tableName;
	
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
	public TimesYearsParam getTimesYearsParam() {
		return timesYearsParam;
	}
	public void setTimesYearsParam(TimesYearsParam timesYearsParam) {
		this.timesYearsParam = timesYearsParam;
	}
	
}
