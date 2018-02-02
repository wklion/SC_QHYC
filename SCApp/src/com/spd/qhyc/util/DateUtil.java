package com.spd.qhyc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * @作者:wangkun
	 * @日期:2017年9月19日
	 * @修改日期:2017年9月19日
	 * @参数:strFormart-格式;cal-日期
	 * @返回:
	 * @说明:日期格式化
	 */
	public static String format(String strFormart,Calendar cal){
		SimpleDateFormat sdf = new SimpleDateFormat(strFormart);
		return sdf.format(cal.getTime());
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年9月19日
	 * @修改日期:2017年9月19日
	 * @参数:strFormart-格式;strDateTime-日期
	 * @返回:
	 * @说明:解析
	 */
	public static Calendar parse(String strFormart,String strDateTime){
		SimpleDateFormat sdf = new SimpleDateFormat(strFormart);
		Date date = null;
		try {
			date = sdf.parse(strDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
