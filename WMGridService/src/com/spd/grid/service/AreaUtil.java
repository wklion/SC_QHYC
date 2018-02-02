package com.spd.grid.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.spd.grid.jdbc.DataSource;

/**
 * @作者:wangkun
 * @日期:2017年1月1日
 * @公司:spd
 * @说明:区域
 */
public class AreaUtil {
	/**
	 * @作者:wangkun
	 * @日期:2017年1月1日
	 * @修改日期:2017年1月1日
	 * @参数:
	 * @返回:市级
	 * @说明:获取市
	 */
	public List<String> GetCity(){
		DataSource dataSource=DataSource.getInstance();
		Connection conn=dataSource.getConnection();
		String sql="select * from t_city";
		PreparedStatement ps;
		List<String> lsResult=null;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			lsResult=new ArrayList<>();
			while(rs.next()){
				String name=rs.getString(3);
				lsResult.add(name);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsResult;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月1日
	 * @修改日期:2017年1月1日
	 * @参数:
	 * @返回:区域
	 * @说明:获取区域
	 */
	public List<String> GetArea(){
		DataSource dataSource=DataSource.getInstance();
		Connection conn=dataSource.getConnection();
		String sql="select distinct(areaname) from t_city";
		PreparedStatement ps;
		List<String> lsResult=null;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			lsResult=new ArrayList<>();
			while(rs.next()){
				String name=rs.getString(1);
				lsResult.add(name);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsResult;
	}
}
