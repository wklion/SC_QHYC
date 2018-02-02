package com.spd.common;
/**
 * 小时的云量查询参数类
 * @author Administrator
 *
 */
public class CloCovParam {
	//时间参数
	private TimesParam timesParam;
	//站类型
	private String stationType;
	
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
