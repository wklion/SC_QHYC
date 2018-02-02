package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class MeteoIndexLifeDao extends BaseDao {

	//添加气象生活指数
	private String ADDMETEOINDEXLIFE = "insert into t_meteoindexlife (%s) values (%s)";
	//获取气象生活指数表结构
	private String QUERYMETEOINDEXLIFESTRUCT = "select * from t_meteoindexlife where 1=2";
	
	/**
	 * 查询风景区天气预报中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, Object> getExistMeteoIndexLife(String forecastTime) {
		String query = "select date_format(forecastTime,'%Y-%m-%d %T') as forecastTime, cityCode from t_meteoindexlife where  forecastTime = '" + forecastTime + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("forecastTime") + "_" + tempMap.get("cityCode");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertMeteoIndexLifeValue(List dataList) {
		insertBatch(ADDMETEOINDEXLIFE, dataList, getPM25ResultSetMetaData());
	}
	
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	public ResultSetMetaData getPM25ResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMETEOINDEXLIFESTRUCT);
	}
}
