package com.spd.qhyc.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spd.qhyc.model.MonthData;
import com.mg.objects.*;

public class CommonUtil {
	static Logger logger = LogManager.getLogger("mylog");
	/**
	 * @作者:杠上花
	 * @日期:2017年12月24日
	 * @修改日期:2017年12月24日
	 * @参数:
	 * @返回:
	 * @说明:判断是否是数字
	 */
	public static boolean isNumeric(char c){
		Boolean result = false;
		if (Character.isDigit(c)){
			result = true;
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月24日
	 * @修改日期:2017年12月24日
	 * @参数:
	 * @返回:
	 * @说明:格点加法
	 */
	public double[][] TwoGridAdd(DatasetRaster dr1,DatasetRaster dr2) {
		int cols1 = dr1.GetWidth();
		int rows1 = dr1.GetHeight();
		int cols2 = dr2.GetWidth();
		int rows2 = dr2.GetHeight();
		if(cols1 != cols1||rows1!=rows2) {
			logger.error("格点行列数据不相等，不能相加!");
			return null;
		}
		Scanline sl1 = new Scanline(dr1.GetValueType(),cols1);
		Scanline sl2 = new Scanline(dr2.GetValueType(),cols2);
		double[][] result = new double[rows1][cols1];
		for(int r=0;r<rows1;r++) {
			dr1.GetScanline(0, r, sl1);
			dr2.GetScanline(0, r, sl2);
			for(int c=0;c<cols1;c++) {
				double val1 = sl1.GetValue(c);
				double val2 = sl2.GetValue(c);
				double avg = (val1+val2)/2;
				result[r][c] = avg;
			}
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月24日
	 * @修改日期:2017年12月24日
	 * @参数:
	 * @返回:
	 * @说明:数组加格点
	 */
	public void ArrayAddGrid(double[][] arr,DatasetRaster dr) {
		int arrRows = arr.length;
		int arrCols = arr[0].length;
		int drCols = dr.GetWidth();
		int drRows = dr.GetHeight();
		if(arrRows != drRows||arrCols!=drCols) {
			logger.error("格点行列数据不相等，不能相加!");
			return;
		}
		Scanline sl = new Scanline(dr.GetValueType(),drCols);
		for(int r=0;r<drRows;r++) {
			dr.GetScanline(0, r, sl);
			for(int c=0;c<drCols;c++) {
				double val1 = arr[r][c];
				double val2 = sl.GetValue(c);
				double avg = (val1+val2)/2;
				arr[r][c] = avg;
			}
		}
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月24日
	 * @修改日期:2017年12月24日
	 * @参数:
	 * @返回:
	 * @说明:数组加格点
	 */
	public double[][] ConvertGridToArray(DatasetRaster dr) {
		int cols = dr.GetWidth();
		int rows = dr.GetHeight();
		Scanline sl = new Scanline(dr.GetValueType(),cols);
		double[][] result = new double[rows][cols];
		for(int r=0;r<rows;r++) {
			dr.GetScanline(0, r, sl);
			for(int c=0;c<cols;c++) {
				double val = sl.GetValue(c);
				result[r][c] = val;
			}
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月28日
	 * @修改日期:2017年12月28日
	 * @参数:
	 * @返回:
	 * @说明:ps检验，计算N0
	 */
	public int getN0(Map<String,Double> mapObv,Map<String,Double> mapFor){
		int noCount = 0;
		for(String strNum:mapFor.keySet()){
			double forVal = mapFor.get(strNum);
			double obvVal = mapObv.get(strNum);
			//同号判断
			double result = forVal*obvVal;
			if(result<0){//异号
				
			}
			else{//同号
				noCount++;
			}
		}
		return noCount;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月28日
	 * @修改日期:2017年12月28日
	 * @参数:
	 * @返回:
	 * @说明:ps检验，计算N1
	 */
	public int getN1(Map<String,Double> mapObv,Map<String,Double> mapFor,String elementID){
		int n1Count = 0;
		if(elementID.equals("prec")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(Math.abs(cha)>=20&&Math.abs(cha)<50){
					n1Count++;
				}
			}
		}
		else if(elementID.equals("temp")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(Math.abs(cha)>=1&&Math.abs(cha)<2){
					n1Count++;
				}
			}
		}
		return n1Count;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月28日
	 * @修改日期:2017年12月28日
	 * @参数:
	 * @返回:
	 * @说明:ps检验，计算N2
	 */
	public int getN2(Map<String,Double> mapObv,Map<String,Double> mapFor,String elementID){
		int n2Count = 0;
		if(elementID.equals("prec")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(Math.abs(cha)>=50){
					n2Count++;
				}
			}
		}
		else if(elementID.equals("temp")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(Math.abs(cha)>=2){
					n2Count++;
				}
			}
		}
		return n2Count;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月28日
	 * @修改日期:2017年12月28日
	 * @参数:
	 * @返回:
	 * @说明:ps检验，计算M
	 */
	public int getM(Map<String,Double> mapObv,Map<String,Double> mapFor,String elementID){
		int mCount = 0;
		if(elementID.equals("prec")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(obvVal>100||obvVal<=-100){
					if(Math.abs(cha)<50){
						mCount++;
					}
				}
			}
		}
		else if(elementID.equals("temp")){
			for(String strNum:mapFor.keySet()){
				double forVal = mapFor.get(strNum);
				double obvVal = mapObv.get(strNum);
				double cha = forVal - obvVal;
				if(Math.abs(obvVal)>=3){
					if(Math.abs(cha)<2){
						mCount++;
					}
				}
			}
		}
		return mCount;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月25日
	 * @修改日期:2017年12月25日
	 * @参数:
	 * @返回:
	 * @说明:生成tif文件
	 */
	public void makeTif(Workspace ws,String outputFile,double[][] data,Rectangle2D r2d,double noVal){
		String targetProj = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
		File file = new File(outputFile);
		if(file.exists()){
			file.delete();
		}
		String fileName = file.getName();
		String strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + fileName+ "\",\"Server\":\"" + outputFile + "\",\"ReadOnly\":\"false\"}";
		Datasource ds = ws.CreateDatasource(strJson);
		int rows = data.length;
		int cols = data[0].length;
		strJson = String.format("{\"Name\":\"RTif\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d}", cols, rows);
		DatasetRaster dr = ds.CreateDatasetRaster(strJson);
		dr.Open();
		dr.SetProjection(targetProj);
		dr.SetNoDataValue(noVal);
		dr.SetBounds(r2d);
		Scanline sl= new Scanline(dr.GetValueType(), cols);
		for(int r=0;r<rows;r++){
        	dr.GetScanline(0, r, sl);
        	for(int c=0;c<cols;c++){
        		double val = data[r][c];
        		sl.SetValue(c, val);
        	}
        	dr.SetScanline(0, r, sl);
        }
        sl.Destroy();
        dr.FlushCache(); 
        dr.CalcExtreme();
        ws.CloseDatasource(fileName);
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
	/**
	 * @作者:杠上花
	 * @日期:2018年1月16日
	 * @修改日期:2018年1月16日
	 * @参数:
	 * @返回:
	 * @说明:栅格裁剪
	 */
	public static void GridClip(Workspace ws,String srcDSName,String srcDGName,String outDSName,String outDGName){
		Analyst pAnalystRasterClip = Analyst.CreateInstance("RasterClip", ws);
		String strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", srcDSName, srcDGName);
		pAnalystRasterClip.SetPropertyValue("Input", strJson);
		strJson = "{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClip\",\"Server\":\"E:/Map/China/T_CLIP.shp\"}";
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
	 * @日期:2018年1月23日
	 * @修改日期:2018年1月23日
	 * @参数:
	 * @返回:
	 * @说明:清空数据源中数据集
	 */
	public static void clearDS(Datasource ds) {
		int size = ds.GetDatasetCount();
		for(int i=size-1;i>=0;i--) {
			String name = ds.GetDataset(i).GetName();
			ds.DeleteDataset(name);
		}
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月21日
	 * @修改日期:2018年1月21日
	 * @参数:
	 * @返回:
	 * @说明:数组包含
	 */
	public static Boolean ArrayIsContain(String[] strs,String str){
		Boolean result = false;
		int size = strs.length;
		for(int i=0;i<size;i++){
			if(strs[i].equals(str)){
				result = true;
				break;
			}
		}
		return result;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月28日
	 * @修改日期:2018年1月28日
	 * @参数:
	 * @返回:
	 * @说明:计算平均值
	 */
	public static double calAvg(Map<String,Double> mapData) {
		double sum = 0;
		int size = mapData.size();
		for(String str:mapData.keySet()) {
			sum += mapData.get(str);
		}
		double avg = sum/size;
		return avg;
	}
}
