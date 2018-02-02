package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;

/**
 * 日照对数统计表
 * @author Administrator
 *
 */
public class T_sshDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_ssh where year = ";

	private String UPDATEDATA = "update t_ssh set %columns = ? where id = ?";
	
	private String INSERTDATA = "insert into t_ssh (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_ssh where 1=2";
	
	public T_sshDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	public T_sshDaoImpl() {
		
	}
	
	public Double queryDataByItem(String item, String datetime, String station_Id_C) {
		int year = Integer.parseInt(datetime.substring(0, 4));
		String query = "select Station_Id_C, year, " + item + ", id from t_ssh where station_Id_C = '" + station_Id_C + "' and year = " + year; 
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return -1.0;
		HashMap dataMap = (HashMap) resultList.get(0);
		Object result = dataMap.get(item);
		if(result != null) {
			return (Double) result;
		}
		return null;
	}
	
	public Double querySSHByTimeAndStation(String datetime, String station_id_C) {
		Integer year = Integer.parseInt(datetime.substring(0, 4));
		String items = CommonTool.createItemStrByRangeDate(datetime, datetime);
		String query = "select " + items + " from t_ssh where station_Id_C = '" + station_id_C + "' and year = " + year;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			Double result = (Double) dataMap.get(items);
			return result;
		}
		return null;
	}
	
	public HashMap<String, Double> querySSHByTime(String datetime) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		Integer year = Integer.parseInt(datetime.substring(0, 4));
		String items = CommonTool.createItemStrByRangeDate(datetime, datetime);
		String query = "select Station_Id_C, " + items + " from t_ssh where year = " + year;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
				Double result = (Double) dataMap.get(items);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				resultMap.put(station_Id_C, result);
			}
		}
		return resultMap;
	}
	
	public List queryData(String startTime, String endTime, String items) {
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		String query = "";
		List list = new ArrayList();
		if(startYear == endYear) {
			//查询当年
			query = "select Station_Id_C, year, " + items + ", id from t_ssh where station_Id_C like '5%' and year = " + startYear;
			
		} else {
			//查询两年
			query = "select Station_Id_C, year, " + items + ", id from t_ssh where station_Id_C like '5%' and year = " + startYear + " or year = " + endYear;
		}
		List resultList = query(getConn(), query, null);
//		for(int i = 0; i < resultList.size(); i++) {
//			HashMap dataMap = (HashMap) resultList.get(i);
//			String year = (Integer)dataMap.get("year") + "";
//			HashMap resultMap = new HashMap();
//			resultMap.put("Station_Id_C", dataMap.get("Station_Id_C")); 
//			Iterator it = dataMap.keySet().iterator();
//			while(it.hasNext()) {
//				String key = (String) it.next();
//				if(key.startsWith("m")) {
//					//判断时间是否在给点的开始，结束范围内
//					boolean flag = CommonTool.isTimeInRange(startTime, endTime, key, year);
//					if(flag) {
//						resultMap.put(CommonTool.getDateStrByItem(key, startYear), dataMap.get(key));
//					}
//				}
//			}
//			list.add(resultMap);
//		}
//		//TODO 是否按时间排序了，没有的话，加上排序
//		return list;
		return resultList;
	}
	
	/**
	 * 根据年份查询已经存在的数据
	 * @param year
	 * @return
	 */
	public HashMap getExistData() {
		List list = query(getConn(), QUERYEXISTDATA, null);
		Set<String> existedStationSet = new HashSet<String>();
		HashMap<String, Integer> existedMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = (String)tempMap.get("Station_Id_C");
				int id = (Integer) tempMap.get("id");
				existedMap.put(key, id);
//				existedStationSet.add(key);
			}
		}
		return existedMap;
	}
	
	public void disposeDataList(List dataList) {
		List insertList = new ArrayList();
		List updateList = new ArrayList();
		
		HashMap<String, Integer> existData = getExistData();
		Set<String> existSet = existData.keySet();
		for (Map<String, Object> item : (ArrayList<Map<String, Object>>)dataList) {
			String station_id_c = (String) item.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_id_c);
			if(!isCQStation) continue;
			if(existSet.contains(station_id_c)) {
				//update
				item.put("id", existData.get(station_id_c));
				updateList.add(item);
			} else {
				//insert
				insertList.add(item);
			}
		}
		UPDATEDATA = UPDATEDATA.replaceAll("%year", year + "");
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		updateBatch(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData());
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
