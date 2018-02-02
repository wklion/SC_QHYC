package com.spd.address.impl;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.spd.tool.LogTool;

/**
 * 匹配地址的公用模块
 * @author xianchao
 *
 */
public class CommonAddressManager {
	/**
	 * 
	 * @param urlAddress url 地址，在这个地址下查找图片
	 * @param imageRegex 根据这个命名规则查找图片
	 * @return 得到符合要求的图片的完整url地址
	 */
	public static List<String> getImagePath(String urlAddress, String imageRegex, boolean UTC){
		Document doc = null;
		List<String> imageRegexList = verifyRegex(imageRegex, UTC);
		List<String> resultImagePath = new ArrayList<String>();
		for(String tempImageRegex : imageRegexList) {
	//		imageRegex = verifyRegex(imageRegex, UTC);
//			List<String> resultImagePath = new ArrayList<String>();
			Set<String> resultImagePathSet = new HashSet<String>();
			try {
	//			URL url = new URL(urlAddress);
	//			doc = Jsoup.parse(url, 60*1000);
				doc = Jsoup.connect(urlAddress).data("limit", "20").ignoreContentType(true)
			       .userAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
					.timeout(30000).post();
			} catch (IOException e) {
				LogTool.logger.error(urlAddress + " 访问失败，详细信息：" + e.getLocalizedMessage());
				return null;
			}
			//获取所有的img标签
			Elements elements = doc.getElementsByTag("IMG");
			for(Element element : elements) {
				//图片的绝对路径
				String src = element.attr("abs:src");
				if(src != null && src.matches(tempImageRegex)) {
					resultImagePathSet.add(src);
				}
			}
			//a超链接标签
			Elements elements2 = doc.getElementsByTag("a");
			for(Element element : elements2) {
				//图片的绝对路径
				String src = element.attr("abs:href");
				if(src != null && src.matches(imageRegex)) {
					resultImagePathSet.add(src);
				}
			}
			Iterator<String> it = resultImagePathSet.iterator();
			while(it.hasNext()) {
				resultImagePath.add(it.next());
			}
		}
		return resultImagePath;
	}
	
	/**
	 * 获取符合条件的超链接地址
	 * @param urlAddress
	 * @param hrefRegex
	 * @return
	 */
	public static List<String> getHrefPath(String urlAddress, String hrefRegex, boolean UTC) {
		Document doc = null;
		List<String> urlAddressList = verifyRegex(urlAddress, UTC);
		List<String> resultImagePath = new ArrayList<String>();
		for(String muUrlAddress:urlAddressList) {
	//		urlAddress = verifyRegex(urlAddress, UTC);
	//		List<String> resultImagePath = new ArrayList<String>();
			try {
	//			doc = Jsoup.connect(urlAddress).get();
				URL url = new URL(muUrlAddress);
				doc = Jsoup.parse(url, 60*1000);
			} catch (IOException e) {
	//			LogTool.logger.error(urlAddress + " 访问失败，详细信息：" + e.getLocalizedMessage());
				return null;
			}
			//获取所有的a标签
			Elements elements = doc.getElementsByTag("a");
			for(Element element : elements) {
				//图片的绝对路径
				String src = element.attr("abs:href");
				if(src != null && src.matches(hrefRegex)) {
					resultImagePath.add(src);
				}
			}
			Elements elements2 = doc.getElementsByTag("img");
			for(Element element : elements2) {
				//图片的绝对路径
				String src = element.attr("abs:src");
				if(src != null && src.matches(hrefRegex)) {
					resultImagePath.add(src);
				}
			}
		}
		return resultImagePath;
	}
	
	/**
	 * 通过图片的地址和指定的正则表达式，换成符合要求的图片地址
	 * @param path
	 * @param regex
	 * @return
	 */
	public static List<String> getValidAddress(String path, String regex, boolean UTC) {
		List<String> resultList = new ArrayList<String>();
		if(regex == null || "".equals(regex)) {
			resultList.add(path);
		}
		List<String> pathList = verifyRegex(regex, UTC);
		for(String tempPath:pathList) {
			if(path.matches(regex)) {
				resultList.add(tempPath);
			}
		}
		return resultList;
	}
	
	/**
	 * 重新构造组装图片路径
	 * @param path 图片的表达式
	 * @return
	 */
	public static List<String> getValidImgAddress(String path, boolean UTC) {
		List<String> addressList = new ArrayList<String>();
		
		List<String> choiceList = new ArrayList<String>();
		
		List<String> returnList = new ArrayList<String>();

		List<String> numList = new ArrayList<String>();
		//${num}这种情况，是为了从00->99都抓取数据，测试使用
//		for(String address : choiceList) {
//			if(address.indexOf("${num}") != -1) {
//				for(int i=0; i<100; i++) {
//					if(i < 10) {
//						returnList.add(address.replaceAll("\\$\\{num\\}", "0" + i));
//					} else {
//						returnList.add(address.replaceAll("\\$\\{num\\}", "" + i));
//					}
//				}
//			} else {
//				returnList.add(address);
//			}
//		}
		//时间处理函数
		List<String> verifyPathList = verifyRegex(path, UTC);
//		String verifyPath = verifyRegex(path, UTC);
		//${num}这种情况，是为了从00->99都抓取数据，测试使用
		for(String verifyPath:verifyPathList) {
			if(verifyPath.indexOf("${num}") != -1) {
				for(int i=0; i<100; i++) {
					if(i < 10) {
						numList.add(verifyPath.replaceAll("\\$\\{num\\}", "0" + i));
					} else {
						numList.add(verifyPath.replaceAll("\\$\\{num\\}", "" + i));
					}
				}
			} else {
				numList.add(verifyPath);
			}
		}
		for(String numStr : numList) {
			//处理1->n的这种情况
			RangeAddressManager rangeAddressManager = new RangeAddressManager();
			addressList =  rangeAddressManager.getCorrectURL(numStr);
			//处理{atlantic|eastpac|westpac|austwest|austeast}的情况，拆分成5种结果。
			for(String address : addressList) {
				ChoiceAddressManager choiceAddressManager = new ChoiceAddressManager();
				returnList.addAll(choiceAddressManager.getCorrectURL(address));
			}
		}
		return returnList;
	}
	
