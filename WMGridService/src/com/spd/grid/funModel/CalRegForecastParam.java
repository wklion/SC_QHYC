package com.spd.grid.funModel;

import java.util.List;

import com.spd.grid.model.Factor;

public class CalRegForecastParam {
	private int month;
	private int flag;//0降水，1气温
	private List<Factor> lsFactor;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public List<Factor> getLsFactor() {
		return lsFactor;
	}
	public void setLsFactor(List<Factor> lsFactor) {
		this.lsFactor = lsFactor;
	}
}
