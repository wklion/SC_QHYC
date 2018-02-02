package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;

/**
 * 秋雨期
 * @author Administrator
 *
 */
public class AutumnRainsSeqDaoImpl extends BaseDao {

	private String tableName;
	
	private String INSERTDATA  = "insert into t_autumnrainsseq (%s) values (%s) ";

	private String DELETEDATA = "delete from  t_autumnrainsseq where year = ";
	
	private String QUERYSURFCHNMULSTRUCT  = "select * from t_autumnrainsseq where 1=2";
	
	private String columnName = "Pre";
	
	
	public HashMap<String, Object> getExistDataTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from " + tableName + " where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	
	public void insert(List dataList) {
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public void update(List dataList, int year) {
		//删除,暂时不管事务的问题。
		boolean flag = update(DELETEDATA + year);
		if(flag) {
			insert(dataList);
		}
	}
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
}
