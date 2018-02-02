package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.spd.qhyc.application.WorkspaceHelper;
import com.spd.qhyc.util.CommonUtil;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

public class HosGridAvgDay {

	public static void main(String[] args) {
		Workspace ws = WorkspaceHelper.getWorkspace();
		String path = "E:/SC/Data/Grid/HosGrid/Temp/mean/day/";
		String fileFormat = "SURF_CLI_CHN_TEM_DAY_GRID_0.5-MEAN-date.txt";
		int startyear=1981;
		int startmonth=1;
		int startday=1;
		String strJson="";
		//1、把天的数据取出来
		for(int j=startmonth;j<13;j++){
			for(int k=startday;k<32;k++){
				String strMonth = j<10?"0"+j:j+"";
				String strDay = k<10?"0"+k:k+"";
			    List<double[][]> lsData = new ArrayList();
			    CommonUtil cu = new CommonUtil();
			    double noVal = - 1;
			    Rectangle2D r2d = null;
			    for(int i=startyear;i<2011;i++){
					String date=i+strMonth+strDay;
					String fileName = fileFormat.replace("date", date);
					String strFile = path+fileName;
					File exist_file=new File(strFile);
					if(!exist_file.exists()){
						continue;
					}
					strJson = "{\"Type\":\"AAIGrid\",\"Alias\":\"test\",\"Server\":\"" + strFile + "\"}";
					Datasource datasource = ws.OpenDatasource(strJson);
					DatasetRaster dr = (DatasetRaster) datasource.GetDataset(0);
					dr.CalcExtreme();
					r2d = dr.GetBounds();
					noVal = dr.GetNoDataValue();
					double[][] data = cu.ConvertGridToArray(dr);
					ws.CloseDatasource("test");
					lsData.add(data);	
			    }
				//求和
			    if(lsData.size()<1){
			    	continue;
			    }
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
				String name = strMonth+strDay +".tif";
		   	    String outputFile = "E:/SC/Data/Mode/TempAvg/day/"+name;
				cu.makeTif(ws,outputFile,result,r2d,noVal);
				System.out.println(name+"生成成功!");
			}
		}
	}
}
