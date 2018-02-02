package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

public class TmpDao extends BaseDao {

	private String QUERYTMPSTRUCT = "select * from t_tmpavghour where 1 = 2";
	
	private String ADDTMP = "insert into  t_tmpavghour(%s) values (%s)";

	private String UPDATETMP= "update t_tmpavghour set tmp=? where datetime=? and stationnum = ?";

	private String QUERYEXISTDATA = "select * from t_tmpavghour where datetime=?";
	
	/**
	 * 添加数据
	 * @param dataList
	 */
	public void insertTmpValue(List dataList) {
		insertBatch(ADDTMP, dataList, getTmpResultSetMetaData());
	}
	
	/**
	 * 查询t_tmpavghour表结构
	 * @return
	 */
	public ResultSetMetaData getTmpResultSetMetaData() {
		return getTableStruct(getConn(), QUERYTMPSTRUCT);
	}
	
	/**
	 * 获取已经存在的数据
	 * @return
	 */
	public HashMap<String, Double> getExistData(Date endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endTimeStr = sdf.format(endTime);
		List list = query(getConn(), QUERYEXISTDATA, new Object[]{endTime});
		HashMap<String, Double> hashMap = new HashMap<String, Double>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				Timestamp time = (Timestamp) tempMap.get("datetime");
				String key = (String) tempMap.get("stationNum");
				hashMap.put(key, ((java.math.BigDecimal)tempMap.get("tmp")).doubleValue());
			}
		}
		return hashMap;
	}
	
	
	/**
	 * 修改
	 * @param dataList
	 */
	public void updateTmpValue(List dataList) {
		ArrayList list = new ArrayList();
		for(int i=0; i<dataList.size(); i++) {
			Map map = (Map)dataList.get(i);
			Object[] params = new Object[map.size()];
			params[0] = map.get("tmp");
			params[1] = map.get("datetime");
			params[2] = map.get("stationnum");
			list.add(params);
		}
//		insertBatch(ADDRAINHOUR, dataList, getRain1HourResultSetMetaData());
		insertOrUpdate(UPDATETMP, list);
	}
}
