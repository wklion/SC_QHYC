package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class PM25Dao extends BaseDao {
	//添加PM2.5
	private String ADDFORECAST = "insert into t_aqi (%s) values (%s)";
	//获取PM2.5表结构
	private String QUERYPM25STRUCT = "select * from t_aqi where 1=2";
	
	/**
	 * 查询风景区天气预报中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, Object> getExistPM25(String observTime) {
		String query = "select date_format(observTime,'%Y-%m-%d %T') as observTime, stationname from t_aqi where  observTime = '" + observTime + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("observTime") + "_" + tempMap.get("stationname");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertPM25Value(List dataList) {
		insertBatch(ADDFORECAST, dataList, getPM25ResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getPM25ResultSetMetaData() {
		return getTableStruct(getConn(), QUERYPM25STRUCT);
	}
	
}
