package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class ProfessionForecastDao extends BaseDao {
	//添加风景区天气预报
	private String ADDFORECAST = "insert into t_ParkForecast (%s) values (%s)";
	//添加气象生活指数预报
	private String ADDMETEOINDEXLIFE = "insert into t_meteoindexlife (%s) values (%s)";
	//获取风景区天气预报表结构
	private String QUERYFORECASTSTRUCT = "select * from t_ParkForecast where 1=2";
	//互殴气象生活指数表结构
	private String QUERYMETEOINDEXLIFESTRUCT = "select * from t_meteoindexlife where 1=2";
	
	/**
	 * 查询风景区天气预报中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, String> getExistParkForecast(String forecastDate) {
		String query = "select date_format(forecastDate,'%Y-%m-%d %T') as forecastDate, CityCode, date_format(updateTime,'%Y-%m-%d %T') as updateTime from t_ParkForecast where  forecastDate = '" + forecastDate + "'";
		List list = query(getConn(), query, null);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("forecastDate") + "_" + tempMap.get("CityCode") + "_" + tempMap.get("updateTime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * 查询风景区天气预报中已经存在的数据
	 * @param forecastTime
	 * @param tabName
	 * @return
	 */
	public HashMap<String, String> getExistMeteoIndex(String forecastTime) {
		String query = "select forecastDate, stationNum from t_meteoindexlife where forecastTime = " + forecastTime;
		List list = query(getConn(), query, null);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("forecastTime") + "_" + tempMap.get("stationNum");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertParkForecastValue(List dataList) {
		insertBatch(ADDFORECAST, dataList, getParkForecastResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getParkForecastResultSetMetaData() {
		return getTableStruct(getConn(), QUERYFORECASTSTRUCT);
	}
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertMeteoIndexLifeValue(List dataList) {
		insertBatch(ADDFORECAST, dataList, getParkForecastResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getMeteoIndexLifeResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMETEOINDEXLIFESTRUCT);
	}
}
