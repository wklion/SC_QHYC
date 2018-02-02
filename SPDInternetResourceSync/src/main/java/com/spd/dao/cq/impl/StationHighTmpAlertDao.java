package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 单站高温预警结果表
 * @author Administrator
 *
 */
public class StationHighTmpAlertDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_StationHighTmpAlert where 1=2";

	private String ADDHOUTMPAVG = "insert into t_StationHighTmpAlert (%s) values (%s)";
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList, String forecastDate) {
		//先判断重复
		List resultList = new ArrayList(); 
		if(dataList == null || dataList.size() == 0) return;
		HashSet<String> existStations = getExistTemAvg(forecastDate);
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			if(!existStations.contains(station_Id_C)) {
				resultList.add(dataMap);
			}
		}
		insertBatch(ADDHOUTMPAVG, resultList, getTmpAvgHouResultSetMetaData());
	}
	
	public HashSet<String> getExistTemAvg(String datetime) {
		//mysql
		String query = "select Station_Id_C from t_StationHighTmpAlert where ForecastDate = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashSet<String> existSet = new HashSet<String>();
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				HashMap itemMap = (HashMap) list.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				existSet.add(station_Id_C);
			}
		}
		return existSet;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
