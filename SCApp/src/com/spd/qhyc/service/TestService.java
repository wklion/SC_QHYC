package com.spd.qhyc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.model.ForcastData;
import com.spd.qhyc.model.MonthData;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.util.CommonUtil;
import com.spd.qhyc.util.DBUtil;
import com.spd.qhyc.util.DateUtil;

public class TestService {
	static Logger logger = LogManager.getLogger("mylog");
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:获取观测数据
	 */
	public Map<String,Double> getObvData(String elementID,int year,int month,DruidPooledConnection dpConn) throws Exception {
		String tableName = elementID.equals("temp")?"t_month_temp":"t_month_rain";
		String sqlF = "select * from %s where year=%d";
		String sql = sqlF.format(sqlF, tableName,year);
		PreparedStatement ps = dpConn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<MonthData> lsMonthData = dbUtil.populate(rs, MonthData.class);
		rs.close();
		ps.close();
		if(lsMonthData.isEmpty()){
			System.out.println("观测数据为空!");
			return null;
		}
		Map<String,Double> mapObv = new HashMap<>();
		for(MonthData monthData:lsMonthData){
			String stationNum = monthData.getStationNum();
			double val = getMonthData(monthData,month);
			mapObv.put(stationNum, val);
		}
		return mapObv;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:计算距平
	 */
	public Map<String,Double> calJP(String elementID,Map<String,Double> mapObv,double[] avgData,List<XNStation> lsXNStation) {
		Map<String,Double> mapResult = new HashMap();
		int stationSize = lsXNStation.size();
		for(int i=0;i<stationSize;i++) {
			XNStation station = lsXNStation.get(i);
			String sn = station.getStation_Id_C();
			Double obvVal = mapObv.get(sn);
			double val = -9999;
			if(obvVal!=null) {
				double avgVal = avgData[i];
				val = elementID.toLowerCase().equals("temp")?obvVal-avgVal:100*(obvVal-avgVal)/avgVal;
			}
			mapResult.put(sn, val);
		}
		return mapResult;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:获取月预报数据
	 */
	public Map<String,Double> getForecastData(String elementID,Calendar cal,DruidPooledConnection dpConn,String method,String makeDate) throws Exception {
		String tableName = elementID.equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
		String sqlF = "select * from %s where forecastDate='%s' and method ='%s' and makeDate='%s'";
		String strForecastDate = DateUtil.format("yyyyMM", cal);
		String sql = sqlF.format(sqlF, tableName,strForecastDate,method,makeDate);
		PreparedStatement ps = dpConn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<ForcastData> lsForcastData = dbUtil.populate(rs, ForcastData.class);
		rs.close();
		ps.close();
		Map<String,Double> mapForecast = new HashMap<>();
		for(ForcastData forcastData:lsForcastData){
			String stationNum = forcastData.getStationNum();
			double val = forcastData.getVal();
			mapForecast.put(stationNum, val);
		}
		return mapForecast;
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:插入月检验数据
	 */
	public int insertMonthTestData(DruidPooledConnection dpConn,String elementID,String makeDate,String forecastDate,String testName,String forecastName,String areaCode,double val) throws Exception {
		String selectSql = "select * from t_monthforecasttest where elementID='%s' and makeDate='%s' and forecastDate='%s' and testName='%s'  and forecastName='%s' and areaCode='%s'";
		selectSql = String.format(selectSql, elementID,makeDate,forecastDate,testName,forecastName,areaCode);
		PreparedStatement ps = dpConn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rowCount = rs.getRow();
		if(rowCount>0) {
			logger.info(elementID+","+makeDate+","+forecastDate+","+forecastName+"数据已存在!");
			rs.close();
			ps.close();
			return 0;
		}
		String sql = "insert into t_monthforecasttest(elementID,makeDate,forecastDate,testName,forecastName,areaCode,val) values('%s','%s','%s','%s','%s','%s',%f)";
		sql = String.format(sql, elementID,makeDate,forecastDate,testName,forecastName,areaCode,val);
		ps = dpConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:插入季检验数据
	 */
	public int insertSeasonTestData(DruidPooledConnection dpConn,String elementID,String makeDate,int seasonIndex,String testName,String forecastName,String areaCode,double val) throws Exception {
		String selectSql = "select * from t_seasonforecasttest where elementID='%s' and makeDate='%s' and season=%d and testName='%s'  and forecastName='%s' and areaCode='%s'";
		selectSql = String.format(selectSql, elementID,makeDate,seasonIndex,testName,forecastName,areaCode);
		PreparedStatement ps = dpConn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rowCount = rs.getRow();
		if(rowCount>0) {
			logger.info(elementID+","+makeDate+","+seasonIndex+","+forecastName+"数据已存在!");
			rs.close();
			ps.close();
			return 0;
		}
		String sql = "insert into t_seasonforecasttest(elementID,makeDate,season,testName,forecastName,areaCode,val) values('%s','%s','%d','%s','%s','%s',%f)";
		sql = String.format(sql, elementID,makeDate,seasonIndex,testName,forecastName,areaCode,val);
		ps = dpConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:插入年检验数据
	 */
	public int insertYearTestData(DruidPooledConnection dpConn,String elementID,String makeDate,String testName,String forecastName,String areaCode,double val) throws Exception {
		String selectSql = "select * from t_yearforecasttest where elementID='%s' and makeDate='%s' and testName='%s'  and forecastName='%s' and areaCode='%s'";
		selectSql = String.format(selectSql, elementID,makeDate,testName,forecastName,areaCode);
		PreparedStatement ps = dpConn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rowCount = rs.getRow();
		if(rowCount>0) {
			logger.info(elementID+","+makeDate+","+forecastName+"数据已存在!");
			rs.close();
			ps.close();
			return 0;
		}
		String sql = "insert into t_yearforecasttest(elementID,makeDate,testName,forecastName,areaCode,val) values('%s','%s','%s','%s','%s',%f)";
		sql = String.format(sql, elementID,makeDate,testName,forecastName,areaCode,val);
		ps = dpConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	/**
	 * @autor:杠上花
	 * @date:2018年1月30日
	 * @modifydate:2018年1月30日
	 * @param:
	 * @return:
	 * @description:PS检验评分计算
	 */
	public double PSTestCal(Map<String,Double> mapJP,Map<String,Double> mapForecast,String elementID){
		CommonUtil commonUtil = new CommonUtil();
		int n = mapJP.size();
		int n0 = commonUtil.getN0(mapJP, mapForecast);
		int n1 = commonUtil.getN1(mapJP, mapForecast,elementID);
		int n2 = commonUtil.getN2(mapJP, mapForecast, elementID);
		int m = commonUtil.getM(mapJP, mapForecast, elementID);
		double divisor = 2*n0+2*n1+4*n2;
		double deDivisor = n+n0+2*n1+4*n2+m;
		double val = 100*divisor/deDivisor;
		val = (int)(val*100)/100.0;
		return val;
	}
	/**
	 * @autor:杠上花
	 * @date:2018年1月30日
	 * @modifydate:2018年1月30日
	 * @param:
	 * @return:
	 * @description:CC检验评分计算
	 */
	public double CCTestCal(Map<String,Double> mapJP,Map<String,Double> mapForecast,List<XNStation> lsStation){
		double val = 0;
		CommonUtil commonUtil = new CommonUtil();
		double obvAvg = commonUtil.calAvg(mapJP);
		double forecastAvg = commonUtil.calAvg(mapForecast);
		double divisor = 0;//计算分子
		double deDivisor =0 ;//计算分母
		double deDivisorForecast = 0;
		double deDivisorObv = 0;
		for(XNStation station:lsStation) {
			String sn = station.getStation_Id_C();
			double forecastVal = mapForecast.get(sn);
			double forecastCha = forecastVal - forecastAvg;
			double obvVal = mapJP.get(sn);
			double obvCha = obvVal - obvAvg;
			divisor += forecastCha*obvCha;
			deDivisorForecast += forecastCha*forecastCha;
			deDivisorObv += obvCha*obvCha;
		}
		deDivisor = Math.sqrt(deDivisorForecast*deDivisorObv);
		val = divisor/deDivisor;
		val = (int)(val*100)/100.0;
		return val;
	}
	private static double getMonthData(MonthData monthData,int month){
		double val = 0;
		switch(month){
			case 1:
				val = monthData.getM1();
				break;
			case 2:
				val = monthData.getM2();
				break;
			case 3:
				val = monthData.getM3();
				break;
			case 4:
				val = monthData.getM4();
				break;
			case 5:
				val = monthData.getM5();
				break;
			case 6:
				val = monthData.getM6();
				break;
			case 7:
				val = monthData.getM7();
				break;
			case 8:
				val = monthData.getM8();
				break;
			case 9:
				val = monthData.getM9();
				break;
			case 10:
				val = monthData.getM10();
				break;
			case 11:
				val = monthData.getM11();
				break;
			case 12:
				val = monthData.getM12();
				break;
			default:
				break;
		}
		return val;
	}
}
