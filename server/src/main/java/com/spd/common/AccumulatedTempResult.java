package com.spd.common;

import java.util.List;

/**
 * 积温结果类
 * @author Administrator
 *
 */
public class AccumulatedTempResult {
	//活动积温
	private List<ActiveAccumulatedTemp> activeAccumulatedTempList;
	//有效积温
	private List<ValidAccumulatedTemp> validAccumulatedTempList;
	
	public List<ActiveAccumulatedTemp> getActiveAccumulatedTempList() {
		return activeAccumulatedTempList;
	}
	public void setActiveAccumulatedTempList(
			List<ActiveAccumulatedTemp> activeAccumulatedTempList) {
		this.activeAccumulatedTempList = activeAccumulatedTempList;
	}
	public List<ValidAccumulatedTemp> getValidAccumulatedTempList() {
		return validAccumulatedTempList;
	}
	public void setValidAccumulatedTempList(
			List<ValidAccumulatedTemp> validAccumulatedTempList) {
		this.validAccumulatedTempList = validAccumulatedTempList;
	}
	
}
