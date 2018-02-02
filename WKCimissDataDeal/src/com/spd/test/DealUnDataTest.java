package com.spd.test;

import com.spd.datadeal.DealUnData;

public class DealUnDataTest {

	public static void main(String[] args) throws Exception {
		//DealUnData dud = new DealUnData();
		//dud.excute();
		String stationNum = "22525";
		Boolean result = isNumeric(stationNum.charAt(0));
		System.out.println(result);
	}
	public static boolean isNumeric(char c){
		Boolean result = false;
		if (Character.isDigit(c)){
			result = true;
		}
		return result;
	}
}
