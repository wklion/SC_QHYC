package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.spd.tool.CommonConstant;
import com.spd.tool.LogTool;

/**
 * 实况线程
 * @author Administrator
 *
 */
public class RealTimeThread extends Thread {

	private String result;
	
	private String timeStr;
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
	
	CIMISSDayExecutor cimissDayExecutor = new CIMISSDayExecutor();
	
	public RealTimeThread(String result, String timeStr) {
		this.result = result;
		this.timeStr = timeStr;
		
	}
	
	public void run() {
		//实况
		LogTool.logger.info(sdf1.format(System.currentTimeMillis()) + "实况线程启动");
		cimissDayExecutor.sync(result, timeStr);
//		Date date = null;
//		try {
//			date = sdf2.parse(timeStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		//数据完整度统计
		//如果传递进来的时间和当前时间-1天的一致，则执行，否则跳过
//		String currentTimeStr = sdf3.format(new Date(System.currentTimeMillis() - CommonConstant.DAYTIMES)) + "000000";
//		if(timeStr.equals(currentTimeStr)) {
//			DataCompleteSync dataCompleteSync = new DataCompleteSync(sdf1.format(date));
//			dataCompleteSync.start();
//		} else {
//			System.out.println("不执行数据完整度检查");
//		}
	}
	
}
