package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;

/**
 * 天气现象统计表
 * @author Administrator
 *
 */
public class WepDaoImpl extends BaseDao {

	private String QUERYHAILTRUCT = "select * from t_wep where 1=2";

	private String ADDHAIL = "insert into t_wep (%s) values (%s)";

	private String UPDATEDATA = "update t_wep set WEP_Record = ? where id = ?";
	
	public HashMap<String, Object> getExistWep(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_wep where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				int id = (Integer) tempMap.get("id");
				hashMap.put(key, id);
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
	public void insertWepValue(List dataList) {
		//先判断重复
		insertBatch(ADDHAIL, dataList, geHailResultSetMetaData());
	}

	public void updateWepValue(List updateList) {
		updateBatch2(UPDATEDATA, updateList, geHailResultSetMetaData(), new String[]{"WEP_Record"});
	}
}
