package com.spd.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test {
	public static void main(String[] args) throws Exception {
		Logger logger = LogManager.getLogger("mylog");
		for(int i = 0; i < 50000; i++) {  
	        logger.trace("trace level");  
	        logger.debug("debug level");  
	        logger.info("info level");  
	        logger.warn("warn level");  
	        logger.error("error level");  
	        logger.fatal("fatal level");  
	    }
	}
}
