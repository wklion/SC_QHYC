package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;
/**
 * 区域高温日值
 * @author Administrator
 *
 */
public class AreaHighTmpDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_AreaHighTmp where 1=2";

	private String ADDHOUTMPAVG = "insert into t_AreaHighTmp (%s) values (%s)";

	private String UPDATEHOUTMPAVG = "update t_AreaHighTmp set datetime = ?, stationCnt = ? where id = ?";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou() {
		String query = "select stationCnt, datetime from t_AreaHighTmp  order by datetime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getTmpAvgByTimes(String startTime, String endTime) {
		String query = "select stationCnt, datetime, date_format(datetime, '%Y-%m-%d') as endTime from t_AreaHighTmp where datetime >= '" + startTime + "' and datetime <= '" + endTime + "'  order by datetime desc";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public int isExist(String datetime) {
		String query = "select id from t_AreaHighTmp  where datetime = '" + datetime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			Map dataMap = (Map) list.get(0);
			return (Integer) dataMap.get("id");
		}
		return -1;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	public boolean updateData(HashMap dataMap) {
		List dataList = new ArrayList();
		dataList.add(dataMap);
		boolean flag = updateBatch2(UPDATEHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData(), new String[]{"datetime", "stationCnt"});
		return flag;
	}
	
	public HashMap<String, Object> getExistTemAvg(String datetime) {
		//mysql
		String query = "select stationCnt, datetime from t_AreaHighTmp where datetime = '" + datetime + "'";		
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
