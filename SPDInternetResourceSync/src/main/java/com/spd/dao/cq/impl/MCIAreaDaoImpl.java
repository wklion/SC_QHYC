package com.spd.dao.cq.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;
/**
 * t_MCIArea的相关操作
 * @author Administrator
 *
 */
public class MCIAreaDaoImpl extends BaseDao {

	private static String ADDMCI = "insert into t_MciArea (%s) values (%s)";

	private static String QUERYMCISTRUCT = "select * from t_MciArea where 1 = 2";
	
	public int getStationsCnt() {
		String query = "select StationCnts from t_mciarea where StartTime is not null and EndTime is null";
		List list = query(getConn(), query, null);
		if(list == null || list.size() == 0) {
			return 0;
		}
		HashMap dataMap = (HashMap) list.get(0);
		Integer stationCnts = (Integer) dataMap.get("StationCnts");
		return stationCnts;
	}
	public String getUnEndMCIArea() {
		String query = "select date_format(StartTime, '%Y-%m-%d') as StartTime from t_mciarea where StartTime is not null and EndTime is null";
		List list = query(getConn(), query, null);
		if(list == null || list.size() == 0) {
			return null;
		}
		HashMap dataMap = (HashMap) list.get(0);
		String startTime = (String) dataMap.get("StartTime");
		return startTime;
	}
	
