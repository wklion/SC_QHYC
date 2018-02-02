package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.spd.dao.cq.impl.ContinuousRainsStationDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_0820DaoImpl;
import com.spd.dao.cq.impl.T_pre_time_2020DaoImpl;
import com.spd.dao.cq.impl.T_sshDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 连阴雨单站统计
 * @author Administrator
 *
 */
public class ContinueStationRainSync {

	public static int NOSUNDAYS = 6; //无日照时数

	private static int NORAINDAYS = 4; //连续无降水，则结束的时间
	
	private T_sshDaoImpl sshDaoImpl = new T_sshDaoImpl();

	private T_pre_time_0820DaoImpl preTime0820DaoImpl = new T_pre_time_0820DaoImpl();

	private ContinuousRainsStationDaoImpl continuousRainsStationDaoImpl = new ContinuousRainsStationDaoImpl();
	
	/**
	 * 根据开始，结束时间，查询t_pre_time_0820、t_ssh数据，判断是否满足连阴雨
	 * @param startTime
	 * @param endTime
	 */
	public List queryContinueStationRain(String startTime, String endTime, Set<String> existStations) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String items = CommonTool.createItemStrByRangeDate(startTime, endTime);
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List sshDataList = sshDaoImpl.queryData(startTime, endTime, items);
		List preTime2020DataList = preTime0820DaoImpl.queryData(startTime, endTime, items);
		List resultList = new ArrayList(); //满足连阴雨的返回结果
		//判断是否满足连阴雨,6天无日照，且 4天以上降水>=0.1
		//把结果整理处理，日照，降水都存到HashMap中
		HashMap<String, Integer> sshMap = new HashMap<String, Integer>();
		HashMap<String, Integer> preCntMap = new HashMap<String, Integer>();
		HashMap<String, Double> preMap = new HashMap<String, Double>();
		
		for(int i = 0; i < sshDataList.size(); i++) {
			HashMap dataMap = (HashMap) sshDataList.get(i);
			String station_id_C = (String) dataMap.get("Station_Id_C"); 
			int year = (Integer) dataMap.get("year");
			Iterator it = dataMap.keySet().iterator();
			int noSSHCnt = 0, preDaysCnt = 0;
			Double preSum = 0.0;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double ssh = (Double) dataMap.get(key);
						if(ssh != null && ssh == 0) {
							noSSHCnt ++;
						}
					}
				}
			}
			Integer preNoSSHCnt = sshMap.get(station_id_C);
			if(preNoSSHCnt != null) {
				sshMap.put(station_id_C, preNoSSHCnt + noSSHCnt);
			} else {
				sshMap.put(station_id_C, noSSHCnt);
			}
		}
		
		for(int i = 0; i < preTime2020DataList.size(); i++) {
			HashMap dataMap = (HashMap) preTime2020DataList.get(i);
			String station_id_C = (String) dataMap.get("Station_Id_C"); 
			int year = (Integer) dataMap.get("year");
			Iterator it = dataMap.keySet().iterator();
			int preCnt = 0, preDaysCnt = 0;
			Double preSum = 0.0;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double pre = (Double) dataMap.get(key);
						if(pre != null && pre >= 0.1 && pre < 999) {
							preCnt ++;
							preSum += pre;
						}
					}
				}
			}
			Integer prePreCnt = preCntMap.get(station_id_C);
			if(prePreCnt != null) {
				preCntMap.put(station_id_C, prePreCnt + preCnt);
			} else {
				preCntMap.put(station_id_C, preCnt);
			}
			
			Double prePreSum = preMap.get(station_id_C);
			if(prePreSum != null) {
				preMap.put(station_id_C, prePreSum + preSum);
			} else {
				preMap.put(station_id_C, preSum);
			}
			
		}
		Iterator<String> it = sshMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Integer noSSHCnt = sshMap.get(key); // 日照
			if(noSSHCnt >= NOSUNDAYS) {
				Integer preCnt = preCntMap.get(key);
				if(preCnt >= NORAINDAYS) {
					HashMap resultMap = new HashMap();
					resultMap.put("Station_Id_C", key);
					resultMap.put("StartTime", startTime + " 00:00:00");
					resultMap.put("EndTime", endTime + " 00:00:00");
					resultMap.put("NoSunDays", NOSUNDAYS);
					resultMap.put("RainDays", preCnt);
					resultMap.put("Pre", CommonTool.roundDouble2(preMap.get(key)));
					if(!existStations.contains(key)) {
						resultList.add(resultMap);
					}
				}
			}
		}
