package com.spd.grid.tool;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.grid.config.ConfigHelper;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:数据处理
 */
public class DataDealUtil {
	/**
	 * @作者:wangkun
	 * @日期:2016年12月12日
	 * @修改日期:2016年12月12日
	 * @参数:
	 * @返回:
	 * @说明:高度场平均
	 */
	public void HgtAvg(){
		LogTool.logger.error("高度场平均处理!--开始");
		Workspace ws=new Workspace();
		Calendar calStart=Calendar.getInstance();
		Calendar calEnd=Calendar.getInstance();
		calStart.set(1981, 0, 1);
		calEnd.set(2010, 12, 31);
		String alias="hgtavg";//生成一个tif文件
		//打开月高度场
		String monFile="E:/SC/EFS/data/hgt.mon.mean.nc";
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"hgt\",\"Server\":\"%s\"}", monFile);
		Datasource ds=ws.OpenDatasource(strJson);
		int dsCount=ds.GetDatasetCount();
		String str="";
		JSONObject json=null;
		int time=0;
		int level=0;
		int needLevel=500;
		Calendar calInit=Calendar.getInstance();
		Map<Integer,List<Integer>> mapDataIndex=new HashMap<>();
		for(int i=1;i<=12;i++){
			List<Integer> lsDataIndex=new ArrayList<>();
			mapDataIndex.put(i, lsDataIndex);
		}
		try{
			for(int i=0;i<dsCount;i++){
				Dataset dataset= ds.GetDataset(i);
				str = dataset.GetMetadata();
				json = new JSONObject(str);
				time=json.getInt("NETCDF_DIM_time");
				level=json.getInt("NETCDF_DIM_level");
				if(level!=needLevel){
					continue;
				}
				int days=time/24;
				calInit.set(1800, 0, 1);
				calInit.add(Calendar.DATE, days);
				if(calInit.compareTo(calStart)<0||calInit.compareTo(calEnd)>0){
					continue;
				}
				int month=calInit.get(Calendar.MONTH)+1;//月份
				List<Integer> lsTemp=mapDataIndex.get(month);
				lsTemp.add(i);
			}
		}
		catch(Exception ex){
			LogTool.logger.info(ex.getMessage());
		}
		Rectangle2D r2d=null;
		for(int i=1;i<=12;i++){
			List<Integer> lsIndex=mapDataIndex.get(i);
			int indexSize=lsIndex.size();
			int rows=0;
			int cols=0;
			double[] totalVal=null;
			for(int j=0;j<indexSize;j++){
				int datasetIndex=lsIndex.get(j);
				Dataset dataset=ds.GetDataset(datasetIndex);
				DatasetRaster dr=(DatasetRaster) dataset;
				rows=dr.GetHeight();
				cols=dr.GetWidth();
				int totalGrid=rows*cols;
				if(j==0){
					r2d=dr.GetBounds();
					totalVal=new double[totalGrid];
				}
				Scanline sl=new Scanline(dr.GetValueType(),cols);
				for(int r=rows-1;r>=0;r--){
					dr.GetScanline(0, r, sl);
					for(int c=0;c<cols;c++){
						double val=sl.GetValue(c);
						int index=r*cols+c;
						totalVal[index]+=val;
					}
				}
				if(j==indexSize-1){//最后一个，求平均
					for(int s=0;s<totalGrid;s++){
						double val=totalVal[s];
						val=val/indexSize;
						totalVal[s]=val;
					}
				}
				sl.Destroy();
			}
			String file=String.format("C:/Users/lenovo/Desktop/temp/hgtavg/m%s_hgtavg.tif", i);
			File fi=new File(file);
			fi.deleteOnExit();
			strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
			Datasource dsResult=ws.CreateDatasource(strJson);
			
			strJson = String.format("{\"Name\":\"temp\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d}",cols, rows);
			DatasetRaster drResult=dsResult.CreateDatasetRaster(strJson);
			drResult.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
			drResult.SetBounds(r2d);
			Scanline sl=new Scanline(drResult.GetValueType(),cols);
			for(int r=rows-1;r>=0;r--){
				for(int c=0;c<cols;c++){
					int index=r*cols+c;
					double val=totalVal[index];
					sl.SetValue(c, val);
				}
				drResult.SetScanline(0, r, sl);
			}
			drResult.CalcExtreme();
	        drResult.FlushCache();
			sl.Destroy();
			ws.CloseDatasource(alias);
		}
		LogTool.logger.error("高度场平均处理!--结束");
	}
	/**
	 * @作者:wangkun
	 * @日期:2016年12月27日
	 * @修改日期:2016年12月27日
	 * @参数:
	 * @返回:
	 * @说明:高度场距平
	 */
	public void HgtJuPing(Calendar cal,String cidu){
		Workspace ws=new Workspace();
		//打开月高度场
		if(ConfigHelper.config==null){
			ConfigHelper configHelper = new ConfigHelper();
			configHelper.excute();
		}
		String strMonFile = ConfigHelper.config.getMonthHgtFile();
		File monFile = new File(strMonFile);
		if(!monFile.exists()){
			LogTool.logger.error("没找到高度场文件:"+monFile);
			return;
		}
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"hgt\",\"Server\":\"%s\"}", strMonFile);
		Datasource ds=ws.OpenDatasource(strJson);
		int dsCount=ds.GetDatasetCount();
		String str="";
		JSONObject json=null;
		int time=0;
		int level=0;
		int needLevel=500;
		Calendar calInit=Calendar.getInstance();
		int rows=0;
		int cols=0;
		int forcastYear=cal.get(Calendar.YEAR);
		String hgtAvgPath = ConfigHelper.config.getHgtMonthAvgPath();
		String hgtMothJPPath = ConfigHelper.config.getHgtMonthJPPath();
		try{
			for(int i=0;i<dsCount;i++){
				Dataset dataset= ds.GetDataset(i);
				str = dataset.GetMetadata();
				json = new JSONObject(str);
				time=json.getInt("NETCDF_DIM_time");
				level=json.getInt("NETCDF_DIM_level");
				
				if(level!=needLevel){
					continue;
				}
				int days=time/24;
				calInit.set(1800, 0, 1);
				calInit.add(Calendar.DATE, days);
				int year=calInit.get(Calendar.YEAR);
				if(forcastYear!=year){
					continue;
				}
				DatasetRaster dr=(DatasetRaster) dataset;
				rows=dr.GetHeight();
				cols=dr.GetWidth();
				Rectangle2D r2d=dr.GetBounds();
				
				//要的月份
				int month=calInit.get(Calendar.MONTH)+1;
				
				String file=String.format(hgtAvgPath+"m%s_hgtavg.tif", month);
				strJson = "{\"Type\":\"GTiff\",\"Alias\":\"hgtavg\",\"Server\":\"" + file + "\"}";
				Datasource dsAvg=ws.OpenDatasource(strJson);
				DatasetRaster drAvg=(DatasetRaster) dsAvg.GetDataset(0);
				
				//创建结果
				String strDate=DateFormat.yyyyMM.format(calInit.getTime());
				file=String.format(hgtMothJPPath+"%s.tif", strDate);
				File fi=new File(file);
				if(fi.exists()){
					continue;
				}
				strJson = "{\"Type\":\"GTiff\",\"Alias\":\"hgtjping\",\"Server\":\"" + file + "\"}";
				Datasource dsResult=ws.CreateDatasource(strJson);
							
				strJson = String.format("{\"Name\":\"temp\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d}",cols, rows);
				DatasetRaster drResult=dsResult.CreateDatasetRaster(strJson);
				drResult.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
				drResult.SetBounds(r2d);
				
				Scanline slResult=new Scanline(drResult.GetValueType(),cols);
				Scanline slAvg=new Scanline(drAvg.GetValueType(),cols);
				Scanline sl=new Scanline(dr.GetValueType(),cols);
				for(int r=rows-1;r>=0;r--){
					dr.GetScanline(0, r, sl);
					drAvg.GetScanline(0, r, slAvg);
					for(int c=0;c<cols;c++){
						double avgVal=slAvg.GetValue(c);
						double val=sl.GetValue(c);
						double jupingVal=val-avgVal;
						slResult.SetValue(c, jupingVal);
					}
					drResult.SetScanline(0, r, slResult);
				}
				slResult.Destroy();
				slAvg.Destroy();
				sl.Destroy();
				drResult.CalcExtreme();
		        drResult.FlushCache();
		        ws.CloseDatasource("hgtavg");
		        ws.CloseDatasource("hgtjping");
			}
		}
		catch(Exception ex){
			LogTool.logger.info(ex.getMessage());
		}
		System.out.println("");
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月9日
	 * @修改日期:2017年1月9日
	 * @参数:
	 * @返回:
	 * @说明:标准化处理
	 */
	public void StandarDeal(Workspace ws,Calendar cal){
		//打开月高度场
		String monFile="E:/SC/EFS/data/hgt.mon.mean.nc";
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"hgt\",\"Server\":\"%s\"}", monFile);
		Datasource ds=ws.OpenDatasource(strJson);
		int dsCount=ds.GetDatasetCount();
		int startYear=1981;
		int forcastYear=cal.get(Calendar.YEAR);
		int forcastStartMonth=cal.get(Calendar.MONTH)+1;
		Calendar calStart=Calendar.getInstance();
		calStart.set(Calendar.YEAR, startYear);
		calStart.set(Calendar.MONTH, 0);//1981年开始
		int needLevel=500;
		String str="";
		int time;
		int level;
		JSONObject json=null;
		int rows=0;
		int cols=0;
		Rectangle2D r2d=null;
		//取得所有需要数据的索引
		HashMap<String,Integer> hmDataIndex=new HashMap();
		try{
			for(int i=0;i<dsCount;i++){
				Calendar calTemp=Calendar.getInstance();
				Dataset dataset=ds.GetDataset(i);
				str = dataset.GetMetadata();
				json = new JSONObject(str);
				time=json.getInt("NETCDF_DIM_time");
				level=json.getInt("NETCDF_DIM_level");
				if(level!=needLevel){//不是同一层次
					continue;
				}
				int days=time/24;
				calTemp.set(1800, 0, 1);
				calTemp.add(Calendar.DATE, days);
				int year=calTemp.get(Calendar.YEAR);
				int month=calTemp.get(Calendar.MONTH)+1;
				if(year<startYear||year>forcastYear){//不在1981到预报年份之间
					continue;
				}
				if(forcastStartMonth!=month){
					continue;
				}
				String strDate=year+""+month;
				hmDataIndex.put(strDate, i);
				if(rows==0||cols==0){
					DatasetRaster dr=(DatasetRaster) dataset;
					rows=dr.GetHeight();
					cols=dr.GetWidth();
					r2d=dr.GetBounds();
				}
			}
		}
		catch(Exception ex){
			
		}
		//创建处理结果，每年一个文件
		Calendar calTemp=(Calendar) calStart.clone();
		String strEndDate=DateFormat.yyyyMM.format(cal.getTime());
		for(int y=startYear;y<=forcastYear;y++){
			String strDate=DateFormat.yyyyMM.format(calTemp.getTime());
			System.out.println(strDate+"---"+strEndDate);
			CreateStandarTif(ws,calStart,cal,calTemp,r2d,rows,cols);
			calTemp.add(Calendar.YEAR, 1);
		}
		
		List<Double> lsVal=new ArrayList<>();
		MathUtil mu=new MathUtil();
		String strForcastStartMonth=forcastStartMonth<10?"0"+forcastStartMonth:forcastStartMonth+"";
		for(int r=rows-1;r>=0;r--){
			for(int c=0;c<cols;c++){
				System.out.println("行:"+r+"列:"+c);
				lsVal.clear();
				for(int i=startYear;i<=forcastYear;i++){
					String key=i+""+forcastStartMonth;
					int index=hmDataIndex.get(key);
					DatasetRaster dr=(DatasetRaster) ds.GetDataset(index);
					dr.CalcExtreme();
					double val=dr.GetValue(c, r);
					lsVal.add(val);
				}
				//标准化处理
				/*List<Double> lsResult=mu.BiaoZhuiHua(lsVal);
				int index=0;
				for(int i=startYear;i<=forcastYear;i++){
					String key=i+""+strForcastStartMonth;
					Datasource dsResult=ws.GetDatasource(key);
					DatasetRaster drResult=(DatasetRaster) dsResult.GetDataset(0);
					drResult.Open();
					drResult.SetValue(c, r, lsResult.get(index));
					index++;
				}*/
			}
		}
		CommonFun.CloseDS(ws);
		System.out.println("数据处理完成!");
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年1月9日
	 * @修改日期:2017年1月9日
	 * @参数:
	 * @返回:
	 * @说明:创建标准化处理结果
	 */
	private void CreateStandarTif(Workspace ws,Calendar calStart,Calendar calEnd,Calendar cal,Rectangle2D r2d,int rows,int cols){
		//开始时间
		int startYear=calStart.get(Calendar.YEAR);
		int endYear=calEnd.get(Calendar.YEAR);
		int year=cal.get(Calendar.YEAR);
		int month=calEnd.get(Calendar.MONTH)+1;
		String strMonth=month<10?"0"+month:month+"";
		String path="C:/Users/lenovo/Desktop/temp/bzh/";
		String alias=year+""+strMonth;
		String fileName=startYear+"_"+endYear+"_"+alias;
		String file=path+alias+".tif";
		File fi=new File(file);
		String strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + alias+ "\",\"Server\":\"" + file + "\"}";
		if(fi.exists()){
			ws.OpenDatasource(strJson);
			return;
		}
		Datasource ds=ws.CreateDatasource(strJson);
		strJson = String.format("\"Name\":\"GTiff\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d", cols, rows);
		DatasetRaster dr = ds.CreateDatasetRaster("{" + strJson + "}");
		dr.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
        dr.SetBounds(new Rectangle2D.Double(r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight()));
        dr.FlushCache();
        //ws.CloseDatasource(alias);
        System.out.println(file+"生成完成!");
	}
}
