package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 *  冰雹数据同步
 * @author Administrator
 *
 */
public class HailDaoImpl extends BaseDao {
	
	private String QUERYHAILTRUCT = "select * from t_hail where 1=2";

	private String ADDHAIL = "insert into t_hail (%s) values (%s)";
	
	public HashMap<String, Object> getExistHail(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_hail where datetime = '" + datetime + "'";		
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
	public ResultSetMetaData geHailResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHAILTRUCT);
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertHailValue(List dataList) {
		//先判断重复
		insertBatch(ADDHAIL, dataList, geHailResultSetMetaData());
	}
	
}
