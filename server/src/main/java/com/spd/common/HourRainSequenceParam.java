package com.spd.common;
/**
 * 小时逐时演变参数类。
 * @author Administrator
 *
 */
public class HourRainSequenceParam {
	//站号
	private String station_Id_C;
	//小时时间参数
	private HourTimesParam hourTimesParam;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public HourTimesParam getHourTimesParam() {
		return hourTimesParam;
	}
	public void setHourTimesParam(HourTimesParam hourTimesParam) {
		this.hourTimesParam = hourTimesParam;
	}
	
}
