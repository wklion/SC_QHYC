package com.spd.grid.ws;

import java.io.File;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.grid.config.ConfigHelper;
import com.spd.grid.domain.Application;
import com.spd.grid.funModel.GetGridParam;
import com.spd.grid.model.CommonResult;
import com.spd.grid.model.Config;
import com.spd.grid.tool.GridUtil;
import com.spd.grid.tool.LogTool;
import com.spd.weathermap.domain.GridData;
import com.spd.weathermap.util.Toolkit;

@Stateless
@Path("FileData")
public class FileData {
	static{
		if(ConfigHelper.config==null){
			ConfigHelper configHelper = new ConfigHelper();
			configHelper.excute();
		}
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月25日
	 * @修改日期:2017年12月25日
	 * @参数:
	 * @返回:
	 * @说明:
	 */
	@POST
	@Path("getGrid")
	@Produces("application/json")
	public Object getGrid(@FormParam("para") String para){
		CommonResult cr = new CommonResult();
		GridUtil gridUtil = new GridUtil();
		double targetDelta = 0.5;//分辨率
		GridData gridData = null;
		Gson gson = new Gson();
		GetGridParam getGridParam = gson.fromJson(para, GetGridParam.class);
		Workspace ws=Application.m_workspace;
		File file = new File(getGridParam.getFile());
		if(!file.exists()){
			cr.setErr(null);
			return cr;
		}
		String alias = file.getName();
		String strDate = alias.substring(0, 8);
		String strFile = file.getAbsolutePath().replace("\\", "/");
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", alias,strFile);
		Datasource ds = ws.OpenDatasource(strJson);
		int index = (getGridParam.getHourspan()-1)*24;
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(index);
		dr.CalcExtreme();
		//创建临时数据源
		String tempAlias = "tempDS";
		strJson = "{\"Type\":\"Memory\",\"Alias\":\""+tempAlias+"\",\"Server\":\"\"}";
        Datasource dsTemp = ws.CreateDatasource(strJson);
        if(dsTemp == null){
            LogTool.logger.error("临时数据源创建失败!");
            cr.setErr(null);
            return cr;
        }
		gridData = Toolkit.convertDatasetRasterToGridDataSrcVal(dr,strDate);
		String elementID = getGridParam.getElementID();
        String valTypeID = getGridParam.getValTypeID();
        String avgAlias = "avgData";
		if(valTypeID.equals("juping")){//距平
		  //裁剪
            gridUtil.GridClip(ws, ds.GetAlias(), dr.GetName(), tempAlias, "ClipDG");
            DatasetRaster originalDR = (DatasetRaster) dsTemp.GetDataset("ClipDG");
            if(originalDR == null){
                LogTool.logger.error("裁剪数据失败!");
                cr.setErr(null);
                return cr;
            }
            
		    //同化模式预测数据
		    gridUtil.TongHua(ws, dsTemp.GetAlias(), originalDR.GetName(), targetDelta, targetDelta, tempAlias, "THDG");
		    originalDR = (DatasetRaster) dsTemp.GetDataset("THDG");
	        if(originalDR == null){
	            LogTool.logger.error("同化数据失败!");
	            cr.setErr(null);
	            return cr;
	        }
			//打开历史平均数据
	        String strAvgFile = getGridParam.getAvgFile();
	        File fileJP = new File(strAvgFile);
            if(!fileJP.exists()){
                cr.setErr(null);
                return cr;
            }
			strAvgFile = strAvgFile.replace("\\", "/");
			strJson = String.format("{\"Type\":\"GTiff\",\"Alias\":\"%s\",\"Server\":\"%s\"}", avgAlias,strAvgFile);
			Datasource dsAvg = ws.OpenDatasource(strJson);
			DatasetRaster drAvg = (DatasetRaster) dsAvg.GetDataset(0);
			//计算距平
			if(elementID.equals("prec")){//距平百分率
				gridUtil.calRaster(ws, originalDR, drAvg, tempAlias, "jpData","(([a]*3600*6*10000)-[b])/[b]");
			}
			else if(elementID.equals("temp")){
				gridUtil.calRaster(ws, originalDR, drAvg, tempAlias, "jpData","([a]-273.15)-[b]");
			}
			else{
			    gridUtil.calRaster(ws, originalDR, drAvg, tempAlias, "jpData","[a]-[b]");
			}
			DatasetRaster jpDR = (DatasetRaster) dsTemp.GetDataset("jpData");
			gridData = Toolkit.convertDatasetRasterToGridDataSrcVal(jpDR,strDate);
			/*ArrayList<Double> vals = gridData.getDValues();
			//获取距平数据
			double[] jpVals = getJP(ws,getGridParam.getJpFile());
			int srcLen = vals.size();
			int jpLen = jpVals.length;
			if(srcLen!=jpLen){
				LogTool.logger.error("原始数组和距平数组长度不相等!");
				cr.setErr(null);
	            return cr;
			}
			if(elementID.equals("prec")){//距平百分率
				for(int i=0;i<srcLen;i++){
					double srcVal = vals.get(i);
					double jpVal = jpVals[i];
					double val = 100*(srcVal-jpVal)/jpVal;
					vals.set(i, val);
				}
			}
			else{//距平
				for(int i=0;i<srcLen;i++){
					double srcVal = vals.get(i);
					double jpVal = jpVals[i];
					double val = srcVal-jpVal;
					vals.set(i, val);
				}
			}*/
		}
		cr.setSuc(gridData);
		ws.CloseDatasource(alias);//关闭预测模式数据
        ws.CloseDatasource(tempAlias);//关闭临时数据源
		return cr;
	}
	/**
	 * @作者:杠上花
	 * @日期:2017年12月25日
	 * @修改日期:2017年12月25日
	 * @参数:cidu-尺度(天,月)
	 * @返回:距平的一维数组
	 * @说明:获取距平
	 */
	private double[] getJP(Workspace ws,String strJPFile){
		File file = new File(strJPFile);
		String strFileName = file.getName();
		String strJson = "{\"Type\":\"GTiff\",\"Alias\":\"" + strFileName+ "\",\"Server\":\"" + strJPFile + "\",\"ReadOnly\":\"true\"}";
		Datasource ds = ws.OpenDatasource(strJson);
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
		dr.Open();
		dr.CalcExtreme();
		int rows = dr.GetHeight();
		int cols = dr.GetWidth();
		Scanline sl = new Scanline(dr.GetValueType(),cols);
		double[] vals = new double[rows*cols];
		int index = 0;
		for(int r=rows-1;r>=0;r--){
			dr.GetScanline(0, r, sl);
			for(int c=0;c<cols;c++){
				double val = sl.GetValue(c);
				vals[index] = val;
				index++;
			}
		}
		ws.CloseDatasource(strFileName);
		return vals;
	}
}
