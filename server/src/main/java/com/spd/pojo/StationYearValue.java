package com.spd.pojo;
/**
 * 站号，年值
 * @author Administrator
 *
 */
public class StationYearValue extends StationValue {
	//天数
	private int days;
	//极值日期
	private String extDay;
	
	public String getExtDay() {
		return extDay;
	}
	public void setExtDay(String extDay) {
		this.extDay = extDay;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	
}
