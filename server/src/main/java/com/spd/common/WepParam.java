package com.spd.common;

import java.util.List;

/**
 * 天气现象参数
 * @author Administrator
 *
 */
public class WepParam {
	//天气现象编码数组
	private String weps;
	//时间段
	private TimesParam timesParam;
	//站号
	private List<String> station_id_Cs;
	
	public List<String> getStation_id_Cs() {
		return station_id_Cs;
	}
	public void setStation_id_Cs(List<String> stationIdCs) {
		station_id_Cs = stationIdCs;
	}
	public String getWeps() {
		return weps;
	}
	public void setWeps(String weps) {
		this.weps = weps;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	
}
