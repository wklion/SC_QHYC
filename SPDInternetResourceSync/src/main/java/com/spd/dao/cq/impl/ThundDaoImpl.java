package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

/**
 * 雷暴
 * @author Administrator
 *
 */
public class ThundDaoImpl extends BaseDao {
	
	private String QUERYTHUNDSTRUCT = "select * from t_thund where 1=2";

	private String ADDSTHUND = "insert into t_thund (%s) values (%s)";
	
	public HashMap<String, Object> getExistThund(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_thund where datetime = '" + datetime + "'";		
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
	 * 从t_surf_chn_mul_day中查询得到数据
	 * @return
	 */
	public List<Map<String, Object>> getAllThund() {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String query = "select count(1), Thund, THUND_OTime, date_format(datetime, '%Y-%m-%d') as datetime, Station_Id_C, Station_Name, " +
						"Lon, Lat from t_surf_chn_mul_day where thund != 0 " +  
						"group by Thund, THUND_OTime, Datetime, Station_Id_C, Station_Name, Lon, Lat" + 
						" having count(1) >= 1";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				Map dataMap = new HashMap();
				HashMap tempMap = (HashMap) list.get(i);
				String Thund = (String) tempMap.get("Thund");
				String THUND_OTime = (String) tempMap.get("THUND_OTime");
				String datetime = (String) tempMap.get("datetime") + " 00:00:00";
				String Station_Id_C = (String) tempMap.get("Station_Id_C");
				String Station_Name = (String) tempMap.get("Station_Name");
				Double Lon = (Double) tempMap.get("Lon");
				Double Lat = (Double) tempMap.get("Lat");
				dataMap.put("Thund", Integer.parseInt(Thund));
				dataMap.put("THUND_OTime", THUND_OTime);
				dataMap.put("datetime", datetime);
				dataMap.put("Station_Id_C", Station_Id_C);
				dataMap.put("Station_Name", Station_Name);
				dataMap.put("Lon", Lon);
				dataMap.put("Lat", Lat);
				dataList.add(dataMap);
			}
		}
		return dataList;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertThundValue(List dataList) {
		//先判断重复
		insertBatch(ADDSTHUND, dataList, getThundResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistThund(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_thund where year >= " + startYear + " and year <= " + endYear;		
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
	public ResultSetMetaData getThundResultSetMetaData() {
		return getTableStruct(getConn(), QUERYTHUNDSTRUCT);
	}
	
}
