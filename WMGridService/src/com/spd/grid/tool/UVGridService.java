package com.spd.grid.tool;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.GeoPoint;
import com.mg.objects.GeoRegion;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.grid.domain.Station;
import com.spd.weathermap.domain.GridData;
import com.spd.weathermap.util.LogTool;

/**
 * @AUTHOR:WANGKUN
 * @DATE:2016年10月20日
 * @DESCRIPTION:UV风场转换
 */
public class UVGridService {
	private static double Resolution=1.0;//分辨率为100公里
	private static double Left=73.0;
	private static double Bottom=17.0;
	private static double Width=60.0;
	private static double Height=36.0;
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月20日
	 * @RETURN:
	 * @PARAM:
	 * @DESCRIPTION:
	 */
	public List<DatasetRaster> GetAllDatasetRaster(Datasource ds,//无用
			Calendar calStart, Calendar calEnd, String level, String flag,
			List<String> date) {
		List<DatasetRaster> lsDR = new ArrayList<>();
		SimpleDateFormat dfyyMMddHHmm = new SimpleDateFormat("yyMMddHHmm");
		SimpleDateFormat dfyyMMddHH = new SimpleDateFormat("yyMMddHH");
		SimpleDateFormat dfView = new SimpleDateFormat("yyyy年MM月dd日");
		String parttern = "t_vwnd_uv_%s_p_%s_024_%s_%s";
		while (calStart.compareTo(calEnd) < 1) {
			Date curDate = calStart.getTime();
			String maketime = dfyyMMddHHmm.format(curDate);
			String datetime = dfyyMMddHH.format(curDate);
			String drName = String.format(parttern, maketime, datetime, level,
					flag);
			Dataset dataset = ds.GetDataset(drName);
			if (dataset == null) {
				LogTool.logger.error(drName + ",为空!");
				continue;
			}
			String viewDate = dfView.format(curDate);
			date.add(viewDate);
			DatasetRaster dr = (DatasetRaster) dataset;
			lsDR.add(dr);
			calStart.add(Calendar.DATE, 1);
		}
		return lsDR;
	}

	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月20日
	 * @RETURN:一个数组
	 * @PARAM:lsDR-栅格列表，r-需要取值的行，c-需要取值的列
	 * @DESCRIPTION:
	 */
	public double[] OnePointToSeries(List<DatasetRaster> lsDR, int r, int c) {//无用
		int size = lsDR.size();
		double[] dataSeries = new double[size];
		for (int i = 0; i < size; i++) {
			DatasetRaster dr = lsDR.get(i);
			Double val = dr.GetValue(c, r);
			dataSeries[i] = val;
		}
		return dataSeries;
	}

	/**
	 * 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月20日
	 * @RETURN:
	 * @PARAM:datas-数组，p1-滤波1，p2-滤波2
	 * @DESCRIPTION:
	 */
	public double[] Butterworth(List<Double> datas, int p1, int p2) {
		int itx = datas.size();
		double[] temp = new double[itx + 1];
		double[] result = new double[itx];
		for (int i = 1; i < itx + 1; i++) {
			temp[i] = datas.get(i - 1);
		}
		float dt = 1.0f;
		double[] wk1 = new double[itx + 1];
		double[] wk2 = new double[itx + 1];
		double pi2 = 2.0 * Math.PI;
		double w1 = pi2 / p1;
		double w2 = pi2 / p2;
		double wo = Math.sqrt(w1 * w2);
		double dw = 2.0 * Math.abs((Math.sin(w1 * dt))
				/ (1.0 + Math.cos(w1 * dt)) - (Math.sin(w2 * dt))
				/ (1.0 + Math.cos(w2 * dt)));
		double dws = (4.0 * Math.sin(w1 * dt) * Math.sin(w2 * dt))
				/ ((1.0 + Math.cos(w1 * dt)) * (1.0 + Math.cos(w2 * dt)));
		double b3 = (2.0 * dw) / (4.0 + 2.0 * dw + dws);
		double b2 = (4.0 - 2.0 * dw + dws) / (4.0 + 2.0 * dw + dws);
		double b1 = (2.0 * (dws - 4.0)) / (4.0 + 2.0 * dw + dws);
		for (int it = 1; it <= itx; it++) {
			wk1[it] = 0.0;
		}
		for (int it = 3; it <= itx; it++) {
			wk1[it] = b3 * (temp[it] - temp[it - 2]) - b1 * wk1[it - 1] - b2
					* wk1[it - 2];
		}
		wk2[itx] = wk1[itx];
		wk2[itx - 1] = wk1[itx - 1];
		wk1[1] = temp[1];
		wk1[2] = temp[2];
		for (int it = 2; it <= itx - 1; it++) {
			int ii = itx - it;
			wk2[ii] = b3 * (wk1[ii] - wk1[ii + 2]) - b1 * wk2[ii + 1] - b2
					* wk2[ii + 2];
		}
		for (int it = 1; it <= itx; it++) {
			temp[it] = wk2[it];
		}
		for (int i = 0; i < itx; i++) {
			result[i] = ((int) (temp[i + 1] * 100)) / 100.0;
		}
		return result;
	}

	/**
	 * 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月26日
	 * @RETURN:List<double[]>
	 * @PARAM:lsDR-格点列表
	 * @DESCRIPTION:取每个格点的数据存入一个double[]里面，返回所有
	 */
	public List<double[]> GetAllGrids(List<DatasetRaster> lsDR) {//无用
		List<double[]> lsAllData = new ArrayList<>();
		int dataCount = lsDR.size();
		DatasetRaster firstDR = lsDR.get(0);
		int cols = firstDR.GetWidth();
		int rows = firstDR.GetHeight();
		int gridCount = rows * cols;
		String valType = firstDR.GetValueType();
		Scanline sl = new Scanline(valType, cols);
		for (int i = 0; i < dataCount; i++)// 取出所有数据
		{
			DatasetRaster dr = lsDR.get(i);
			dr.CalcExtreme();
			int thisCols = dr.GetWidth();
			int thisRows = dr.GetHeight();
			if (thisRows != rows || thisCols != cols) {
				LogTool.logger.error("数据格点个数不相同!");
				return lsAllData;
			}
			double[] data = new double[gridCount];
			for (int r = 0; r < rows; r++) {
				dr.GetScanline(0, r, sl);
				int count = sl.GetValueCount();
				for (int c = 0; c < cols; c++) {
					double val = sl.GetValue(c);
					int index = r * cols + c;
					data[index] = val;
				}
			}
			lsAllData.add(data);
		}
		sl.Destroy();
		return lsAllData;
	}

