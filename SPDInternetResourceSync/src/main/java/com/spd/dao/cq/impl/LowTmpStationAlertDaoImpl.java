package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

/**
 * 低温单站预警
 * @author Administrator
 *
 */
public class LowTmpStationAlertDaoImpl extends BaseDao {
	
	private String QUERYSTRUCT = "select * from t_lowtmpstationalert where 1=2";

	private String ADDLOWTMPSTATION = "insert into t_lowtmpstationalert (%s) values (%s)";

	
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertLowTmpStationValue(List dataList, String forecastDate) {
		List insertList = new ArrayList();
		Set<String> existStation_Id_C = getExistStation(forecastDate);
		if(existStation_Id_C == null) return;
		//先按照StartTime, Station_Id_C判断是否已经存在，存在则修改，否则删除
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			if(existStation_Id_C.contains(station_Id_C)) {
				continue;
			} else {
				insertList.add(dataMap);
			}
		}
		insertBatch(ADDLOWTMPSTATION, insertList, getResultSetMetaData());
	}
	
	public Set<String> getExistStation(String forecastDate) {
		Set<String> existStationSet = new HashSet<String>();
		String query = "select Station_Id_C from t_lowtmpstationalert where ForecastDate = '" + forecastDate + "'";		
		List list = query(getConn(), query, null);
		if(list == null || list.size() == 0) return existStationSet;
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String station_Id_C = (String)dataMap.get("Station_Id_C");
			existStationSet.add(station_Id_C);
		}
		return existStationSet;
	}
	
	public int getCntByDateTime(String datetime, String forecastDatetime) {
		String query = "select count(1) as cnt from t_lowtmpstationalert where StartTime <= '" + datetime + "' and " +
				"EndTime >= '" + datetime + "' and ForecastDate = '" + forecastDatetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return -1;
		HashMap dataMap = (HashMap) resultList.get(0);
		return ((Long) dataMap.get("cnt")).intValue();
	}
	
	public String getEndTimeByForecastTime(String forecastTime) {
		String query = "select date_format(EndTime, '%Y-%m-%d') as EndTime from t_lowtmpstationalert where ForecastDate = '" + forecastTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap dataMap = (HashMap) resultList.get(0);
		return (String) dataMap.get("EndTime");
	}
	public Object[] caleDaysAndAnomalys(String startTime, String endTime, String forecastDatetime) {
		String query = "select count(1) as cnt, sum(Anomaly) as sumAnomaly from t_lowtmpstationalert where " +
				"(StartTime >= '" + startTime + "' and EndTime <= '" + endTime + "') or " + 
				" (StartTime <= '" + startTime + "' and EndTime >= '" + startTime + "') or " + 
				" (StartTime <= '" + endTime + "' and EndTime >= '" + endTime + "') or " + 
				" (StartTime <= '" + startTime + "' and EndTime >= '" + endTime + "') and ForecastDate = '" + forecastDatetime + "'" ;
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap dataMap = (HashMap) resultList.get(0);
		Integer cnt = ((Long) dataMap.get("cnt")).intValue();
		Double sumAnomaly = (Double) dataMap.get("sumAnomaly");
		return new Object[]{cnt, sumAnomaly};
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSTRUCT);
	}
	
}
