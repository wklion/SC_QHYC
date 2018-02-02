package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class WinInstMaxDaoImpl extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_win_inst_max where 1=2";

	private String ADDHOUTMPAVG = "insert into t_win_inst_max (%s) values (%s)";
	
	/**
	 * 查询大风表中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getWinAvgHou(int startYear, int endYear) {
		String query = "select Station_Name, Province, City, Cnty, Town, Station_Id_C, Station_Id_d, Lat, Lon, Alti, Station_levl," +
				" Admin_Code_CHN, Datetime, WIN_S_Inst_Max, WIN_D_INST_Max, year from t_surf_chn_mul_day where GaWIN = 1 and year >= " + startYear
				+ " and year <= " + endYear + " and WIN_S_Inst_Max >= 13.9 and WIN_S_Inst_Max != 999999";
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertWinValue(List dataList) {
		insertBatch(ADDHOUTMPAVG, dataList, getWinResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistWinAvg(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, DATE_FORMAT(DATETIME, '%Y-%m-%d %T') as datetime from t_win_inst_max where year >= " + startYear + " and year <= " + endYear;		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	/**
	 * 查询t_ParkForecast表结构
	 * @return
	 */
	private ResultSetMetaData getWinResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
