package com.spd.grid.station;

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
import com.mg.objects.GeoPoint;
import com.mg.objects.GeoRegion;
import com.mg.objects.Workspace;
import com.spd.grid.config.ConfigHelper;
import com.spd.grid.domain.Station;
import com.spd.grid.domain.XNStation;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.jdbc.DataSourceSingleton;
import com.spd.grid.tool.DBUtil;
import com.spd.grid.tool.GeoRel;
import com.spd.grid.tool.LogTool;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:站点查询
 */
public class StationUtil {
	private static String root=Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
	/**
	 * @作者:wangkun
	 * @日期:2016年12月27日
	 * @修改日期:2016年12月27日
	 * @参数:
	 * @返回:站点信息
	 * @说明:获取四川站
	 */
	public List<Station> GetSCSatation(){
		DataSource dataSource=DataSource.getInstance();
		Connection conn=dataSource.getConnection();
		String sql="select * from t_station";
		List<Station> lsResult=new ArrayList<>();
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			Station station=null;
			while(rs.next()){
				String snum=rs.getString(1);
				String sna=rs.getString(2);
				double lon=rs.getDouble(3);
				double lat=rs.getDouble(4);
				double hei=rs.getDouble(5);
				station=new Station(snum,sna,lon,lat,hei,1,1,"");
				lsResult.add(station);
			}
			ps.close();
		} catch (SQLException e) {
			LogTool.logger.error("GetSCSatation()--执行sql出错!");
		}
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lsResult;
	}
	/**
	 * @throws Exception 
	 * @作者:wangkun
	 * @日期:2016年12月27日
	 * @修改日期:2016年12月27日
	 * @参数:
	 * @返回:站点信息
	 * @说明:获取西南站
	 */
	public List GetXNSatation(String filterAreaName) throws Exception{
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql="";
		if(filterAreaName.equals("")){
			sql="select * from t_xnstation";
		}
		else{
			sql="select * from t_xnstation where pname='%s'";
			sql=String.format(sql, filterAreaName);
		}
		ResultSet rs = null;
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
		} catch (SQLException e) {
			LogTool.logger.error("GetSCSatation()--执行sql出错!");
		}
		DBUtil dbUtil = new DBUtil();
		List lsXNStation = dbUtil.populate(rs, XNStation.class);
		conn.close();
		return lsXNStation;
	}
	/**
	 * @throws Exception 
	 * @作者:wangkun
	 * @日期:2017年4月25日
	 * @修改日期:2017年4月25日
	 * @参数:areaname-查找的区域，对应t_xnStation里面的pname
	 * @返回:所有站点
	 * @说明:根据区域获取站点
	 */
	public List<XNStation> getStationByArea(Workspace ws,GeoRegion gr,String areaname) throws Exception{
		List<XNStation> result=new ArrayList();
		GeoRel geoRel=new GeoRel();
		List<XNStation> lsStation = GetXNSatation(areaname);
		int stationCount=lsStation.size();
		for(int j=0;j<stationCount;j++){
			XNStation station=lsStation.get(j);
			Double lon=station.getLon();
			Double lat=station.getLat();
			GeoPoint gp=new GeoPoint(lon,lat);
			Boolean b=geoRel.Contain(ws, gr, gp);
			if(b){
				result.add(station);
			}
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年4月26日
	 * @修改日期:2017年4月26日
	 * @参数:lsStation-站点列表，stationNum-站号
	 * @返回:
	 * @说明:根据站号返回站点
	 */
	public XNStation getStationByNum(List<XNStation> lsStation,String stationNum){
		XNStation station=null;
		int size=lsStation.size();
		for(int i=0;i<size;i++){
			XNStation tempS=lsStation.get(i);
			if(tempS.getStation_Id_C().equals(stationNum)){
				station=tempS;
				break;
			}
		}
		return station;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:
	 * @返回:
	 * @说明:获取站点
	 */
	public static List<Station> getStations(){
		List<Station> lsStation = new ArrayList();
		String strFile = ConfigHelper.config.getStationFile();
		JsonParser parse =new JsonParser();  //创建json解析器
		try {
			JsonObject json=(JsonObject) parse.parse(new FileReader(strFile)); //创建jsonObject对象
			JsonArray ja = json.get("rows").getAsJsonArray();
			int size = ja.size();
			Station station = null;
			for(int i=0;i<size;i++){
				JsonElement je = ja.get(i);
				JsonArray jea = (JsonArray) je;
				String stationNum = jea.get(0).getAsString();
				String stationName = jea.get(1).getAsString();
				String province = jea.get(2).getAsString();
				double lon = jea.get(3).getAsDouble();
				double lat = jea.get(4).getAsDouble();
				station = new Station(stationNum,stationName,lat,lon,0.0,0,0,"");
				lsStation.add(station);
			}
		} catch (Exception ex) {
			System.out.println("解析station.json出错!");
			System.out.println(ex.getMessage());
		}
		return lsStation;
	}
	public List<Station> getStationByBlock(String blockName){
		List<Station> lsStation = new ArrayList();
		Statement  stmt = null;
		DruidPooledConnection conn = null;
		try{
			DruidDataSource dds = DataSourceSingleton.getBaseInstance();
			conn = dds.getConnection();
			stmt = conn.createStatement();
			String sql = String.format("select * from t_county tc left join t_station ts on ts.AreaCode = tc.code where tc.parentcode in(select code from t_city where areaname='%s')", blockName);
			ResultSet resultSet = stmt.executeQuery(sql);
			Station station = null;
			while(resultSet.next()){
				String stationName = resultSet.getString("StationName");
				String stationNum = resultSet.getString("StationNum");
				double lon = resultSet.getDouble("Longitude");
				double lat = resultSet.getDouble("Latitude");
				double height = resultSet.getDouble("Height");
				int zoomLevel = resultSet.getInt("ZoomLevel");
				int type = resultSet.getInt("Type");
				String areaCode = resultSet.getString("AreaCode");
				station = new Station(stationNum,stationName,lon,lat,height,zoomLevel,type,areaCode);
				if(!stationName.equals("")){
					lsStation.add(station);
				}
			}
			stmt.close();
			conn.close();
		}
		catch(Exception ex){
			
		}
		return lsStation;
	}
	/**
	 * @throws Exception 
	 * @作者:wangkun
	 * @日期:2016年12月27日
	 * @修改日期:2016年12月27日
	 * @参数:
	 * @返回:站点信息
	 * @说明:获取站
	 */
	public List GetSatation(String filterAreaCode,Connection conn) throws Exception{
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
			PreparedStatement ps = conn.prepareStatement(sql);
			rs=ps.executeQuery();
		} catch (SQLException e) {
			System.out.println("GetSatation()--执行sql出错!");
		}
		DBUtil dbUtil = new DBUtil();
		List lsXNStation = dbUtil.populate(rs, XNStation.class);
		return lsXNStation;
	}
}
