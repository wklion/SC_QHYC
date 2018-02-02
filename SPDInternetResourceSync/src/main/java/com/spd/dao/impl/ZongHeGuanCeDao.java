package com.spd.dao.impl;

import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

public class ZongHeGuanCeDao extends BaseDao {

	private static String QUERYZONGHEGUANCE = "select * from t_ProductAttribute where level1name = '综合观测' and URL != '' and interfaceAddress not like 'http%' and level3name not like '长江%'";
//	private static String QUERYZONGHEGUANCE = "select id from v_metadict_new where id = 18";
	/**
	 * 获取综合观测
	 * @return
	 */
	public List getZongHeGuanCe() {
		List<Map> list = query(getConn(), QUERYZONGHEGUANCE, null);
		return list;
	}
}
