package com.spd.common.evaluate;

import com.spd.common.TimesParam;

/**
 * 暴雨评估，时间段查询参数
 * @author Administrator
 *
 */
public class RainStormAreaTimesRangeParam implements Cloneable {
	//时间
	private TimesParam timesParam;
	//最大总降水量
	private double maxPre;
	//最大单站降水量
	private double maxSignalPre;
	//最大持续时间
	private int maxPersistDays;
	//最大站数
	private int maxStationCnt;
	//最小总降水量
	private double minPre;
	//最小单站降水量
	private double minSignalPre;
	//最小持续时间
	private int minPersistDays;
	//最小站数
	private int minStationCnt;
	//类型，分别有PRE，0808,2002
	private String type;
	//暴雨总量权重
	private double weight1;
	//范围权重
	private double weight2;
	//日降水量极值权重
	private double weight3;
	//持续时间权重
	private double weight4;
	//暴雨等级1
	private double level1;
	//暴雨等级2
	private double level2;
	//暴雨等级3
	private double level3;
	//暴雨等级4
	private double level4;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//常年开始年份
	private int perennialStartYear;
	//常年结束年份
	private int perennialEndYear;
	
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	public int getPerennialStartYear() {
		return perennialStartYear;
	}
	public void setPerennialStartYear(int perennialStartYear) {
		this.perennialStartYear = perennialStartYear;
	}
	public int getPerennialEndYear() {
		return perennialEndYear;
	}
	public void setPerennialEndYear(int perennialEndYear) {
		this.perennialEndYear = perennialEndYear;
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
	public double getWeight1() {
		return weight1;
	}
	public void setWeight1(double weight1) {
		this.weight1 = weight1;
	}
	public double getWeight2() {
		return weight2;
	}
	public void setWeight2(double weight2) {
		this.weight2 = weight2;
	}
	public double getWeight3() {
		return weight3;
	}
	public void setWeight3(double weight3) {
		this.weight3 = weight3;
	}
	public double getWeight4() {
		return weight4;
	}
	public void setWeight4(double weight4) {
		this.weight4 = weight4;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public double getMaxPre() {
		return maxPre;
	}
	public void setMaxPre(double maxPre) {
		this.maxPre = maxPre;
	}
	public double getMaxSignalPre() {
		return maxSignalPre;
	}
	public void setMaxSignalPre(double maxSignalPre) {
		this.maxSignalPre = maxSignalPre;
	}
	public int getMaxPersistDays() {
		return maxPersistDays;
	}
	public void setMaxPersistDays(int maxPersistDays) {
		this.maxPersistDays = maxPersistDays;
	}
	public int getMaxStationCnt() {
		return maxStationCnt;
	}
	public void setMaxStationCnt(int maxStationCnt) {
		this.maxStationCnt = maxStationCnt;
	}
	public double getMinPre() {
		return minPre;
	}
	public void setMinPre(double minPre) {
		this.minPre = minPre;
	}
	public int getMinPersistDays() {
		return minPersistDays;
	}
	public void setMinPersistDays(int minPersistDays) {
		this.minPersistDays = minPersistDays;
	}
	public int getMinStationCnt() {
		return minStationCnt;
	}
	public void setMinStationCnt(int minStationCnt) {
		this.minStationCnt = minStationCnt;
	}
	public double getMinSignalPre() {
		return minSignalPre;
	}
	public void setMinSignalPre(double minSignalPre) {
		this.minSignalPre = minSignalPre;
	}
	
	@Override  
    public Object clone() throws CloneNotSupportedException  
    {  
        return super.clone();  
    }  
}
