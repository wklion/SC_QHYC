package com.spd.address.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeAddressManager {
	
	private  List<String> resultList = new ArrayList<String>();
		
	private void transURL(String sourceURL) {
		Pattern p = Pattern.compile("(\\{)\\d*->\\d*(\\})");
		Matcher m = p.matcher(sourceURL);
		if(m.find()) {
			int start = m.start();
			int end = m.end();
			//匹配处
			String regex = m.group(0);
			String startSubStr = sourceURL.substring(0, start);
			String endSubStr = sourceURL.substring(end, sourceURL.length());
			regex = regex.substring(1, regex.length()-1);
			String[] indexes = regex.split("->");
			int startIndex = Integer.parseInt(indexes[0]);
			int endIndex = Integer.parseInt(indexes[1]);
			int length = (endIndex + "").length();
			for(int i=startIndex; i<=endIndex; i++) {
				String temp = "";
				int iLength = (i + "").length();
				if(iLength < length) {
					for(int j=iLength; j<length; j++) {
						temp += "0";
					}
				}
				temp += i;
//				if(indexes[0].length() >= 2 && i < 10) {
//					for(int j=0;j<indexes[0].length(); j++) {
//						temp += "0";
//					}
//					temp += i;
//				} else {
//					temp = "" + i;
//				}
				String string = startSubStr + temp + endSubStr;
				transURL(string);
			}
		} else {
			resultList.add(sourceURL);
		}
	}
	
	public List<String> getCorrectURL(String sourceURL) {
		transURL(sourceURL);
		return resultList;
	}
}
