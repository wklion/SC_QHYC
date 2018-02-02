package com.spd.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTool {
	
	public static Logger getLogger(Class instance) {
		return LoggerFactory.getLogger(instance);
	}
	
}
