package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 年度高温综合评估
 * @author Administrator
 *
 */
public class AreaHighTmpYearResultDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_AreaHighTmpYearResult where 1=2";

	private String ADDHOUTMPAVG = "insert into t_AreaHighTmpYearResult (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou() {
		String query = "select year, YHI from t_AreaHighTmpYearResult  order by datetime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public void updateOrInsert(HashMap dataMap, int year) {
		String query = "select * from t_AreaHighTmpYearResult where year = " + year;
		List list = query(getConn(), query, null);
		String updateSQL = "";
		if(list != null && list.size() > 0) {
			// update
			updateSQL = "update t_AreaHighTmpYearResult set YHI = " + dataMap.get("YHI") + " where year = " + year;
		} else {
			updateSQL = "insert into t_AreaHighTmpYearResult(year, YHI) values (" + year+ ", " + dataMap.get("YHI") + ")";
		}
		update(updateSQL);
	}
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistTemAvg(String StartTime, String EndTime) {
		//mysql
		String query = "select year, YHI from t_AreaHighTmpYearResult where StartTime = '" + StartTime + "' and EndTime = '" + EndTime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String) tempMap.get("datetime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
