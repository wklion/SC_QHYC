package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;
/**
 * 区域站小时降水的相关操作，分年
 * @author Administrator
 *
 */
public class MWSYearHourDataDaoImpl extends BaseDao {

	private static String ADDHOURRAIN ;

	private static String QUERYHOURRAINSTRUCT;
	
	private static String tableName;
	
	public void  init(int year) {
		tableName = "t_mwshourrain" + year;
		ADDHOURRAIN = "insert into " + tableName + " (%s) values (%s)";
		QUERYHOURRAINSTRUCT = "select * from " + tableName + " where 1 = 2";
	}
	
	public HashMap<String, Object> getExistRain(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d %T') as datetime from " + tableName + " where datetime = '" + datetime + "'";		
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
	 * 获取全部区域站
	 * @param datetime
	 * @return
	 */
	public Set<String> getMWSStations() {
		String query = "select Station_Id_C from t_station where station_Id_C like 'A%'";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				stationSet.add(((String) tempMap.get("Station_Id_C")).trim());
			}
		}
		return stationSet;
	}
	
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOURRAIN, dataList, getAWSHourRainResultSetMetaData());
	}
	
	public ResultSetMetaData getAWSHourRainResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOURRAINSTRUCT);
	}
	
}
