package com.spd.grid.ws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.context.ContextLoader;

import com.spd.grid.domain.ApplicationContextFactory;
import com.spd.grid.domain.DatasourceConnectionConfigInfo;
import com.spd.grid.pojo.CommonConfig;
import com.spd.grid.service.IForecastfineService;
import com.spd.grid.tool.Common;
import com.spd.grid.tool.ExcelUtil;
import com.spd.grid.tool.ForecastfineFilter;


@Stateless
@Path("ForecastfineService")
public class ForecastfineService {

	private static CommonConfig commonfig;
	public static DatasourceConnectionConfigInfo datasourceConnectionConfigInfo;
	static {
		datasourceConnectionConfigInfo = (DatasourceConnectionConfigInfo)ApplicationContextFactory.getInstance().getBean("datasourceConnectionConfigInfo");
		commonfig = (CommonConfig)ApplicationContextFactory.getInstance().getBean("commonConifg");
	}
	
//	站点预报报文存储
	@POST
	@Path("ForecastToTXT")
	@Produces("application/json")
	public String ForecastToTXT(@FormParam("para") String para){
		synchronized (this) {
			String paraChangeString = para.replaceAll("\n", "\u0001");
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(paraChangeString);
				String name = jsonObject.getString("name");
				String areaName = jsonObject.getString("areaName");
				String productName = jsonObject.getString("productName");
				String data=jsonObject.getString("data");
				//按站点大小排序
				String[] datas=data.split("\u0001");
				//初始化一个数组，存放结果
				String[] result=new String[datas.length-1];
				List<String> lsStation=new ArrayList<String>();
				for(int d=1;d<datas.length;d++){
					String thisLine=datas[d];
					String stationNum=thisLine.substring(0, 5);
					lsStation.add(stationNum);
				}
				Collections.sort(lsStation);
				for(int d=1;d<datas.length;d++){
					String thisLine=datas[d];
					String stationNum=thisLine.substring(0, 5);
					int index=lsStation.indexOf(stationNum);
					result[index]=thisLine;
				}
				String strResult=datas[0];
				for(int r=0;r<result.length;r++){
					strResult+=result[r]+"\n";
				}
				String filePath = "";
				if(areaName.equals("shitai")){
					filePath = commonfig.getShitai_ForecastPath()+productName+"/";
				}
				else{
					filePath = commonfig.getQutai_ForecastPath()+productName+"/";
				}
				File file = new File(filePath);
				if(!file.exists()){
					file.mkdirs();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + "/" + name));
				bw.write(strResult);
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}
	}
	
