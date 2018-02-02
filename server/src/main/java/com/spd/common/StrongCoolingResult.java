package com.spd.common;

import java.util.List;

/**
 * 按时间段统计强降温的结果统计类
 * @author Administrator
 *
 */
public class StrongCoolingResult {
	//逐次
	private List<StrongCoolingSequenceResult> strongCoolingSequenceResultList;
	//合计
	private List<StrongCoolingTotalResult> strongCoolingTotalResultList;
	
	public List<StrongCoolingSequenceResult> getStrongCoolingSequenceResultList() {
		return strongCoolingSequenceResultList;
	}
	public void setStrongCoolingSequenceResultList(
			List<StrongCoolingSequenceResult> strongCoolingSequenceResultList) {
		this.strongCoolingSequenceResultList = strongCoolingSequenceResultList;
	}
	public List<StrongCoolingTotalResult> getStrongCoolingTotalResultList() {
		return strongCoolingTotalResultList;
	}
	public void setStrongCoolingTotalResultList(
			List<StrongCoolingTotalResult> strongCoolingTotalResultList) {
		this.strongCoolingTotalResultList = strongCoolingTotalResultList;
	}
	
}
