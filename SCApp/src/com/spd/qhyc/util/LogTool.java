package com.spd.qhyc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogTool {
	private static Logger logger = null;
	/**
	 * @autor:杠上花
	 * @date:2018年1月29日
	 * @modifydate:2018年1月29日
	 * @param:
	 * @return:
	 * @description:
	 */
	public static Logger getLog(){
		if(logger == null){
			logger = LogManager.getLogger("mylog");
		}
		return logger;
	}
}
