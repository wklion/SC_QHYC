package com.spd.schedule;


import java.util.List;

import com.spd.common.ResourceItem;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;
import com.spd.tool.ResourceFactory;


/**
 * 主函数，启动类，图片下载,互联网图片下载
 * @author xianchao
 *
 */
public class MainExecutor  {

	public void start(){
		LogTool.logger.info("开始执行");
		List<ResourceItem> resources = ResourceFactory.getInstanceFromDB();
//		for(ResourceItem item : resources) {
//			SyncTimerTask syncTimerTask = new SyncTimerTask(item);
//			syncTimerTask.run();
//		}
//		Resources resources = ResourceFactory.getInstance();
//		List<Group> groups = resources.getGroups();
//		for(Group group:groups) {
//			SyncTimerTask syncTimerTask = new SyncTimerTask(group);
////			syncTimerTask.start();
//			syncTimerTask.run();
//		}
	}
	
	
	public static void main(String[] args) {
		MonitorThread monitorThread = new MonitorThread(30);
		monitorThread.setDaemon(true);
		monitorThread.start();
		PropertiesUtil.loadSysCofing();
		MainExecutor mainExecutor = new MainExecutor();
		mainExecutor.start();
	}

}
