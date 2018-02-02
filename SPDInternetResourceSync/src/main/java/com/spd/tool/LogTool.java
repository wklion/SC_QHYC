package com.spd.tool;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogTool {
	
	public static Logger logger = null;

	/**
	 * @param args
	 */
	
	static{
		if(logger==null){
			PropertyConfigurator.configure("log4j.properties");
			logger = Logger.getRootLogger();
		}	
	}
	
}
