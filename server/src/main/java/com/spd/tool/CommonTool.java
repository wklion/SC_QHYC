package com.spd.tool;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.FilterTypes;
import com.spd.common.RankParam;
import com.spd.common.ResultItemYear;
import com.spd.common.StatisticsTypes;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.pojo.StationYearValue;

/**
 * 通用的工具类
 * @author Administrator
 *
 */
public class CommonTool {
	
	private static final SimpleDateFormat mmdd = new SimpleDateFormat("MMdd");

	public static synchronized String formatDate(Date date) {
		return mmdd.format(date);
	}
	
	/**
	 * 根据开始结束时间，构造需要查询的字段
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String createItemStrByRange(Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		Set<String> columns = new LinkedHashSet<String>();
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
		for(long time=startTime; time<=endTime; time += 24 * 60 * 60 * 1000) {
			columns.add("m" + sdfMon.format(time) + "d" + sdfDay.format(time));
		}
		Iterator it = columns.iterator();
		while(it.hasNext()) {
			result.append(it.next()).append(",");
		}
		return result.toString().substring(0, result.length() - 1);
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
	 * 根据月份，构造对应的items
	 * @param monthes
	 * @return
	 */
	public static String createItemsByMonthes(int[] monthes) {
		//月份对应的天数，按照闰年来定
		HashMap<Integer, Integer> monthDaysMap = new HashMap<Integer, Integer>();
		monthDaysMap.put(1, 31);
		monthDaysMap.put(2, 29);
		monthDaysMap.put(3, 31);
		monthDaysMap.put(4, 30);
		monthDaysMap.put(5, 31);
		monthDaysMap.put(6, 30);
		monthDaysMap.put(7, 31);
		monthDaysMap.put(8, 31);
		monthDaysMap.put(9, 30);
		monthDaysMap.put(10, 31);
		monthDaysMap.put(11, 30);
		monthDaysMap.put(12, 31);
		String items = "";
		for(int i = 0; i < monthes.length; i++) {
			int days = monthDaysMap.get(monthes[i]);
			for(int j = 1; j <= days; j++) {
				items += "m" + String.format("%02d", monthes[i]) + "d" + String.format("%02d", j) + ",";
			}
		}
		items = items.substring(0, items.length() - 1);
		return items;
	}
	
