package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;

import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;
import com.spd.qhyc.application.WorkspaceHelper;
import com.spd.qhyc.util.CommonUtil;

public class HosMonthProcess {

	public static void main(String[] args) {
		Workspace ws = WorkspaceHelper.getWorkspace();
		String strFile = "E:/SC/Data/Mode/Hos/monthPrec.nc";
		String elementID = "temp";
		String strJson = "{\"Type\":\"netCDF\",\"Alias\":\"mode\",\"Server\":\"" + strFile + "\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		int dsCount = ds.GetDatasetCount();
		CommonUtil commonUtil = new CommonUtil();
		double noVal = -99;
		for(int m=0;m<12;m++){
			double[][] newData = null;
			Rectangle2D r2d = null; 
			for(int y=0;y<30;y++){
				int index = y*12+m;	
				DatasetRaster dr = (DatasetRaster) ds.GetDataset(index);
				dr.CalcExtreme();
				r2d = dr.GetBounds();
				double[][] curData = commonUtil.ConvertGridToArray(dr);
				if(y==0){
					newData = curData;
				}
				else{
					int rows = newData.length;
					int cols = newData[0].length;
					for(int r=0;r<rows;r++){
						for(int c=0;c<cols;c++){
							double curVal = curData[r][c];
							double oldVal = newData[r][c];
							if(curVal==noVal||oldVal==noVal){
								newData[r][c] = noVal;
							}
							else{
								double avg = (curVal+oldVal)/2;
								newData[r][c] = avg;
							}
						}
					}
				}
			}
			String outputFile = "E:/SC/Data/Mode/Hos/precAvg/month/"+(m+1)+".tif";
			commonUtil.makeTif(ws,outputFile,newData,r2d,noVal);
		}
		ws.CloseDatasource("mode");
		ws.Destroy();
		System.out.println("over");
	}

}
