package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 区域降雪参数
 * @author Administrator
 *
 */
public class SnowAreaParam {
	//时间参数
	private TimesParam timesParam;
	//持续时间权重
	private double IA;
	//最大影响范围权重
	private double IB;
	//平均积雪深度权重
	private double IC;
	//最大积雪权重
	private double ID;
	//降雪等级划分
	//轻度
	private double level1;
	//中度
	private double level2;
	//重度
	private double level3;
	//特重
	private double level4;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getIA() {
		return IA;
	}
	public void setIA(double iA) {
		IA = iA;
	}
	public double getIB() {
		return IB;
	}
	public void setIB(double iB) {
		IB = iB;
	}
	public double getIC() {
		return IC;
	}
	public void setIC(double iC) {
		IC = iC;
	}
	public double getID() {
		return ID;
	}
	public void setID(double iD) {
		ID = iD;
	}
	public double getLevel1() {
		return level1;
	}
	public void setLevel1(double level1) {
		this.level1 = level1;
	}
	public double getLevel2() {
		return level2;
	}
	public void setLevel2(double level2) {
		this.level2 = level2;
	}
	public double getLevel3() {
		return level3;
	}
	public void setLevel3(double level3) {
		this.level3 = level3;
	}
	public double getLevel4() {
		return level4;
	}
	public void setLevel4(double level4) {
		this.level4 = level4;
	}
	
}
