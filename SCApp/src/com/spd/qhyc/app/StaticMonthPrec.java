package com.spd.qhyc.app;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.CimissMonthData;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.util.DBUtil;
import com.spd.qhyc.util.DateUtil;

public class StaticMonthPrec {
	static Logger logger = LogManager.getLogger("mylog");
	static String fInsertSql = "insert into t_month_rain(province,stationname,stationnum,year,m%d) values(?,?,?,?,?)";
	static String fUpdateSql = "update t_month_rain set m%d=? where year=? and stationnum=?";	
	static Map<String,XNStation> map=new HashMap<>();
	public static void main(String[] args) {
		try {
			args=new String[1];
			args[0]="201712";
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
			calNow.add(Calendar.MONTH, -1);
			if (args.length > 0) {
				if (args[0].length() != 6) {
					logger.error("输入日期错误,应是201712这样的格式");
					return;
				}
				calNow = DateUtil.parse("yyyyMM", args[0]);
			}
			//更新降雨量数据			
			String sqlstation = "select * from t_xnstation";
			PreparedStatement stationps = dpConn.prepareStatement(sqlstation);
			ResultSet rs_station = stationps.executeQuery();
			DBUtil dbUtil = new DBUtil();
			List<XNStation> lsXNStation = dbUtil.populate(rs_station, XNStation.class);
			for (XNStation xnStation:lsXNStation) {
				String num=xnStation.getStation_Id_C();;
				map.put(num, xnStation);
			}
        	editRainData(calNow, dpConn, config);
        	System.out.println("over");
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
		Calendar rainCal = (Calendar) cal.clone();
		int curYear = rainCal.get(Calendar.YEAR);
		int curMonth = rainCal.get(Calendar.MONTH)+1;
		
		//查询数据是否存在
		String exitSql = "select * from t_month_rain where year=%s";//%s固定字符类型
		exitSql = String.format(exitSql, curYear);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(exitSql);
		ResultSet rs = ps.executeQuery();
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
		ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
		String selectSql = "select * from t_pre_time_2020 where year=%s";
		selectSql = String.format(selectSql, curYear);
		DruidPooledPreparedStatement ps_pre_time = (DruidPooledPreparedStatement) conn.prepareStatement(selectSql);
		ResultSet rs_pre_time = ps_pre_time.executeQuery();
//		rainCal.set(Calendar.DATE, 1);
		List<Double> ms = null;
		while(rs_pre_time.next()){
		int year = rs_pre_time.getInt("YEAR");
		//站点
		String stationnum = rs_pre_time.getString("STATION_ID_C");
		//每一个月的降雨和值
		BigDecimal count = null;
		rainCal.set(Calendar.YEAR, year);//设置年份
		//字段名称：如M01D01
		String key = "";
		//m负责拼凑前部分字段：如M01 d负责拼凑后部分字段：如D01
		String m = "",d="";
		//存储当前站点12个月的降雨量
		ms = new ArrayList<Double>();
		count = new BigDecimal("0");
		if(curMonth < 10){
			m = "M0" + curMonth;
		} else {
			m = "M" + curMonth;
		}
		
		rainCal.set(Calendar.MONTH, curMonth-1);//设置月份
		//获取天数
		int days = rainCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int j=1;j<=days;j++){//循环天数相加
			if(j < 10){
				d = "D0" + j;
			} else {
				d = "D" + j;
			}
			
			//拼凑数据库表字段 效果：M01D01
			key = m + d;
			
			if(rs_pre_time.getDouble(key) == 999999){//999999 跳过相加
				continue;
			}
			
			count = count.add(new BigDecimal(rs_pre_time.getDouble(key)));
		}
//		ms.add(count.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());//存取保留小数后两位的降雨量
		double val=count.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//		 String fSelectsql="select * from t_xnstation where Station_Id_C=%s";
//		 fSelectsql=String.format(fSelectsql, stationnum);
//		DruidPooledPreparedStatement ps3 = (DruidPooledPreparedStatement) conn.prepareStatement(fSelectsql);
//		ResultSet rs3 = ps3.executeQuery();//查询的站点信息
//		rs3.first();
//		String stationName=rs3.getString("Station_Name");
//		String province=rs3.getString("Province");	
		XNStation isstation=map.get(stationnum);
		if(isstation==null)
		{
			continue;
		}
		String province=isstation.getProvince();
		String stationName=isstation.getStation_Name();
		if(isInsert) {
 				ps.setString(1, province);
				ps.setString(2, stationName);
				ps.setString(3, stationnum);
				ps.setInt(4, curYear);
				ps.setDouble(5, val);
 			}
 		else {
 				ps.setDouble(1, val);
 				ps.setInt(2, curYear);
				ps.setString(3, stationnum);
 			}
			ps.addBatch();
			System.out.println("站点：" + stationnum + ",月份：" + curMonth+",年份：" + curYear+",降雨值"+val);
	   }
		rs.close();
		logger.info("执行批量操作!");
//		ps_pre_time.executeBatch();
		ps.executeBatch();
		conn.commit();
	 	logger.info("批量操作完成!");
    	ps_pre_time.close();
	 	ps.close();
	}
}