	private static List<String> verifyRegex(String regex, boolean UTC) {
		Date date = new Date();
		long time = date.getTime();
		if(UTC) {
			time -= 8 * 60 * 60 * 1000;
		}
		List<String> resultHH = new ArrayList<String>();
		List<String> resultDD = new ArrayList<String>();
		List<String> resultMM = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		//数据可能不会是当前时间，而是往前推算后的时间点
		if(regex.indexOf("${hh-") != -1) {
			int start = regex.indexOf("${hh-") + "${hh-".length();
			int end = regex.indexOf("}", start);
			int subHour = Integer.parseInt(regex.substring(start, end));
			SimpleDateFormat sdf = new SimpleDateFormat("HH");
			for(long startTime = time- subHour * 60 * 60 * 1000; startTime<=time; startTime+=60 * 60 * 1000) {
				date = new Date(startTime);
				String hour = sdf.format(date);
				String subString = regex.substring(regex.indexOf("${hh-"), end + 1);
				subString = subString.replace("$", "\\$");
				subString = subString.replace("{", "\\{");
				subString = subString.replace("}", "\\}");
				String temp = regex.replaceAll(subString, hour);
				resultHH.add(temp);
			}
			
		} else {
			resultHH.add(regex);
		}
		if(regex.indexOf("${dd-") != -1) {
			int start = regex.indexOf("${dd-") + "${dd-".length();
			int end = regex.indexOf("}", start);
			int subDay = Integer.parseInt(regex.substring(start, end));
//			date = new Date(time - subDay * 24 * 60 * 60 * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			for(long startTime=time - subDay * 24 * 60 * 60 * 1000; startTime<=time; startTime+=24 * 60 * 60 * 1000) {
				for(String resultStr:resultHH) {
					String day = sdf.format(startTime);
					String subString = regex.substring(regex.indexOf("${dd-"), end + 1);
					subString = subString.replace("$", "\\$");
					subString = subString.replace("{", "\\{");
					subString = subString.replace("}", "\\}");
					String temp = resultStr.replaceAll(subString, day);
					resultDD.add(temp);
				}
			}
			
		} else {
			resultDD = new ArrayList<String>(resultHH);
		}
		if(regex.indexOf("${MM-") != -1) {
			int start = regex.indexOf("${MM-") + "${MM-".length();
			int end = regex.indexOf("}", start);
			long subMonth = Integer.parseInt(regex.substring(start, end));
//			date = new Date(time - subDay * 24 * 60 * 60 * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("MM");
			for(long startTime=time - subMonth * 30 * 24 * 60 * 60 * 1000; startTime<=time; startTime += (long)30* 24 * 60 * 60 * 1000) {
				for(String resultStr:resultDD) {
					String day = sdf.format(startTime);
					String subString = regex.substring(regex.indexOf("${MM-"), end + 1);
					subString = subString.replace("$", "\\$");
					subString = subString.replace("{", "\\{");
					subString = subString.replace("}", "\\}");
					String temp = resultStr.replaceAll(subString, day);
					resultMM.add(temp);
				}
			}
			
		} else {
			resultMM = new ArrayList<String>(resultDD);
		}
		for(String resultURL:resultMM) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String fullYear = sdf.format(date);
			sdf = new SimpleDateFormat("yy");
			String partYear = sdf.format(date);
			sdf = new SimpleDateFormat("MM");
			String month = sdf.format(date);
			sdf = new SimpleDateFormat("M");
			String month2 = sdf.format(date);
			sdf = new SimpleDateFormat("dd");
			String day = sdf.format(date);
			sdf = new SimpleDateFormat("HH");
			String hour = sdf.format(date);
			resultURL = resultURL.replaceAll("\\$\\{yyyy\\}", fullYear);
			resultURL = resultURL.replaceAll("\\$\\{yy\\}", partYear);
			resultURL = resultURL.replaceAll("\\$\\{MM\\}", month);
			resultURL = resultURL.replaceAll("\\$\\{M\\}", month2);
			resultURL = resultURL.replaceAll("\\$\\{dd\\}", day);
			resultURL = resultURL.replaceAll("\\$\\{hh\\}", hour);
			result.add(resultURL);
		}
		return result;
	}
}
