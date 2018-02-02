package com.spd.schedule;

import java.text.SimpleDateFormat;

import com.spd.tool.LogTool;

/**
 * 单要素资料线程
 * @author Administrator
 *
 */
public class MeteoEleThread extends Thread {

	private String result;
	
	private String timeStr;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	CIMISSDayExecutor cimissDayExecutor = new CIMISSDayExecutor();
	
	public MeteoEleThread(String result, String timeStr) {
		this.result = result;
		this.timeStr = timeStr;
		
	}
	
	public void run() {
		LogTool.logger.info(sdf.format(System.currentTimeMillis()) + "单要素资料线程启动");
//		降雪同步
//		cimissDayExecutor.syncSnow(result, timeStr);
////		//雾同步
//		cimissDayExecutor.syncFog(result, timeStr);
////		//大风同步
//		cimissDayExecutor.syncWinInstMax(result, timeStr);
//		//高温
		cimissDayExecutor.syncTmpMax(result, timeStr);
		//雷暴
//		cimissDayExecutor.syncThund(result, timeStr);
		//低温
		cimissDayExecutor.syncLowTmp(result, timeStr);
		//暴雨08-08
//		cimissDayExecutor.syncRainStorm(result, timeStr, "t_rainstorm0808");
//		//暴雨08-20
//		cimissDayExecutor.syncRainStorm(result, timeStr, "t_rainstorm0820");
		//暴雨20-20
		cimissDayExecutor.syncRainStorm(result, timeStr, "t_rainstorm2020");
		//暴雨 20-08
//		cimissDayExecutor.syncRainStorm(result, timeStr, "t_rainstorm2008");
		//冰雹
//		cimissDayExecutor.syncHail(timeStr);
//		//天气现象
//		cimissDayExecutor.syncWep(result, timeStr);
//		//土壤湿度
//		AgmeSoilSync agmeSoilSync = new AgmeSoilSync();
//		agmeSoilSync.sync(timeStr);
//		//小时降水累积统计，极值统计
//		HourRainStatiStatistics hourRainStatiStatistics = new HourRainStatiStatistics();
//		hourRainStatiStatistics.sync(timeStr);
	}
}
