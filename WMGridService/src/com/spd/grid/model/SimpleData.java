package com.spd.grid.model;

import java.util.List;

/**
 * @作者:wangkun
 * @日期:2017年4月26日
 * @公司:spd
 * @说明:简单数据
*/
public class SimpleData {
	private String name;//名称
	private List<Double> lsData;//数据
	public SimpleData(String name,List<Double> lsdata){
		this.name=name;
		this.lsData=lsdata;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Double> getLsData() {
		return lsData;
	}
	public void setLsData(List<Double> lsData) {
		this.lsData = lsData;
	}
}
