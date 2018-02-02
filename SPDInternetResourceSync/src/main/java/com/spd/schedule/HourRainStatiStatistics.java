package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.spd.hourrain.HourRainCalc;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 小时降水统计
 * @author Administrator
 *
 */
public class HourRainStatiStatistics {

	/**
	 * 累积统计
	 * @param datetime
	 */
	public void accumulate(String startTime, String endTime) {
		HourRainCalc hourRainAccumulate = new HourRainCalc();
		hourRainAccumulate.accumulate(startTime, endTime);
	}
	
	/**
	 * 极值统计
	 * @param datetime
	 */
	public void ext(String startTime, String endTime) {
		HourRainCalc hourRainAccumulate = new HourRainCalc();
		hourRainAccumulate.ext(startTime, endTime);
	}
	
	public static void main2(String[] args) {
		PropertiesUtil.loadSysCofing();
		HourRainStatiStatistics hourRainStatiStatistics = new HourRainStatiStatistics();
		List<String> dataList = new ArrayList<String>();
		dataList.add("1991-01-01 00:00:00,1991-01-14 23:00:00");
		dataList.add("1991-01-15 00:00:00,1991-01-31 23:00:00");
		dataList.add("1991-02-01 00:00:00,1991-02-14 23:00:00");
		dataList.add("1991-02-15 00:00:00,1991-02-28 23:00:00");
		dataList.add("1991-03-01 00:00:00,1991-03-14 23:00:00");
		dataList.add("1991-03-15 00:00:00,1991-03-31 23:00:00");
		dataList.add("1991-04-01 00:00:00,1991-04-14 23:00:00");
		dataList.add("1991-04-15 00:00:00,1991-04-30 23:00:00");
		dataList.add("1991-05-01 01:00:00,1991-05-14 23:00:00");
		dataList.add("1991-05-15 00:00:00,1991-05-31 23:00:00");
		dataList.add("1991-06-01 01:00:00,1991-06-14 23:00:00");
		dataList.add("1991-06-15 00:00:00,1991-06-30 23:00:00");
		dataList.add("1991-07-01 01:00:00,1991-07-14 23:00:00");
		dataList.add("1991-07-15 00:00:00,1991-07-31 23:00:00");
		dataList.add("1991-08-01 01:00:00,1991-08-14 23:00:00");
		dataList.add("1991-08-15 00:00:00,1991-08-31 23:00:00");
		dataList.add("1991-09-01 01:00:00,1991-09-14 23:00:00");
		dataList.add("1991-09-15 00:00:00,1991-09-30 23:00:00");
		dataList.add("1991-10-01 00:00:00,1991-10-14 23:00:00");
		dataList.add("1991-10-15 00:00:00,1991-10-31 23:00:00");
		dataList.add("1991-11-01 00:00:00,1991-11-14 23:00:00");
		dataList.add("1991-11-15 00:00:00,1991-11-30 23:00:00");
		dataList.add("1991-12-01 00:00:00,1991-12-14 23:00:00");
		dataList.add("1991-12-15 00:00:00,1991-12-31 23:00:00");
		
		for(int i = 0; i < dataList.size(); i++) {
			String str = dataList.get(i);
			System.out.println(str);
			hourRainStatiStatistics.ext(str.split(",")[0], str.split(",")[1]);
		}
		
	}
	
	public void sync(String timeStr) {
		SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat daySDF = new SimpleDateFormat("dd");
		SimpleDateFormat monthSDF = new SimpleDateFormat("yyyy-MM");
		Date date = null;
		try {
			date = sdf.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		int day = Integer.parseInt(daySDF.format(date));
		Date itemEndDate = null, itemStartDate = null;
		String itemStartStr = null, itemEndStr = null;
		if(day == 1) {
			itemEndDate = new Date(date.getTime() - CommonConstant.HOURTIMES);
			//上个月的16号
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(date);
			endCalendar.add(Calendar.MONTH, -1);
			String temp = monthSDF.format(endCalendar.getTime());
			itemStartStr = temp + "-15 00:00:00";
		} else if(day == 15) {
			itemStartDate = new Date(date.getTime() - 14 * CommonConstant.DAYTIMES);
			itemEndDate = new Date(date.getTime() - CommonConstant.HOURTIMES);
			itemStartStr = sdfAll.format(itemStartDate);
		} else {
			return;
		}
		itemEndStr = sdfAll.format(itemEndDate);
		//极值统计
		ext(itemStartStr, itemEndStr);
		//求和统计
		accumulate(itemStartStr, itemEndStr);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		HourRainStatiStatistics hourRainStatiStatistics = new HourRainStatiStatistics();
		//时间为每月1号00:00:00, 14号23:00:00 
		//15号00:00:00，月底23:00:00
		//判断当前时间是否为1号，15号，对应的找到开始时间、结束时间
//		String startTime = "1991-01-01", endTime = "2017-06-16";
		String startTime = "1992-01-01", endTime = "2017-06-16";
		SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat daySDF = new SimpleDateFormat("dd");
		SimpleDateFormat monthSDF = new SimpleDateFormat("yyyy-MM");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long start = startDate.getTime();
		long end = endDate.getTime();
		for(long time = start; time <= end; time += CommonConstant.DAYTIMES) {
			Date date = new Date(time);
			int day = Integer.parseInt(daySDF.format(date));
			Date itemEndDate = null, itemStartDate = null;
			String itemStartStr = null, itemEndStr = null;
			if(day == 1) {
				itemEndDate = new Date(date.getTime() - CommonConstant.HOURTIMES);
				//上个月的16号
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(date);
				endCalendar.add(Calendar.MONTH, -1);
				String temp = monthSDF.format(endCalendar.getTime());
				itemStartStr = temp + "-15 00:00:00";
			} else if(day == 15) {
				itemStartDate = new Date(date.getTime() - 14 * CommonConstant.DAYTIMES);
				itemEndDate = new Date(date.getTime() - CommonConstant.HOURTIMES);
				itemStartStr = sdfAll.format(itemStartDate);
			} else {
				continue;
			}
			itemEndStr = sdfAll.format(itemEndDate);
			System.out.println(itemStartStr + "," + itemEndStr);
			hourRainStatiStatistics.ext(itemStartStr, itemEndStr);
		}
//		hourRainStatiStatistics.accumulate(startTime, endTime);
	}

}
