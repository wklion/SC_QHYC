package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.StrongCoolingAreaDaoImpl;
import com.spd.dao.cq.impl.StrongCoolingStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域强降温
 * @author Administrator
 *
 */
public class StrongCoolingAreaNewSync {

	private StrongCoolingStationDaoImpl strongCoolingStationDaoImpl = new StrongCoolingStationDaoImpl();

	private StrongCoolingAreaDaoImpl strongCoolingAreaDaoImpl = new StrongCoolingAreaDaoImpl();
	
	private static int STATIONCNT = 7; //大于等于该站数的，表示符合连阴雨区域的条件
	
	/**
	 * 分析强降温过程
	 * @param datetime
	 */
	public String analyst(String datetime) {
		int cnt = strongCoolingStationDaoImpl.getStationCntByTimes(datetime);
		if(cnt >= STATIONCNT) {
			String preDatetime = CommonTool.addDays(datetime, -1);
			return analyst(preDatetime);
		} else {
			return datetime;
		}
	}
	
	public void sync(String datetime) {
		//1. 判断当天是否满足，如果满足的话，则往前找，直到找到不满足的为止。
		String startTime = analyst(datetime);
		if(startTime.equals(datetime) || CommonTool.addDays(startTime, 1).equals(datetime)) {
			//不满足条件
			return;
		}
		//2. 分析参数，构造结果，入库
		startTime = CommonTool.addDays(startTime, 1);
		analystResult(startTime, datetime);
	}
	
	private void analystResult(String startTime, String endTime) {
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", startTime + " 00:00:00");
		dataMap.put("EndTime", endTime + " 00:00:00");
		//查询影响站数
		Integer stationCnt = strongCoolingStationDaoImpl.getStationCntByTime(startTime, endTime);
		dataMap.put("StationCnt", stationCnt);
		Double[] tmps = strongCoolingStationDaoImpl.getTmpByTime(startTime, endTime);
		if(tmps != null) {
			if(tmps[0] != null) {
				dataMap.put("MaxTmp", tmps[0]);
			}
			if(tmps[1] != null) {
				dataMap.put("MinTmp", tmps[1]);
			}
			if(tmps[2] != null) {
				dataMap.put("AvgTmp", CommonTool.roundDouble(tmps[2]));
			}
		}
		List dataList = new ArrayList();
		dataList.add(dataMap);
		strongCoolingAreaDaoImpl.insert(dataList, startTime);
	}
	
	public static void main(String[] args) throws Exception {
		PropertiesUtil.loadSysCofing();
		StrongCoolingAreaNewSync strongCoolingAreaNewSync = new StrongCoolingAreaNewSync();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "1951-01-02";
		String endTime = "2017-01-09";
		Date startDate = sdf.parse(startTime);
		Date endDate = sdf.parse(endTime);
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			System.out.println(timeStr);
			strongCoolingAreaNewSync.sync(timeStr);
		}
	}
}
