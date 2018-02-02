package com.spd.test;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


public class HTMLTest {

	public static List<String> resultList = new ArrayList<String>();
	
	/**
	 * 
	 * @param sourceURL
	 * @return
	 */
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
			for(int i=startIndex; i<=endIndex; i++) {
				String temp = "";
				if(indexes[0].length() == 2 && i < 10) {
					temp = "0" + i;
				} else {
					temp = "" + i;
				}
				String string = startSubStr + temp + endSubStr;
				transURL(string);
			}
		} else {
			resultList.add(sourceURL);
		}
	}
	
	private void testDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		Date date = new Date();
		String string = sdf.format(date);
		System.out.println(string);
	}
	
	private void testRegex(){
//		String regex = ".*?/w[^/]*?-\\d*?\\.GIF";
		String regex = ".*?/w[^/][^-]*?\\d*?\\.GIF";
		String url = "http://tropic.ssec.wisc.edu/real-time/eastpac/winds/wg9conv4-9.GIF";
		System.out.println(url.matches(regex));
	}
	
	private void testTable() {
		Document doc = null;
		String urlAddress = "http://www.cwb.gov.tw/V7/observe/rainfall/A136.htm";
		List<String> resultImagePath = new ArrayList<String>();
		try {
			URL url = new URL(urlAddress);
			doc = Jsoup.parse(url, 60*1000);
			Element element = doc.getElementById("tableData");
			Elements elements = element.getElementsByTag("tr");
			element.getElementsByTag("tr").get(0).getElementsByTag("th").get(0).text();
		} catch (IOException e) {
		}
	}
	
	private void testTable2() {
		Document doc = null;
		String urlAddress = "http://www.cwb.gov.tw/V7/observe/rainfall/Rain_Hr/22.htm";
		List<String> resultImagePath = new ArrayList<String>();
		try {
			URL url = new URL(urlAddress);
			doc = Jsoup.parse(url, 60*1000);
			Element element = doc.getElementsByClass("tablesorter").first();
			Elements elements = element.getElementsByTag("tr");
			element.getElementsByTag("tr").get(0).getElementsByTag("th").get(0).text();
			//列数
			int colCnt =  element.getElementsByTag("tr").get(0).getElementsByTag("th").size();
			//行数
			int recordSize = element.getElementsByTag("tr").size() - 1;
			for(int i=1; i<recordSize; i++) {
				Elements elements2 = elements.get(i).getElementsByTag("td");//.get(0).text()
				String value = elements2.get(1).text();
				String stationName = elements2.get(colCnt - 1).text();
				System.out.println("StationName:" + stationName + ", value:" + value);
			}
		} catch (IOException e) {
		}
	}

	private void testTable3() {
		Document doc = null;
		String urlAddress = "http://www.cwb.gov.tw/V7/observe/rainfall/A136.htm";
		List<String> resultImagePath = new ArrayList<String>();
		try {
			URL url = new URL(urlAddress);
			doc = Jsoup.parse(url, 60*1000);
			Element element = doc.getElementsByClass("description").first();
			String time = element.getElementsByTag("td").get(3).text();
			System.out.println(time);
		} catch (IOException e) {
		}
	}
	
	private void testTable4() {
		Document doc = null;
		String urlAddress = "http://www.cwb.gov.tw/V7/observe/real/ALL.htm";
		List<String> resultImagePath = new ArrayList<String>();
		try {
			URL url = new URL(urlAddress);
			doc = Jsoup.parse(url, 60*1000);
			Element element = doc.getElementsByClass("BoxTable").first();
			String stationNum = element.getElementsByTag("tr").get(1).getElementsByTag("th").get(0).getElementsByTag("a").attr("href").split("\\.")[0];
//			String time = element.getElementsByTag("td").get(3).text();
//			System.out.println(time);
		} catch (IOException e) {
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		HTMLTest htmlTest = new HTMLTest();
		htmlTest.testTable4();
//		htmlTest.testRegex();
//		htmlTest.testRegex();
//		String sourceURL = "http://www.jma.go.jp/en/gms/imgs/6/watervapor/1/201303072200-00.png";
//		HTMLTest htmlTest = new HTMLTest();
//		htmlTest.transURL(sourceURL);
//		for(String url : resultList) {
//			System.out.println(url);
//		}
//		String verifyPath = "http://www.jma.go.jp/en/gms/imgs/{1->6}/watervapor/1/20130307{00->18}00-00.png";
//		String verifyPath = "http://www.jma.go.jp/en/gms/imgs/{1->6}/watervapor/1/20130307{00->18}00-00.png";
//		String regex = "\\{\\d*?->\\d*\\}";
//		String[] result = verifyPath.split(regex);
//		int start = 0;
//		for(int i=0; i<result.length; i++) {
//			start += result[i].length();
//			if(start >= verifyPath.length()) {
//				break;
//			}
//			int end = verifyPath.indexOf(result[i+1]);
//			String resultStr = verifyPath.substring(start, end);
//			//
//			String[] temps = resultStr.split("->");
//			int startIndex = Integer.parseInt(temps[0]);
//			int endIndex = Integer.parseInt(temps[1]);
//			boolean flag = false;
//			if(startIndex < 10 && temps[0].length()==2) {
//				flag = true;//个位数表示未03,04等
//			}
//			for(int j= startIndex; j<endIndex; j++) {
////				String result = url.replace(oldChar, newChar)
//			}
//			start += resultStr.length();
//		}
//		int start = 0;
//		if(verifyPath.indexOf("->") != -1) {
//			while(start < verifyPath.length()) {
//				int index = verifyPath.indexOf("->", start+1);
//				if(index == -1) {
//					break;
//				}
//				System.out.println(index);
//				start = index;
//			}
//		}
//		Pattern p = Pattern.compile(".*?(\\{.*?->.*?\\}).*?");
//		Matcher m = p.matcher(verifyPath);
//		boolean flag = m.matches();
//		while(m.find()) {
//			System.out.println(m.group(1));
//		}
//		System.out.println(flag);
//		Document doc = Jsoup.connect("http://www.nmc.gov.cn/publish/precipitation/1-day.htm").get();
//		String imgUrl = doc.getElementById("img_path").attr("src");
//		//获取所有的img标签
//		Elements elements = doc.getElementsByTag("a");
//		for(Element element : elements) {
//			//图片的绝对路径
//			String src = element.attr("abs:href");
//			System.out.println(src);
////			URL url = new URL(src);
////			System.out.println(url.getFile());
//		}
//		Document doc = Jsoup.connect("http://www.nmc.gov.cn/publish/precipitation/1-day.htm").get();
//		String imgUrl = doc.getElementById("img_path").attr("src");
//		//获取所有的img标签
//		Elements elements = doc.getElementsByTag("IMG");
//		for(Element element : elements) {
//			//图片的绝对路径
//			String src = element.attr("abs:src");
//			System.out.println(src);
//			URL url = new URL(src);
//			System.out.println(url.getFile());
//		}
//		Elements elements = doc.select("img[src$=.png]");
//		System.out.println(elements);
	}

}
