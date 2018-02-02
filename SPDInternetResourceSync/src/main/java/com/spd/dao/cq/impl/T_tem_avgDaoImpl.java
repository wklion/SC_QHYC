package com.spd.dao.cq.impl;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;

/**
 * 平均气温统计
 * @author Administrator
 *
 */
public class T_tem_avgDaoImpl extends BaseDao {

	private int year;
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, id from t_tem_avg where year = ";

	private String UPDATEDATA = "update t_tem_avg set %columns = ? where id = ?";
	
	private String INSERTDATA = "insert into t_tem_avg (%s) values (%s) ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_tem_avg where 1=2";

	public T_tem_avgDaoImpl(int year) {
		this.year = year;
		QUERYEXISTDATA += this.year;
	}
	
	public T_tem_avgDaoImpl(){
		
	}
	
	public List<HashMap> getDataByItems(String items, int year, int month) {
		List<HashMap> resultList = new ArrayList<HashMap>();
		String query = "select Station_Id_C, " + items + " from t_tem_avg where year = " + year;
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap dataMap = new HashMap();
				Double sum = 0.0;
				int cnt = 0;
				String station_Id_C = null;
				HashMap tempMap = (HashMap) list.get(i);
				Iterator it = tempMap.keySet().iterator();
				while(it.hasNext()) {
					String key = (String) it.next();
					if(key.equals("Station_Id_C")) {
						station_Id_C = (String) tempMap.get("Station_Id_C");
						boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
						if(!isCQStation) continue;
					} else if(key.startsWith("m")) {
						Object obj = tempMap.get(key);
						if(obj == null) continue;
						Double value = ((java.math.BigDecimal) obj).doubleValue();
						if(value < 99999) {
							sum += value;
							cnt++;
						}
					}
				}
				if(cnt != 0) {
					dataMap.put("Station_Id_C", station_Id_C);
					dataMap.put("avgTmp", CommonTool.roundDouble(sum / cnt));
					dataMap.put("year", year);
					dataMap.put("month", month);
					resultList.add(dataMap);
				}
			}
		}
		return resultList;
	}
	/**
	 * 统计指定站号的资料个数 
	 * @param station_Id_C
	 * @return
	 */
	public int getDataCntByStation(String station_Id_C) {
		int cnt = 0;
		String query = "select * from t_tem_avg where station_id_C = '" + station_Id_C + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				Iterator it = tempMap.keySet().iterator();
				while(it.hasNext()) {
					String key = (String) it.next();
					if(key.startsWith("m")) {
						Object value = tempMap.get(key);
						if(value != null) {
							cnt++;
						}
					}
				}
			}
		}
		return cnt;
	}
	
	public boolean isDataUpdateByTime(String year, String column, String station_Id_C) {
		String query = "select " + column + " from t_tem_avg where Station_Id_C = '" + station_Id_C + "' and year = " + year;
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			Object value = tempMap.get(column);
			if(value == null) {
				return false;
			}
			return true;
		}
		return false;
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
	
	/**
	 * 根据时间，查询对应的温度
	 * @param datetime
	 * @return
	 */
	public LinkedHashMap<String, Double> getTemByTime(String datetime) {
		int year = Integer.parseInt(datetime.substring(0, 4));
		String item = "m" + datetime.substring(5, 7) + "d" + datetime.substring(8, 10);
		String query = "select " + item + ", Station_Id_C, year from t_tem_avg where year = " + year + " and Station_Id_C like '5%'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		LinkedHashMap<String, Double> resultMap = new LinkedHashMap<String, Double>();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			BigDecimal bigDecimalPre = (BigDecimal) dataMap.get(item);
			if(bigDecimalPre == null) {
				resultMap.put(station_Id_C, null);
			} else {
				Double pre = bigDecimalPre.doubleValue();
				resultMap.put(station_Id_C, pre);
			}
		}
		return resultMap;
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
