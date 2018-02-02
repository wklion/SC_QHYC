package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 低温单站
 * @author Administrator
 *
 */
public class LowTmpStationDaoImpl extends BaseDao {
	
	private String QUERYSTRUCT = "select * from t_lowtmpstation where 1=2";

	private String ADDLOWTMPSTATION = "insert into t_lowtmpstation (%s) values (%s)";

	private String UPDATE = "update t_lowtmpstation set EndTime = ?, PersistHous = ?, AvgTmp = ?, Anomaly = ? where id = ?";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getFog(int startYear, int endYear) {
		String query = "select Station_Id_C,Station_Id_d, Station_Name, year, Lon, Lat, Fog, Fog_OTime, VIS_Min, VIS_Min_OTime, RHU_Avg, RHU_Min, RHU_Min_OTIME, datetime,Mist from t_surf_chn_mul_day " +
				"where year >= " + startYear + " and year <= " + endYear + " and Fog != 0";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getFog(String datetime) {
		String query = "select Station_Id_C,Station_Id_d, Station_Name, year, Lon, Lat, Fog, Fog_OTime, VIS_Min, VIS_Min_OTime, RHU_Avg, RHU_Min, RHU_Min_OTIME, datetime, Mist from t_surf_chn_mul_day " +
				"where datetime = '" + datetime + "' and Fog != 0";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public HashMap<String, Object> getExistFog(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_fog where datetime = '" + datetime + "'";		
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
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertLowTmpStationValue(List dataList) {
		List updateList = new ArrayList();
		List insertList = new ArrayList();
		//先按照StartTime, Station_Id_C判断是否已经存在，存在则修改，否则删除
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			Integer persistHous = (Integer) dataMap.get("PersistHous");
			Double avgTmp = (Double) dataMap.get("AvgTmp");
			Double anomaly = (Double) dataMap.get("Anomaly");
			Integer id = getExistData(station_Id_C, startTime.substring(0, 10));
			if(id != null) {
				HashMap updateMap = new HashMap();
				updateMap.put("id", id);
				updateMap.put("EndTime", endTime);
				updateMap.put("PersistHous", persistHous);
				updateMap.put("AvgTmp", avgTmp);
				updateMap.put("Anomaly", anomaly);
				updateMap.put("Station_Id_C", station_Id_C);
				updateList.add(updateMap);
			} else {
				insertList.add(dataMap);
			}
		}
		insertBatch(ADDLOWTMPSTATION, insertList, getResultSetMetaData());
		updateBatch2(UPDATE, updateList, getResultSetMetaData(), new String[]{"EndTime", "PersistHous", "AvgTmp", "Anomaly"});
	}
	
	public Integer getExistData(String station_Id_C, String startTime) {
		String query = "select id from t_lowtmpstation where Station_Id_C = '" + station_Id_C + "' and StartTime = '" + startTime + "'";		
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			return (Integer)dataMap.get("id");
		}
		return null;
	}
	
	public int getCntByDateTime(String datetime) {
		String query = "select count(1) as cnt from t_lowtmpstation where StartTime <= '" + datetime + "' and " +
				"EndTime >= '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return -1;
		HashMap dataMap = (HashMap) resultList.get(0);
		return ((Long) dataMap.get("cnt")).intValue();
	}
	
	public Object[] caleDaysAndAnomalys(String startTime, String endTime) {
		String query = "select count(1) as cnt, sum(Anomaly) as sumAnomaly from t_lowtmpstation where " +
				"(StartTime >= '" + startTime + "' and EndTime <= '" + endTime + "') or " + 
				" (StartTime <= '" + startTime + "' and EndTime >= '" + startTime + "') or " + 
				" (StartTime <= '" + endTime + "' and EndTime >= '" + endTime + "') or " + 
				" (StartTime <= '" + startTime + "' and EndTime >= '" + endTime + "') "; 
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
