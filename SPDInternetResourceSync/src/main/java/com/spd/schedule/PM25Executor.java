package com.spd.schedule;

import java.util.Set;

import com.spd.pm25.impl.PM25Impl;
import com.spd.tool.PropertiesUtil;
/**
 * 修改成从AQI报文中获取。
 */
public class PM25Executor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		PM25Impl pm25Impl = new PM25Impl();
		Set<String> cities = pm25Impl.getAllCites();
		for(String city : cities) {
			pm25Impl.analyst(city);
		}
//		String[] results = pm25Impl.getPMResult(new String[]{"chongqing", "chengdu"});
//		pm25Impl.analyst(results);
	}

}
