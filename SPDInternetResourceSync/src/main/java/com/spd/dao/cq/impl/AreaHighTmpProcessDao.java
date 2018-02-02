package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 区域高温日值
 * @author Administrator
 *
 */
public class AreaHighTmpProcessDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_AreaHighTmpProcess where 1=2";

	private String ADDHOUTMPAVG = "insert into t_AreaHighTmpProcess (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou(String startTime, String endTime) {
		String query = "select * from t_AreaHighTmpProcess where StartTime = '" + startTime + "' and EndTime = '" + endTime + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List getTmpByStartTime(String startTime) {
		String query = "select StartTime, EndTime, persistDays from t_AreaHighTmpProcess where StartTime = '" + startTime + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public boolean updateTmpByStartTime(String startTime, String endTime, int persistDays) {
		String query = "update t_AreaHighTmpProcess set StartTime = '" + startTime + "', EndTime = '" + endTime + "', persistDays = " + persistDays + " where startTime = '" + startTime + "'";
		boolean flag = update(query);
		return flag;
	}
	
	public List getALLData() {
		String query = "select StartTime, EndTime, persistDays from t_AreaHighTmpProcess";
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
	
	public HashMap<String, Object> getExistTemAvg(String datetime) {
		//mysql
		String query = "select StartTime, EndTime from t_AreaHighTmpProcess where datetime = '" + datetime + "'";		
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