	/**
	 * 根据开始，结束时间，查找对应的值
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String[] getStartTimeListByTimes(String startTime, String endTime, int stationCnt, int noDataDays) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int days = ((Long)((endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES)).intValue() + 1;
		String query = "select date_format(StartTime, '%Y-%m-%d') as StartTime, Station_Id_C from t_mcistation where StartTime >= '" +
			startTime + "' and StartTime <= '" + endTime + "' order by StartTime";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			int resultCnt = list.size();
			if(resultCnt >= stationCnt) {
				//站数满足条件， 然后判断缺测的日数是否满足条件
				String query2 = "select count(1) as cnt from (select distinct starttime as cnt from t_mcistation where StartTime >= '" +
					startTime + "' and StartTime <= '" + endTime + "')a";
				List cntDaysList = query(getConn(), query2, null);
				if(cntDaysList != null && cntDaysList.size() > 0) {
					//天数有多少
					int daysCnt = ((Long)((HashMap)cntDaysList.get(0)).get("cnt")).intValue();
					if(days - daysCnt <= noDataDays) {
						//满足条件
						String result1 = (String) ((HashMap)list.get(0)).get("StartTime");
						String result2 = (String) ((HashMap)list.get(list.size() - 1)).get("StartTime");
						return new String[]{result1, result2};
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据开始，结束时间，查找对应的值
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String[] getEndTimeListByTimes(String startTime, String endTime, int stationCnt, int noDataDays) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int days = ((Long)((endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES)).intValue() + 1;
		String query = "select date_format(EndTime, '%Y-%m-%d') as EndTime, Station_Id_C from t_mcistation where EndTime >= '" +
			startTime + "' and EndTime <= '" + endTime + "' order by EndTime";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			int resultCnt = list.size();
			if(resultCnt >= stationCnt) {
				//站数满足条件， 然后判断缺测的日数是否满足条件
				String query2 = "select count(1) as cnt from (select distinct EndTime as cnt from t_mcistation where EndTime >= '" +
					startTime + "' and EndTime <= '" + endTime + "')a";
				List cntDaysList = query(getConn(), query2, null);
				if(cntDaysList != null && cntDaysList.size() > 0) {
					//天数有多少
					int daysCnt = ((Long)((HashMap)cntDaysList.get(0)).get("cnt")).intValue();
					if(days - daysCnt <= noDataDays) {
						//满足条件
						String result1 = (String) ((HashMap)list.get(0)).get("EndTime");
						String result2 = (String) ((HashMap)list.get(list.size() - 1)).get("EndTime");
						return new String[]{result1, result2};
					}
				}
			}
		}
		return null;
	}
	
	public void addMCIAreaStartTime(String startTime, String startTmpTime1, String startTmpTime2) {
		int year = Integer.parseInt(startTime.substring(0, 4));
		String query = "select id, StartTime from t_mciarea where StartTime >= '" + startTime + "' or EndTime is null";
		List existList = query(getConn(), query, null);
		if(existList != null && existList.size() > 0) {
			int id = (Integer) ((HashMap) existList.get(0)).get("id");
			String updateSQL = "update t_mciarea set StartTime = '" + startTime + "', StartTmpTime1 = '" + startTmpTime1 + "', StartTmpTime2 = '" + startTmpTime2 + "', year = " + year + " where id = " + id;
			update(updateSQL);
		} else {
			List dataList = new ArrayList();
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", startTime);
			dataMap.put("StartTmpTime1", startTmpTime1);
			dataMap.put("StartTmpTime2", startTmpTime2);
			dataMap.put("year", year);
			dataList.add(dataMap);
			insertBatch(ADDMCI, dataList);
		}
	}
	
	
	public void updateMCIAreaEndTime(String endTime, String endTmpTime1, String endTmpTime2) {
		String yearStr = endTime.substring(0, 4);
		int year = Integer.parseInt(yearStr);
//		String query = "select max(id) as id from t_mciarea where EndTime is null and year = " + year; //保证一个过程定义在一个范围内
		String query = "select max(id) as id from t_mciarea where  year = " + year; //保证一个过程定义在一个范围内
		List existList = query(getConn(), query, null);
		int maxId = 0;
		if(existList != null && existList.size() > 0) {
			Integer id = (Integer) ((HashMap) existList.get(0)).get("id");
			if(id == null) return;
			maxId = id;
			String updateSQL = "update t_mciArea set EndTime = '" + endTime + "', EndTmpTime1 = '" + endTmpTime1 + "', EndTmpTime2 = '" + endTmpTime2 + "' where id = " + id;
			update(updateSQL);
			//计算影响站数，值等。
			String querySQL = "select date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(StartTmpTime1, '%Y-%m-%d') as StartTmpTime1, date_format(StartTmpTime2, '%Y-%m-%d') as StartTmpTime2," + 
						"date_format(EndTime, '%Y-%m-%d') as EndTime, date_format(EndTmpTime1, '%Y-%m-%d') as EndTmpTime1, date_format(EndTmpTime2, '%Y-%m-%d') as EndTmpTime2 from t_mciArea where id = " + id;
			List resultList = query(getConn(), querySQL, null);
			if(resultList == null || resultList.size() == 0) return;
			HashMap dataMap = (HashMap) resultList.get(0);
			String StartTime = (String) dataMap.get("StartTime");
			String StartTmpTime1 = (String) dataMap.get("StartTmpTime1");
			String StartTmpTime2 = (String) dataMap.get("StartTmpTime2");
			String EndTime = (String) dataMap.get("EndTime");
			String EndTmpTime1 = (String) dataMap.get("EndTmpTime1");
			String EndTmpTime2 = (String) dataMap.get("EndTmpTime2");
			//计算影响站数
			String stationQuery = "select distinct station_Id_C as station_Id_C from t_mcistation where (starttime >= '" + StartTmpTime1 + "' and starttime <= '" + StartTmpTime2 + "')" +
									"or (EndTime >= '" + EndTmpTime1 + "' and EndTime <= '" + EndTmpTime2 + "')";
			List stationList = query(getConn(), stationQuery, null);
			if(stationList == null || stationList.size() == 0) return;
			int stationCnts = stationList.size(); // 影响站数
			String SumStrengthQuery = "";
			String stationArray = "";
			for(int i = 0; i < stationList.size(); i++) {
				HashMap itemMap = (HashMap) stationList.get(i);
				String station_Id_C = (String) itemMap.get("station_Id_C");
				stationArray += "'" + station_Id_C + "',";
			}
			stationArray = stationArray.substring(0, stationArray.length() - 1);
			SumStrengthQuery = "select sum(mci) as sum from t_mci where datetime >= '" + StartTime + "' and datetime <= '" + EndTime + "' and station_id_C in (" +
								stationArray + ") and mci <= -1";
			List sumResultList = query(getConn(), SumStrengthQuery, null);
			if(sumResultList == null || sumResultList.size() == 0) return;
			HashMap sumItemMap = (HashMap) sumResultList.get(0);
			Double SumStrength = (Double) sumItemMap.get("sum");
			String updateMCIAreaSQL = "update t_mciarea set StationCnts = " + stationCnts + ", SumStrength = " + SumStrength + " where id = " + id;
			update(updateMCIAreaSQL);
		} 
	}
	
	/**
	 * 计算开始的序列，大于startTime的序列，找到第index条的记录的时间
	 * @param startTime
	 * @param index
	 * @return
	 */
	public String queryStartTime(String startTime, int index) {
		String query = "select date_format(startTime, '%Y-%m-%d') as startTime from t_mcistation where starttime >= '" + startTime + "' order by starttime  limit " + index;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(index - 1);
			return (String) dataMap.get("startTime");
		}
		return null;
	}
	
	public String queryEndTime(String startTime, int index) {
		String query = "select date_format(endTime, '%Y-%m-%d') as endTime from t_mcistation where endtime <= '" + startTime + "' order by endtime desc limit " + index;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(index - 1);
			return (String) dataMap.get("endTime");
		}
		return null;
	}
	
	/**
	 * 查询最大的开始时间和结束时间
	 * @return
	 */
	public Date[] getExtTimes() {
		String query = "select date_format(max(StartTmpTime2), '%Y-%m-%d') as maxStartTime, date_format(max(EndTmpTime2), '%Y-%m-%d') as maxEndTime from t_mciarea";
		List resultList = query(getConn(), query, null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			String maxStartTime = (String) dataMap.get("maxStartTime");
			String maxEndTime = (String) dataMap.get("maxEndTime");
			if(maxStartTime == null || maxEndTime == null) return null;
			try {
				Date maxStartDate = sdf.parse(maxStartTime);
				Date maxEndDate = sdf.parse(maxEndTime);
				return new Date[]{maxStartDate, maxEndDate};
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 根据开始时间，获取已经存在的数据
	 * @param datetime
	 * @return
	 */
	public HashMap<String, Object> getExistMCI(String datetime) {
		//mysql
		String query = "select StationCnts, date_format(StartTime, '%Y-%m-%d') as StartTime from t_MciArea where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			hashMap.put("cnt", tempMap.get("cnt"));
			hashMap.put("datetime", tempMap.get("datetime") + " 00:00:00");
		}
		return hashMap;
	}
	
	public String getMaxStartTime() {
		String query = "select date_format(max(StartTime), '%Y-%m-%d') as EndTime from t_MciArea";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			String result = (String) tempMap.get("StartTime");
			return result;
		}
		return null;
	}
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertMCIValue(List dataList) {
		//先判断重复
		insertBatch(ADDMCI, dataList, getMCIResultSetMetaData());
	}
	
	public void updateMCIValue(HashMap dataMap) {
		String updateSQL = "update t_MciArea set cnt = " + dataMap.get("cnt") + " where datetime = '" + dataMap.get("datetime") + "'";
		update(updateSQL);
	}
	
	public ResultSetMetaData getMCIResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMCISTRUCT);
	}
	
	public static void main(String[] args) throws Exception {
		PropertiesUtil.loadSysCofing();
		MCIAreaDaoImpl mciAreaDaoImpl = new MCIAreaDaoImpl();
//		mciAreaDaoImpl.updateMCIAreaEndTime("2011-10-01", "2011-09-28", "2011-10-01");
		BufferedReader br = new BufferedReader(new FileReader(new File("d:/1.txt")));
		String line = "";
		while((line = br.readLine()) != null) {
			String[] arrays = line.split("\\s+");
			System.out.println(arrays);
			mciAreaDaoImpl.updateMCIAreaEndTime(arrays[0], arrays[1], arrays[2]);
		}
		br.close();
	}
}
