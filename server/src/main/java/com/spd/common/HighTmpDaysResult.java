package com.spd.common;

import java.util.List;

/**
 * 高温日数统计结果
 * @author Administrator
 *
 */
public class HighTmpDaysResult {
	//高温日数
	private List<HighTmpTotal> highTmpTotalList;
	//高温日期
	private List<HighTmpSequence> highTmpSequenceList;
	
	public List<HighTmpTotal> getHighTmpTotalList() {
		return highTmpTotalList;
	}
	public void setHighTmpTotalList(List<HighTmpTotal> highTmpTotalList) {
		this.highTmpTotalList = highTmpTotalList;
	}
	public List<HighTmpSequence> getHighTmpSequenceList() {
		return highTmpSequenceList;
	}
	public void setHighTmpSequenceList(List<HighTmpSequence> highTmpSequenceList) {
		this.highTmpSequenceList = highTmpSequenceList;
	}
	
}
