package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 区域高温预警结果表
 * @author Administrator
 *
 */
public class AreaHighTmpAlertDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_areahightmpalert where 1=2";

	private String ADDHOUTMPAVG = "insert into t_areahightmpalert (%s) values (%s)";
	
	private String UPDATE = "update t_areahightmpalert set StartTime = ?, EndTime = ?, ForecastDate = ?, RI = ?, DI = ?, level = ? where id = ?";
	
	public boolean isExist(String datetime) {
		String query = "select stationCnt, datetime from t_areahightmpalert  where datetime = '" + datetime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList, String forecastDate) {
		//先判断重复
		if(dataList == null || dataList.size() == 0) return;
		Integer id = getExistTemAvg(forecastDate);
		if(id == -1) {
			// insert
			insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
		} else {
			HashMap dataMap = (HashMap) dataList.get(0);
			HashMap updateMap = new HashMap();
			updateMap.put("StartTime", dataMap.get("StartTime"));
			updateMap.put("EndTime", dataMap.get("EndTime"));
			updateMap.put("ForecastDate", dataMap.get("ForecastDate"));
			updateMap.put("RI", dataMap.get("RI"));
			updateMap.put("DI", dataMap.get("DI"));
			updateMap.put("level", dataMap.get("level"));
			updateMap.put("id", id);
			List updateList = new ArrayList();
			updateList.add(updateMap);
			updateBatch2(UPDATE, updateList, getTmpAvgHouResultSetMetaData(), new String[]{"StartTime", "EndTime", "ForecastDate", "RI", "DI", "level"});
		}
		
	}
	
	public Integer getExistTemAvg(String datetime) {
		//mysql
		String query = "select id from t_areahightmpalert where ForecastDate = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			HashMap itemMap = (HashMap) list.get(0);
			Integer id = (Integer) itemMap.get("id");
			return id;
		}
		return -1;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
