package com.spd.grid.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spd.grid.domain.Station;
import com.spd.grid.domain.StationVal;
import com.spd.grid.domain.XNStation;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.tool.LogTool;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:
 */
public class LiveUtil {
	/**
	 * @throws Exception 
	 * @作者:wangkun
	 * @日期:2016年12月16日
	 * @修改日期:2016年12月16日
	 * @参数:
	 * @返回:
	 * @说明:获取西南站点的四川站点
	 */
	public List<StationVal> GetLiveMonthData(String elementid,int year,int month){
		DataSource dataSource=DataSource.getInstance();
		Connection conn=dataSource.getConnection();
		String colName="m"+month;
		String tableName=elementid.equals("temp")?"t_month_temp":"t_month_rain";
		String sql="select txn.areaname,txn.stationnum,txn.longitude,txn.latitude,%s from %s tm left join t_xnstation txn on tm.stationnum=txn.stationnum where year=%d and txn.stationnum is not null";
		sql=String.format(sql, colName,tableName,year);
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
	 * @日期:2017年4月25日
	 * @修改日期:2017年4月25日
	 * @参数:lsStation-站点
	 * @返回:所有站点
	 * @说明:获取所有站点这段时间降水
	 */
	public Map<String,List<Double>> getRainLiveBySation(List<XNStation> lsStation,Calendar calStart,Calendar calEnd){
		Map<String,List<Double>> result=new HashMap();
		Calendar curCal=(Calendar) calStart.clone();
		SimpleDateFormat MMdd = new SimpleDateFormat("(MM)dd");
		StringBuilder sbDate=new StringBuilder();
		while(curCal.compareTo(calEnd)<1){
			String strDate=MMdd.format(curCal.getTime());
			strDate=strDate.replace("(", "M");
			strDate=strDate.replace(")", "D");
			sbDate.append(strDate+",");
			curCal.add(Calendar.DATE, 1);
		}
		sbDate.deleteCharAt(sbDate.length()-1);//删除最后一个，
		StringBuilder sbSation=new StringBuilder();
		int stationCount=lsStation.size();
		for(int i=0;i<stationCount;i++){
			String stationNum=lsStation.get(i).getStation_Id_C();
			sbSation.append(stationNum+",");
		}
		sbSation.deleteCharAt(sbSation.length()-1);//删除最后一个，
		String sql="select STATION_ID_C,%s from t_pre_time_0808 where YEAR=2016 and STATION_ID_C in(%s)";
		sql=String.format(sql, sbDate,sbSation);
		LogTool.logger.info(sql);
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			int cols=rs.getMetaData().getColumnCount();
			while(rs.next()){
				List<Double> lsData=new ArrayList();
				String stationNum=rs.getString(1);
				for(int c=2;c<=cols;c++){
					double val=rs.getDouble(c);
					lsData.add(val);
				}
				result.put(stationNum, lsData);
			}
		} catch (Exception ex) {
			LogTool.logger.error("执行sql出错!");
		}
		return result;
	}
}
