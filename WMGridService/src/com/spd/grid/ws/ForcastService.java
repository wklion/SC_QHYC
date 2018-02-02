package com.spd.grid.ws;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.codehaus.jettison.json.JSONObject;

import EOFCCA.EofCca;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;
import com.spd.grid.config.ConfigHelper;
import com.spd.grid.domain.Application;
import com.spd.grid.domain.IndexName;
import com.spd.grid.domain.StationVal;
import com.spd.grid.domain.XNStation;
import com.spd.grid.funModel.AddFactorSchemeParam;
import com.spd.grid.funModel.CalRegForecastParam;
import com.spd.grid.funModel.CheckResExitParam;
import com.spd.grid.funModel.CheckSchemeName;
import com.spd.grid.funModel.DLForcastParam;
import com.spd.grid.funModel.DeleteSchemeParam;
import com.spd.grid.funModel.GetFactorParam;
import com.spd.grid.funModel.GetFactorSetByMonthParam;
import com.spd.grid.funModel.GetLastForecastDataDateParam;
import com.spd.grid.funModel.GetMonthAvgParam;
import com.spd.grid.funModel.GetForecastDataParam;
import com.spd.grid.funModel.GetSchemeByMonthAndEle;
import com.spd.grid.funModel.GetTestParam;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.jdbc.DataSourceSingleton;
import com.spd.grid.model.CommonResult;
import com.spd.grid.model.Config;
import com.spd.grid.model.Factor;
import com.spd.grid.model.ForecastMonthData;
import com.spd.grid.model.IndexCorr;
import com.spd.grid.model.IndexScheme;
import com.spd.grid.model.MonthAvg;
import com.spd.grid.model.MonthForecastTest;
import com.spd.grid.model.SeasonForecastTest;
import com.spd.grid.model.YearForecastTest;
import com.spd.grid.service.Forcast;
import com.spd.grid.service.ForcastServiceHelper;
import com.spd.grid.service.HosUtil;
import com.spd.grid.service.impl.FactorDllLibary;
import com.spd.grid.station.StationUtil;
import com.spd.grid.tool.Common;
import com.spd.grid.tool.CommonFun;
import com.spd.grid.tool.DBUtil;
import com.spd.grid.tool.DateUtil;
import com.spd.grid.tool.LogTool;
import com.spd.grid.tool.MathUtil;
import com.sun.jna.Native;

/**
 * @作者:wangkun
 * @日期:2017年1月5日
 * @公司:spd
 * @说明:预报相关
 */
