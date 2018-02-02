package com.spd.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.spd.dao.cq.impl.HourRainDaoImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 小时降水排位程序，对每个站的前20位进行统计
 * @author Administrator
 *
 */
public class TestHourRainRank {

	//定义统计的数
	private int limit = 20;
	
	public void aws() {
		HourRainDaoImpl hourRainDaoImpl = new HourRainDaoImpl();
		String[] keys = new String[]{"R1", "R3", "R6", "R12", "R24"};
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("e:/mwsstation.csv")));
			String line = null;
			while((line = br.readLine()) != null) {
				System.out.println(line);
				String[] temp = line.split(",");
				String station_Id_C = temp[0];
				String station_Name = temp[1];
				for(String key : keys) {
					hourRainDaoImpl.queryDataByItem(station_Id_C, station_Name, key, limit);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		TestHourRainRank testHourRainRank = new TestHourRainRank();
		testHourRainRank.aws();
	}

}
