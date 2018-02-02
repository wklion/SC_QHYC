package com.spd.qhyc.util;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spd.qhyc.model.XNStation;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:站点查询
 */
public class StationUtil {
	private static String root=Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
	/**
	 * @throws Exception 
	 * @作者:wangkun
	 * @日期:2016年12月27日
	 * @修改日期:2016年12月27日
	 * @参数:
	 * @返回:站点信息
	 * @说明:获取西南站
	 */
	public List GetXNSatation(String filterAreaCode,DruidPooledConnection dpConn) throws Exception{
		String sql="";
		if(filterAreaCode.equals("")){
			sql="select * from t_xnstation";
		}
		else{
			sql="select * from t_xnstation where Admin_Code_CHN like '%d%';";
			sql=String.format(sql, filterAreaCode);
		}
		ResultSet rs = null;
		try {
			PreparedStatement ps = dpConn.prepareStatement(sql);
			rs=ps.executeQuery();
		} catch (SQLException e) {
			System.out.println("GetSCSatation()--执行sql出错!");
		}
		DBUtil dbUtil = new DBUtil();
		List lsXNStation = dbUtil.populate(rs, XNStation.class);
		return lsXNStation;
	}
}