@Stateless
@Path("ForcastService")
public class ForcastService {
	static{
		if(ConfigHelper.config==null){
			ConfigHelper configHelper = new ConfigHelper();
			configHelper.excute();
		}
	}
	@POST
    @Path("downScaling")
    @Produces("application/json")
    public Object downScaling(@FormParam("para") String para) throws Exception{
	    CommonResult cr = new CommonResult();
	    //1、解析参数
	    Gson gson = new Gson();
	    DLForcastParam DLForcastParam = gson.fromJson(para, DLForcastParam.class);
	    String strMakeDate = DLForcastParam.getMakeDate();
        String[] strForecastDates = DLForcastParam.getForcastDate();
        String elementID = DLForcastParam.getElementID();
        String methodName = DLForcastParam.getMethodName();
        // 2、连接数据库
        DataSource dataSource=DataSource.getBaseInstance();
        Connection conn=dataSource.getBaseConnection();
        //3、查询数据
        List<List<StationVal>> lsData = new ArrayList();
        String sqlF = "select fmt.stationNum,xs.Station_Name as stationName,xs.Lon as longitude,xs.Lat as latitude,val as value from %s fmt left join t_xnstation xs on fmt.stationNum=xs.Station_Id_C where makeDate='%s' and forecastDate='%s'  and method='%s'";
        String tableName = elementID.equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
        DBUtil dbUtil = new DBUtil();
        for(String forecastDate:strForecastDates){
            String sql = String.format(sqlF, tableName,strMakeDate,forecastDate,methodName);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<StationVal> lsSV = dbUtil.populate(rs, StationVal.class);
            lsData.add(lsSV);
        }
        //合并
        if(lsData.size()<1){
            cr.setErr("查询出错!");
            return cr;
        }
        List<StationVal> lsResult = lsData.get(0);
        //转成Map
        Map<String,Double> mapData = new HashMap();
        for(StationVal sv:lsResult){
            String strStationNum = sv.getStationNum();
            double val = sv.getValue();
            mapData.put(strStationNum, val);
        }
        int size = lsData.size();
        for(int i=1;i<size;i++){
            List<StationVal> lsTemp = lsData.get(i);
            for(StationVal sv:lsResult){
                String strStationNum = sv.getStationNum();
                double val = mapData.get(strStationNum);
                double oldVal = mapData.get(strStationNum);
                double resultVal = 0;
                if(elementID.equals("temp")){
                    resultVal = (val+oldVal)/2;
                }
                else{
                    resultVal = val+oldVal;
                }
                mapData.put(strStationNum, resultVal);
            }
        }
        //转成List
        for(StationVal sv:lsResult){
            String strStationNum = sv.getStationNum();
            double val = mapData.get(strStationNum);
            sv.setValue(val);
        }
        cr.setSuc(lsResult);
        conn.close();
	    return cr;
	}
	@POST
   	@Path("getFactor")
   	@Produces("application/json")
	public Object getFactor(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		//1、获取因子名称
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_index_name";
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List lsIndexName = dbUtil.populate(rs, IndexName.class);
		conn.close();
		
		//2、解析参数
		Gson gson = new Gson();
		GetFactorParam getFactorParam = gson.fromJson(para, GetFactorParam.class);
		int forecastMonth = getFactorParam.getMonth();
		int flag = getFactorParam.getFlag();
				
				
		Config config = ConfigHelper.config;
		//3、计算因子
		String corrDllFile = config.getFactorPath()+"corr_and_reg";
		FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrDllFile, FactorDllLibary.class);
		factorDll.corr(flag);
		
