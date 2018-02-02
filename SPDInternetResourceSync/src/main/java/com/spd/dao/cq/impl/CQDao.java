package com.spd.dao.cq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.BaseDao;

public class CQDao extends BaseDao {

	private String tableName;
	
	private static Map<String, String> STATIONMAP = new HashMap<String, String>();
	
	static {
		initStation();
	}
	/**
	 * 初始化站名和站号的对应
	 */
	private static void initStation() {
		STATIONMAP.put("57333", "城口");
		STATIONMAP.put("57338", "开州");
		STATIONMAP.put("57339", "云阳");
		STATIONMAP.put("57345", "巫溪");
		STATIONMAP.put("57348", "奉节");
		STATIONMAP.put("57349", "巫山");
		STATIONMAP.put("57409", "潼南");
		STATIONMAP.put("57425", "垫江");
		STATIONMAP.put("57426", "梁平");
		STATIONMAP.put("57432", "万州");
		STATIONMAP.put("57437", "忠县");
		STATIONMAP.put("57438", "石柱");
		STATIONMAP.put("57502", "大足");
		STATIONMAP.put("57505", "荣昌");
		STATIONMAP.put("57506", "永川");
		STATIONMAP.put("57509", "万盛");
		STATIONMAP.put("57510", "铜梁");
		STATIONMAP.put("57512", "合川");
		STATIONMAP.put("57513", "渝北");
		STATIONMAP.put("57514", "璧山");
		STATIONMAP.put("57516", "沙坪坝");
		STATIONMAP.put("57517", "江津");
		STATIONMAP.put("57518", "巴南");
		STATIONMAP.put("57519", "南川");
		STATIONMAP.put("57520", "长寿");
		STATIONMAP.put("57522", "涪陵");
		STATIONMAP.put("57523", "丰都");
		STATIONMAP.put("57525", "武隆");
		STATIONMAP.put("57536", "黔江");
		STATIONMAP.put("57537", "彭水");
		STATIONMAP.put("57612", "綦江");
		STATIONMAP.put("57633", "酉阳");
		STATIONMAP.put("57635", "秀山");
		STATIONMAP.put("57511", "北碚");
	}
	
	public CQDao(String tableName) {
		this.tableName = tableName;
	}
	
	public List queryData(String columnName, int year) {
		String query = "select " + columnName + ", Station_Id_C, Station_Name from " + tableName + " where year = " + year + " and Station_Id_C like '5%'";	
		Set<String> stationSet = new HashSet<String>();
		List list = query(getConn(), query, null);
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				HashMap dataMap = (HashMap) list.get(i);
				String station_Id_C = (String) dataMap.get("Station_Id_C");
				String station_Name = STATIONMAP.get(station_Id_C);
				dataMap.put("Station_Name", station_Name);
			}
		}
		return list;
	}
	
}
