package com.spd.pm25.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spd.dao.impl.PM25Dao;

public class PM25Impl {

	/**
	 * 获取某一时刻点已经存在的PM25值
	 * @param timePoint
	 * @return
	 */
	public HashMap<String, Object> getExistedAQI(String timePoint) {
		PM25Dao pm25Dao = new PM25Dao();
		HashMap<String, Object> existedData = pm25Dao.getExistPM25(timePoint);
		return existedData;
	}
	
	/**
	 * 获取到所有需要同步的城市。
	 * @return
	 */
	public Set<String> getAllCites() {
		//1. 获取全部的html连接，过滤去掉重复。
		Set<String> citiesSet = new HashSet<String>();
		Document doc = null;
		try {
			URL url = new URL("http://www.pm25s.com/");
			doc = Jsoup.parse(url, 60*1000);
			Element element = doc.getElementsByClass("state").first();
			Elements linkElements = element.getElementsByTag("a");
			for(int i=0; i<linkElements.size(); i++) {
				Element linkElement = linkElements.get(i);
				String subUrl = linkElement.absUrl("href");
				citiesSet.add(subUrl);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return citiesSet;
	}
	
	/**
	 * 获取具体的城市的pm2.5
	 * @param url
	 */
	public void analyst(String urlStr) {
		HashMap<String, Object> existedData = null; 
		List insertDataList = new ArrayList();
		Document doc = null;
		try {
			URL url = new URL(urlStr);
			String citycode = url.getPath().split("\\.")[0];
			citycode = citycode.substring(1, citycode.length());
			doc = Jsoup.parse(url, 60*1000);
			//1. 取到观测时间
			Element element = doc.getElementsByClass("date").first();
			String time = element.text();
			System.out.println(time);
			int startYearIndex = time.indexOf("年");
			int startMonthIndex = time.indexOf("月");
			int startDayIndex = time.indexOf("日");
			int startHourIndex = time.indexOf("时");
			String year = time.substring(1, startYearIndex);
			String month = time.substring(startYearIndex + 1, startMonthIndex);
			String day = time.substring(startMonthIndex + 1, startDayIndex);
			String hour = time.substring(startDayIndex + 1, startHourIndex);
			month = String.format("%02d", Integer.parseInt(month));
			day = String.format("%02d", Integer.parseInt(day));
			hour = String.format("%02d", Integer.parseInt(hour));
			String observTimes = year + "-" + month + "-" + day + " " + hour + ":00:00";
			existedData = getExistedAQI(observTimes);
			
			//2 取表格中的数据 默认取第一个
			Element pm25Element = doc.getElementsByClass("pm25").first();
			Elements itemPM25Elements = pm25Element.getElementsByTag("div");
			for(int i=2; i<itemPM25Elements.size(); i++) {
				Element itemPM25 = itemPM25Elements.get(i);
				Elements spanElements = itemPM25.getElementsByTag("span");
				String stationName = spanElements.get(0).text();
				int aqi = -9999;
				try {
					aqi = Integer.parseInt(spanElements.get(1).text());
				} catch(Exception e) {
					
				}
				double pm25 = -9999;
				try {
					pm25 = Double.parseDouble(spanElements.get(2).text());
				} catch(Exception e) {
					
				}
				double pm10 = -9999;
				try {
					pm10 = Double.parseDouble(spanElements.get(3).text());
				} catch(Exception e) {
					
				}
				double co = -9999;
				try {
					co = Double.parseDouble(spanElements.get(4).text());
				} catch(Exception e) {
					
				}
				double no2 = -9999;
				try {
					no2 = Double.parseDouble(spanElements.get(5).text());
				} catch(Exception e) {
					
				}
				double so2 = -9999;
				try {
					so2 = Double.parseDouble(spanElements.get(6).text());
				} catch(Exception e) {
					
				}
				double O31HourAvg = -9999;
				try {
					O31HourAvg = Double.parseDouble(spanElements.get(7).text());
				} catch(Exception e) {
					
				}
				double O38HourAvg = -9999;
				try {
					O38HourAvg = Double.parseDouble(spanElements.get(8).text());
				} catch(Exception e) {
					
				}
				String aqiType = spanElements.get(9).text();
				if(existedData.containsKey(observTimes + "_" + stationName )) {
					continue;
				}
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("stationname", stationName);
				mapData.put("observTime", observTimes);
				mapData.put("aqi", aqi);
				mapData.put("pm25", pm25);
				mapData.put("pm10", pm10);
				mapData.put("co", co);
				mapData.put("no2", no2);
				mapData.put("so2", so2);
				mapData.put("O31HourAvg", O31HourAvg);
				mapData.put("O38HourAvg", O38HourAvg);
				mapData.put("aqiType", aqiType);
				mapData.put("citycode", citycode);
				insertDataList.add(mapData);
			}
			//入库：
			PM25Dao pm25Dao = new PM25Dao();
			pm25Dao.insertPM25Value(insertDataList);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void analyst(String[] pmResult) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		PM25Dao pm25Dao = new PM25Dao();
		for(int j=0; j<pmResult.length; j++) {
	        JsonParser parser = new JsonParser();  
	        JsonArray jsonArray = parser.parse(pmResult[j]).getAsJsonArray();  
	        if(jsonArray == null) {
	        	return;
	        }
	        int index = 0;
	        HashMap<String, Object> existData = null;
	        for (int i = 0; i < jsonArray.size(); i++) {  
	        	HashMap<String, Object> dataMap = new HashMap<String, Object>();
	            //获取第i个数组元素  
	            JsonElement el = jsonArray.get(i);  
	            JsonObject jo = el.getAsJsonObject();
	            int aqi = Integer.parseInt(jo.get("aqi").getAsString());
	            String area = jo.get("area").getAsString();
	            float pm2_5 = jo.get("pm2_5").getAsFloat();
	            float pm2_5_24h = jo.get("pm2_5_24h").getAsFloat();
	            String position_name = null;
	            try {
	            	position_name = jo.get("position_name").getAsString();
	            } catch(Exception e) {
	            	
	            }
	            String primary_pollutant = jo.get("primary_pollutant").getAsString();
	            String quality = jo.get("quality").getAsString();
	            String station_code = null;
	            try {
	            	station_code = jo.get("station_code").getAsString();
	            }catch(Exception e) {
	            	
	            }
	            String time_point = jo.get("time_point").getAsString();
	            time_point = time_point.replace("T", " ").replace("Z", ""); 
	            dataMap.put("aqi", aqi);
	            dataMap.put("area", area);
	            dataMap.put("pm2_5", pm2_5);
	            dataMap.put("pm2_5_24h", pm2_5_24h);
	            dataMap.put("position_name", position_name);
	            dataMap.put("primary_pollutant", primary_pollutant);
	            dataMap.put("quality", quality);
	            dataMap.put("station_code", station_code);
	            dataMap.put("time_point", time_point);
	            index++;
	            if(index == 1) {
	            	existData = pm25Dao.getExistPM25(time_point);
	            }
	            if(existData != null && existData.containsKey(time_point + "_" + station_code)) {
	            	continue;
	            }
	            dataList.add(dataMap);
	        }  
	        
	        pm25Dao.insertPM25Value(dataList);
		}
	}
	
	public String[] getPMResult(String[] cityes) {
		String[] results = new String[cityes.length];
		for(int i=0; i<cityes.length; i++) {
			try {
				URL url = new URL("http://www.pm25.in/api/querys/pm2_5.json?token=5j1znBVAsnSf5xQyNQyq&city=" + cityes[i]);
				URLConnection urlConnection = url.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
				String line = "";
				String result = "";
				while((line = reader.readLine()) != null) {
					result += line;
				}
				reader.close();
				results[i] = result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
}
