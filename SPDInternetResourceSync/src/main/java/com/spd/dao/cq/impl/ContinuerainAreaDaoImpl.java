package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 区域连阴雨
 * @author Administrator
 *
 */
public class ContinuerainAreaDaoImpl extends BaseDao {

	private String INSERTDATA = "insert into t_continuerainarea (%s) values (%s) ";

	private String QUERYSURFCHNMULSTRUCT = "select * from t_continuerainarea where 1=2";
	
	public void insert(List dataList, String startTime) {
		//1. 先删除重复的
		String deletedata = "delete from  t_continuerainarea where StartTime = '" + startTime + "'";
		update(deletedata);
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public void update(List dataList, int id) {
		String updateSQL = "update t_continuerainarea set StartTime = ?, EndTime = ?, ProcessDays = ?, SumStations = ?, SumPre = ?, PreDays = ? where id = ?";
		updateBatch2(updateSQL, dataList, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "ProcessDays", "SumStations", "SumPre", "PreDays"});
	}
	
	public void insert(List dataList) {
		//1. 先删除重复的
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public int getDataByStartTime(String startTime) {
		String query = "select id, date_format(StartTime, '%Y-%m-%d') as startTime from t_continuerainarea where startTime = '" + startTime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			return (Integer)(dataMap.get("id"));
		}
		return -1;
	}
	
	public String getLastedTime() {
		String query = "select max(date_format(EndTime, '%Y-%m-%d')) as datetime from t_continuerainarea";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			String result = (String) ((HashMap) list.get(0)).get("datetime");
			return result;
		}
		return null;
	}
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
