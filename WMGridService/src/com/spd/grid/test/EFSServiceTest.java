package com.spd.grid.test;

import com.google.gson.Gson;
import com.spd.grid.funModel.UVProcessParam;
import com.spd.grid.ws.EFSService;

public class EFSServiceTest {

	public static void main(String[] args) {
		EFSService efs = new EFSService();
		UVProcessParam uvProcessParam = new UVProcessParam();
		uvProcessParam.setLevel("850");
		uvProcessParam.setPeriod("30-60");
		uvProcessParam.setResDate("2017-5-1");
		uvProcessParam.setUvDir("E:/SC/Data/UV/");
		uvProcessParam.setDerfUVDir("E:/SC/Data/Derf/");
		uvProcessParam.setTempDir("C:/Users/wklion/Desktop/temp/efs/");
		Gson gson  = new Gson();
		String param = gson.toJson(uvProcessParam);
		efs.UVProcess(param);
	}

}
