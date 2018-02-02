package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;
/**
 * 预报数据
 * @author Administrator
 *
 */
public class ForecastDataDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_forecastdata where 1=2";

	private String ADDHOUTMPAVG = "insert into t_forecastdata (%s) values (%s)";
	
	public int getNoPre3DaysCnt(String datetime) {
		//根据预报时间，查询未来三天都没有降水的站数总和
		String query = "select count(1) as cnt from (" + 
					"select station_id_C,  ForscastDate, count(1) from t_forecastdata where " +  
					"forscastdate = '" + datetime + "'  and PreTime12 is null and PreTime24 is null " + 
					"group by station_id_C, ForscastDate) a";
		List list = query(getConn(),query, null);
		if(list != null && list.size() > 0) {
			HashMap dataMap = (HashMap) list.get(0);
			int cnt = ((Long) dataMap.get("cnt")).intValue();
			return cnt;
		}
		return -1;
	}
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertValues(List dataList, String datetime) {
		HashMap<String, Object> existData = getExistData(datetime);
		for(int i = dataList.size() - 1; i >= 0; i--) {
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			int hourSpan = (Integer) dataMap.get("HourSpan");
			String key = datetime + "_" + station_Id_C + "_" + hourSpan;
			if(existData.containsKey(key)) {
				dataList.remove(i);
			}
		}
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	/**
	 * 查询预报的时效
	 * @param datetime
	 * @return
	 */
	public List getFutureDateByForecastDate(String datetime) {
		String query = "select distinct date_format(FutureDate, '%Y-%m-%d') as  FutureDate from t_forecastdata where ForscastDate = '" + datetime + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public String getMaxFutureDateByForecastDate(String forscastDate) {
		String query = "select date_format(max(FutureDate), '%Y-%m-%d') as  FutureDate from t_forecastdata where ForscastDate = '" + forscastDate + "'";
		List list = query(getConn(), query, null);
		if(list == null || list.size() == 0) return null;
		HashMap dataMap = (HashMap) list.get(0);
		return (String) dataMap.get("FutureDate");
	}
	
	/**
	 * 根据时间，查询预报的数据
	 * @param datetime
	 * @return
	 */
	public List getDataListByTime(String datetime) {
		String query = "select date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, Station_Id_C, WeatherState12, WeatherState24, MaxTmp, " +
				"MinTmp, HourSpan from t_forecastdata where ForscastDate = '" + datetime + "' order by HourSpan";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getDataListByTimeAndHourspan(String datetime, int hourspan) {
		String query = "select date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, Station_Id_C, WeatherState12, WeatherState24, MaxTmp, " +
			"MinTmp, HourSpan from t_forecastdata where ForscastDate = '" + datetime + "' and hourspan <= " + hourspan;
		List list = query(getConn(), query, null);
		return list;
	}
	public List getDataCountByDatetime(String datetime) {
		String query = "select date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, count(1) as cnt, HourSpan from t_forecastdata where MaxTmp >= 35 and ForscastDate = '" + datetime +
				"' group by HourSpan, ForscastDate order by HourSpan";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getWeatherStateByDatetime(String datetime) {
		String query = "select date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, HourSpan, WeatherState24 as WeatherState, Station_Id_C " +
				" from t_forecastdata where Station_Id_C like '5%' and ForscastDate = '" + datetime + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	
	public List getCntListByTimes(String datetime, int days, int MAXAREAHIGHTMPSTATIONS) {
		String query = "select date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, count(1) as cnt, HourSpan from t_forecastdata where MaxTmp >= 35 and ForscastDate = '" + datetime + 
						"' group by HourSpan, ForscastDate having count(1) >= " + MAXAREAHIGHTMPSTATIONS;
		List list = query(getConn(), query, null);
		return list;
	}
	public HashMap<String, Object> getExistData(String datetime) {
		String query = "select Station_Id_C, date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, HourSpan from t_forecastdata where ForscastDate = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String forscastDate = (String) tempMap.get("ForscastDate");
				String station_Id_C = (String) tempMap.get("Station_Id_C");
				int HourSpan = (Integer) tempMap.get("HourSpan");
				hashMap.put(forscastDate + "_" + station_Id_C + "_" + HourSpan, "");
			}
		}
		return hashMap;
	}
	
	public List getForecastDataByForecastTimeAndStation(String datetime, String maxFutureDate, String station_Id_C) {
		String query = "select Station_Id_C, date_format(ForscastDate, '%Y-%m-%d') as ForscastDate, date_format(FutureDate, '%Y-%m-%d') as FutureDate, WeatherState24 as WeatherState from t_forecastdata where ForscastDate = '" + datetime + "' and " +
				"FutureDate <= '" + maxFutureDate + "' and Station_Id_C = '" + station_Id_C + "' order by FutureDate";
		return query(getConn(), query, null);
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
