package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 年度连阴雨查询参数类
 * @author Administrator
 *
 */
public class ContinueRainYearParam {
	//时间
	private TimesParam timesParam;
	//最大单站连阴雨强度
	private double maxStationStrength;
	//最大区域连阴雨强度
	private double maxAreaStrength;
	//最小单站连阴雨强度
	private double minStationStrength;
	//最小区域连阴雨强度
	private double minAreaStrength;
	//强度等级1
	private double strengthIndex1;
	//强度等级2
	private double strengthIndex2;
	//强度等级3
	private double strengthIndex3;
	//强度等级4
	private double strengthIndex4;
	
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getMaxStationStrength() {
		return maxStationStrength;
	}
	public void setMaxStationStrength(double maxStationStrength) {
		this.maxStationStrength = maxStationStrength;
	}
	public double getMaxAreaStrength() {
		return maxAreaStrength;
	}
	public void setMaxAreaStrength(double maxAreaStrength) {
		this.maxAreaStrength = maxAreaStrength;
	}
	public double getMinStationStrength() {
		return minStationStrength;
	}
	public void setMinStationStrength(double minStationStrength) {
		this.minStationStrength = minStationStrength;
	}
	public double getMinAreaStrength() {
		return minAreaStrength;
	}
	public void setMinAreaStrength(double minAreaStrength) {
		this.minAreaStrength = minAreaStrength;
	}
	public double getStrengthIndex1() {
		return strengthIndex1;
	}
	public void setStrengthIndex1(double strengthIndex1) {
		this.strengthIndex1 = strengthIndex1;
	}
	public double getStrengthIndex2() {
		return strengthIndex2;
	}
	public void setStrengthIndex2(double strengthIndex2) {
		this.strengthIndex2 = strengthIndex2;
	}
	public double getStrengthIndex3() {
		return strengthIndex3;
	}
	public void setStrengthIndex3(double strengthIndex3) {
		this.strengthIndex3 = strengthIndex3;
	}
	public double getStrengthIndex4() {
		return strengthIndex4;
	}
	public void setStrengthIndex4(double strengthIndex4) {
		this.strengthIndex4 = strengthIndex4;
	}

}
