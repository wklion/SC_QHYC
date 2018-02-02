package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class DuanLinDao extends BaseDao {
	//添加0-6小时短时指导预报
	private String ADDFOREDS06 = "insert into t_ForeDS06 (%s) values (%s)";
	//获取0-6小时短时指导预报表结构
	private String QUERYFOREDS06STRUCT = "select * from t_ForeDS06 where 1=2";
	
	/**
	 * 查询风景区天气预报中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, String> getExistForeDS06(String productId) {
		String query = "select productId from t_ForeDS06 where productId = '" + productId + "'";
		List list = query(getConn(), query, null);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String) tempMap.get("productId");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertForeDS06Value(List dataList) {
		insertBatch(ADDFOREDS06, dataList, getForeDS06ResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getForeDS06ResultSetMetaData() {
		return getTableStruct(getConn(), QUERYFOREDS06STRUCT);
	}
	
}
