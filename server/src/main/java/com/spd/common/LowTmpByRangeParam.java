package com.spd.common;

/**
 * 灾害低温统计的参数类
 * @author Administrator
 *
 */
public class LowTmpByRangeParam {
	
	private TimesRangeParam timesRangeParam = new TimesRangeParam();
	//一般低温连续几候气温和历年同期
	private int level1SequenceSeason;
	//一般低温连续低于历年同期的气温
	private double level1SequenceTmp;
	//一般低温排除的月份
	private int[] level1ExceptMonthes;
	//严重低温连续几候气温和历年同期
	private int level2SequenceSeason;
	//严重低温连续低于历年同期的气温数
	private double level2SequenceTmp;
	//严重低温排除的月份数
	private int[] level2ExceptMonthes;
	//过滤站点
	private String station_Id_Cs;
	//开始年
	private int startYear;
	//结束年
	private int endYear;
	//常年开始年
	private int constatStartYear = 1981;
	//常年结束年
	private int constatEndYear = 2010;
	
	public int getConstatStartYear() {
		return constatStartYear;
	}

	public void setConstatStartYear(int constatStartYear) {
		this.constatStartYear = constatStartYear;
	}

	public int getConstatEndYear() {
		return constatEndYear;
	}

	public void setConstatEndYear(int constatEndYear) {
		this.constatEndYear = constatEndYear;
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

	public String getStation_Id_Cs() {
		return station_Id_Cs;
	}

	public void setStation_Id_Cs(String stationIdCs) {
		station_Id_Cs = stationIdCs;
	}

	public LowTmpByRangeParam(String startTimeStr, String endTimeStr) {
		timesRangeParam.setStartTimeStr(startTimeStr);
		timesRangeParam.setEndTimeStr(endTimeStr);
	}

	public int getLevel1SequenceSeason() {
		return level1SequenceSeason;
	}

	public void setLevel1SequenceSeason(int level1SequenceSeason) {
		this.level1SequenceSeason = level1SequenceSeason;
	}

	public double getLevel1SequenceTmp() {
		return level1SequenceTmp;
	}

	public void setLevel1SequenceTmp(double level1SequenceTmp) {
		this.level1SequenceTmp = level1SequenceTmp;
	}

	public int[] getLevel1ExceptMonthes() {
		return level1ExceptMonthes;
	}

	public void setLevel1ExceptMonthes(int[] level1ExceptMonthes) {
		this.level1ExceptMonthes = level1ExceptMonthes;
	}

	public int getLevel2SequenceSeason() {
		return level2SequenceSeason;
	}

	public void setLevel2SequenceSeason(int level2SequenceSeason) {
		this.level2SequenceSeason = level2SequenceSeason;
	}

	public double getLevel2SequenceTmp() {
		return level2SequenceTmp;
	}

	public void setLevel2SequenceTmp(double level2SequenceTmp) {
		this.level2SequenceTmp = level2SequenceTmp;
	}

	public int[] getLevel2ExceptMonthes() {
		return level2ExceptMonthes;
	}

	public void setLevel2ExceptMonthes(int[] level2ExceptMonthes) {
		this.level2ExceptMonthes = level2ExceptMonthes;
	}

	public TimesRangeParam getTimesRangeParam() {
		return timesRangeParam;
	}

	public void setTimesRangeParam(TimesRangeParam timesRangeParam) {
		this.timesRangeParam = timesRangeParam;
	}
	
}
