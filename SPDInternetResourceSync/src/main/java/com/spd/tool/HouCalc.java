package com.spd.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 根据月份，把对应的候的字段返回
 * @author Administrator
 *
 */
public class HouCalc {

	public static List<String> getHouListByMonth(int year, int month, int index) {
		// 1:1~5; 2:6~10; 3:11~15; 4:16:20, 5:21~25; 6:26~月底
		List<String> resultList = new ArrayList<String>();
		String monthStr = String.format("%02d", month);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int maxDays = 0;
		try {
			Date date = sdf.parse(year + monthStr + "01");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			maxDays = getMaxDaysByDate(calendar);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		switch(index) {
			case 1:
				resultList.add("m" + monthStr + "d01");
				resultList.add("m" + monthStr + "d02");
				resultList.add("m" + monthStr + "d03");
				resultList.add("m" + monthStr + "d04");
				resultList.add("m" + monthStr + "d05");
				break;
			case 2:
				resultList.add("m" + monthStr + "d06");
				resultList.add("m" + monthStr + "d07");
				resultList.add("m" + monthStr + "d08");
				resultList.add("m" + monthStr + "d09");
				resultList.add("m" + monthStr + "d10");
				break;
			case 3:
				resultList.add("m" + monthStr + "d11");
				resultList.add("m" + monthStr + "d12");
				resultList.add("m" + monthStr + "d13");
				resultList.add("m" + monthStr + "d14");
				resultList.add("m" + monthStr + "d15");
				break;
			case 4:
				resultList.add("m" + monthStr + "d16");
				resultList.add("m" + monthStr + "d17");
				resultList.add("m" + monthStr + "d18");
				resultList.add("m" + monthStr + "d19");
				resultList.add("m" + monthStr + "d20");
				break;
			case 5:
				resultList.add("m" + monthStr + "d21");
				resultList.add("m" + monthStr + "d22");
				resultList.add("m" + monthStr + "d23");
				resultList.add("m" + monthStr + "d24");
				resultList.add("m" + monthStr + "d25");
				break;
			case 6:
				for(int i = 26; i <= maxDays; i++) {
					resultList.add("m" + monthStr + "d" + i);
				}
				break;
			default:
				break;
		}
		return resultList;
	}
	
	/**
	 * 根据天数，判断所在候
	 * @param day
	 * @return
	 */
	public static int getHouIndexByDay(int day) {
		if(day >= 1 && day <= 5) {
			return 1;
		}
		if(day >= 6 && day <= 10) {
			return 2;
		}
		if(day >= 11 && day <= 15) {
			return 3;
		}
		if(day >= 16 && day <= 20) {
			return 4;
		}
		if(day >= 21 && day <= 25) {
			return 5;
		}
		if(day >= 26) {
			return 6;
		}
		return -1;
	}
	
	private static int getMaxDaysByDate(Calendar calendar) {
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}
}
