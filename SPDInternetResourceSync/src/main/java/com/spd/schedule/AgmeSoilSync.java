package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.cimiss.CIMISSRest;
import com.spd.dao.cq.impl.AgmeSoilDaoImpl;
import com.spd.dao.impl.StationDaoImpl;
import com.spd.pojo.AgmeSoilItem;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 土壤水分资料同步
 * @author Administrator
 *
 */
public class AgmeSoilSync {

	private static Map<String, String> columnMap;
	
	private CIMISSRest cimissRest = new CIMISSRest();
	
	private static Set<String> stations = new HashSet<String>();//国家站
	
	public void init() {
		if(columnMap == null) {
			columnMap = new HashMap<String, String>();
			columnMap.put("Station_Name", "String");
			columnMap.put("Datetime", "String");
			columnMap.put("Station_Id_C", "String");
			columnMap.put("Soil_Indi", "int");
			columnMap.put("Soil_Depth_BelS", "double");
			columnMap.put("SVWC", "double");
			columnMap.put("SRHU", "double");
			columnMap.put("SWWC", "double");
			columnMap.put("SVMS", "double");
		}
		//初始化stations
		if(stations.size() == 0) {
			StationDaoImpl stationDaoImpl = new StationDaoImpl();
			stations = stationDaoImpl.getAWSStations();
		}
	}
	
	public AgmeSoilSync() {
		init();
	}
	public List analyst(String datetime) {
		//1. 从CIMISS中获取数据
		String str = getCIMISSStr(datetime);
		//2. 解析结果
		List dataList = cimissRest.analystChnMulDay(str, columnMap);
		//3. 过滤，只保留国家站
		filter(dataList);
		return dataList;
	}
	
	public HashMap<String, AgmeSoilItem> dispose(List<List> dayDataLists, String datetime) {
		HashMap<String, AgmeSoilItem> resultMap = new HashMap<String, AgmeSoilItem>();
		if(dayDataLists == null) {
			return resultMap;
		}
		for(int i = 0; i < dayDataLists.size(); i++) {
			List dayDataList = dayDataLists.get(i);
			if(dayDataList == null) {
				continue;
			}
			for(int j = 0; j < dayDataList.size(); j++) {
				HashMap dataMap = (HashMap) dayDataList.get(j);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				Double Soil_Depth_BelS = (Double) dataMap.get("Soil_Depth_BelS");
				if(Soil_Depth_BelS > 40) { //只取10,20,30,40的
					continue;
				}
				Double SVWC = (Double) dataMap.get("SVWC");
				Double SRHU = (Double) dataMap.get("SRHU");
				Double SWWC = (Double) dataMap.get("SWWC");
				Double SVMS = (Double) dataMap.get("SVMS");
				AgmeSoilItem agmeSoilItem = resultMap.get(station_Id_C + "_" + Soil_Depth_BelS);
				if(agmeSoilItem == null) {
					agmeSoilItem = new AgmeSoilItem();
				}
				agmeSoilItem.setStation_Id_C(station_Id_C);
				agmeSoilItem.setSoil_Depth_BelS(Soil_Depth_BelS);
				if(SVWC < 999) {
					if(agmeSoilItem.getSVWC() == null) {
						agmeSoilItem.setSVWC(SVWC); 
					} else {
						agmeSoilItem.setSVWC((SVWC + agmeSoilItem.getSVWC()) / 2);
					}
				}
				if(SRHU < 999) {
					if(agmeSoilItem.getSRHU() == null) {
						agmeSoilItem.setSRHU(SRHU); 
					} else {
						agmeSoilItem.setSRHU((SRHU + agmeSoilItem.getSRHU()) / 2);
					}
				}
				if(SWWC < 999) {
					if(agmeSoilItem.getSWWC() == null) {
						agmeSoilItem.setSWWC(SWWC); 
					} else {
						agmeSoilItem.setSWWC((SWWC + agmeSoilItem.getSWWC()) / 2);
					}
				}
				if(SVMS < 999) {
					if(agmeSoilItem.getSVMS() == null) {
						agmeSoilItem.setSVMS(SVMS);
					} else {
						agmeSoilItem.setSVMS(SVMS + agmeSoilItem.getSVMS() / 2);
					}
				}
				resultMap.put(station_Id_C + "_" + Soil_Depth_BelS, agmeSoilItem);
			}
		}
		return resultMap;
	}
	
	public void insert(HashMap<String, AgmeSoilItem> agmeSoilMap, String datetime) {
		datetime = datetime.substring(0, 4) + "-" + datetime.substring(4, 6) + "-" + datetime.substring(6);
		AgmeSoilDaoImpl agmeSoilDaoImpl = new AgmeSoilDaoImpl();
		HashMap<String, Object> existData = agmeSoilDaoImpl.getExistData(datetime);
		List dataList = new ArrayList();
		Iterator it = agmeSoilMap.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			AgmeSoilItem item = agmeSoilMap.get(key);
			HashMap dataMap = new HashMap();
			dataMap.put("Station_Id_C", item.getStation_Id_C());
			dataMap.put("Datetime", datetime + " 00:00:00");
			dataMap.put("soil_Depth_BelS", item.getSoil_Depth_BelS());
			if(existData.containsKey(item.getStation_Id_C() + "_" + datetime + "_" + item.getSoil_Depth_BelS())) {
				continue;
			}
			dataMap.put("SVWC", item.getSVWC());
			dataMap.put("SRHU", item.getSRHU());
			dataMap.put("SWWC", item.getSWWC());
			dataMap.put("SVMS", item.getSVMS());
			dataList.add(dataMap);
		}
		//入库
		agmeSoilDaoImpl.insertValue(dataList);
	}
	
	private void filter(List dataList) {
		if(dataList != null && dataList.size() > 0) {
			for(int i = dataList.size() - 1; i >= 0; i--) {
				HashMap dataMap = (HashMap) dataList.get(i);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				if(!stations.contains(station_Id_C)) {
					dataList.remove(i);
				}
			}
		}
	}
	
	private String getCIMISSStr(String datetime) {
		String url = "http://10.230.89.55/cimiss-web/api?userId=BECQ_QHZX_byy&pwd=qhzxbyy&interfaceId=getAgmeEleInRegionByTime&dataCode=" +
				"AGME_CHN_SOIL_HOR&times=" + datetime + "&adminCodes=500000&elements=Station_Name," +
				"Datetime,Station_Id_C,Soil_Depth_BelS,SVWC,SRHU,SWWC,SVMS&dataFormat=json";
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	
	public void sync(String datetime) {
		datetime = datetime.replaceAll("-", "");
		//遍历24个小时的资料
		List<List> dayDataLists = new ArrayList<List>();
		for(int i = 0; i < 24; i++) {
			String hourDateTime = datetime + String.format("%02d", i) + "0000";
			List hourList = analyst(hourDateTime);
			dayDataLists.add(hourList);
		}
		//处理结果
		HashMap<String, AgmeSoilItem> resultMap = dispose(dayDataLists, datetime);
		//整理入库
		insert(resultMap, datetime);
	}
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		AgmeSoilSync agmeSoilSync = new AgmeSoilSync();
		//有数据的起始时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = "2016-06-22";
		String end = "2017-06-09";
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(start);
			endDate = sdf.parse(end);
			for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
				String datetime = sdf.format(new Date(i));
				System.out.println(datetime);
				agmeSoilSync.sync(datetime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
