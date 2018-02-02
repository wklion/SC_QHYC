package com.spd.qhyc.config;

import java.io.File;

import com.google.gson.Gson;
import com.spd.qhyc.file.FileHelper;
import com.spd.qhyc.model.Config;

/**
 * @作者:wangkun
 * @日期:2017年12月5日
 * @公司:spd
 * @说明:
*/
public class ConfigHelper {
	private static Config config;
	public static Config getConfig(){
		if(config==null){
			String strFile = "config/config.json";
			File file = new File(strFile);
			System.out.println(file.getAbsolutePath());
			FileHelper fileHelper = new FileHelper();
			Gson gson = new Gson();
			try {
				String str = fileHelper.readFile(strFile);
				config = gson.fromJson(str, Config.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return config;
	}
}
