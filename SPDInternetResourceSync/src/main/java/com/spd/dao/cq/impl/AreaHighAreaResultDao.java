package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 高温综合结果表
 * @author Administrator
 *
 */
public class AreaHighAreaResultDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_AreaHighAreaResult where 1=2";

	private String ADDHOUTMPAVG = "insert into t_AreaHighAreaResult (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou() {
		String query = "select StartTime, EndTime, RI, persistDays from t_AreaHighAreaResult  order by datetime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getTmpAvgHouGroupByYear() {
		String query = "select DI, RI, persistDays,  date_format(StartTime, '%Y') as year from t_AreaHighAreaResult  order by StartTime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getTmpAvgByYear(int year) {
		String query = "select DI, RI, persistDays,  date_format(StartTime, '%Y') as year from t_AreaHighAreaResult where date_format(StartTime, '%Y') = " + year;
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	public void updateOrInsertData(String StartTime, String EndTime, int persistDays, Double RI, String level, Integer DI) {
		String query = "select StartTime, EndTime, RI, persistDays from t_AreaHighAreaResult where StartTime = '" + StartTime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			String updateSQL = "update t_AreaHighAreaResult set StartTime = '" + StartTime + "', EndTime = '" + 
			EndTime + "', RI = " + RI + ", persistDays = " + persistDays+ ", level = '" + level + "', DI = " + DI +
			" where StartTime = '" + StartTime + "'";
			update(updateSQL);
		} else {
			List dataList = new ArrayList();
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", StartTime);
			dataMap.put("EndTime", EndTime);
			dataMap.put("persistDays", persistDays);
			dataMap.put("RI", RI);
			dataMap.put("level", level);
			dataMap.put("DI", DI);
			dataList.add(dataMap);
			insertTemAvgHouValue(dataList);
		}
	}
	
	public HashMap<String, Object> getExistTemAvg(String StartTime, String EndTime) {
		//mysql
		String query = "select StartTime, EndTime, RI from t_AreaHighAreaResult where StartTime = '" + StartTime + "' and EndTime = '" + EndTime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String) tempMap.get("datetime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
