package com.spd.qhyc.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import EOFCCA.EofCca;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mg.objects.Workspace;
import com.mg.objects.Workspace;
import com.spd.qhyc.application.WorkspaceHelper;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.file.FileHelper;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.MonthAvg;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.util.DBHelper;
import com.spd.qhyc.util.DateUtil;
import com.spd.qhyc.util.GridUtil;
import com.spd.qhyc.util.StationUtil;

public class EofCcaApp {
	static Logger logger = LogManager.getLogger("mylog");
	public static void main(String[] args) throws Exception {
		Workspace ws = WorkspaceHelper.getWorkspace();
		String[] elements = {"Temp","Prec"};
		String strStartDate = "";//预报数据日期
		if(args.length>1) {
			strStartDate = args[0];
		}
		else {
			//Calendar cal = Calendar.getInstance();
			//strStartDate = DateUtil.format("yyyyMMdd", cal);
			strStartDate = "2017-01-01";
		}
		// 2、连接数据库
		logger.info("2、连接数据库");
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// 4、获取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		//5、获取站点数据
		logger.info("3、获取站点数据");
        StationUtil stationUtil = new StationUtil();
        List<XNStation> lsXNStation = stationUtil.GetXNSatation("",dpConn);
        int stationCount = lsXNStation.size();
        GridUtil gridUtil = new GridUtil();
        DBHelper dbHelper = new DBHelper();
        for(String elementID:elements) {
        	logger.info("开始预测:"+elementID);
        	Calendar calStart = DateUtil.parse("yyyy-MM-dd", strStartDate);
			Calendar calForecast = (Calendar) calStart.clone();
			for(int m=1;m<=13;m++) {
				int month = calForecast.get(Calendar.MONTH)+1;
				//获取过去30年观测数据
				double[][] hosLive = gridUtil.getMonthHosObvStationFromGrid(ws, month, config, elementID, lsXNStation);
				//获取过去30年模式数据
				double[][] hosMode = gridUtil.getMonthHosModeStationFromGrid(ws, month, config, lsXNStation);
				//获取
				double[][] forecastMode = gridUtil.getForecastModeStationData(ws, calStart, month, config, lsXNStation);
				MWNumericArray MWHosLive = new MWNumericArray(hosLive, MWClassID.DOUBLE);//历史实况
				MWNumericArray MWHosModel = new MWNumericArray(hosMode, MWClassID.DOUBLE);//历史模式
				MWNumericArray MWForModel = new MWNumericArray(forecastMode, MWClassID.DOUBLE);//预报模式
				EofCca eofcca = null;
				double[][] forecastData = new double[stationCount][1];
				try{
					eofcca = new EofCca();
					Object[] obj = eofcca.EOFCCA(1,5, 5,MWHosLive,MWHosModel,MWForModel);
					MWNumericArray wmArray = (MWNumericArray) obj[0];
					for(int r=0;r<stationCount;r++){
						double val = wmArray.getDouble(r+1);
						val = (int)val/10.0;
						forecastData[r][0] = val;
					}
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
				double[] hosData = gridUtil.getHosMonthAvg(ws, month, config, lsXNStation, elementID);
				List<StationVal> lsResult = new ArrayList();
	            StationVal sv = null;
			    for(int j=0;j<stationCount;j++){
			        sv = new StationVal();
			        XNStation xnStation = lsXNStation.get(j);
			        String strStationNum = xnStation.getStation_Id_C();
			        double val = forecastData[j][0];
			        double avgVal = hosData[j];
			        double jpVal = 0;
			        if(elementID.toLowerCase().equals("temp")){
			            jpVal = val - avgVal;
			        }
			        else{
			            jpVal = 100*(val - avgVal)/avgVal;
			        }
			        jpVal = (int)(jpVal*100)/100.0;
			        sv.setValue(jpVal);
			        sv.setLongitude(xnStation.getLon());
	                sv.setLatitude(xnStation.getLat());
	                sv.setStationNum(xnStation.getStation_Name());
	                sv.setStationNum(xnStation.getStation_Id_C());
	                lsResult.add(sv);
			    }
				dbHelper.insertMonthForecastData(dpConn, lsResult, elementID, calStart, calForecast,"EOF-CCA");
				calForecast.add(Calendar.MONTH, 1);
				String strForecastDate = DateUtil.format("yyyyMM", calForecast);
				System.out.println(strForecastDate);
			}
        }
        dpConn.close();
        System.out.println("over");
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月26日
	 * @修改日期:2018年1月26日
	 * @参数:
	 * @返回:
	 * @说明:转成list
	 */
	private static List<StationVal> ConvertToList(double[][] data,List<XNStation> lsXNStation){
		List<StationVal> lsStationVal = new ArrayList();
		StationVal sv = null;
		int index = 0;
		for(XNStation xnStation:lsXNStation){
			sv = new StationVal();
			sv.setStationName(xnStation.getStation_Name());
			sv.setStationNum(xnStation.getStation_Id_C());
			sv.setLongitude(xnStation.getLon());
			sv.setLatitude(xnStation.getLat());
			double val = data[index][0];
			sv.setValue(val);
			lsStationVal.add(sv);
			index++;
		}
		return lsStationVal;
	}
}
