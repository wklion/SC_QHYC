package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

/**
 * 小时降水求和
 * @author Administrator
 *
 */
public class HourRainSumDaoImpl extends BaseDao {

	private String QUERYSTRUCT = "select * from t_hourrainsum where 1=2";
	
	private String ADDDATA = "insert into t_hourrainsum (%s) values (%s)";
	
	public void insert(List dataList, String startTime, String endTime) {
		HashMap existMap = getExistData(startTime, endTime);
		for(int i = dataList.size() - 1; i >= 0; i--) {
			Map dataMap = (Map) dataList.get(i);
			String key = (String) dataMap.get("Station_Id_C") + "_" + (String) dataMap.get("StartTime") + "_" + (String) dataMap.get("EndTime");
			if(existMap.containsKey(key)) {
				dataList.remove(i);
			}
		}
		//插入数据
		insertBatch(ADDDATA, dataList, getResultSetMetaData());
	}
	
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSTRUCT);
	}
	
	private HashMap<String, Object> getExistData(String startTime, String endTime) {
		//mysql
		String query = "select Station_Id_C, date_format(StartTime, '%Y-%m-%d %T') as StartTime, date_format(EndTime, '%Y-%m-%d %T') as EndTime" +
				"  from t_hourrainsum where startTime = '" + startTime + "' and endTime = '" + endTime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("StartTime") + "_" + tempMap.get("EndTime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	public List queryDataByTimes(String startTime, String endTime) {
		String sql = "select Station_Id_C, sum(R1) as SumRain, count(1) as SumHours, '" + startTime + "' as StartTime, '" + endTime + "' as EndTime" +
				" from t_awshourrain where datetime >= '" + startTime + "'" +
				" and datetime <= '" + endTime + "' and R1 > 0 group by Station_id_C " +
				"union all " + 
				"select Station_Id_C, sum(R1) as SumRain, count(1) as SumHours, '" + startTime + "' as StartTime, '" + endTime + "' as EndTime" +
				" from t_mwshourrain where datetime >= '" + startTime + "'" +
				" and datetime <= '" + endTime + "' and R1 > 0 group by Station_id_C ";
		List resultList = query(getConn(), sql, null);
		return resultList;
	}
	
}
