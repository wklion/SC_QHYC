package com.spd.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 小时时间段范围的参数类
 * @author Administrator
 *
 */
public class HourTimesParam {

	//开始时间字符串，格式yyyy-MM-dd
	private String startTimeStr;
	//结束时间字符串，格式yyyy-MM-dd
	private String endTimeStr;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Date startDate;
	
	private Date endDate;

	private int startYear;
	
	private int endYear;
	
	private int startMon;
	
	private int endMon;
	
	private int startDay;
	
	private int endDay;

	private String type;
	
	private int startHour;

	private int endHour;
	
	private String areaCode;
	
	private String resultType;
	
	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public int getStartHour() {
		return startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addDays(int days) {
		startDate = new Date(startDate.getTime() + days * CommonConstant.DAYTIMES);
		startTimeStr = sdf.format(startDate);
		setStartTimeStr(startTimeStr);
	}
	
	public void addHour(int hours) {
		startDate = new Date(startDate.getTime() + hours * 60 * 60 * 1000);
		startTimeStr = sdf.format(startDate);
		setStartTimeStr(startTimeStr);
	}
	
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
		String startYearStr = "", startMonStr = "", startDayStr = "", startHourStr = "";
		try {
			startDate = sdf.parse(startTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		//2010-01-02 11:00:00
		startYearStr = startTimeStr.substring(0, 4);
		startMonStr = startTimeStr.substring(5, 7);
		startDayStr = startTimeStr.substring(8, 10);
		startHourStr = startTimeStr.substring(11, 13);
		startYear = Integer.parseInt(startYearStr);
		startMon = Integer.parseInt(startMonStr);
		startDay = Integer.parseInt(startDayStr);
		startHour = Integer.parseInt(startHourStr);
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
		String endYearStr = "", endMonStr = "", endDayStr = "", endHourStr = "";
		try {
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		endYearStr = endTimeStr.substring(0, 4);
		endMonStr = endTimeStr.substring(5, 7);
		endDayStr = endTimeStr.substring(8, 10);
		endHourStr = endTimeStr.substring(11, 13);
		endYear = Integer.parseInt(endYearStr);
		endMon = Integer.parseInt(endMonStr);
		endDay = Integer.parseInt(endDayStr);
		endHour = Integer.parseInt(endHourStr);
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
	
	public HourTimesParam copy() {
		HourTimesParam hourTimesParam = new HourTimesParam();
		hourTimesParam.setAreaCode(areaCode);
		hourTimesParam.setStartTimeStr(startTimeStr);
		hourTimesParam.setEndTimeStr(endTimeStr);
		hourTimesParam.setResultType(resultType);
		hourTimesParam.setType(type);
		return hourTimesParam;
	}
}
