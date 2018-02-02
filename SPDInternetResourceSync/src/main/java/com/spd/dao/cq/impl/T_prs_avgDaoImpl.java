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
 * 平均气压统计
 * @author Administrator
 *
 */
public class T_prs_avgDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_prs_avg where year = ";

	private String UPDATEDATA = "update t_prs_avg set %columns = ? where id = ?";
	
	private String INSERTDATA = "insert into t_prs_avg (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_prs_avg where 1=2";
	
	public T_prs_avgDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	/**
	 * 根据年份查询已经存在的数据
	 * @param year
	 * @return
	 */
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
//				existedStationSet.add(key);
			}
		}
		return existedMap;
	}
	
	public void disposeDataList(List dataList) {
		List insertList = new ArrayList();
		List updateList = new ArrayList();
		
		HashMap<String, Integer> existData = getExistData();
		Set<String> existSet = existData.keySet();
		for (Map<String, Object> item : (ArrayList<Map<String, Object>>)dataList) {
			String station_id_c = (String) item.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_id_c);
			if(!isCQStation) continue;
			if(existSet.contains(station_id_c)) {
				//update
				item.put("id", existData.get(station_id_c));
				updateList.add(item);
			} else {
				//insert
				insertList.add(item);
			}
		}
		UPDATEDATA = UPDATEDATA.replaceAll("%year", year + "");
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		updateBatch(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData());
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
}
