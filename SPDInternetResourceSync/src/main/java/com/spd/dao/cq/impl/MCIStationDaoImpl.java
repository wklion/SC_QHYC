package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;
/**
 * MCIStation的相关操作
 * @author Administrator
 *
 */
public class MCIStationDaoImpl extends BaseDao {

	private static String ADDMCI = "insert into t_mcistation (%s) values (%s)";

	private static String QUERYMCISTRUCT = "select * from t_mcistation where 1 = 2";
	
	public HashMap<String, Object> getExistMCI(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_Mci where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	/**
	 * 获取全部自动站
	 * @param datetime
	 * @return
	 */
	public Set<String> getStations() {
		String query = "select Station_Id_C from t_station where station_Id_C like '5%' and station_Id_C <> '57431' ";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				stationSet.add((String) tempMap.get("Station_Id_C"));
			}
		}
		return stationSet;
	}
	
	/**
	 * 查询是否有存在已经开始，但还没有结束的干旱
	 * @return
	 */
	public HashMap getUnEndMCIStation() {
		String query = "select Station_Id_C, date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime from t_mcistation " +
				"where StartTime is not null and EndTime is null";
		HashMap resultMap = new HashMap();
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				HashMap dataMap = (HashMap) list.get(i);
				String startTime = (String) dataMap.get("StartTime");
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				resultMap.put(station_Id_C, startTime);
			}
		}
		return resultMap;
	}
	
	public boolean updateData(HashMap dataMap) {
		String updateSQL = "update t_mcistation set SumStrength = " + dataMap.get("SumStrength") + ", EndTime = '" + dataMap.get("EndTime") + "', " +
				"SingleStrength = " + dataMap.get("SingleStrength") + ", SingleSynthStrength = " + dataMap.get("SingleSynthStrength") + ", StrengthLevel = " +
				dataMap.get("StrengthLevel") + " where StartTime = '" + dataMap.get("StartTime") + "' and Station_Id_C = '" + dataMap.get("Station_Id_C") + "'";
		boolean flag = update(updateSQL);
		return flag;
	}
	
	public boolean getStartMCIStation(String station_Id_C, String datetime) {
		String query = "select date_format(StartTime, '%Y-%m-%d') as StartTime, Station_Id_C from t_mcistation where StartTime = '" + datetime + "' " +
				"and station_Id_C = '" + station_Id_C + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) return true;
		return false;
	}
	
	/**
	 * 查询指定站，MCI开始，但没结束的记录
	 * @param station_Id_C
	 * @return
	 */
	public Integer getUnEndMCIIdByStation(String station_Id_C, String datetime) {
		String query = "select id from t_mcistation where Station_Id_C = '" + station_Id_C + "' and StartTime < '" + datetime + "' and endTime is null";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			Integer id = (Integer) dataMap.get("id");
			return id;
		}
		return null;
	}
	
	/**
	 * 查询所有已经开始，但还没有结束的过程
	 * @return
	 */
	public List getAllUnEndMCIStations() {
		String query = "select id, date_format(StartTime, '%Y-%m-%d') as StartTime, Station_Id_C" +
				" from t_mcistation where StartTime is not null and EndTime is null";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			return list;
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
	
	public ResultSetMetaData getMCIResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMCISTRUCT);
	}
	
}
