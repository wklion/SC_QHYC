package com.spd.pojo;
/**
 * 土壤湿度数据
 * @author Administrator
 *
 */
public class AgmeSoilItem {
	//站号
	private String station_Id_C;
	//土壤深度
	private Double soil_Depth_BelS;
	//土壤体积含水量
	private Double SVWC;
	//土壤体积含水量
	private Double SRHU;
	//土壤体积含水量
	private Double SWWC;
	//土壤体积含水量
	private Double SVMS;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public Double getSoil_Depth_BelS() {
		return soil_Depth_BelS;
	}
	public void setSoil_Depth_BelS(Double soilDepthBelS) {
		soil_Depth_BelS = soilDepthBelS;
	}
	public Double getSVWC() {
		return SVWC;
	}
	public void setSVWC(Double sVWC) {
		SVWC = sVWC;
	}
	public Double getSRHU() {
		return SRHU;
	}
	public void setSRHU(Double sRHU) {
		SRHU = sRHU;
	}
	public Double getSWWC() {
		return SWWC;
	}
	public void setSWWC(Double sWWC) {
		SWWC = sWWC;
	}
	public Double getSVMS() {
		return SVMS;
	}
	public void setSVMS(Double sVMS) {
		SVMS = sVMS;
	}
	
	
}
