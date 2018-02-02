package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;
/**
 * MCI的相关操作
 * @author Administrator
 *
 */
public class MCIAreaDaysDaoImpl extends BaseDao {

	private static String ADDMCI = "insert into t_MciAreaDays (%s) values (%s)";

	private static String QUERYMCISTRUCT = "select * from t_MciAreaDays where 1 = 2";
	
	public HashMap<String, Object> getExistMCI(String datetime) {
		//mysql
		String query = "select cnt, date_format(datetime, '%Y-%m-%d') as datetime from t_MciAreaDays where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			hashMap.put("cnt", tempMap.get("cnt"));
			hashMap.put("datetime", tempMap.get("datetime") + " 00:00:00");
		}
		return hashMap;
	}
	
	public List getMCIAreaDaysByTimes(String startTime, String endTime) {
		List resultList = new ArrayList(); 
		String query = "select cnt, date_format(datetime, '%Y-%m-%d') as datetime from t_MciAreaDays where datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and cnt >= 7 order by datetime";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			hashMap.put("cnt", tempMap.get("cnt"));
			hashMap.put("datetime", tempMap.get("datetime"));
			resultList.add(hashMap);
		}
		return resultList;
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
		String updateSQL = "update t_MciAreaDays set cnt = " + dataMap.get("cnt") + " where datetime = '" + dataMap.get("datetime") + "'";
		update(updateSQL);
	}
	
	public ResultSetMetaData getMCIResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMCISTRUCT);
	}
	
}
