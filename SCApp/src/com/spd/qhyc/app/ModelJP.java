package com.spd.qhyc.app;

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
import com.mg.objects.Workspace;


public class ModelJP {
	static Logger logger = LogManager.getLogger("mylog");
	public void excute(Workspace ws,String path) {
		File filePath = new File(path);
		if(!filePath.exists()) {
			logger.error("路径:"+path+"不存在!");
			return;
		}
		Calendar cal = Calendar.getInstance();
		int curYear = cal.get(Calendar.YEAR);
		String fileNameF = "startDate01.atm.TREFHT.startDate-endDate_sfc_member.nc";
		CommonUtil cu = new CommonUtil();
		Map<String,double[][]> mapData = new HashMap();
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
				for(int i=0;i<drCount;i++) {
					String preKey = m+"";
					String nextKey = (m+i)+"";
					String key = preKey + nextKey;
					DatasetRaster dr = (DatasetRaster) ds.GetDataset(i);
					dr.CalcExtreme();
					double[][] result = mapData.get(key);
					if(result == null) {
						result = cu.ConvertGridToArray(dr);
					}
					else {
						cu.ArrayAddGrid(result, dr);
					}
				}
				ws.CloseDatasource("model");
			}
		}
	}
}
