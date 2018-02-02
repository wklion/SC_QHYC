package com.spd.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class LogTool {
	static {
		File file = new File("log4j2.xml");
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			final ConfigurationSource source = new ConfigurationSource(in);
			Configurator.initialize(null, source);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Logger logger = LogManager.getLogger(LogTool.class.getName());
}
