package com.spd.common;

import java.util.List;

/**
 * 合计、逐次统计结果
 * @author Administrator
 *
 */
public class SnowResult {
	//合计
	private List<SnowResultTotal> snowResultTotalList;
	//逐次
	private List<SnowSequenceResult> snowSequenceResultList;
	
	public List<SnowResultTotal> getSnowResultTotalList() {
		return snowResultTotalList;
	}
	public void setSnowResultTotalList(List<SnowResultTotal> snowResultTotalList) {
		this.snowResultTotalList = snowResultTotalList;
	}
	public List<SnowSequenceResult> getSnowSequenceResultList() {
		return snowSequenceResultList;
	}
	public void setSnowSequenceResultList(
			List<SnowSequenceResult> snowSequenceResultList) {
		this.snowSequenceResultList = snowSequenceResultList;
	}
	
}
