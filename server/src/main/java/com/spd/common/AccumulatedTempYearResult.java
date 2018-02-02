package com.spd.common;

import java.util.List;

/**
 * 积温结果类,年值
 * @author Administrator
 *
 */
public class AccumulatedTempYearResult {
	//活动积温
	private List<ActiveAccumulatedYearTemp> activeAccumulatedTempList;
	//有效积温
	private List<ValidAccumulatedYearTemp> validAccumulatedTempList;
	
	public List<ActiveAccumulatedYearTemp> getActiveAccumulatedTempList() {
		return activeAccumulatedTempList;
	}
	public void setActiveAccumulatedTempList(
			List<ActiveAccumulatedYearTemp> activeAccumulatedTempList) {
		this.activeAccumulatedTempList = activeAccumulatedTempList;
	}
	public List<ValidAccumulatedYearTemp> getValidAccumulatedTempList() {
		return validAccumulatedTempList;
	}
	public void setValidAccumulatedTempList(
			List<ValidAccumulatedYearTemp> validAccumulatedTempList) {
		this.validAccumulatedTempList = validAccumulatedTempList;
	}
	
}
