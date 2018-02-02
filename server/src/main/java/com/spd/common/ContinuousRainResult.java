package com.spd.common;

import java.util.List;

/**
 * 连阴雨的统计结果，包括合计，逐次数据
 * @author Administrator
 *
 */
public class ContinuousRainResult {

	//逐次
	private List<ContinuousRainSequenceResult> sequenceList;
	//合计
	private List<ContinuousRainContrastResult> contrastList;
	
	public List<ContinuousRainSequenceResult> getSequenceList() {
		return sequenceList;
	}
	public void setSequenceList(List<ContinuousRainSequenceResult> sequenceList) {
		this.sequenceList = sequenceList;
	}
	public List<ContinuousRainContrastResult> getContrastList() {
		return contrastList;
	}
	public void setContrastList(List<ContinuousRainContrastResult> contrastList) {
		this.contrastList = contrastList;
	}
	
}
