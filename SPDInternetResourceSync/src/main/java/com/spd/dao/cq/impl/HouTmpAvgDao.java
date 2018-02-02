package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.BaseDao;

public class HouTmpAvgDao extends BaseDao {
	
	private String QUERYHOUTMPAVGSTRUCT = "select * from t_tem_avg_hou where 1=2";

	private String ADDHOUTMPAVG = "insert into t_tem_avg_hou (%s) values (%s)";
	
	/**
	 * 查询平均温度中已经存在的数据
	 * @param forecastDate
	 * @param tabName
	 * @return
	 */
	public List getTmpAvgHou(String items, int startYear, int endYear) {
		String query = "select Station_Id_C, Station_Name, year, Lon, Lat, " + items + " from t_tem_avg where Station_Id_C like '5%' and year >= " + startYear + " and year <= " + endYear;
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 批量插入数据
	 * @param dataList
	 */
	public void insertTemAvgHouValue(List dataList) {
		//先判断重复
		insertBatch(ADDHOUTMPAVG, dataList, getTmpAvgHouResultSetMetaData());
	}
	
	public HashMap<String, Object> getExistTemAvg(int startYear, int endYear) {
		//mysql
		String query = "select Station_Id_C, year, month, hou from t_tem_avg_hou where year >= " + startYear + " and year <= " + endYear;		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("year") + "_" + tempMap.get("month") + "_" + tempMap.get("hou");
				hashMap.put(key, "");
			}
		}
		return hashMap;
	}
	
	public List getHouAvgTmp(int year, int month, int hou) {
		String query = "select Station_Id_C, year, month, hou, avgTmp from t_tem_avg_hou where year = " + year
					+ " and month = " + month + " and hou = " + hou + " and Station_Id_C like '5%'";
		List resultList = query(getConn(), query, null);
		return resultList;
	}
	
	public HashMap<String, Double> getHouAvgTmps(int startYear, int endYear, int year, int month, int hou) {
		String query = "select Station_Id_C, avg(avgTmp) as avgTmp from t_tem_avg_hou where year >= " + startYear + " and year <= " + endYear
				+ " and month = " + month + " and hou = " + hou + " group by Station_Id_C";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Double avgTmp = (Double) dataMap.get("avgTmp");
			if(avgTmp == null) continue;
			resultMap.put(station_Id_C, avgTmp);
		}
		return resultMap;
	}
	
	public Double getHouAvgTmpByStation(String station_Id_C, int year, int month, int hou) {
		String query = "select Station_Id_C, year, month, hou, avgTmp from t_tem_avg_hou where year = " + year
					+ " and month = " + month + " and hou = " + hou + " and Station_Id_C = '" + station_Id_C + "'";
		List resultList = query(getConn(), query, null);
		if(resultList == null || resultList.size() == 0) return null;
		HashMap dataMap = (HashMap) resultList.get(0);
		return (Double) dataMap.get("avgTmp");
	}
	
	public Double getAvgTmpByStationAndYears(int startYear, int endYear, int month, int[] hous, String station_Id_C) {
		String houStr = "";
		for(int i = 0; i < hous.length; i++) {
			houStr = houStr + hous[i];
			if(i != hous.length - 1) {
				houStr += ",";
			}
		}
		String query = "select avg(avgTmp) as avgTmp from t_tem_avg_hou where year >= " + startYear 
				+ " and year <= " + endYear + " and station_Id_C = '" + station_Id_C + "' and month = " + month + " and hou in (" + houStr + ")";
		List resultList = query(getConn(), query, null);
		if(resultList != null && resultList.size() > 0) {
			HashMap resultMap = (HashMap) resultList.get(0);
			Double result = (Double) resultMap.get("avgTmp");
			return result;
		}
		return null;
	}
	
	/**
	 * 查询t_tem_avg_hou表结构
	 * @return
	 */
	public ResultSetMetaData getTmpAvgHouResultSetMetaData() {
		return getTableStruct(getConn(), QUERYHOUTMPAVGSTRUCT);
	}
	
}
