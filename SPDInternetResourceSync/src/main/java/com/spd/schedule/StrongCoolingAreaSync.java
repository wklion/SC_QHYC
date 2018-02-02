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
public class StrongCoolingAreaSync {

	private StrongCoolingStationDaoImpl strongCoolingStationDaoImpl = new StrongCoolingStationDaoImpl();

	private StrongCoolingAreaDaoImpl strongCoolingAreaDaoImpl = new StrongCoolingAreaDaoImpl();
	
	/**
	 * 计算当前时间是否是在区域强降温过程中
	 * @param datetime
	 */
	private boolean isInStrongCoolingArea(String datetime) {
		return strongCoolingStationDaoImpl.isAreaStrongCooling(datetime);
	}
	
	/**
	 * 判断是否在开始中
	 * @param datetime
	 * @return
	 */
	private boolean isInStartArea(String datetime) {
		return strongCoolingAreaDaoImpl.isInStartArea(datetime);
	}
	
	/**
	 * 同步区域强降温过程
	 * @param datetime
	 */
	public void sync(String datetime) {
		//1. 查询结果中是否有EndTime为null，preDatetime在StartTime中。
		String preDatetime = CommonTool.addDays(datetime,  -1);
		HashMap startDataMap = strongCoolingAreaDaoImpl.getDataByStartTimeNullEndTime(preDatetime);
//		if(startDataMap != null) {
//			//update
//			Integer id = (Integer) startDataMap.get("id");
//			update(preDatetime, datetime, id);
//			return;
//		} 
		boolean isStrongCoolingArea = isInStrongCoolingArea(datetime);
		//判断
		if(!isStrongCoolingArea) {
			//
			if(startDataMap != null) {
				//只持续一天的，删除该记录
				Integer id = (Integer) startDataMap.get("id");
				strongCoolingAreaDaoImpl.delete(id);
				return;
			} 
		}
		HashMap endDataMap = strongCoolingAreaDaoImpl.getDataByEndTime(preDatetime);
		//2. 满足条件的话，update EndTime
		if(endDataMap != null) {
			// update
			String startTime = (String) endDataMap.get("StartTime");
			Integer id = (Integer) endDataMap.get("id");
			update(startTime, datetime, id);
			return;
		} 
		//insert
		insert(datetime);
	}
	
	public void sync2(String datetime) {
		//1.
		String preDatetime = CommonTool.addDays(datetime,  -1);
		boolean isStrongCoolingArea = isInStrongCoolingArea(datetime);
		if(isStrongCoolingArea) {
			//满足
			boolean preIsStrongCoolingArea = isInStrongCoolingArea(preDatetime);
			if(preIsStrongCoolingArea) {
				//update
				HashMap endDataMap = strongCoolingAreaDaoImpl.getDataByEndTime(preDatetime);
				if(endDataMap != null) {
					// update
					String startTime = (String) endDataMap.get("StartTime");
					Integer id = (Integer) endDataMap.get("id");
					update(startTime, datetime, id);
				} 
			} else {
				//insert
				insert(datetime);
			}
		} else {
			//不满足
			boolean isInStart = isInStartArea(preDatetime);
			if(isInStart) {
				//delete
				delete(preDatetime);
			}
		}
	}
	
	public void delete(String datetime) {
		HashMap preResult = strongCoolingAreaDaoImpl.getDataByStartTime(datetime);
		int id = (Integer) preResult.get("id");
		strongCoolingAreaDaoImpl.delete(id);
	}
	
	public void insert(String startTime) {
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", startTime + " 00:00:00");
		dataMap.put("EndTime", startTime + " 00:00:00");
		//查询影响站数
		Integer stationCnt = strongCoolingStationDaoImpl.getStationCntByTime(startTime, startTime);
		dataMap.put("StationCnt", stationCnt);
		Double[] tmps = strongCoolingStationDaoImpl.getTmpByTime(startTime, startTime);
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
	
	public void update(String startTime, String endTime, int id) {
		HashMap dataMap = new HashMap();
		dataMap.put("StartTime", startTime);
		dataMap.put("EndTime", endTime);
		dataMap.put("id", id);
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
		strongCoolingAreaDaoImpl.update(dataList, id);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		StrongCoolingAreaSync strongCoolingAreaSync = new StrongCoolingAreaSync();
		//测试开始
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "1951-01-02";
		String endTime = "2017-01-08";
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			System.out.println(timeStr);
			strongCoolingAreaSync.sync2(timeStr);
		}
		//测试结束
//		strongCoolingAreaSync.sync("1961-04-26");
	}

}
