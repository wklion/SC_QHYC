package com.spd.efs.controller;


import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.spd.efs.dao.TemperatureDao;
import com.spd.efs.dao.TemperatureDaoImpl;
import com.spd.efs.pojo.Temperature;

public class TemperatureController extends BaseController{
	
	
	   TemperatureDao temDao = new TemperatureDaoImpl();
	   
	   
	   
	   
	   public void index(){
		   
		   
	   }
	   
	   public void getAllTemData(){
		   ArrayList<Temperature> list = new ArrayList<Temperature>();
		   list = (ArrayList<Temperature>) temDao.getAllTemData();
		   int count = list.size();
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("draw", 1);
		   jsonObject.put("recordsTotal", count);
		   jsonObject.put("recordsFiltered", count);
		   JSONArray data = JSONArray.fromObject(list);
		   jsonObject.put("data", data);
		   renderJson(jsonObject);
		   
	   }
	   
	   public void initColumnarData(){
		   
		   ArrayList<Temperature> list = new ArrayList<Temperature>();
		   List<Double> data = new ArrayList<Double>();
		   list = (ArrayList<Temperature>) temDao.initColumnarData();
		   for(int i=0;i<list.size();i++){
			   double tem_avg = list.get(i).getTem_avg();
			   data.add(tem_avg);
		   }
		   JSONArray columnarData = new JSONArray().fromObject(data);
		   renderJson(columnarData);
		   
	   }

}
