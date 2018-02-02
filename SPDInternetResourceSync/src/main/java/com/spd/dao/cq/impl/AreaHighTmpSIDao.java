package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 区域高温SI
 * @author Administrator
 *
 */
public class AreaHighTmpSIDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_AreaHighTmpSI where 1=2";

	private String ADDHOUTMPAVG = "insert into t_AreaHighTmpSI (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou(String startTime, String endTime) {
		String query = "select StartTime, EndTime from t_AreaHighTmpSI where StartTime = '" + startTime + "' and EndTime = '" + endTime + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getLevelGroup(String startTime, String endTime) {
		String query = "select count(1) as cnt, G from t_areahightmpsi where StartTime = '" + startTime +  "' and station_id_C like '5%' group by level";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public boolean deleteByTime(String startTime) {
		String query = "delete from t_areahightmpsi where StartTime = '" + startTime + "'";
		boolean flag = update(query);
		return flag;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistTemAvg(String startTime, String endTime) {
		//mysql
		String query = "select date_format(StartTime, '%Y-%m-%d') as StartTime, date_format(EndTime, '%Y-%m-%d') as EndTime, Station_Id_C, si from t_AreaHighTmpSI where StartTime = '" + startTime + "' and EndTime = '" + endTime +"'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String) tempMap.get("StartTime") + "_" + (String) tempMap.get("EndTime") + "_" + (String) tempMap.get("Station_Id_C");
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