	/**
	 * 多年历史同期的构造查询字段
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public static String createItemStrByTimes(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		LinkedHashSet<String> columns = new LinkedHashSet<String>();
		StringBuffer result = new StringBuffer();
		String start = startYear + "-" + String.format("%02d", startMonth) + "-" + String.format("%02d", startDay) + " 00:00:00";
		String end = endYear + "-" + String.format("%02d", endMonth) + "-" + String.format("%02d", endDay) + " 00:00:00";
		long startTime = 0L, endTime = 0L;
		try {
			startTime = sdf.parse(start).getTime();
			endTime = sdf.parse(end).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for(long time = startTime; time <= endTime; time += 24 * 60 * 60 * 1000) {
			columns.add("m" + sdfMon.format(time) + "d" + sdfDay.format(time));
		}
		Iterator it = columns.iterator();
		while(it.hasNext()) {
			result.append(it.next()).append(",");
		}
		return result.toString().substring(0, result.length() - 1);
	}
	
	public static String createAllItemStrByTimes(TimesParam timesParam) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = timesParam.getStartDate();
		Date endDate = timesParam.getEndDate();
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String[] timeStr = sdf.format(new Date(i)).split("-");
			String item = "m" + timeStr[1] + "d" + timeStr[2];
			result += item;
			if(i != endDate.getTime()) {
				result += ",";
			}
		}
		return result;
	}
	
	/**
	 * 按年份进行区分
	 * @param timesParam
	 * @return
	 */
	public static LinkedHashMap<Integer, String> createItemStrByTimes(TimesParam timesParam) {
		LinkedHashMap<Integer, String> yearItemsMap = new LinkedHashMap<Integer, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = timesParam.getStartDate();
		Date endDate = timesParam.getEndDate();
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String[] timeStr = sdf.format(new Date(i)).split("-");
			String item = "m" + timeStr[1] + "d" + timeStr[2];
			Integer year = Integer.parseInt(timeStr[0]);
			String temp = yearItemsMap.get(year);
			if(temp == null) {
				yearItemsMap.put(year, item);
			} else {
				yearItemsMap.put(year, temp + "," + item);
			}
		}
		return yearItemsMap;
	}
	
	
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
	
	public static double roundDouble3(double in) {
		double temp = -0.0005;
		if(in >= 0) {
			temp = 0.0005;
		} else {
			temp = -0.0005;
		}
		BigDecimal columnBD = new BigDecimal(in + "");
		BigDecimal columnTM = new BigDecimal(temp + "");
		BigDecimal multiBD = new BigDecimal(1000 + "");
		double resultValue = columnBD.add(columnTM).multiply(multiBD).intValue() / 1000.0;
		return resultValue;
	}
	
	/**
	 * 计算一个时间距离当年的第一天的距离天数
	 * @param dateStr
	 * @return
	 * @throws ParseException 
	 */
	public static int calcDaysInYear(String dateStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		Date date = sdf.parse(dateStr);
		String startStr = sdfYear.format(date) + "-01-01";
		long end = date.getTime();
		long start = sdfYear.parse(startStr).getTime();
		//不然会漏算一天
		int days = (int) ((end - start) / (24 * 60 * 60 * 1000)) + 1;
		return days;
	}
	
	/**
	 * 根据距离1月1号的天数，计算在该年的月，日
	 * @param days
	 * @param year
	 * @return
	 */
	public static String calcDateStr(long days, int year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyddd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
		Date date = null;
		try {
			date = sdf.parse(year + String.format("%03d", days));
			String result = sdf2.format(date);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	/**
	 * 计算季节的早晚
	 * @param anomaly
	 * @return
	 */
	public static String calcTimes(int anomaly) {
		if(anomaly < -15) return "特早";
		if(anomaly >= -15 && anomaly < -5) return "偏早";
		if(anomaly >= -15 && anomaly < -5) return "偏早";
		if(anomaly >= -5 && anomaly <= 5) return "正常";
		if(anomaly > 5 && anomaly <= 15) return "偏晚";
		if(anomaly > 15) return "特晚";
		return "";
	}
	
	/**
	 * 处理缺测数据
	 * @return 返回 true 表示该数据不符合要求，应该舍弃，为 false 表示该数据可以继续使用。
	 */
	public static boolean missingObservData(Map map, double missingRatio, int itemLength, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		int validCnt = 0;
		if(missingRatio == 0) { // 不处理缺测
			return false;
		}
		Set set = map.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			if(key.matches("m\\d{2}d\\d{2}")) {
				//具体值
				Double value = null;
				Object objValue = map.get(key);
				if("BigDecimal".equals(columnType) && objValue != null) {
					value = ((BigDecimal)objValue).doubleValue();
				} else {
					value = (Double) map.get(key);
				}
				value = Eigenvalue.dispose(value);
				if(value != null) {
					validCnt++;
				}
//				if(value > CommonConstant.MININVALID && value < CommonConstant.MAXINVALID && value != null) {
//					//表示缺测
//					validCnt ++;
//				} 
			}
		}
		if(validCnt / (itemLength + 0.0) <= missingRatio) {
			return true;
		}
		return false;
	}
	
	/**
	 * 过滤值
	 * @param rankParam
	 * @param value
	 * @return false表示不过滤， true表示该值被过滤掉
	 */
	public static boolean filter(String filterType, double max, double min, double  contrast, Double value){
		boolean flag = false;
		value = Eigenvalue.dispose(value);
		if(value == null) {
			flag = true;
			return flag;
		}
//		if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID) {
//			flag = true;
//		}
		if(filterType != null && !"".equals(filterType)) {
			// 进行值的过滤
			FilterTypes filterTypeEnum = FilterTypes.getFilterTypeName(filterType);
			switch(filterTypeEnum) {
			case GET: // >=
				if(value < contrast) {
					flag = true;
				}
				break;
			case GT: // >
				if(value <= contrast) {
					flag = true;
				}
				break;
			case LT: // <
				if(value >= contrast) {
					flag = true;
				}
				break;
			case LET: // <=
				if(value > contrast) {
					flag = true;
				}
				break;
			case BETWEEN: // between
				if(value < min || value > max) {
					flag = true;
				}
				break;
			default:
				break;
			}
		} 
		return flag;
	}
	
	/**
	 * 按年， 站 来进行划分
	 * @param rankParam
	 * @param rankResult
	 * @param year
	 * @return
	 */
	public static void statistics(String statistics, int year, StationYearValue stationYearValue, double mValue, String day) {
		day = day.substring(1, 3) + "-" + day.substring(4, 6);
		stationYearValue.setYear(year);
		stationYearValue.setDays(stationYearValue.getDays() + 1);
		Double value = stationYearValue.getValue();
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(statistics);
		switch(statisticsTypes) {
		case SUM:
		case AVG:
			if(value == null) {
				stationYearValue.setValue(mValue);
			} else {
				stationYearValue.setValue(value + mValue); 
			}
			break;
		case MAX:
			if(value == null) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue > value ) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue == value) {
				stationYearValue.setExtDay(day + "," + stationYearValue.getExtDay());
			}
			break;
		case MIN:
			if(value == null) {
				stationYearValue.setValue(mValue); 
				stationYearValue.setExtDay(day);
			} else if(mValue < value) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue == value) {
				stationYearValue.setExtDay(day + "," + stationYearValue.getExtDay());
			}
			break;
		case DAYS:
			if(value == null) {
				stationYearValue.setValue(1.0);
			} else {
				stationYearValue.setValue(value + 1);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 对结果进行处理，输入数据中，求平均，求和等都是一个和值
	 * @param stationYearValueMap
	 */
	public static void disposeResult(String statistics, Map<String, StationYearValue> stationYearValueMap) {
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(statistics);
		Set<String> stationYearValueMapSet = stationYearValueMap.keySet();
		Iterator<String>  stationYearValueMapIt = stationYearValueMapSet.iterator();
		while(stationYearValueMapIt.hasNext()) {
			String key = stationYearValueMapIt.next();
			stationYearValueMap.get(key);
			StationYearValue stationYearValue = stationYearValueMap.get(key);
			Double value = stationYearValue.getValue();
			if(value == null) {
				continue;
			}
			int days = stationYearValue.getDays();
			switch(statisticsTypes) {
			case SUM:
				break;
			case AVG:
				stationYearValue.setValue(value / days); 
				break;
			case MAX:
				break;
			case MIN:
				break;
			case DAYS:
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 判断是否超过了一年的情况，比如2016-09-01 到2016-09-10
	 * @param timesRangeParam
	 * @return
	 */
	public static boolean isMoreThan1Year(TimesRangeParam timesRangeParam) {
		//简单判断
		Date startDate = timesRangeParam.getStartDate();
		Date endDate = timesRangeParam.getEndDate();
		long start = startDate.getTime();
		long end = endDate.getTime();
		if(end - start >= 365 * CommonConstant.DAYTIMES) return true;
		return false;
	}
	
	/**
	 * 判断是否跨年
	 * @param startMon
	 * @param startDay
	 * @param endMon
	 * @param endDay
	 * @return
	 */
	public static boolean isOverYear(int startMon, int startDay, int endMon, int endDay) {
		int year = 2000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startStr = year + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
		String endStr = year + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
		long start = 0L, end = 0L;
		try {
			start = sdf.parse(startStr).getTime();
			end = sdf.parse(endStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(start > end) {
			return true;
		}
		return false;
	}
	
	public static boolean isOverYear(int startYear, int endYear, int startMon, int startDay, int endMon, int endDay) {
		if(startYear != endYear) return true;
		int year = 2000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startStr = year + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
		String endStr = year + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
		long start = 0L, end = 0L;
		try {
			start = sdf.parse(startStr).getTime();
			end = sdf.parse(endStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(start > end) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isOverYear(TimesParam timesParam) {
		int startYear = timesParam.getStartYear();
		int endYear = timesParam.getEndYear();
		if(startYear < endYear) return true;
		return false;
	}
	/**
	 * 判断当前的日期算作那一年
	 * @param startMon
	 * @param startDay
	 * @param endMon
	 * @param endDay
	 * @return true表示第二年，false表示第一年
	 */
	public static boolean isCurTimeOverYear(int year, String curMon, String curDay, int startMon, int endMon, int startDay, int endDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startStr = year + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
		String endStr = year + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
		String curStr = year + "-" + curMon + "-" + curDay;
		Date startDate = null, endDate = null, curDate = null;
		try {
			startDate = sdf.parse(startStr);
			endDate = sdf.parse(endStr);
			curDate = sdf.parse(curStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		if(curDate.getTime() > startDate.getTime()) {
			return false;
		}
		if(curDate.getTime() < endDate.getTime()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前的日期算作那一年，简单判断,为了速度
	 * @param startMon
	 * @param startDay
	 * @param endMon
	 * @param endDay
	 * @return true表示第一年，false表示第二年
	 */
	public static boolean isCurTimeOverYear(int curMon, int curDay, int startMon, int endMon, int startDay, int endDay) {
		// >= end || <= start false
		int end = endMon * 100 + endDay;
		int start = startMon * 100 + startDay;
		int cur = curMon * 100 + curDay;
		if(cur < start) {
			return true;
		}
		if(cur > end) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断当前时间是否在给定的时间范围内
	 * @param curMon
	 * @param curDay
	 * @param startMon
	 * @param startDay
	 * @param endMon
	 * @param endDay
	 * @return true 表示是在当前范围, false表示不在范围
	 */
	public static boolean isCurTimeInRanges(int curMon, int curDay, int startMon, int startDay, int endMon, int endDay) {
		int start = startMon * 100 + startDay;
		int end = endMon * 100 + endDay;
		int cur = curMon * 100 + curDay;
		if(start > end) {
			//跨年
			if(cur >= start || cur <= end) {
				return true;
			}
			return false;
		} else {
			if(cur >= start && cur <= end) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 判断 currentDate是否在startDate, endDate所包含的月日范围内
	 * @param startDate
	 * @param endDate
	 * @param currentDate
	 * @return < 返回 -1, = 返回 0, > 返回 1
	 */
	public static int currentTimeInRange(Date startDate, Date endDate, Date currentDate) {
		int start = Integer.parseInt(formatDate(startDate));
		int end = Integer.parseInt(formatDate(endDate));
		int current = Integer.parseInt(formatDate(currentDate));
		if(current < start) return -1;
		if(current >= start && current <= end) return 0;
		if(current > end) return 1;
		return -1;
	}
	
	/**
	 * 判断是否是闰年，
	 * @param year
	 * @return 闰年：true，非闰年：false
	 */
	public static boolean isLeapYear(int year) {
		if((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 处理候候的开始时间
	 * @param date
	 * @return
	 */
	public static  String disposeHouStartTimes(Date date) {
		Date resultDate = date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat yyyySDF = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(yyyySDF.format(date));
		SimpleDateFormat mmSDF = new SimpleDateFormat("MM");
		int month = Integer.parseInt(mmSDF.format(date));
		SimpleDateFormat ddSDF = new SimpleDateFormat("dd");
		int day = Integer.parseInt(ddSDF.format(date));
		if(day > 26) {
			//下个月1号为开始时间
			String dayStr = "01";
			String monStr = "";
			String yearStr = "";
			if(month == 12) {
				monStr = "01";
				yearStr = (year + 1) + ""; 
			} else {
				monStr = String.format("%03d", month + 1);
				yearStr = year + "";
			}
			try {
				resultDate = sdf.parse(yearStr + "-" + monStr + "-" + dayStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(day % 5 == 0){
			resultDate = new Date(date.getTime() +  CommonConstant.DAYTIMES);
		} else if(day % 5 != 1) {
			//往后推算，直到日期被5整除
			int temp = 6 - day % 5; // 和一候之间相差的天数。
			resultDate = new Date(date.getTime() - temp * CommonConstant.DAYTIMES);
		}
		return sdf.format(resultDate);
	}
	
	/**
	 * 处理候的结束时间
	 * @param date
	 * @return
	 */
	public static String disposeHouEndTimes(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat ddSDF = new SimpleDateFormat("dd");
		int day = Integer.parseInt(ddSDF.format(date));
		if(day < 30 && day % 5 == 0) {
			return sdf.format(date);
		} else {
			//月底最后一天
			Date tempDate = new Date(date.getTime() + CommonConstant.DAYTIMES);
			String tempDay = ddSDF.format(tempDate);
			if(tempDay.equals("01")) {
				return sdf.format(date);
			} else {
				return sdf.format(new Date(date.getTime() + (day % 5 * CommonConstant.DAYTIMES)));
			}
		}
	}
	
	/**
	 * 根据时间，判断出是几月，几候
	 * @param date
	 * @return
	 */
	public static int[] getHouTimesByDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str = sdf.format(date);
		int year = Integer.parseInt(str.substring(0, 4));
		int month = Integer.parseInt(str.substring(4, 6));
		int day = Integer.parseInt(str.substring(6, 8));
		int hou = 0;
		if(day <= 5) {
			hou = 1;
		} else if( day <= 10) {
			hou = 2;
		} else if (day <= 15) {
			hou = 3;
		} else if (day <= 20) {
			hou = 4;
		} else if (day <= 25) {
			hou = 5;
		} else {
			hou = 6;
		}
		return new int[]{year, month, hou};
	}
	
	/**
	 * 根据年，月，候，判断出对应的候的开始，结束时间
	 * @param date
	 * @return
	 */
	public static Date[] getDateRangeByHou(int year, int month, int hou) {
		String strYear = year + "";
		String strMonth = String.format("%02d", month);
		int startDay = 0, endDay = 0;
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month, 1);
//		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);//Java月份才0开始算
		int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("dd");
//		String strDay = sdf.format(calendar.getTime());
		if(hou == 1) {
			startDay = 1;
			endDay = 5;
		} else if(hou == 2) {
			startDay = 6;
			endDay = 10;
		} else if(hou == 3) {
			startDay = 11;
			endDay = 15;
		} else if(hou == 4) {
			startDay = 16;
			endDay = 20;
		} else if (hou == 5) {
			startDay = 21;
			endDay = 25;
		} else if (hou == 6) {
			startDay = 26;
			endDay = dateOfMonth;
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		String startStr = strYear + "" + strMonth + "" + String.format("%02d", startDay);
		String endStr = strYear + "" + strMonth + "" + String.format("%02d", endDay);
		Date startDate = null, endDate = null;
		try {
			startDate = sdf2.parse(startStr);
			endDate = sdf2.parse(endStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
 		return new Date[]{startDate, endDate};
	}
	
	/**
	 * 根据开始结束时间，计算年月候，返回结果List<year_mon_hou>
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getYearMonHou(Date startDate, Date endDate) {
		List<String> list = new ArrayList<String>();
		Set<String> resultSet = new LinkedHashSet<String>();
		//1-5: 1, 6-10:2, 11-15:3, 16-20:4, 21-25:5, 26-:5
		long start = startDate.getTime();
		long end = endDate.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(long i = start; i <= end; i+= CommonConstant.DAYTIMES) {
			Date tempDate = new Date(i);
			String timeStr = sdf.format(tempDate);
			String[] temp = timeStr.split("-");
			String year = temp[0];
			String mon = temp[1];
			String day = temp[2];
			int dayInt = Integer.parseInt(day);
			int hou = 0;
			if(dayInt >= 1 && dayInt <= 5) {
				hou = 1;
			} else if(dayInt >= 6 && dayInt <= 10) {
				hou = 2;
			} else if(dayInt >= 11 && dayInt <= 15) {
				hou = 3;
			} else if(dayInt >= 16 && dayInt <= 20) {
				hou = 4;
			} else if(dayInt >= 21 && dayInt <= 25) {
				hou = 5;
			} else if(dayInt >= 26) {
				hou = 6;
			}
			resultSet.add(year + "_" + mon + "_" + String.format("%02d", hou));
		}
		Iterator it = resultSet.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			list.add(key);
		}
		return list;
	}
	
	private static int chgyyymmdd2yyyyseason(int yyyymmddInt) {
		int mmddInt = yyyymmddInt % 10000;
		int year = yyyymmddInt / 10000;
		int resultInt = 0;
		if(mmddInt >= 1201) {
			//冬
			resultInt = (year + 1) * 10 + 1;
		}
		else if(mmddInt < 301) {
			//冬 
//			startInt = (startYear + 1) * 10 + 1;
			resultInt = year * 10 + 1;
		} else if (mmddInt >= 301 && mmddInt < 601) {
			//春
			resultInt = year * 10 + 2;
		} else if (mmddInt >= 601 && mmddInt < 901) {
			//夏
			resultInt = year * 10 + 3;
		} else if (mmddInt > 901 && mmddInt < 1201) {
			//秋
			resultInt = year * 10 + 4;
		}
		return resultInt;
	}
	
	/**
	 * 根据开始Date，结束Date计算所包含的季
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getSeasonByDates(Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String startStr = sdf.format(startDate);
		String endStr = sdf.format(endDate);
		int start = Integer.parseInt(startStr);
		int end = Integer.parseInt(endStr);
		int startSeason = chgyyymmdd2yyyyseason(start);
		int endSeason = chgyyymmdd2yyyyseason(end);
		List<String> resultList = new ArrayList<String>();
		for(int i = startSeason; i <= endSeason; i++) {
			int tempYear = i / 10;
			int tempSeason = i % 10;
			String season = "";
			if(tempSeason == 1) {
				season = "冬季";
			} else if(tempSeason == 2) {
				season = "春季";
			} else if(tempSeason == 3) {
				season = "夏季";
			} else if(tempSeason == 4) {
				season = "秋季";
			}
			String result = tempYear + "年" + season;
			resultList.add(result);
			if(tempSeason == 4) {
				i = (tempYear + 1) * 10;
			}
		}
		
		return resultList;
	}
	
	/**
	 * 根据季节和时间，来计算需要保留的时间起始和截止
	 * @param startDate
	 * @param endDate
	 * @param season
	 * @return
	 */
	public static Date[] getSeasonByDates(Date startDate, Date endDate, String season) {
		Date startResultDate = null, endResultDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int year = Integer.parseInt(season.substring(0, 4));
		String seasonStr = season.substring(5);
		String startTimeStr = "", endTimeStr = "";
		if(seasonStr.equals("冬季")) {
			startTimeStr = (year - 1) + "1201";
			endTimeStr = year + "0301";
		} else if(seasonStr.equals("春季")) {
			startTimeStr = year + "0301";
			endTimeStr = year + "0601";
		} else if(seasonStr.equals("夏季")) {
			startTimeStr = year + "0601";
			endTimeStr = year + "0901";
		} else if(seasonStr.equals("秋季")) {
			startTimeStr = year + "0901";
			endTimeStr = year + "1201";
		}
		long startDateTime = startDate.getTime();
		long endDateTime = endDate.getTime();
		long startTempTime = 0, endTempTime = 0;
		try {
			startTempTime = sdf.parse(startTimeStr).getTime();
			endTempTime = sdf.parse(endTimeStr).getTime();
			//减一天
			endTempTime = endTempTime - CommonConstant.DAYTIMES;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(startTempTime > startDateTime) {
			try {
				startResultDate = sdf.parse(startTimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			startResultDate = startDate;
		}
		
		if(endTempTime < endDateTime) {
			endResultDate = new Date(endTempTime);
		} else {
			endResultDate = endDate;
		}
		return new Date[]{startResultDate, endResultDate};
	}
	/**
	 * 判断两候是否连续
	 * @param hou1 年-月-候
	 * @param hou2
	 * @returnt
	 */
	public static boolean isHouSuccession(String houStr1, String houStr2) {
		int year1 = 0, month1 = 0, hou1 = 0;
		int year2 = 0, month2 = 0, hou2 = 0;
		String[] arrs1 = houStr1.split("-");
		String[] arrs2 = houStr2.split("-");
		year1 = Integer.parseInt(arrs1[0]);
		month1 = Integer.parseInt(arrs1[1]);
		hou1 = Integer.parseInt(arrs1[2]);
		year2 = Integer.parseInt(arrs2[0]);
		month2 = Integer.parseInt(arrs2[1]);
		hou2 = Integer.parseInt(arrs2[2]);
		// 1. 年 月相同，候差1
		if(year1 == year2 && month1 == month2 && (hou2 - hou1 == 1)) {
			return true;
		}
		//2. 年相同，月差1
		if(year1 == year2 && month1 == month2 - 1 && hou1 == 6 && hou2 == 1) {
			return true;
		}
		//3.年差1
		if(year2 - year1 == 1 && month1 == 12 && month2 == 1 && hou1 == 6 && hou2 == 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * 计算候对应的开始时间
	 * @param houStr
	 * @return
	 */
	public static String chgStartHouStr2Time(String houStr) {
		String[] arrs1 = houStr.split("-");
		int year1 = Integer.parseInt(arrs1[0]);
		int month1 = Integer.parseInt(arrs1[1]);
		int hou1 = Integer.parseInt(arrs1[2]);
		String str = "";
		if(hou1 == 1) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "01";
		} else if(hou1 == 2) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "06";
		} else if(hou1 == 3) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "11";
		} else if(hou1 == 4) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "16";
		} else if(hou1 == 5) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "21";
		} else if(hou1 == 6) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "26";
		}
		return str;
	}
	
	/**
	 * 计算候对应的开始时间
	 * @param houStr
	 * @return
	 */
	public static String chgEndHouStr2Time(String houStr) {
		String[] arrs1 = houStr.split("-");
		int year1 = Integer.parseInt(arrs1[0]);
		int month1 = Integer.parseInt(arrs1[1]);
		int hou1 = Integer.parseInt(arrs1[2]);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		String dateStr = year1 + String.format("%02d", month1);
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {
			date = sdf2.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		int maxDay = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
		String str = "";
		if(hou1 == 1) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "05";
		} else if(hou1 == 2) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "10";
		} else if(hou1 == 3) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "15";
		} else if(hou1 == 4) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "20";
		} else if(hou1 == 5) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + "25";
		} else if(hou1 == 6) {
			str = year1 + "-" + String.format("%02d", month1) + "-" + maxDay;
		}
		return str;
	}
	
	/**
	 * 根据日期，计算出对应的年，月，旬。
	 * @param date
	 * @return
	 */
	public static String getYearMonTenDaysFromDate(Date date) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str = sdf.format(date);
		String year = str.substring(0, 4);
		String month = str.substring(4, 6);
		String day = str.substring(6, 8);
		int dayInt = Integer.parseInt(day);
		if(dayInt <= 10) {
			result = year + month + "01";
		} else if (dayInt <= 20) {
			result = year + month + "02";
		} else {
			result = year + month + "03";
		}
		return result;
	}
	
	/**
	 * 根据年月旬，计算出对应的开始，结束日期
	 * @param str
	 * @return
	 */
	public static Date[] getDateFromTenDaysStr(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String year = str.substring(0, 4);
		String month = str.substring(4, 6);
		String tendays = str.substring(6, 8);
		int tendaysInt = Integer.parseInt(tendays);
		Date startDate = null, endDate = null;
		String startTimeStr = "", endTimeStr = "";
		if(tendaysInt == 1) {
			startTimeStr = year + month + "01";
			endTimeStr = year + month + "10";
		} else if(tendaysInt == 2) {
			startTimeStr = year + month + "11";
			endTimeStr = year + month + "20";
		} else if(tendaysInt == 3) {
			startTimeStr = year + month + "21";
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);//Java月份才0开始算
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
			endTimeStr = year + month + dateOfMonth;
		}
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date[]{startDate, endDate};
	}
	
	/**
	 * 把m08d09,2015年转换成2015-08-09
	 * @param column
	 * @param year
	 * @return
	 */
	public static String createTimeStrByColumn(String column, int year) {
		String result = year + "-" + column.substring(1, 3) + "-" + column.substring(4, 6);
		return result;
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
		int day = ((Long)(value / CommonConstant.DAYTIMES)).intValue();
		return day + 1;
	}
	
	/**
	 * 获取CIMISS数据
	 * @param timeStr
	 * @return
	 */
	public static String getCIMISSData(String timeStr, String elements, String staLevels) {
		timeStr = timeStr.replaceAll("-", "");
		timeStr += "000000";
		String url = "http://10.230.89.55/cimiss-web/api?userId=BECQ_QHZX_byy&pwd=qhzxbyy&interfaceId=getSurfEleInRegionByTimeRange";
		url += "&dataCode=SURF_CHN_MUL_DAY&elements=";
		url += elements;
		url += "&timeRange=[" + timeStr + "," + timeStr + "]&orderby=Datetime:ASC&staLevels=" + staLevels + "&adminCodes=500000&dataFormat=json";
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	
	/**
	 * 比较开始、结束时间大小
	 * @param startTime
	 * @param endTime
	 * @return 如果开始时间 < 结束时间 返回 -1, 如果开始时间 = 结束时间 返回0， 如果开始时间 > 结束时间，返回 1
	 */
	public static int compareDates(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long start = startDate.getTime();
		long end = endDate.getTime();
		if(start < end) return -1;
		if(start == end) return 0;
		if(start > end) return 1;
		return -1;
	}
	
	/**
	 * 比较开始、结束时间大小
	 * @param startTime
	 * @param endTime
	 * @return 如果开始时间 < 结束时间 返回 -1, 如果开始时间 = 结束时间 返回0， 如果开始时间 > 结束时间，返回 1
	 */
	public static int compareDateHours(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long start = startDate.getTime();
		long end = endDate.getTime();
		if(start < end) return -1;
		if(start == end) return 0;
		if(start > end) return 1;
		return -1;
	}
	
	/**
	 * 计算日期加减小时
	 * @param times
	 * @param hours
	 * @return
	 */
	public static String addHours(String times, int hours) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(times);
			long result = date.getTime() + hours * CommonConstant.HOURTIMES;
			Date resultDate = new Date(result);
			return sdf.format(resultDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
