package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 积雪
 * @author Administrator
 *
 */
public class SnowDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_Snow where 1=2";

	private String ADDSNOW = "insert into t_Snow (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getSnow(int startYear, int endYear) {
		String query = "select Station_Id_C,Station_Id_d, Station_Name, year, Lon, Lat, Snow, Snow_OTime, GSS, Snow_Depth, datetime from t_surf_chn_mul_day " +
				"where year >= " + startYear + " and year <= " + endYear + " and (GSS != 0 or snow != 0)";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getSnow(String datetime) {
		String query = "select Station_Id_C,Station_Id_d, Station_Name, year, Lon, Lat, Snow, Snow_OTime, GSS, Snow_Depth, datetime from t_surf_chn_mul_day " +
				"where datetime = '" + datetime + "' and (GSS != 0 or snow != 0 or GSS != 999999)";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getSnowDepthByTime(String datetime) {
		String query = "select Station_Id_C, Snow_Depth from t_snow where Datetime = '" + datetime + "' and (Snow_Depth > 0 and Snow_Depth < 9999 or GSS = 1)";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getSnowDepthByTimes(String startTime, String endTime) {
		String query = "select Station_Id_C, Snow_Depth, date_format(datetime, '%Y-%m-%d') as datetime from t_snow where" +
				" Datetime >= '" + startTime + "' and Datetime <='" + endTime + "' and (Snow_Depth > 0 and Snow_Depth < 9999 or GSS = 1) order by datetime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 查询最大积雪深度
	 * @return
	 */
	public double getMaxSnowDepth() {
		String query = "select max(Snow_Depth) as Snow_Depth from t_snow where Snow_Depth > 0 and Snow_Depth < 9999";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			Double depth = (Double) dataMap.get("Snow_Depth");
			return depth;
		}
		return 0;
	}
	
	public HashMap<String, Object> getExistSnow(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_Snow where datetime = '" + datetime + "'";		
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
	public void insertSnowValue(List dataList) {
		//先判断重复
		insertBatch(ADDSNOW, dataList, getSnowResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistSnow(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_Snow where year >= " + startYear + " and year <= " + endYear;		
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
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getSnowResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
}
