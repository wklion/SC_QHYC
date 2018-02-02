package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.ForecastDataDao;
import com.spd.dao.cq.impl.MCIAreaAlertDao;
import com.spd.dao.cq.impl.MCIAreaDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 区域干旱
 * 执行过程：
 * 1. 当t_mciarea 发生，但还没有结束
 * 2. 当前时间大于t_mciarea中开始的时间
 * 3. 判断未来三天有超过7个站没有降水，则发生干旱区域预警
 * @author Administrator
 *
 */
public class MCIAreaAlertSync {

	private static int STATIONCNT = 7; //满足区域干旱的站数
	
	private MCIAreaDaoImpl mciAreaDaoImpl = new MCIAreaDaoImpl(); //区域干旱
	
	private ForecastDataDao forecastDataDao = new ForecastDataDao(); //预报数据

	private MCIAreaAlertDao mciAreaAlertDao = new MCIAreaAlertDao(); //预警
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		MCIAreaAlertSync mciAreaAlertSync = new MCIAreaAlertSync();
		mciAreaAlertSync.sync();
	}
	
	public void sync() {
		//往前推算一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateTimeStr = sdf.format(new Date(System.currentTimeMillis() - CommonConstant.DAYTIMES));
		String startTime = getUnEndMCIArea(); // 干旱开始时间
		if(startTime == null) return;
		boolean isForcastDaysRain = isForcastDaysRain(dateTimeStr);
		if(!isForcastDaysRain) return;
		int stationCnt = getStationsCnt();
		//满足条件后,往预警表中插入一条记录
		List dataList = new ArrayList();
		HashMap dataMap = new HashMap();
		dataMap.put("ForecastDate", dateTimeStr + " 00:00:00");
		dataMap.put("StartTime", startTime + " 00:00:00");
		dataMap.put("StationCnts", stationCnt);
		dataList.add(dataMap);
		mciAreaAlertDao.insertValues(dataList, startTime + " 00:00:00", dateTimeStr);
	}
	
	/**
	 * 查询未结束的干旱的开始时间
	 * @return
	 */
	private String getUnEndMCIArea() {
		return mciAreaDaoImpl.getUnEndMCIArea();
	}
	
	/**
	 * 查询未结束的干旱影响站数
	 * @return
	 */
	private int getStationsCnt() {
		return mciAreaDaoImpl.getStationsCnt();
	}
	/**
	 * 判断未来三天是否有降水
	 * 有超过7个站都没有降水，返回true
	 * 否则返回false
	 * @return
	 */
	private boolean isForcastDaysRain(String dateTimeStr) {
		int days = forecastDataDao.getNoPre3DaysCnt(dateTimeStr);
		if(days >= STATIONCNT) return true;
		return false;
	}
}
