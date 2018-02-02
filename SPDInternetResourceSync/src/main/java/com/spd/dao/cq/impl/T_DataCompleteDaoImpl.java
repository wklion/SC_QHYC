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
 * 数据完整度的更新
 * @author Administrator
 *
 */
public class T_DataCompleteDaoImpl extends BaseDao {

	private int year;
	

	private String UPDATEDATA = "update t_datacomplete set UpdateTime = ?, DataCount = ? where id = ?";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_datacomplete where 1=2";

	private String QUERYEXISTDATA = "select Station_Id_C, id from t_datacomplete";
	
	public void updateDataList(List updateList) {
		updateBatch2(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"UpdateTime", "DataCount"});
	}
	
	public HashMap getExistData() {
		List list = query(getConn(), QUERYEXISTDATA, null);
		Set<String> existedStationSet = new HashSet<String>();
		HashMap<String, Integer> existedMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String)tempMap.get("Station_Id_C");
				int id = (Integer) tempMap.get("id");
				existedMap.put(key, id);
			}
		}
		return existedMap;
	}
	
	/**
	 * 查询t_datacomplete中的站点
	 * @return
	 */
	public List<String> getAllStations() {
		List<String> resultList = new ArrayList<String>();
		String query = "select Station_Id_C from t_datacomplete";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String)tempMap.get("Station_Id_C");
				resultList.add(key);
			}
		}
		return resultList;
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
