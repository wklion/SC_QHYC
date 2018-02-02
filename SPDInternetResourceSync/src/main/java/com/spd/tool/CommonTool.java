package com.spd.tool;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.spd.cimiss.CIMISSRest;

public class CommonTool {

	private static HashMap<String, Integer> winHashMap = new HashMap<String, Integer>();
	
	private static SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	
	static {
		winHashMap.put("東", 90);
		winHashMap.put("南", 180);
		winHashMap.put("西", 270);
		winHashMap.put("北", 0);
		winHashMap.put("東南", 135);
		winHashMap.put("西南", 225);
		winHashMap.put("西北", 315);
		winHashMap.put("東北", 45);
		
		winHashMap.put("北北東", 23);
		winHashMap.put("東北東", 68);
		winHashMap.put("東南東", 113);
		winHashMap.put("南南東", 135);
		winHashMap.put("南南西", 203);
		winHashMap.put("西南西", 248);
		winHashMap.put("西北西", 293);
		winHashMap.put("北北西", 325);
	}
	
	public static String getValidImgName(String urlAddress, String rename, String timeIndex){
		String fileName = "";
		String newName = "";
		String time = "";
		fileName = urlAddress.substring(urlAddress.lastIndexOf("/") + 1);
		if(fileName.indexOf("?") != -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf("?"));
		}
		//后缀
		String subString = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		
		if(timeIndex != null || !"".equals(timeIndex)) {
			String[] temp = timeIndex.split(",");
			int start = Integer.parseInt(temp[0]);
			int end = Integer.parseInt(temp[1]);
			time = fileName.substring(start, end);
			int length = end - start;
			if(length == 6) {
				time += "01000000";
			} else if(length < 14) {
				for(int i=0; i< 14 -length; i++) {
					time += "0";
				}
			}
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
			time = sdf.format(new Date());
		}
		if(rename != null && !"".equals(rename)) {
			fileName = rename;
		} 
		newName = fileName + time + subString;
		return newName;
	}
	
	/**
	 * 把汉字转换成风向
	 * @param value
	 * @return
	 */
	public static Integer transWinDir(String value) {
		Integer instantWindD =  winHashMap.get(value);
		if(instantWindD == null) {
			return null;
		}
		return instantWindD;
	}
	
	/**
	 * 保留一位小数
	 * @param in
	 * @return
	 */
	public static double roundDouble(double in) {
		double temp = -0.05;
		if(in >= 0) {
			temp = 0.05;
		} else {
			temp = -0.05;
		}
		BigDecimal columnBD = new BigDecimal(in + "");
		BigDecimal columnTM = new BigDecimal(temp + "");
		BigDecimal multiBD = new BigDecimal(10 + "");
		double resultValue = columnBD.add(columnTM).multiply(multiBD).intValue() / 10.0;
		return resultValue;
	}
	/**
	 * 保留两位小数
	 * @param in
	 * @return
	 */
	public static double roundDouble2(double in) {
		double temp = -0.005;
		if(in >= 0) {
			temp = 0.005;
		} else {
			temp = -0.005;
		}
		BigDecimal columnBD = new BigDecimal(in + "");
		BigDecimal columnTM = new BigDecimal(temp + "");
		BigDecimal multiBD = new BigDecimal(100 + "");
		double resultValue = columnBD.add(columnTM).multiply(multiBD).intValue() / 100.0;
		return resultValue;
	}
	
	
	public static String getAllItems() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDateStr = "2000-01-01";
		String endDateStr = "2000-12-31";
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startDateStr);
			endDate = sdf.parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		return items;
	}
	
	/**
	 * 把m08d09,2015年转换成2015-08-09
	 * @param column
	 * @param year
	 * @return
	 */
	public static String createTimeStrByColumn(String column, String year) {
		String result = year + "-" + column.substring(1, 3) + "-" + column.substring(4, 6);
		return result;
	}
	
	/**
	 * 把日期对应的转换为m09d10的形式
	 * @param date
	 * @return
	 */
	public static String createColumnByDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String str = sdf.format(date);
		String result = "m" + str.substring(0, 2) + "d" + str.substring(3, 5);
		return result;
	}
	
	public static String createItemStrByRangeDate(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return createItemStrByRange(startDate, endDate);
	}
	
	public static String getDateStrByItem(String item, int year) {
		String str = year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6);
		return str;
	}
	
	public static boolean isTimeInRange(String startTime, String endTime, String itemTime, String year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null, itemDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
			itemDate = sdf.parse(getDateStrByItemStr(itemTime, year));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(itemDate.getTime() >= startDate.getTime() && itemDate.getTime() <= endDate.getTime()) {
			return true;
		}
		return false;
	}
	
	
	public static String getDateStrByItemStr(String item, String year) {
		String timeStr = year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6);
		return timeStr;
	}
	
	public static String createItemStrByRange(Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		LinkedHashSet<String> columns = new LinkedHashSet<String>();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		StringBuffer result = new StringBuffer();
		if(startTime > endTime) {
			//跨年
			String start = "1999-" + sdfMon.format(startTime) + "-" + sdfDay.format(startTime) + " 00:00:00";
			String end = "2000-" + sdfMon.format(endTime) + "-" + sdfDay.format(endTime) + " 00:00:00";
			try {
				startTime = sdf.parse(start).getTime();
				endTime = sdf.parse(end).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.clear();
		endCalendar.clear();
		startCalendar.setTime(startDate);
		endCalendar.setTime(endDate);
//		startCalendar.setTimeInMillis(startTime);
//		endCalendar.setTimeInMillis(endTime);
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, adds);
//		Date resultDate = calendar.getTime();
		while(!startCalendar.after(endCalendar)) {
			columns.add("m" + sdfMon.format(startCalendar.getTimeInMillis()) + "d" + 
					sdfDay.format(startCalendar.getTimeInMillis()));
			startCalendar.add(Calendar.DATE, 1);
		}
		//解决夏令时问题，夏令时问题集中在1986年到1991年之间
		columns.add("m" + sdfMon.format(endCalendar.getTimeInMillis()) + "d" + 
				sdfDay.format(endCalendar.getTimeInMillis()));
//		for(long time=startTime; time<=endTime; time += 24 * 60 * 60 * 1000) {
//			columns.add("m" + sdfMon.format(time) + "d" + sdfDay.format(time));
//		}
		Iterator it = columns.iterator();
		while(it.hasNext()) {
			result.append(it.next()).append(",");
		}
		return result.toString().substring(0, result.length() - 1);
	}
	
	public static String addDays(String datetime, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null; 
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return sdf.format(calendar.getTime());
	}
	
	public static boolean isInTime(String itemName, int year, Date startDate, Date endDate) {
		String itemTimeStr = year + "-" + itemName.substring(1, 3) + "-" + itemName.substring(4, 6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date currentDate = sdf.parse(itemTimeStr);
			if(currentDate.getTime() >= startDate.getTime() && currentDate.getTime() <= endDate.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 计算两个日期天数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int caleDays(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null; 
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long value = endDate.getTime() - startDate.getTime();
//		int day = ((Long)(value / CommonConstant.DAYTIMES)).intValue();
		//解决夏令时问题
		double tempDay = (value + 0.0) / CommonConstant.DAYTIMES;
		int day = ((Long) (Math.round(tempDay))).intValue();
		return day + 1;
	}
	
	/**
	 * 候加减 暂时只支持adds为1，或-1
	 * @param year
	 * @param month
	 * @param hou
	 * @return
	 */
	public static int[] addHou(int year, int month, int hou, int adds) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int[] result = null;
		// 转换成date, date - 1天， 转换成候
		if(adds < 0) {
			String datetimeStr = chgStartTimeByHou(new int[]{year, month, hou});
			Date date = null;
			try {
				date = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, adds);
			Date resultDate = calendar.getTime();
			result = getYearMonthHou(sdf.format(resultDate));
//			Date resultDate = new Date(date.getTime() + adds * CommonConstant.DAYTIMES);
//			result = getYearMonthHou(sdf.format(resultDate));
		} else {
			String datetimeStr = chgEndTimeByHou(new int[]{year, month, hou});
			Date date = null;
			try {
				date = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, adds);
			Date resultDate = calendar.getTime();
			result = getYearMonthHou(sdf.format(resultDate));
//			Date resultDate = new Date(date.getTime() + adds * CommonConstant.DAYTIMES);
//			result = getYearMonthHou(sdf.format(resultDate));
		}
		return result;
	}
	
	/**
	 * 比较
	 * @param hou1
	 * @param hou2
	 * @return 正数 > ，0 =，负数 <
	 */
	public static int compare(int[] hou1, int[] hou2) {
		int houValue1 = hou1[0] * 72 + hou1[1] * 6 + hou1[2];
		int houValue2 = hou2[0] * 72 + hou2[1] * 6 + hou2[2];
		int value = houValue1 - houValue2;
		return value;
	}
	public static int[] getYearMonthHou(String datetime) {
		int[] result = new int[3];
		result[0] = Integer.parseInt(datetime.substring(0, 4));
		result[1] = Integer.parseInt(datetime.substring(5, 7));
		int day = Integer.parseInt(datetime.substring(8, 10));
		if(day <= 5) {
			result[2] = 1;
		} else if (day > 5 && day <= 10) {
			result[2] = 2;
		} else if (day > 10 && day <= 15) {
			result[2] = 3;
		} else if (day > 15 && day <= 20) {
			result[2] = 4;
		} else if (day > 20 && day <= 25) {
			result[2] = 5;
		} else if (day > 25) {
			result[2] = 6;
		}
		return result;
	}
	
	public static int minusHous(int[] hou1, int[] hou2) {
		//TODO 计算hou1和候2的候间隔，这个应该放到一个单独的Hou类中比较合适
		int result = (hou1[0] * 12 * 6 + hou1[1] * 6 + hou1[2]) -
						(hou2[0] * 12 * 6 + hou2[1] * 6 + hou2[2]) + 1;
		return result;
	}
	
	/**
	 * 判断是不是候的最后一天
	 * @param datetime
	 * @return
	 */
	public static boolean isLastDayInHou(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int day = Integer.parseInt(datetime.substring(8, 10));
		Date preDate = new Date(date.getTime() + CommonConstant.DAYTIMES);
		String preTimeStr = sdf.format(preDate);
		if(preTimeStr.endsWith("01") || day == 5 || day == 10 || day == 15 
				|| day == 20 || day == 25) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是不是候的第一天
	 * @param datetime
	 * @return
	 */
	public static boolean isFirstDayInHou(String datetime) {
		int day = Integer.parseInt(datetime.substring(8, 10));
		if(day  == 1 || day == 6 || day == 11 || day == 16 
				|| day == 21 || day == 26) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据年月候，转换对应的开始时间
	 * @param hous
	 * @return
	 */
	public static String chgStartTimeByHou(int[] hous) {
		String day = "";
		if(hous[2] == 1) {
			day = "01";
		} else if (hous[2] == 2) {
			day = "06";
		} else if (hous[2] == 3) {
			day = "11";
		} else if (hous[2] == 4) {
			day = "16";
		} else if (hous[2] == 5) {
			day = "21";
		} else if (hous[2] == 6) {
			day = "26";
		}
		String monthStr = String.format("%02d", hous[1]);
		String datetime = hous[0] + "-" + monthStr + "-" + day;
		return datetime;
	}
	
	/**
	 * 根据年月候，转换对应的结束时间
	 * @param hous
	 * @return
	 */
	public static String chgEndTimeByHou(int[] hous) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String monthStr = String.format("%02d", hous[1]);
		if(hous[2] == 6) {
			String startTime = chgStartTimeByHou(hous);
			Date date = null;
			try {
				date = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 10 * CommonConstant.DAYTIMES);
			String result = sdf.format(date);
			result = result.substring(0, 8) + "01";
			try {
				date = sdf.parse(result);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			date = new Date(date.getTime() - CommonConstant.DAYTIMES);
			result = sdf.format(date);
			return result;
		} else if(hous[2] == 1) {
			return hous[0] + "-" + monthStr + "-05";
		} else if(hous[2] == 2) {
			return hous[0] + "-" + monthStr + "-10";
		} else if(hous[2] == 3) {
			return hous[0] + "-" + monthStr + "-15";
		} else if(hous[2] == 4) {
			return hous[0] + "-" + monthStr + "-20";
		} else if(hous[2] == 5) {
			return hous[0] + "-" + monthStr + "-25";
		}
		return null;
//		//1 + 1 候，然后计算开始日期，减1
//		int curHou = hous[0] * 72 + hous[1] * 6 + hous[2];
//		int resultHou = curHou + 1;
//		int year = resultHou / 72;
//		int month = (resultHou - year * 72) / 6;
//		int hou = resultHou - year * 72 - month * 6;
//		String preDateTime = chgStartTimeByHou(new int[]{year, month, hou});
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date preDate = null;
//		try {
//			preDate = sdf.parse(preDateTime);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		Long preTime = preDate.getTime();
//		Long curTime = preTime + CommonConstant.DAYTIMES;
//		return sdf.format(new Date(curTime));
	}
	
	public static Date chgStr2Date(String datetime) {
		try {
			Date date = sdfyyyyMMdd.parse(datetime);
			return date;
		} catch (ParseException e) {
			
		}
		return null;
	}
	
	public static String chgDate2Str(Date date) {
		String datetime = sdfyyyyMMdd.format(date);
		return datetime;
	}
	
	/**
	 * 处理降水中的无效值
	 * @param item
	 * @return
	 */
	public static Map<String, Object> disposePreData(Map<String, Object> item) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Iterator it = item.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			if(key.matches("m.*?d.*?")) {
				Double value = (Double) item.get(key);
				if(value != null && value == 999990) {
					value = 0.0;
				} else if(value != null && value == 999999) {
					value = null;
				} 
				resultMap.put(key, value);
			} else {
				resultMap.put(key, item.get(key));
			}
			
		}
		return resultMap;
	}
	
	/**
	 * 比较两个时间大小
	 * @param timeStr1
	 * @param timeStr2
	 * @return time1 < time2 -1
	 * time1 == time2 0
	 * time1 > time2 1
	 */
	public static int compareTimes(String timeStr1, String timeStr2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null, date2 = null;
		try {
			date1 = sdf.parse(timeStr1);
			date2 = sdf.parse(timeStr2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		if(time1 < time2) return -1; 
		if(time1 == time2) return 0; 
		if(time1 > time2) return 1;
		return 0;
	}
	
}
