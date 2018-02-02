package com.spd.common;
/**
 * MCI的逐次结果
 * @author Administrator
 *
 */
public class MCISequenceResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//地区代码
	private String areaCode;
	//SPIW60
	private Double SPIW60;
	//MI
	private Double MI;
	//SPI90
	private Double SPI90;
	//SPI150
	private Double SPI150;
	//MCI
	private Double MCI;
	//等级
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
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public Double getSPIW60() {
		return SPIW60;
	}
	public void setSPIW60(Double sPIW60) {
		SPIW60 = sPIW60;
	}
	public Double getMI() {
		return MI;
	}
	public void setMI(Double mI) {
		MI = mI;
	}
	public Double getSPI90() {
		return SPI90;
	}
	public void setSPI90(Double sPI90) {
		SPI90 = sPI90;
	}
	public Double getSPI150() {
		return SPI150;
	}
	public void setSPI150(Double sPI150) {
		SPI150 = sPI150;
	}
	public Double getMCI() {
		return MCI;
	}
	public void setMCI(Double mCI) {
		MCI = mCI;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}
