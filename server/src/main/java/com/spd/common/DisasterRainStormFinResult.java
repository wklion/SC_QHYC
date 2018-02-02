package com.spd.common;

import java.util.List;

public class DisasterRainStormFinResult {

	private List<DisasterRainStormResult> seqResult;
	
	private List<DisasterRainStormTotalResult> totalResult;

	public List<DisasterRainStormResult> getSeqResult() {
		return seqResult;
	}

	public void setSeqResult(List<DisasterRainStormResult> seqResult) {
		this.seqResult = seqResult;
	}

	public List<DisasterRainStormTotalResult> getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(List<DisasterRainStormTotalResult> totalResult) {
		this.totalResult = totalResult;
	}

	
}
