package com.spd.dao.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

public class ChangJiangDao extends BaseDao {
	
	private static String QUERYSHANDIANSQL = "select * from t_ProductAttribute where level3name like '长江%'  and productCode <> '' order by level1name, level2name, level3name,productName";

	private static String ISFILEEXIST = "select * from t_doc where md5 = ?";

	private static String ADDDOC = "insert into t_doc(%s) values (%s)";

	private String QUERYDOCSTRUCT = "select * from t_doc where 1=2";

	
	public List<HashMap> queryChangJiang() {
		List list = query(getConn(), QUERYSHANDIANSQL, null);
		return list;
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
		String query = "select concat(md5, '_', filename) from t_doc where intime >= '" + startTime + "' and intime <= '" + endTime + "' and website = 'ftp://10.104.64.16:2121'";
		Set set = queryResultToSet(getConn(), query, null);
		return set;
	}
	
	public boolean isFileExist(String md5) {
		List list = query(getConn(), ISFILEEXIST, new String[]{md5});
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void insertChangJiangValue(List dataList) {
		insertBatch(ADDDOC, dataList, getDocResultSetMetaData());
	}
	
	public ResultSetMetaData getDocResultSetMetaData() {
		return getTableStruct(getConn(), QUERYDOCSTRUCT);
	}
	
}
