package com.spd.grid.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;








import com.spd.grid.domain.StationVal;
import com.spd.grid.jdbc.DataSource;

/**
 * @作者:wangkun
 * @日期:2016年12月29日
 * @公司:spd
 * @说明:
 */
public class SynthesizeUtil {
	public List<StationVal> GetJP(String elementid,int year,int month){
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String tablename=elementid.equals("temp")?"t_month_temp":"t_month_rain";
		String sql="select tm.stationname,vhr.stationnum,vhr.longitude,vhr.latitude,round(100*(tm.m%d-vhr.m%d)/vhr.m%d,0) as m%d from %s tm right join v_hos_rain vhr on tm.stationnum=vhr.stationnum where year=%d";
		sql=String.format(sql, month,month,month,month,tablename,year);
		System.out.println(sql);
		List<StationVal> lsResult=new ArrayList<>();
		StationVal sv=null;
		try{
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				sv=new StationVal();
				String stationName=rs.getString(1);
				String stationNum=rs.getString(2);
				double lon=rs.getDouble(3);
				double lat=rs.getDouble(4);
				double val=rs.getDouble(5);
				val = val/10.0;
				sv.setStationName(stationName);
				sv.setStationNum(stationNum);
				sv.setLongitude(lon);
				sv.setLatitude(lat);
				sv.setValue(val);
				lsResult.add(sv);
			}
			ps.close();
		}
		catch(Exception ex){
			
		}
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lsResult;
	}
}
