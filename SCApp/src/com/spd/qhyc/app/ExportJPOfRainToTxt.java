package com.spd.qhyc.app;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.CimissMonthData;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.util.DBUtil;

/**
 * @作者:wangkun
 * @日期:2017年12月5日
 * @公司:spd
 * @说明:导出降水距平
*/
public class ExportJPOfRainToTxt {
	public static void main(String[] args) throws Exception {
		//1、读取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		//2、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3、读取数据
		String sql = "select * from t_month_rain";
		PreparedStatement ps = dpConn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		CimissMonthData cimissMonthData = (CimissMonthData) dbUtil.populate(rs, CimissMonthData.class);
		
	}
}
