package com.spd.grid.tool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.grid.domain.Station;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.jdbc.DataSourceSingleton;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年11月4日
 * @DESCRIPTION:站点相关
 */
public class StationUtil {
	/**
	 * @throws Exception 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月4日
	 * @RETURN:
	 * @PARAM:
	 * @DESCRIPTION:计算区域平均
	 */
	public List<Double> CalAreaAvg(List<Station> lsStation,Calendar calStart,Calendar calEnd){
		//获取日期--start
		StringBuilder sbDate=new StringBuilder();
		Calendar calCurrent=(Calendar) calStart.clone();
		while(calCurrent.compareTo(calEnd)<1)
		{
			String strDate=Common.MMMDdd.format(calCurrent.getTime());
			strDate=strDate.replace("(", "M");//替换月
			strDate=strDate.replace(")", "D");//替换日
			sbDate.append("avg("+strDate+"),");
			calCurrent.add(Calendar.DATE, 1);
		}
		int delIndex=sbDate.lastIndexOf(",");
		sbDate=sbDate.deleteCharAt(delIndex);
		//获取日期--end
		//获取站点--start
		StringBuilder sbStation=new StringBuilder();
		int stationCount=lsStation.size();
		for(int i=0;i<stationCount;i++)
		{
			String stationNum=lsStation.get(i).getStationNum();
			sbStation.append("'"+stationNum+"',");
		}
		delIndex=sbStation.lastIndexOf(",");
		sbStation=sbStation.deleteCharAt(delIndex);
		//获取站点--end
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		List<Double> lsResult=new ArrayList<>();
		try{
			Statement  stmt = conn.createStatement();
			String sql=String.format("select %s from t_pre_time_0808 where STATION_ID_C in(%s)", sbDate,sbStation);
			ResultSet resultSet = stmt.executeQuery(sql);
			int cols=resultSet.getMetaData().getColumnCount();
			while(resultSet.next()){
				for(int i=1;i<=cols;i++){
					Double val=resultSet.getDouble(i);
					if(val>500){
							continue;
					}
					val=(int)(val*100)/100.0;
					lsResult.add(val);
				}
			}
			conn.close();
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
		}
		return lsResult;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年4月6日
	 * @修改日期:2017年4月6日
	 * @参数:station-站点;calStart-开始日期;calEnd-结束日期
	 * @返回:站点数据
	 * @说明:获取站点实况
	 */
	public List<Double> getStationLive(Station station,Calendar calStart,Calendar calEnd){
		//获取日期--start
		StringBuilder sbDate=new StringBuilder();
		Calendar calCurrent=(Calendar) calStart.clone();
		while(calCurrent.compareTo(calEnd)<0){
			String strDate=Common.MMMDdd.format(calCurrent.getTime());
			strDate=strDate.replace("(", "M");//替换月
			strDate=strDate.replace(")", "D");//替换日
			sbDate.append("avg("+strDate+"),");
			calCurrent.add(Calendar.DATE, 1);
		}
		int delIndex=sbDate.lastIndexOf(",");
		sbDate=sbDate.deleteCharAt(delIndex);
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		List<Double> lsResult=new ArrayList<>();
		try{
			Statement  stmt = conn.createStatement();
			String sql=String.format("select %s from t_pre_time_0808 where STATION_ID_C in(%s)", sbDate,station.getStationNum());
			ResultSet resultSet = stmt.executeQuery(sql);
			int cols=resultSet.getMetaData().getColumnCount();
			while(resultSet.next()){
				for(int i=1;i<=cols;i++){
					Double val=resultSet.getDouble(i);
					val=(int)(val*100)/100.0;
					lsResult.add(val);
				}
			}
			conn.close();
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
		}
		return lsResult;
	}
	/**
	 * @throws Exception 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月4日
	 * @RETURN:
	 * @PARAM:
	 * @DESCRIPTION:获取所有站点
	 */
	public List<Station> GetAllStation(){
		DruidDataSource dds=DataSourceSingleton.getBaseInstance();
		List<Station> result = new ArrayList<>();
		try{
			DruidPooledConnection conn=dds.getConnection();
			Statement  stmt = conn.createStatement();
			String sql="select * from t_station";
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				result.add(new Station(resultSet.getString("StationNum"), resultSet.getString("StationName"), 
						resultSet.getDouble("Latitude"), resultSet.getDouble("Longitude"), resultSet.getDouble("Height"), 
						resultSet.getInt("ZoomLevel"), resultSet.getInt("Type"), resultSet.getString("AreaCode")));
			}
			conn.close();
		}
		catch(Exception ex){
			
		}
		return result;
	}
}
