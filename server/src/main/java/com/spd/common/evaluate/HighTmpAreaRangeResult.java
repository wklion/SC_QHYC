package com.spd.common.evaluate;

import java.util.List;

/**
 * 时间段范围评估高温结果类
 * @author Administrator
 *
 */
public class HighTmpAreaRangeResult {
	//区域
	private List<HighTmpRangeAreaResult> highTmpRangeAreaResultList;
	//单站
	private List<HighTmpRangeStationResult> highTmpRangeStationResultList;
	
	public List<HighTmpRangeAreaResult> getHighTmpRangeAreaResultList() {
		return highTmpRangeAreaResultList;
	}
	public void setHighTmpRangeAreaResultList(
			List<HighTmpRangeAreaResult> highTmpRangeAreaResultList) {
		this.highTmpRangeAreaResultList = highTmpRangeAreaResultList;
	}
	public List<HighTmpRangeStationResult> getHighTmpRangeStationResultList() {
		return highTmpRangeStationResultList;
	}
	public void setHighTmpRangeStationResultList(
			List<HighTmpRangeStationResult> highTmpRangeStationResultList) {
		this.highTmpRangeStationResultList = highTmpRangeStationResultList;
	}
	
}