//	获取城镇预报数量
	@POST
	@Path("zdybForecastNum")
	@Produces("application/json")
	public Object zdybForecastNum(@FormParam("para") String para){
		JSONObject jsonObject = null;
		int[] indexs = new int[2];
		try {
			jsonObject = new JSONObject(para);
			String time1 = jsonObject.getString("time1");
			String time2 = jsonObject.getString("time2");
			String ds1 = jsonObject.getString("ds1");
			String ds2 = jsonObject.getString("ds2");
			String areaName = jsonObject.getString("areaName");
			String productName1 = jsonObject.getString("productName1");
			String productName2 = jsonObject.getString("productName2");
			String filePath = "";
			if(areaName.equals("shitai")){
				filePath = commonfig.getShitai_ForecastPath()+productName1+"/";
			}
			else{
				filePath = commonfig.getQutai_ForecastPath()+productName1+"/";
			}
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			String fileNameNormal = "Z_SEVP_C_" + ds2 + "_" + time1 + ".*?P_RFFC-SPCC-" + time2 + ".TXT";
			File[] resultFiles = file.listFiles(new ForecastfineFilter(fileNameNormal));
			int index = resultFiles.length;
			indexs[0] = index;
			
			String guidetime = jsonObject.getString("time");
			String guidefilePath = "";
			if(areaName.equals("shitai")){
				guidefilePath = commonfig.getShitai_ForecastPath()+productName2+"/";
			}
			else{
				guidefilePath = commonfig.getQutai_ForecastPath()+productName2+"/";
			}
			File guidefile = new File(guidefilePath);
			if(!guidefile.exists()){
				guidefile.mkdirs();
			}
			String guideFileNameNormal = ds1 + "DY" + guidetime + ".ENN";
			File[] guideresultFiles = guidefile.listFiles(new ForecastfineFilter(guideFileNameNormal));
			int guideIndex = guideresultFiles.length;
			indexs[1] = guideIndex;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexs;
	}
	
	
//	读取乡镇预报已上传内容格式
	@POST
	@Path("getCountryNum")
	@Produces("application/json")
	public String getCountryNum(@FormParam("para") String para){
		try {
			JSONObject jsonObject = new JSONObject(para);
			String areaName = jsonObject.getString("areaName");
			String name = jsonObject.getString("name");
			String productName = jsonObject.getString("productName");
			String filePath = "";
			if(areaName.equals("shitai")){
				filePath = commonfig.getShitai_ForecastPath()+productName+"/";
			}
			else{
				filePath = commonfig.getQutai_ForecastPath()+productName+"/";
			}
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			BufferedReader bReader = new BufferedReader(new FileReader(filePath + "/" + name));
			String fName = null;
			boolean flag = false;
			int i = 0;
			while((fName = bReader.readLine()) != null){
				if((i==1)&&(fName.indexOf("CC")!=-1||fName.indexOf("RR")!=-1)){
					flag = true;
					break;
				}
				i++;
			}
			bReader.close();
			if(flag){
				return fName;
			}
		} catch (Exception e) {
			return "-1";
		}
		return null;
	}
	
//	获取所有产品上传状态
	@POST
	@Path("getAllProductNum")
	@Produces("application/json")
	public Object getAllProductNum(@FormParam("para") String para){
		JSONObject jsonObject = null;
		int[] indexs = null;
		try {
			jsonObject = new JSONObject(para);
			String areaName = jsonObject.getString("areaName");
			JSONArray nameList = jsonObject.getJSONArray("nameList");
			indexs = new int[nameList.length()];
			String filePath = "";
			if(areaName.equals("shitai")){
				filePath = commonfig.getShitai_ForecastPath();
			}
			else{
				filePath = commonfig.getQutai_ForecastPath();
			}
			for(int i=0;i<nameList.length();i++){
				JSONArray typeList = nameList.getJSONArray(i);
				Boolean allSubmitBoolean = true;
				for(int j=0;j<typeList.length();j++){
					JSONObject typeObj = typeList.getJSONObject(j);
					String nameString = typeObj.getString("name");
					String productName = typeObj.getString("productName");
					File file = new File(filePath+productName+"/");
					if(!file.exists()){
						file.mkdirs();
					}
					File[] resultFiles = file.listFiles(new ForecastfineFilter(nameString));
					int index = resultFiles.length;
					if(allSubmitBoolean&&index==0){
						allSubmitBoolean = false;
					}
				}
				if(allSubmitBoolean){
					indexs[i] = 1;
				}
				else {
					indexs[i] = 0;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexs;
	}
	
//	获取用户所在地区站点
	@POST
	@Path("getUserStation")
	@Produces("application/json")
	public Object getUserStation(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String departCode = jsonObject.getString("departCode");
			int type = jsonObject.getInt("type");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("departCode", departCode);
			paramMap.put("type", type);
			Object result = forecastfineService.getUserStation(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
	
//	获取用户所在地区站点new
	@POST
	@Path("getUserStationNew")
	@Produces("application/json")
	public Object getUserStationNew(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			int id = jsonObject.getInt("id");
			String type = jsonObject.getString("type");
			String departCode = jsonObject.getString("departCode");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("id", id);
			paramMap.put("type", type);
			paramMap.put("departCode", departCode);
			Object result = forecastfineService.getUserStationNew(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取所有产品
	@POST
	@Path("getZDYBPublishTime")
	@Produces("application/json")
	public Object getZDYBPublishTime(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String depart = jsonObject.getString("depart");
			String areaCode = jsonObject.getString("areaCode");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("depart", depart);
			paramMap.put("areaCode", areaCode);
			Object result = forecastfineService.getZDYBPublishTime(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取站点预报设置信息
	@POST
	@Path("getZDYBSet")
	@Produces("application/json")
	public Object getZDYBSet(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String depart = jsonObject.getString("depart");
			String type = jsonObject.getString("type");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("depart", depart);
			paramMap.put("type", type);
			Object result = forecastfineService.getZDYBSet(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取服务器时间
	@POST
	@Path("getServiceTime")
	@Produces("application/json")
	public Object getServiceTime(){
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleFormat.format(new Date());
	}
	
//	获取站点预报产品类型
	@POST
	@Path("getZDYBType")
	@Produces("application/json")
	public Object getZDYBType(){
		JSONObject jsonObject = null;
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			Object result = forecastfineService.getZDYBType(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	更新站点预报制作设置
	@POST
	@Path("updateZDYBSet")
	@Produces("application/json")
	public Object updateZDYBSet(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			int id = jsonObject.getInt("id");
			int publishTime = jsonObject.getInt("publishTime");
			String makeTime = jsonObject.getString("makeTime");
			String gdybPublishTime = jsonObject.getString("gdybPublishTime");
			String forecastTime = jsonObject.getString("forecastTime");
			String gdybType = jsonObject.getString("gdybType");
			String endTime = jsonObject.getString("endTime");
			String stationNums = jsonObject.getString("stationNums");
			String depart = jsonObject.getString("depart");
			String areaCodes = jsonObject.getString("areaCodes");
			String outType = jsonObject.getString("outType");
			String hourSpan = jsonObject.getString("hourSpan");
			String hourSpanTotal = jsonObject.getString("hourSpanTotal");
			String ui = jsonObject.getString("ui");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("id", id);
			paramMap.put("publishTime", publishTime);
			paramMap.put("gdybPublishTime", gdybPublishTime);
			paramMap.put("makeTime", makeTime);
			paramMap.put("forecastTime", forecastTime);
			paramMap.put("gdybType", gdybType);
			paramMap.put("endTime", endTime);
			paramMap.put("stationNums", stationNums);
			paramMap.put("depart", depart);
			paramMap.put("areaCodes", areaCodes);
			paramMap.put("outType", outType);
			paramMap.put("hourSpan", hourSpan);
			paramMap.put("hourSpanTotal", hourSpanTotal);
			paramMap.put("ui", ui);
			Object result = forecastfineService.updateZDYBSet(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	新增站点预报时次
	@POST
	@Path("insertZDYBSet")
	@Produces("application/json")
	public Object insertZDYBSet(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			int publishTime = jsonObject.getInt("publishTime");
			String makeTime = jsonObject.getString("makeTime");
			String gdybPublishTime = jsonObject.getString("gdybPublishTime");
			String forecastTime = jsonObject.getString("forecastTime");
			String gdybType = jsonObject.getString("gdybType");
			String endTime = jsonObject.getString("endTime");
			String stationNums = jsonObject.getString("stationNums");
			String name = jsonObject.getString("name");
			String depart = jsonObject.getString("depart");
			String areaCodes = jsonObject.getString("areaCodes");
			String type = jsonObject.getString("type");
			String outType = jsonObject.getString("outType");
			String context = jsonObject.getString("context");
			String hourSpan = jsonObject.getString("hourSpan");
			String hourSpanTotal = jsonObject.getString("hourSpanTotal");
			String zdybHour = jsonObject.getString("zdybHour");
			String ui = jsonObject.getString("ui");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("publishTime", publishTime);
			paramMap.put("gdybPublishTime", gdybPublishTime);
			paramMap.put("makeTime", makeTime);
			paramMap.put("forecastTime", forecastTime);
			paramMap.put("gdybType", gdybType);
			paramMap.put("endTime", endTime);
			paramMap.put("stationNums", stationNums);
			paramMap.put("name", name);
			paramMap.put("depart", depart);
			paramMap.put("areaCodes", areaCodes);
			paramMap.put("type", type);
			paramMap.put("outType", outType);
			paramMap.put("context", context);
			paramMap.put("hourSpan", hourSpan);
			paramMap.put("hourSpanTotal", hourSpanTotal);
			paramMap.put("zdybHour", zdybHour);
			paramMap.put("ui", ui);
			Object result = forecastfineService.insertZDYBSet(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取站点预报所有要素
	@POST
	@Path("getZDYBElement")
	@Produces("application/json")
	public Object getZDYBElement(){
		JSONObject jsonObject = null;
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			Object result = forecastfineService.getZDYBElement(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取所有输出类型
	@POST
	@Path("getZDYBOutType")
	@Produces("application/json")
	public Object getZDYBOutType(){
		JSONObject jsonObject = null;
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			Object result = forecastfineService.getZDYBOutType(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取所有站点类型
	@POST
	@Path("getZDYBStationType")
	@Produces("application/json")
	public Object getZDYBStationType(){
		JSONObject jsonObject = null;
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			Object result = forecastfineService.getZDYBStationType(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	获取格点预报制作时间
	@POST
	@Path("getGDYBPublishTime")
	@Produces("application/json")
	public Object getGDYBPublishTime(){
		JSONObject jsonObject = null;
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			Object result = forecastfineService.getGDYBPublishTime(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	删除站点预报制作时次
	@POST
	@Path("deleteProductTime")
	@Produces("application/json")
	public Object deleteProductTime(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			int id = jsonObject.getInt("id");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("id", id);
			Object result = forecastfineService.deleteProductTime(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	新增站点预报类型
	@POST
	@Path("addProductType")
	@Produces("application/json")
	public Object addProductType(@FormParam("para") String para){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String name = jsonObject.getString("name");
			String stationType = jsonObject.getString("stationType");
			int showTable = jsonObject.getInt("showTable");
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			HashMap paramMap = new HashMap();
			paramMap.put("name", name);
			paramMap.put("stationType", stationType);
			paramMap.put("showTable", showTable);
			Object result = forecastfineService.addProductType(paramMap);
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
	
//	站点导入
	@POST
    @Path("uploadStations")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public String uploadStations(@Context HttpServletRequest request) {
		String fileName = "";
        try {
        	String Type = "";
        	String addStationName = "";
            if (ServletFileUpload.isMultipartContent(request)) {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = null;
                try {
                    items = upload.parseRequest(request);
                } catch (FileUploadException e) {
                    e.printStackTrace();
                }
                if (items != null) {
                    Iterator<FileItem> iter = items.iterator();
                    while (iter.hasNext()) {
                        FileItem item = iter.next();
                        if(item.getFieldName().equals("addStationName")){
                        	addStationName = new String(item.getString().getBytes("iso-8859-1"),"utf-8");
                        }
                    	if(item.getFieldName().equals("addStationType")){
                    		Type = item.getString();
                    		IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
                			HashMap paramMap = new HashMap();
                			paramMap.put("name", addStationName);
                			paramMap.put("type", Type);
                			Object result = forecastfineService.addStationType(paramMap);
                        }
                		if(item.getFieldName().equals("stationType")){
                			Type = item.getString();
                        }
                        if (!item.isFormField() && item.getSize() > 0) {
                            fileName = item.getName();
                            File txtFile = new File(Common.ZD_FILE_PATH);
							if (!txtFile.exists()) {
								txtFile.mkdirs();
							}
                            try {
                                item.write(new File(Common.ZD_FILE_PATH+fileName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            List<Map> list = null;
			try {
				list = ExcelUtil.readExcel(Common.ZD_FILE_PATH+fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String insertAllString = null;
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", datasourceConnectionConfigInfo.getServer(), 
					datasourceConnectionConfigInfo.getPort(), datasourceConnectionConfigInfo.getDatabase()),
					datasourceConnectionConfigInfo.getUser(),datasourceConnectionConfigInfo.getPassword());
			int[]result ;
			conn.setAutoCommit(false);
			PreparedStatement  pstmt = conn.prepareStatement("insert into t_stationforecast (StationNum,StationName,Latitude,Longitude,Height,Type,AreaCode,HYJXHType) " +
					"values (?,?,?,?,?,?,?,?)");
			for(int i=0;i<list.size();i++){
				Map map = list.get(i);
				String StationNum = map.get("站名").toString();
				String StationName = map.get("站号").toString().split("\\.")[0];
				Double Latitude = Double.parseDouble(map.get("纬度").toString());
				Double Longitude = Double.parseDouble(map.get("经度").toString());
				Double Height = map.get("高度")==null?0:Double.parseDouble(map.get("高度").toString());
				String AreaCode = map.get("行政区划代码").toString().split("\\.")[0];
				String HYJXHType = map.get("海洋精细化类型")==null?null:map.get("海洋精细化类型").toString().split("\\.")[0];
				pstmt.setObject(1, StationNum);
				pstmt.setObject(2, StationName);
				pstmt.setObject(3, Latitude);
				pstmt.setObject(4, Longitude);
				pstmt.setObject(5, Height);
				pstmt.setObject(6, Type);
				pstmt.setObject(7, AreaCode);
				pstmt.setObject(8, HYJXHType);
				pstmt.addBatch();
			}
			result = pstmt.executeBatch();
			conn.commit();
			pstmt.close();
			conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
        	return "导入失败,请检查Excel";
        }
		return "导入成功";
	}
	//	获取站点预报所有要素
	@POST
	@Path("getStation")
	@Produces("application/json")
	public Object getStation(){
		try {
			IForecastfineService forecastfineService = (IForecastfineService)ContextLoader.getCurrentWebApplicationContext().getBean("ForecastfineService");
			Object result = forecastfineService.getStation();
			return result;
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 } 
		 return null;
	}
}
