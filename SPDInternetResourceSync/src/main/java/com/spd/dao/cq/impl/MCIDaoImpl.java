package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonTool;
/**
 * MCI的相关操作
 * @author Administrator
 *
 */
public class MCIDaoImpl extends BaseDao {

	private static String ADDMCI = "insert into t_MCI (%s) values (%s)";

	private static String QUERYMCISTRUCT = "select * from t_MCI where 1 = 2";
	
	public HashMap<String, Object> getExistMCI(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_Mci where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	public List<String> getAllOCCurMCIStations(String datetime) {
		String query = "select  Station_id_C from t_Mci where  datetime = '" + datetime + "' and mci <= -1.0";		
		List list = query(getConn(), query, null);
		List<String> stationList = new ArrayList<String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String station_Id_C = (String) tempMap.get("Station_id_C");
				stationList.add(station_Id_C);
			}
		}
		return stationList;
	}
	
	public List<String> getAllUnOCCurMCIStations(String datetime) {
		String query = "select  Station_id_C from t_Mci where  datetime = '" + datetime + "' and mci > -1.0";		
		List list = query(getConn(), query, null);
		List<String> stationList = new ArrayList<String>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String station_Id_C = (String) tempMap.get("Station_id_C");
				stationList.add(station_Id_C);
			}
		}
		return stationList;
	}
	
	/**
	 * 根据时间、站号查询MCI值
	 * @param datetime
	 * @param station_Id_C
	 * @return
	 */
	public Double getMCIByTimeStation(String datetime, String station_Id_C) {
		Double mci = null;
		String query = "select  mci from t_Mci where  datetime = '" + datetime + "' and station_Id_C = '" + station_Id_C + "'";		
		List list = query(getConn(), query, null);
		List<String> stationList = new ArrayList<String>();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			mci = (Double) tempMap.get("mci");
			return mci;
		} else {
			return null;
		}
	}
	public HashMap<String, Integer> getStartMCIByTimes(String startTime, String endTime) {
		//mysql
		String query = "select count(1) as cnt, Station_id_C from t_Mci where  datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and mci <= -1.0 group by station_id_C";		
		List list = query(getConn(), query, null);
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				int cnt = ((Long) tempMap.get("cnt")).intValue();
				String station_Id_C = (String) tempMap.get("Station_id_C");
				hashMap.put(station_Id_C, cnt);
			}
		}
		return hashMap;
	}
	
	public HashMap<String, Integer> getEndMCIByTimes(String startTime, String endTime) {
		//mysql
		String query = "select count(1) as cnt, Station_id_C from t_Mci where  datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and mci > -1.0 group by station_id_C";		
		List list = query(getConn(), query, null);
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				int cnt = ((Long) tempMap.get("cnt")).intValue();
				String station_Id_C = (String) tempMap.get("Station_id_C");
				hashMap.put(station_Id_C, cnt);
			}
		}
		return hashMap;
	}
	
	public HashMap<String, Integer> querySumMCI(String startTime, String endTime, String station_Id_C) {
		//mysql
		String query = "select sumStrength, SingleStrength, SingleStrength - days / 200 as SingleSynthStrength from ( " +
						"select sum(mci) as sumStrength, sum(mci) / (TIMESTAMPDIFF(DAY,'" + startTime + "','" + endTime + "') + 1) as SingleStrength , (TIMESTAMPDIFF(DAY,'" + startTime + "','" + endTime + "') + 1) as days " + 
						"from t_mci where datetime >='" + startTime + "' and datetime <= '" + endTime + "' and station_id_C = '" + station_Id_C + "') a";		
		List list = query(getConn(), query, null);
		HashMap  hashMap = new HashMap();
		if(list != null && list.size() > 0) {
			HashMap tempMap = (HashMap) list.get(0);
			Double singleStrength = (Double) tempMap.get("SingleStrength");
			Double singleSynthStrength = (Double) tempMap.get("SingleSynthStrength");
			Double sumStrength = (Double) tempMap.get("sumStrength");
			if(singleStrength == null || singleSynthStrength == null || sumStrength == null) return null;
			singleStrength = CommonTool.roundDouble2(singleStrength);
			singleSynthStrength = CommonTool.roundDouble2(singleSynthStrength);
			sumStrength = CommonTool.roundDouble2(sumStrength);
//			=IF(AND(N150>-1.5,N150<=-1),1,IF(AND(N150<=-1.5,N150>-2),2,IF(AND(N150<=-2,N150>-2.5),3,IF(N150<=-2.5,4,0))))
			int strengthLevel = 0;
			if(singleSynthStrength > -1.5 && singleSynthStrength <= -1) {
				strengthLevel = 1;
			} else if(singleSynthStrength > -2 && singleSynthStrength <= -1.5) {
				strengthLevel = 2;
			} else if(singleSynthStrength > -2.5 && singleSynthStrength <= -2) {
				strengthLevel = 3;
			} else if(singleSynthStrength <= -2.5) {
				strengthLevel = 4;
			} 
			hashMap.put("SumStrength", sumStrength);
			hashMap.put("SingleStrength", singleStrength);
			hashMap.put("SingleSynthStrength", singleSynthStrength);
			hashMap.put("StrengthLevel", strengthLevel);
			hashMap.put("EndTime", endTime);
			hashMap.put("StartTime", startTime);
			hashMap.put("Station_Id_C", station_Id_C);
		}
		return hashMap;
	}
	
	
	public HashMap queryMCIAreaDays(String datetime) {
		HashMap dataMap = new HashMap();
		String sql = "select count(1) as cnt from t_mcistation where starttime <= '" + datetime + "' and endtime >= '" + datetime + "'";
		List list = query(getConn(), sql, null);
		if(list != null && list.size() > 0) {
			HashMap itemMap = (HashMap) list.get(0);
			int cnt = ((Long) itemMap.get("cnt")).intValue();
			dataMap.put("cnt", cnt);
			dataMap.put("datetime", datetime + " 00:00:00");
		}
		return dataMap;
	}
	
	/**
	 * 获取全部自动站
	 * @param datetime
	 * @return
	 */
	public Set<String> getStations() {
		String query = "select Station_Id_C from t_station where station_Id_C like '5%' and station_Id_C <> '57431' ";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				stationSet.add((String) tempMap.get("Station_Id_C"));
			}
		}
		return stationSet;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertMCIValue(List dataList) {
		//先判断重复
		insertBatch(ADDMCI, dataList, getMCIResultSetMetaData());
	}
	
	public ResultSetMetaData getMCIResultSetMetaData() {
		return getTableStruct(getConn(), QUERYMCISTRUCT);
	}
	
}
