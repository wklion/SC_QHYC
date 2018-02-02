package com.spd.qhyc.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mg.objects.Workspace;
import com.spd.qhyc.application.WorkspaceHelper;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.service.TestService;
import com.spd.qhyc.util.CommonUtil;
import com.spd.qhyc.util.DateUtil;
import com.spd.qhyc.util.GridUtil;
import com.spd.qhyc.util.LogTool;
import com.spd.qhyc.util.StationUtil;

public class TestOfMonth {
	public static void main(String[] args) throws Exception {
		Logger logger = LogTool.getLog();
		logger.info("月检验!");
		Workspace ws = WorkspaceHelper.getWorkspace();
		String[] areaCodes = {"5","50","51","52","53","54"};
		String[] methods = {"动力方程","EOF-CCA"};
		String[] elementIDs = {"temp","prec"};
		String[] testNames = {"PS","CC"};
		// 1、获取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		//2、初始化时间
		Calendar cal = Calendar.getInstance();
		if(args.length>0){
			DateUtil dateUtil = new DateUtil(); 
			cal = dateUtil.parse("yyyyMM", args[0]);
		}
		else {
			Boolean isDebug = config.getDebug();
			if(isDebug){
				cal.set(Calendar.YEAR, 2017);
				cal.set(Calendar.MONTH	, 11);
			}
			else{
				cal.add(Calendar.MONTH, -1);
			}
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		String strForecastDate = DateUtil.format("yyyyMM", cal);
		//3、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//4、获取站点
		StationUtil stationUtil = new StationUtil();
		List<XNStation> lsXNStation = stationUtil.GetXNSatation("", dpConn);
		
		GridUtil gridUtil = new GridUtil();
		CommonUtil commonUtil = new CommonUtil();
		TestService testService = new TestService();
		for(String elementID:elementIDs) {
			logger.info(elementID+"--CC检验");
			//5、获取观测数据
			Map<String,Double> mapObv = testService.getObvData(elementID,year,month,dpConn);
			if(mapObv==null || mapObv.size()<1) {
				logger.info("观测数据为空！");
				continue;
			}
			//6、获取平均数据
			double[] avgData = gridUtil.getHosMonthAvg(ws, month, config, lsXNStation, elementID);
			if(avgData==null || avgData.length<1) {
				logger.info("历史平均数据为空！");
				continue;
			}
			//7、计算距平
			Map<String,Double> mapJP = testService.calJP(elementID,mapObv,avgData,lsXNStation);
			//8、获取预报数据
			for(String method:methods) {
				Calendar tempCalMake = (Calendar) cal.clone();
				for(int mon=0;mon<13;mon++) {//过去13个月资料时间
					String strMakeDate = DateUtil.format("yyyy-MM-01", tempCalMake);
					tempCalMake.add(Calendar.MONTH,-1);
					Map<String,Double> mapForecastData = testService.getForecastData(elementID,cal,dpConn,method,strMakeDate);
					if(mapForecastData==null || mapForecastData.size()<1) {
						logger.info(strMakeDate+"--"+strForecastDate+"预报数据为空！");
						continue;
					}
					for(String testName:testNames){//检验名称
						for(String areaCode:areaCodes) {
							//筛选数据
							Map<String,Double> curMapJP = new HashMap();
							Map<String,Double> curMapForecast = new HashMap();
							List<XNStation> curStation = new ArrayList();
							for(XNStation station:lsXNStation) {
								String thisAreaCode = station.getAdmin_Code_CHN();
								if(!thisAreaCode.startsWith(areaCode)) {
									continue;
								}
								String sn = station.getStation_Id_C();
								Double jpVal = mapJP.get(sn);
								Double forecastVal = mapForecastData.get(sn);
								if(jpVal==null || jpVal==-9999 || forecastVal==null || forecastVal==-9999 ) {
									continue;
								}
								curMapJP.put(sn, jpVal);
								curMapForecast.put(sn, forecastVal);
								curStation.add(station);
							}
							int curObvSize = curMapJP.size();
							int curForecastSize = curMapForecast.size();
							if(curObvSize == 0 || curForecastSize == 0 || curObvSize != curForecastSize) {
								logger.info(strMakeDate+"--"+strForecastDate+"筛选出的数据不能计算！");
								continue;
							}
							double ccVal = 0;
							if(testName.equals("PS")){
								ccVal = testService.PSTestCal(mapJP, curMapForecast, elementID);
							}
							else{
								ccVal = testService.CCTestCal(mapJP, curMapForecast, curStation);
							}
							//入库
							testService.insertMonthTestData(dpConn,elementID,strMakeDate,strForecastDate,testName,method,areaCode,ccVal);
						}
					}
				}
			}
		}
		dpConn.close();
		logger.info("over");
		System.out.println("over");
	}
}
