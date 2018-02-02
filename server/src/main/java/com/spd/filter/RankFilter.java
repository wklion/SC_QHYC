package com.spd.filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.google.gson.Gson;
import com.spd.service.ICommon;

public class RankFilter {

	
	public String createQueryParam(String para, List<String> authorityCodes, String areaCode) {
		CommonFilter commonFilter = new CommonFilter();
		String result = commonFilter.createQueryParam(para, authorityCodes, areaCode);
		if(result != null) {
			return result;
		}
		Gson gson = new Gson();
		com.google.gson.internal.LinkedTreeMap treeMap = gson.fromJson(para, com.google.gson.internal.LinkedTreeMap.class);
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("areaCode", areaCode);
		List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
		boolean flag = false;
		String value = "";
		for(int i = 0; i < station_idList.size(); i++) {
			value += station_idList.get(i).get("Station_Id_C");
			if(i != station_idList.size() - 1) {
				value += ",";
			}
		}
		treeMap.put("station_Id_Cs", value);
		para = gson.toJson(treeMap);
		return para;
	}
}
