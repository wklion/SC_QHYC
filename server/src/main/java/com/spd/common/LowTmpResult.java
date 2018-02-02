package com.spd.common;

import java.util.List;

/**
 * 低温统计的合计结果
 * @author Administrator
 *
 */
public class LowTmpResult {

	//逐候统计结果
	private List<LowTmpResultHous> lowTmpResultHousResult;
	//逐次统计结果
	private List<LowTmpResultTimes>  lowTmpResultTimesListResult;
	//合计统计结果
	private List<LowTmpSequenceResult> lowTmpSequenceResult;
	
	public List<LowTmpResultHous> getLowTmpResultHousResult() {
		return lowTmpResultHousResult;
	}
	public void setLowTmpResultHousResult(
			List<LowTmpResultHous> lowTmpResultHousResult) {
		this.lowTmpResultHousResult = lowTmpResultHousResult;
	}
	public List<LowTmpResultTimes> getLowTmpResultTimesListResult() {
		return lowTmpResultTimesListResult;
	}
	public void setLowTmpResultTimesListResult(
			List<LowTmpResultTimes> lowTmpResultTimesListResult) {
		this.lowTmpResultTimesListResult = lowTmpResultTimesListResult;
	}
	public List<LowTmpSequenceResult> getLowTmpSequenceResult() {
		return lowTmpSequenceResult;
	}
	public void setLowTmpSequenceResult(
			List<LowTmpSequenceResult> lowTmpSequenceResult) {
		this.lowTmpSequenceResult = lowTmpSequenceResult;
	}
}
