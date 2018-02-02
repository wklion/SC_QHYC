package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.spd.tool.CommonConstant;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;

/**
 * 灾害要素
 * @author Administrator
 *
 */
public class DisasterThread extends Thread {

	private String timeStr;
	
	CIMISSDayExecutor cimissDayExecutor = new CIMISSDayExecutor();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public DisasterThread(String timeStr) {
		this.timeStr = timeStr;
		
	}
	
	public static void main(String[] args) {
		String startTime = "1951-01-01";
		String endTime = "2017-04-06";
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
		PropertiesUtil.loadSysCofing();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			String currentTime = sdf.format(new Date(i));
			DisasterThread disasterThread = new DisasterThread(currentTime);
			System.out.println(currentTime);
			disasterThread.run();
		}
	}
	
	public void run() {
		LogTool.logger.info(sdf.format(System.currentTimeMillis()) + "灾害要素线程启动");
		//区域高温
		cimissDayExecutor.syncAreaHighTmp(timeStr);
//		//秋雨
		cimissDayExecutor.syncAutumnRains(timeStr);
//		//区域评估暴雨
		cimissDayExecutor.syncAreaRainStorm(timeStr);
//		//连阴雨单站
		cimissDayExecutor.syncContinueStationRain(timeStr);
//		//连阴雨区域
		cimissDayExecutor.syncContinueAreaRain(timeStr);
////		//强降温单站
		cimissDayExecutor.syncStrongCoolingStation(timeStr);
//		//强降温区域
		cimissDayExecutor.syncStrongCoolingArea(timeStr);
//		//低温单站
		cimissDayExecutor.syncLowTmpStation(timeStr);
//		//低温区域
		cimissDayExecutor.syncLowTmpArea(timeStr);
//		//降雪区域
		cimissDayExecutor.syncSnowArea(timeStr);
	}
}
