package com.spd.common;
/**
 * 日较差时间段参数
 * @author Administrator
 *
 */
public class TmpGapTimesParam {
	//时间
	private TimesParam timesParam;
	//站点类型
	private String stationType;
	//开始年
	private int startYear;
	//结束年
	private int endYear;
	//对比时段
	private TimesParam contrastTimeParam;

	public TimesParam getContrastTimeParam() {
		return contrastTimeParam;
	}
	public void setContrastTimeParam(TimesParam contrastTimeParam) {
		this.contrastTimeParam = contrastTimeParam;
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
	public TimesParam getTimesParam() {
		return timesParam;
	}
	public void setTimesParam(TimesParam timesParam) {
		this.timesParam = timesParam;
	}
	public String getStationType() {
		return stationType;
	}
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	
}
