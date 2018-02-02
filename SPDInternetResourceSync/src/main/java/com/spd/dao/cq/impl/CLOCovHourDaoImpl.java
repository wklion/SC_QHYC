package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 云量表（小时）
 * @author Administrator
 *
 */
public class CLOCovHourDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_CloCovHour where 1=2";

	private String ADDSDATA = "insert into t_CloCovHour (%s) values (%s)";
	
	public HashMap<String, Object> getExistData(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d %H')  as datetime, Hours from t_CloCovHour where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime") + " " + String.format("%02d", tempMap.get("Hours")) + ":00:00";
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValues(List dataList) {
		//先判断重复
		insertBatch(ADDSDATA, dataList, getResultSetMetaData());
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
}
