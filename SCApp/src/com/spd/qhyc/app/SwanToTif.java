package com.spd.qhyc.app;

import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;

public class SwanToTif {

	public static void main(String[] args) {
		String strFile = "C:/Users/wklion/Desktop/temp/Z_OTHE_RADAMCR_20180117015400.bin";
		String outputFile = "C:/Users/wklion/Desktop/temp/Z_OTHE_RADAMCR_20180117015400.tif";
		Workspace ws = new Workspace();
		String strJson = String.format("{\"Type\":\"SWAN\",\"Alias\":\"test\",\"Server\":\"%s\"}", strFile);
		Datasource ds = ws.OpenDatasource(strJson);
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
		int rows = dr.GetHeight();
		int cols = dr.GetWidth();
		//创建tif
		strJson = "{\"Type\":\"GTiff\",\"Alias\":\"test1\",\"Server\":\"" + outputFile + "\",\"ReadOnly\":\"false\"}";
		Datasource dsTif = ws.CreateDatasource(strJson);
		strJson = String.format("{\"Name\":\"RTif\",\"ValueType\":\"Single\",\"Width\":%d,\"Height\":%d}", cols, rows);
		DatasetRaster drTif = dsTif.CreateDatasetRaster(strJson);
		drTif.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
		drTif.SetBounds(dr.GetBounds());
		//drTif.SetProjection(arg0);
		Scanline sl = new Scanline(dr.GetValueType(),cols);
		Scanline slTif = new Scanline(drTif.GetValueType(),cols);
		for(int r=0;r<rows;r++) {
			dr.GetScanline(0, r, sl);
			for(int c=0;c<cols;c++) {
				double val = sl.GetValue(c);
				slTif.SetValue(c, val);
			}
			drTif.SetScanline(0, r, slTif);
		}
		sl.Destroy();
		slTif.Destroy();
		drTif.CalcExtreme();
		drTif.FlushCache();
		System.out.println(drTif.GetMaxValue());
		ws.CloseDatasource("test");
		ws.CloseDatasource("test1");
		System.out.println("over");
		System.exit(0);
	}

}
