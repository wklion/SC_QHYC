package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import com.spd.qhyc.util.CommonUtil;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

/**
 * @作者:杠上花
 * @日期:2017年11月18日
 * @公司:spd
 * @说明:
*/
public class HosGridAvg {

	public static void main(String[] args) {
		Workspace workspace = new Workspace();
		String path = "E:/SC/Data/Grid/HosGrid/Temp/mean/month/";
		String fileFormat = "SURF_CLI_CHN_TEM_MON_GRID_0.5-MEAN-date.txt";
		int targetMonth = 12;
		int startYear = 1981;
		String strJson = "";
		//1、把这个月的数据取出来
		List<double[][]> lsData = new ArrayList();
		CommonUtil cu = new CommonUtil();
		double noVal = - 1;
		Rectangle2D r2d = null;
		for(int i=startYear;i<2011;i++){
			String strMonth = targetMonth<10?"0"+targetMonth:targetMonth+"";
			String date = i+strMonth;
			String fileName = fileFormat.replace("date", date);
			String strFile = path+fileName;
			strJson = "{\"Type\":\"AAIGrid\",\"Alias\":\"test\",\"Server\":\"" + strFile + "\"}";
			Datasource datasource = workspace.OpenDatasource(strJson);
			DatasetRaster dr = (DatasetRaster) datasource.GetDataset(0);
			dr.CalcExtreme();
			r2d = dr.GetBounds();
			noVal = dr.GetNoDataValue();
			double[][] data = cu.ConvertGridToArray(dr);
			workspace.CloseDatasource("test");
			lsData.add(data);
		}
		//2、计算和
		double[][] result = lsData.get(0);
		int rows = result.length;
		int cols = result[0].length;
		int size = lsData.size();
		for(int i=1;i<size;i++){
			double[][] thisData = lsData.get(i);
			for(int r=0;r<rows;r++){
				for(int c=0;c<cols;c++){
					double oldVal = result[r][c];
					double newVal = thisData[r][c];
					double sum = -1;
					if(oldVal==noVal||newVal==noVal){
						sum = noVal;
					}
					else{
						sum = oldVal+newVal;
					}
					result[r][c] = sum;
				}
			}
		}
		//计算平均
		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				double val = result[r][c];
				if(val!=noVal){
					double avg = val/size;
					avg = (int)(avg*10)/10.0;
					result[r][c] = avg;
				}
			}
		}
		//生成tif
		String outputFile = "E:/SC/Data/Grid/HosGrid/Temp/mean/monthAvg/"+targetMonth+".tif";
		cu.makeTif(workspace,outputFile,result,r2d,noVal);
		workspace.Destroy();
		System.out.println("生成成功!");
	}

}
