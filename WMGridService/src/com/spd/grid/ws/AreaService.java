package com.spd.grid.ws;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spd.grid.dao.AreaDao;
import com.spd.grid.dao.impl.AreaDaoImpl;
import com.spd.grid.domain.ApplicationContextFactory;
import com.spd.grid.domain.Area;
import com.spd.grid.domain.DatasourceConnectionConfigInfo;
import com.spd.grid.domain.Depart;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.jdbc.DataSourceSingleton;
import com.spd.grid.service.AreaUtil;
import com.spd.grid.tool.Common;
import com.spd.grid.tool.ExcelUtil;
import com.spd.weathermap.util.CommonTool;
import com.spd.weathermap.util.LogTool;

@Stateless
@Path("AreaService")
public class AreaService {
	private AreaDao areaDao = new AreaDaoImpl();
	/*
	 * 娣诲姞鍖哄煙
	 * 鍙傛暟锛氳〃瀛楁
	 * 杩斿洖锛氭槸鍚︽垚鍔�
	 * @return 
	 * */
	@POST
	@Path("addArea")
	@Produces("application/json")
	public Object addArea(@FormParam("para") String para)
	{
		Boolean result = false;
		try {
			JSONObject jsonObject = new JSONObject(para);
			Area area = new Area();
			Integer id = 0;
			String name = CommonTool.getJSONStr(jsonObject, "name");
			String type = CommonTool.getJSONStr(jsonObject, "type");
			Double centerX = CommonTool.getJSONDouble(jsonObject, "centerX");
			Double centerY = CommonTool.getJSONDouble(jsonObject, "centerY");
			String coordinates = CommonTool.getJSONStr(jsonObject, "coordinates");
			Date dateNow = new Date();
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateNow);
			String createUser = CommonTool.getJSONStr(jsonObject, "createUser");
			String departCode = CommonTool.getJSONStr(jsonObject, "departCode");
			Integer status = 0;
			area.setName(name);
			area.setType(Integer.parseInt(type));
			area.setCenterX(centerX);
			area.setCenterY(centerY);
			area.setCoordinates(coordinates);
			area.setCreateDate(createDate);
			area.setCreateUser(createUser);
			area.setDepartCode(departCode);
			area.setStatus(status);
			areaDao.addArea(area);
//			DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = GridService.datasourceConnectionConfigInfo;
//			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
//					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
//					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
//			Statement  stmt = conn.createStatement(); 
//			String sql = String.format("INSERT INTO t_areacustom (id, name, centerX, centerY, createDate, createUser, departCode, status, coordinates) VALUES (%d, '%s', %f, %f, '%s', '%s', '%s', %d, '%s');",
//					id, name, centerX, centerY, createDate, createUser, departCode, status, coordinates);
//			stmt.executeUpdate(sql);
//			stmt.close();
//			conn.close();
			
			result = true;
		} catch (Exception e) {
			LogTool.logger.error("娣诲姞鍖哄煙锛�" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * 淇敼鍖哄煙
	 * 鍙傛暟锛氳〃瀛楁
	 * 杩斿洖锛氭槸鍚︽垚鍔�
	 * @return 
	 * */
	@POST
	@Path("updateArea")
	@Produces("application/json")
	public Object updateArea(@FormParam("para") String para)
	{
		Boolean result = false;
		try {
			JSONObject jsonObject = new JSONObject(para);
			Integer id = CommonTool.getJSONInt(jsonObject, "id");
			String name = CommonTool.getJSONStr(jsonObject, "name");
			Double centerX = CommonTool.getJSONDouble(jsonObject, "centerX");
			Double centerY = CommonTool.getJSONDouble(jsonObject, "centerY");
			String coordinates = CommonTool.getJSONStr(jsonObject, "coordinates");
			String createDate = CommonTool.getJSONStr(jsonObject, "createDate");
			String createUser = CommonTool.getJSONStr(jsonObject, "createUser");
			String departCode = CommonTool.getJSONStr(jsonObject, "departCode");
			Integer status = CommonTool.getJSONInt(jsonObject, "status");
			
			DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = GridService.datasourceConnectionConfigInfo;
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			Statement  stmt = conn.createStatement(); 
			String sql = String.format("UPDATE t_areacustom SET name='%s', centerX='%s', centerY='%s', coordinates='%s', createDate='%s', createUser='%s', departCode='%s', status='%s' WHERE id=%d;",
					name, centerX, centerY, coordinates, createDate, createUser, departCode, status, id);
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
			result = true;
		} catch (Exception e) {
			LogTool.logger.error("娣诲姞鍖哄煙锛�" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * 鍒犻櫎鍖哄煙
	 * 鍙傛暟锛歩d
	 * 杩斿洖锛氭槸鍚︽垚鍔�
	 * @return 
	 * */
	@POST
	@Path("deleteArea")
	@Produces("application/json")
	public Object deleteArea(@FormParam("para") String para)
	{
		Boolean result = false;
		try {
			JSONObject jsonObject = new JSONObject(para);
			Integer id = CommonTool.getJSONInt(jsonObject, "id");
			
			DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = GridService.datasourceConnectionConfigInfo;
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			Statement  stmt = conn.createStatement(); 
			String sql = String.format("DELETE FROM t_areacustom WHERE id=%d;", id);
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
			result = true;
		} catch (Exception e) {
			LogTool.logger.error("娣诲姞鍖哄煙锛�" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * 锛堟牴鎹儴闂級鑾峰彇鍖哄煙
	 * 鍙傛暟锛氶儴闂ㄧ紪鐮�
	 * 杩斿洖锛氬尯鍩熸暟缁�
	 * @return 
	 * */
	@POST
	@Path("getAreas")
	@Produces("application/json")
	public Object getAreas(@FormParam("para") String para)
	{
		ArrayList<Area> result = new ArrayList<Area>();
		try {
			JSONObject jsonObject = new JSONObject(para);
			String departCode = CommonTool.getJSONStr(jsonObject, "departCode");
			
			DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = GridService.datasourceConnectionConfigInfo;
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			Statement  stmt = conn.createStatement(); 
			String sql = String.format("select * from t_areacustom where departCode='%s'", departCode);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				result.add(new Area(resultSet.getInt("id"), resultSet.getString("name"),resultSet.getDouble("centerX"),
						resultSet.getDouble("centerY"), resultSet.getString("coordinates"),resultSet.getString("createDate"),
						resultSet.getString("createUser"), resultSet.getString("departCode"),resultSet.getInt("status")));
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			LogTool.logger.error("娣诲姞鍖哄煙锛�" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	@POST
	@Path("getAreasForGrid")
	@Produces("application/json")
	public Object getAreasForGrid(@FormParam("para") String para) throws Exception{   
		JSONObject jsonObject = new JSONObject(para);
		String departCode = CommonTool.getJSONStr(jsonObject, "departCode");
		List<Area> areaList = areaDao.getAreaByDepartCode(Integer.parseInt(departCode));
		int count = areaDao.countAreaByDepartCode(Integer.parseInt(departCode));
		Gson json = new Gson();
		JsonObject result = new JsonObject();
//		result.addProperty("success", true);
//		result.addProperty("totalRows", count);
//		result.addProperty("curPage", 1); // 榛樿浠庣涓�椤靛紑濮�
//		result.addProperty("data", json.toJson(areaList));
		return json.toJson(areaList).toString();
		
	}
	
	@POST
	@Path("getAreasByType")
	@Produces("application/json")
	public Object getAreasByType(@FormParam("para") String para) throws Exception{
		JSONObject jsonObject = new JSONObject(para);
		String type = CommonTool.getJSONStr(jsonObject, "type");
		List<Area> areaList = areaDao.getAreaByType(Integer.parseInt(type));
		Gson json = new Gson();
		return json.toJson(areaList).toString();
	}
	
	@POST
	@Path("getDepartByUser")
	@Produces("application/json")
	public Object getDepartByUser(@FormParam("para") String para,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response){
		Depart result = null;
		try {
			JSONObject jsonObject;
			jsonObject = new JSONObject(para);
			String userName = jsonObject.getString("userName");
			
			//DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = GridService.datasourceConnectionConfigInfo;
			/*DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = (DatasourceConnectionConfigInfo)ApplicationContextFactory.getInstance().getBean("datasourceConnectionConfigInfo");			
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());*/
			DataSource dataSource=DataSource.getBaseInstance();
			Connection conn=dataSource.getBaseConnection();
			Statement  stmt = conn.createStatement(); 
			String sql = String.format("select * from t_depart where departCode in (select departCode from t_user_depart where userName='%s')", userName);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				result = new Depart(resultSet.getInt("DepartID"), resultSet.getInt("AreaID"), resultSet.getString("DepartName"),
						resultSet.getInt("Parent_ID"), resultSet.getString("DepartCode"), resultSet.getString("CodeOfTownForecast"), resultSet.getString("CodeOfGuidanceForecast"));
				break;
			}
			stmt.close();
			conn.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 鎵归噺瀵煎叆鍖哄煙
	 * @throws Exception 
	 * @throws Exception 
	 * @throws Exception 
	 */
	
	    @SuppressWarnings("unchecked")
		@POST  
	    @Path("exportAreas")  
	    @Consumes(MediaType.MULTIPART_FORM_DATA)
	    public Object exportAreas(@Context HttpServletRequest request){  
				String filePath = "";
				boolean isUpload = ServletFileUpload.isMultipartContent(request);
				if (isUpload) {
					DiskFileItemFactory factory = new DiskFileItemFactory();
					ServletFileUpload upload = new ServletFileUpload(factory);
							List<FileItem> items = null;
							try {
								
								items = upload.parseRequest(request);
							} catch (FileUploadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(items.size());
							Iterator iter = items.iterator();
							while (iter.hasNext()) {
								FileItem item = (FileItem) iter.next();
								if (!item.isFormField()) {
									filePath = item.getName();
									// System.out.println(filePath);
									File txtFile = new File(Common.FILE_PATH + filePath);
									if (txtFile.exists()) {
										txtFile.delete();
									}
									try {
										item.write(txtFile);
									} catch (Exception e) {
										e.printStackTrace();
										
									}
			
								}
							}
					}
	    	 
	    	         List<Map> list = null;
					try {
						list = ExcelUtil.readExcel(Common.FILE_PATH+filePath);
					} catch (Exception e) {
						e.printStackTrace();
						LogTool.logger.error("鏂囦欢瀵煎叆澶辫触,璇锋鏌ユ枃浠舵槸鍚﹁鍗犵敤");
					}
	    	         for(Map map:list){
	    	        	 Area area = new Area();
	    	        	 area.setName(map.get("name").toString());
	    	        	 area.setCenterX(Double.parseDouble(map.get("centerX").toString()));
	    	        	 area.setCenterY(Double.parseDouble(map.get("centerY").toString()));
	    	        	 area.setCreateDate(map.get("createDate").toString());
	    	        	 area.setCreateUser(map.get("createUser").toString());
	    	        	 double departCode = Double.parseDouble(map.get("departCode").toString());
	    	        	 Integer intDepartCodeValue = (int)departCode;
	    	        	 area.setDepartCode(intDepartCodeValue.toString());
	    	        	 double doubleTypeValue = Double.parseDouble(map.get("type").toString());
	    	        	 int intTypeValue = (int)doubleTypeValue;
	    	        	 area.setType(intTypeValue);
	    	        	 
	    	        	 double stationCode = Double.parseDouble(map.get("stationCode").toString());
	    	        	 Integer intStationCodeValue = (int)stationCode;
	    	        	 area.setStationCode(intStationCodeValue.toString());
	    	        	 area.setStationName(map.get("stationName").toString());
	    	        	 area.setStationX(Double.parseDouble(map.get("stationX").toString()));
	    	        	 area.setStationY(Double.parseDouble(map.get("stationY").toString()));
	    	        	 
	    	        	 double doubleStatusValue =  Double.parseDouble(map.get("status").toString());
	    	        	 int intStatusValue = (int)doubleStatusValue;
	    	        	 area.setStatus(intStatusValue);
	    	        	 area.setCoordinates(map.get("coordinates").toString());
	    	        	 try {
							areaDao.addArea(area);
						} catch (Exception e) {
							e.printStackTrace();
						}
	    	         }
	    	         
			         System.out.println(list.size());
	    	         return "瀵煎叆鎴愬姛 "; 

		 
	 }
	    
	    
	    
	    /**
	     * 淇敼鍖哄煙鍚嶇О
	     * @throws Exception 
	     */
	    
	    @POST
		@Path("updateAreaName")
		@Produces("application/json")
		public Object updateAreaName(@FormParam("para") String para) throws Exception{
	    	JSONObject jsonObject = null;
	    	Area area = new Area();
			try {
				jsonObject = new JSONObject(para);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String name = CommonTool.getJSONStr(jsonObject, "name");
	    	String id = CommonTool.getJSONStr(jsonObject, "id");
	    	area = areaDao.getAreaById(Integer.parseInt(id));
	    	if(area!=null){
	    		area.setName(name);
	    		areaDao.updateAreaName(area);
	    	}else{
	    		
	    		return false;
	    	}
	    	
	    	
	    	return true;
	    }
	    /**
	     * 获取区域和站点
	     * @throws Exception 
	     */
	    
	    @POST
		@Path("getStationByAreaName")
		@Produces("application/json")
		public ArrayList<Object> getStationByAreaName(@FormParam("para") String para) throws Exception{
	    	ArrayList<Object> al=new ArrayList<Object>();
	    	DatasourceConnectionConfigInfo datasourceConnectionConfigInfo = (DatasourceConnectionConfigInfo)ApplicationContextFactory.getInstance().getBean("datasourceConnectionConfigInfo");			
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			JSONObject jsonObject = new JSONObject(para);
			String areaname = CommonTool.getJSONStr(jsonObject, "areaname");
			Statement  stmt = conn.createStatement(); 
			String sql ="select name,StationNum as stationname from t_station left join (select * from t_county where parentcode in(select code from t_city where areaname='%s')) as tc on code=AreaCode where name is not null";
			sql=String.format(sql,areaname);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				al.add(new Object[]{resultSet.getString(1),resultSet.getString(2)});
			}
			stmt.close();
			conn.close();	
	    	return al;
	    }
	    /**
	     * 获取区域和站点
	     * @throws Exception 
	     */
	    
	    @POST
		@Path("getAreaAndStation")
		@Produces("application/json")
		public ArrayList<Object> getAreaAndStation(@FormParam("para") String para) throws Exception{
	    	ArrayList<Object> al=new ArrayList<Object>();
	    	DruidDataSource dds = DataSourceSingleton.getBaseInstance();
	    	DruidPooledConnection conn = dds.getConnection();
	    	Statement stmt = conn.createStatement();
			String sql ="select t_city.name as cityname,t_station.StationName as stationname,Longitude,Latitude from t_city left join t_county on t_city.code=t_county.parentcode left join t_station on t_county.code=t_station.AreaCode order by t_city.id asc";
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				al.add(new Object[]{resultSet.getString(1),resultSet.getString(2),resultSet.getDouble(3),resultSet.getDouble(4)});
			}
			stmt.close();
			conn.close();	
	    	return al;
	    }
	    @POST
		@Path("GetCity")
		@Produces("application/json")
		public Object GetCity(@FormParam("para") String para){
	    	List<String> lsResult=new ArrayList<>();
	    	AreaUtil au=new AreaUtil();
	    	lsResult=au.GetCity();
	    	return lsResult;
	    }
	    @POST
		@Path("GetArea")
		@Produces("application/json")
		public Object GetArea(@FormParam("para") String para){
	    	List<String> lsResult=new ArrayList<>();
	    	AreaUtil au=new AreaUtil();
	    	lsResult=au.GetArea();
	    	return lsResult;
	    }
}
