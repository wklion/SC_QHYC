package com.spd.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间段范围的参数类
 * @author Administrator
 *
 */
public class TimesParam {

	//开始时间字符串，格式yyyy-MM-dd
	private String startTimeStr;
	//结束时间字符串，格式yyyy-MM-dd
	private String endTimeStr;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private Date startDate;
	
	private Date endDate;

	private int startYear;
	
	private int endYear;
	
	private int startMon;
	
	private int endMon;
	
	private int startDay;
	
	private int endDay;

	public TimesParam() {
		TimeZone tz = TimeZone.getTimeZone("GMT+08:00");//获取中国北京时区
        TimeZone.setDefault(tz);//设置中国北京时区为默认时区
	}
	
	public String getStartTimeStr() {
		return startTimeStr;
	}
	
	public void addDays(int days) {
		startDate = new Date(startDate.getTime() + days * CommonConstant.DAYTIMES);
		startTimeStr = sdf.format(startDate);
		setStartTimeStr(startTimeStr);
	}
	
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
		String startYearStr = "", startMonStr = "", startDayStr = "";
		try {
			startDate = sdf.parse(startTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		startYearStr = startTimeStr.substring(0, 4);
		startMonStr = startTimeStr.substring(5, 7);
		startDayStr = startTimeStr.substring(8, 10);
		startYear = Integer.parseInt(startYearStr);
		startMon = Integer.parseInt(startMonStr);
		startDay = Integer.parseInt(startDayStr);
	}
	
	public String getEndTimeStr() {
		return endTimeStr;
	}
	
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
		String endYearStr = "", endMonStr = "", endDayStr = "";
		try {
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		endYearStr = endTimeStr.substring(0, 4);
		endMonStr = endTimeStr.substring(5, 7);
		endDayStr = endTimeStr.substring(8, 10);
		endYear = Integer.parseInt(endYearStr);
		endMon = Integer.parseInt(endMonStr);
		endDay = Integer.parseInt(endDayStr);
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public int getStartYear() {
		return startYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public int getStartMon() {
		return startMon;
	}
	public int getEndMon() {
		return endMon;
	}
	public int getStartDay() {
		return startDay;
	}
	public int getEndDay() {
		return endDay;
	}
	
}
