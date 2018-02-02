package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 候平均降水
 * @author Administrator
 *
 */
public class HouPreResultDaoImpl extends BaseDao {

	
	private String INSERTDATA = "insert into t_houpreresult (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_houpreresult where 1=2";
	
	
	public void insert(List dataList) {
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
	/**
	 * 查询常年的站点候平均雨量
	 * @param datetime
	 * @return
	 */
	public HashMap<String, Double> getYearsData(String datetime, String type) {
		//mysql
		String query = "select Station_Id_C, avg(HouPre) as HouPre from t_houpreresult where year >= 1981 and year <= 2010 and type = '" + type + "' group by Station_Id_C";		
		List list = query(getConn(), query, null);
		HashMap<String, Double> hashMap = new HashMap<String, Double>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String station_Id_C = (String) tempMap.get("Station_Id_C");
				Double houPre = (Double) tempMap.get("HouPre");
				hashMap.put(station_Id_C, houPre);
			}
		}
		return hashMap;
	}
}
