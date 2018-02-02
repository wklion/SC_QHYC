package com.spd.common;
/**
 * 强降温参数
 * @author Administrator
 *
 */
public class StrongCoolingParam {
	//时间类参数
	private TimesParam timesParam;
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
	//站点号
	private String station_id_Cs;
	//站类型
	private String stationType;
	
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	public String getStation_id_Cs() {
		return station_id_Cs;
	}
	public void setStation_id_Cs(String stationIdCs) {
		station_id_Cs = stationIdCs;
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
	public int[] getWinterMonthes() {
		return winterMonthes;
	}
	public int[] getSpringAutumnMonthes() {
		return springAutumnMonthes;
	}
	public int[] getSummerMonthes() {
		return summerMonthes;
	}
	
}
