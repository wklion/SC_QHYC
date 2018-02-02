package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

public class TDocDao extends BaseDao {

	private String ADDDOC = "insert into t_doc (%s) values (%s)";

	private String QUERYDOCSTRUCT = "select * from t_doc where 1 = 2";
	
	public boolean isDownload(String md5) {
		String query = "select md5 from t_doc where md5='" + md5 + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取startTime, endTime时间段之间的文件，文件格式以MD5 + "_" + fileName的格式返回。
	 * 该方法的两个目的。
	 * 1.批量查询出结果，不用每次都查询一遍，提升效率。
	 * 2.解决当文件内容一样的时候，不去同步的bug
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Set getExistFile(String startTime, String endTime) {
		String query = "select concat(md5, '_', filename) from t_doc where intime >= '" + startTime + "' and intime <= '" + endTime + "'";
		Set set = queryResultToSet(getConn(), query, null);
		return set;
	}
	
	public boolean insert(List dataList) {
		return insertBatch(ADDDOC, dataList, getDOCMetaData());
	}
	
	public ResultSetMetaData getDOCMetaData() {
		return getTableStruct(getConn(), QUERYDOCSTRUCT);
	}
}
