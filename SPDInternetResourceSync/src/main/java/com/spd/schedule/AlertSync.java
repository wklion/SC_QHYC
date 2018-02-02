package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 灾害预警统一入口，执行时间，每半小时
 * @author Administrator
 *
 */
public class AlertSync {

	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		//监测
		MonitorThread monitorThread = new MonitorThread(30);
		monitorThread.setDaemon(true);
		monitorThread.start();
		AreaHighTmpAlertSync areaHighTmpAlertSync = new AreaHighTmpAlertSync(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String datetime = sdf.format(new Date(System.currentTimeMillis() - CommonConstant.DAYTIMES));
		//高温预警，包含单站、区域
		areaHighTmpAlertSync.sync(datetime);
		//单站低温预警
		LowTmpStationAlertSync lowTmpStationAlertSync = new LowTmpStationAlertSync();
		lowTmpStationAlertSync.sync(datetime);
		//区域低温预警
		LowTmpAreaAlertSync lowTmpAreaAlertSync = new LowTmpAreaAlertSync();
		lowTmpAreaAlertSync.sync(datetime);
		//连阴雨单站
		ContinueStationRainAlertSync continueStationRainAlertSync = new ContinueStationRainAlertSync();
		continueStationRainAlertSync.sync(datetime);
		//连阴雨区域
		ContinueAreaRainAlertSync continueAreaRainAlertSync = new ContinueAreaRainAlertSync();
		continueAreaRainAlertSync.sync(datetime);
		//区域干旱
		MCIAreaAlertSync mciAreaAlertSync = new MCIAreaAlertSync();
		mciAreaAlertSync.sync();
	}
}
