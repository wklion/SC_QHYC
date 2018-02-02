package com.spd.dao.cq.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;
import com.spd.dao.MSBaseDao;
/**
 * 区域站小时降水的相关操作
 * @author Administrator
 *
 */
public class MWSQueryHourDataDaoImpl extends MSBaseDao {

	private static String ADDHOURRAIN = "insert into t_mwshourrain2 (%s) values (%s)";

	private static String QUERYHOURRAINSTRUCT = "select * from t_mwshourrain2 where 1 = 2";
	
//	private Connection conn;
	
//	public Connection getConn() {
////		if(conn != null) {
////			return conn;
////		}
//		try {
//			// 加载MySql的驱动类
//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		} catch (ClassNotFoundException e) {
//			System.out.println("找不到驱动程序类 ，加载驱动失败！");
//			e.printStackTrace();
//		}
//		String url = "jdbc:sqlserver://172.24.186.74;DatabaseName=HourData;user=Clim;password=123";
//		String username = "Clim";
//		String password = "123";
//		try {
//			conn = DriverManager.getConnection(url, username,
//					password);
//		} catch (SQLException se) {
//			System.out.println("数据库连接失败！");
//			se.printStackTrace();
//		}
//		return conn;
//	}
	
	public HashMap<String, Object> getExistMCI(String datetime) {
		//mysql
		String query = "select Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from t_mwshourrain where datetime = '" + datetime + "'";		
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
	
	public List getDataMCI(String startDateTime, Set<String> stationSet) {
		//mysql
//		String query = "select * from 小时降水 where 时次 >= '" + startDateTime + "' and 时次 <= '" + endDateTime + "'";		
		String query = "select * from 小时降水 where 时次 = '" + startDateTime + "' and 站号 like 'A%'";		
		List list = query(getConn(), query, null);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < list.size(); i++) {
			HashMap tempMap = (HashMap) list.get(i);
			BigDecimal R1 = (BigDecimal) tempMap.get("r1");
			BigDecimal R3 = (BigDecimal) tempMap.get("r3");
			BigDecimal R6 = (BigDecimal) tempMap.get("r6");
			BigDecimal R12 = (BigDecimal) tempMap.get("r12");
			BigDecimal R24 = (BigDecimal) tempMap.get("r24");
			if((R1 != null && R1.floatValue() !=0) || (R3 != null && R3.floatValue() !=0) || 
					(R6 != null && R6.floatValue() !=0) || (R12 != null && R12.floatValue() !=0)
					|| (R24 != null && R24.floatValue() !=0)) {
				HashMap dataMap = new HashMap();
				String station = tempMap.get("站号") + "";
				station = station.trim();
				String time = ((String) tempMap.get("时次")).trim();
				dataMap.put("datetime", time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8) + " "
						+ time.substring(8, 10) + ":00:00");
				dataMap.put("station_Id_C", station);
				dataMap.put("r1", R1 == null ? 0 : R1.floatValue());
				dataMap.put("r3", R3 == null ? 0 : R3.floatValue());
				dataMap.put("r6", R6 == null ? 0 : R6.floatValue());
				dataMap.put("r12", R12 == null ? 0 : R12.floatValue());
				dataMap.put("r24", R24 == null ? 0 : R24.floatValue());
				if(stationSet.contains(station)) {
					resultList.add(dataMap);
				}
			}
		}
		return resultList;
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
	public void insertValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOURRAIN, dataList, getAWSHourRainResultSetMetaData());
	}
	
	public ResultSetMetaData getAWSHourRainResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOURRAINSTRUCT);
	}
	
}
