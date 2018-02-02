package com.spd.common;

import java.util.List;

/**
 * 合计、逐次统计结果
 * @author Administrator
 *
 */
public class FogResult {
	//合计
	private List<FogResultTotal> fogResultTotalList;
	//雾逐次
	private List<FogSequenceResult> fogSequenceResultList;
	//轻雾逐次
	private List<MistSequenceResult> mistSequenceResultList;
	
	public List<FogResultTotal> getFogResultTotalList() {
		return fogResultTotalList;
	}
	public void setFogResultTotalList(List<FogResultTotal> fogResultTotalList) {
		this.fogResultTotalList = fogResultTotalList;
	}
	public List<FogSequenceResult> getFogSequenceResultList() {
		return fogSequenceResultList;
	}
	public void setFogSequenceResultList(
			List<FogSequenceResult> fogSequenceResultList) {
		this.fogSequenceResultList = fogSequenceResultList;
	}
	public List<MistSequenceResult> getMistSequenceResultList() {
		return mistSequenceResultList;
	}
	public void setMistSequenceResultList(
			List<MistSequenceResult> mistSequenceResultList) {
		this.mistSequenceResultList = mistSequenceResultList;
	}
	
}
