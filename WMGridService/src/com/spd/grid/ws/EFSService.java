package com.spd.grid.ws;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.GeoRegion;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.grid.config.ConfigHelper;
import com.spd.grid.domain.Application;
import com.spd.grid.domain.Station;
import com.spd.grid.domain.StationVal;
import com.spd.grid.domain.XNStation;
import com.spd.grid.funModel.UVProcessParam;
import com.spd.grid.model.CommonResult;
import com.spd.grid.model.SimpleData;
import com.spd.grid.service.EFSServiceHelper;
import com.spd.grid.service.Forcast;
import com.spd.grid.service.HosUtil;
import com.spd.grid.service.LiveUtil;
import com.spd.grid.service.SynthesizeUtil;
import com.spd.grid.station.StationUtil;
import com.spd.grid.tool.Common;
import com.spd.grid.tool.CommonFun;
import com.spd.grid.tool.DataDealUtil;
import com.spd.grid.tool.DateFormat;
import com.spd.grid.tool.DateUtil;
import com.spd.grid.tool.GeometryUtil;
import com.spd.grid.tool.IndexRead;
import com.spd.grid.tool.LogTool;
import com.spd.grid.tool.UVGridService;
import com.spd.model.OSR;
import com.spd.weathermap.domain.GridData;
import com.spd.weathermap.util.CommonTool;
@Stateless
@Path("EFSService")
public class EFSService {
	static{
		if(ConfigHelper.config==null){
			ConfigHelper configHelper = new ConfigHelper();
			configHelper.excute();
		}
	}
	/**
	 * 获取格点报文件
	 * @return
	 */
	@POST
	@Path("GetRMM")
	@Produces("application/json")
	public Object GetRMM(@FormParam("para") String para) {
		Map<String,Double> result=null;
		JSONObject jsonObject=null;
		Date startDate=null;
		Date endDate=null;
		try {
			jsonObject = new JSONObject(para);
		} catch (JSONException e) {
			LogTool.logger.error("GetRMM中转换json出错!");
		}
		String element = CommonTool.getJSONStr(jsonObject, "element");//数据名称
		String strStartDate = CommonTool.getJSONStr(jsonObject, "startdate");
		String strEndDate = CommonTool.getJSONStr(jsonObject, "enddate");
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(strStartDate);
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate);
		} catch (ParseException e) {
			LogTool.logger.error("GetRMM中转换json转换日期出错!");
		}
		IndexRead RMMRead=new IndexRead();
		result=RMMRead.GetRMMData(element,startDate,endDate);
		return result;
	}
	/**
	 * 
	 * @throws Exception 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年8月20日
	 * @RETURN:Object
	 * @PARAM:para
	 * @DESCRIPTION:获取MJO
	 */
	@POST
	@Path("GetMJO")
	@Produces("application/json")
	public Object GetMJO(@FormParam("para") String para) throws Exception {
		Map<String,Double> result=null;
		JSONObject jsonObject=null;
		Date startDate=null;
		Date endDate=null;
		try {
			jsonObject = new JSONObject(para);
		} catch (JSONException e) {
			LogTool.logger.error("GetMJO中转换json出错!");
		}
		String element = CommonTool.getJSONStr(jsonObject, "element");//数据名称
		String strStartDate = CommonTool.getJSONStr(jsonObject, "startdate");
		String strEndDate = CommonTool.getJSONStr(jsonObject, "enddate");
		String pentad = CommonTool.getJSONStr(jsonObject, "pentad");
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(strStartDate);
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate);
		} catch (ParseException e) {
			LogTool.logger.error("GetMJO中转换json转换日期出错!");
		}
		IndexRead RMMRead=new IndexRead();
		result=RMMRead.GetMJOData(element,startDate,endDate,pentad);
		return result;
	}
	@POST
   	@Path("GetValidDate")
   	@Produces("application/json")
   	public Object GetValidDate(@FormParam("para") String para)
   	{
    	long begin = System.currentTimeMillis();
    	LogTool.logger.info("获取日期");
    	Workspace ws=new Workspace();
    	JSONObject jsonObject=null;
    	Date startDate=null;
    	Date endDate=null;
    	String[] elements=null;//要素
    	UVGridService uvGridService=new UVGridService();
		try {
			jsonObject = new JSONObject(para);
			startDate = DateFormat.yyyy_MM_dd.parse(jsonObject.getString("startdate"));
			//startDate= Common.yyyy_MM_dd.parse();
			endDate = DateFormat.yyyy_MM_dd.parse(jsonObject.getString("enddate"));
			//endDate= Common.yyyy_MM_dd.parse(jsonObject.getString("enddate"));
			String strElement=jsonObject.getString("elements");
			elements=strElement.split(",");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		Calendar calStart=Calendar.getInstance();
		Calendar calEnd=Calendar.getInstance();
		calStart.setTime(startDate);//开始日期
		calEnd.setTime(endDate);//结束日期
		calStart.set(Calendar.HOUR, 0);
		calStart.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.HOUR, 11);
		calEnd.set(Calendar.MINUTE, 59);
		int eSize=elements.length;
		Map<String,List<String>> mapAllDate=new HashMap<>();
		for(int i=0;i<eSize;i++)
		{
			String element=elements[i];
			if(element.equals("uv"))//解析uv
			{
				try{
					List<String> lsUVDate=uvGridService.GetUVValidDate(ws);
					if(lsUVDate!=null&&lsUVDate.size()>0){
						mapAllDate.put(element, lsUVDate);
					}
				}
				catch(Exception ex){
					LogTool.logger.info(ex.getMessage());
				}
			}
			else if(element.equals("olr"))//解析olr
			{
				LogTool.logger.info("解析olr");
				List<String> lsUVOLRDate=uvGridService.GetOLRValidDate(ws);
				if(lsUVOLRDate!=null&&lsUVOLRDate.size()>0){
					mapAllDate.put(element, lsUVOLRDate);
				}
			}
		}
		String strStartDate=Common.stryyyyMMdd.format(calStart.getTime());
		String strEndDate=Common.stryyyyMMdd.format(calEnd.getTime());
		Map<String,String> mapEleStatus=new HashMap<>();
		for(String key:mapAllDate.keySet())
		{
			List<String> lsDate=mapAllDate.get(key);
			if(lsDate.contains(strStartDate)&&lsDate.contains(strEndDate))
			{
				mapEleStatus.put(key, 1+"");
			}
			else
			{
				String info=lsDate.get(0)+"-"+lsDate.get(lsDate.size()-1);
				mapEleStatus.put(key, info);
			}
		}
		CommonFun.CloseDS(ws);
		long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("获取可用时效耗时：" + usetime + "秒");
		return mapEleStatus;
    }
	@POST
   	@Path("UVProcess")
   	@Produces("application/json")
	public Object UVProcess(@FormParam("para") String para){
		long begin = System.currentTimeMillis();
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		UVProcessParam uvProcessParam = gson.fromJson(para, UVProcessParam.class);
		Workspace ws=Application.m_workspace;
		UVGridService uvGridService=new UVGridService();
		//1、处理参数
		String strDate = uvProcessParam.getResDate();
		Calendar calStart = DateUtil.parse("yyyy-MM-dd", strDate);
		int level = Integer.parseInt(uvProcessParam.getLevel());
		String strP = uvProcessParam.getPeriod();
		String tempDir = uvProcessParam.getTempDir();
		String derfUVDir = uvProcessParam.getDerfUVDir();
		String uvDir = uvProcessParam.getUvDir();
		String[] strPs = strP.split("-");
		int p1 = Integer.parseInt(strPs[0]);
		int p2 = Integer.parseInt(strPs[1]);
		Calendar calEnd = (Calendar) calStart.clone();
		calEnd.add(Calendar.MONTH, 2);
		calEnd.add(Calendar.DATE, -1);
		//解析参数结束
    	//处理各类数据--start
    	System.out.println("开始处理UV");
    	//Boolean b =uvGridService.ProcessUV(ws,calStart,calEnd,level,p1,p2,tempDir,uvDir);
    	//System.out.println("处理UV完成");
    	uvGridService.ProcessDerfUV(ws,calStart,calEnd,level,p1,p2,tempDir,derfUVDir);
    	//处理计算lsw
    	//uvGridService.CalLSWIndex(calStart, calEnd, p1, p2, level);
    	//处理各类数据--end
		long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("处理数据耗时：" + usetime + "秒");
		return true;
	}
	@POST
   	@Path("MakeForcast")
   	@Produces("application/json")
	public Object MakeForcast(@FormParam("para") String para) throws Exception{
		long begin = System.currentTimeMillis();
		//获取参数--start
		Workspace ws=Application.m_workspace;
		List<SimpleData> result=new ArrayList();//结果
		JSONObject jsonObject=null;
    	String strForcastPoints =null;//预报区域的点
    	String strFactorPoints =null;//因子区域的点
    	Calendar calStart=Calendar.getInstance();//开始日期
    	Calendar calEnd=Calendar.getInstance();//结束日期
    	String level="";//层次
    	Boolean areaForcast=true;//是否是区域预报，否则站点预报
    	String tempDir = "";
    	int p1=30;//滤波周期1,默认值
    	int p2=60;//滤波周期2,默认值
    	try{
    		jsonObject = new JSONObject(para);
			Date startDate= Common.yyyy_MM_dd.parse(jsonObject.getString("startdate"));
			calStart.setTime(startDate);
			calStart.set(Calendar.HOUR, 0);
			
			Date endDate= Common.yyyy_MM_dd.parse(jsonObject.getString("enddate"));
			calEnd.setTime(endDate);
			calEnd.set(Calendar.HOUR, 0);
			
			level=jsonObject.getString("level");
			tempDir = jsonObject.getString("tempDir");
			
			String strPeriod=jsonObject.getString("period");
			String[] strPeriods=strPeriod.split("-");
			p1=Integer.parseInt(strPeriods[0]);
			p2=Integer.parseInt(strPeriods[1]);
			strForcastPoints=jsonObject.getString("forcastlines");
			strFactorPoints=jsonObject.getString("factorlines");
			
			String strFocastType=jsonObject.getString("forcasttype");
			areaForcast=strFocastType.equals("areaforcast")?true:false;
    	}
    	catch(Exception ex){
    		LogTool.logger.error("解析参数出错!",ex.getMessage());
    	}
    	UVGridService uvGridService=new UVGridService();
    	//获取参数--end
    	GeometryUtil geometryUtil=new GeometryUtil();
    	List<GeoRegion> lsFactorRegion=geometryUtil.ConvertPointsToRegions(strFactorPoints);//获取因子区域
    	//获取预报区域--start
    	
    	List<GeoRegion> lsForcastRegion=geometryUtil.ConvertPointsToRegions(strForcastPoints);
    	int forcastSize=lsForcastRegion.size();
    	if(forcastSize==0){
    		LogTool.logger.error("预报区域为空!");
    		return null;	
    	}
    	//获取区域平均实况--start
    	List<List<Double>> lsFactor=new ArrayList<>();
    	List<List<Double>> lsDerf=new ArrayList<>();
     	//获取区域平均实况--end
    	int factorRegionCount=lsFactorRegion.size();
    	//获取区域uv平均--start
    	for(int i=0;i<factorRegionCount;i++){
    		GeoRegion gr=lsFactorRegion.get(i);
    		List<Double> lsAvgU = uvGridService.GetAreaAvg(ws, gr, calStart, calEnd, level, p1, p2,"uv","u",tempDir);
    		List<Double> lsAvgV = uvGridService.GetAreaAvg(ws, gr, calStart, calEnd, level, p1, p2,"uv","v",tempDir);
    		lsFactor.add(lsAvgU);
    		lsFactor.add(lsAvgV);
    		List<Double> lsDerfU = uvGridService.GetAreaAvg(ws, gr, calStart, calEnd, level, p1, p2,"derf","u",tempDir);
    		List<Double> lsDerfV = uvGridService.GetAreaAvg(ws, gr, calStart, calEnd, level, p1, p2,"derf","v",tempDir);
    		lsDerf.add(lsDerfU);
    		lsDerf.add(lsDerfV);
    	}
    	//获取区域u平均--end
    	//获取区域v平均--end
    	if(lsFactor.size()<2){
    		LogTool.logger.error("指数太少，不能参与计算!");
    		return null;
    	}
    	//把因子和实况转换成数据
    	int pCount=lsFactor.size();
    	int tCount=lsFactor.get(0).size();
    	double[][] xData=new double[tCount][pCount];
    	double[] yData=new double[tCount];
    	OSR osr=new OSR();
    	for(int i=0;i<pCount;i++){
    		for(int j=0;j<tCount;j++){
    			xData[j][i]=lsFactor.get(i).get(j);
        	}
    	}
    	if(areaForcast){//区域预报
    		for(int i=0;i<forcastSize;i++){
        		GeoRegion forcastRegion=lsForcastRegion.get(i);
        		List<Double> lsLive=uvGridService.GetAreaAvgLive(ws,forcastRegion,calStart,calEnd);
        		if(lsLive==null||lsLive.size()<1){
            		LogTool.logger.error("实况无数据!");
            		return null;
            	}
        		if(lsLive.size()!=tCount){
            		LogTool.logger.warn("观测数据和模式数据长度不相同!");
            		return null;
            	}
        		for(int j=0;j<tCount;j++){
            		yData[j]=lsLive.get(j);
            	}
        		double[] xishu=osr.CreateEquation(xData, yData);
        		int days=lsLive.size();
        		int factorCount=lsFactor.size();
        		List<Double> lsForcast=new ArrayList<>();
        		for(int d=0;d<days;d++){
            		Double score=xishu[0];
            		for(int j=0;j<factorCount;j++){
            			score+=xishu[j+1]*lsDerf.get(j).get(d);
            		}
            		score=(int)(score*100)/100.0;
            		if(score<0){
            			score=0.0;
            		}
            		lsForcast.add(score);
            	}
        		SimpleData sd=new SimpleData(i+"",lsForcast);
				result.add(sd);
        	}
    	}
    	else{//站点预报
    		StationUtil su=new StationUtil();
    		LiveUtil lu=new LiveUtil();
    		for(int i=0;i<forcastSize;i++){
    			GeoRegion forcastRegion=lsForcastRegion.get(i);
    			List<XNStation> lsStation=su.getStationByArea(ws, forcastRegion, "四川省");//获取预报区域所有站点，先固定四川省
    			Map<String,List<Double>> mapData=lu.getRainLiveBySation(lsStation, calStart, calEnd);
    			//Map<String,double[]> mapXiShu=new HashMap();///站点对应方程系数
    			//有因子区域数据和预测站点数据，可计算方法，计算所有站点的方程
    			for (String stationNum : mapData.keySet()){
    				List<Double> lsLiveData=mapData.get(stationNum);
    				int liveCount=lsLiveData.size();
    				for(int j=0;j<liveCount;j++){
                		yData[j]=lsLiveData.get(j);
                	}
    				double[] xishu=osr.CreateEquation(xData, yData);
    				
    				//mapXiShu.put(stationNum, xishu);
    				//代入derfuv计算结果
    				List<Double> lsDay=new ArrayList();
    				int derfCount=lsDerf.size();
    				for(int d=0;d<liveCount;d++){
    					double score=xishu[0];
    					for(int j=0;j<derfCount;j++){
    						score+=xishu[j+1]*lsDerf.get(j).get(d);
                		}
    					score=(int)(score*100)/100.0;
    					if(score<0){
    						score=0;
    					}
    					lsDay.add(score);
    				}
    				XNStation thisStation=su.getStationByNum(lsStation, stationNum);
    				SimpleData sd=new SimpleData(thisStation.getStation_Name(),lsDay);
    				result.add(sd);
    			}
    		}
    	}
    	//获取区域u平均--end
		long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("预报共耗时：" + usetime + "秒");
		return result;
	}
	@POST
   	@Path("DisplayProcessedData")
   	@Produces("application/json")
   	public Object DisplayProcessedData(@FormParam("para") String para){
    	long begin = System.currentTimeMillis();
    	Workspace ws=Application.m_workspace;
    	List<GridData> lsGrid=new ArrayList<>();
    	JSONObject jsonObject=null;
    	Calendar calStart=Calendar.getInstance();//开始日期
    	Calendar calEnd=Calendar.getInstance();//开始日期
    	Calendar calDisplay=Calendar.getInstance();//开始日期
    	String level="";
    	String tempDir = "";
    	int p1=30,p2=60;//滤波周期
    	//解析参数--start
    	try{
			jsonObject = new JSONObject(para);
			Date startDate= Common.yyyy_MM_dd.parse(jsonObject.getString("startdate"));
			calStart.setTime(startDate);
			Date endDate= Common.yyyy_MM_dd.parse(jsonObject.getString("enddate"));
			calEnd.setTime(endDate);
			Date displayDate= Common.yyyy_MM_dd.parse(jsonObject.getString("viewdate"));
			calDisplay.setTime(displayDate);
			level= jsonObject.getString("level");
			String strPeriod=jsonObject.getString("period");
			tempDir = jsonObject.getString("tempDir");
			String[] periods=strPeriod.split("-");
			p1=Integer.parseInt(periods[0]);
			p2=Integer.parseInt(periods[1]);
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
		}
    	//解析参数--end
    	List<String> lsFile=new ArrayList<>();
    	String strStartDate=DateFormat.MMdd.format(calStart.getTime());
    	String strEndDate=DateFormat.MMdd.format(calEnd.getTime());
    	String strDisplayDate=DateFormat.MMdd.format(calDisplay.getTime());
    	String fileU="derf_"+strStartDate+"_"+strEndDate+"_"+strDisplayDate+"_"+level+"_"+p1+"_"+p2+"_u.tif";
		String fileV="derf_"+strStartDate+"_"+strEndDate+"_"+strDisplayDate+"_"+level+"_"+p1+"_"+p2+"_v.tif";
		lsFile.add(fileU);
		lsFile.add(fileV);
    	int fileCount=lsFile.size();
    	String strJson="";
    	for(int i=0;i<fileCount;i++)
    	{
    		GridData grid=new GridData();
    		String file = tempDir+lsFile.get(i);
    		File fi=new File(file);
    		if(!fi.exists()){
    			LogTool.logger.error(file+"不存在!");
    			return null;
    		}
    		String alias="temp"+i;
    		strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
    		Datasource ds=ws.OpenDatasource(strJson);
    		DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
    		int rows=dr.GetHeight();
        	int cols=dr.GetWidth();
        	dr.CalcExtreme();
        	ArrayList<Double> dValues=new ArrayList<>();
        	Scanline sl=new Scanline(dr.GetValueType(),cols);
    		for(int r=0;r<rows;r++){
    			dr.GetScanline(0, r, sl);
    			for(int c=0;c<cols;c++){
    				Double val=sl.GetValue(c);
    				val=(int)(val*100)/100.0;
    				dValues.add(val);
    			}
    		}
        	Rectangle2D r2d=dr.GetBounds();
        	grid.setLeft(r2d.getX());
        	grid.setBottom(r2d.getY());
        	grid.setRight(r2d.getX() + r2d.getWidth());
        	grid.setTop(r2d.getY() + r2d.getHeight());
        	grid.setRows(rows);
        	grid.setCols(cols);
        	grid.setDValues(dValues);
        	grid.setNoDataValue(dr.GetNoDataValue());
        	lsGrid.add(grid);
        	sl.Destroy();
    	}
    	CommonFun.CloseDS(ws);
    	long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("获取数据耗时：" + usetime + "秒");
		return lsGrid;
   	}
	@POST
   	@Path("DisplayPointData")
   	@Produces("application/json")
   	public Object DisplayPointData(@FormParam("para") String para){
		long begin = System.currentTimeMillis();
		Workspace ws=Application.m_workspace;
		List<List<Double>> lsResult=new ArrayList<>();
		JSONObject jsonObject=null;
    	Calendar calStart=Calendar.getInstance();//开始日期
    	Calendar calEnd=Calendar.getInstance();//开始日期
    	Point2D p2d=null;
    	String level="";
    	int p1=30,p2=60;//滤波周期
    	String element="";
    	String tempDir = "";
    	//解析参数--start
    	try 
		{
			jsonObject = new JSONObject(para);
			Date startDate= Common.yyyy_MM_dd.parse(jsonObject.getString("startdate"));
			calStart.setTime(startDate);
			Date endDate= Common.yyyy_MM_dd.parse(jsonObject.getString("enddate"));
			calEnd.setTime(endDate);
			String strpt= jsonObject.getString("point");
			String[] strpts=strpt.split(",");
			p2d=new Point2D.Double(Double.parseDouble(strpts[0]),Double.parseDouble(strpts[1]));
			level= jsonObject.getString("level");
			tempDir = jsonObject.getString("tempDir");
			String strPeriod=jsonObject.getString("period");
			String[] periods=strPeriod.split("-");
			p1=Integer.parseInt(periods[0]);
			p2=Integer.parseInt(periods[1]);
			element=jsonObject.getString("element");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	//解析参数--end
    	calStart.set(Calendar.HOUR, 0);
    	calEnd.set(Calendar.HOUR, 23);
    	String strStartDate = DateUtil.format("MMdd", calStart);
    	String strEndDate = DateUtil.format("MMdd", calEnd);
    	Calendar calCurrent=(Calendar) calStart.clone();
    	List<Double> lsFirst=new ArrayList<>();
    	List<Double> lsSecound=new ArrayList<>();
    	String strJson="";
    	while(calCurrent.compareTo(calEnd)<1)
    	{
    		String strCurrentDate= DateUtil.format("MMdd", calCurrent);
    		List<String> lsFile=new ArrayList<>();
    		if(element.equals("uv"))
        	{
        		String fileU="derf_"+strStartDate+"_"+strEndDate+"_"+strCurrentDate+"_"+level+"_"+p1+"_"+p2+"_u.tif";
        		String fileV="derf_"+strStartDate+"_"+strEndDate+"_"+strCurrentDate+"_"+level+"_"+p1+"_"+p2+"_v.tif";
        		lsFile.add(fileU);
        		lsFile.add(fileV);
        	}
        	else if(element.equals("olr"))
        	{
        		String file="olr_"+strStartDate+"_"+strEndDate+"_"+strCurrentDate+"_1000"+"_"+p1+"_"+p2+".tif";
        		lsFile.add(file);
        	}
    		int fileCount=lsFile.size();
        	for(int i=0;i<fileCount;i++)
        	{
        		GridData grid=new GridData();
        		String file = tempDir + lsFile.get(i);
        		File fi=new File(file);
        		if(!fi.exists())
        		{
        			System.out.println(file+"不存在!");
        			return null;
        		}
        		String alias="temp"+i;
        		strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
        		Datasource ds=ws.OpenDatasource(strJson);
        		DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
        		Point2D ptcell=dr.PointToCell(p2d);
        		double val=dr.GetValue((int)ptcell.getX(), (int)ptcell.getY());
        		val=(int)(val*100)/100.0;
        		if(i==0){
        			lsFirst.add(val);
        		}
        		else if(i==1){
        			lsSecound.add(val);
        		}
        		ws.CloseDatasource(alias);
        	}
    		calCurrent.add(Calendar.DATE, 1);
    	}
    	if(element.equals("uv")){
    		lsResult.add(lsFirst);
    		lsResult.add(lsSecound);
    	}
    	else if(element.equals("olr")){
    		lsResult.add(lsFirst);
    	}
    	CommonFun.CloseDS(ws);
		long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("获取数据耗时：" + usetime + "秒");
		return lsResult;
   	}
	@POST
   	@Path("HResDeal")
   	@Produces("application/json")
	public void HResDeal(@FormParam("para") String para){
		JSONObject jo=null;
		String cidu="";
		Calendar calDate=Calendar.getInstance();
		try {
			jo = new JSONObject(para);
			Date dt=Common.yyyy_MM_dd.parse(jo.getString("datetime"));
			calDate.setTime(dt);
			cidu=jo.getString("cidu");
		} catch (Exception ex) {
			LogTool.logger.error("HResDeal()--解析参数出错!");
		}
		
		DataDealUtil ddu=new DataDealUtil();
		//ddu.HgtAvg();//处理81-2010年的数据，不用再处理，按月的
		ddu.HgtJuPing(calDate, cidu);
	}
	@POST
   	@Path("DLForcast")
   	@Produces("application/json")
	public Object DLForcast(@FormParam("para") String para){
		Workspace ws=Application.m_workspace;
		JSONObject jo=null;
		String yucefun="";//预测方法
		String elementid="";//要素id
		String type="";//返回类型
		String cidu="";//尺度有年季月
		Calendar calDate=Calendar.getInstance();
		try {
			jo = new JSONObject(para);
			yucefun=jo.getString("yucefun");
			elementid=jo.getString("elementid");
			type=jo.getString("type");
			cidu=jo.getString("cidu");
			Date dt=Common.yyyy_MM.parse(jo.getString("datetime"));
			calDate.setTime(dt);
		} catch (Exception ex) {
			LogTool.logger.error("HResDeal()--解析参数出错!");
		}
		Forcast f=new Forcast();
		List<StationVal> result=f.Downscaling(ws, elementid, calDate, cidu);
		if(result==null)
			return null;
		if(type.equals("freal")){
			//获取
			int month=calDate.get(Calendar.MONTH)+1;
			HosUtil hu=new HosUtil();
			List<StationVal> lsHos=hu.GetHosData(elementid, month);
			int size=result.size();
			int hosSize=lsHos.size();
			for(int i=0;i<size;i++){
				StationVal sv=result.get(i);
				String stationNum=sv.getStationNum();
				for(int j=0;j<hosSize;j++){
					StationVal svHos=lsHos.get(j);
					if(stationNum.equals(svHos.getStationNum())){
						double val=sv.getValue();
						double hosVal=svHos.getValue();
						double newVal=hosVal+val*hosVal/100;
						sv.setValue(newVal);
					}
				}
			}
		}
		CommonFun.CloseDS(ws);
		//test
		int year=calDate.get(Calendar.YEAR);
		int month=calDate.get(Calendar.MONTH)+1;
		SynthesizeUtil su=new SynthesizeUtil();
		List<StationVal> lsJP=su.GetJP(elementid, year, month);
		int size=lsJP.size();
		int count=0;
		for(int i=0;i<size;i++){
			double nv=result.get(i).getValue();
			double rv=lsJP.get(i).getValue();
			if(nv*rv<0){//不同号
				count++;
			}
		}
		System.out.println(100-100*count/size);
		//test
		return result;
	}
}
