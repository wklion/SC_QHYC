package com.spd.common;

import java.util.List;

/**
 * 低温统计逐候结果。
 * @author Administrator
 *
 */
public class LowTmpResultHous {
	// 站号
	private String station_Id_C;
	//站名
	private String station_Name;
	//地区
	private String area;
	//时间序列
	private List<HouSequence> list;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
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
	public List<HouSequence> getList() {
		return list;
	}
	public void setList(List<HouSequence> list) {
		this.list = list;
	}
	
	/**
	 * 候序列
	 * @author Administrator
	 *
	 */
	public class HouSequence {
		//时间
		private String time;
		//值
		private double value;
		//距平
		private double anomaly;
		
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public double getAnomaly() {
			return anomaly;
		}
		public void setAnomaly(double anomaly) {
			this.anomaly = anomaly;
		}
		
	}
}

