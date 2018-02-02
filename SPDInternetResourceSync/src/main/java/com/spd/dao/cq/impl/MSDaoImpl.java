package com.spd.dao.cq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.MSBaseDao;

public class MSDaoImpl extends MSBaseDao {

	public List getHourRainData(String startDateTime, String endDateTime, Set<String> stationSet) {
		//mysql
		String query = "select 站号, convert(varchar(100), 日期, 120) as 日期, R1, R3, R6, R12, R24 from hourrain where 日期 >= '" + startDateTime + "' and 日期 <= '" + endDateTime + "'";		
		List list = query(getConn(), query, null);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < list.size(); i++) {
			HashMap tempMap = (HashMap) list.get(i);
			Float R1 = (Float) tempMap.get("R1");
			Float R3 = (Float) tempMap.get("R3");
			Float R6 = (Float) tempMap.get("R6");
			Float R12 = (Float) tempMap.get("R12");
			Float R24 = (Float) tempMap.get("R24");
			if((R1 != null && R1 !=0) || (R3 != null && R3 !=0) || (R6 != null && R6 !=0) || (R12 != null && R12 !=0) || (R24 != null && R24 !=0)) {
				HashMap dataMap = new HashMap();
				String station = tempMap.get("站号") + "";
				dataMap.put("datetime", tempMap.get("日期"));
				dataMap.put("station_Id_C", station);
				dataMap.put("R1", tempMap.get("R1"));
				dataMap.put("R3", tempMap.get("R3"));
				dataMap.put("R6", tempMap.get("R6"));
				dataMap.put("R12", tempMap.get("R12"));
				dataMap.put("R24", tempMap.get("R24"));
				if(stationSet.contains(station)) {
					resultList.add(dataMap);
				}
			}
		}
		return resultList;
	}
}
