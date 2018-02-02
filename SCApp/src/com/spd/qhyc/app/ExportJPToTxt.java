package com.spd.qhyc.app;

import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.model.Config;

/**
 * @作者:wangkun
 * @日期:2017年12月6日
 * @公司:spd
 * @说明:
*/
public class ExportJPToTxt {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//1、读取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		
		ExportJPToTxtPro exportJPToTxtPro = new ExportJPToTxtPro();
		//2、气温
//		String path = config.getDataOutputPath();
//		String fileName = config.getMonthTempJPFileName();
		String path="E:/test/";
		String fileName="guance_t.txt";
		String strFile = path+fileName;
		exportJPToTxtPro.excute("t_month_temp", "v_hos_temp", strFile);
		System.out.println("导出月气温距平完成!");
		//3、降水
//		fileName = config.getMonthPrecJPFileName();
		fileName="guance_r.txt";
		strFile = path+fileName;
		//exportJPToTxtPro.excute("t_month_rain", "v_hos_rain", strFile);
		//System.out.println("导出月降水距平完成!");
	}

}
