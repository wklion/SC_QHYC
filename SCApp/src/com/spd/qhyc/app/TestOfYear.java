package com.spd.qhyc.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * @author:杠上花
 *	@description:年检验
 */
public class TestOfYear {
	public static void main(String[] args) throws Exception {
		Logger logger = LogTool.getLog();
		logger.info("年检验!");
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
		//3、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(dpConn == null){
			logger.info("CCTestOfYear--数据库连接失败!");
			return;
		}
		//4、获取站点
		StationUtil stationUtil = new StationUtil();
		List<XNStation> lsXNStation = stationUtil.GetXNSatation("", dpConn);
		
		CommonUtil commonUtil = new CommonUtil();
		TestService testService = new TestService();
		GridUtil gridUtil = new GridUtil();
		
		for(String elementID:elementIDs) {
			Calendar tempCalMake = (Calendar) cal.clone();
			logger.info(elementID+"--CC检验");
			//5、获取近一年的观测数据
			List<Map<String,Double>> lsObv = new ArrayList<>();
			for(int m=0;m<12;m++){
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
				for(int i=0;i<12;i++){
					sum += lsObv.get(i).get(sn);
				}
				double avg = sum/12;
				mapObv.put(sn, avg);
			}
			//6、获取近12个月的历史平均
			tempCalMake = (Calendar) cal.clone();
			List<double[]> lsHosAvg = new ArrayList<>();
			for(int m=0;m<12;m++){
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
				for(int m=0;m<12;m++){
					sum += lsHosAvg.get(m)[i];
				}
				double avg = sum/12;
				hosAvg[i] = avg;
			}
			//7、计算距平
			Map<String,Double> mapJP = testService.calJP(elementID,mapObv,hosAvg,lsXNStation);
			for(String method:methods) {//按方法预测
				tempCalMake = (Calendar) cal.clone();
				tempCalMake.add(Calendar.YEAR, -1);
				tempCalMake.add(Calendar.MONTH, 1);
				String strMakeDate = DateUtil.format("yyyy-MM-01", tempCalMake);
				List<Map<String,Double>> lsForecastData = new ArrayList<>();
				//按制作时间，循环获取预报12个月的预报数据
				for(int m=0;m<12;m++){
					Map<String,Double> mapForecastData = testService.getForecastData(elementID,tempCalMake,dpConn,method,strMakeDate);
					if(mapForecastData==null || mapForecastData.size()<1) {
						String strForecastDate = DateUtil.format("yyyyMM", tempCalMake);
						logger.info(strMakeDate+"--"+strForecastDate+","+method+"预报数据为空！");
						break;
					}
					lsForecastData.add(mapForecastData);
					tempCalMake.add(Calendar.MONTH, 1);
				}
				if(lsForecastData.size()!=12){
					logger.info(strMakeDate+","+method+"未来一年预报数据有为空！");
					continue;
				}
				//计算平均
				Map<String, Double> mapForecast = lsForecastData.get(0);
				for(String sn:mapForecast.keySet()){
					double sum = 0;
					for(int m=0;m<12;m++){
						sum += lsForecastData.get(m).get(sn);
					}
					double avg = sum/12;
					mapForecast.put(sn, avg);
				}
				for(String testName:testNames){
					//按区域预报
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
							logger.info(strMakeDate+"筛选出的数据不能计算！");
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
						int result = testService.insertYearTestData(dpConn,elementID,strMakeDate,testName,method,areaCode,ccVal);
						if(result == 1){
							logger.info(strMakeDate+","+elementID+","+method+"入库成功!");
						}
						else{
							logger.info(strMakeDate+","+elementID+","+method+"入库失败!");
						}
					}
				}
			}
		}
		dpConn.close();
		logger.info("入库完成!");
		System.out.println("over");
	}
}
