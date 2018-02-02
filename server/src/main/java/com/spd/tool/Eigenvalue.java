package com.spd.tool;

/**
 * 特征值的处理
 * @author Administrator
 *
 */
public class Eigenvalue {
	
	/**
	 * 处理特征值 （暂时只处理降水）
	 * @param preValue 原值
	 * @return
	 */
	public static synchronized Double dispose(Double preValue) {
		if(preValue == null) return null;
		if(preValue > 999990) return null;
		if(preValue > 999600 && preValue < 999700) return preValue - 999600;
		if(preValue > 999700 && preValue < 999800) return preValue - 999700;
		if(preValue == 999990) return 0.1;
		if(preValue >= 99999 || preValue <= -99999) return null;
		return preValue;
	}
}
