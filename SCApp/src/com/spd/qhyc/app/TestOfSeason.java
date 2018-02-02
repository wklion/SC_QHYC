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

import sun.management.counter.Variability;

public class TestOfSeason {
	public static void main(String[] args) throws Exception {
		System.out.println("开始季检验");
		Logger logger = LogTool.getLog();
		logger.info("开始季检验!");
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
				cal.set(Calendar.MONTH	, 2);
			}
			else{
				cal.add(Calendar.MONTH, -1);
			}
		}
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
			Calendar tempCalMake = (Calendar) cal.clone();
			logger.info(elementID+"--CC检验");
			//5、获取近3个月的
			List<Map<String,Double>> lsObv = new ArrayList<>();
			for(int m=0;m<3;m++){
				int year = tempCalMake.get(Calendar.YEAR);
				int month = tempCalMake.get(Calendar.MONTH)+1;
				Map<String,Double> mapObv = testService.getObvData(elementID,year,month,dpConn);
				if(mapObv==null || mapObv.size()<1) {
					logger.info("观测数据为空！");
					return;
				}
				lsObv.add(mapObv);
				tempCalMake.add(Calendar.MONTH, -1);
			}
			//5.1、观测数据计算
			Map<String,Double> mapObv = lsObv.get(0);
			for(String sn:mapObv.keySet()){
				double sum = 0;
				for(int i=0;i<3;i++){
					sum += lsObv.get(i).get(sn);
				}
				double avg = sum/3;
				mapObv.put(sn, avg);
			}
			//6、获取近3个月的历史平均
			tempCalMake = (Calendar) cal.clone();
			List<double[]> lsHosAvg = new ArrayList<>();
			for(int m=0;m<3;m++){
				int year = tempCalMake.get(Calendar.YEAR);
				int month = tempCalMake.get(Calendar.MONTH)+1;
				double[] avgData = gridUtil.getHosMonthAvg(ws, month, config, lsXNStation, elementID);
				if(avgData==null || avgData.length<1) {
					logger.info("历史平均数据为空！");
					return;
				}
				lsHosAvg.add(avgData);
				tempCalMake.add(Calendar.MONTH, -1);
			}
			//6.1、历史平均计算
			double[] hosAvg = lsHosAvg.get(0);
			int hosAvgSize = hosAvg.length;
			for(int i=0;i<hosAvgSize;i++){
				double sum = 0;
				for(int m=0;m<3;m++){
					sum += lsHosAvg.get(m)[i];
				}
				double avg = sum/3;
				hosAvg[i] = avg;
			}
			//7、计算距平
			Map<String,Double> mapJP = testService.calJP(elementID,mapObv,hosAvg,lsXNStation);
			//8、计算制作时间
			tempCalMake = (Calendar) cal.clone();
			String[] strMakeDates = new String[4];
			tempCalMake.add(Calendar.MONTH, 1);
			for(int i=0;i<4;i++){
				tempCalMake.add(Calendar.MONTH, -3);
				String strMakeDate = DateUtil.format("yyyy-MM-01", tempCalMake);
				strMakeDates[i] = strMakeDate;
			}
			//9、计算预报时间
			tempCalMake = (Calendar) cal.clone();
			String[] strForecastDates = new String[3];
			for(int i=0;i<3;i++){
				strForecastDate = DateUtil.format("yyyyMM", tempCalMake);
				strForecastDates[i] = strForecastDate;
				tempCalMake.add(Calendar.MONTH, -1);
			}
			//10、获取预报数据
			for(String method:methods) {
				for(int i=0;i<4;i++){//4个季度
					String strMakeDate = strMakeDates[i];
					List<Map<String,Double>> lsForecastData = new ArrayList<>();
					for(int j=0;j<3;j++){//1个季度3个月,算平均
						strForecastDate = strForecastDates[j];
						Calendar calTemp = DateUtil.parse("yyyyMM", strForecastDate);
						Map<String,Double> mapForecastData = testService.getForecastData(elementID,calTemp,dpConn,method,strMakeDate);
						if(mapForecastData==null || mapForecastData.size()<1) {
							logger.info(strMakeDate+"--"+strForecastDate+"预报数据为空！");
							break;
						}
						lsForecastData.add(mapForecastData);
					}
					if(lsForecastData.size()!=3){
						continue;
					}
					//计算平均
					Map<String, Double> mapForecast = lsForecastData.get(0);
					for(String sn:mapForecast.keySet()){
						double sum = 0;
						for(int m=0;m<3;m++){
							sum += lsForecastData.get(m).get(sn);
						}
						double avg = sum/3;
						mapForecast.put(sn, avg);
					}
					for(String testName:testNames){
						//区域
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
								Double forecastVal = mapForecast.get(sn);
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
							//计算季度
							int forecastMonth = Integer.parseInt(strForecastDates[0].substring(4, 6));
							int makeMonth = Integer.parseInt(strMakeDate.substring(5, 7));
							int seasonIndex = makeMonth>forecastMonth?(forecastMonth+12-makeMonth+1)/3:(forecastMonth-makeMonth+1)/3;
							testService.insertSeasonTestData(dpConn,elementID,strMakeDate,seasonIndex,testName,method,areaCode,ccVal);
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
