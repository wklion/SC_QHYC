package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spd.dao.BaseDao;

/**
 * 连阴雨预警单站统计表
 * @author Administrator
 *
 */
public class AlertContinuousRainsStationDaoImpl extends BaseDao {

	private String INSERTDATA = "insert into t_continuerainstationalert (%s) values (%s) ";

	private String UPDATEDATA = "update t_continuerainstationalert set  StartTime = ?, EndTime = ?, ForecastDate = ?, Station_Id_C = ?, NoSunDays = ?, RainDays = ?, Levle = ?, Pre = ? where id = ?";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_continuerainstationalert where 1=2";
	
	/**
	 * 根据开始时间，取到已经存在的连阴雨
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Set<String> getExistDataByTimes(String forecastDate) {
		Set<String> existStationSet = new HashSet<String>();
		String query = "select date_format(ForecastDate, '%Y-%m-%d') as ForecastDate, Station_Id_C from t_continuerainstationalert" +
				" where ForecastDate = '" + forecastDate + "' ";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				existStationSet.add(station_Id_C);
			}
		}
		return existStationSet;
	}
	
	public int getExistIdByStationAndStartTime(String startTime, String station_Id_C) {
		String query = "select id from t_continuerainstationalert" +
				" where StartTime = '" + startTime + "' and Station_id_C = '" + station_Id_C + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			int id = (Integer)dataMap.get("id");
			return id;
		}
		return -1;
	}
	public int getCntByForecastTime(String forecastDate) {
		String query = "select count(1) as cnt from t_continuerainstationalert where ForecastDate = '" + forecastDate + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			int cnt = ((Long)dataMap.get("cnt")).intValue();
			return cnt;
		}
		return -1;
	}
	
	public int[] getSumStationSumRainDaysByTimes(String startTime, String endTime) {
		int[] sumStationRainDays = new int[2];
		String query = "select count(1) as stationCnt, sum(RainDays) as RainDays from t_continuerainstationalert where StartTime >= '" + startTime + "' " +
				"and EndTime <= '" + endTime + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			sumStationRainDays[0] = ((Long)dataMap.get("stationCnt")).intValue();
			sumStationRainDays[1] = ((java.math.BigDecimal)dataMap.get("RainDays")).intValue();
		}
		return sumStationRainDays;
	}
	
	public String[] getMinMaxTimeByForecast(String forecastDate) {
		String query = "select date_format(min(StartTime), '%Y-%m-%d') as StartTime, date_format(max(EndTime), '%Y-%m-%d') as EndTime " +
				" from t_continuerainstationalert where ForecastDate = '" + forecastDate + "'";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap dataMap = (HashMap) resultList.get(0);
			String startTime = (String) dataMap.get("StartTime");
			String endTime = (String) dataMap.get("EndTime");
			return new String[]{startTime, endTime};
		}
		return null;
	}
	
	public String getAreaStartTime(String startTime, int STATIONCNT) {
		String query = "select count(1) as cnt from t_continuerainstationalert where StartTime = '" + startTime + "' having count(1) >= " + STATIONCNT;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			return startTime;
		}
		return null;
	}
	
	public String getAreaEndTime(String endTime, int STATIONCNT) {
		String query = "select count(1) as cnt from t_continuerainstationalert where EndTime = '" + endTime + "' having count(1) >= " + STATIONCNT;
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			return endTime;
		}
		return null;
	}
	
	public void insert(List dataList, String forecastDate) {
		List insertList = new ArrayList();
		List updateList = new ArrayList();
//		Set<String> existDataSet = getExistDataByTimes(forecastDate);
		for(int i = 0; i < dataList.size(); i++) {
			HashMap dataMap = (HashMap) dataList.get(i);
			if(dataMap == null) continue;
			int existId = getExistIdByStationAndStartTime((String)dataMap.get("StartTime"), (String)dataMap.get("Station_Id_C"));
			if(existId != -1) {
				dataMap.put("id", existId);
				updateList.add(dataMap);
			} else {
				insertList.add(dataMap);
			}
		}
		insertBatch(INSERTDATA, insertList, getSurfChnMulResultSetMetaData());
		updateBatch2(UPDATEDATA, updateList, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "ForecastDate", "Station_Id_C", "NoSunDays", "RainDays", "Levle", "Pre"});
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
