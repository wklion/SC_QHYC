package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

public class RainStaticDao extends BaseDao {
	
	private String tableName;
	
	private String QUERYRAIN1HOURSTRUCT = "select * from t_rainhouraccumulate where 1 = 2";
	
	private String ADDRAINHOUR = "insert into  t_rainhouraccumulate(%s) values (%s)";

	private String UPDATERAINHOUR= "update t_rainhouraccumulate set rain1hours=?, rain3hours=?, rain6hours=?, rain12hours=?, " +
			"rain24hours=?, rain48hours=?, rain96hours=?, rain120hours=?, rain144hours=? where datetime=? and stationnum = ?";
	
//	public String getTableName() {
//		return tableName;
//	}
//
//	public void setTableName(String tableName) {
//		this.tableName = tableName;
//	}

	public RainStaticDao() {
		
	}
	
//	public RainStaticDao(String tableName) {
//		this.tableName = tableName;
//		QUERYRAIN1HOURSTRUCT = "select * from " + tableName + " where 1 = 2";
//		ADDRAINHOUR = "insert into " + tableName + "(%s) values (%s)";
//		UPDATERAINHOUR = "update " + tableName + " set precipitation=? where stationnum=? and datetime=?";
//	}
	
	public HashMap<String, Double> getStaticExistData(Date endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endTimeStr = sdf.format(endTime);
		String query = "select stationnum, datetime, rain1hours from t_rainhouraccumulate where datetime = '" + endTimeStr + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Double> hashMap = new HashMap<String, Double>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				Timestamp time = (Timestamp) tempMap.get("datetime");
				String key = (String) tempMap.get("stationnum");
				hashMap.put(key, ((java.math.BigDecimal)tempMap.get("rain1hours")).doubleValue());
			}
		}
		return hashMap;
	}
	
	/**
	 * 获取已经存在的数据
	 * @return
	 */
	public HashMap<String, Double> getExistData(Date endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endTimeStr = sdf.format(endTime);
		String query = "select stationnum, datetime, precipitation from " + tableName + " where datetime = '" + endTimeStr + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Double> hashMap = new HashMap<String, Double>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				Timestamp time = (Timestamp) tempMap.get("datetime");
				String key = (String) tempMap.get("stationnum") + "_"+ sdf.format(new Date(time.getTime()));
				hashMap.put(key, (Double)tempMap.get("precipitation"));
			}
		}
		return hashMap;
	}
	
	/**
	 * 添加数据
	 * @param dataList
	 */
	public void insertt_rain1hourValue(List dataList) {
		insertBatch(ADDRAINHOUR, dataList, getRain1HourResultSetMetaData());
	}
	
	/**
	 * 修改
	 * @param dataList
	 */
	public void updatet_rain1hourValue(List dataList) {
		ArrayList list = new ArrayList();
		for(int i=0; i<dataList.size(); i++) {
			Map map = (Map)dataList.get(i);
			Object[] params = new Object[map.size()];
			params[0] = map.get("rain1hours");
			params[1] = map.get("rain3hours");
			params[2] = map.get("rain6hours");
			params[3] = map.get("rain12hours");
			params[4] = map.get("rain24hours");
			params[5] = map.get("rain48hours");
			params[6] = map.get("rain96hours");
			params[7] = map.get("rain120hours");
			params[8] = map.get("rain144hours");
			params[9] = map.get("datetime");
			params[10] = map.get("stationnum");
			list.add(params);
		}
//		insertBatch(ADDRAINHOUR, dataList, getRain1HourResultSetMetaData());
		insertOrUpdate(UPDATERAINHOUR, list);
	}
	
	/**
	 * 查询t_rainhouraccumulate表结构
	 * @return
	 */
	public ResultSetMetaData getRain1HourResultSetMetaData() {
		return getTableStruct(getConn(), QUERYRAIN1HOURSTRUCT);
	}
}
