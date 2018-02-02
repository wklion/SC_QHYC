package com.spd.schedule;

import com.spd.config.CommonConfig;
import com.spd.modis.NDVIImpl;
import com.spd.tool.PropertiesUtil;

/**
 * Modis 中 Ndvi 下载
 * @author xianchao
 *
 */
public class ModisNdviExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MonitorThread monitorThread = new MonitorThread(60);
		monitorThread.setDaemon(true);
		monitorThread.start();
		PropertiesUtil.loadSysCofing();
		PropertiesUtil.loadMODISConfig();
		NDVIImpl ndviImpl = new NDVIImpl();
		String saveDir = CommonConfig.MODISNDVI_SAVEPATH;
		String[][] dirs = ndviImpl.getAllNeedSyncDirs();
		for(int i=0; i<dirs.length; i++) {
			String dateDir = dirs[i][0];
			String url = dirs[i][1];
			ndviImpl.getAllNeedSyncFilesByDirName(url, saveDir + "/" + dateDir);
		}
	}

}
