package com.spd.shuiqi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.spd.dao.impl.GXShuiQingDao;

public class GXShuiQingImpl {

	/**
	 * 获取数据
	 * @param startTime
	 * @param endTime
	 */
	public void analyst(String startTime, String endTime) {
		GXShuiQingDao gxShuiQingDao = new GXShuiQingDao();
		HashMap<String, Object> existedData = gxShuiQingDao.getExistRiverRegimenInfo(startTime, endTime);
		List insertDataList = new ArrayList();
		String urlStr = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Water/WaterDataProvider.ashx?type=Last&sttp=";//start=" + startTime + "&end=" + endTime;
		try {
			String timeStr = "RR,ZZ,DD,ZQ&" + "start=" + startTime + "&end=" + endTime;
//			timeStr = timeStr.replaceAll("=", "%3d");
			timeStr = timeStr.replaceAll(" ", "%20");
			timeStr = timeStr.replaceAll("-", "%2d");
			timeStr = timeStr.replaceAll(":", "%3a");
			timeStr = timeStr.replaceAll(",", "%2c");
			urlStr += timeStr;
//			System.out.println(urlStr + timeStr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			URL url = new URL(urlStr);
			doc = Jsoup.parse(url, 60*1000);
			Elements eles = doc.getElementsByTag("body");
			for(int i=0; i<eles.size(); i++) {
				String content = eles.get(i).text();
				Gson gson = new Gson();
				Map map = gson.fromJson(content, Map.class);
				ArrayList list = (ArrayList) map.get("data");
				for(int j=0; j<list.size(); j++) {
					com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) list.get(j);
//					{ennm=老虎头水库, ennmcd=B10003450923, areacode=450923, AreaName=博白县, Region=玉林市, BCode=450900, MapX=109.8808, MapY=21.8252}
					//站名
					String STNM = (String) linkedTreeMap.get("STNM");
					//站号
					String STCD = (String) linkedTreeMap.get("STCD");
					//时间
					String TM = (String) linkedTreeMap.get("TM");
					// 水位
					String VALStr = (String) linkedTreeMap.get("VAL");
					//警戒水位
					String WRZStr = (String) linkedTreeMap.get("WRZ");
					//保证/正常水位
					String GRZStr = (String) linkedTreeMap.get("GRZ");
					//流量
					String QStr = (String) linkedTreeMap.get("Q");
					// 涨势
					String VALTXT = (String) linkedTreeMap.get("VALTXT");
					Map<String, Object> mapData = new HashMap<String, Object>();
					mapData.put("STNM", STNM);
					mapData.put("STCD", STCD);
					mapData.put("TM", TM);
					try {
						double val = Double.parseDouble(VALStr);
						mapData.put("VAL", val);
					} catch(Exception e) {
						
					}
					try {
						double WRZ = Double.parseDouble(WRZStr);
						mapData.put("WRZ", WRZ);
					} catch(Exception e) {
						
					}
					try {
						double GRZ = Double.parseDouble(GRZStr);
						mapData.put("GRZ", GRZ);
					} catch(Exception e) {
						
					}
					try {
						double Q = Double.parseDouble(QStr);
						mapData.put("GRZ", Q);
					} catch(Exception e) {
						
					}
					if(VALTXT.endsWith("↑")) {
						mapData.put("trend", 1);
					} else if(VALTXT.endsWith("↓")) {
						mapData.put("trend", -1);
					} else if(VALTXT.endsWith("-")) {
						mapData.put("trend", 0);
					} 
					if(!existedData.containsKey(TM + "_" + STCD)) {
						insertDataList.add(mapData);
					}
				}
			}
			//入库
			gxShuiQingDao.insertRiverRegimenValue(insertDataList);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
