package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
/**
 * 自动站小时降水的相关操作
 * @author Administrator
 *
 */
public class AWSHourDataDaoImpl extends BaseDao {

	private static String ADDHOURRAIN = "insert into t_awshourrain (%s) values (%s)";

	private static String QUERYHOURRAINSTRUCT = "select * from t_awshourrain where 1 = 2";
	
	public HashMap<String, Object> getExistRain(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d %T') as datetime from t_awshourrain where datetime = '" + datetime + "'";		
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
	
	public List getDataMCI(String startDateTime, String endDateTime, Set<String> stationSet) {
		//mysql
		String query = "select 站号, date_format(日期, '%Y-%m-%d %T') as 日期, R1, R3, R6, R12, R24 from hourrain where 日期 >= '" + startDateTime + "' and 日期 <= '" + endDateTime + "'";		
		List list = query(getConn(), query, null);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < list.size(); i++) {
			HashMap tempMap = (HashMap) list.get(i);
			Float R1 = (Float) tempMap.get("R1");
			Float R3 = (Float) tempMap.get("R3");
			Float R6 = (Float) tempMap.get("R6");
			Float R12 = (Float) tempMap.get("R12");
			Float R24 = (Float) tempMap.get("R24");
			if((R1 != null && R1 !=0) || (R3 != null && R3 !=0) || (R6 != null && R6 !=0) || (R12 != null && R12 !=0) || (R24 != null && R24 !=0)) {
				HashMap dataMap = new HashMap();
				String station = tempMap.get("站号") + "";
				dataMap.put("datetime", tempMap.get("日期"));
				dataMap.put("station_Id_C", station);
				dataMap.put("R1", tempMap.get("R1"));
				dataMap.put("R3", tempMap.get("R3"));
				dataMap.put("R6", tempMap.get("R6"));
				dataMap.put("R12", tempMap.get("R12"));
				dataMap.put("R24", tempMap.get("R24"));
				if(stationSet.contains(station)) {
					resultList.add(dataMap);
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 获取全部自动站
	 * @param datetime
	 * @return
	 */
	public Set<String> getStations() {
		String query = "select Station_Id_C from t_station where station_Id_C like '5%' and seq is not null ";	
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
	 * 获取全部区域站
	 * @param datetime
	 * @return
	 */
	public Set<String> getMWSStations() {
		String query = "select Station_Id_C from t_station where station_Id_C like 'A%'";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				stationSet.add(((String) tempMap.get("Station_Id_C")).trim());
			}
		}
		return stationSet;
	}
	
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOURRAIN, dataList, getAWSHourRainResultSetMetaData());
	}
	
	public ResultSetMetaData getAWSHourRainResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOURRAINSTRUCT);
	}
	
}
