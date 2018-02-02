package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.NationCityStation;
import com.spd.common.Station;
import com.spd.service.ICommon;

public class CommonBus {

	public Object getStationsByUser(String userName) {
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("UserName", userName);
		List<LinkedHashMap> result = iCommon.getStationsByUser(paramMap);
		String str = "";
		for(int i = 0; i < result.size(); i++) {
			LinkedHashMap itemMap = result.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			str += station_Id_C;
			if(i != result.size() - 1) {
				str += ",";
			}
		}
		return str;
	}
	
	public Object getStationsByLevel(int level) {
		//1. 查询站点
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("ZoomLevel", level);
		List<Station> statisticsListResult = iCommon.getStationsByLevel(paramMap);
		return statisticsListResult;
	}
	
	public Object getAllStations() {
		//1. 查询站点
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		List<Station> stations = new ArrayList<Station>();
		List<LinkedHashMap> list = iCommon.getAllStations();
		for(int i = 0; i < list.size(); i++) {
			LinkedHashMap map = list.get(i);
			Station station = new Station();
			station.setStation_Id_C((String)map.get("Station_Id_C"));
			station.setStation_Name((String)map.get("Station_Name"));
			station.setLon((Double) map.get("Lon"));
			station.setLat((Double) map.get("Lat"));
			station.setAreaCode((String)map.get("areaCode"));
			stations.add(station);
		}
		return stations;
	}
	
	/**
	 * 根据国家站，查询对应的区域站
	 * @param nationStations
	 * @return
	 */
	public String getAllStationsByNationStations(String nationStations) {
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("station_id_Cs", nationStations);
		List<LinkedHashMap> resultList = iCommon.getAllStationsByNationStations(paramMap);
		String result = "";
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String station_id_C = (String) itemMap.get("station_id_C");
			result += "'" + station_id_C + "',";
		}
		if(result.length() > 1) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	public List<NationCityStation> getAllNationCityStations() {
		List<NationCityStation> resultList = new ArrayList<NationCityStation>();
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> statisticsListResult = iCommon.getAllNationCityStations(paramMap);
		Map<String, List<LinkedHashMap>> map = new HashMap<String, List<LinkedHashMap>>();
		for(int i = 0; i < statisticsListResult.size(); i++) {
			LinkedHashMap itemMap = statisticsListResult.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			List<LinkedHashMap> itemList = map.get(station_Name);
			if(itemList == null || itemList.size() == 0) {
				itemList = new ArrayList<LinkedHashMap>();
			}
			itemList.add(itemMap);
			map.put(station_Name, itemList);
		}
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> itemList = map.get(key);
			NationCityStation nationCityStation = new NationCityStation();
			nationCityStation.setStation_Name(key);
			String[] stationIDs = new String[itemList.size()];
			String[] types = new String[itemList.size()];
			for(int i = 0; i < itemList.size(); i++) {
				LinkedHashMap itemMap = itemList.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				String type = (String) itemMap.get("Type");
				stationIDs[i] = station_Id_C;
				types[i] =  type;
			}
			nationCityStation.setStation_Id_C(stationIDs);
			nationCityStation.setType(types);
			resultList.add(nationCityStation);
		}
		return resultList;
	}
}
