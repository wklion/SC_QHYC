package com.spd.datadeal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledStatement;
import com.spd.jdbc.DbPoolConnection;

public class MonthData {
	public void excute(Calendar cal) throws Exception {
		DbPoolConnection dbp = DbPoolConnection.getInstance();
		DruidPooledConnection con = dbp.getConnection();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String strMonth = month<10?"0"+month:month+"";
		//当前月的所有日期
		StringBuilder sbDays = new StringBuilder();
		while(day>0) {
			sbDays.append("M"+strMonth);
			String strDay = day<10?"0"+day:day+"";
			sbDays.append("D"+strDay);
			day--;
			sbDays.append(",");
		}
		sbDays = sbDays.deleteCharAt(sbDays.length()-1);
		String sql = "select station_id_c,%s from t_pre_time_0808 where year=%d";
		sql = String.format(sql, sbDays,year);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) con.prepareStatement(sql);
		ResultSet rs= ps.executeQuery();
		int cols = rs.getMetaData().getColumnCount();
		Map<String,Double> stationVal = new HashMap();
		while(rs.next()) {
			String stationNum = rs.getString(1);
			double sum = rs.getDouble(2);
			for(int i=3;i<=cols;i++) {
				double val = rs.getDouble(i);
				sum+=val;
			}
			sum = ((int)(sum*100))/100.0;
			stationVal.put(stationNum, sum);
		}
		
		//入库,先判断是否有这个月的数据，否则更新
		sql = "select * from t_month_rain where year=%d";
		sql = String.format(sql, year);
		ps = (DruidPooledPreparedStatement) con.prepareStatement(sql);
		rs= ps.executeQuery();
		con.setAutoCommit(false);
		if(rs.next()) {//更新
			sql = "update t_month_rain set m%d=? where year=%d and stationnum=?";
			sql = String.format(sql, month,year);
			ps = (DruidPooledPreparedStatement) con.prepareStatement(sql);
			ps.clearBatch();
			for(String strStationNum:stationVal.keySet()) {
				double val = stationVal.get(strStationNum);
				ps.setDouble(1, val);
				ps.setString(2, strStationNum);
				ps.addBatch();
			}
			int[] result = ps.executeBatch();
			con.commit();
		}
		else {//插入
			sql = "insert into t_month_rain(stationnum,year,m%d) values(?,%d,?)";
			sql = String.format(sql, month,year);
			ps = (DruidPooledPreparedStatement) con.prepareStatement(sql);
			for(String strStationNum:stationVal.keySet()) {
				double val = stationVal.get(strStationNum);
				ps.setString(1, strStationNum);
				ps.setDouble(2, val);
				ps.addBatch();
			}
			int[] result = ps.executeBatch();
			con.commit();
		}
		ps.close();
		System.out.println(sql);
	}
}
