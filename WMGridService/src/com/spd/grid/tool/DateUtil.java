package com.spd.grid.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * @作者:杠上花
     * @日期:2017年9月19日
     * @修改日期:2017年9月19日
     * @参数:
     * @返回:
     * @说明:格式化
     */
	public static String format(String strFormat,Calendar cal){
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		String strDateTime = sdf.format(cal.getTime());
		return strDateTime;
	}
	/**
     * @作者:杠上花
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
