package com.spd.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 地区和站号的对应，现在只包括自动站
 * @author Administrator
 *
 */
public class StationArea {

	private static Map<String, String> stationAreaMap = new HashMap<String, String>();
	
	static {
		stationAreaMap.put("57516", "主城");
		stationAreaMap.put("57511", "主城");
		stationAreaMap.put("57513", "主城");
		stationAreaMap.put("57516", "主城");
		stationAreaMap.put("57518", "主城");
		stationAreaMap.put("57409", "西部");
		stationAreaMap.put("57512", "西部");
		stationAreaMap.put("57510", "西部");
		stationAreaMap.put("57502", "西部");
		stationAreaMap.put("57514", "西部");
		stationAreaMap.put("57505", "西部");
		stationAreaMap.put("57506", "西部");
		stationAreaMap.put("57517", "西南");
		stationAreaMap.put("57612", "西南");
		stationAreaMap.put("57509", "西南");
		stationAreaMap.put("57519", "西南");
		stationAreaMap.put("57520", "中部");
		stationAreaMap.put("57522", "中部");
		stationAreaMap.put("57523", "中部");
		stationAreaMap.put("57425", "中部");
		stationAreaMap.put("57525", "东南");
		stationAreaMap.put("57635", "东南");
		stationAreaMap.put("57633", "东南");
		stationAreaMap.put("57537", "东南");
		stationAreaMap.put("57536", "东南");
		stationAreaMap.put("57438", "东南");
		stationAreaMap.put("57437", "东北");
		stationAreaMap.put("57426", "东北");
		stationAreaMap.put("57432", "东北");
		stationAreaMap.put("57339", "东北");
		stationAreaMap.put("57338", "东北");
		stationAreaMap.put("57348", "东北");
		stationAreaMap.put("57349", "东北");
		stationAreaMap.put("57345", "东北");
		stationAreaMap.put("57333", "东北");
		stationAreaMap.put("57431", "东北");
	}
	
	public Map<String, String> getStationAreaMap() {
		return stationAreaMap;
	}
}
