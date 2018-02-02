package com.spd.grid.test;

import com.spd.grid.config.ConfigHelper;
import com.spd.grid.model.Config;
import com.spd.grid.service.impl.FactorDllLibary;
import com.spd.grid.ws.ForcastService;
import com.sun.jna.Native;

public class ForcastServiceTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		///fs.calFactor(null);
		//String json = "{'month':6}";
		//fs.getFactor(json);
		ConfigHelper ch = new ConfigHelper();
		ch.excute();
		//Config config = ConfigHelper.config;
		//String corrFile = config.getFactorPath()+"corr_and_reg";
		//FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrFile, FactorDllLibary.class);
		//factorDll.reg(2017, 6);
		calCorr();
		//getCorr();
	}
	private static void calCorr(){
		Config config = ConfigHelper.config;
		String corrFile = config.getFactorPath()+"corr_and_reg";
		FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrFile, FactorDllLibary.class);
		//0-降水;1-气温;
		factorDll.corr(1); 
		System.out.println("计算相关系数完成!");
	}
	private static void getCorr() throws Exception{
		String json = "{'month':6}";
		ForcastService fs = new ForcastService();
		Object obj = fs.getFactor(json);
		System.out.println(obj);
	}
}
