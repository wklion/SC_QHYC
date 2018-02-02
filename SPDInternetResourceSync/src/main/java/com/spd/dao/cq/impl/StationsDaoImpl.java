package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

/**
 * 站点
 * @author Administrator
 *
 */
public class StationsDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_station where 1=2";

	private String ADDSTATION = "insert into t_station (%s) values (%s)";
	
	public Set<String> getExistStation() {
		//mysql
		String query = "select Station_Id_C from t_station";		
		List list = query(getConn(), query, null);
		Set<String> stationSet = new HashSet<String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String) tempMap.get("Station_Id_C");
				stationSet.add(key);
			}
		}
		return stationSet;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertStationValue(List dataList) {
		//先判断重复
		insertBatch(ADDSTATION, dataList, getStationResultSetMetaData());
	}
	
	/**
	 * 查询t_max_tmp表结构
	 * @return
	 */
	public ResultSetMetaData getStationResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
}
