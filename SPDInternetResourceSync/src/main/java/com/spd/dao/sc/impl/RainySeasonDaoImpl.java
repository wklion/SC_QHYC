package com.spd.dao.sc.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

/**
 * 雨季实现类
 * @author Administrator
 *
 */
public class RainySeasonDaoImpl extends BaseDao {

	private String QUERYSTRUCT = "select * from t_rainyseason where 1=2";

	private String ADDDATA = "insert into t_rainyseason (%s) values (%s)";
	
	public HashMap<String, Object> getExistData(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_rainyseason where datetime = '" + datetime + "'";		
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
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertFogValue(List dataList) {
		//先判断重复
		insertBatch(ADDDATA, dataList, getResultSetMetaData());
	}
	
	public List getDataByYear(int year) {
		String query = "select id, Station_Id_C, year, date_format(StartTime, '%Y-%m-%d') as StartTime from t_rainyseason where year = " + year;
		List list = query(getConn(), query, null);
		return list;
	}
	public Set<String> getExist(int year) {
		//mysql
		String query = "select Station_Id_C, year " +
				" from t_rainyseason where year = " + year;
		Set<String> resultSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String station_Id_C = (String) tempMap.get("Station_Id_C");
				resultSet.add(station_Id_C);
			}
		}
		return resultSet;
	}
	/**
	 * @return
	 */
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSTRUCT);
	}
	
	public void updateData(List sqls) {
		for(int i = 0; i < sqls.size(); i++) {
			String sql = (String) sqls.get(i);
			update(sql);
		}
	}
}
