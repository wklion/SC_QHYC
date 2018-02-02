package com.spd.qhyc.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.app.StationVal;
import com.spd.qhyc.model.MonthAvg;

public class DBHelper {
	static Logger logger = LogManager.getLogger("mylog");
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月22日
	 * @修改日期:2018年1月22日
	 * @参数:
	 * @返回:
	 * @说明:
	 */
	public void insertMonthForecastData(DruidPooledConnection dpConn,List<StationVal> lsVal,String elementID,Calendar calMakeDate,Calendar calForecastDate,String forecastMethodName) throws Exception {
		String strMakeDate = DateUtil.format("yyyyMM01", calMakeDate);
		String strForecastDate = DateUtil.format("yyyyMM", calForecastDate);
		String tableName = elementID.toLowerCase().equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
		String selectSql = "select * from %s where makeDate='%s' and forecastDate='%s' and method='%s'";
		selectSql = String.format(selectSql, tableName,strMakeDate,strForecastDate,forecastMethodName);
		PreparedStatement ps = dpConn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rowCount = rs.getRow();
		if(rowCount>0) {
			logger.info(elementID+","+calMakeDate+","+calForecastDate+","+forecastMethodName+"数据已存在!");
			rs.close();
			ps.close();
			return;
		}
		String sqlF = "insert into %s(method,makeDate,forecastDate,stationNum,val) values('%s','%s',%s,?,?)";
		sqlF = String.format(sqlF, tableName,forecastMethodName,strMakeDate,strForecastDate);
		dpConn.setAutoCommit(false);
		ps = dpConn.prepareStatement(sqlF);
		for(StationVal sv:lsVal) {
			String stationNum = sv.getStationNum();
			double val = sv.getValue();
			ps.setString(1, stationNum);
			ps.setDouble(2, val);
			ps.addBatch();
		}
		ps.executeBatch();
		dpConn.commit();
		dpConn.setAutoCommit(true);
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月23日
	 * @修改日期:2018年1月23日
	 * @参数:
	 * @返回:
	 * @说明:获取月平均
	 */
	public List<MonthAvg> getMonthAvg(DruidPooledConnection dpConn,String elementID){
	    String sql = "select * from %s";
	    String tableName = elementID.toLowerCase().equals("temp")?"v_hos_temp":"v_hos_rain";
	    sql = String.format(sql, tableName);
	    List<MonthAvg> lsMonthAvg = null;
	    try {
            PreparedStatement ps = dpConn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DBUtil dbUtil = new DBUtil();
            lsMonthAvg = dbUtil.populate(rs, MonthAvg.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    return lsMonthAvg;
	}
}
