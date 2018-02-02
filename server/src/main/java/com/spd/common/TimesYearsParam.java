package com.spd.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 历年同期的参数类
 * @author Administrator
 *
 */
public class TimesYearsParam extends TimesParam {
//
//	//开始时间字符串，格式yyyy-MM-dd
//	private String startTimeStr;
//	//结束时间字符串，格式yyyy-MM-dd
//	private String endTimeStr;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	//给一个默认的年份
	private int year = 2000;
	
//	private Date startDate;
//	
//	private Date endDate;
	//历史比较的开始年
	private int startYear;
	//历史比较的结束年
	private int endYear;
	
	private int startMon;
	
	private int endMon;

	private int startDay;
	
	private int endDay;
	
	public TimesYearsParam(TimesParam timesParam, int startYear, int endYear) {
		int startMon = timesParam.getStartMon();
		int startDay = timesParam.getStartDay();
		int endMon = timesParam.getEndMon();
		int endDay = timesParam.getEndDay();
		this.startMon = startMon;
		this.startDay = startDay;
		this.endMon = endMon;
		this.endDay = endDay;
		this.startYear = startYear;
		this.endYear = endYear;
	}
	
	public TimesYearsParam(int startMon, int startDay, int endMon, int endDay, int startYear, int endYear) {
		this.startMon = startMon;
		this.startDay = startDay;
		this.endMon = endMon;
		this.endDay = endDay;
		this.startYear = startYear;
		this.endYear = endYear;
	}
	
	public void addDays(int days) {
		SimpleDateFormat sdfMM = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDD = new SimpleDateFormat("dd");
		String startStr = year + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
		String endStr = year + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
		try {
			Date startDate = sdf.parse(startStr);
			Date endDate = sdf.parse(endStr);
			long startTime = startDate.getTime();
			long endTime = endDate.getTime();
			startTime += days * CommonConstant.DAYTIMES;
			startDate = new Date(startTime);
			endDate = new Date(endTime);
			startMon = Integer.parseInt(sdfMM.format(startDate));
			endMon = Integer.parseInt(sdfMM.format(endDate));
			startDay = Integer.parseInt(sdfDD.format(startDate));
			endDay = Integer.parseInt(sdfDD.format(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void addEndDays(int days) {
		SimpleDateFormat sdfMM = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDD = new SimpleDateFormat("dd");
		String startStr = year + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
		String endStr = year + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
		try {
			Date startDate = sdf.parse(startStr);
			Date endDate = sdf.parse(endStr);
			long endTime = endDate.getTime();
			endTime += days * CommonConstant.DAYTIMES;
			endDate = new Date(endTime);
			startMon = Integer.parseInt(sdfMM.format(startDate));
			endMon = Integer.parseInt(sdfMM.format(endDate));
			startDay = Integer.parseInt(sdfDD.format(startDate));
			endDay = Integer.parseInt(sdfDD.format(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
//	//历史同期，起始年
//	private int hisStartYear;
//	//历史同期，结束年
//	private int hisEndYear;
//	
//	public int getHisStartYear() {
//		return hisStartYear;
//	}
//
//	public void setHisStartYear(int hisStartYear) {
//		this.hisStartYear = hisStartYear;
//	}
//
//	public int getHisEndYear() {
//		return hisEndYear;
//	}
//
//	public void setHisEndYear(int hisEndYear) {
//		this.hisEndYear = hisEndYear;
//	}

//	public void addDays(int days) {
//		startDate = new Date(startDate.getTime() + days * CommonConstant.DAYTIMES);
//		startTimeStr = sdf.format(startDate);
//		setStartTimeStr(startTimeStr);
//		setHisStartTime(days);
//	}
//	
//	public String getStartTimeStr() {
//		return startTimeStr;
//	}
//	
//	public void setHisStartTime(int days) {
//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
//		String hisStartYearStr = hisStartYear + startTimeStr.substring(4);
//		Date hisStartDate = null;
//		try {
//			hisStartDate = sdf.parse(hisStartYearStr);
//			hisStartDate = new Date(hisStartDate.getTime() + days * CommonConstant.DAYTIMES);
//			startTimeStr = sdf2.format(hisStartDate);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//	
//	public void setStartTimeStr(String startTimeStr) {
//		this.startTimeStr = startTimeStr;
//		String startYearStr = "", startMonStr = "", startDayStr = "";
//		try {
//			startDate = sdf.parse(startTimeStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return;
//		}
//		startYearStr = startTimeStr.substring(0, 4);
//		startMonStr = startTimeStr.substring(5, 7);
//		startDayStr = startTimeStr.substring(8, 10);
//		startYear = Integer.parseInt(startYearStr);
//		startMon = Integer.parseInt(startMonStr);
//		startDay = Integer.parseInt(startDayStr);
//	}
//	public String getEndTimeStr() {
//		return endTimeStr;
//	}
//	
//	public void setEndTimeStr(String endTimeStr) {
//		this.endTimeStr = endTimeStr;
//		String endYearStr = "", endMonStr = "", endDayStr = "";
//		try {
//			endDate = sdf.parse(endTimeStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return;
//		}
//		endYearStr = endTimeStr.substring(0, 4);
//		endMonStr = endTimeStr.substring(5, 7);
//		endDayStr = endTimeStr.substring(8, 10);
//		endYear = Integer.parseInt(endYearStr);
//		endMon = Integer.parseInt(endMonStr);
//		endDay = Integer.parseInt(endDayStr);
//	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public void setStartMon(int startMon) {
		this.startMon = startMon;
	}

	public void setEndMon(int endMon) {
		this.endMon = endMon;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
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
