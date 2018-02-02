package com.spd.qhyc.app;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.spd.qhyc.util.CommonUtil;
import com.spd.qhyc.util.MyFilter;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;


public class ModelMonthJP {
	static Logger logger = LogManager.getLogger("mylog");
	public void excute(Workspace ws,String path) {
		File filePath = new File(path);
		if(!filePath.exists()) {
			logger.error("路径:"+path+"不存在!");
			return;
		}
		Calendar cal = Calendar.getInstance();
		int curYear = cal.get(Calendar.YEAR);
		//String fileNameF = "startDate01.atm.TREFHT.startDate-endDate_sfc_member.nc";
		String fileNameF = "startDate01.atm.PREC.startDate-endDate_sfc_member.nc";
		CommonUtil cu = new CommonUtil();
		Map<String,double[][]> mapData = new HashMap();
		Rectangle2D r2d = null;
		for(int m=1;m<=12;m++) {
			System.out.println(m+"月");
			String strM = m<10?"0"+m:m+"";
			for(int y=1981;y<=curYear;y++) {
				String strStartDate = y+strM;
				String strEndDate = (y+1)+strM;
				String fileName = fileNameF.replace("startDate", strStartDate);
				fileName = fileName.replace("endDate", strEndDate);
				String strFile = path+fileName;
				File file = new File(strFile);
				if(!file.exists()) {
					continue;
				}
				System.out.println(strFile);
				String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"model\",\"Server\":\"%s\"}", strFile);
				Datasource ds=ws.OpenDatasource(strJson);
				int drCount = ds.GetDatasetCount();
				int index = 1;
				for(int i=0;i<drCount;i+=24) {
					String preKey = m+"";
					String nextKey = index+"";
					String key = preKey +"_"+ nextKey;
					DatasetRaster dr = (DatasetRaster) ds.GetDataset(i);
					dr.CalcExtreme();
					if(r2d == null){
						r2d = dr.GetBounds();
					}
					double[][] result = mapData.get(key);
					if(result == null) {
						result = cu.ConvertGridToArray(dr);
						mapData.put(key, result);
					}
					else {
						cu.ArrayAddGrid(result, dr);
						System.out.println("");
					}
					index++;
				}
				ws.CloseDatasource("model");
			}
		}
		String ouputPath = "E:/SC/Data/Mode/PrecAvg/month/";
		for(String key:mapData.keySet()){
			double[][] data = mapData.get(key);
			String outputFile = ouputPath+key+".tif";
			makeTif(ws,outputFile,data,r2d);
		}
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月25日
	 * @修改日期:2017年12月25日
	 * @参数:
	 * @返回:
	 * @说明:生成tif文件
	 */
	private static void makeTif(Workspace ws,String outputFile,double[][] data,Rectangle2D r2d){
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
		dr.SetNoDataValue(-9999.0);
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
}
