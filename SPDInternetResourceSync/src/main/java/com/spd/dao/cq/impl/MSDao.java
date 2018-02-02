package com.spd.dao.cq.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.MSBaseDao;

public class MSDao extends MSBaseDao {
	
	private String tableName;
	
	public MSDao(String tableName) {
		this.tableName = tableName;
	}
	
	public List queryData(String dateTime) {
		String query = "select * from " + tableName + " where 日期 = '" + dateTime + "'";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		return list;
	}
}
