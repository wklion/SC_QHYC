package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;
import java.io.File;

import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;

public class MonthGridToNC {

	public static void main(String[] args) {
		String outputFile = "C:/Users/lenovo/Desktop/temp/monthPrec.nc";
		File file = new File(outputFile);
		file.delete();
		String strJson = "{\"Type\":\"MGCnetCDF\",\"Alias\":\"TestWriteNETCDF2\",\"Server\":\""+outputFile+"\"}";
		Workspace ws = new Workspace();
		Datasource ds = ws.CreateDatasource(strJson);
		//设置bounds
		String strTestFile = "E:/SC/Data/Grid/HosGrid/Prec/month/SURF_CLI_CHN_PRE_MON_GRID_0.5-198104.txt";
		strJson = "{\"Type\":\"AAIGrid\",\"Alias\":\"grid\",\"Server\":\"" + strTestFile + "\"}";
		Datasource dsTest = ws.OpenDatasource(strJson);
		DatasetRaster drTest = (DatasetRaster) dsTest.GetDataset(0);
		drTest.CalcExtreme();
		double noVal = drTest.GetNoDataValue();
		Rectangle2D rcBounds = drTest.GetBounds();
		ws.CloseDatasource("grid");
		int w = (int)(rcBounds.getWidth() / 0.5), h = (int)(rcBounds.getHeight() / 0.5);
		
		//设全局属性
        //nc_type可取值:NC_INT,NC_FLOAT,NC_DOUBLE,NC_STRING
        ds.AddPropertyValue("att", "{\"name\":\"Conventions\",\"nc_type\":\"NC_STRING\",\"value\":\"C\"}");
        ds.AddPropertyValue("att", "{\"name\":\"writer\",\"nc_type\":\"NC_STRING\",\"value\":\"wangkun\"}");
        ds.AddPropertyValue("att", "{\"name\":\"year\",\"nc_type\":\"NC_INT\",\"value\":\"1981\"}");
        strJson = String.format("{\"name\":\"origin\",\"nc_type\":\"NC_FLOAT\",\"value\":\"%f %f\"}", rcBounds.getX(), rcBounds.getY());
        ds.AddPropertyValue("att", strJson);
        
      //定义维度
        ds.AddPropertyValue("dim", "{\"name\":\"time\",\"len\":360}");
        //ds.AddPropertyValue("dim", "{\"name\":\"level\",\"len\":1}");
        strJson = String.format("{\"name\":\"latitude\",\"len\":%d}", h);
        ds.AddPropertyValue("dim", strJson);
        strJson = String.format("{\"name\":\"longitude\",\"len\":%d}", w);
        ds.AddPropertyValue("dim", strJson);
        
      //定义变量(只能是 1维 或 从指定维度到最低维度、连续的多维)
        //ds.AddPropertyValue("var", "{\"name\":\"level\",\"nc_type\":\"NC_INT\",\"dims\":\"level\"}");
        ds.AddPropertyValue("var", "{\"name\":\"latitude\",\"nc_type\":\"NC_DOUBLE\",\"dims\":\"latitude\"}");
        ds.AddPropertyValue("var", "{\"name\":\"longitude\",\"nc_type\":\"NC_DOUBLE\",\"dims\":\"longitude\"}");
        //ds.AddPropertyValue("var", "{\"name\":\"pressure\",\"nc_type\":\"NC_FLOAT\",\"dims\":\"time level latitude longitude\"}");
        ds.AddPropertyValue("var", "{\"name\":\"temperature\",\"nc_type\":\"NC_FLOAT\",\"dims\":\"time latitude longitude\"}");
        
      //设变量属性
        ds.AddPropertyValue("att", "{\"var\":\"level\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"m\"}");
        ds.AddPropertyValue("att", "{\"var\":\"latitude\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"degree\"}");
        ds.AddPropertyValue("att", "{\"var\":\"longitude\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"degree\"}");
        
        //ds.AddPropertyValue("att", "{\"var\":\"pressure\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"hpa\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"pressure\",\"name\":\"missing_value\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"-9999\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"pressure\",\"name\":\"add_offset\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"pressure\",\"name\":\"scale_factor\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        
        //ds.AddPropertyValue("att", "{\"var\":\"precipitation\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"mm\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"precipitation\",\"name\":\"missing_value\",\"nc_type\":\"NC_DOUBLE\",\"value\":\""+noVal+"\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"precipitation\",\"name\":\"add_offset\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"precipitation\",\"name\":\"scale_factor\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        
        ds.AddPropertyValue("att", "{\"var\":\"temperature\",\"name\":\"units\",\"nc_type\":\"NC_STRING\",\"value\":\"c\"}");
        ds.AddPropertyValue("att", "{\"var\":\"temperature\",\"name\":\"missing_value\",\"nc_type\":\"NC_DOUBLE\",\"value\":\""+noVal+"\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"temperature\",\"name\":\"add_offset\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        //ds.AddPropertyValue("att", "{\"var\":\"temperature\",\"name\":\"scale_factor\",\"nc_type\":\"NC_DOUBLE\",\"value\":\"100.0\"}");
        
        String fileFormat = "E:/SC/Data/Grid/HosGrid/Prec/month/SURF_CLI_CHN_PRE_MON_GRID_0.5-%s.txt";
        int index = 0;
        
        DatasetRaster dr = (DatasetRaster)ds.GetDataset(index++);
        dr.Open();
        for (int j = 0; j < dr.GetWidth(); j++)
        	dr.SetValue(j, 0, rcBounds.getY() + rcBounds.getHeight() / h * (h - 1 - j));
        dr.CalcExtreme();
        dr.FlushCache();
        dr.Close(); //可释放缓存，节省内存
        
        dr = (DatasetRaster)ds.GetDataset(index++);
        dr.Open();
        for (int j = 0; j < dr.GetWidth(); j++)
        	dr.SetValue(j, 0, rcBounds.getX() + rcBounds.getWidth() / w * j);
        dr.CalcExtreme();
        dr.FlushCache();
        dr.Close(); //可释放缓存，节省内存
        
        for(int y=1981;y<2011;y++){
        	for(int m=1;m<13;m++){
        		DatasetRaster drTarget = (DatasetRaster)ds.GetDataset(index);
        		drTarget.Open();
        		String strMonth = m<10?"0"+m:m+"";
        		String strDate = y+strMonth;
        		String strFile = fileFormat.format(fileFormat, strDate);
        		strJson = "{\"Type\":\"AAIGrid\",\"Alias\":\"modeGrid\",\"Server\":\"" + strFile + "\"}";
        		Datasource dsGrid = ws.OpenDatasource(strJson);
        		DatasetRaster drGrid = (DatasetRaster) dsGrid.GetDataset(0);
        		drGrid.CalcExtreme();
        		drTarget.SetBounds(drGrid.GetBounds());
        		drTarget.SetNoDataValue(drGrid.GetNoDataValue());
        		int rows = drGrid.GetHeight();
        		int cols = drGrid.GetWidth();
        		Scanline slGrid = new Scanline(drGrid.GetValueType(),cols);
        		Scanline slTarget = new Scanline(drTarget.GetValueType(),cols);
        		for(int r=0;r<rows;r++){
        			drGrid.GetScanline(0, r, slGrid);
        			for(int c=0;c<cols;c++){
        				double val = slGrid.GetValue(c);
        				slTarget.SetValue(c, val);
        			}
        			drTarget.SetScanline(0, rows-r-1, slTarget);
        		}
        		drTarget.CalcExtreme();
        		drTarget.FlushCache();
        		drTarget.Close();
        		ws.CloseDatasource("modeGrid");
        		index++;
        	}
        }
        ws.CloseDatasource("TestWriteNETCDF2");
        ws.Destroy();
        System.out.print("over");
	}

}
