package com.spd.common;

import java.util.List;

/**
 * 大风的逐次和合计结果
 * @author Administrator
 *
 */
public class MaxWindResult {
	//合计结果
	private List<MaxWindRangeResult> maxWindRangeResultList;
	//逐次
	private List<MaxWindRangeResultSequence> maxWindRangeResultSequenceList;
	
	public List<MaxWindRangeResult> getMaxWindRangeResultList() {
		return maxWindRangeResultList;
	}
	public void setMaxWindRangeResultList(
			List<MaxWindRangeResult> maxWindRangeResultList) {
		this.maxWindRangeResultList = maxWindRangeResultList;
	}
	public List<MaxWindRangeResultSequence> getMaxWindRangeResultSequenceList() {
		return maxWindRangeResultSequenceList;
	}
	public void setMaxWindRangeResultSequenceList(
			List<MaxWindRangeResultSequence> maxWindRangeResultSequenceList) {
		this.maxWindRangeResultSequenceList = maxWindRangeResultSequenceList;
	}
	
	
}