		String corrFile = "";
		if(flag==0){
			corrFile = config.getFactorPath()+"corr_r.txt";
		}
		else{
			corrFile = config.getFactorPath()+"corr_t.txt";
		}
		File file = new File(corrFile);
		if(!file.exists()){
			cr.setErr("文件"+corrFile+"不存在!");
			LogTool.logger.error("文件"+corrFile+"不存在!");
			return cr;
		}
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		BufferedReader br = new BufferedReader(reader);
		String strMonth = forecastMonth<10?"0"+forecastMonth:forecastMonth+"";
		List<Factor> lsFactor = new ArrayList();
		String line = "";
		line = br.readLine();
		while (line != null) {
	            line = line.trim();
	            String[] strs = line.split("\\s+");
	            String caption = strs[0];
	            if(!caption.startsWith(strMonth)){
	            	line = br.readLine(); // 一次读入一行数据
	            	continue;
	            }
	            Factor factor = null;
	            String strTempMonth = caption.substring(3, 5);
	            int tempMonth = Integer.parseInt(strTempMonth);
	            int len = strs.length;
	            for(int i=1;i<len;i++){
	            	factor = new Factor();
	            	factor.setMonth(tempMonth);
	            	int factorID = ((IndexName)lsIndexName.get(i-1)).getId();
	            	factor.setFactorID(factorID);
	            	String name = ((IndexName)lsIndexName.get(i-1)).getName();
	            	double val = Double.parseDouble(strs[i]);
	            	factor.setName(name);
	            	factor.setVal(val);
	            	lsFactor.add(factor);
	            }
	            line = br.readLine(); // 一次读入一行数据
	        }
		reader.close();
		cr.setSuc(lsFactor);
		return cr;
	}
	@POST
   	@Path("calRegForecast")
   	@Produces("application/json")
	public Object calRegForecast(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Config config = ConfigHelper.config;
		
		Gson gson = new Gson();
		CalRegForecastParam calRegForecastParam = gson.fromJson(para, CalRegForecastParam.class);
		int forecastMonth = calRegForecastParam.getMonth();
		List<Factor> lsFactor = calRegForecastParam.getLsFactor();
		
		//1、生成文件
		String strSelectFactorFile = config.getFactorPath()+"tiaoxuanhouyinzi.txt";
		LogTool.logger.info("挑选因子文件:"+strSelectFactorFile);
		File fileFactor = new File(strSelectFactorFile);
		fileFactor.deleteOnExit();
		fileFactor.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(strSelectFactorFile));
		int factorSize = lsFactor.size();
		out.write(factorSize+"\r\n");
		String line = "";
		for(Factor factor:lsFactor){
			line = factor.getMonth()+" "+factor.getFactorID();
			out.write(line+"\r\n");
		}
		out.flush();
		out.close();
		LogTool.logger.info("挑选文件生成完成!");
		
		String corrFile = config.getFactorPath()+"corr_and_reg";
		try{
			FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrFile, FactorDllLibary.class);
			LogTool.logger.info("加载完成!"+factorDll);
			int flag = calRegForecastParam.getFlag();
			factorDll.reg(2017, forecastMonth,flag);
		}
		catch(Exception ex){
			LogTool.logger.error(ex.getMessage());
		}
		LogTool.logger.error("回归计算完成!");
		
		//factorDll.reg(2017, forecastMonth);
		
		//2、打开站点文件
		String strStationFile = config.getFactorPath()+"staid.txt";
		File stationFile = new File(strStationFile);
		if(!stationFile.exists()){
			cr.setErr("站点文件staid.txt不存在!");
			return cr;
		}
		InputStreamReader reader = new InputStreamReader(new FileInputStream(stationFile));
		BufferedReader br = new BufferedReader(reader);
		List<String> lsStation = new ArrayList();
		line = "";
		line = br.readLine();
		while (line != null) {
			String strStationNum = line.trim();
			lsStation.add(strStationNum);
			line = br.readLine();
		}
		reader.close();
		//3、获取站点信息
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_xnstation";
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<XNStation> lsXNStation = dbUtil.populate(rs, XNStation.class);
		conn.close();
		
		//4、选择用到的站点数据
		List<XNStation> lsSelectStation = new ArrayList();
		for(String strS:lsStation){
			Boolean isFind = false;
			for(XNStation xnStation:lsXNStation){
				String strSationNum = xnStation.getStation_Id_C();
				if(strSationNum.equals(strS)){
					lsSelectStation.add(xnStation);
					isFind = true;
					break;
				}
			}
			if(!isFind){
				System.out.println(strS);
			}
		}
		//5、获取预报数据
		String strPreFile = config.getFactorPath();
		int flag = calRegForecastParam.getFlag();
		if(flag==0){
			strPreFile+="pre_r.txt";
		}
		else{
			strPreFile+="pre_t.txt";
		}
		File preFile = new File(strPreFile);
		if(!preFile.exists()){
			cr.setErr("预测文件"+strPreFile+"不存在!");
			return cr;
		}
		reader = new InputStreamReader(new FileInputStream(preFile));
		br = new BufferedReader(reader);
		line = "";
		line = br.readLine();
		int index = 0;
		List<StationVal> lsStationVal = new ArrayList();
		StationVal stationVal = null;
		while (line != null&&!line.equals("")) {
			line = line.trim();
			double val = Double.parseDouble(line);
			XNStation xnStation = lsSelectStation.get(index);
			stationVal = new StationVal();
			stationVal.setStationName(xnStation.getStation_Name());
			stationVal.setStationNum(xnStation.getStation_Id_C());
			stationVal.setLongitude(xnStation.getLon());
			stationVal.setLatitude(xnStation.getLat());
			stationVal.setValue(val);
			lsStationVal.add(stationVal);
			index++;
			line = br.readLine();
		}
		reader.close();
		cr.setSuc(lsStationVal);
		System.out.println("over");
		return cr;
	}
	@POST
   	@Path("calFactor")
   	@Produces("application/json")
	public void calFactor(@FormParam("para") String para) throws Exception{
		Config config = ConfigHelper.config;
		String corrFile = config.getFactorPath()+"corr_and_reg";
		FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrFile, FactorDllLibary.class);
		factorDll.corr(0);
		System.out.println("计算因子!");
	}
	@POST
   	@Path("getFactorSet")
   	@Produces("application/json")
	public Object getFactorSet(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_index_name";
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<IndexName> lsIndexName = dbUtil.populate(rs, IndexName.class);
		conn.close();
		cr.setSuc(lsIndexName);
		return cr;
	}
	@POST
   	@Path("saveFactorSet")
   	@Produces("application/json")
	public Object saveFactorSet(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		JsonParser jsonParser = new JsonParser();
		para = para.replaceAll("true", "1");
		para = para.replaceAll("false", "0");
		JsonArray jsonArray = jsonParser.parse(para).getAsJsonArray();
		Gson gson = new Gson();
		List<IndexCorr> lsIndexCorr = new ArrayList();
		for(JsonElement je:jsonArray){
			IndexCorr indexCorr = gson.fromJson(je, IndexCorr.class);
			lsIndexCorr.add(indexCorr);
		}
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		conn.setAutoCommit(false);
		String sqlF = "update t_index_corr set month1=?,month2=?,month3=?,month4=?,month5=?,month6=?,month7=?,month8=?,month9=?,month10=?,month11=?,month12=? where indexID=? and month=? and schemeID=?";
		PreparedStatement ps=conn.prepareStatement(sqlF);
		for(IndexCorr indexCorr:lsIndexCorr){
			int m1 = indexCorr.getMonth1();
			ps.setInt(1, m1);
			int m2 = indexCorr.getMonth2();
			ps.setInt(2, m2);
			int m3 = indexCorr.getMonth3();
			ps.setInt(3, m3);
			int m4 = indexCorr.getMonth4();
			ps.setInt(4, m4);
			int m5 = indexCorr.getMonth5();
			ps.setInt(5, m5);
			int m6 = indexCorr.getMonth6();
			ps.setInt(6, m6);
			int m7 = indexCorr.getMonth7();
			ps.setInt(7, m7);
			int m8 = indexCorr.getMonth8();
			ps.setInt(8, m8);
			int m9 = indexCorr.getMonth9();
			ps.setInt(9, m9);
			int m10 = indexCorr.getMonth10();
			ps.setInt(10, m10);
			int m11 = indexCorr.getMonth11();
			ps.setInt(11, m11);
			int m12 = indexCorr.getMonth12();
			ps.setInt(12, m12);
			int id = indexCorr.getIndexID();
			ps.setInt(13, id);
			int month = indexCorr.getMonth();
			ps.setInt(14, month);
			int schemeID = indexCorr.getSchemeID();
			ps.setInt(15, schemeID);
			ps.addBatch();
		}
		int[] result = ps.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
		ps.close();
		conn.close();
		cr.setSuc(result);
		return cr;
	}
	@POST
   	@Path("getFactorSetByMonthAndSchemeID")
   	@Produces("application/json")
	public Object getFactorSetByMonthAndSchemeID(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		GetFactorSetByMonthParam getFactorSetByMonthParam = gson.fromJson(para, GetFactorSetByMonthParam.class);
		int month = getFactorSetByMonthParam.getMonth();
		int schemeID = getFactorSetByMonthParam.getSchemeID();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sqlF = "select tn.category,tn.name,ic.* from t_index_corr ic left join t_index_name tn on ic.indexID=tn.id where month=%d and schemeID=%d";
		String sql = String.format(sqlF, month,schemeID);
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<IndexCorr> lsIndexCorr = dbUtil.populate(rs, IndexCorr.class);
		conn.close();
		cr.setSuc(lsIndexCorr);
		return cr;
	}
	@POST
   	@Path("getMonthAvg")
   	@Produces("application/json")
	public Object getMonthAvg(@FormParam("para") String para) throws Exception{
		Workspace ws = Application.m_workspace;
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		GetMonthAvgParam getMonthAvgParam = gson.fromJson(para, GetMonthAvgParam.class);
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		StationUtil stationUtil = new StationUtil();
		List<XNStation> lsStation = stationUtil.GetSatation("",conn);
		Config config = ConfigHelper.config;
		int flag = getMonthAvgParam.getFlag();
		String path = flag==0?config.getHosMonthPrecAvgPath():config.getHosMonthTempAvgPath();
		int stationCount = lsStation.size();
		List<DatasetRaster> lsDR = new ArrayList();
		List<String> lsAlias = new ArrayList();
		for(int i=1;i<=12;i++){
			String strFile = path + i +".tif";
			File file = new File(strFile);
			if(!file.exists()){
				LogTool.logger.error("不存在文件:"+strFile);
				cr.setErr("不存在文件:"+strFile);
				return null;
			}
			Calendar cal = Calendar.getInstance();
			String alias = i+DateUtil.format("hhmmss", cal);
			lsAlias.add(alias);
			String strJson = String.format("{\"Type\":\"GTiff\",\"Alias\":\""+alias+"\",\"Server\":\"%s\"}", strFile);
			Datasource ds = ws.OpenDatasource(strJson);
			DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
			dr.CalcExtreme();
			lsDR.add(dr);
		}
		List<MonthAvg> lsMonthAvg = new ArrayList();
		MonthAvg monthAvg = null;
		for(int j=0;j<stationCount;j++){
			monthAvg = new MonthAvg();
			XNStation xnStation = lsStation.get(j);
			double lon = xnStation.getLon();
			double lat = xnStation.getLat();
			monthAvg.setLongtitude(lon);
			monthAvg.setLatitude(lat);
			monthAvg.setStationnum(xnStation.getStation_Id_C());
			Point2D p2d=new Point2D.Double(lon,lat);
			for(int m=0;m<12;m++){
				DatasetRaster dr = lsDR.get(m);
				Point2D cell=dr.PointToCell(p2d);
				int x=(int) cell.getX();
				int y=(int) cell.getY();
				double val=dr.GetValue(x, y);//此格点值
				if(m==0){
					monthAvg.setM1(val);
				}
				else if(m==1){
					monthAvg.setM2(val);
				}
				else if(m==2){
					monthAvg.setM3(val);
				}
				else if(m==3){
					monthAvg.setM4(val);
				}
				else if(m==4){
					monthAvg.setM5(val);
				}
				else if(m==5){
					monthAvg.setM6(val);
				}
				else if(m==6){
					monthAvg.setM7(val);
				}
				else if(m==7){
					monthAvg.setM8(val);
				}
				else if(m==8){
					monthAvg.setM9(val);
				}
				else if(m==9){
					monthAvg.setM10(val);
				}
				else if(m==10){
					monthAvg.setM11(val);
				}
				else{
					monthAvg.setM12(val);
				}
			}
			lsMonthAvg.add(monthAvg);
		}
		cr.setSuc(lsMonthAvg);
		conn.close();
		//关闭数据源
		for(String str:lsAlias){
			ws.CloseDatasource(str);
		}
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:检验方案名是否可用
	 */
	@POST
   	@Path("checkSchemeName")
   	@Produces("application/json")
	public Object checkSchemeName(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		CheckSchemeName checkSchemeName = gson.fromJson(para, CheckSchemeName.class);
		String sqlF = "select * from t_index_corr_scheme where name='%s' and id in(select DISTINCT schemeID from t_index_corr where month=%d)";
		String sql = String.format(sqlF, checkSchemeName.getName(),checkSchemeName.getMonth());
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		rs.last();
		int rows = rs.getRow();
		if(rows>0){
			cr.setSuc(false);
		}
		else{
			cr.setSuc(true);
		}
		conn.close();
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:增加方案
	 */
	@POST
   	@Path("addFactorScheme")
   	@Produces("application/json")
	public Object addFactorScheme(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		AddFactorSchemeParam addFactorSchemeParam = gson.fromJson(para, AddFactorSchemeParam.class);
		String sqlF = "insert into t_index_corr_scheme(name,elementID) values('%s','%s')";
		String sql = String.format(sqlF, addFactorSchemeParam.getName(),addFactorSchemeParam.getElementID());
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		PreparedStatement ps=conn.prepareStatement(sql);
		int result = ps.executeUpdate();
		sql = "select max(id) from t_index_corr_scheme";
		ps=conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.first();
		int index = rs.getInt(1);
		cr.setSuc(index);
		conn.close();
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:添加因子方案
	 */
	@POST
   	@Path("addFactorSet")
   	@Produces("application/json")
	public Object addFactorSet(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		JsonParser jsonParser = new JsonParser();
		para = para.replaceAll("true", "1");
		para = para.replaceAll("false", "0");
		JsonArray jsonArray = jsonParser.parse(para).getAsJsonArray();
		Gson gson = new Gson();
		List<IndexCorr> lsIndexCorr = new ArrayList();
		for(JsonElement je:jsonArray){
			IndexCorr indexCorr = gson.fromJson(je, IndexCorr.class);
			lsIndexCorr.add(indexCorr);
		}
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		conn.setAutoCommit(false);
		String sqlF = "insert into t_index_corr(indexID,month,month1,month2,month3,month4,month5,month6,month7,month8,month9,month10,month11,month12,schemeID) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps=conn.prepareStatement(sqlF);
		for(IndexCorr indexCorr:lsIndexCorr){
			int id = indexCorr.getIndexID();
			ps.setInt(1, id);
			int month = indexCorr.getMonth();
			ps.setInt(2, month);
			int m1 = indexCorr.getMonth1();
			ps.setInt(3, m1);
			int m2 = indexCorr.getMonth2();
			ps.setInt(4, m2);
			int m3 = indexCorr.getMonth3();
			ps.setInt(5, m3);
			int m4 = indexCorr.getMonth4();
			ps.setInt(6, m4);
			int m5 = indexCorr.getMonth5();
			ps.setInt(7, m5);
			int m6 = indexCorr.getMonth6();
			ps.setInt(8, m6);
			int m7 = indexCorr.getMonth7();
			ps.setInt(9, m7);
			int m8 = indexCorr.getMonth8();
			ps.setInt(10, m8);
			int m9 = indexCorr.getMonth9();
			ps.setInt(11, m9);
			int m10 = indexCorr.getMonth10();
			ps.setInt(12, m10);
			int m11 = indexCorr.getMonth11();
			ps.setInt(13, m11);
			int m12 = indexCorr.getMonth12();
			ps.setInt(14, m12);
			int schemeID = indexCorr.getSchemeID();
			ps.setInt(15, schemeID);
			ps.addBatch();
		}
		int[] result = ps.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
		ps.close();
		conn.close();
		cr.setSuc(result);
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:根据月和要素获取方案
	 */
	@POST
   	@Path("getSchemeByMonthAndEle")
   	@Produces("application/json")
	public Object getSchemeByMonthAndEle(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		GetSchemeByMonthAndEle getSchemeByMonthAndEle = gson.fromJson(para, GetSchemeByMonthAndEle.class);
		int month = getSchemeByMonthAndEle.getMonth();
		String elementID = getSchemeByMonthAndEle.getElementID();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sqlF = "select * from t_index_corr_scheme where elementID='%s' and id in(select distinct schemeID from t_index_corr where month=%d)";
		String sql = String.format(sqlF, elementID,month);
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<IndexScheme> lsIndexScheme = dbUtil.populate(rs, IndexScheme.class);
		conn.close();
		cr.setSuc(lsIndexScheme);
		return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月22日
	 * @修改日期:2017年12月22日
	 * @参数:
	 * @返回:
	 * @说明:根据月和要素获取方案
	 */
	@POST
   	@Path("getForecastData")
   	@Produces("application/json")
	public Object getForecastData(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		GetForecastDataParam getMonthForecastDataParam = gson.fromJson(para, GetForecastDataParam.class);
		if(getMonthForecastDataParam == null){
		    cr.setErr("参数:"+para);
		    return cr;
		}
		String strMakeDate = getMonthForecastDataParam.getMakeDate();
		String elementID = getMonthForecastDataParam.getElementID();
		String tableName = elementID.equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
		String[] strMethods = getMonthForecastDataParam.getMethods();
		String[] strForecastDate = getMonthForecastDataParam.getForecastDates();
		//2、连接数据库
		DataSource dataSource=DataSource.getBaseInstance();
        Connection conn=dataSource.getBaseConnection();
		String sqlF = "select method,stationNum,round(avg(val),2) as val from %s where makeDate='%s' and method='%s' and (%s) group by stationNum";
		DBUtil dbUtil = new DBUtil();
		Map<String,List<ForecastMonthData>> mapMethodData = new HashMap();
		for(String method:strMethods){//方法
		    List<List<ForecastMonthData>> lsMethodData = new ArrayList();
		    StringBuilder sb = new StringBuilder();
		    for(String forecastDate:strForecastDate){
		        sb.append("forecastDate='"+forecastDate+"' or ");
		    }
		    sb = sb.delete(sb.length()-4, sb.length());
		    String sql = sqlF.format(sqlF, tableName,strMakeDate,method,sb.toString());
		    LogTool.logger.info(sql);
		    PreparedStatement ps=conn.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            List<ForecastMonthData> lsForecastMonthData = dbUtil.populate(rs, ForecastMonthData.class);
            mapMethodData.put(method, lsForecastMonthData);
		}
		//获取站点数据
		StationUtil stationUtil = new StationUtil();
		List<XNStation> lsXNStation = stationUtil.GetXNSatation("");
		//站点转成map
		Map<String,XNStation> mapStation = new HashMap();
		for(XNStation xnStation:lsXNStation){
		    mapStation.put(xnStation.getStation_Id_C(), xnStation);
		}
		//数据转换
		Map<String,List<StationVal>> mapResult = new HashMap();
		for(String str:mapMethodData.keySet()){
		    List<ForecastMonthData> lsForecastMonthData = mapMethodData.get(str);
		    List<StationVal> lsStationVal = new ArrayList();;
		    StationVal sv = null;
		    for(ForecastMonthData forecastMonthData:lsForecastMonthData){
		        sv = new StationVal();
		        String sn = forecastMonthData.getStationNum();
		        double val = forecastMonthData.getVal();
		        XNStation xnStation = mapStation.get(sn);
		        if(xnStation == null){
		            continue;
		        }
		        sv.setValue(val);
		        sv.setLongitude(xnStation.getLon());
		        sv.setLatitude(xnStation.getLat());
		        sv.setStationNum(sn);
		        sv.setStationName(xnStation.getStation_Name());
		        lsStationVal.add(sv);
		    }
		    mapResult.put(str, lsStationVal);
		}
		conn.close();
		cr.setSuc(mapResult);
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:删除方案
	 */
	@POST
   	@Path("deleteScheme")
   	@Produces("application/json")
	public Object deleteScheme(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson  gson = new Gson();
		DeleteSchemeParam deleteSchemeParam = gson.fromJson(para, DeleteSchemeParam.class);
		int schemeID = deleteSchemeParam.getSchemeID();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		//删除方案表中数据
		String sql = "delete from t_index_corr_scheme where id=%d";
		sql = String.format(sql, schemeID);
		PreparedStatement ps=conn.prepareStatement(sql);
		ps.execute();
		//删除因子配置中数据
		sql = "delete from t_index_corr where schemeID=%d";
		sql = String.format(sql, schemeID);
		ps=conn.prepareStatement(sql);
		ps.execute();
		cr.setSuc(true);
		return cr;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月12日
	 * @修改日期:2017年12月12日
	 * @参数:
	 * @返回:
	 * @说明:设置默认方案
	 */
	@POST
   	@Path("setDefaultScheme")
   	@Produces("application/json")
	public Object setDefaultScheme(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(para).getAsJsonArray();
		Gson  gson = new Gson();
		List<IndexScheme> lsIndexScheme = new ArrayList();
		for(JsonElement je:jsonArray){
			IndexScheme indexScheme = gson.fromJson(je, IndexScheme.class);
			lsIndexScheme.add(indexScheme);
		}
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		conn.setAutoCommit(false);
		String sqlF = "update t_index_corr_scheme set isDefault=? where id=?";
		PreparedStatement ps=conn.prepareStatement(sqlF);
		for(IndexScheme indexScheme:lsIndexScheme){
			ps.setInt(1, indexScheme.getIsDefault());
			ps.setInt(2, indexScheme.getId());
			ps.addBatch();
		}
		int[] result = ps.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
		cr.setSuc(result);
		ps.close();
		conn.close();
		return cr;
	}
	@POST
   	@Path("getLastForecastDataDate")
   	@Produces("application/json")
	public Object getLastForecastDataDate(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		GetLastForecastDataDateParam getLastForecastDataDateParam = gson.fromJson(para, GetLastForecastDataDateParam.class);
		String methodName = getLastForecastDataDateParam.getMethodName();
		String elementID = getLastForecastDataDateParam.getElementID();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String tableName = elementID.equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
		String sql = "select max(makeDate) from %s where method='%s'";
		sql = String.format(sql, tableName,methodName);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String strNewDate = rs.getString(1);
			cr.setSuc(strNewDate);
			break;
		}
		conn.close();
		return cr;
	}
	@POST
   	@Path("checkResExit")
   	@Produces("application/json")
	public Object checkResExit(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		CheckResExitParam checkResExitParam = gson.fromJson(para, CheckResExitParam.class);
		String methodName = checkResExitParam.getMethodName();
		String elementID = checkResExitParam.getElementID();
		String makeDate = checkResExitParam.getMakeDate();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String tableName = elementID.equals("temp")?"t_forecast_month_temp":"t_forecast_month_prec";
		String sql = "select * from %s where method='%s' and makeDate='%s'";
		sql = String.format(sql, tableName,methodName,makeDate);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			cr.setSuc(true);
			break;
		}
		conn.close();
		return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月27日
	 * @修改日期:2018年1月27日
	 * @参数:
	 * @返回:
	 * @说明:获取月检验数据
	 */
	@POST
   	@Path("getMonthTest")
   	@Produces("application/json")
	public Object getMonthTest(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		GetTestParam getTestParam = gson.fromJson(para, GetTestParam.class);
		String elementID = getTestParam.getElementID();
		String forecastName = getTestParam.getForecastName();
		String makeDate = getTestParam.getMakeDate();
		String testName = getTestParam.getTestName();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_monthforecasttest where elementID='%s' and testName='%s' and forecastName='%s' and makeDate='%s'";
		sql = String.format(sql, elementID,testName,forecastName,makeDate);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<MonthForecastTest> lsMonthForecastTest = dbUtil.populate(rs, MonthForecastTest.class);
		cr.setSuc(lsMonthForecastTest);
		conn.close();
		return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月29日
	 * @修改日期:2018年1月29日
	 * @参数:
	 * @返回:
	 * @说明:获取季检验数据
	 */
	@POST
   	@Path("getSeasonTest")
   	@Produces("application/json")
	public Object getSeasonTest(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		GetTestParam getTestParam = gson.fromJson(para, GetTestParam.class);
		String elementID = getTestParam.getElementID();
		String forecastName = getTestParam.getForecastName();
		String makeDate = getTestParam.getMakeDate();
		String testName = getTestParam.getTestName();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_seasonforecasttest where elementID='%s' and testName='%s' and forecastName='%s' and makeDate='%s'";
		sql = String.format(sql, elementID,testName,forecastName,makeDate);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<SeasonForecastTest> lsSeasonForecastTest = dbUtil.populate(rs, SeasonForecastTest.class);
		cr.setSuc(lsSeasonForecastTest);
		conn.close();
		return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月29日
	 * @修改日期:2018年1月29日
	 * @参数:
	 * @返回:
	 * @说明:获取年检验数据
	 */
	@POST
   	@Path("getYearTest")
   	@Produces("application/json")
	public Object getYearTest(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		Gson gson = new Gson();
		GetTestParam getTestParam = gson.fromJson(para, GetTestParam.class);
		String elementID = getTestParam.getElementID();
		String forecastName = getTestParam.getForecastName();
		String makeDate = getTestParam.getMakeDate();
		String testName = getTestParam.getTestName();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select * from t_yearforecasttest where elementID='%s' and testName='%s' and forecastName='%s' and makeDate='%s'";
		sql = String.format(sql, elementID,testName,forecastName,makeDate);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<YearForecastTest> lsYearForecastTest = dbUtil.populate(rs, YearForecastTest.class);
		cr.setSuc(lsYearForecastTest);
		conn.close();
		return cr;
	}
}
