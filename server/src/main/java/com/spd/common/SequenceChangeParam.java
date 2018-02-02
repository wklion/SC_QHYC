package com.spd.common;
/**
 * 连续变化参数类
 * @author Administrator
 *
 */
public class SequenceChangeParam {
	//时间
	private TimesParam timesParam;
	//站点
	private String station_Id_C;
	//运算
	private String statisticsType;
	//标准值开始年
	private int standardStartYear;
	//标准值结束年
	private int standardEndYear;
	//气候类型
	private String climTimeType;
	// 要素类型
	private String eleTypes;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public String getStatisticsType() {
		return statisticsType;
	}
	public void setStatisticsType(String statisticsType) {
		this.statisticsType = statisticsType;
	}
	public int getStandardStartYear() {
		return standardStartYear;
	}
	public void setStandardStartYear(int standardStartYear) {
		this.standardStartYear = standardStartYear;
	}
	public int getStandardEndYear() {
		return standardEndYear;
	}
	public void setStandardEndYear(int standardEndYear) {
		this.standardEndYear = standardEndYear;
	}
	public String getClimTimeType() {
		return climTimeType;
	}
	public void setClimTimeType(String climTimeType) {
		this.climTimeType = climTimeType;
	}
	public String getEleTypes() {
		return eleTypes;
	}
	public void setEleTypes(String eleTypes) {
		this.eleTypes = eleTypes;
	}
	
}
