package com.spd.common.evaluate;

import java.util.Comparator;

/**
 * 按singleSynthStrength1排序
 * @author Administrator
 *
 */
public class MCIStationTimesResultSortSum implements Comparator<MCIStationTimesResult> {

	public int compare(MCIStationTimesResult o1, MCIStationTimesResult o2) {
		Double sumStrength1 = o1.getSumStrength();
		Double sumStrength2 = o2.getSumStrength();
		if(sumStrength1 < sumStrength2) return -1;
		if(sumStrength1 == sumStrength2) return 0;
		if(sumStrength1 > sumStrength2) return 1;
		return 0;
	}
	
}
