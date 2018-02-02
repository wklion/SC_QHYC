package com.spd.test;

import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

public class ZongHeGuanCeBuLuDao extends BaseDao {
	
	public List getZongHeGuanCe(String sql) {
		List<Map> list = query(getConn(), sql, null);
		return list;
	}
	
	public int getTypeId(String productCode) {
		List<Map> list = query(getConn(), "select id from v_metadict_new where productCode = '" + productCode + "'", null);
		if(list != null && list.size() >0) {
			Map map = list.get(0);
			int id = Integer.parseInt(map.get("id") + "");
			return id;
		}
		return -1;
	}
}
