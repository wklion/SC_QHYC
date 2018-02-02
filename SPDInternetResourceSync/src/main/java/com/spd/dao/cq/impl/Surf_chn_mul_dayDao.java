package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;

public class Surf_chn_mul_dayDao extends BaseDao {
	
	private int year;
	
	//添加CIMISS日值
//	private String ADDSURFCHNMUL = "insert into t_surf_chn_mul_day (id, %s) values (seq_surf_chn_mul_day.nextval, %s)"; // oracle
	private String ADDSURFCHNMUL = "insert into t_surf_chn_mul_day (%s) values (%s)"; // mysql
	//获取CIMISS日值表结构
	private String QUERYSURFCHNMULSTRUCT = "select * from t_surf_chn_mul_day where 1=2";
	
	private String QUERYEXISTDATA = "select Station_Id_C, year, date_format(datetime, '%Y-%m-%d %T') as Datetime, id from t_surf_chn_mul_day where datetime = ";
	
	private String UPDATEDATA = "update t_surf_chn_mul_day set ";
	
	private String INSERTDATA = "insert into t_surf_chn_mul_day (%s) values (%s) ";
		
	public Surf_chn_mul_dayDao(int year) {
		this.year = year;
	}
	
	/**
	 * 查询CIMISS日值表中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public HashMap<String, Object> getExistSurfChnMul(String startObservTime, String endObservTime) {
		// oracle
//		String query = "select to_char(datetime, 'yyyy-MM-dd HH24:mi:ss') as Datetime, Station_Id_C from t_surf_chn_mul_day where  Datetime >= to_date('" 
//			+ startObservTime + "','yyyy-MM-dd HH24:mi:ss') and Datetime <= to_date('" + endObservTime + "', 'yyyy-MM-dd HH24:mi:ss')";
		//mysql
		String query = "select date_format(datetime, '%Y-%m-%d %T') as Datetime, Station_Id_C, id from t_surf_chn_mul_day where  Datetime >= '" 
			+ startObservTime + "' and Datetime <= '" + endObservTime + "'";
		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Datetime") + "_" + tempMap.get("Station_Id_C");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	/**
	 * t_surf_chn_mul_day中插入数据
	 * @param dataList
	 */
	public void insertSurfChnMulValue(List dataList) {
		insertBatch(ADDSURFCHNMUL, dataList, getSurfChnMulResultSetMetaData());
	}
	
	/**
	 * 查询t_surf_chn_mul_day表结构
	 * @return
	 */
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
	public HashMap getExistData(String timeStr) {
		List list = query(getConn(), QUERYEXISTDATA + "'" + timeStr + "'", null);
		Set<String> existedStationSet = new HashSet<String>();
		HashMap<String, Integer> existedMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String station_Id_C = (String)tempMap.get("Station_Id_C");
				String datetime = (String)tempMap.get("Datetime");
				int id = (Integer) tempMap.get("id");
				existedMap.put(station_Id_C + "_" + datetime, id);
//				existedStationSet.add(key);
			}
		}
		return existedMap;
	}
	
	public void disposeDataList(List dataList, String timeStr) {
		List insertList = new ArrayList();
		List updateList = new ArrayList();
		
		HashMap<String, Integer> existData = getExistData(timeStr);
		Set<String> existSet = existData.keySet();
		for (Map<String, Object> item : (ArrayList<Map<String, Object>>)dataList) {
			String station_id_c = (String) item.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_id_c);
			if(!isCQStation) continue;
			String datetime = (String) item.get("Datetime");
			String key = station_id_c + "_" + datetime;
			if(existSet.contains(key)) {
				//update
				item.put("id", existData.get(key));
				updateList.add(item);
			} else {
				//insert
				insertList.add(item);
			}
		}
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		//TODO update  先不处理
//		if(updateList != null && updateList.size() > 0) {
//			UPDATEDATA = createUpdateSQL(UPDATEDATA, (Map<String, Object>) updateList.get(0));
//		}
//		updateBatch(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData());
	}
	
}
