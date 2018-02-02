package com.spd.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.google.gson.Gson;
import com.spd.service.ICommon;

public class HourRainFilter {

	//参数中可以添加分区域进行过滤
	private static Set<String> HOURRAINAREAPARASET = new HashSet<String>();
	//对结果进行过滤，参数不用过滤
	private static Set<String> HOURRAINONLYRESULTSET = new HashSet<String>();
	//没有参数的请求
	private static Set<String> HOURRAINNOPARARESULTSET = new HashSet<String>();
	
	public HourRainFilter() {
		if(HOURRAINAREAPARASET.size() == 0) {
			HOURRAINAREAPARASET.add("hourRainExt");
			HOURRAINAREAPARASET.add("hourRainAccumulate");
			HOURRAINAREAPARASET.add("hourRainSequence");
			HOURRAINAREAPARASET.add("hourRainRankTimesStatistics");
			HOURRAINAREAPARASET.add("hourRainRankYearsStatistics");
//			HOURRAINAREAPARASET.add("hourRainExtYearsStatistics");
			HOURRAINAREAPARASET.add("hourRainExtByTimes");
		}
		if(HOURRAINONLYRESULTSET.size() == 0) {
//			HOURRAINONLYRESULTSET.add("hourRainStation");
			HOURRAINONLYRESULTSET.add("hourRainExtYearsStatistics");
			HOURRAINONLYRESULTSET.add("hourRainSortByStation");
			HOURRAINONLYRESULTSET.add("hourRainChange");
		}
		if(HOURRAINNOPARARESULTSET.size() == 0) {
			HOURRAINNOPARARESULTSET.add("hourRainStation");
		}
	}
	
	public String createQueryParam(String para, List<String> authorityCodes, String areaCode, String methodName) {
		//如果有浏览全部的权限，或者是市局用户，则不过滤参数
		if("500000".equals(areaCode)) {
			return para;
		}
		for(String authorityCode : authorityCodes) {
			if("BROWSEALL".equals(authorityCode)) {
				return para;
			}
		}
		Gson gson = new Gson();
		com.google.gson.internal.LinkedTreeMap treeMap = gson.fromJson(para, com.google.gson.internal.LinkedTreeMap.class);
		if(HOURRAINAREAPARASET.contains(methodName)) {
			if(treeMap == null) return para;
			boolean isContainsKey = treeMap.containsKey("type");
			if(!isContainsKey)
				return para;
			String type = (String) treeMap.get("type");
			if(!"AREA".equals(type)) {
				treeMap.put("type", "AREA");
				treeMap.put("resultType", type);
			}
			System.out.println(treeMap);
			String result = gson.toJson(treeMap);
			return result;
		} else if(HOURRAINONLYRESULTSET.contains(methodName)) {
			String station_Id_C = (String) treeMap.get("Station_Id_C");
			ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
			HashMap paramMap = new HashMap();
			paramMap.put("areaCode", areaCode);
			List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
			boolean flag = false;
			for(int i = 0; i < station_idList.size(); i++) {
				LinkedHashMap itemStationMap = station_idList.get(i);
				String itemStation = (String) itemStationMap.get("Station_Id_C");
				if(itemStation.equals(station_Id_C)) {
					flag = true;
					return para;
				}
			}
			if(!flag) {
				return null;
			}
		} else if (HOURRAINNOPARARESULTSET.contains(methodName)) {
			ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
			HashMap paramMap = new HashMap();
			paramMap.put("areaCode", areaCode);
			List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
			String value = "";
			for(int i = 0; i < station_idList.size(); i++) {
				value += station_idList.get(i).get("Station_Id_C");
				if(i != station_idList.size() - 1) {
					value += ",";
				}
			}
			treeMap = new com.google.gson.internal.LinkedTreeMap();
			treeMap.put("station_Id_Cs", value);
			para = gson.toJson(treeMap);
			return para;
		}
		return para;
		
	}
}
