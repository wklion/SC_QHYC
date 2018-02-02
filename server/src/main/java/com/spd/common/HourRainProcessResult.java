package com.spd.common;

import java.util.List;

/**
 * 小时雨量过程降水，结果类
 * @author Administrator
 *
 */
public class HourRainProcessResult {
	//逐次降水过程
	private List<HourRainSequenceResult> hourRainSequenceResultList;
	//最大降水量
	private List<HourRainMaxResult> hourRainMaxResultList;
	
	public List<HourRainSequenceResult> getHourRainSequenceResultList() {
		return hourRainSequenceResultList;
	}
	public void setHourRainSequenceResultList(
			List<HourRainSequenceResult> hourRainSequenceResultList) {
		this.hourRainSequenceResultList = hourRainSequenceResultList;
	}
	public List<HourRainMaxResult> getHourRainMaxResultList() {
		return hourRainMaxResultList;
	}
	public void setHourRainMaxResultList(
			List<HourRainMaxResult> hourRainMaxResultList) {
		this.hourRainMaxResultList = hourRainMaxResultList;
	}
	
}
