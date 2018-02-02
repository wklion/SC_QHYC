package com.spd.pojo;

import java.util.List;

public class HuaDong {

	//站号
	private String station_Id_C;
	//值序列
	private List<HuaDongItem> huaDongItemList;
	
	public String getStation_Id_C() {
		return station_Id_C;
	}
	public void setStation_Id_C(String stationIdC) {
		station_Id_C = stationIdC;
	}
	public List<HuaDongItem> getHuaDongItemList() {
		return huaDongItemList;
	}
	public void setHuaDongItemList(List<HuaDongItem> huaDongItemList) {
		this.huaDongItemList = huaDongItemList;
	}
}
