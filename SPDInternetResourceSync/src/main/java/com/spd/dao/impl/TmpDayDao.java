package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.List;

import com.spd.dao.BaseDao;

public class TmpDayDao extends BaseDao {

	private String ADDTMPDAO = "insert into t_daydata(%s) values (%s)";

	private String QUERYTMPDAOSTRUCT = "select * from  t_daydata where 1 = 2";
	
	/**
	 * t_ParkForecast中插入数据
	 * @param dataList
	 */
	public void insertDayDataValue(List dataList) {
		insertBatch(ADDTMPDAO, dataList, getDayDataResultSetMetaData());
	}
	
	/**
	 * 查询t_daydata表结构
	 * @return
	 */
	public ResultSetMetaData getDayDataResultSetMetaData() {
		return getTableStruct(getConn(), QUERYTMPDAOSTRUCT);
	}
}
