package com.spd.util;

public class CommonUtil {
	/**
	 * @作者:wangkun
	 * @日期:2017年10月22日
	 * @公司:spd
	 * @说明:是否是数字
	*/
	public static boolean isNumeric(char c){
		Boolean result = false;
		if (Character.isDigit(c)){
			result = true;
		}
		return result;
	}
}
