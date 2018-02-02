package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class RainDao extends BaseDao {

	private static String QUERYRAIN1HOURSTRUCT = "select * from t_rain1hour where 1 = 2";
	
	private String ADDRAIN1HOUR = "insert into t_rain1hour(%s) values (%s)";
	
	/**
	 * 获取已经存在的数据
	 * @return
	 */
	public HashMap<String, String> getExistData(Date startTime, Date endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = sdf.format(startTime);
		String endTimeStr = sdf.format(endTime);
		String query = "select stationnum, datetime from t_rain1hour where datetime in( '" + startTimeStr + "','" + endTimeStr + "' )";
		List list = query(getConn(), query, null);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				Timestamp time = (Timestamp) tempMap.get("datetime");
				String key = (String) tempMap.get("stationnum") + "_"+ sdf.format(new Date(time.getTime()));
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * 添加数据
	 * @param dataList
	 */
	public void insertt_rain1hourValue(List dataList) {
		insertBatch(ADDRAIN1HOUR, dataList, getRain1HourResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getRain1HourResultSetMetaData() {
		return getTableStruct(getConn(), QUERYRAIN1HOURSTRUCT);
	}
}
