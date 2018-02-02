package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;

/**
 * 月平均气温
 * @author Administrator
 *
 */
public class MonthAvgTmpDaoImpl extends BaseDao {
	
	private String QUERYSTRUCT = "select * from t_MonthAvgTmp where 1=2";

	private String ADDSDATA = "insert into t_MonthAvgTmp (%s) values (%s)";
	
	private String UPDATEDATA = "update t_MonthAvgTmp set Station_Id_C = ?, avgTmp = ?, year = ?, month = ? where id = ?";
	
	/**
	 * 处理数据
	 * @param dataList
	 */
	public void disposeDataList(List dataList, int year, int month) {
		List insertList = new ArrayList();
		List updateList = new ArrayList();
		
		HashMap<String, Integer> existData = getExistData(year, month);
		Set<String> existSet = existData.keySet();
		for (Map<String, Object> item : (ArrayList<Map<String, Object>>)dataList) {
			String station_id_c = (String) item.get("Station_Id_C");
			String key = station_id_c + "_" + year + "_" + month;
			boolean isCQStation = CQAWSStation.isCQStation(station_id_c);
			if(!isCQStation) continue;
			item = CommonTool.disposePreData(item);
			if(existSet.contains(key)) {
				//update
				item.put("id", existData.get(key));
				updateList.add(item);
			} else {
				//insert
				insertList.add(item);
			}
		}
		UPDATEDATA = UPDATEDATA.replaceAll("%year", year + "");
		insertBatch(ADDSDATA, insertList, getResultSetMetaData());
		updateBatch2(UPDATEDATA, updateList, getResultSetMetaData(), new String[]{"Station_Id_C", "avgTmp", "year", "month"});
	}
	
	public HashMap<String, Integer> getExistData(int year, int month) {
		//mysql
		String query = "select id, Station_Id_C, year, month from t_MonthAvgTmp where year = " + year + " and month = " + month;		
		List list = query(getConn(), query, null);
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("year") + "_" + tempMap.get("month");
				int id = (Integer) tempMap.get("id");
				hashMap.put(key, id);
			}
		}
		return hashMap;
	}
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSTRUCT);
	}
	
}
