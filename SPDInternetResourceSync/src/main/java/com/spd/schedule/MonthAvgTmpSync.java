package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.MonthAvgTmpDaoImpl;
import com.spd.dao.cq.impl.T_tem_avgDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 计算月平均气温
 * @author Administrator
 *
 */
public class MonthAvgTmpSync {

	private MonthAvgTmpDaoImpl monthAvgTmpDaoImpl = new MonthAvgTmpDaoImpl();

	private T_tem_avgDaoImpl tem_avgDaoImpl = new T_tem_avgDaoImpl();
	
	public void sync(String datetime) {
		//1. 判断是否是月初
		boolean firstDayInMonth = firstDayInMonth(datetime);
		if(!firstDayInMonth) return;
		String[] preMonthStr = getPreMonthStr(datetime);
		//2. 是月初的话，计算上个月的每个站的月平均气温
		String items = createMonthItems(preMonthStr[0], preMonthStr[1]);
		//3. 取到数据，然后求平均
		int year = Integer.parseInt(preMonthStr[0].split("-")[0]);
		int month = Integer.parseInt(preMonthStr[0].split("-")[1]);
		List<HashMap> dataList = tem_avgDaoImpl.getDataByItems(items, year, month);
		//3. 判断数据在结果库中是否存在，如果不存在，插入数据，如果存在，更新数据
		monthAvgTmpDaoImpl.disposeDataList(dataList, year, month);
	}
	
	private String[] getPreMonthStr(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date preMonthLastDayDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
		String preMonthLastDayStr = sdf.format(preMonthLastDayDate);
		String preMonthFirstDayStr = preMonthLastDayStr.substring(0, preMonthLastDayStr.length() - 2) + "01";
		return new String[]{preMonthFirstDayStr, preMonthLastDayStr};
	}
	/**
	 * 计算该时间的上一个月对应的字段
	 * @param datetime
	 * @return
	 */
	private String createMonthItems(String preMonthFirstDayStr, String preMonthLastDayStr) {
		String items = CommonTool.createItemStrByRangeDate(preMonthFirstDayStr, preMonthLastDayStr);
		return items;
	}
	
	private boolean firstDayInMonth(String datetime) {
		if(datetime == null) return false;
		if(datetime.endsWith("01")) return true;
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		MonthAvgTmpSync monthAvgTmpSync = new MonthAvgTmpSync();
		String startTimeStr = "1951-02-01";
		String endTimeStr = "2017-03-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		for(long i = startTime; i <= endTime; i += CommonConstant.DAYTIMES) {
			String datetime = sdf.format(i);
			System.out.println(datetime);
			monthAvgTmpSync.sync(datetime);
		}
	}

}
