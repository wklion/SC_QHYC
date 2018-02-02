package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 干旱预警
 * @author Administrator
 *
 */
public class MCIAreaAlertDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_mciareaalert where 1=2";

	private String ADDHOUTMPAVG = "insert into t_mciareaalert (%s) values (%s)";
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValues(List dataList, String startTime, String datetime) {
		HashMap<String, Object> existData = getExistData(startTime, datetime);
		for(int i = dataList.size() - 1; i >= 0; i--) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String forecastDate = (String) dataMap.get("ForecastDate");
			String key = startTime + "_" + forecastDate;
			if(existData.containsKey(key)) {
				dataList.remove(i);
			}
		}
		insertBatch(ADDHOUTMPAVG, dataList, getMCIAreaAlertResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistData(String startTime, String datetime) {
		String query = "select date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(ForecastDate, '%Y-%m-%d %T') as ForecastDate" +
				" from t_mciareaalert where ForecastDate = '" + datetime + "' and StartTime = '" + startTime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String forecastDate = (String) tempMap.get("ForecastDate");
				hashMap.put(startTime + "_" + forecastDate, "");
			}
		}
		return hashMap;
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getMCIAreaAlertResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
