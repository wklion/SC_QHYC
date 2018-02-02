package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 高温
 * @author Administrator
 *
 */
public class HighTmpDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_max_tmp where 1=2";

	private String ADDSTMP = "insert into t_max_tmp (%s) values (%s)";

	private String UPDATETMP = "update t_max_tmp set TEM_Max = ?, TEM_Max_OTime = ? where id = ?";
	
	public List getAllData() {
		String query = "select TEM_Max, Datetime, Station_Name, Station_Id_C from t_max_tmp  order by Datetime, Station_Id_C ";		
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getDataByTimeRange(String startDateTime, String endDateTime) {
		String query = "select TEM_Max, Datetime, Station_Name, Station_Id_C from t_max_tmp where station_id_c like '5%' and datetime >= '" + startDateTime + "' and datetime <= '" + endDateTime + "' order by Datetime, Station_Id_C ";		
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getDataByTime(String datetime) {
		String query = "select date_format(Datetime, '%Y-%m-%d') as Datetime, count(1) as cnt from t_max_tmp where Station_Id_C like '5%' and datetime = '" + datetime + "' and Tem_Max >= 35 having cnt >= 7";		
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getCntListByTimes(String startTime, String endTime, int days) {
		String query = "select date_format(Datetime, '%Y-%m-%d') as Datetime, count(1) as cnt from t_max_tmp where Station_Id_C like '5%' and datetime >= '" 
			+ startTime + "' and datetime <= '" + endTime + "' and Tem_Max >= 35  group by datetime having cnt >= " + days;		
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getDataListByTime(String datetime) {
		String query = "select date_format(Datetime, '%Y-%m-%d') as Datetime, Tem_Max from t_max_tmp where Station_Id_C like '5%' and datetime = '" + datetime + "' and Tem_Max >= 35";		
		List list = query(getConn(), query, null);
		return list;
	}
	
	public HashMap<String, Object> getExistMaxTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_max_tmp where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertMaxTmpValue(List dataList) {
		//先判断重复
		insertBatch(ADDSTMP, dataList, getMaxTmpResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistMaxTmp(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_max_tmp where year >= " + startYear + " and year <= " + endYear;		
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
	 * 查询t_max_tmp表结构
	 * @return
	 */
	public ResultSetMetaData getMaxTmpResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
	public void update(List updateDataList) {
		updateBatch2(UPDATETMP, updateDataList, getMaxTmpResultSetMetaData(), new String[]{"TEM_Max", "TEM_Max_OTime"});
	}
	
}
