package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class GXShuiKuDao extends BaseDao {

	//添加水库水情
	private String ADDRIVERREGIMENINFO = "insert into t_reservoirregimeninfo (%s) values (%s)";
	//获取水库水情表结构
	private String QUERYRIVERREGIMENINFO = "select * from t_reservoirregimeninfo where 1=2";
	
	/**
	 * 查询已经存在的水库水情
	 * @param TM
	 * @return
	 */
	public HashMap<String, Object> getExistRiverRegimenInfo(String TMStart, String TMEnd) {
		String query = "select date_format(DT,'%Y-%m-%d %T') as DT, reservoirName from t_reservoirregimeninfo where  DT >= '" + TMStart + "' and DT <= '" + TMEnd + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("DT") + "_" + tempMap.get("reservoirName");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_reservoirregimeninfo中插入数据
	 * @param dataList
	 */
	public void insertRiverRegimenValue(List dataList) {
		insertBatch(ADDRIVERREGIMENINFO, dataList, getRiverRegimenResultSetMetaData());
	}
	
	/**
	 * 查询t_riverregimeninfo表结构
	 * @return
	 */
	public ResultSetMetaData getRiverRegimenResultSetMetaData() {
		return getTableStruct(getConn(), QUERYRIVERREGIMENINFO);
	}
}
