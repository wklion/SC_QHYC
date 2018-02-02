package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 暴雨降水
 * @author Administrator
 *
 */
public class T_RainStormDaoImpl extends BaseDao {

	private String tableName;
	
	private String INSERTDATA;// = "insert into " + tableName + " (%s) values (%s) ";

	private String UPDATEDATA;// = "insert into " + tableName + " (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT;// = "select * from " + tableName + " where 1=2";
	
	private String columnName = "Pre";
	
	public T_RainStormDaoImpl(String tableName) {
		this.tableName = tableName;
		INSERTDATA = "insert into " + tableName + " (%s) values (%s) ";
		
		UPDATEDATA = "update " + tableName + " set " + columnName + " = ? where id = ?";
		
		QUERYSURFCHNMULSTRUCT = "select * from " + tableName + " where 1=2";
	}
	
	public HashMap<String, Object> getExistDataTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from " + tableName + " where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	public List getRainStormByTime(String datetime, String type) {
		String query = "";
		if("AWS".equals(type)) {
			query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime, Pre from " + tableName + " where station_Id_C like '5%' and datetime = '" + datetime + "' and Pre >= 50";
		} else {
			query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime, Pre from " + tableName + " where datetime = '" + datetime + "' and Pre >= 50";
		}
		List list = query(getConn(), query, null);
		return list;
	}
	
	
	public HashMap<String, Integer> getRainStationStandard() {
		String query = "select Station_Id_C, StdStationCnt from t_rainstationstandard";
		List list = query(getConn(), query, null);
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		for(int i = 0; i < list.size(); i++) {
			HashMap itemMap = (HashMap) list.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			Integer StdStationCnt = (Integer) itemMap.get("StdStationCnt");
			resultMap.put(station_Id_C, StdStationCnt);
		}
		return resultMap;
	}
	
	/**
	 * 查询区县的雨量站，汇总
	 * @param datetime
	 * @return
	 */
	public HashMap<String, List<String>> getRainStationGroupByArea() {
		String query = "select Station_Id_C, Station_Name, AreaStation  from t_RainStation";
		List list = query(getConn(), query, null);
		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
		for(int i = 0; i < list.size(); i++) {
			HashMap itemMap = (HashMap) list.get(i);
			String areaStation = (String) itemMap.get("AreaStation");
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			List<String> stationList = resultMap.get(areaStation);
			if(stationList == null) {
				stationList = new ArrayList<String>(); 
			}
			stationList.add(station_Id_C);
			resultMap.put(areaStation, stationList);
		}
		return resultMap;
	}
	
	public void insert(List dataList) {
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public void update(List updateDataList) {
		updateBatch2(UPDATEDATA, updateDataList, getSurfChnMulResultSetMetaData(), new String[]{columnName});
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
}
