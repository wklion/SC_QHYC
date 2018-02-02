package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;

/**
 * 单站强降温
 * @author Administrator
 *
 */
public class StrongCoolingAreaDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_tem_avg where year = ";

	private String UPDATEDATA = "update t_strongcoolingarea set  StartTime =?, EndTime = ?, StationCnt = ?, MaxTmp = ?, MinTmp = ?, AvgTmp = ? where id = ?";
	
	private String INSERTDATA = "insert into t_strongcoolingarea (%s) values (%s) ";

	private String QUERYSURFCHNMULSTRUCT = "select * from t_strongcoolingarea where 1=2";

	public StrongCoolingAreaDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	public StrongCoolingAreaDaoImpl(){
		
	}
	
	public void delete(int id) {
		update("delete from t_strongcoolingarea where id = " + id);
	}
	
	public boolean isInStartArea(String datetime) {
		String query = "select starttime from t_strongcoolingarea where starttime = '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void insert(List dataList, String startTime) {
		//如果已经存在的话，则跳过
		HashMap dataMap = getDataByStartTime2(startTime);
		if(dataMap == null) {
			insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
		} else {
			HashMap map = (HashMap) dataList.get(0);
			map.put("id", dataMap.get("id"));
			List updateList = new ArrayList();
			updateList.add(map);
			updateBatch2(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "StationCnt", "MaxTmp", "MinTmp", "AvgTmp"});
		}
	}
	
	/**
	 * 判断是否存在指定时间开始的数据
	 * @param startTime
	 * @return
	 */
	public HashMap getDataByStartTimeNullEndTime(String startTime) {
		String query = "select id,date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime" +
				" from t_strongcoolingarea where StartTime = '" + startTime + "' and EndTime is null";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			return dataMap;
		}
		return null;
	}
	
	public HashMap getDataByStartTime(String startTime) {
		String query = "select id,date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime" +
				" from t_strongcoolingarea where StartTime = '" + startTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			return dataMap;
		}
		return null;
	}
	
	/**
	 * 判断是否存在指定时间开始的数据
	 * @param startTime
	 * @return
	 */
	public HashMap getDataByStartTime2(String startTime) {
		String query = "select id,date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime" +
				" from t_strongcoolingarea where StartTime = '" + startTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			return dataMap;
		}
		return null;
	}
	
	/**
	 * 判断是否存在指定时间结束的数据
	 * @param startTime
	 * @return
	 */
	public HashMap getDataByEndTime(String endTime) {
		String query = "select id, date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime" +
				" from t_strongcoolingarea where EndTime = '" + endTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0){
			HashMap dataMap = (HashMap) resultList.get(0);
			return dataMap;
		}
		return null;
	}
	
	public void update(List updateList, int id) {
		updateBatch2(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "StationCnt", "MaxTmp", "MinTmp", "AvgTmp"});
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
