package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 低温区域
 * @author Administrator
 *
 */
public class LowTmpAreaAlertDaoImpl extends BaseDao {
	
	private String QUERYSTRUCT = "select * from t_lowtmpareaalert where 1 = 2";

	private String ADDLOWTMPSTATION = "insert into t_lowtmpareaalert (%s) values (%s)";

	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertLowTmpStationValue(List dataList, String datetime) {
		List insertList = new ArrayList();
		//先按照StartTime, Station_Id_C判断是否已经存在，存在则修改，否则删除
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String startTime = (String) dataMap.get("StartTime");
			Integer id = getExistData(startTime, datetime);
			if(id == null) {
				insertList.add(dataMap);
			}
		}
		insertBatch(ADDLOWTMPSTATION, insertList, getResultSetMetaData());
	}
	
	public Integer getExistData(String startTime, String forecastDate) {
		String query = "select id from t_lowtmpareaalert where StartTime = '" + startTime + "' and ForecastDate = '" + forecastDate + "'";		
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			return (Integer)dataMap.get("id");
		}
		return null;
	}
	
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSTRUCT);
	}
	
}
