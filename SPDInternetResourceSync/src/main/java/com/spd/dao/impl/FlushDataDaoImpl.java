package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class FlushDataDaoImpl extends BaseDao {
	//添加Flush数据
	private String ADDFLUSH = "insert into t_flashdata (%s) values (%s)";
	//获取t_flashdata表结构
	private String QUERYFLUSHSTRUCT = "select * from t_flashdata where 1=2";
	
	/**
	 * 查询Flush中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, Object> getExistFLUSH(String startTime, String endTime) {
		String query = "select date_format(datetime,'%Y-%m-%d %T') as datetime, longitude, latitude, haomiao from t_flashdata where datetime >= '" + startTime + "' and datetime <= '" + endTime + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("datetime") + "_" + tempMap.get("longitude") + "_" + tempMap.get("latitude") + "_" + tempMap.get("haomiao");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_flashdata中插入数据
	 * @param dataList
	 */
	public void insertFLUSHValue(List dataList) {
		insertBatch(ADDFLUSH, dataList, getFlushResultSetMetaData());
	}
	
	/**
	 * 查询t_flashdata表结构
	 * @return
	 */
	public ResultSetMetaData getFlushResultSetMetaData() {
		return getTableStruct(getConn(), QUERYFLUSHSTRUCT);
	}
}	
