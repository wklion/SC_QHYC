package com.spd.qhyc.service;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.mg.objects.DatasetRaster;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;
import com.spd.qhyc.app.StationVal;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.util.DateUtil;
import com.spd.qhyc.util.LogTool;

public class ForecastServer {
	static Logger logger = LogTool.getLog();
	/**
	 * @autor:杠上花
	 * @date:2018年1月30日
	 * @modifydate:2018年1月30日
	 * @param:
	 * @return:
	 * @description:获取历史月数据
	 */
	public List<StationVal> getHosMonthJP(Workspace ws,int year,int month,Config config,String elementID,List<XNStation> lsXNStation){
		String dir = elementID.toLowerCase().equals("temp")?config.getHosMonthTempPathMulFile():config.getHosMonthPrecPathMulFile();
		String strMonth = month<10?"0"+month:month+"";
		String fileName = year+strMonth+".tif";
		String strFile = dir+fileName;
		File file = new File(strFile);
		if(!file.exists()){
			logger.error("文件:"+strFile+"不存在!");
			return null;
		}
		Calendar cal = Calendar.getInstance();
		String alias = "grid"+DateUtil.format("hhmmss", cal);
		String strJson = String.format("{\"Type\":\"GTiff\",\"Alias\":\""+alias+"\",\"Server\":\"%s\"}", strFile);
		Datasource ds = ws.OpenDatasource(strJson);
		DatasetRaster dr = (DatasetRaster) ds.GetDataset(0);
		dr.CalcExtreme();
		//打开历史平均
		dir = elementID.toLowerCase().equals("temp")?config.getHosMonthTempAvgPath():config.getHosMonthPrecAvgPath();
		strFile = dir+month+".tif";
		file = new File(strFile);
		if(!file.exists()){
			logger.error("文件:"+strFile+"不存在!");
			return null;
		}
		alias = "hosGrid"+DateUtil.format("hhmmss", cal);
		strJson = String.format("{\"Type\":\"GTiff\",\"Alias\":\""+alias+"\",\"Server\":\"%s\"}", strFile);
		Datasource hosDS = ws.OpenDatasource(strJson);
		DatasetRaster hosDR = (DatasetRaster) hosDS.GetDataset(0);
		hosDR.CalcExtreme();
		List<StationVal> lsStationVal = new ArrayList();
		StationVal sv = null;
		String id = elementID.toLowerCase();
		for(XNStation station:lsXNStation){
			double lon = station.getLon();
			double lat = station.getLat();
			Point2D p2d=new Point2D.Double(lon,lat);
			Point2D cell=dr.PointToCell(p2d);
			int x=(int) cell.getX();
			int y=(int) cell.getY();
			double val=dr.GetValue(x, y);//此格点值
			double hosVal = hosDR.GetValue(x, y);
			double resultVal = id.equals("temp")?val-hosVal:100*(val-hosVal)/hosVal;
			sv = new StationVal();
			sv.setLongitude(lon);
			sv.setLatitude(lat);
			sv.setStationName(station.getStation_Name());
			sv.setStationNum(station.getStation_Id_C());
			sv.setValue(resultVal);
			lsStationVal.add(sv);
		}
		ws.CloseDatasource(alias);
		return lsStationVal;
	}
}
