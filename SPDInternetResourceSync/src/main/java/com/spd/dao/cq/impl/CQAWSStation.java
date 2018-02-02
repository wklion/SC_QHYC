package com.spd.dao.cq.impl;

import java.util.HashSet;
import java.util.Set;

/**
 * 国家站
 * @author Administrator
 *
 */
public class CQAWSStation {
	
	public static Set<String> STATIONS;
	
	private CQAWSStation() {
		
	}
	
	public static boolean isCQStation(String station) {
//		init();
//		if(station.startsWith("5") && !STATIONS.contains(station)) return false;
		return true;
	}
	
	private static void init() {
		if(STATIONS == null) {
			STATIONS  = new HashSet<String>();
			STATIONS.add("57516");
			STATIONS.add("57511");
			STATIONS.add("57513");
			STATIONS.add("57518");
			STATIONS.add("57409");
			STATIONS.add("57512");
			STATIONS.add("57510");
			STATIONS.add("57502");
			STATIONS.add("57514");
			STATIONS.add("57505");
			STATIONS.add("57506");
			STATIONS.add("57517");
			STATIONS.add("57612");
			STATIONS.add("57509");
			STATIONS.add("57519");
			STATIONS.add("57520");
			STATIONS.add("57522");
			STATIONS.add("57523");
			STATIONS.add("57425");
			STATIONS.add("57525");
			STATIONS.add("57635");
			STATIONS.add("57633");
			STATIONS.add("57537");
			STATIONS.add("57536");
			STATIONS.add("57438");
			STATIONS.add("57437");
			STATIONS.add("57426");
			STATIONS.add("57432");
			STATIONS.add("57339");
			STATIONS.add("57338");
			STATIONS.add("57348");
			STATIONS.add("57349");
			STATIONS.add("57345");
			STATIONS.add("57333");
		}
	}
	
	public static Set<String> getAWSStations() {
		init();
		return STATIONS;
	}
}
