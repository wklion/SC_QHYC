package com.spd.common;

import java.util.List;

/**
 * 霜冻结果类
 * @author Administrator
 *
 */
public class FrostResult {
	//合计
	private List<FrostTotalResult> frostTotalResultList;
	//序列
	private List<FrostSequenceResult> frostSequenceResultList;
	
	public List<FrostTotalResult> getFrostTotalResultList() {
		return frostTotalResultList;
	}
	public void setFrostTotalResultList(List<FrostTotalResult> frostTotalResultList) {
		this.frostTotalResultList = frostTotalResultList;
	}
	public List<FrostSequenceResult> getFrostSequenceResultList() {
		return frostSequenceResultList;
	}
	public void setFrostSequenceResultList(
			List<FrostSequenceResult> frostSequenceResultList) {
		this.frostSequenceResultList = frostSequenceResultList;
	}
	
	
}
