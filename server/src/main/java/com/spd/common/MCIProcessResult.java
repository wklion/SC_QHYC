package com.spd.common;

import java.util.List;

/**
 * 干旱过程结果类
 * @author Administrator
 *
 */
public class MCIProcessResult {
	//过程
	private List<MCIProcessSequenceResult> mciProcessSequenceResultList;
	//次数
	private List<MCIProcessTotalResult> mciProcessTotalResultList;
	
	public List<MCIProcessSequenceResult> getMciProcessSequenceResultList() {
		return mciProcessSequenceResultList;
	}
	public void setMciProcessSequenceResultList(
			List<MCIProcessSequenceResult> mciProcessSequenceResultList) {
		this.mciProcessSequenceResultList = mciProcessSequenceResultList;
	}
	public List<MCIProcessTotalResult> getMciProcessTotalResultList() {
		return mciProcessTotalResultList;
	}
	public void setMciProcessTotalResultList(
			List<MCIProcessTotalResult> mciProcessTotalResultList) {
		this.mciProcessTotalResultList = mciProcessTotalResultList;
	}
	
	
}
