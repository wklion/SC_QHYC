package com.spd.common;

import java.util.List;

/**
 * 高温时间段统计结果参数
 * @author Administrator
 *
 */
public class HighTmpResult {
	//逐次
	private List<HighTmpRangeResultSequence> highTmpRangeResultSequenceList;
	//合计
	private List<HighTmpRangeResult> highTmpRangeResultList;
	
	public List<HighTmpRangeResultSequence> getHighTmpRangeResultSequenceList() {
		return highTmpRangeResultSequenceList;
	}
	public void setHighTmpRangeResultSequenceList(
			List<HighTmpRangeResultSequence> highTmpRangeResultSequenceList) {
		this.highTmpRangeResultSequenceList = highTmpRangeResultSequenceList;
	}
	public List<HighTmpRangeResult> getHighTmpRangeResultList() {
		return highTmpRangeResultList;
	}
	public void setHighTmpRangeResultList(
			List<HighTmpRangeResult> highTmpRangeResultList) {
		this.highTmpRangeResultList = highTmpRangeResultList;
	}
	
	
}
