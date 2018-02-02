package com.spd.grid.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mg.objects.Dataset;
import com.mg.objects.DatasetVector;
import com.mg.objects.Datasource;
import com.mg.objects.Recordset;
import com.spd.grid.domain.Application;
import com.spd.grid.domain.ClimaticRegionItem;
import com.spd.grid.domain.ClimaticRegionType;
import com.spd.grid.domain.DatasourceConnectionConfigInfo;
import com.spd.grid.jdbc.DataSourceSingleton;
import com.spd.weathermap.util.CommonTool;
import com.spd.weathermap.util.LogTool;
import com.spd.weathermap.util.Toolkit;

/*
 * 气候区划服务
 * */
@Stateless
@Path("ClimaticRegionService")
public class ClimaticRegionService {
	private String m_strAlias = "dsClimaticRegion";
	
	public Datasource getClimaticRegionDatasource()
	{
		Datasource ds = Application.m_workspace.GetDatasource(m_strAlias);
		if(ds == null)
		{
			String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			classPath=classPath.substring(1);
			String strJson = String.format("{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClimaticRegion\",\"Server\":\"%s\"}", classPath + "../data/T_CLIMATICREGION_CITY.shp");
			ds = Application.m_workspace.OpenDatasource(strJson);
		}
		return ds;
	}
	
	/*
	 * 获取区划类型
	 * 参数：无
	 * 返回：表名、区划名称数组
	 * @return 
	 * */
	@POST
	@Path("getClimaticRegionTypes")
	@Produces("application/json")
	public Object getClimaticRegionTypes()
	{
		ArrayList<ClimaticRegionType> types = new ArrayList<ClimaticRegionType>();
//		String strPrefix = "T_CLIMATICREGION";
//		Datasource ds = this.getClimaticRegionDatasource();
//		for(Integer i=0; i<ds.GetDatasetCount(); i++)
//		{
//			Dataset dt = ds.GetDataset(i);
//			String dtName = dt.GetName();
//			if(dtName.startsWith(strPrefix))
//				types.add(new ClimaticRegionType(dtName, dtName)); //没有数据集描述了，第二个参数“区划名称”怎么来？
//		}
		//由于数据库里面无法存矢量数据，只能把数据存到SHP文件中，并通过XML文件配置数据元数据（主要是数据类型名）
		types.add(new ClimaticRegionType("T_CLIMATICREGION_CITY", "地市边界"));
		return types;
	}
	
	/*
	 *  获取区划子项名称
	 * 参数：数据集名
	 * 返回：区域名称regionName，regionId
	 * @return 
	 * */
	@POST
	@Path("getClimaticRegionItemNames")
	@Produces("application/json")
	public Object getClimaticRegionItemNames(@FormParam("para") String para)
	{
		ArrayList<ClimaticRegionItem> items = new ArrayList<ClimaticRegionItem>(); 
		try {
			JSONObject jsonObject = new JSONObject(para);
			String datasetname = CommonTool.getJSONStr(jsonObject, "datasetname");
			Datasource ds = this.getClimaticRegionDatasource();
			Dataset dt = ds.GetDataset(datasetname);
			if(dt != null)
			{
				DatasetVector dtv = (DatasetVector)dt;
				Recordset rs = dtv.Query("", null);
				if(rs != null)
				{
					try
					{
						rs.MoveFirst();
						while(!rs.IsEOF())
						{
							Object obj = rs.GetFieldValue("NAME");
							if(obj != null)
								items.add(new ClimaticRegionItem(obj.toString(), rs.GetID()));				
							rs.MoveNext();
						}
					}catch (Exception e) {
						LogTool.logger.error("气候区划数据集获取子项名称，详情【" + e.getMessage() + "】");
						e.printStackTrace();
					}
					finally
					{
						rs.Destroy();
					}	
				}							
			}
		} catch (Exception e) {
			LogTool.logger.error("获取区划子项名称，详情【" + e.getMessage() + "】");
			e.printStackTrace();
		}
		return items;
	}
	
	/*
	 *  获取区划子项边界
	 * 参数：数据集名、对象ID
	 * 返回：几何对象及其属性（id,name,code,stationName,stationCode,stationX,stationY,geometry）
	 * @return 
	 * */
	@POST
	@Path("getClimaticRegionItem")
	@Produces("application/json")
	public Object getClimaticRegionItem(@FormParam("para") String para)
	{
		long begintime = System.currentTimeMillis();
		String result = null;
		try {			
			JSONObject jsonObject = new JSONObject(para);
			String datasetName = CommonTool.getJSONStr(jsonObject, "datasetName");
			Integer regionId = CommonTool.getJSONInt(jsonObject, "regionId");
			DatasetVector dtv = (DatasetVector)this.getClimaticRegionDatasource().GetDataset(datasetName);
			result = Toolkit.convertFeatureToJson(dtv, regionId, "REGION");
			
		} catch (Exception e) {
			LogTool.logger.error("获取区划子项（边界），详情【" + e.getMessage() + "】");
			e.printStackTrace();
		}
		long endtime = System.currentTimeMillis();
		LogTool.logger.info("获取区划子项（边界）：" + String.valueOf(endtime - begintime));
		return result;
	}
	/*
	 * 根据区域获取站点
	 * 参数：区域名称
	 * 返回：站点（stationname+stationnum）
	 * add by wangkun 20160415
	 * @return 
	 * */
	@POST
	@Path("getStationsByArea")
	@Produces("application/json")
	public Object getStationsByArea(@FormParam("para") String para)
	{
		ArrayList<String> alStation=new  ArrayList<String>();
		//获取站点和站名
		try{
			JSONObject jsonObject = new JSONObject(para);
			String areaName = CommonTool.getJSONStr(jsonObject, "name");
			DruidDataSource dds = DataSourceSingleton.getBaseInstance();
			DruidPooledConnection conn = dds.getConnection();
			Statement  stmt = conn.createStatement();
			String sql = String.format("select StationName,StationNum,Longitude,Latitude from t_county left join t_city on t_county.parentcode=t_city.code left join t_station on t_county.code=t_station.AreaCode where t_city.areaname='%s' and t_station.StationNum is not null", areaName);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()){
				alStation.add(resultSet.getString("StationName")+","+resultSet.getString("StationNum")+","+resultSet.getString("Longitude")+","+resultSet.getString("Latitude"));
			}
			stmt.close();
			conn.close();
			
		}catch(Exception ex){
			
		}finally{
			
		}
		return alStation;
	}
	/*
	 * 根据站点查经纬度
	 * 参数：站号
	 * 返回：经纬度（longitude+latitude）
	 * add by wangkun 20160415
	 * @return 
	 * */
	@POST
	@Path("getLonAndLatByStationNum")
	@Produces("application/json")
	public Object getLonAndLatByStationNum(@FormParam("para") String para)
	{
		String strResult="";
		try{
			JSONObject jsonObject = new JSONObject(para);
			String stationNum = CommonTool.getJSONStr(jsonObject, "StationNum");
			DatasourceConnectionConfigInfo datasourceConnectionConfigInfo=GridService.datasourceConnectionConfigInfo;
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			Statement  stmt = conn.createStatement();
			String sql = String.format("select Longitude,Latitude from t_station where t_station.StationNum='%s‘", stationNum);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()){
				strResult=resultSet.getString("Longitude")+","+resultSet.getString("Latitude");
				break;
			}
			stmt.close();
			conn.close();
			
		}catch(Exception ex){
			
		}
		return strResult;
	}
}
