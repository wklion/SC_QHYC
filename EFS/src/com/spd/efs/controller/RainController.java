package com.spd.efs.controller;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.spd.efs.dao.RainDao;
import com.spd.efs.dao.RainDaoImpl;
import com.spd.efs.pojo.Rain;
import com.spd.efs.pojo.Temperature;

public class RainController extends BaseController{
	
	
	   RainDao rainDao = new RainDaoImpl();
	   
	   
	   
	   public void  getRainAvgData(){
		   ArrayList<Rain> list = new ArrayList<Rain>();
		   list = (ArrayList<Rain>) rainDao.getRainTemAvgData();
		   int count = list.size();
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("draw", 1);
		   jsonObject.put("recordsTotal", count);
		   jsonObject.put("recordsFiltered", count);
		   JSONArray data = JSONArray.fromObject(list);
		   jsonObject.put("data", data);
		   renderJson(jsonObject);
		   
	   }
	   
	   public void initRainColumnarData(){
		   
		   ArrayList<Rain> list = new ArrayList<Rain>();
		   List<Double> data = new ArrayList<Double>();
		   list = (ArrayList<Rain>) rainDao.getRainTemAvgData();
		   for(int i=0;i<list.size();i++){
			   double tem_avg = list.get(i).rain_avg;
			   data.add(tem_avg);
		   }
		   JSONArray columnarData = new JSONArray().fromObject(data);
		   renderJson(columnarData);
	   }
	   
	   
	   // 计算高原雨季开始期
	   public List<Rain> getPlateauRainSeasonStartTimeData(){
		   //获取4月21日 - 6月30日 的所有降水数据
		   List<Rain> list = new ArrayList<Rain>();
		   list = rainDao.getPlateauRainSeasonStartTimeData();
		   
		   
		   return null;
	   }
	   
	   

}
