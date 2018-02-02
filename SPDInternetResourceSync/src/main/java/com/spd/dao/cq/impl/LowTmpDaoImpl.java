package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 *  低温
 * @author Administrator
 *
 */
public class LowTmpDaoImpl extends BaseDao {
	
	private String QUERYMINTMPTRUCT = "select * from t_min_tmp where 1=2";

	private String ADDMINTMP = "insert into t_min_tmp (%s) values (%s)";
	
	private String UPDATEMINTMP = "update t_min_tmp set TEM_Min = ?, TEM_Min_OTime = ? where id = ?";
	
	public HashMap<String, Object> getExistMinTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_min_tmp where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertMinTmpValue(List dataList) {
		//先判断重复
		insertBatch(ADDMINTMP, dataList, getMinTmpResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistMinTmp(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_min_tmp where year >= " + startYear + " and year <= " + endYear;		
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
	 * 根据实况的开始时间，结束时间，预报的开始时间，结束时间，联合计算平均气温
	 * @param realStartTime
	 * @param realEndTime
	 * @param forecastStartTime
	 * @param forecastEndTime
	 * @return
	 */
	public HashMap<String, Double> getRealForecastHouAvgTmp(String realStartTime, String realEndTime, String forecastStartTime, String forecastEndTime) {
		String query  = "select Station_Id_C, date_format(Datetime, '%Y-%m-%d') as  Datetime, TEM_Min from t_min_tmp where Datetime >= '" + realStartTime + "' and Datetime <= '" + realEndTime + "' and Station_Id_C like '5%' " +
						" union all " +
						" select Station_Id_C, date_format(FutureDate, '%Y-%m-%d') as Datetime, MinTmp as TEM_Min from t_forecastdata where FutureDate >= '" + forecastStartTime + "' and FutureDate <= '" + forecastEndTime + "' and Station_Id_C like '5%' ";
		List resultList = query(getConn(), query, null);
		HashMap<String, Double> houSumTmpMap = new HashMap<String, Double>();
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		HashMap<String, Integer> stationCntMap = new HashMap<String, Integer>();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Integer existStationCnt = stationCntMap.get(station_Id_C);
			if(existStationCnt != null) {
				existStationCnt +=1;
			} else {
				existStationCnt = 1;
			}
			stationCntMap.put(station_Id_C, existStationCnt);
			Double TEM_Min = (Double) dataMap.get("TEM_Min");
			Double existTmp = houSumTmpMap.get(station_Id_C);
			if(existTmp != null) {
				TEM_Min += existTmp;
			}
			houSumTmpMap.put(station_Id_C, TEM_Min);
		}
		//计算每个站的候平均
		Iterator<String> it = houSumTmpMap.keySet().iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			Double houTmp = houSumTmpMap.get(station_Id_C);
			Integer stationCnt = stationCntMap.get(station_Id_C);
			if(stationCnt == null || stationCnt == 0) continue;
			Double avgHouTmp = houTmp / stationCnt;
			resultMap.put(station_Id_C, avgHouTmp);
		}
		return resultMap;
	}
	
	/**
	 * 查询t_max_tmp表结构
	 * @return
	 */
	public ResultSetMetaData getMinTmpResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMINTMPTRUCT);
	}
	
	public void update(List updateDataList) {
		updateBatch2(UPDATEMINTMP, updateDataList, getMinTmpResultSetMetaData(), new String[]{"TEM_Min", "TEM_Min_OTime"});
	}
}
