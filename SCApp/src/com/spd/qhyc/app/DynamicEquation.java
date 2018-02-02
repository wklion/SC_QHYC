package com.spd.qhyc.app;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.service.ForecastServer;
import com.spd.qhyc.util.CommonUtil;
import com.spd.qhyc.util.DBHelper;
import com.spd.qhyc.util.DBUtil;
import com.spd.qhyc.util.DateUtil;
import com.spd.qhyc.util.GridUtil;
import com.spd.qhyc.util.LogTool;
import com.spd.qhyc.util.MathUtil;
import com.spd.qhyc.util.StationUtil;
import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

public class DynamicEquation {
	static Logger logger = LogTool.getLog();
	public static void main(String[] args) throws Exception {
		logger.info("年检验!");
		// 1、获取配置
		logger.info("1、获取配置");
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		Workspace ws = new Workspace();
		//2、需要参数(预报数据年月,预报月份)
		String[] elements = {"prec","temp"};
		//String[] elements = {"temp"};
		String strStartDate = "";//预报数据日期
		Calendar cal = Calendar.getInstance();
		if(args.length>1) {
			strStartDate = args[0];
		}
		else {
			Boolean isDebug = config.getDebug();
			if(isDebug){
				cal.set(Calendar.YEAR, 2017);
				cal.set(Calendar.MONTH	, 8);
				cal.set(Calendar.DAY_OF_MONTH, 1);
			}
			else{
				cal.add(Calendar.MONTH, -1);
			}
			strStartDate = DateUtil.format("yyyyMMdd", cal);
		}
		// 3、连接数据库
		logger.info("2、连接数据库");
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(dpConn == null){
			logger.error("数据库连接失败!");
			return;
		}
		//4、获取站点数据
		logger.info("3、获取站点数据");
        StationUtil stationUtil = new StationUtil();
        List<XNStation> lsXNStation = stationUtil.GetXNSatation("",dpConn);
		//5、新建内存数据集
		logger.info("4、新建内存数据集");
		String heightJPAlias = "temp"+DateUtil.format("HHmmss", Calendar.getInstance());
		String strJson = "{\"Type\":\"Memory\",\"Alias\":\""+heightJPAlias+"\",\"Server\":\"\"}";
		Datasource dsTemp = ws.CreateDatasource(strJson);
		ForecastServer forecastServer = new ForecastServer();
		for(String elementID:elements) {
			logger.info("开始预测:"+elementID);
			Calendar calStart = DateUtil.parse("yyyyMMdd", strStartDate);
			Calendar calForecast = (Calendar) calStart.clone();
			for(int m=0;m<13;m++) {
				String strForecastDate = DateUtil.format("yyyyMM", calForecast);
				logger.info("起报:"+strStartDate+",预报:"+strForecastDate);
				logger.info("5、获取观测前一年测距平数据");
				//5、获取观测前一年测距平数据
				Calendar calTwoYear = (Calendar) calForecast.clone();
				calTwoYear.add(Calendar.YEAR, -2);
				int year = calTwoYear.get(Calendar.YEAR);
				int month = calForecast.get(Calendar.MONTH)+1;
				List<StationVal> lsObserveData = forecastServer.getHosMonthJP(ws, year, month, config, elementID, lsXNStation);
				//6、获取历史距平
				
				logger.info("6、获取前一年高度场距平数据");
				//7、获取前一年高度场距平数据
				DatasetRaster drJP = calLiveHeightDeparture(ws,config,calStart,calTwoYear,dsTemp);
				logger.info("7、建方程");
				//8、建方程
				int stationCount=lsObserveData.size();
				double[][] hJuPing=new double[stationCount][4];
				for(int i = 0;i < stationCount;i++){
					StationVal stationVal = lsObserveData.get(i);
					double lon=stationVal.getLongitude();
					double lat=stationVal.getLatitude();
					Point2D p2d=new Point2D.Double(lon,lat);
					Point2D cell=drJP.PointToCell(p2d);
					int x=(int) cell.getX();
					int y=(int) cell.getY();
					double val=drJP.GetValue(x, y);//此格点值
					double rightVal=drJP.GetValue(x+1, y);//右格点值
					double bottomVal=drJP.GetValue(x, y-1);//下格点值
					double leftVal=drJP.GetValue(x-1, y);//左格点值
					double topVal=drJP.GetValue(x, y+1);//上格点值
					double pos=Math.abs(lat+2.5-lat*(lat+2.5)-lat);
					double term1=(rightVal+leftVal+topVal+bottomVal-4*val)/pos;
					double term2=rightVal-leftVal;
					double term3=(topVal-bottomVal)/y;
					double term4=val;
					hJuPing[i][0]=term1;
					hJuPing[i][1]=term2;
					hJuPing[i][2]=term3;
					hJuPing[i][3]=term4;
				}
				double[][] blive=new double[stationCount][1];
				for(int i=0;i<stationCount;i++){
					StationVal stationVal=lsObserveData.get(i);
					blive[i][0]=stationVal.getValue();
				}
				MathUtil mu=new MathUtil();
				double[][] x1=mu.getA_T(hJuPing);
				double[][] x1x=mu.MulMatrix(x1, hJuPing);
				double[][] xx1f1=mu.GetNiMatrix(x1x);
				double[][] x1y=mu.MulMatrix(x1, blive);
				double[][] xishu=mu.MulMatrix(xx1f1,x1y);
				double c1=xishu[0][0];
				double c2=xishu[1][0];
				double c3=xishu[2][0];
				double c4=xishu[3][0];
				logger.info("8、预测");
				//8、预测
				DatasetRaster drMode = calHeightDeparture(ws,config,calStart,calForecast,dsTemp);
				if(drMode == null){
					logger.error("预测模式数据为空!");
					return;
				}
				List<StationVal> lsResult = new ArrayList();//结果
				StationVal sv=null;
				for(int i=0;i<stationCount;i++){
					StationVal stationVal = lsObserveData.get(i);
					double lon = stationVal.getLongitude();
					double lat = stationVal.getLatitude();
					Point2D p2d = new Point2D.Double(lon,lat);
					Point2D cell = drMode.PointToCell(p2d);
					int x = (int) cell.getX();
					int y = (int) cell.getY();
					double val = drMode.GetValue(x, y);//此格点值
					double rightVal = drMode.GetValue(x+1, y);//右格点值
					double bottomVal = drMode.GetValue(x, y-1);//下格点值
					double leftVal = drMode.GetValue(x-1, y);//左格点值
					double topVal = drMode.GetValue(x, y+1);//上格点值
					double pos=Math.abs(lat+2.5-lat*(lat+2.5)-lat);
					double term1=(rightVal+leftVal+topVal+bottomVal-4*val)/pos;
					double term2=rightVal-leftVal;
					double term3=(topVal-bottomVal)/y;
					double term4=val;
					val=term1*c1+term2*c2+term3*c3+term4*c4;
					val=val<-100?-100:val;
					val = (int)(val*100)/100.0;
					sv=new StationVal();
					sv.setStationName(stationVal.getStationName());
					sv.setStationNum(stationVal.getStationNum());
					sv.setLongitude(stationVal.getLongitude());
					sv.setLatitude(stationVal.getLatitude());
					sv.setValue(val);
					lsResult.add(sv);
				}
				logger.info("//9、入库");
				//9、入库
				DBHelper dbHelper = new DBHelper();
				dbHelper.insertMonthForecastData(dpConn, lsResult, elementID, calStart, calForecast,"动力方程");
				//清空内存数据源
				CommonUtil.clearDS(dsTemp);
				calForecast.add(Calendar.MONTH, 1);
			}
		}
		dpConn.close();
		//10、关闭临时数据源
		logger.info("10、关闭临时数据源");
		ws.CloseDatasource(heightJPAlias);
		logger.info("预报制作完成!");
		System.out.println("over");
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月15日
	 * @修改日期:2018年1月15日
	 * @参数:
	 * @返回:
	 * @说明:获取观测距平数据
	 */
	private static List<StationVal> getObserveJPData(DruidPooledConnection dpConn,String elementid,int year,int month) throws Exception{
		String sql = "";
		String tablename = "";
		String hosTableName = "";
		if(elementid.equals("temp")){
			tablename = "t_month_temp";
			hosTableName = "v_hos_temp";
			sql="select tm.stationname,vhr.stationnum,vhr.longitude,vhr.latitude,round(tm.m%d-vhr.m%d,0) as value from %s tm right join %s vhr on tm.stationnum=vhr.stationnum where year=%d";
			sql=String.format(sql, month,month,tablename,hosTableName,year);
		}else{
			tablename = "t_month_rain";
			hosTableName = "v_hos_rain";
			sql="select tm.stationname,vhr.stationnum,vhr.longitude,vhr.latitude,round(100*(tm.m%d-vhr.m%d)/vhr.m%d,0) as value from %s tm right join %s vhr on tm.stationnum=vhr.stationnum where year=%d";
			sql=String.format(sql, month,month,month,tablename,hosTableName,year);
		}
		PreparedStatement ps=dpConn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<StationVal> lsResult = dbUtil.populate(rs, StationVal.class);
		ps.close();
		return lsResult;
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月15日
	 * @修改日期:2018年1月15日
	 * @参数:
	 * @返回:
	 * @说明:计算高度场实况距平
	 */
	private static DatasetRaster calLiveHeightDeparture(Workspace ws,Config config,Calendar calMake,Calendar calForecast,Datasource tempDS) throws Exception {
		Calendar calEnd = (Calendar) calMake.clone();
		calEnd.add(Calendar.YEAR, 1);
		//1、高度场
		String strFile = config.getLiveHeightFile();
		File file = new File(strFile);
		if(!file.exists()) {
			logger.error(String.format("实况高度场文件:%s,不存在!", strFile));
			return null;
		}
		strFile = strFile.replace("\\", "/");
		String heightAlias = "height"+DateUtil.format("HHmmss", Calendar.getInstance());
		String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAlias+"\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		if(ds==null || ds.GetDatasetCount()<1) {
			logger.error("高度场数据打开失败!");
			return null;
		}
		int index = getIndexFromLiveHeight(ds,calForecast);
		DatasetRaster drHeight = (DatasetRaster) ds.GetDataset(index);
		drHeight.CalcExtreme();
		GridUtil gridUtil = new GridUtil();
		//2、裁剪
		tempDS.DeleteDataset("ClipDG");
		gridUtil.GridClip(ws, ds.GetAlias(), drHeight.GetName(), tempDS.GetAlias(), "ClipDG");
		//3、同化
		tempDS.DeleteDataset("resampleDG");
		gridUtil.TongHua(ws, tempDS.GetAlias(), "ClipDG", 0.5, 0.5, tempDS.GetAlias(), "resampleDG");
		DatasetRaster resampleDR = (DatasetRaster) tempDS.GetDataset("resampleDG");
		if(resampleDR == null){
			logger.error("裁剪数据失败!");
			return null;
		}
		resampleDR.CalcExtreme();
		//4、历史平均
		String path = config.getHgtAvgPath();
		int forecastMonth = calForecast.get(Calendar.MONTH)+1;
		strFile = path + forecastMonth +".tif";
		file = new File(strFile);
		if(!file.exists()) {
			logger.error(String.format("文件:%s,不存在!", strFile));
			return null;
		}
		strFile = strFile.replace("\\", "/");
		String heightAvgAlias = "heightAvg"+DateUtil.format("HHmmss", Calendar.getInstance());
		strJson = "{\"Type\":\"GTiff\",\"Alias\":\""+heightAvgAlias+"\",\"Server\":\"" + strFile + "\"}";
		ds = ws.OpenDatasource(strJson);
		if(ds==null || ds.GetDatasetCount()<1) {
			logger.error("高度场平均数据打开失败!");
			return null;
		}
		DatasetRaster drHeightAvg = (DatasetRaster) ds.GetDataset(0);
		drHeightAvg.CalcExtreme();
		
		//4、栅格运算
		gridUtil.calRaster(ws, resampleDR, drHeightAvg, tempDS.GetAlias(), "jpLiveData", "[a]-[b]");
		DatasetRaster drTemp = (DatasetRaster) tempDS.GetDataset("jpLiveData");
		
		ws.CloseDatasource(heightAlias);
		ws.CloseDatasource(heightAvgAlias);
		return drTemp;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月15日
	 * @修改日期:2018年1月15日
	 * @参数:
	 * @返回:
	 * @说明:计算高度场距平
	 */
	private static DatasetRaster calHeightDeparture(Workspace ws,Config config,Calendar calMake,Calendar calForecast,Datasource dsTemp) {
		Calendar calEnd = (Calendar) calMake.clone();
		calEnd.add(Calendar.YEAR, 1);
		//1、高度场
		String path = config.getModeHeightPath();
		String strMakeDate = DateUtil.format("yyyyMMdd", calMake);
		String strStartDate = DateUtil.format("yyyyMM", calMake);
		String strEndDate = DateUtil.format("yyyyMM", calEnd);
		String strFileNameFormat = "%s.atm.Z3.%s-%s_prs0500_member.nc";
		String strFileName = String.format(strFileNameFormat,strMakeDate,strStartDate,strEndDate);
		String strFile = path + strFileName;
		File file = new File(strFile);
		if(!file.exists()) {
			logger.error(String.format("高度场文件:%s,不存在!", strFile));
			return null;
		}
		strFile = strFile.replace("\\", "/");
		String heightAlias = "height"+DateUtil.format("HHmmss", Calendar.getInstance());
		String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAlias+"\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		if(ds==null || ds.GetDatasetCount()<1) {
			logger.error("高度场数据打开失败!");
			return null;
		}
		int index = getIndexFromNC(ds,calMake,calForecast);
		DatasetRaster drHeight = (DatasetRaster) ds.GetDataset(index);
		//2、裁剪
		GridUtil gridUtil = new GridUtil();
		dsTemp.DeleteDataset("heightClip");
		gridUtil.GridClip(ws, heightAlias, drHeight.GetName(), dsTemp.GetAlias(), "heightClip");
		//3、同化
		dsTemp.DeleteDataset("resampleDG");
		gridUtil.TongHua(ws, dsTemp.GetAlias(), "ClipDG", 0.5, 0.5, dsTemp.GetAlias(), "resampleDG");
		DatasetRaster resampleDR = (DatasetRaster) dsTemp.GetDataset("resampleDG");
		if(resampleDR == null){
			logger.error("裁剪数据失败!");
			return null;
		}
		//3、历史平均
		path = config.getHgtAvgPath();
		int forecastMonth = calForecast.get(Calendar.MONTH)+1;
		strFile = path + forecastMonth +".tif";
		file = new File(strFile);
		if(!file.exists()) {
			logger.error(String.format("文件:%s,不存在!", strFile));
			return null;
		}
		strFile = strFile.replace("\\", "/");
		String heightAvgAlias = "heightAvg"+DateUtil.format("HHmmss", Calendar.getInstance());
		strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAvgAlias+"\",\"Server\":\"" + strFile + "\"}";
		ds = ws.OpenDatasource(strJson);
		if(ds==null || ds.GetDatasetCount()<1) {
			logger.error("高度场平均数据打开失败!");
			return null;
		}
		DatasetRaster drHeightAvg = (DatasetRaster) ds.GetDataset(0);
		drHeightAvg.CalcExtreme();
		//4、栅格运算
		gridUtil.calRaster(ws, resampleDR, drHeightAvg, dsTemp.GetAlias(), "jpModeData", "[a]-[b]");
		DatasetRaster drTemp = (DatasetRaster) dsTemp.GetDataset("jpModeData");
		ws.CloseDatasource(heightAlias);
		ws.CloseDatasource(heightAvgAlias);
		return drTemp;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月15日
	 * @修改日期:2018年1月15日
	 * @参数:
	 * @返回:
	 * @说明:获取预报月份的索引
	 */
	private static int getIndexFromNC(Datasource ds,Calendar calMake,Calendar calForecast) {
		Calendar calMakeTemp = (Calendar) calMake.clone();
		int diffMonth = 0;
		while(true) {
			diffMonth++;
			int makeYear = calMakeTemp.get(Calendar.YEAR);
			int makeMonth = calMakeTemp.get(Calendar.MONTH)+1;
			int forecastYear = calForecast.get(Calendar.YEAR);
			int forecastMonth = calForecast.get(Calendar.MONTH)+1;
			if(makeYear == forecastYear && makeMonth == forecastMonth) {
				break;
			}
			calMakeTemp.add(Calendar.MONTH, 1);
		}
		int index = (diffMonth - 1)*24;
		return index;
	}
	/**
	 * @throws Exception 
	 * @作者:杠上花
	 * @日期:2018年1月15日
	 * @修改日期:2018年1月15日
	 * @参数:
	 * @返回:索引
	 * @说明:获取预报月份去年月份在实况NC中的索引
	 */
	private static int getIndexFromLiveHeight(Datasource ds,Calendar calForecast) throws Exception {
		int index = 0;
		Calendar calForecastTemp = (Calendar) calForecast.clone();
		int tartgetYear = calForecastTemp.get(Calendar.YEAR);
		int tartgetMonth = calForecastTemp.get(Calendar.MONTH)+1;
		int startYear = 1948;
		int month = 1;
		int targetLevel = 500;
		int dsCount = ds.GetDatasetCount();
		JSONObject json = null;
		for(int i = 0;i<dsCount;i++) {
			Dataset dataset = ds.GetDataset(i);
			String strMeta = dataset.GetMetadata();
			json = new JSONObject(strMeta);
			int level = json.getInt("NETCDF_DIM_level");
			if(level ==10){//最小
				month++;
			}
			if(month>12){
				month=1;
				startYear++;
			}
			if(startYear == tartgetYear && month == tartgetMonth && level == targetLevel){
				break;
			}
			index++;
		}
		return index;
	}
}
