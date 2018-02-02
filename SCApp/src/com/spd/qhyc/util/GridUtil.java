package com.spd.qhyc.util;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.mg.objects.Analyst;
import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

/**
 * @作者:杠上花
 * @日期:2018年1月17日
 * @公司:spd
 * @说明:
*/
public class GridUtil {
	static Logger logger = LogManager.getLogger("mylog");
	/**
	 * @作者:杠上花
	 * @日期:2018年1月17日
	 * @修改日期:2018年1月17日
	 * @参数:
	 * @返回:
	 * @说明:计算二个格点
	 */
	public void calRaster(Workspace ws,DatasetRaster dr1,DatasetRaster dr2,String outputDS,String outputDG,String exp){
		Analyst pAnalyst = Analyst.CreateInstance("RasterCalc", ws);
		pAnalyst.SetPropertyValue("Expression", exp);
		//设置输入数据
		String strJson = "{\"Datasource\":\"" + dr1.GetDatasource().GetAlias() + "\",\"Dataset\":\"" + dr1.GetName() + "\"}";
        pAnalyst.SetPropertyValue("a", strJson);
        strJson = "{\"Datasource\":\"" + dr2.GetDatasource().GetAlias() + "\",\"Dataset\":\"" + dr2.GetName() + "\"}";
        pAnalyst.SetPropertyValue("b", strJson);
        
        strJson = "{\"Datasource\":\""+outputDS+"\",\"Dataset\":\""+outputDG+"\"}";
        pAnalyst.SetPropertyValue("Output", strJson);
        pAnalyst.Execute();
        pAnalyst.Destroy();
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月16日
	 * @修改日期:2018年1月16日
	 * @参数:
	 * @返回:
	 * @说明:栅格裁剪
	 */
	public void GridClip(Workspace ws,String srcDSName,String srcDGName,String outDSName,String outDGName){
		Analyst pAnalystRasterClip = Analyst.CreateInstance("RasterClip", ws);
		String strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", srcDSName, srcDGName);
		pAnalystRasterClip.SetPropertyValue("Input", strJson);
		String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
		String strClipFile = classPath + "T_CLIP.shp";
		strJson = "{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClip\",\"Server\":\""+strClipFile+"\"}";
		Datasource dsClip = ws.OpenDatasource(strJson);
		Dataset dtClip = dsClip.GetDataset(0);
		strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsClip.GetAlias(), dtClip.GetName());
		pAnalystRasterClip.SetPropertyValue("ClipRegion", strJson);
		strJson = String.format("{\"Type\":\"Memory\",\"Alias\":\"%s\",\"Server\":\"\"}", outDSName);
		Datasource dsRasterClip = ws.CreateDatasource(strJson);
		strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClip.GetAlias(), outDGName);
		pAnalystRasterClip.SetPropertyValue("Output", strJson);
        pAnalystRasterClip.Execute();
        pAnalystRasterClip.Destroy();
        ws.CloseDatasource(dsClip.GetAlias());//关掉裁剪失量数据
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月25日
	 * @修改日期:2018年1月25日
	 * @参数:
	 * @返回:
	 * @说明:获取历史月观测数据
	 */
	public double[][] getMonthHosObvStationFromGrid(Workspace ws,int month,Config config,String elementID,List<XNStation> lsXNStation){
		String strFile = "";
		strFile = elementID.equals("Temp")?config.getHosTempFile():config.getHosPrecFile();
		File filePath = new File(strFile);
		if(!filePath.exists()){
			logger.error("不存在文件："+strFile);
			return null;
		}
		int stationCount = lsXNStation.size();
		double[][] result = new double[stationCount][30];
		
		String strJson = "{\"Type\":\"netCDF\",\"Alias\":\"mode\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		for(int i=0;i<30;i++){
			int index = i*12+month-1;
			DatasetRaster dr = (DatasetRaster) ds.GetDataset(index);
			dr.CalcExtreme();
			for(int j=0;j<stationCount;j++){
				XNStation xnStation = lsXNStation.get(j);
				double lon = xnStation.getLon();
				double lat = xnStation.getLat();
				Point2D p2d=new Point2D.Double(lon,lat);
				Point2D cell=dr.PointToCell(p2d);
				int x=(int) cell.getX();
				int y=(int) cell.getY();
				double val=dr.GetValue(x, y);//此格点值
				if(val>1000){
					System.out.println(val);
				}
				result[j][i] = val;
			}
		}
		ws.CloseDatasource("mode");
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月25日
	 * @修改日期:2018年1月25日
	 * @参数:
	 * @返回:
	 * @说明:获取历史月模式数据
	 */
	public double[][] getMonthHosModeStationFromGrid(Workspace ws,int month,Config config,List<XNStation> lsXNStation){
		int stationCount = lsXNStation.size();
		double[][] result = new double[stationCount][30];
		String strFile = config.getLiveHeightFile();
		File filePath = new File(strFile);
		if(!filePath.exists()){
			logger.error("不存在文件："+strFile);
			return null;
		}
		String strJson = "{\"Type\":\"netCDF\",\"Alias\":\"mode\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		int startYear = 1981;
		int dsCount = ds.GetDatasetCount();
		//获取开始索引
		int index = (startYear-1948)*12*17+(month-1)*17+5;
		for(int i=0;i<30;i++){
			DatasetRaster dr = (DatasetRaster) ds.GetDataset(index);
			dr.CalcExtreme();
			for(int j=0;j<stationCount;j++){
				XNStation xnStation = lsXNStation.get(j);
				double lon = xnStation.getLon();
				double lat = xnStation.getLat();
				Point2D p2d=new Point2D.Double(lon,lat);
				Point2D cell=dr.PointToCell(p2d);
				int x=(int) cell.getX();
				int y=(int) cell.getY();
				double val=dr.GetValue(x, y);//此格点值
				result[j][i] = val;
			}
			index += 12*17;
		}
		ws.CloseDatasource("mode");
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月21日
	 * @修改日期:2018年1月21日
	 * @参数:
	 * @返回:
	 * @说明:获取预报模式数据
	 */
	public double[][] getForecastModeStationData(Workspace ws,Calendar cal,int month,Config config,List<XNStation> lsXNStation){
		Calendar tempMakeDate = (Calendar) cal.clone();
		String strMakeDate1 = DateUtil.format("yyyyMM", tempMakeDate);
		String strMakeDate2 = strMakeDate1 + "01";
		tempMakeDate.add(Calendar.YEAR, 1);
		String strEndDate = DateUtil.format("yyyyMM", tempMakeDate);
		tempMakeDate.add(Calendar.YEAR, -1);//减回来
		String modeFileFormat = "%s.atm.Z3.%s-%s_prs0500_member.nc";
		String fileName = String.format(modeFileFormat, strMakeDate2,strMakeDate1,strEndDate);
		String path = config.getModeHeightPath();
		String strHgtFile = path + fileName;
		String modeAlias = "mode"+DateUtil.format("HHmmss", Calendar.getInstance());
       String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+modeAlias+"\",\"Server\":\"" + strHgtFile + "\"}";
       Datasource ds = ws.OpenDatasource(strJson);
       int stationCount = lsXNStation.size();
       double[][] result = new double[stationCount][1];
       int dsCount = ds.GetDatasetCount();
       int makeMonth = tempMakeDate.get(Calendar.MONTH)+1;
       int dsIndex = month>makeMonth?month-makeMonth:month+12-makeMonth;
       DatasetRaster dr = (DatasetRaster) ds.GetDataset(dsIndex);
       dr.CalcExtreme();
       for(int j=0;j<stationCount;j++){
			XNStation xnStation = lsXNStation.get(j);
			double lon = xnStation.getLon();
			double lat = xnStation.getLat();
			Point2D p2d=new Point2D.Double(lon,lat);
			Point2D cell=dr.PointToCell(p2d);
			int x=(int) cell.getX();
			int y=(int) cell.getY();
			double val=dr.GetValue(x, y);//此格点值
			result[j][0] = val;
		}
       ws.CloseDatasource(modeAlias);
       return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月25日
	 * @修改日期:2018年1月25日
	 * @参数:
	 * @返回:
	 * @说明:获取历史月平均
	 */
	public double[] getHosMonthAvg(Workspace ws,int month,Config config,List<XNStation> lsXNStation,String elementID){
		String path  = "";
		if(elementID.toLowerCase().equals("temp")){
			path = config.getHosMonthTempAvgPath();
		}
		else{
			path = config.getHosMonthPrecAvgPath();
		}
		String strFile = path + month +".tif";
		File file = new File(strFile);
		if(!file.exists()){
			logger.error("不存在文件："+strFile);
			return null;
		}
		Calendar cal = Calendar.getInstance();
		String alias = "mode"+DateUtil.format("hhmmss", cal);
		String strJson = "{\"Type\":\"Gtiff\",\"Alias\":\""+alias+"\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
		dr.CalcExtreme();
		double noVal = dr.GetNoDataValue();
		int stationCount = lsXNStation.size();
		double[] result = new double[stationCount];
		for(int i=0;i<stationCount;i++){
			XNStation xnStation = lsXNStation.get(i);
			double lon = xnStation.getLon();
			double lat = xnStation.getLat();
			Point2D p2d=new Point2D.Double(lon,lat);
			Point2D cell=dr.PointToCell(p2d);
			int x=(int) cell.getX();
			int y=(int) cell.getY();
			double val=dr.GetValue(x, y);//此格点值
			result[i] = val;
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月13日
	 * @修改日期:2018年1月13日
	 * @参数:
	 * @返回:
	 * @说明:同化
	 */
	public void TongHua(Workspace ws,String srcDSName,String srcDGName,Double resolutionX,Double resolutionY,String outDSName,String outDGName){
		Analyst pAnalystResample = Analyst.CreateInstance("Resample", ws);
		String strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", srcDSName, srcDGName);
		pAnalystResample.SetPropertyValue("Input", strJson);
		pAnalystResample.SetPropertyValue("OutputCellSize", String.format("%s %s", resolutionX, resolutionY));
		pAnalystResample.SetPropertyValue("ResamplingType", "Bilinear");
		strJson = String.format("{\"Type\":\"Memory\",\"Alias\":\"%s\",\"Server\":\"\"}", outDSName);
		Datasource dsResample = ws.CreateDatasource(strJson);
		strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", outDSName, outDGName);
		pAnalystResample.SetPropertyValue("Output", strJson);
		pAnalystResample.Execute();
        pAnalystResample.Destroy();
	}
}
