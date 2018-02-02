package com.spd.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.Station;
import com.spd.service.ICommon;

public class CommonUtil {

	public static HashMap<String, String> stationAreaMap = new HashMap<String, String>();

	public static HashMap<String, String> stationNameMap = new HashMap<String, String>();

	public static HashMap<String, String> areaCodeMap = new HashMap<String, String>();

	private static List<Station> awsStations = new ArrayList<Station>();
	
	private static CommonUtil commonUtil; 
	
	private CommonUtil() {
		
	}
	
	public static CommonUtil getInstance() {
		initStationAreaMap();
		initAWSStations();
		return commonUtil;
	}
	
	public static List<Station> getAwsStations() {
		return awsStations;
	}

	public static void initAWSStations() {
		if(awsStations.size() > 0) return;
		ICommon common = (ICommon) ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap commonParamMap = new HashMap();
		commonParamMap.put("ZoomLevel", 1);
		// 自动站
		awsStations = common.getStationsByLevel(commonParamMap);
	}
	
	public static void initStationAreaMap() {
		if(stationAreaMap.size() > 0 && stationNameMap.size() > 0) return;
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		List<LinkedHashMap> resultMapList = iCommon.getAllStations();
		for(int i = 0; i < resultMapList.size(); i++) {
			LinkedHashMap itemMap = resultMapList.get(i);
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
			String Station_Name = (String) itemMap.get("Station_Name");
			String areaCode = (String) itemMap.get("areaCode");
			String area = (String) itemMap.get("area");
			stationAreaMap.put(Station_Id_C, area);
			stationNameMap.put(Station_Id_C, Station_Name);
			areaCodeMap.put(Station_Id_C, areaCode);
		}
	}
	
}