	/**
	 * 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月26日
	 * @RETURN:List<double[]>
	 * @PARAM:lsAllData
	 * @DESCRIPTION:把取出来的所有时次的格点按格点位置组合，再做滤波处理
	 */
	public List<double[]> ButterworthAllData(List<double[]> lsAllData,
			int rows, int cols, int dataCount) {
		List<double[]> lsBFData = new ArrayList<>();
		int gridCount = rows * cols;
		for (int i = 0; i < gridCount; i++) {
			double[] datas = new double[dataCount];
			for (int d = 0; d < dataCount; d++) {
				double val = lsAllData.get(d)[i];
				datas[d] = val;
			}
			/*double[] bwDatas = Butterworth(datas, 30, 60);
			lsBFData.add(bwDatas);*/
		}
		return lsBFData;
	}

	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月26日
	 * @RETURN:所有格点
	 * @PARAM:lsBFData-滤波后的数据，dataCount-格点个数，rows-格点行，cols-格点列，left-左，right-右，top-上，bottom-下，noValue-无效值
	 * @DESCRIPTION:
	 */
	public ArrayList<GridData> RecoveryToGrid(List<double[]> lsUBFData,
			List<double[]> lsVBFData, int dataCount, int rows, int cols,
			double left, double bottom, double right, double top, double noValue) {
		ArrayList<GridData> grids = new ArrayList<>();
		int gridCount = rows * cols;
		ArrayList<Double> dValues = null;
		// ReduceUtil reduceUtil=new ReduceUtil();
		for (int i = 0; i < dataCount; i++) {
			System.out.println("还原第" + i + "个格点");
			GridData grid = new GridData();
			dValues = new ArrayList<>();
			for (int c = 0; c < gridCount; c++) {
				double[] seriesU = lsUBFData.get(c);
				double[] seriesV = lsVBFData.get(c);
				double uVal = seriesU[i];
				double vVal = seriesV[i];
				Double dSpeed = Math.sqrt(uVal * uVal + vVal * vVal);
				Double dDirection = 270.0 - Math.atan2(vVal, uVal) * 180.0
						/ Math.PI;
				dSpeed = Math.round(dSpeed * 10.0) / 10.0;
				dDirection = Math.round(dDirection * 10.0) / 10.0;
				dValues.add(dDirection);
				dValues.add(dSpeed);
			}
			grid.setLeft(left);
			grid.setBottom(bottom);
			grid.setRight(right);
			grid.setTop(top);
			grid.setRows(rows);
			grid.setCols(cols);
			grid.setDValues(dValues);
			grid.setNoDataValue(noValue);
			grids.add(grid);
		}
		return grids;
	}

	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月26日
	 * @RETURN:List<DatasetRaster>
	 * @PARAM:ds-数据源，lsUDR-所有U数据
	 * @DESCRIPTION:
	 */
	public List<DatasetRaster> GetAllVDatasetRaster(Datasource ds,
			List<DatasetRaster> lsUDR) {
		List<DatasetRaster> lsVDR = new ArrayList<>();
		int dataCount = lsUDR.size();
		for (int c = 0; c < dataCount; c++) {
			DatasetRaster thidDR = lsUDR.get(c);
			String drUName = thidDR.GetName();
			String drVName = drUName.substring(0, drUName.length() - 1) + "v";
			DatasetRaster uDR = (DatasetRaster) ds.GetDataset(drVName);
			lsVDR.add(uDR);
		}
		return lsVDR;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月27日
	 * @RETURN:
	 * @PARAM:ws-工作空间，uFile-uwind文件，v-File-vwind文件
	 * @DESCRIPTION:打开UV文件
	 */
	public void OpenUVFile(Workspace ws,String uFile,String vFile)
	{
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"uwind\",\"Server\":\"%s\"}", uFile);
		Datasource dsU=ws.GetDatasource("uwind");
		if(dsU==null)
			ws.OpenDatasource(strJson);
		strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"vwind\",\"Server\":\"%s\"}", vFile);
		Datasource dsV=ws.OpenDatasource(strJson);
		if(dsV==null)
			ws.OpenDatasource(strJson);
	}
	public void GetWindData(Datasource ds,LinkedHashMap<String,DatasetRaster> mapUV,Calendar calStart,Calendar calEnd,int targetlevel)
	{
		Calendar calCurrent=Calendar.getInstance();
		int count=ds.GetDatasetCount();
		int startMonth=calStart.get(Calendar.MONTH)+1;
		int endMonth=calEnd.get(Calendar.MONTH)+1;
		for(int i=0;i<count;i++){//NETCDF_DIM_time":"1893408,,NETCDF_DIM_level
			calCurrent.set(1800, 0, 1, 0, 0);
			Dataset dataset=ds.GetDataset(i);
			String meta=dataset.GetMetadata();
			JSONObject json=null;
			int time=0;
			int level=0;
			try{
				json = new JSONObject(meta);
				if(!json.has("NETCDF_DIM_time"))//有待确定
					break;
				time=json.getInt("NETCDF_DIM_time");
				level=json.getInt("NETCDF_DIM_level");
			} catch (JSONException e) {
				LogTool.logger.error(e.getMessage());
			}
			if(level!=targetlevel)//不是这个层次
				continue;
			int days=time/24;
			calCurrent.add(Calendar.DATE, days);
			String strDate=Common.yyyyMMdd.format(calCurrent.getTime());
			String strMonth=strDate.substring(4, 6);
			int tempMonth=Integer.parseInt(strMonth);
			if(tempMonth==startMonth||tempMonth==endMonth){
				DatasetRaster dr=(DatasetRaster) dataset;
				String MD=strDate.substring(4);
				mapUV.put(MD, dr);//key存月日
			}
		}
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月31日
	 * @RETURN:
	 * @PARAM:
	 * @DESCRIPTION:
	 */
	public Boolean CreateUVTifs(Workspace ws,Calendar calStart, Calendar calEnd,String level,LinkedHashMap<String,DatasetRaster> uReslt,LinkedHashMap<String,DatasetRaster> vReslt,int p1,int p2)
	{
		Boolean result=true;//是否是生成
		String strStart = Common.yyyyMMdd.format(calStart.getTime());// 开始时间
		String strEnd = Common.yyyyMMdd.format(calEnd.getTime());// 结束时间
		String tempPath=System.getProperty("java.io.tmpdir")+"efs/";
		//判断目录
		File dic=new File(tempPath);
		if(!dic.exists())
		{
			try{
				dic.mkdir();
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		String strJson = "";
		int width=(int)(Width/Resolution);
		int height=(int)(Height/Resolution);
		String[] strUV=new String[]{"u","v"};
		int eleSize=strUV.length;
		Calendar calCurrent=(Calendar) calStart.clone();
		while (calCurrent.compareTo(calEnd) < 1) 
		{
			String strCurDate = Common.yyyyMMdd.format(calCurrent.getTime());
			for(int i=0;i<eleSize;i++)
			{
				String ele=strUV[i];
				String alias="wind_"+strStart + "_" + strEnd+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2+"_"+ele;//用搜索开始时间_结束时间_处理日期作为唯一文件名
				String fileName = alias + ".tif";
				String file = tempPath + fileName;
				file = file.replace("\\", "/");
				strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
				File fi=new File(file);
				if(fi.exists())
				{
					Datasource ds = ws.OpenDatasource(strJson);
					DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
					if(ele.equals("u"))
					{
						uReslt.put(strCurDate, dr);
					}
					else if(ele.equals("v"))
					{
						vReslt.put(strCurDate, dr);
					}
					calCurrent.add(Calendar.DATE, 1);
					result=false;
					continue;
				}
				Datasource ds = ws.CreateDatasource(strJson);
				strJson = String.format("\"Name\":\"TestGTiff\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d", width, height);
				DatasetRaster dr = ds.CreateDatasetRaster("{" + strJson + "}");
				dr.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
		        dr.SetBounds(new Rectangle2D.Double(Left, Bottom, Width, Height));
		        dr.FlushCache();
		        if(ele.equals("u"))
				{
					uReslt.put(strCurDate, dr);
				}
				else if(ele.equals("v"))
				{
					vReslt.put(strCurDate, dr);
				}
			}
			calCurrent.add(Calendar.DATE, 1);
		}
		System.out.println("所有tif文件生成完成!");
		return result;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月31日
	 * @RETURN:
	 * @PARAM:
	 * @DESCRIPTION:
	 */
	public Boolean CreateTifs(Workspace ws,Calendar calStart, Calendar calEnd,LinkedHashMap<String,DatasetRaster> reslt,String element,int level,int p1,int p2,String flag,String tempDir){
		Boolean result=true;//是否是生成
		String strStart = DateFormat.MMdd.format(calStart.getTime());// 开始时间
		String strEnd = DateFormat.MMdd.format(calEnd.getTime());// 结束时间
		//判断目录
		File dic=new File(tempDir);
		if(!dic.exists()){
			return false;
		}
		String strJson = "";
		int width=(int)(Width/Resolution);
		int height=(int)(Height/Resolution);
		Calendar calCurrent=(Calendar) calStart.clone();
		while (calCurrent.compareTo(calEnd) < 1){
			String strCurDate=DateFormat.MMdd.format(calCurrent.getTime());
			System.out.println("创建"+strCurDate+"的tif");
			String alias=element+"_"+strStart + "_" + strEnd+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2;//用要素_开始时间_结束时间_处理日期_层次_滤波1_滤波2作为唯一文件名
			if(!flag.equals("")){
				alias+="_"+flag;
			}
			String fileName = alias + ".tif";
			String file = tempDir + fileName;
			file = file.replace("\\", "/");
			strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
			File fi=new File(file);
			if(fi.exists()){
				Datasource ds = ws.OpenDatasource(strJson);
				DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
				reslt.put(strCurDate, dr);
				calCurrent.add(Calendar.DATE, 1);
				result=false;
				continue;
			}
			Datasource ds = ws.CreateDatasource(strJson);
			strJson = String.format("\"Name\":\"GTiff\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d", width, height);
			DatasetRaster dr = ds.CreateDatasetRaster("{" + strJson + "}");
			dr.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
	        dr.SetBounds(new Rectangle2D.Double(Left, Bottom, Width, Height));
	        dr.FlushCache();
	        reslt.put(strCurDate, dr);
	        calCurrent.add(Calendar.DATE, 1);
		}
		LogTool.logger.debug("所有tif文件生成完成!");
		return result;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月31日
	 * @RETURN:
	 * @PARAM:ds-数据源，mapOLR-存放格点对象，calStart-开始日期，calEnd-结束日期
	 * @DESCRIPTION:
	 */
	public void GetOLRData(Datasource ds,LinkedHashMap<String,DatasetRaster> mapOLR,Calendar calStart,Calendar calEnd)
	{
		LogTool.logger.debug("开始获取OLR数据!");
		Calendar calStartTemp=(Calendar) calStart.clone();
		calStartTemp.set(Calendar.YEAR, 2016);
		Calendar calEndTemp=(Calendar) calEnd.clone();
		calEndTemp.set(Calendar.YEAR, 2016);
		Calendar calCurrent=Calendar.getInstance();
		int count=ds.GetDatasetCount();
		for(int i=0;i<count;i++){//17540448为2002年1月1号
			calCurrent.set(2002, 0, 1, 0, 0);
			DatasetRaster dr=(DatasetRaster) ds.GetDataset(i);
			String meta=dr.GetMetadata();
			JSONObject json=null;
			int time=0;
			try{
				json = new JSONObject(meta);
				if(!json.has("NETCDF_DIM_time"))//有待确定
					break;
				time=json.getInt("NETCDF_DIM_time");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			time=time-17540448;
			int days=time/24;
			calCurrent.add(Calendar.DATE, days);
			String strDate = DateFormat.MMdd.format(calCurrent.getTime());
			if(calCurrent.compareTo(calStartTemp)<0||calCurrent.compareTo(calEndTemp)>0)//不在搜索时间范围内
				continue;
			mapOLR.put(strDate, dr);
		}
		System.out.println("获取OLR数据完成!");
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年10月31日
	 * @RETURN:
	 * @PARAM:calStart-开始日期，calEnd-结束日期
	 * @DESCRIPTION:计算LSW指数，需要uv风和olr
	 */
	public void CalLSWIndex(Calendar calStart,Calendar calEnd,int p1,int p2,int level)
	{
		long begin = System.currentTimeMillis();
		LogTool.logger.debug("开始计算lsw数据!");
		Workspace ws=new Workspace();
		String path=System.getProperty("java.io.tmpdir")+"efs/";
;		path=path.replace("\\", "/");
		String strJson = "";
		File fi=null;
		String[] eles={"u","v","olr"};
		LinkedHashMap<String,DatasetRaster> resultLSW=new LinkedHashMap<>();//新建一个Map，用来存放LSW结果时间与DataRaster
		Calendar calCurrent=(Calendar) calStart.clone();
		String strStartDate=DateFormat.MMdd.format(calStart.getTime());
		String strEndDate=DateFormat.MMdd.format(calEnd.getTime());
		LinkedHashMap<String,DatasetRaster> resultU=new LinkedHashMap<>();//U
		LinkedHashMap<String,DatasetRaster> resultV=new LinkedHashMap<>();//V
		LinkedHashMap<String,DatasetRaster> resultOLR=new LinkedHashMap<>();//OLR
		Boolean isNew=CreateTifs(ws,calStart, calEnd,resultLSW,"lsw",level,p1,p2,"","");//生成tif
		if(!isNew){
			LogTool.logger.info("lsw已存在!");
			return;
		}
		int eleSize=eles.length;
		while(calCurrent.compareTo(calEnd)<1){//得到需要的格点
			String strCurrentDate=DateFormat.MMdd.format(calCurrent.getTime());
			for(int i=0;i<eleSize;i++){
				String ele=eles[i];
				String alias="";
				if(ele.equals("u")||ele.equals("v")){
					alias="uv_"+strStartDate+"_"+strEndDate+"_"+strCurrentDate+"_"+level+"_"+p1+"_"+p2+"_"+ele;
				}
				else{
					alias=ele+"_"+strStartDate+"_"+strEndDate+"_"+strCurrentDate+"_"+"1000"+"_"+p1+"_"+p2;
				}
				String file=path+alias+".tif";
				fi=new File(file);
				if(!fi.exists()){
					LogTool.logger.debug(file+"不存在!");
					continue;
				}
				strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
				Datasource ds=ws.OpenDatasource(strJson);
				DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
				if(ele.equals("u")){
					resultU.put(strCurrentDate, dr);
				}
				else if(ele.equals("v")){
					resultV.put(strCurrentDate, dr);
				}
				else if(ele.equals("olr")){
					resultOLR.put(strCurrentDate, dr);
				}
			}
			calCurrent.add(Calendar.DATE,1);
		}
		//得到第一个格点的大小
		DatasetRaster drFirst=(DatasetRaster) resultU.values().toArray()[0];//得到第一个目标数据，作为参考标准
		int width=drFirst.GetWidth();
		int height=drFirst.GetHeight();
		int dataCount=resultU.keySet().size();//以U为基准
		int dataCountV=resultV.keySet().size();
		if(dataCount==0||dataCountV==0||dataCount!=dataCountV){
			LogTool.logger.error("找到的UV文件个数据为零或UV产品个数不相同!");
			return;
		}
		double[] dataU=new double[dataCount];
		double[] dataV=new double[dataCount];
		double[] dataOLR=new double[dataCount];
		//OLR可以无数据
		int olrSize=resultOLR.keySet().size();
		Boolean olrFlag=true;
		if(olrSize<1)
			olrFlag=false;
		for(int r=0;r<height;r++)
		{
			for(int c=0;c<width;c++)
			{
				int i=0;
				for(String key:resultU.keySet())
				{
					DatasetRaster drU=resultU.get(key);
					DatasetRaster drV=resultV.get(key);
					Double uVal=drU.GetValue(c, r);
					Double vVal=drV.GetValue(c, r);
					dataU[i]=uVal;
					dataV[i]=vVal;
					if(olrFlag){
						DatasetRaster drOLR=resultOLR.get(key);
						Double olrVal=drOLR.GetValue(c, r);
						dataOLR[i]=olrVal;
					}
					i++;
				}
				double[] vsm=GetVSM(dataU,dataV);
				double avgVSM=CommonFun.CalAvg(vsm);
				double sdV = GetStandardDeviation(vsm,avgVSM);
				double[] lsw=new double[dataCount];
				if(olrFlag){
					double avgR=CommonFun.CalAvg(dataOLR);
					double sdR = GetStandardDeviation(dataOLR,avgR);
					for (int index = 0; index < dataCount; index++){
						lsw[index] = (vsm[index] - avgVSM) / sdV - (dataOLR[index] - avgR) / sdR;
		            }
				}
				else
				{
					for (int index = 0; index < dataCount; index++)
		            {
						lsw[index] = (vsm[index] - avgVSM) / sdV;
		            }
				}
				i=0;
				for(String key:resultLSW.keySet())
				{
					double val=lsw[i];
					DatasetRaster drLSW=resultLSW.get(key);
					drLSW.SetValue(c, r, val);
					i++;
				}
			}
		}
		//关闭打开的全部数据源
		CommonFun.CloseDS(ws);
		System.out.println("lsw计算完成!");
		long end = System.currentTimeMillis() - begin;
		long usetime=end/1000;
		System.out.println("共耗时：" + usetime + "秒");
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月1日
	 * @RETURN:VSM数组
	 * @PARAM:lsU-U列表，lsV-V列表
	 * @DESCRIPTION:计算VSM
	 */
	private double[] GetVSM(double[] dataU,double[] dataV)
	{
		int uSize=dataU.length;
		int vSize=dataV.length;
		double[] vsm=new double[uSize];
		if(uSize!=vSize)
		{
			System.out.println("GetVSM中U数据和V数据个数不相同!");
			return vsm;
		}
		Double ss=Math.sqrt (2);
		for (int i = 0; i < uSize; i++)
        {
			vsm[i] = (dataU[i] + dataV[i]) / ss;
        }
		return vsm;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月1日
	 * @RETURN:标准差
	 * @PARAM:data-数组,avg-数组平均值
	 * @DESCRIPTION:计算标准差
	 */
	private double GetStandardDeviation(double[] data,double avg)
	{
		int size=data.length;
		if(size==0)
			return 0;
		double qSD=0;
		for(int i=0;i<size;i++)
		{
			double val=data[i];
			qSD += Math.pow((val - avg), 2)/size;
		}
		double SD= Math .sqrt(qSD );
		return SD;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月8日
	 * @RETURN:
	 * @PARAM:ws-工作空间，geo-面对象,calStart-开始日期，calEnd-结束日期，level-层次，p1-周期1，p2-周期2,flag-u或v
	 * @DESCRIPTION:获取UV平均值
	 */
	public List<Double> GetAreaAvg(Workspace ws,GeoRegion geo,Calendar calStart,Calendar calEnd,String level,int p1,int p2,String element,String flag,String tempDir){
		List<Double> lsResult=new ArrayList<>();
		Calendar calCurrent=(Calendar) calStart.clone();
		String strStartDate=DateFormat.MMdd.format(calStart.getTime());
		String strEndDate=DateFormat.MMdd.format(calEnd.getTime());
		String strJson="";
		Rectangle2D r2d=geo.GetBounds();
		Point2D rt=new Point2D.Double(r2d.getX()+r2d.getWidth(),r2d.getY());
		Point2D lb=new Point2D.Double(r2d.getX(),r2d.getY());
		while(calCurrent.compareTo(calEnd)<1){
			String strCurDate=DateFormat.MMdd.format(calCurrent.getTime());
			String alias=element+"_"+strStartDate+"_"+strEndDate+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2+"_"+flag;
			String filePath = tempDir + alias+".tif";
			File fi=new File(filePath);
			if(!fi.exists()){
				LogTool.logger.error("未找到文件:"+filePath);
				return null;
			}
			strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + filePath + "\"}";
			Datasource ds=ws.OpenDatasource(strJson);
			DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
			List<Double> lsTemp=new ArrayList<>();
			Point2D lbCell=dr.PointToCell(lb);
			Point2D rtCell=dr.PointToCell(rt);
			int y1=(int)lbCell.getY();
			int x1=(int)lbCell.getX();
			int y2=(int)rtCell.getY();
			int x2=(int)rtCell.getX();
			for(int r=y1;r<=y2;r++){
				for(int c=x1;c<=x2;c++){
					double val=dr.GetValue(c, r);
					lsTemp.add(val);
				}
			}
			double avgTemp=CommonFun.CalAvg(lsTemp);
			lsResult.add(avgTemp);
			calCurrent.add(Calendar.DATE, 1);
			ws.CloseDatasource(alias);
		}
		return lsResult;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月8日
	 * @RETURN:
	 * @PARAM:ws-工作空间，geo-面对象,calStart-开始日期，calEnd-结束日期，level-层次，p1-周期1，p2-周期2
	 * @DESCRIPTION:获取lsw平均值
	 */
	public List<Double> GetLSWAvg(Workspace ws,GeoRegion geo,Calendar calStart,Calendar calEnd,String level,int p1,int p2){
		
		List<Double> lsAllLSW=new ArrayList<>();
		Calendar calCurrent=(Calendar) calStart.clone();
		String strStartDate=Common.yyyyMMdd.format(calStart.getTime());
		String strEndDate=Common.yyyyMMdd.format(calEnd.getTime());
		String path=System.getProperty("java.io.tmpdir")+"efs/";
		path=path.replace("\\", "/");
		String strJson="";
		GeoRel geoRel=new GeoRel();
		Rectangle2D r2d=geo.GetBounds();
		Point2D rt=new Point2D.Double(r2d.getX()+r2d.getWidth(),r2d.getY());
		Point2D lb=new Point2D.Double(r2d.getX(),r2d.getY());
		while(calCurrent.compareTo(calEnd)<1)
		{
			String strCurDate=Common.yyyyMMdd.format(calCurrent.getTime());
			String alias="lsw_"+strStartDate+"_"+strEndDate+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2;
			String filePath=path+alias+".tif";
			File fi=new File(filePath);
			if(!fi.exists())
			{
				return null;
			}
			strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + filePath + "\"}";
			Datasource ds=ws.OpenDatasource(strJson);
			DatasetRaster dr=(DatasetRaster) ds.GetDataset(0);
			int rows=dr.GetHeight();
			int cols=dr.GetWidth();
			Scanline sl=new Scanline(dr.GetValueType(),cols);
			List<Double> lsLSW=new ArrayList<>();
			Point2D lbCell=dr.PointToCell(lb);
			Point2D rtCell=dr.PointToCell(rt);
			int y1=(int)lbCell.getY();
			int x1=(int)lbCell.getX();
			int y2=(int)rtCell.getY();
			int x2=(int)rtCell.getX();
			for(int r=y1;r<=y2;r++)
			{
				for(int c=x1;c<=x2;c++)
				{
					double val=dr.GetValue(c, r);
					lsLSW.add(val);
				}
			}
			double avgLSW=CommonFun.CalAvg(lsLSW);
			lsAllLSW.add(avgLSW);
			calCurrent.add(Calendar.DATE, 1);
		}
		CommonFun.CloseDS(ws);
		return lsAllLSW;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月9日
	 * @RETURN:UV所有时效
	 * @PARAM:ws-工作空间
	 * @DESCRIPTION:获取UV所有时效
	 */
	public List<String> GetUVValidDate(Workspace ws)
	{
		List<String> lsDate=new ArrayList<>();
		String uvDic="";
		try{
			uvDic=ComfigureUtil.config.getUvPath();
		}
		catch(Exception ex){
			LogTool.logger.info(ex.getMessage());
		}
		String uFile=uvDic+"uwnd.2016.nc";
		String vFile=uvDic+"vwnd.2016.nc";
		File fi=new File(uFile);
		if(!fi.exists()){
			LogTool.logger.info(uFile+"不存在!");
			return null;
		}
		fi=new File(vFile);
		if(!fi.exists()){
			LogTool.logger.info(vFile+"不存在!");
			return null;
		}
		LogTool.logger.info("开始打开源文件!");
		Datasource dsU=null;
		Datasource dsV=null;
		try{
			String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"uwind\",\"Server\":\"%s\"}", uFile);
			dsU=ws.OpenDatasource(strJson);
			strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"vwind\",\"Server\":\"%s\"}", vFile);
			dsV=ws.OpenDatasource(strJson);
		}
		catch(Exception ex){
			LogTool.logger.info("打开数据源失败!");
			return null;
		}
		LogTool.logger.info("打开数据源成功!");
		int dsUCount=dsU.GetDatasetCount();
		int dsVCount=dsV.GetDatasetCount();
		Calendar calInit=Calendar.getInstance();
		//计算U
		List<String> lsUDate=new ArrayList<>();
		for(int c=0;c<dsUCount;c++)
		{
			calInit.set(1800, 0, 1, 0, 0);//设置初始日期
			DatasetRaster dr=(DatasetRaster) dsU.GetDataset(c);
			if(dr==null)
				continue;
			String meta=dr.GetMetadata();
			JSONObject json=null;
			int time=0;
			try
			{
				json = new JSONObject(meta);
				if(!json.has("NETCDF_DIM_time"))//有待确定
				{
					break;
				}
				time=json.getInt("NETCDF_DIM_time");
				int days=time/24;
				calInit.add(Calendar.DATE, days);
				String strDate=Common.stryyyyMMdd.format(calInit.getTime());
				if(!lsUDate.contains(strDate))
					lsUDate.add(strDate);
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		//计算U
		List<String> lsVDate=new ArrayList<>();
		for(int c=0;c<dsVCount;c++)
		{
			calInit.set(1800, 0, 1, 0, 0);//设置初始日期
			DatasetRaster dr=(DatasetRaster) dsV.GetDataset(c);
			if(dr==null)
				continue;
			String meta=dr.GetMetadata();
			JSONObject json=null;
			int time=0;
			try
			{
				json = new JSONObject(meta);
				if(!json.has("NETCDF_DIM_time"))//有待确定
				{
					break;
				}
				time=json.getInt("NETCDF_DIM_time");
				int days=time/24;
				calInit.add(Calendar.DATE, days);
				String strDate=Common.stryyyyMMdd.format(calInit.getTime());
				if(!lsVDate.contains(strDate))
					lsVDate.add(strDate);
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		int uDateCount=lsUDate.size();
		for(int i=0;i<uDateCount;i++)
		{
			String uDate=lsUDate.get(i);
			if(lsVDate.contains(uDate))
			{
				lsDate.add(uDate);
			}
		}
		return lsDate;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年3月3日
	 * @RETURN:
	 * @PARAM:ws-工作空间
	 * @DESCRIPTION:获取OLR可用日期
	 */
	public List<String> GetOLRValidDate(Workspace ws)
	{
		List<String> lsDate=new ArrayList<>();
		String dic=ComfigureUtil.config.getOlrPath();
		dic=dic.replace("\\", "/");
		String file=dic+"olr.day.mean.nc";
		File fi=new File(file);
		if(!fi.exists())
		{
			LogTool.logger.info(file+"不存在!");
			return null;
		}
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"olr\",\"Server\":\"%s\"}", file);
		Datasource ds=ws.OpenDatasource(strJson);
		int dsCount=ds.GetDatasetCount();
		Calendar calInit=Calendar.getInstance();
		for(int i=0;i<dsCount;i++)
		{
			calInit.set(2002, 0, 1, 0, 0);
			DatasetRaster dr=(DatasetRaster) ds.GetDataset(i);
			String meta=dr.GetMetadata();
			JSONObject json;
			int time=0;
			try 
			{
				json = new JSONObject(meta);
				if(!json.has("NETCDF_DIM_time"))//有待确定
					break;
				time=json.getInt("NETCDF_DIM_time");
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			time=time-17540448;
			int days=time/24;
			calInit.add(Calendar.DATE, days);
			String strDate=Common.stryyyyMMdd.format(calInit.getTime());
			if(!lsDate.contains(strDate))
				lsDate.add(strDate);
		}
		return lsDate;
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月9日
	 * @RETURN:
	 * @PARAM:ws-工作空间,calStart-开始日期，calEnd-结束日期，level-层次，p1-周期1，p2-周期2
	 * @DESCRIPTION:处理UV
	 */
	/*public void ProcessUV1(Workspace ws,Calendar calStart,Calendar calEnd,String level,int p1,int p2)
	{
		System.out.println("开始处理UV");
		LinkedHashMap<String,DatasetRaster> resultU=new LinkedHashMap<>();//新建一个Map，用来存放U结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> resultV=new LinkedHashMap<>();//新建一个Map，用来存放V结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcMapU=new LinkedHashMap<>();//新建一个Map，用来存放U原数据时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcMapV=new LinkedHashMap<>();//新建一个Map，用来存放V原数据时间与DataRaster
		String[] flags={"u","v"};
		int size=flags.length;
		Boolean createResult=true;
		for(int i=0;i<size;i++)
		{
			String flag=flags[i];
			Boolean b=true;
			if(i==0){
				b=CreateTifs(ws,calStart, calEnd,resultU,"wind",level,p1,p2,flag);
			}
			else if(i==1){
				b=CreateTifs(ws,calStart, calEnd,resultV,"wind",level,p1,p2,flag);
			}
			createResult=createResult||b;
		}
		//Boolean createResult=CreateUVTifs(ws,calStart, calEnd,level,resultU,resultV,p1,p2);
		if(!createResult)//已经处理了
		{
			LogTool.logger.error("UV已处理过!");
			return;
		}
		String dic=ComfigureUtil.config.getUvPath();
		File fDic=new File(dic);
		if(!fDic.exists()){
			LogTool.logger.error(dic+"目录不存在!");
			return;
		}
		//读年份
		List<Integer> lsYear=new ArrayList<>();
		int year=calStart.get(Calendar.YEAR);
		lsYear.add(year);
		year=calStart.get(Calendar.YEAR);
		if(!lsYear.contains(year))
			lsYear.add(year);
		//打开数据
		String[] flags1={"uwnd","vwnd"};
		size=flags1.length;
		int yearSize=lsYear.size();
		List<Datasource> lsDSU=new ArrayList<>();
		List<Datasource> lsDSV=new ArrayList<>();
		for(int i=0;i<size;i++)//uv
		{
			String flag=flags1[i];
			for(int j=0;j<yearSize;j++)
			{
				year=lsYear.get(j);
				String file=dic+flag+"."+year+".nc";
				String alias=flag+year;
				String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", alias,file);
				Datasource ds=ws.OpenDatasource(strJson);
				if(flag.equals("uwnd")){
					lsDSU.add(ds);
				}
				else if(flag.equals("vwnd")){
					lsDSV.add(ds);
				}
			}
		}
		GetWindData(lsDSU, srcMapU, calStart, calEnd,level);//得到MapU
		GetWindData(lsDSV, srcMapV, calStart, calEnd,level);//得到MapV
		DatasetRaster drFirst=(DatasetRaster) resultU.values().toArray()[0];//得到第一个目标数据，作为参考标准
		int width=drFirst.GetWidth();
		int height=drFirst.GetHeight();
		for(int r=0;r<height;r++)
		{
			for(int c=0;c<width;c++)
			{
				List<Double> lsUData=new ArrayList<>();
				List<Double> lsVData=new ArrayList<>();
				Point2D ptLL=drFirst.CellToPoint(new Point2D.Double(c,r));
				for(String key:srcMapU.keySet())
				{
					DatasetRaster drU=srcMapU.get(key);
					DatasetRaster drV=srcMapV.get(key);
					Point2D ptXY=drU.PointToCell(ptLL);
					Double valU=drU.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					Double valV=drV.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					lsUData.add(valU);
					lsVData.add(valV);
				}
				double[] bwUDatas = Butterworth(lsUData, p1, p2);
				double[] bwVDatas = Butterworth(lsVData, p1, p2);
				//写入目标格点
				int i=0;
				for(String key:srcMapU.keySet())
				{
					double uVal=bwUDatas[i];
					double vVal=bwVDatas[i];
					DatasetRaster drU=resultU.get(key);
					DatasetRaster drV=resultV.get(key);
					uVal=(int)(uVal*100)/100.0;
					vVal=(int)(vVal*100)/100.0;
					drU.SetValue(c, r, uVal);
					drV.SetValue(c, r, vVal);
					i++;
				}
			}
		}
		srcMapU.clear();
		srcMapV.clear();
		resultU.clear();
		resultV.clear();
		CommonFun.CloseDS(ws);
		ProcessDerfUV(ws,calStart,calEnd,level,p1,p2);
		LogTool.logger.info("处理UV完成!");
	}*/
	/**
	 * @作者:wangkun
	 * @日期:2017年3月27日
	 * @修改日期:2017年3月27日
	 * @参数:ws-工作空间,calStart-开始日期，calEnd-结束日期，level-层次，p1-周期1，p2-周期2
	 * @返回:
	 * @说明:
	 */
	public Boolean ProcessUV(Workspace ws,Calendar calStart,Calendar calEnd,int level,int p1,int p2,String tempDir,String uvDir){
		LogTool.logger.debug("开始处理UV!");
		LinkedHashMap<String,DatasetRaster> resultU=new LinkedHashMap<>();//新建一个Map，用来存放U结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> resultV=new LinkedHashMap<>();//新建一个Map，用来存放V结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcMapU=new LinkedHashMap<>();//新建一个Map，用来存放U原数据时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcMapV=new LinkedHashMap<>();//新建一个Map，用来存放V原数据时间与DataRaster
		String[] flags={"u","v"};
		int size=flags.length;
		Boolean createResult=true;
		for(int i=0;i<size;i++)
		{
			String flag=flags[i];
			Boolean b=true;
			if(i==0){
				b=CreateTifs(ws,calStart, calEnd,resultU,"uv",level,p1,p2,flag,tempDir);
			}
			else if(i==1){
				b=CreateTifs(ws,calStart, calEnd,resultV,"uv",level,p1,p2,flag,tempDir);
			}
			createResult=createResult||b;
		}
		if(!createResult){//已经处理了
			LogTool.logger.debug("UV已处理过!");
			return false;
		}
		File fDic=new File(uvDir);
		if(!fDic.exists()){
			LogTool.logger.error(uvDir+"目录不存在!");
			return false;
		}
		String[] flags1={"uwnd","vwnd"};
		size=flags1.length;
		Datasource dsU=null;
		Datasource dsV=null;
		int curYear = calStart.get(Calendar.YEAR);
		for(int i=0;i<size;i++){//uv
			String flag=flags1[i];
			String strFile = uvDir+flag+"."+curYear+".nc";
			File file = new File(strFile);
			if(!file.exists()){
					System.out.println("文件"+strFile+"不存在!");
					return false;
			}
			String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", flag,strFile);
			if(flag.equals("uwnd")){
				dsU=ws.OpenDatasource(strJson);
			}
			else if(flag.equals("vwnd")){
				dsV=ws.OpenDatasource(strJson);
			}
		}
		GetWindData(dsU, srcMapU, calStart, calEnd,level);//得到MapU
		GetWindData(dsV, srcMapV, calStart, calEnd,level);//得到MapV
		DatasetRaster drFirst=(DatasetRaster) resultU.values().toArray()[0];//得到第一个目标数据，作为参考标准
		int width=drFirst.GetWidth();
		int height=drFirst.GetHeight();
		for(int r=0;r<height;r++)
		{
			for(int c=0;c<width;c++)
			{
				List<Double> lsUData=new ArrayList<>();
				List<Double> lsVData=new ArrayList<>();
				Point2D ptLL=drFirst.CellToPoint(new Point2D.Double(c,r));
				for(String key:srcMapU.keySet())
				{
					DatasetRaster drU=srcMapU.get(key);
					DatasetRaster drV=srcMapV.get(key);
					Point2D ptXY=drU.PointToCell(ptLL);
					Double valU=drU.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					Double valV=drV.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					lsUData.add(valU);
					lsVData.add(valV);
				}
				double[] bwUDatas = Butterworth(lsUData, p1, p2);
				double[] bwVDatas = Butterworth(lsVData, p1, p2);
				//写入目标格点
				int i=0;
				for(String key:srcMapU.keySet())
				{
					double uVal=bwUDatas[i];
					double vVal=bwVDatas[i];
					DatasetRaster drU=resultU.get(key);
					DatasetRaster drV=resultV.get(key);
					uVal=(int)(uVal*100)/100.0;
					vVal=(int)(vVal*100)/100.0;
					drU.SetValue(c, r, uVal);
					drV.SetValue(c, r, vVal);
					i++;
				}
			}
		}
		srcMapU.clear();
		srcMapV.clear();
		resultU.clear();
		resultV.clear();
		CommonFun.CloseDS(ws);
		LogTool.logger.debug("处理UV完成!");
		return true;
	}
	/**
	 * @throws Exception 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月9日
	 * @RETURN:
	 * @PARAM:ws-工作空间,calStart-开始日期，calEnd-结束日期，p1-周期1，p2-周期2
	 * @DESCRIPTION:处理OLR
	 */
	public void ProcessOLR(Workspace ws,Calendar calStart,Calendar calEnd,int p1,int p2) throws Exception{
		LogTool.logger.debug("开始处理OLR");
		LinkedHashMap<String,DatasetRaster> result=new LinkedHashMap<>();//新建一个Map，用来存放U结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcOLRMap=new LinkedHashMap<>();//新建一个Map，用来存放U原数据时间与DataRaster
		int level=1000;//固定1000s
		/*打开OLR文件*/
		String dic=ComfigureUtil.config.getOlrPath();
		String file=dic+"olr.day.mean.nc";
		File fi=new File(file);
		if(!fi.exists()){
			LogTool.logger.error(file+"文件不存在!");
			return;
		}
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"olr\",\"Server\":\"%s\"}", file);
		Datasource ds=ws.OpenDatasource(strJson);
		DatasetRaster firstSRC=(DatasetRaster) ds.GetDataset(0);
		String srcMeta=firstSRC.GetMetadata();
		JSONObject srcJson = new JSONObject(srcMeta);
		Double offset=srcJson.getDouble("add_offset");
		Double scale=srcJson.getDouble("scale_factor");
		GetOLRData(ds, srcOLRMap, calStart, calEnd);//得到MapU
		if(srcOLRMap.keySet().size()<1){
			LogTool.logger.error("ProcessOLR(),OLR数据为空!");
			return;
		}
		Boolean createResult=CreateTifs(ws,calStart, calEnd,result,"olr",level,p1,p2,"","");//此处创建tif文件，判断是否有原数据，没有则不创建
		if(!createResult){//已经处理了
			return;
		}
		DatasetRaster drFirst=(DatasetRaster) result.values().toArray()[0];//得到第一个目标数据，作为参考标准
		int width=drFirst.GetWidth();
		int height=drFirst.GetHeight();
		for(int r=0;r<height;r++){
			for(int c=0;c<width;c++){
				List<Double> lsData=new ArrayList<>();
				Point2D ptLL=drFirst.CellToPoint(new Point2D.Double(c,r));
				for(String key:srcOLRMap.keySet()){
					DatasetRaster dr=srcOLRMap.get(key);
					Point2D ptXY=dr.PointToCell(ptLL);
					Double val=dr.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					val=val*scale+offset;
					lsData.add(val);
				}
				double[] bwDatas = Butterworth(lsData, p1, p2);
				//写入目标格点
				int i=0;
				for(String key:srcOLRMap.keySet()){
					double val=bwDatas[i];
					val=(int)(val*100)/100.0;
					DatasetRaster dr=result.get(key);
					dr.SetValue(c, r, val);
					i++;
				}
			}
		}
		CommonFun.CloseDS(ws);
		LogTool.logger.error("处理OLR完成!");
	}
	/**
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年11月10日
	 * @RETURN:区域所有站点的平均实况
	 * @PARAM:ws-工作空间,gr-预报区域，calStart-开始日期，calEnd-结束日期
	 * @DESCRIPTION:获取区域平均实况
	 */
	public List<Double> GetAreaAvgLive(Workspace ws,GeoRegion gr,Calendar calStart,Calendar calEnd){
		//获取所有站点信息--start
		StationUtil stationUtil=new StationUtil();
		List<Station> lsStation=stationUtil.GetAllStation();
		//获取所有站点信息--end
		//获取需要站点--start
		GeoRel geoRel=new GeoRel();
		int stationCount=lsStation.size();
		List<Station> lsFindStation=new ArrayList<Station>();//找到的站点
		for(int j=0;j<stationCount;j++)
		{
			Station station=lsStation.get(j);
			Double lon=station.getLongitude();
			Double lat=station.getLatitude();
			GeoPoint gp=new GeoPoint(lon,lat);
			Boolean b=geoRel.Contain(ws, gr, gp);
			if(b)
			{
				lsFindStation.add(station);
			}
		}
		//获取需要站点--end
		//根据区域内的站点，求区域平均--start
		StationUtil su=new StationUtil();
		List<Double> lsData=su.CalAreaAvg(lsFindStation, calStart, calEnd);
		//根据区域内的站点，求区域平均--end
		return lsData;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年4月6日
	 * @修改日期:2017年4月6日
	 * @参数:
	 * @返回:
	 * @说明:获取所有站点实况
	 */
	public Map<String,List<Double>> GetAllStationLive(Workspace ws,GeoRegion gr,Calendar calStart,Calendar calEnd){
		Map<String,List<Double>> result=new HashMap();
		//获取所有站点信息--start
		StationUtil stationUtil=new StationUtil();
		List<Station> lsStation=stationUtil.GetAllStation();
		//获取所有站点信息--end
		//获取需要站点--start
		GeoRel geoRel=new GeoRel();
		int stationCount=lsStation.size();
		List<Station> lsFindStation=new ArrayList<Station>();//找到的站点
		for(int j=0;j<stationCount;j++)
		{
			Station station=lsStation.get(j);
			Double lon=station.getLongitude();
			Double lat=station.getLatitude();
			GeoPoint gp=new GeoPoint(lon,lat);
			Boolean b=geoRel.Contain(ws, gr, gp);
			if(b)
			{
				lsFindStation.add(station);
			}
		}
		//获取需要站点--end
		//根据区域内的站点，求区域平均--start
		StationUtil su=new StationUtil();
		int findSize=lsFindStation.size();
		for(int i=0;i<findSize;i++){
			Station station=lsFindStation.get(i);
			List<Double> tempData=su.getStationLive(station, calStart, calEnd);
			String stationInfo=station.getStationName()+","+station.getStationNum();
			result.put(stationInfo, tempData);
		}
		//根据区域内的站点，求区域平均--end
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年3月24日
	 * @修改日期:2017年3月24日
	 * @参数:
	 * @返回:
	 * @说明:处理derf风场
	 */
	public void ProcessDerfUV(Workspace ws,Calendar calStart,Calendar calEnd,int level,int p1,int p2,String tempDir,String derfUVDir){
		String fileFormatU="startdate01.atm.U.startdate01-enddate30_prslevel_member.nc";
		String fileFormatV="startdate01.atm.V.startdate01-enddate30_prslevel_member.nc";
		String strStartDate=DateFormat.yyyyMM.format(calStart.getTime());
		String strEndDate=DateFormat.yyyyMM.format(calEnd.getTime());
		String strLevel=(String) (level<1000?"0"+level:level);
		fileFormatU=fileFormatU.replace("startdate", strStartDate);
		fileFormatU=fileFormatU.replace("enddate", strEndDate);
		fileFormatU=fileFormatU.replace("level", strLevel);
		fileFormatV=fileFormatV.replace("startdate", strStartDate);
		fileFormatV=fileFormatV.replace("enddate", strEndDate);
		fileFormatV=fileFormatV.replace("level", strLevel);
		String fileDerfU = derfUVDir + fileFormatU;
		String fileDerfV = derfUVDir + fileFormatV;
		File file=new File(fileDerfU);
		if(!file.exists()){
			System.out.println(fileDerfU+"不存在！");
			return;
		}
		file=new File(fileDerfV);
		if(!file.exists()){
			System.out.println(fileDerfV+"不存在！");
			return;
		}
		LinkedHashMap<String,DatasetRaster> resultU=new LinkedHashMap<>();//新建一个Map，用来存放U结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> resultV=new LinkedHashMap<>();//新建一个Map，用来存放V结果时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcU=new LinkedHashMap<>();//新建一个Map，用来存放U原数据时间与DataRaster
		LinkedHashMap<String,DatasetRaster> srcV=new LinkedHashMap<>();//新建一个Map，用来存放V原数据时间与DataRaster
		//创建临时预报tif数据
		Boolean createResult=true;
		Boolean b = CreateTifs(ws,calStart, calEnd,resultU,"derf",level,p1,p2,"u",tempDir);
		createResult = createResult||b;
		b = CreateTifs(ws,calStart, calEnd,resultV,"derf",level,p1,p2,"v",tempDir);
		createResult = createResult||b;
		if(!createResult){//已经处理了
			LogTool.logger.debug("UV已处理过!");
			return;
		}
		//打开数据源
		String aliasU = "derfu";
		String aliasV = "derfv";
		Datasource dsU=null;
		Datasource dsV=null;
		try{
			String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", aliasU,fileDerfU);
			dsU=ws.OpenDatasource(strJson);
			strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", aliasV,fileDerfV);
			dsV=ws.OpenDatasource(strJson);
		}
		catch(Exception ex){
			LogTool.logger.error("打开derf数据源失败!");
			return;
		}
		Calendar calTemp=(Calendar) calStart.clone();//临时日期
		calEnd.add(Calendar.DATE, 1);
		int dataIndex=0;
		while(calTemp.compareTo(calEnd) < 0){
			String strDate = DateFormat.MMdd.format(calTemp.getTime());
			DatasetRaster drU=(DatasetRaster) dsU.GetDataset(dataIndex);
			DatasetRaster drV=(DatasetRaster) dsV.GetDataset(dataIndex);
			srcU.put(strDate, drU);
			srcV.put(strDate, drV);
			calTemp.add(Calendar.DATE, 1);
			dataIndex +=24;
		}
		DatasetRaster drFirst = (DatasetRaster) resultU.values().toArray()[0];//得到第一个目标数据，作为参考标准
		int width=drFirst.GetWidth();
		int height=drFirst.GetHeight();
		for(int r=0;r<height;r++){
			for(int c=0;c<width;c++){
				List<Double> lsUData=new ArrayList<>();
				List<Double> lsVData=new ArrayList<>();
				Point2D ptLL=drFirst.CellToPoint(new Point2D.Double(c,r));
				for(String key:srcU.keySet()){
					DatasetRaster drU=srcU.get(key);
					DatasetRaster drV=srcV.get(key);
					Point2D ptXY=drU.PointToCell(ptLL);
					Double valU=drU.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					Double valV=drV.GetValue((int)ptXY.getX(), (int)ptXY.getY());
					lsUData.add(valU);
					lsVData.add(valV);
				}
				double[] bwUDatas = Butterworth(lsUData, p1, p2);
				double[] bwVDatas = Butterworth(lsVData, p1, p2);
				//写入目标格点
				int i=0;
				for(String key:srcU.keySet()){
					double uVal=bwUDatas[i];
					double vVal=bwVDatas[i];
					DatasetRaster drU = resultU.get(key);
					DatasetRaster drV = resultV.get(key);
					uVal=(int)(uVal*100)/100.0;
					vVal=(int)(vVal*100)/100.0;
					drU.SetValue(c, r, uVal);
					drV.SetValue(c, r, vVal);
					i++;
				}
			}
		}
		//关闭数据源
		for(String key:resultU.keySet()){
			String alias = resultU.get(key).GetDatasource().GetAlias();
			ws.CloseDatasource(alias);
		}
		for(String key:resultV.keySet()){
			String alias = resultV.get(key).GetDatasource().GetAlias();
			ws.CloseDatasource(alias);
		}
		ws.CloseDatasource(aliasU);
		ws.CloseDatasource(aliasV);
		LogTool.logger.debug("DerfUV处理完成!");
	}
	public List<Double> ReadDerfUV(Calendar calStart,Calendar calEnd,int level,int p1,int p2){
		String tempPath=System.getProperty("java.io.tmpdir")+"efs/";
		String strStartDate=DateFormat.MMdd.format(calStart.getTime());
		Calendar tempCalEnd=(Calendar) calEnd.clone();
		tempCalEnd.add(Calendar.DATE, -1);
		String strEndDate=DateFormat.MMdd.format(tempCalEnd.getTime());
		Calendar calCur=(Calendar) calStart.clone();
		List<Double> lsDate=new ArrayList<>();
		while(calCur.compareTo(calEnd)<0){
			String strCurDate=DateFormat.MMdd.format(calCur.getTime());
			String filename="derf_"+strStartDate + "_" + strEndDate+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2;
			String file=tempPath+filename;
			file=file.replace("\\", "/");
			File fi=new File(file);
			if(!fi.exists()){
				return null;
			}
			calCur.add(Calendar.DATE, 1);
		}
		return lsDate;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年4月25日
	 * @修改日期:2017年4月25日
	 * @参数:lsStation-所有站点，calStart-开始日期，calEnd-结束日期，level-层次，p1-滤波1，p2-滤波2
	 * @返回:站点的derfuv
	 * @说明:获取站点的derfuv
	 */
	public Map<Station,List<Double>> getStationDerfUV(Workspace ws,List<Station> lsStation,Calendar calStart,Calendar calEnd,String level,int p1,int p2){
		Map<Station,List<Double>> result=new HashMap();
		String tempPath=System.getProperty("java.io.tmpdir")+"efs/";
		String strStartDate=DateFormat.MMdd.format(calStart.getTime());
		String strEndDate=DateFormat.MMdd.format(calEnd.getTime());
		Calendar curCal=(Calendar) calEnd.clone();
		int stationSize=lsStation.size();
		while(curCal.compareTo(calEnd)<1){
			String strCurDate=DateFormat.MMdd.format(curCal.getTime());
			String filenameU="derf_"+strStartDate + "_" + strEndDate+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2+"_u";
			String filenameV="derf_"+strStartDate + "_" + strEndDate+"_"+strCurDate+"_"+level+"_"+p1+"_"+p2+"_v";
			String uAlias="derfU";
			String vAlias="derfV";
			String strJsonU = "{\"Type\":\"GTiff\",\"Alias\":\"" + uAlias+ "\",\"Server\":\"" + filenameU + "\"}";
			String strJsonV = "{\"Type\":\"GTiff\",\"Alias\":\"" + vAlias+ "\",\"Server\":\"" + filenameV + "\"}";
			Datasource dsU=ws.OpenDatasource(strJsonU);
			Datasource dsV=ws.OpenDatasource(strJsonV);
			DatasetRaster datasetU=(DatasetRaster) dsU.GetDataset(0);
			DatasetRaster datasetV=(DatasetRaster) dsV.GetDataset(0);
			for(int i=0;i<stationSize;i++){
				List<Double> lsData=new ArrayList();
				Station station=lsStation.get(i);
				String stationNum=station.getStationNum();
				String stationName=station.getStationName();
				String stationInfo=stationName+"("+stationNum+")";
				double lon=station.getLongitude();
				double lat=station.getLatitude();
				Point2D cell=datasetU.PointToCell(new Point2D.Double(lon,lat));
				double valU=datasetU.GetValue((int)cell.getX(), (int)cell.getY());
				double valV=datasetV.GetValue((int)cell.getX(), (int)cell.getY());
				lsData.add(valU);
				lsData.add(valV);
				result.put(station,lsData);
			}
			curCal.add(Calendar.DATE, 1);
		}
		return result;
	}
}
