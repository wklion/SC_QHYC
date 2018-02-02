package com.spd.qhyc.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.database.DataSourceSingleton;

/**
 * @作者:wangkun
 * @日期:2017年11月8日
 * @公司:spd
 * @说明:
*/
public class ExportDBToTxt {

	public static void main(String[] args) throws Exception {
		//1、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strFile = "C:/Users/lenovo/Desktop/temp/kg.txt";
		FileWriter fw = new FileWriter(strFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		String sql = "select * from t_month_rain";
		PreparedStatement ps = dpConn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		StringBuilder sb = null;
		int cols = rs.getMetaData().getColumnCount();
		while(rs.next()){
			sb = new StringBuilder();
			for(int c=2;c<=cols;c++){
				String val = rs.getString(c);
				sb.append(val);
				sb.append(" ");
			}
			String str = sb.toString().trim();
			bw.write(str);
			bw.newLine();
		}
		bw.close();
		try {
			dpConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("数据导出完成!");
	}

}
