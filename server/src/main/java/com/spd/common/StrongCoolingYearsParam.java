package com.spd.common;

/**
 * 历年同期强降温参数
 * @author Administrator
 *
 */
public class StrongCoolingYearsParam {
	//时间类参数
	private TimesParam timesParam;
	//开始年份
	private int startYear;
	//结束年份
	private int endYear;
	//冬季月份
	private int[] winterMonthes = new int[]{12, 1, 2};
	//春、秋季月份
	private int[] springAutumnMonthes = new int[]{3, 4, 10, 11};
	//夏季月份
	private int[] summerMonthes = new int[]{5, 6, 7, 8, 9};
	//冬季强降温度数
	private double level1WinterTmp = 6;
	//春，秋季强降温度数
	private double level1springAutumnTmp = 8;
	//夏季强降温度数
	private double level1SummerTmp = 8;
	//冬季特强降温度数
	private double level2WinterTmp = 8;
	//春，秋季特强降温度数
	private double level2springAutumnTmp = 10;
	//夏季特强降温度数
	private double level2SummerTmp = 10;
	//夏季强降温是否参与计算
	private boolean level1SummerFlag = false;
	//夏季特强降温是否参与计算
	private boolean level2SummerFlag = false;
	//常年开始年
	private int perennialStartYear;
	//常年结束年
	private int perennialEndYear;
	//站点
	private String station_Id_Cs;
	//站点类型
	private String stationType;
	
	public String getStation_Id_Cs() {
		return station_Id_Cs;
	}
	public void setStation_Id_Cs(String stationIdCs) {
		station_Id_Cs = stationIdCs;
	}
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
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
	public boolean isLevel1SummerFlag() {
		return level1SummerFlag;
	}
	public void setLevel1SummerFlag(boolean level1SummerFlag) {
		this.level1SummerFlag = level1SummerFlag;
	}
	public boolean isLevel2SummerFlag() {
		return level2SummerFlag;
	}
	public void setLevel2SummerFlag(boolean level2SummerFlag) {
		this.level2SummerFlag = level2SummerFlag;
	}
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
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
	public int[] getWinterMonthes() {
		return winterMonthes;
	}
	public void setWinterMonthes(int[] winterMonthes) {
		this.winterMonthes = winterMonthes;
	}
	public int[] getSpringAutumnMonthes() {
		return springAutumnMonthes;
	}
	public void setSpringAutumnMonthes(int[] springAutumnMonthes) {
		this.springAutumnMonthes = springAutumnMonthes;
	}
	public int[] getSummerMonthes() {
		return summerMonthes;
	}
	public void setSummerMonthes(int[] summerMonthes) {
		this.summerMonthes = summerMonthes;
	}
	public double getLevel1WinterTmp() {
		return level1WinterTmp;
	}
	public void setLevel1WinterTmp(double level1WinterTmp) {
		this.level1WinterTmp = level1WinterTmp;
	}
	public double getLevel1springAutumnTmp() {
		return level1springAutumnTmp;
	}
	public void setLevel1springAutumnTmp(double level1springAutumnTmp) {
		this.level1springAutumnTmp = level1springAutumnTmp;
	}
	public double getLevel1SummerTmp() {
		return level1SummerTmp;
	}
	public void setLevel1SummerTmp(double level1SummerTmp) {
		this.level1SummerTmp = level1SummerTmp;
	}
	public double getLevel2WinterTmp() {
		return level2WinterTmp;
	}
	public void setLevel2WinterTmp(double level2WinterTmp) {
		this.level2WinterTmp = level2WinterTmp;
	}
	public double getLevel2springAutumnTmp() {
		return level2springAutumnTmp;
	}
	public void setLevel2springAutumnTmp(double level2springAutumnTmp) {
		this.level2springAutumnTmp = level2springAutumnTmp;
	}
	public double getLevel2SummerTmp() {
		return level2SummerTmp;
	}
	public void setLevel2SummerTmp(double level2SummerTmp) {
		this.level2SummerTmp = level2SummerTmp;
	}
	
}
