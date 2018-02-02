package com.spd.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

public class CommonTool {
	
	private static Properties prop;
	
	public String getValue(String param) {
		return prop.getProperty(param);
	}
	
	public  CommonTool() {
		prop = new Properties();
//		String configPath = getConfigPath("common.properties");
		String configPath = "common.properties";
		InputStream is = null;
		try {
			is = new FileInputStream(configPath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		} 
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {      
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}
