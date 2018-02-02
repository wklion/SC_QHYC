package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;

import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.mg.objects.*;

/**
 * @作者:杠上花
 * @日期:2018年1月22日
 * @公司:spd
 * @说明:
*/
public class TifRewrite {

	public static void main(String[] args) {
		Workspace workspace = new Workspace();
		String strFile = "C:/Users/lenovo/Desktop/temp/20180117Res/Z_OTHE_RADAMCR_20180117010600.tif";
		String strJson = String.format("{\"Type\":\"Gtiff\",\"Alias\":\"model\",\"Server\":\"%s\",\"ReadOnly\":\"false\"}", strFile);
		Datasource ds = workspace.OpenDatasource(strJson);
		DatasetRaster dRaster  = (DatasetRaster) ds.GetDataset(0);
		dRaster.SetProjection("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
		Rectangle2D rectangle2d  = dRaster.GetBounds();
		dRaster.FlushCache();
		workspace.CloseDatasource("model");
		System.out.println("over");
	}

}
