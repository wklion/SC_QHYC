package com.spd.schedule.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.spd.cimiss.CIMISSRest;
import com.spd.dao.cq.impl.StationsDaoImpl;
import com.spd.tool.PropertiesUtil;

/**
 *  从CIMISS中导入站点
 * @author Administrator
 *
 */
public class StationImpl {

	private StationsDaoImpl stationsDaoImpl = new StationsDaoImpl();
	
	public Set<String> getStations() {
		//1. 从CIMISS中获取站点
		Set<String> set = stationsDaoImpl.getExistStation();
		return set;
		//2. 判断已经存在的数据，不存在的导入
	}
	
	public String getStationsByCIMISS() {
		CIMISSRest cimissRest = new CIMISSRest();
		String url = "http://10.230.89.17:8008/cimiss-web/api?userId=BECQ_QHZX_byy&pwd=qhzxbyy&interfaceId=getStaInfoByRegionCode&dataCode=STA_INFO_SURF_CHN&elements=Station_Id_C,Station_Name,Admin_Code_CHN,Province,City,Cnty,Town,Lat,Lon,Alti&adminCodes=500000&dataFormat=json";
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	
	public List analystChnMulDayItemData(String data, Set<String> existStations) {
		Gson gson = new Gson();
		Object o = gson.fromJson(data, Object.class);
		List insertDataList = new ArrayList();
		com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) o;
		ArrayList ds = (ArrayList) linkedTreeMap.get("DS");
		if(ds == null) {
			return null;
		}
		for(int i=0; i<ds.size(); i++) {
			com.google.gson.internal.LinkedTreeMap item = (com.google.gson.internal.LinkedTreeMap)ds.get(i);
			Set set = item.keySet();
			Iterator it = set.iterator();
			Map<String, Object> mapData = new HashMap<String, Object>();
			String Station_Id_C = (String) item.get("Station_Id_C");
			if(existStations.contains(Station_Id_C)) {
				continue;
			}
			String Station_Name = (String) item.get("Station_Name");
			String Province = "重庆市";
			String Country = (String) item.get("Cnty");
			String AreaCode = (String) item.get("Admin_Code_CHN");
			double Lon = Double.parseDouble((String) item.get("Lon"));
			double Lat = Double.parseDouble((String) item.get("Lat"));
			double Alti = Double.parseDouble((String) item.get("Alti"));
			mapData.put("Station_Id_C", Station_Id_C);
			mapData.put("Station_Name", Station_Name);
			mapData.put("Province", Province);
			mapData.put("Country", Country);
			mapData.put("AreaCode", AreaCode);
			mapData.put("Lon", Lon);
			mapData.put("Lat", Lat);
			mapData.put("Alti", Alti);
			insertDataList.add(mapData);
		}
		return insertDataList;
	}
	
	public void insert(List dataList) {
		stationsDaoImpl.insertStationValue(dataList);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		StationImpl stationImpl = new StationImpl();
		Set<String> existStationSet = stationImpl.getStations();
		String cimissStations = stationImpl.getStationsByCIMISS();
		List dataList = stationImpl.analystChnMulDayItemData(cimissStations, existStationSet);
		stationImpl.insert(dataList);
	}

}