//			if(noSSHCnt >= NOSUNDAYS) {
//				//满足日照
//				for(int j = 0; j < preTime2020DataList.size(); j++) {
//					HashMap preTimeMap = (HashMap) preTime2020DataList.get(j);
//					String preStation_id_C = (String) preTimeMap.get("Station_Id_C");
//					if(!preStation_id_C.equals(station_id_C)) {
//						continue;
//					} 
//					Iterator it2 = preTimeMap.keySet().iterator();
//					while(it2.hasNext()) {
//						String key = (String) it2.next();
//						if(key.startsWith("m")) {
//							boolean isInTime = isInTime(key, year, startDate, endDate);
//							if(isInTime) {
//								Double pre = (Double) preTimeMap.get(key);
//								if(pre != null && pre >= 0.1 && pre < 999) {
//									preDaysCnt ++;
//									preSum += pre;
//								}
//							}
//						}
//					}
//					if(preDaysCnt >= 4) {
//						//满足连阴雨条件
//						HashMap resultMap = new HashMap();
//						resultMap.put("Station_Id_C", station_id_C);
//						resultMap.put("StartTime", startTime + " 00:00:00");
//						resultMap.put("EndTime", endTime + " 00:00:00");
//						resultMap.put("NoSunDays", NOSUNDAYS);
//						resultMap.put("RainDays", preDaysCnt);
//						resultMap.put("Pre", CommonTool.roundDouble2(preSum));
//						if(!existStations.contains(station_id_C)) {
//							resultList.add(resultMap);
//						}
//					}
//					break;
//				}
//			} else {
//				continue;
//			}
//		}
		return resultList;
	}
	
	public void sync(String endTime, String startTime) {
		//判断endTime的前一天是否已经在结果表中存在
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = null;
		try {
			endDate = sdf.parse(endTime);
			endDate = new Date(endDate.getTime() - CommonConstant.DAYTIMES);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String preEndTime = sdf.format(endDate);
		List existEndDataList = continuousRainsStationDaoImpl.getDataByEndTime(preEndTime);
		Set<String> updateStations = new HashSet<String>();
		HashMap[] existMap = queryExistLastMap(endTime);
		List updateList = new ArrayList();
		for(int i = 0; i < existEndDataList.size(); i++) {
			HashMap itemMap = (HashMap) existEndDataList.get(i);
			Integer id = (Integer) itemMap.get("id");
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			int noSunDays = (Integer) itemMap.get("NoSunDays");
			int rainDays = (Integer) itemMap.get("RainDays");
			double pre = (Double) itemMap.get("Pre");
			int sshCnt = (Integer) existMap[0].get(station_Id_C);
			int preCnt = (Integer) existMap[1].get(station_Id_C);
			Double preLast = (Double) existMap[2].get(station_Id_C);
			if(sshCnt == NORAINDAYS && preCnt != 0) {
				//满足条件，update
				HashMap updateMap = new HashMap();
				updateMap.put("id", id);
				updateMap.put("EndTime", endTime);
				updateMap.put("NoSunDays", noSunDays + 1);
				if(preLast >= 0.1 && preLast < 999) {
					updateMap.put("Pre", CommonTool.roundDouble2(pre + preLast));
					updateMap.put("RainDays", rainDays + 1);
				} else {
					updateMap.put("RainDays", rainDays);
					updateMap.put("Pre", pre);
				}
				updateList.add(updateMap);
				updateStations.add(station_Id_C);
			}
		}
		continuousRainsStationDaoImpl.update(updateList);
		//对于不连续的，则查询并且判断是否满足连阴雨
		List insertList = queryContinueStationRain(startTime, endTime, updateStations);
		List existLast = continuousRainsStationDaoImpl.getDataByEndTime(endTime);
		for(int i = insertList.size() - 1; i >= 0; i--) {
			HashMap itemMap = (HashMap) insertList.get(i);
			String existStartTime = (String) itemMap.get("StartTime");
			String existEndTime = (String) itemMap.get("EndTime");
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			for(int j = 0; j < existLast.size(); j++) {
				HashMap itemMap2 = (HashMap) existLast.get(j);
				String existStartTime2 = (String) itemMap2.get("StartTime");
				String existEndTime2 = (String) itemMap2.get("EndTime");
				String station_Id_C2 = (String) itemMap2.get("Station_Id_C");
				if((existEndTime + station_Id_C).equals(existEndTime2 + station_Id_C2)) {
					insertList.remove(i);
					break;
				}
			}
		}
		continuousRainsStationDaoImpl.insert(insertList);
	}
	
	private HashMap[] queryExistLastMap(String endTime) {
		//获取最后一天的对应的字段
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = null;
		Date endDate = null;
		try {
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date startDate = new Date(endDate.getTime() - CommonConstant.DAYTIMES * NORAINDAYS);
		startTime = sdf.format(startDate);
		String lastColumnName = CommonTool.createColumnByDate(endDate);
		
		//在existEndDataList中已经存在的结果，判断最后两天和当前天，一起，是否满足结束条件，
		Date tempStart = new Date(endDate.getTime() - (NORAINDAYS - 1) * CommonConstant.DAYTIMES);
		String tempStartStr = sdf.format(tempStart);
		String items = CommonTool.createItemStrByRangeDate(tempStartStr, endTime);
		List sshDataList = sshDaoImpl.queryData(startTime, endTime, items);
		List preTime2020DataList = preTime0820DaoImpl.queryData(startTime, endTime, items);
		
		//计数
		HashMap<String, Integer> sshCntMap = new HashMap<String, Integer>();
		HashMap<String, Integer> preCntMap = new HashMap<String, Integer>();
		HashMap<String, Double> preLastMap = new HashMap<String, Double>(); // 最后一天的降水
		for(int i = 0; i < sshDataList.size(); i++) {
			int sshCnt = 0;
			HashMap sshMap = (HashMap) sshDataList.get(i);
			String station_Id_C = (String) sshMap.get("Station_Id_C");
			int year = (Integer) sshMap.get("year");
			Iterator it = sshMap.keySet().iterator();
			while(it.hasNext()) {
				//加上时间判断，介于开始，结束时间
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double value = (Double) sshMap.get(key);
						if(value != null && value == 0 && value < 999) {
							sshCnt ++;
						}
					}
				}
			}
			Integer preSSHCnt = sshCntMap.get(station_Id_C);
			if(preSSHCnt != null) {
				sshCntMap.put(station_Id_C, preSSHCnt + sshCnt);
			} else {
				sshCntMap.put(station_Id_C, sshCnt);
			}
		}
		for(int i = 0; i < preTime2020DataList.size(); i++) {
			int preCnt = 0;
			HashMap preMap = (HashMap) preTime2020DataList.get(i);
			int year = (Integer) preMap.get("year");
			Iterator it = preMap.keySet().iterator();
			String station_Id_C = (String) preMap.get("Station_Id_C");
			Double pre = (Double) preMap.get(lastColumnName);
			preLastMap.put(station_Id_C, pre);
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.startsWith("m")) {
					boolean isInTime = CommonTool.isInTime(key, year, startDate, endDate);
					if(isInTime) {
						Double value = (Double) preMap.get(key);
						if(value != null && value != 0 && value < 999) {
							preCnt ++;
						}
					}
				}
			}
			Integer prePreCnt = preCntMap.get(station_Id_C);
			if(prePreCnt != null) {
				preCntMap.put(station_Id_C, prePreCnt + preCnt);
			} else {
				preCntMap.put(station_Id_C, preCnt);
			}
		}
		return new HashMap[]{sshCntMap, preCntMap, preLastMap};
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startStr = "1952-10-01";
		String endStr = "2016-08-25";
		Date date = null, endDate = null;
		try {
			date = sdf.parse(startStr);
			endDate = sdf.parse(endStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		//
//		//测试开始
//		String endTimeStr = "1952-10-29";
//		try {
//			date = sdf.parse(endTimeStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		//测试结束
		for(long i = date.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String endTime = sdf.format(date);
			System.out.println(endTime);
			Date startDate = new Date(date.getTime() - (NOSUNDAYS - 1) * CommonConstant.DAYTIMES);
			String startTime = sdf.format(startDate);
			ContinueStationRainSync continueStationRainSync = new ContinueStationRainSync();
			continueStationRainSync.sync(endTime, startTime);
			date = new Date(date.getTime() + CommonConstant.DAYTIMES);
		}
	}

}
