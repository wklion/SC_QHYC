package com.spd.grid.service;

import java.awt.geom.Point2D;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mg.objects.*;
import com.spd.grid.climatic.ClimaticFun;
import com.spd.grid.config.ConfigHelper;
import com.spd.grid.domain.SimpleStationVal;
import com.spd.grid.domain.Station;
import com.spd.grid.domain.StationVal;
import com.spd.grid.domain.XNStation;
import com.spd.grid.model.Config;
import com.spd.grid.station.StationUtil;
import com.spd.grid.tool.CommonFun;
import com.spd.grid.tool.DBUtil;
import com.spd.grid.tool.DataDealUtil;
import com.spd.grid.tool.DateFormat;
import com.spd.grid.tool.DateUtil;
import com.spd.grid.tool.LogTool;
import com.spd.grid.tool.MathUtil;

import CCA.MatlabCCA;
import EOFCCA.EofCca;

/**
 * @作者:wangkun
 * @日期:2016年12月29日
 * @公司:spd
 * @说明:预报
 */
public class Forcast {
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月29日
	 * @修改日期:2016年12月29日
	 * @参数:
	 * @返回:
	 * @说明:Downscaling降尺度法
	 */
	public List<StationVal> Downscaling(Workspace ws,String elementid,Calendar cal,String cidu){
		//获取高度场距平
		List<StationVal> result = new ArrayList();
		if(cidu.equals("month")){
			String strDate = DateUtil.format("yyyy-MM-dd", cal);
			//1、当前月
			List<StationVal> resultThis=Downscaling_Month(ws,elementid,cal);
			//2、下个月
			cal.add(Calendar.MONTH, 1);
			List<StationVal> resultNext=Downscaling_Month(ws,elementid,cal);
			System.out.println(strDate);
			//3、计算
			int count = resultThis.size();
			if(elementid.equals("rain")){
				for(int i=0;i<count;i++){
					StationVal svThis = resultThis.get(i);
					StationVal svNext = resultNext.get(i);
					double val = svThis.getValue()+svNext.getValue();
					svThis.setValue(val);
					result.add(svThis);
				}
			}
			else if(elementid.equals("temp")){
				for(int i=0;i<count;i++){
					StationVal svThis = resultThis.get(i);
					StationVal svNext = resultNext.get(i);
					double val = (svThis.getValue()+svNext.getValue())/2.0;
					svThis.setValue(val);
					result.add(svThis);
				}
			}
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月29日
	 * @修改日期:2016年12月29日
	 * @参数:
	 * @返回:
	 * @说明:Downscaling_Month
	 */
	public List<StationVal> Downscaling_Month(Workspace ws,String elementid,Calendar cal){
		if(ConfigHelper.config==null){
			ConfigHelper configHelper = new ConfigHelper();
			configHelper.excute();
		}
		Calendar calBefore=(Calendar) cal.clone();
		calBefore.add(Calendar.YEAR, -1);//前年
		int beforeYear=calBefore.get(Calendar.YEAR);
		int beforeMonth=calBefore.get(Calendar.MONTH)+1;
		//1、获取降水或温度距平
		SynthesizeUtil su=new SynthesizeUtil();
		List<StationVal> lsJP=su.GetJP(elementid, beforeYear, beforeMonth);//前一年观测距平
		int stationCount=lsJP.size();
		if(stationCount==0){
			LogTool.logger.error("距平数据为空!");
			return null;
		}
		//2、获取距平tif数据
		String hgtJPPath = ConfigHelper.config.getHgtMonthJPPath();
		String strBeforMonth = beforeMonth<10?"0"+beforeMonth:beforeMonth+"";
		String jpFile = hgtJPPath + beforeYear+strBeforMonth+".tif";
		File file = new File(jpFile);
		if(!file.exists()){
			return null;
		}
		String strJson = "{\"Type\":\"GTiff\",\"Alias\":\"hgtJP\",\"Server\":\"" + jpFile + "\"}";
		Datasource ds=ws.OpenDatasource(strJson);
		DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
		double[][] hJuPing=new double[stationCount][4];
		for(int i=0;i<stationCount;i++){
			StationVal stationVal=lsJP.get(i);
			double lon=stationVal.getLongitude();
			double lat=stationVal.getLatitude();
			Point2D p2d=new Point2D.Double(lon,lat);
			Point2D cell=dr.PointToCell(p2d);
			int x=(int) cell.getX();
			int y=(int) cell.getY();
			double val=dr.GetValue(x, y);//此格点值
			double rightVal=dr.GetValue(x+1, y);//右格点值
			double bottomVal=dr.GetValue(x, y-1);//下格点值
			double leftVal=dr.GetValue(x-1, y);//左格点值
			double topVal=dr.GetValue(x, y+1);//上格点值
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
		//去年对应的实况值
		double[][] blive=new double[stationCount][1];
		for(int i=0;i<stationCount;i++){
			StationVal stationVal=lsJP.get(i);
			blive[i][0]=stationVal.getValue();
		}
		MathUtil mu=new MathUtil();
		double[][] x1=mu.getA_T(hJuPing);
		double[][] x1x=mu.MulMatrix(x1, hJuPing);
		double[][] xx1f1=mu.GetNiMatrix(x1x);
		double[][] x1y=mu.MulMatrix(x1, blive);
		double[][] xishu=mu.MulMatrix(xx1f1,x1y);
		//double cs=xishu[0][0];//常数
		double c1=xishu[0][0];
		double c2=xishu[1][0];
		double c3=xishu[2][0];
		double c4=xishu[3][0];
		//预报高度场距平
		strJson = "{\"Type\":\"GTiff\",\"Alias\":\"fhgtavg\",\"Server\":\"" + file + "\"}";
		ds=ws.OpenDatasource(strJson);
		dr=(DatasetRaster) ds.GetDataset(0);
		List<StationVal> lsResult=new ArrayList<>();//结果
		StationVal sv=null;
		for(int i=0;i<stationCount;i++){
			StationVal stationVal=lsJP.get(i);
			double lon=stationVal.getLongitude();
			double lat=stationVal.getLatitude();
			Point2D p2d=new Point2D.Double(lon,lat);
			Point2D cell=dr.PointToCell(p2d);
			int x=(int) cell.getX();
			int y=(int) cell.getY();
			double val=dr.GetValue(x, y);//此格点值
			double rightVal=dr.GetValue(x+1, y);//右格点值
			double bottomVal=dr.GetValue(x, y-1);//下格点值
			double leftVal=dr.GetValue(x-1, y);//左格点值
			double topVal=dr.GetValue(x, y+1);//上格点值
			double pos=Math.abs(lat+2.5-lat*(lat+2.5)-lat);
			double term1=(rightVal+leftVal+topVal+bottomVal-4*val)/pos;
			double term2=rightVal-leftVal;
			double term3=(topVal-bottomVal)/y;
			double term4=val;
			//val=cs+term1*c1+term2*c2+term3*c3+term4*c4;
			val=term1*c1+term2*c2+term3*c3+term4*c4;
			val=val<-100?-100:val;
			sv=new StationVal();
			sv.setStationName(stationVal.getStationName());
			sv.setStationNum(stationVal.getStationNum());
			sv.setLongitude(stationVal.getLongitude());
			sv.setLatitude(stationVal.getLatitude());
			sv.setValue(val);
			lsResult.add(sv);
		}
		return lsResult;
	}
	public List<StationVal>  EOF_CCA(Workspace ws,String elementid,Calendar cal,String cidu) throws Exception{
		//1、时间处理
		LogTool.logger.info("开始处理时间");
		Calendar calStart = (Calendar) cal.clone();
		Calendar calEnd = (Calendar) cal.clone();
		if(cidu.equals("month")){//2个月
			calStart.add(Calendar.MONTH, -2);
			calEnd.add(Calendar.DATE, -1);
		}
		String strHosStartDate = DateUtil.format("yyyyMM01", calStart);//历史资料开始时间
		//String strHosEndDate = DateUtil.format("yyyyMMdd", calEnd);//历史资料结束时间
		String strCurDate = DateUtil.format("yyyyMMdd", cal);//预报开始时间
		LogTool.logger.info("时间处理完成，开始获取站点!");
		//2、获取站点数据
		StationUtil su = new StationUtil();
		List<XNStation> lsStation = su.GetXNSatation("");
		LogTool.logger.info("获取站点完成,共"+lsStation.size()+"个站点!");
		//3、获取历史观测数据
		LogTool.logger.info("获取历史观测数据");
		LiveUtil lu = new LiveUtil();
		Map<String, List<Double>> mapLiveData = lu.getRainLiveBySation(lsStation,calStart,calEnd);
		Map<String, List<Double>> newMapLiveData = new HashMap();
		LogTool.logger.info("3、获取历史观测数据完成");
		//4、处理数据，去除null和无效值的站点,还有异常，比如全是0的
		LogTool.logger.info("4、处理数据");
		int days = 0;
		for(String key:mapLiveData.keySet()){
			List<Double> lsData = mapLiveData.get(key);
			int size = lsData.size();
			days = size;
			Boolean flag = true;
			for(int i=0;i<size;i++){
				double val = lsData.get(i);
				if(val>1000){//认定是无效
					flag = false;
					break;
				}
			}
			if(flag){
				newMapLiveData.put(key, lsData);
			}
			else{
				for(XNStation station:lsStation){
					if(station.getStation_Name().equals(key)){
						lsStation.remove(station);
						break;
					}
				}
			}
		}
		double[][] liveData = new double[lsStation.size()][days];//需传递的实况观测数组
		int index = 0;
		for(String key:newMapLiveData.keySet()){
			List<Double> lsData = mapLiveData.get(key);
			int size = lsData.size();
			for(int i=0;i<size;i++){
				liveData[index][i] = lsData.get(i);
			}
			index++;
		}
		LogTool.logger.info("4、处理数据完成");
		//5、获取过去2个月的模式数据
		LogTool.logger.info("5、获取过去2个月的模式数据");
		String derfPath = ConfigHelper.config.getDerfHgtMonthPath();
		File file = new File(derfPath);
		File[] files = file.listFiles();
		File findFile = null;//找到的文件
		for(File fi:files){
			String fileName = fi.getName();
			if(fileName.startsWith(strHosStartDate)){
				findFile = fi;
				break;
			}
		}
		if(findFile == null){
			LogTool.logger.info("EOF_CCA:没有找到过去2个月的模式数据！");
			return null;
		}
		String strFile = findFile.getAbsolutePath();
		strFile = strFile.replace("\\", "/");
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", "hosModel",strFile);
		Datasource dsModel = ws.OpenDatasource(strJson);
		double[][] modelHosData = getModelData(dsModel,lsStation);//需要的历史模式数据
		ws.CloseDatasource("hosModel");
		LogTool.logger.info("5、获取过去2个月的模式数据完成");
		//6、获取未来2个月的模式数据
		LogTool.logger.info("6、获取未来2个月的模式数据");
		findFile = null;
		for(File fi:files){
			String fileName = fi.getName();
			if(fileName.startsWith(strCurDate)){
				findFile = fi;
				break;
			}
		}
		if(findFile == null){
			LogTool.logger.error("EOF_CCA:没有找到预测模式数据！");
			return null;
		}
		strFile = findFile.getAbsolutePath();
		strFile = strFile.replace("\\", "/");
		strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", "forModel",strFile);
		dsModel = ws.OpenDatasource(strJson);
		double[][] modelForData = getModelData(dsModel,lsStation);//需要的观测模式数据
		ws.CloseDatasource("forModel");
		LogTool.logger.info("6、获取未来2个月的模式数据完成");
		//7、计算
		LogTool.logger.info("7、计算");
		MWNumericArray MWHosLive = new MWNumericArray(liveData, MWClassID.DOUBLE);//历史实况
		MWNumericArray MWHosModel = new MWNumericArray(modelHosData, MWClassID.DOUBLE);//历史模式
		MWNumericArray MWForModel = new MWNumericArray(modelForData, MWClassID.DOUBLE);//预报模式
		int stationCount = modelForData.length;
		days = modelForData[0].length;
		double[][] forcastTime = new double[stationCount][days];
		EofCca eofcca = null;
		try {
			eofcca = new EofCca();
			Object[] obj = eofcca.EOFCCA(1,5, 5,MWHosLive,MWHosModel,MWForModel);
			MWNumericArray wmArray = (MWNumericArray) obj[0];
			for(int c=0;c<days;c++){
				for(int r=0;r<stationCount;r++){
					int tempIndex = c*r+r+1;
					double val = wmArray.getDouble(tempIndex);
					forcastTime[r][c] = val;
				}
			}
			System.out.println("");
		} catch (MWException e) {
			e.printStackTrace();
		}
		LogTool.logger.info("7、计算完成!");
		//8、结合站点计算结果
		LogTool.logger.info("8、结合站点计算结果");
		MathUtil mathUtil = new MathUtil();
		List<StationVal> result = new ArrayList();
		for(int i=0;i<stationCount;i++){
			double[] stationData = forcastTime[i];
			double avg = mathUtil.Sum(stationData);
			if(avg==0){
				continue;
			}
			avg = avg/10.0;
			XNStation station = lsStation.get(i);
			String stationName = station.getStation_Name();
			String stationNum = station.getStation_Id_C();
			double lon = station.getLon();
			double lat = station.getLat();
			StationVal sv = new StationVal();
			sv.setStationName(stationName);
			sv.setStationNum(stationNum);
			sv.setLongitude(lon);
			sv.setLatitude(lat);
			sv.setValue(avg);
			result.add(sv);
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:
	 * @返回:
	 * @说明:获取模式数据
	 */
	private double[][] getModelData(Datasource ds,List<XNStation> lsStation){
		int R = lsStation.size();
		//int C = ds.GetDatasetCount();
		int C = 61;
		double[][] result = new double[R][C];
		for(int r=0;r<R;r++){
			XNStation station = lsStation.get(r);
			for(int c=0;c<C;c++){
				DatasetRaster dr = (DatasetRaster) ds.GetDataset(c);
				Point2D cell = dr.PointToCell(new Point2D.Double(station.getLon(),station.getLat()));
				double val = dr.GetValue((int)cell.getX(), (int)cell.getY());
				result[r][c] = val;
			}
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:
	 * @返回:
	 * @说明:数据转成Mat
	 */
	private Mat convertArrayToMat(double[][] arr){
		int R = arr.length;
		int C = arr[0].length;
		Mat result = new Mat(R,C,CvType.CV_64F);
		for(int r=0;r<R;r++){
			for(int c=0;c<C;c++){
				result.put(r, c, arr[r][c]);
			}
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月21日
	 * @修改日期:2018年1月21日
	 * @参数:cal-资料日期,month-过去多少月资料,elementID-要素ID,conn-数据库连接
	 * @返回:
	 * @说明:获取过去多少个月的月数据
	 */
	public Map<String,List<Double>> getPastMonthData(Calendar cal,int month,String elementID,Connection conn){
		Calendar calTemp = (Calendar) cal.clone();
		calTemp.add(Calendar.MONTH, -1);
		String sqlF = "select stationnum,%s as stationVal from %s where year=%d";
		String tableName = elementID.equals("temp")?"t_month_temp":"t_month_rain";
		List<List<SimpleStationVal>> lsStationVals = new ArrayList();
		DBUtil dbUtil = new DBUtil();
		try{
			while(month>0){
				int curMonth = calTemp.get(Calendar.MONTH)+1;
				String monthField = "m"+curMonth;
				int year = calTemp.get(Calendar.YEAR);
				String sql = String.format(sqlF, monthField,tableName,year);
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				List<SimpleStationVal> lsSimpleStationVal = dbUtil.populate(rs, SimpleStationVal.class);
				lsStationVals.add(lsSimpleStationVal);
				calTemp.add(Calendar.MONTH, -1);
				month--;
			}
		}
		catch(Exception ex){
			
		}
		Map<String,List<Double>> mapResult = new HashMap();
		int size = lsStationVals.size();
		for(int i = size-1;i>=0;i--){
			List<SimpleStationVal> lsSimpleStationVal = lsStationVals.get(i);
			for(SimpleStationVal ssv:lsSimpleStationVal){
				String strStationNum = ssv.getStationNum();
				double val = ssv.getStationVal();
				if(i == size-1){
					List<Double> tempLS = new ArrayList();
					tempLS.add(val);
					mapResult.put(strStationNum, tempLS);
				}
				else{
					List<Double> tempLS = mapResult.get(strStationNum);
					if(tempLS != null){
						tempLS.add(val);
					}
				}
			}
		}
		return mapResult;
	}
	public Map<String,List<Double>> getModeStationData(Workspace ws,Calendar cal,int month,Config config,List<XNStation> lsXNStation){
		Calendar calTemp = (Calendar) cal.clone();
		calTemp.add(Calendar.MONTH, -month);
		String strLiveModeFile = config.getMonthHgtFile();
		File file = new File(strLiveModeFile);
		if(!file.exists()){
			LogTool.logger.error("月高度场文件不存在!");
			return null;
		}
		String heightAlias = "height"+DateUtil.format("HHmmss", Calendar.getInstance());
        String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAlias+"\",\"Server\":\"" + strLiveModeFile + "\"}";
        Datasource ds = ws.OpenDatasource(strJson);
        int startYear = 1948;
        int targetLevel = 500;
        int dsCount = ds.GetDatasetCount();
        JSONObject json = null;
        int tempMonth = 1;
        int forecastYear = calTemp.get(Calendar.YEAR);
        int forecastMonth = calTemp.get(Calendar.MONTH) +1;
        List<DatasetRaster> lsDR = new ArrayList();
        int monthIndex = 1;
        //选出dr
        try{
        	for(int i = 0;i<dsCount;i++){
            	Dataset dataset = ds.GetDataset(i);
            	String strMeta = dataset.GetMetadata();
            	json = new JSONObject(strMeta);
                int level = json.getInt("NETCDF_DIM_level");
                if(level ==10){//最小
                	tempMonth++;
                }
                if(tempMonth>12){
                	tempMonth = 1;
                    startYear++;
                }
                if(startYear == forecastYear && tempMonth == forecastMonth && level == targetLevel){
                	DatasetRaster dr = (DatasetRaster) dataset;
                	lsDR.add(dr);
                	calTemp.add(Calendar.MONTH, 1);
                	forecastYear = calTemp.get(Calendar.YEAR);
                    forecastMonth = calTemp.get(Calendar.MONTH) +1;
                    monthIndex++;
                    if(monthIndex>month){
                    	break;
                    }
                }
            }
        }
        catch(Exception ex){
        	
        }
        if(lsDR.size() != month){
        	LogTool.logger.error("模式资料无新数据!");
			return null;
        }
        Map<String,List<Double>> mapData = new HashMap();
        for(XNStation xnStation:lsXNStation){
        	String stationNum = xnStation.getStation_Id_C();
        	double lon = xnStation.getLon();
        	double lat = xnStation.getLat();
        	List<Double> lsData = new ArrayList();
        	for(DatasetRaster dr:lsDR){
        		Point2D p2d=new Point2D.Double(lon,lat);
                Point2D cell=dr.PointToCell(p2d);
                int x=(int) cell.getX();
                int y=(int) cell.getY();
                double val=dr.GetValue(x, y);//此格点值
                lsData.add(val);
        	}
        	mapData.put(stationNum, lsData);
        }
        ws.CloseDatasource(heightAlias);
        return mapData;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月21日
	 * @修改日期:2018年1月21日
	 * @参数:
	 * @返回:
	 * @说明:获取预报模式数据
	 */
	public Map<String,List<Double>> getForecastModeStationData(Workspace ws,Calendar cal,String[] strForecastDate,Config config,List<XNStation> lsXNStation){
		Calendar tempMakeDate = (Calendar) cal.clone();
		String strMakeDate1 = DateUtil.format("yyyyMM", tempMakeDate);
		String strMakeDate2 = strMakeDate1 + "01";
		tempMakeDate.add(Calendar.YEAR, 1);
		String strEndDate = DateUtil.format("yyyyMM", tempMakeDate);
		tempMakeDate.add(Calendar.YEAR, -1);//减回来
		String modeFileFormat = "%s.atm.Z3.%s-%s_prs0500_member.nc";
		String fileName = String.format(modeFileFormat, strMakeDate2,strMakeDate1,strEndDate);
		String path = config.getModeHgtPath();
		String strHgtFile = path + fileName;
		String modeAlias = "mode"+DateUtil.format("HHmmss", Calendar.getInstance());
        String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+modeAlias+"\",\"Server\":\"" + strHgtFile + "\"}";
        Datasource ds = ws.OpenDatasource(strJson);
        int dsCount = ds.GetDatasetCount();
        CommonFun cf = new CommonFun();
        List<DatasetRaster> lsDR = new ArrayList();
        for(int i = 0;i<dsCount;i++){
        	String strDate = DateUtil.format("yyyyMM", tempMakeDate);
        	Boolean isContain = cf.ArrayIsContain(strForecastDate, strDate);
        	if(isContain){
        		DatasetRaster dr = (DatasetRaster) ds.GetDataset(i);
        		lsDR.add(dr);
        	}
        	tempMakeDate.add(Calendar.MONTH, 1);
        	i += 24;//只取第一层
        }
        Map<String,List<Double>> mapData = new HashMap();
        for(XNStation xnStation:lsXNStation){
        	String stationNum = xnStation.getStation_Id_C();
        	double lon = xnStation.getLon();
        	double lat = xnStation.getLat();
        	List<Double> lsData = new ArrayList();
        	for(DatasetRaster dr:lsDR){
        		Point2D p2d=new Point2D.Double(lon,lat);
                Point2D cell=dr.PointToCell(p2d);
                int x=(int) cell.getX();
                int y=(int) cell.getY();
                double val=dr.GetValue(x, y);//此格点值
                lsData.add(val);
        	}
        	mapData.put(stationNum, lsData);
        }
        ws.CloseDatasource(modeAlias);
        return mapData;
	}
}
