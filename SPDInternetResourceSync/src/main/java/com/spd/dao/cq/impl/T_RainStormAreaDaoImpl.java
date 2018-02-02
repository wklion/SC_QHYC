package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 暴雨降水
 * @author Administrator
 *
 */
public class T_RainStormAreaDaoImpl extends BaseDao {

	private String INSERTDATA = "insert into t_rainstormarea (%s) values (%s) ";

	private String UPDATEDATA = "insert into t_rainstormarea (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_rainstormarea where 1=2";
	
	private String columnName = "Pre";
	
	
	public HashMap<String, Object> getExistDataTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_rainstormarea where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	
	public void insert(List dataList, String datetime, String type) {
		//先删除，再插入
		update("delete from t_rainstormarea where datetime = '" + datetime + "' and type = '" + type + "'");
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public void update(List updateDataList) {
		updateBatch2(UPDATEDATA, updateDataList, getSurfChnMulResultSetMetaData(), new String[]{columnName});
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
}
