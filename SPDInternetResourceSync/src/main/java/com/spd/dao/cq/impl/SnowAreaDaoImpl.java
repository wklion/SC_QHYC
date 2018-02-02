package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 区域积雪过程
 * @author Administrator
 *
 */
public class SnowAreaDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_SnowArea where 1=2";

	private String QUERYEXISTEDDATA = "select id from t_SnowArea where StartTime ";

	private String ADDSNOW = "insert into t_SnowArea (%s) values (%s)";
	
	private String UPDATESNOW = "update t_SnowArea set StartTime = ?, EndTime = ?, MaxStations = ?, AvgDepth = ?, MaxDepth = ?, Strength = ? where id = ?";
	
	public HashMap<String, Object> getExistSnow(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_Snow where datetime = '" + datetime + "'";		
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
	public void insertSnowValue(List dataList) {
		//先判断重复
		insertBatch(ADDSNOW, dataList, getSnowResultSetMetaData());
	}
	

	public void update(List updateList) {
		updateBatch2(UPDATESNOW, updateList, getSnowResultSetMetaData(), new String[]{"StartTime", "EndTime", "MaxStations", "AvgDepth", "MaxDepth", "Strength"});
	}
	
	public int getExistData(String startTime) {
		String query = "select id from t_SnowArea where StartTime = '" + startTime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			int id = (Integer) (dataMap.get("id"));
			return id;
		}
		return -1;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getSnowResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
}
