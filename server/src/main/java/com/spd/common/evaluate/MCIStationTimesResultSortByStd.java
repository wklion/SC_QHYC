package com.spd.common.evaluate;

import java.util.Comparator;

/**
 * 按singleSynthStrength1排序
 * @author Administrator
 *
 */
public class MCIStationTimesResultSortByStd implements Comparator<MCIStationTimesResult> {

	public int compare(MCIStationTimesResult o1, MCIStationTimesResult o2) {
		Double std1 = o1.getStandardValue();
		Double std2 = o2.getStandardValue();
		if(std1 < std2) return -1;
		if(std1 == std2) return 0;
		if(std1 > std2) return 1;
		return 0;
	}
	
}
