package com.spd.common;

/**
 * 气温日较差时间段对比结果类
 * @author Administrator
 *
 */
public class TmpGapTimesResult {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//日较差
	private Double tmpGap;
	//对比日较差值
	private Double contrastTmpGap;
	//距平
	private Double anomaly;
	
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
	public Double getTmpGap() {
		return tmpGap;
	}
	public void setTmpGap(Double tmpGap) {
		this.tmpGap = tmpGap;
	}
	public Double getAnomaly() {
		return anomaly;
	}
	public void setAnomaly(Double anomaly) {
		this.anomaly = anomaly;
	}
	public Double getContrastTmpGap() {
		return contrastTmpGap;
	}
	public void setContrastTmpGap(Double contrastTmpGap) {
		this.contrastTmpGap = contrastTmpGap;
	}
	
}
