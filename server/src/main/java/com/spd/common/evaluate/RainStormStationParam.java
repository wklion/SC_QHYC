package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 单点暴雨参数
 * @author Administrator
 *
 */
public class RainStormStationParam {
	//时间参数
	private TimesParam timesParam;
	//最大单点暴雨总量
	private double maxStationPreTotal;
	//最小单点暴雨总量
	private double minStationPreTotal;
	//最大单点站点总和
	private int maxStationCntTotal;
	//最小单点站点总和
	private int minStationCntTotal;
	//单点暴雨强度等级1
	private double level1;
	//单点暴雨强度等级2
	private double level2;
	//单点暴雨强度等级3
	private double level3;
	//单点暴雨强度等级4
	private double level4;
	
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
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getMaxStationPreTotal() {
		return maxStationPreTotal;
	}
	public void setMaxStationPreTotal(double maxStationPreTotal) {
		this.maxStationPreTotal = maxStationPreTotal;
	}
	public double getMinStationPreTotal() {
		return minStationPreTotal;
	}
	public void setMinStationPreTotal(double minStationPreTotal) {
		this.minStationPreTotal = minStationPreTotal;
	}
	public int getMaxStationCntTotal() {
		return maxStationCntTotal;
	}
	public void setMaxStationCntTotal(int maxStationCntTotal) {
		this.maxStationCntTotal = maxStationCntTotal;
	}
	public int getMinStationCntTotal() {
		return minStationCntTotal;
	}
	public void setMinStationCntTotal(int minStationCntTotal) {
		this.minStationCntTotal = minStationCntTotal;
	}
	
}
