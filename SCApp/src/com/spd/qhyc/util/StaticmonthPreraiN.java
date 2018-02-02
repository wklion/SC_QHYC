package com.spd.qhyc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;

public class StaticmonthPreraiN {
	static Logger logger = LogManager.getLogger("mylog");
	static String fInsertSql = "insert into t_month_rain(province,stationname,stationnum,year,m%d) values(?,?,?,?,?)";
	static String fUpdateSql = "update t_month_rain set m%d=? where year=? and stationnum=?";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// 1、获取配置
			ConfigHelper configHelper = new ConfigHelper();
			Config config = configHelper.getConfig();
			// 2、连接数据库
			DruidDataSource dds = DataSourceSingleton.getInstance();
			DruidPooledConnection dpConn = null;
			try {
				dpConn = dds.getConnection();
				dpConn.setAutoCommit(false);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			Calendar calNow = Calendar.getInstance();
			if (args.length > 0) {
				if (args[0].length() != 6) {
					logger.error("输入日期错误,应是201712这样的格式");
					return;
				}
				calNow = DateUtil.parse("yyyyMM", args[0]);
			}

			//更新降雨量数据
			editRainData(calNow, dpConn, config);

			dpConn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void editRainData(Calendar cal,DruidPooledConnection conn,Config config) throws Exception{
		String thisStrDate = DateUtil.format("yyyy-MM", cal);
		logger.info("正在执行"+thisStrDate);
		Calendar tempCal = (Calendar) cal.clone();
		int curYear = tempCal.get(Calendar.YEAR);
		int curMonth = tempCal.get(Calendar.MONTH)+1;
		//查询数据是否存在
		String exitSql = "select * from t_month_rain where year=%s";//%s固定字符类型
		//查询所有降雨数据语句
		String selectSql = "select * from t_pre_time_0808 where year=%s";
		exitSql = String.format(exitSql, curYear);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(exitSql);
		ResultSet rs = ps.executeQuery();//ps存放每月的降雨
		rs.last();
		int rows = rs.getRow();
		rs.close();
		Boolean isInsert = rows>0?false:true;
		String sql = "";
		if(isInsert){
			logger.info("执行插入");
			sql = String.format(fInsertSql, curMonth);//插入
		}
		else{
			logger.info("执行更新");
			sql = String.format(fUpdateSql, curMonth);//更新
		}
		ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);//ps查询完后的存放每月的降雨
	}

}
