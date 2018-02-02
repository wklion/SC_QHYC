package com.spd.qhyc.app;

import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;

public class TifTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String strFile = "C:/Users/wklion/Desktop/temp/Z_OTHE_RADAMCR_20180117010600.tif";
		Workspace ws = new Workspace();
		String strJson = String.format("{\"Type\":\"GTiff\",\"Alias\":\"test\",\"Server\":\"%s\"}", strFile);
		Datasource ds = ws.OpenDatasource(strJson);
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
		dr.CalcExtreme();
		double max = dr.GetMaxValue();
		System.out.println(max);
	}

}
