package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 土壤水分
 * @author Administrator
 *
 */
public class AgmeSoilDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_agmesoil where 1=2";

	private String ADDDATA = "insert into t_agmesoil (%s) values (%s)";
	
	public HashMap<String, Object> getExistData(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime, Soil_Depth_BelS from t_agmesoil where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime") + "_" + tempMap.get("Soil_Depth_BelS");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValue(List dataList) {
		//先判断重复
		insertBatch(ADDDATA, dataList, getFogResultSetMetaData());
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getFogResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
}
