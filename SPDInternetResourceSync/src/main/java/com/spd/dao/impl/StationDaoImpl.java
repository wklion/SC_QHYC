package com.spd.dao.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 站点
 * @author Administrator
 *
 */
public class StationDaoImpl extends BaseDao {

	/**
	 * 获取国家站站点
	 * @return
	 */
	public HashSet<String> getAWSStations() {
		//mysql
		HashSet<String> result = new HashSet<String>();
		String query = "select Station_Id_C from t_station where zoomlevel = 1 and seq is not null";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				result.add((String)tempMap.get("Station_Id_C"));
			}
		}
		return result;
	}
}
