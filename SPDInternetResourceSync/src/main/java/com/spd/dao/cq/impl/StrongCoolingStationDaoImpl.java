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
public class StrongCoolingStationDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_tem_avg where year = ";

	private String UPDATEDATA = "update t_StrongCoolingStation set Station_Id_c = ?, StartTime =?, EndTime = ?, CoolTmp = ?, Cool72HTmp = ?, level = ? where id = ?";
	
	private String INSERTDATA = "insert into t_StrongCoolingStation (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_StrongCoolingStation where 1=2";

	public StrongCoolingStationDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	public StrongCoolingStationDaoImpl(){
		
	}
	
	/**
	 * 根据年份查询已经存在的数据
	 * @param year
	 * @return
	 */
	public HashMap getExistData() {
		List list = query(getConn(), QUERYEXISTDATA, null);
		Set<String> existedStationSet = new HashSet<String>();
		HashMap<String, Integer> existedMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String)tempMap.get("Station_Id_C");
				int id = (Integer) tempMap.get("id");
				existedMap.put(key, id);
//				existedStationSet.add(key);
			}
		}
		return existedMap;
	}
	
	private HashMap getDataByTime(String datetime) {
		String query = "select id, Station_Id_c from t_StrongCoolingStation where EndTime = '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		HashMap resultMap = new HashMap();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_c = (String) dataMap.get("Station_Id_c");
			int id = (Integer) dataMap.get("id");
			resultMap.put(station_Id_c, id);
		}
		return resultMap;
	}
	
	private HashMap getExistDataByTime(String datetime) {
		String query = "select date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(EndTime, '%Y-%m-%d %T') as EndTime, " +
				"Station_Id_C from t_StrongCoolingStation where EndTime = '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		HashMap resultMap = new HashMap();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			String StartTime = (String) dataMap.get("StartTime");
			String EndTime = (String) dataMap.get("EndTime");
			resultMap.put(station_Id_C + "_" + StartTime + "_" + EndTime, "");
		}
		return resultMap;
	}
	
	public void insert (List dataList, String datetime) {
		//对于前面和现在数据连续的记录，要修改。
		//1. 查询datetime的头一天的记录
		String preDateTime = CommonTool.addDays(datetime, -1);
		HashMap existStationMap = getDataByTime(preDateTime);
		List updateList = new ArrayList();
		List insertList = new ArrayList();
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			if(existStationMap.containsKey(station_Id_C)) {
				dataMap.put("id", existStationMap.get(station_Id_C));
				updateList.add(dataMap);
			} else {
				insertList.add(dataMap);
			}
		}
		//再对一样的记录进行过滤（站号，开始，结束时间一样的）
		HashMap existMap = getExistDataByTime(datetime);
		for(int i = insertList.size() - 1; i >= 0; i--) {
			HashMap dataMap = (HashMap) insertList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			String StartTime = (String) dataMap.get("StartTime");
			String EndTime = (String) dataMap.get("EndTime");
			String key = station_Id_C + "_" + StartTime + "_" + EndTime;
			if(existMap.containsKey(key)) {
				insertList.remove(i);
			}
		}
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		updateBatch2(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"Station_Id_C", "StartTime", "EndTime", "CoolTmp", "Cool72HTmp", "level"});
	}
	
	/**
	 * 根据时间，查询对应的温度
	 * @param datetime
	 * @return
	 */
	public LinkedHashMap<String, Double> getTemByTime(String datetime) {
		int year = Integer.parseInt(datetime.substring(0, 4));
		String item = "m" + datetime.substring(4, 6) + "d" + datetime.substring(8, 10);
		String query = "select " + item + ", Station_Id_C, year from t_tem_avg where year = " + year + " and Station_Id_C like '5%'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		LinkedHashMap<String, Double> resultMap = new LinkedHashMap<String, Double>();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Double pre = (Double) dataMap.get(item);
			resultMap.put(station_Id_C, pre);
		}
		return resultMap;
	}
	
	public boolean isAreaStrongCooling(String datetime) {
		String query = "select count(1) as cnt from t_strongcoolingstation where StartTime <= '" + datetime + "' and EndTime >= '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return false;
		HashMap dataMap = (HashMap) resultList.get(0);
		Integer cnt = ((Long) dataMap.get("cnt")).intValue();
		if(cnt >= 7) return true;
		return false;
	}
	
	public int getStationCntByTimes(String datetime) {
		String query = "select count(1) as cnt from t_strongcoolingstation where StartTime <= '" + datetime + "' and EndTime >= '" + datetime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return -1;
		HashMap dataMap = (HashMap) resultList.get(0);
		Integer cnt = ((Long) dataMap.get("cnt")).intValue();
		return cnt;
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
	public Integer getStationCntByTime(String startTime, String endTime) {
		String query = "select count(1) as cnt from t_strongcoolingstation where " + 
		" (StartTime <= '" + startTime + "' and EndTime >= '" + startTime + "') " + 
		" or (StartTime <= '" + endTime + "' and EndTime >= '" + endTime + "') " + 
		" or (StartTime <= '" + startTime + "' and EndTime >= '" + endTime + "') " + 
		" or (StartTime >= '" + startTime + "' and EndTime <= '" + endTime + "') ";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return 0;
		HashMap dataMap = (HashMap) resultList.get(0);
		Integer cnt = ((Long) dataMap.get("cnt")).intValue();
		return cnt;
	}
	
	public Double[] getTmpByTime(String startTime, String endTime) {
		System.out.println("startTime:" + startTime + ", endTime" + endTime);
		String query = "select max(CoolTmp) as max, min(CoolTmp) as min, avg(CoolTmp) as avg from t_strongcoolingstation where " + 
		" (StartTime <= '" + startTime + "' and EndTime >= '" + startTime + "') " + 
		" or (StartTime <= '" + endTime + "' and EndTime >= '" + endTime + "') " + 
		" or (StartTime <= '" + startTime + "' and EndTime >= '" + endTime + "') " + 
		" or (StartTime >= '" + startTime + "' and EndTime <= '" + endTime + "') ";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap dataMap = (HashMap) resultList.get(0);
		return new Double[]{(Double) dataMap.get("max"), (Double) dataMap.get("min"), (Double) dataMap.get("avg")};
	}
}
