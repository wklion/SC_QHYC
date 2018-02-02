package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class GXShuiQingDao extends BaseDao {

	//添加江河水情
	private String ADDRIVERREGIMENINFO = "insert into t_riverregimeninfo (%s) values (%s)";
	//获取水库水情表结构
	private String QUERYRIVERREGIMENINFO = "select * from t_riverregimeninfo where 1=2";
	
	/**
	 * 查询已经存在的水情
	 * @param TM
	 * @return
	 */
	public HashMap<String, Object> getExistRiverRegimenInfo(String TMStart, String TMEnd) {
		String query = "select date_format(TM,'%Y-%m-%d %T') as TM, STCD from t_riverregimeninfo where  TM >= '" + TMStart + "' and TM <= '" + TMEnd + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("TM") + "_" + tempMap.get("STCD");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_riverregimeninfo中插入数据
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
