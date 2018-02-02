package com.spd.address.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 针对url字符串中的{abc|def} 这种情况进行重新排列组合
 * @author xianchao
 *
 */
public class ChoiceAddressManager {

	private List<String> resultList = new ArrayList<String>();
	
	private void transURL(String sourceURL) {
		Pattern p = Pattern.compile("(\\{).*?\\|.*?(\\})");
		Matcher m = p.matcher(sourceURL);
		if(m.find()) {
			int start = m.start();
			int end = m.end();
			//匹配处
			String regex = m.group(0);
			String startSubStr = sourceURL.substring(0, start);
			String endSubStr = sourceURL.substring(end, sourceURL.length());
			regex = regex.substring(1, regex.length()-1);
			String[] indexes = regex.split("\\|");
			if(indexes.length == 1) {
				String str1 = startSubStr + indexes[0] + endSubStr;
				transURL(str1);
				String str2 = startSubStr +  endSubStr;
				transURL(str2);
			} else {
				for(int i=0; i<indexes.length; i++) {
					String str = startSubStr + indexes[i] + endSubStr;
					transURL(str);
				}
			}
		} else {
			resultList.add(sourceURL);
		}
	}
	
	public List<String> getCorrectURL(String sourceURL) {
		transURL(sourceURL);
		return resultList;
	}
	
	public static void main(String[] args) {
		String str = "http://tropic.ssec.wisc.edu/real-time/{atlantic|eastpac|westpac|austwest|austeast}/winds/wgmsdlm1{Z|}-3.GIF";
		ChoiceAddressManager choiceAddressManager = new ChoiceAddressManager();
		List<String> result = choiceAddressManager.getCorrectURL(str);
		System.out.println(result);
		
	}
}
