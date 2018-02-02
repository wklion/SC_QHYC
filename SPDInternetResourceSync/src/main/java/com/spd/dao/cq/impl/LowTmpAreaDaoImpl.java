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
public class LowTmpAreaDaoImpl extends BaseDao {
	
	private String QUERYSTRUCT = "select * from t_lowtmparea where 1 = 2";

	private String ADDLOWTMPSTATION = "insert into t_lowtmparea (%s) values (%s)";

	private String UPDATE = "update t_lowtmparea set EndTime = ?, SumStations = ?, SumAnomaly = ? where id = ?";
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertLowTmpStationValue(List dataList) {
		List updateList = new ArrayList();
		List insertList = new ArrayList();
		//先按照StartTime, Station_Id_C判断是否已经存在，存在则修改，否则删除
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			Integer sumStations = (Integer) dataMap.get("SumStations");
			Double sumAnomaly = (Double) dataMap.get("SumAnomaly");
			Integer id = getExistData(startTime);
			if(id != null) {
				HashMap updateMap = new HashMap();
				updateMap.put("id", id);
				updateMap.put("EndTime", endTime + " 00:00:00");
				updateMap.put("SumStations", sumStations);
				updateMap.put("SumAnomaly", sumAnomaly);
				updateList.add(updateMap);
			} else {
				dataMap.put("StartTime", startTime + " 00:00:00");
				dataMap.put("EndTime", endTime + " 00:00:00");
				insertList.add(dataMap);
			}
		}
		insertBatch(ADDLOWTMPSTATION, insertList, getResultSetMetaData());
		updateBatch2(UPDATE, updateList, getResultSetMetaData(), new String[]{"EndTime", "SumStations", "SumAnomaly"});
	}
	
	public Integer getExistData(String startTime) {
		String query = "select id from t_lowtmparea where StartTime = '" + startTime + "'";		
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
