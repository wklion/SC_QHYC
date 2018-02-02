package com.spd.common;

/**
 * 霜冻，合计
 * @author Administrator
 *
 */
public class FrostSequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//开始日期
	private String startDatetime;
	//结束日期
	private String endDatetime;
	//持续天数
	private int persistDays;
	//低温天数
	private int lowTmpDays;
	//极端低温
	private double extLowTmp;
	//程度
	private String level;
	
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStartDatetime() {
		return startDatetime;
	}
	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}
	public String getEndDatetime() {
		return endDatetime;
	}
	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}
	public int getPersistDays() {
		return persistDays;
	}
	public void setPersistDays(int persistDays) {
		this.persistDays = persistDays;
	}
	public int getLowTmpDays() {
		return lowTmpDays;
	}
	public void setLowTmpDays(int lowTmpDays) {
		this.lowTmpDays = lowTmpDays;
	}
	public double getExtLowTmp() {
		return extLowTmp;
	}
	public void setExtLowTmp(double extLowTmp) {
		this.extLowTmp = extLowTmp;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
