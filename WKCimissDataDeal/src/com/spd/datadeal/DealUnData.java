package com.spd.datadeal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.spd.jdbc.DbPoolConnection;
import com.spd.schedule.CIMISSDayExecutor;
import com.spd.util.DateUtil;

public class DealUnData {
	public void excute() throws Exception {
		Calendar calStart = Calendar.getInstance();//起始日期
		calStart.set(Calendar.YEAR, 1981);
		calStart.set(Calendar.MONTH, 0);
		calStart.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar calEnd = Calendar.getInstance();//结束日期
		
		DbPoolConnection dbp = DbPoolConnection.getInstance();
		DruidPooledConnection conn = dbp.getConnection();
		
		String pSql = "select %s from t_pre_time_0808 where year=%d";
		while(calStart.compareTo(calEnd)==-1) {//晚于
			String strDate = DateUtil.sdf_yyyyMMdd000000.format(calStart.getTime());
			System.out.println("当前日期:"+strDate);
			//String[] strDates = strDate.split("_");
			int year = Integer.parseInt(strDate.substring(0,4));
			String strMonth = strDate.substring(4,6);
			String strDay = strDate.substring(6,8);
			String strMD = "M"+strMonth+"D"+strDay;
			String sql = String.format(pSql, strMD,year);
			DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
			ResultSet rs= ps.executeQuery();
			//获取数据行数
			rs.last();
			int rows = rs.getRow();
			rs.first();
			if(rows==0) {
				calJar(strDate);
			}
			else {
				while(rs.next()) {
					Object obj = rs.getObject(1);
					if(obj==null) {
						calJar(strDate);
						calStart.add(Calendar.DATE, 1);
						break;
					}
				}
			}
			calStart.add(Calendar.DATE, 1);
		}
		conn.close();
	}
	private void calJar(String strDate) throws Exception {
		String[] dates = {strDate};
		CIMISSDayExecutor.main(dates);
	}
}
