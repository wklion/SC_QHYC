package com.spd.util;

public class CommonUtil {
	/**
	 * @����:wangkun
	 * @����:2017��10��22��
	 * @��˾:spd
	 * @˵��:�Ƿ�������
	*/
	public static boolean isNumeric(char c){
		Boolean result = false;
		if (Character.isDigit(c)){
			result = true;
		}
		return result;
	}
}
