package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

//import com.mg.objects.*;
import com.spd.qhyc.util.CommonUtil;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

/**
 * @作者:杠上花
 * @日期:2018年1月8日
 * @公司:spd
 * @说明:
*/
public class NCToTiff {

	public static void main(String[] args) throws Exception {
		String strFile = "E:/SC/Data/Height/hgt.mon.mean.nc";
		Workspace ws = new Workspace();
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"model\",\"Server\":\"%s\"}", strFile);
		Datasource ds=ws.OpenDatasource(strJson);
		int drCount = ds.GetDatasetCount();
		System.out.println(drCount);
		CommonUtil cu = new CommonUtil();
		JSONObject json=null;
		int targetLevel = 500;
		int targetMonth = 11;
		int year = 1948;
		int month = 1;
		Rectangle2D r2d = null;
		//创建临时数据源
		strJson = "{\"Type\":\"Memory\",\"Alias\":\"temp\",\"Server\":\"\"}";
		Datasource dsTemp = ws.CreateDatasource(strJson);
		
		CommonUtil commonUtil = new CommonUtil();
		List<double[][]> lsData = new ArrayList();
		double noVal = 0;
		for(int i=0;i<drCount;i++){
			DatasetRaster dr = (DatasetRaster) ds.GetDataset(i);
			noVal = dr.GetNoDataValue();
			String strMeta = dr.GetMetadata();
			json = new JSONObject(strMeta);
			int level=json.getInt("NETCDF_DIM_level");
			if(year>1980){
				if(targetLevel==level){
					if(targetMonth==month){
						commonUtil.GridClip(ws, ds.GetAlias(), dr.GetName(), dsTemp.GetAlias(), "clipDG");
						commonUtil.TongHua(ws, dsTemp.GetAlias(), "clipDG", 0.5, 0.5, dsTemp.GetAlias(), "THDG");
						DatasetRaster newDR = (DatasetRaster) dsTemp.GetDataset("THDG");
						r2d = newDR.GetBounds();
						double[][] tempData = cu.ConvertGridToArray(newDR);
						Boolean b = dsTemp.DeleteDataset("clipDG");
						b = dsTemp.DeleteDataset("THDG");
						lsData.add(tempData);
					}
				}
			}
			if(level ==10){//最小
				month++;
			}
			if(month>12){
				month=1;
				year++;
			}
			if(year>2010){
				break;
			}
		}
		//统计
		double[][] sumData = lsData.get(0);
		int size = lsData.size();
		for(int i=1;i<size;i++){
			double[][] tempData = lsData.get(i);
			int rows = tempData.length;
			int cols = tempData[0].length;
			for(int r=0;r<rows;r++){
				for(int c=0;c<cols;c++){
					double newVal = tempData[r][c];
					double oldVal = sumData[r][c];
					double sum = newVal+oldVal;
					sumData[r][c] = sum;
				}
			}
		}
		//取平均
		int rows = sumData.length;
		int cols = sumData[0].length;
		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				double val = sumData[r][c];
				val = val/size;
				val = (int)(val*10)/10.0;
				sumData[r][c] = val;
			}
		}
		String outputFile = "E:/SC/Data/Mode/HeightAvg/"+targetMonth+".tif";
		commonUtil.makeTif(ws,outputFile,sumData,r2d,noVal);
		System.out.println("完成!");
	}

}
