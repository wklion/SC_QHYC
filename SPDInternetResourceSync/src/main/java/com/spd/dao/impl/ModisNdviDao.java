package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

public class ModisNdviDao extends BaseDao {

	//添加ModisNdvi
	private String ADDMODISNDVIINFO = "insert into t_Modis_Ndvi (%s) values (%s)";
	//获取ModisNdvi表结构
	private String QUERYMODISNDVIINFO = "select * from t_Modis_Ndvi where 1=2";
	
	/**
	 * 查询已经存在的水库水情
	 * @param TM
	 * @return
	 */
	public boolean isFileDownloaded(String fileName) {
		String query = "select FileName from t_Modis_Ndvi where  FileName = '" + fileName + "'";
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * t_reservoirregimeninfo中插入数据
	 * @param dataList
	 */
	public void insertModisNdviValue(List dataList) {
		insertBatch(ADDMODISNDVIINFO, dataList, getModisNdviResultSetMetaData());
	}
	
	/**
	 * 查询t_riverregimeninfo表结构
	 * @return
	 */
	public ResultSetMetaData getModisNdviResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMODISNDVIINFO);
	}
}
