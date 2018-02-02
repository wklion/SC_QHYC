package com.spd.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	public static SimpleDateFormat sdf_yyyy_MM_dd = new SimpleDateFormat("yyyy_MM_dd");
	public static SimpleDateFormat sdf_yyyyMMdd000000 = new SimpleDateFormat("yyyyMMdd000000");
	public static String format(Calendar cal,String strFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		return sdf.format(cal.getTime());
	}
}
