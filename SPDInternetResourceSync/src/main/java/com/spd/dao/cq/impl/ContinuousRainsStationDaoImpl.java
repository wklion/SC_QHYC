package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;

/**
 * 连阴雨单站统计表
 * @author Administrator
 *
 */
public class ContinuousRainsStationDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_continuousrainsstation where year = ";

	private String UPDATEDATA = "update t_continuousrainsstation set StartTime = ?, EndTime = ?, NoSunDays = ?, RainDays = ?, Pre = ? where id = ?";
	
	private String INSERTDATA = "insert into t_continuousrainsstation (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_continuousrainsstation where 1=2";
	
	public ContinuousRainsStationDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	public ContinuousRainsStationDaoImpl() {
		
	}
	
	/**
	 * 根据开始时间，取到已经存在的连阴雨
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List getExistDataByTimes(String startTime) {
		String query = "select id, date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime, Station_Id_C from t_continuousrainsstation" +
				" where StartTime <= '" + startTime + "' and EndTime >= '" + startTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			return resultList;
		}
		return null;
	}
	
	public void insert(List dataList) {
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
		
	}
	
	public void insertOrUpdate(List dataList) {
		//先delete已经存在的
		List updateList = new ArrayList();
		List insertList = new ArrayList();
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			if(dataMap == null) continue;
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Integer id = getListByTimes(startTime, endTime, station_Id_C);
			if(id != null) {
				dataMap.put("id", id);
				updateList.add(dataMap);
			} else {
				insertList.add(dataMap);
			}
		}
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		updateBatch3(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "NoSunDays", "RainDays", "Pre", "id"});
	}
	
	public Integer getListByTimes(String startTime, String endTime, String station_Id_C) {
//		String query = "select id from t_continuousrainsstation where startTime >= '" + startTime + "' and EndTime <= '" + endTime + "' and station_id_c = '" + station_Id_C + "'";
		String query = "select id from t_continuousrainsstation where station_id_c = '" + station_Id_C + "' " +
				" and ((startTime >= '" + startTime + "' and startTime <= '" + endTime + "')" +
				" or (endTime >= '" + startTime + "' and endTime <= '" + endTime + "') " +
				" or (startTime <= '" + startTime + "' and endTime >= '" + endTime + "') " +
				" or (startTime >= '" + startTime + "' and endTime <= '" + endTime + "')) ";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		return (Integer)((HashMap) resultList.get(0)).get("id");
	}
	/**
	 * 分站，返回最新的数据
	 * @return
	 */
	public List getLastedData(String datetime) {
		String query = "select max(date_format(EndTime, '%Y-%m-%d')) as EndTime, Station_id_C from t_continuousrainsstation where EndTime < '" + datetime + "' group by station_id_C";
		List resultList = query(getConn(), query, null);
		return resultList;
	}
	
	public List getDataListByTimes(String startTime, String endTime) {
		String query = "select Station_Id_C, date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(EndTime, '%Y-%m-%d %T') as EndTime, NoSunDays, RainDays, Pre" +
					" from t_continuousrainsstation where StartTime >= '" + startTime + "' and EndTime <= '" + endTime + "'";
		List resultList = query(getConn(), query, null);
		return resultList;
	}
	
	public String getLastedDateByStartTime(String startTime) {
		String query = "select max(date_format(EndTime, '%Y-%m-%d')) as EndTime from t_continuousrainsstation where startTime = '" + startTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap resultMap = (HashMap) resultList.get(0);
			return (String) resultMap.get("EndTime");
		}
		return null;
	}
	
	public String getDataByTimesRange(String startTime, String endTime) {
		String query = "select max(date_format(EndTime, '%Y-%m-%d')) as EndTime from t_continuousrainsstation" +
				//" where startTime >= '" + startTime + "' and startTime <= '" + endTime + "'";
				" where (startTime <= '" + startTime + "' and endTime >= '" + startTime + "') || (" +
						" startTime <= '" + endTime + "' and endTime >= '" + endTime + "') || (" + 
						" startTime >= '" + startTime + "' and endTime <= '" + endTime + "') || (" +
						 "startTime <= '" + startTime + "' and endTime >= '" + endTime + "')";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap resultMap = (HashMap) resultList.get(0);
			return (String) resultMap.get("EndTime");
		}
		return null;
	}
	
	/**
	 * 查询同时发生连阴雨的站数
	 * @param datetime
	 * @return
	 */
	public int getStationCntsByDatetime(String datetime) {
		String query = "select count(1) as cnt from t_continuousrainsstation where startTime <='" + datetime + "' and endTime >= '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap resultMap = (HashMap) resultList.get(0);
			return ((Long)(resultMap.get("cnt"))).intValue();
		}
		return -1;
	}
	
	public List getDataByEndTime(String endTime) {
		String query = "select id, date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(EndTime, '%Y-%m-%d %T') as EndTime, NoSunDays, RainDays, Pre, " +
				" Station_Id_C from t_continuousrainsstation where EndTime = '" + endTime + "'";
		List resultList = query(getConn(), query, null);
		return resultList;
	}
	
	public List getListByTimes(String startTime, String endTime) {
		String query = "select id, date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(EndTime, '%Y-%m-%d %T') as EndTime, NoSunDays, RainDays, Pre, " +
//		" Station_Id_C from t_continuousrainsstation where startTime >= '" + startTime + "' and EndTime <= '" + endTime + "'";
			" Station_Id_C from t_continuousrainsstation where " + 
			"(startTime <= '" + startTime + "' and EndTime >= '" + endTime + "') || " +  
			"(startTime >= '" + startTime + "' and EndTime <= '" + endTime + "') || " + 
			"(startTime <= '" + startTime + "' and EndTime >= '" + startTime + "') || " + 
			"(startTime <= '" + endTime + "' and EndTime >= '" + endTime + "') " ;
		List resultList = query(getConn(), query, null);
		return resultList;
	}
	public void update(List updateDataList) {
		updateBatch3(UPDATEDATA, updateDataList, getSurfChnMulResultSetMetaData(), new String[]{"EndTime", "NoSunDays", "RainDays", "Pre", "id"});
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
