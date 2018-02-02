package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 雾
 * @author Administrator
 *
 */
public class FogDaoImpl extends BaseDao {
	
	private String QUERYSNOWSTRUCT = "select * from t_fog where 1=2";

	private String ADDSNOW = "insert into t_fog (%s) values (%s)";
	
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
	public void insertFogValue(List dataList) {
		//先判断重复
		insertBatch(ADDSNOW, dataList, getFogResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistFog(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_fog where year >= " + startYear + " and year <= " + endYear;		
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
	public ResultSetMetaData getFogResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSNOWSTRUCT);
	}
	
}
