package com.spd.common;

import java.util.List;

/**
 * 雷暴逐次、合计结果类。
 * @author Administrator
 *
 */
public class ThundResult {
	//逐次
	private List<ThundSequenceResult> thundSequenceResultList;
	//合计
	private List<ThundTotalResult> thundTotalResultList;
	
	public List<ThundSequenceResult> getThundSequenceResultList() {
		return thundSequenceResultList;
	}
	public void setThundSequenceResultList(
			List<ThundSequenceResult> thundSequenceResultList) {
		this.thundSequenceResultList = thundSequenceResultList;
	}
	public List<ThundTotalResult> getThundTotalResultList() {
		return thundTotalResultList;
	}
	public void setThundTotalResultList(List<ThundTotalResult> thundTotalResultList) {
		this.thundTotalResultList = thundTotalResultList;
	}
	
	
}
