package com.spd.hourrain;

import java.util.List;

import com.spd.dao.cq.impl.HourRainExtDaoImpl;
import com.spd.dao.cq.impl.HourRainSumDaoImpl;

public class HourRainCalc {
	
	/**
	 * 求和
	 * @param startTime
	 * @param endTime
	 */
	public void accumulate(String startTime, String endTime) {
		//1. 统计结果
		HourRainSumDaoImpl hourRainSumDaoImpl = new HourRainSumDaoImpl();
		List dataList = hourRainSumDaoImpl.queryDataByTimes(startTime, endTime);
		//2. 插入数据库
		hourRainSumDaoImpl.insert(dataList, startTime, endTime);
	}
	
	/**
	 * 求极值
	 * @param startTime
	 * @param endTime
	 */
	public void ext(String startTime, String endTime) {
		//1. 统计极值
		HourRainExtDaoImpl hourRainExtDaoImpl = new HourRainExtDaoImpl();
		List dataList = hourRainExtDaoImpl.queryDataByTimes(startTime, endTime);
		//2. 入库
		hourRainExtDaoImpl.insert(dataList, startTime, endTime);
	}
}
