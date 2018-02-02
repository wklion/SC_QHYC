package com.spd.grid.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.spd.grid.domain.StationVal;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.tool.LogTool;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:
 */
public class HosUtil {
	/** 
	 * @作者:wangkun
	 * @日期:2016年12月16日
	 * @修改日期:2016年12月16日
	 * @参数:
	 * @返回:
	 * @说明:获取西南站点的四川站点
	 */
	public List<StationVal> GetHosMonthData(String elementid,int month){
		DataSource dataSource=DataSource.getInstance();
		Connection conn=dataSource.getConnection();
		String colName="m"+month;
		String tableName=elementid.equals("temp")?"t_hos_t_monthavg":"t_hos_r_monthavg";
		String sql="select ts.StationName,ts.StationNum,ts.Longitude,ts.Latitude,%s from %s thm left join t_station ts on thm.stationnum=ts.StationNum where ts.StationNum is not null";
		sql=String.format(sql, colName,tableName);
		List<StationVal> lsResult=new ArrayList<>();
		try{
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			StationVal sv=null;
			while(rs.next()){
				sv=new StationVal();
				String stationName=rs.getString(1);
				String stationNum=rs.getString(2);
				double lon=rs.getDouble(3);
				double lat=rs.getDouble(4);
				double val=rs.getDouble(5);
				sv.setStationName(stationName);
				sv.setStationNum(stationNum);
				sv.setLongitude(lon);
				sv.setLatitude(lat);
				sv.setValue(val);
				lsResult.add(sv);
			}
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
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
	 * @日期:2016年12月29日
	 * @修改日期:2016年12月29日
	 * @参数:elementid--要素id,month-月份
	 * @返回:站点数据
	 * @说明:获取历史数据
	 */
	public List<StationVal> GetHosData(String elementid,int month){
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String colName="m"+month;
		String tableName=elementid.equals("temp")?"v_hos_temp":"v_hos_rain";
		String sql="select vh.stationnum,vh.longitude,vh.latitude,(%s) as val from %s vh group by vh.stationnum";
		sql=String.format(sql, colName,tableName);
		List<StationVal> lsResult=new ArrayList<>();
		try{
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			StationVal sv=null;
			while(rs.next()){
				sv=new StationVal();
				//String stationName=rs.getString(1);
				String stationNum=rs.getString(1);
				double lon=rs.getDouble(2);
				double lat=rs.getDouble(3);
				double val=rs.getDouble(4);
				//sv.setStationName(stationName);
				sv.setStationNum(stationNum);
				sv.setLongitude(lon);
				sv.setLatitude(lat);
				sv.setValue(val);
				lsResult.add(sv);
			}
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsResult;
	}
}
