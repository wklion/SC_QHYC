package com.spd.shuiqi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.spd.dao.impl.GXShuiKuDao;

public class GXShuiKuImpl {

	/**
	 * 分析水库信息
	 * @param bgTime
	 * @param edTime
	 */
	public void analyst(String bgTime, String edTime) {
		GXShuiKuDao gxShuiKuDao = new GXShuiKuDao();
		HashMap<String, Object> existedData = gxShuiKuDao.getExistRiverRegimenInfo(bgTime, edTime);
		List insertDataList = new ArrayList();
		String urlStr = "http://www.gxwater.gov.cn/Publish/Reservoir/BLL/AjaxHandle/RsOperationReg.ashx?page=1&rp=50&sortname=stnm&sortorder=asc&query=&qtype=&";//start=" + startTime + "&end=" + endTime;
		try {
			String timeStr = "bgTime=" + bgTime + "&edTime=" + edTime;
//			timeStr = timeStr.replaceAll("=", "%3d");
			timeStr = timeStr.replaceAll(" ", "%20");
			timeStr = timeStr.replaceAll("-", "%2d");
			timeStr = timeStr.replaceAll(":", "%3a");
			timeStr = timeStr.replaceAll(",", "%2c");
			urlStr += timeStr;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			URL url = new URL(urlStr);
			doc = Jsoup.parse(url, 60*1000);
			Elements eles = doc.getElementsByTag("body");
			for(int i=0; i<eles.size(); i++) {
				Element bodyElement = eles.get(i);
				String content = eles.get(i).text();
				Gson gson = new Gson();
				Map map = gson.fromJson(content, Map.class);
				ArrayList list = (ArrayList) map.get("rows");
				for(int j=0; j<list.size(); j++) {
					com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) list.get(j);
					System.out.println(linkedTreeMap);
					ArrayList cell = (java.util.ArrayList) linkedTreeMap.get("cell");
					Map<String, Object> mapData = new HashMap<String, Object>();
					String reservoirName  = (String) cell.get(0);
					mapData.put("reservoirName", reservoirName);
					mapData.put("DT", bgTime);
					mapData.put("location", cell.get(1));
					// 溢洪道高程（m）
					try {
						double spillwayheight = Double.parseDouble((String)cell.get(2));
						mapData.put("spillwayheight", spillwayheight);
					} catch(Exception e) {
						
					}
					// 汛限水位（m）
					try {
						double floodLevel = Double.parseDouble((String)cell.get(3));
						mapData.put("floodLevel", floodLevel);
					} catch(Exception e) {
						
					}
					// 日降水量（m）
					try {
						double dayPrecipitation = Double.parseDouble((String)cell.get(4));
						mapData.put("dayPrecipitation", dayPrecipitation);
					} catch(Exception e) {
						
					}
					// 入库流量（m3/s）
					try {
						double inFlow = Double.parseDouble((String)cell.get(5));
						mapData.put("inFlow", inFlow);
					} catch(Exception e) {
						
					}
					// 库水位
					try {
						double waterLevel = Double.parseDouble((String)cell.get(6));
						mapData.put("inFlow", waterLevel);
					} catch(Exception e) {
						
					}
					// 水势
					try {
						String flowwater = (String)cell.get(7);
						if("—".equals(flowwater)) {
							mapData.put("flowwater", 0);
						} else if("↑".equals(flowwater)) {
							mapData.put("flowwater", 1);
						} else if("↓".equals(flowwater)) {
							mapData.put("flowwater", -1);
						} 
					} catch(Exception e) {
						
					}
					// 蓄水量（亿m3）
					try {
						double waterstorage = Double.parseDouble((String)cell.get(8));
						mapData.put("waterstorage", waterstorage);
					} catch(Exception e) {
						
					}
					
					//出库流量（m3/s）
					try {
						double outFlow = Double.parseDouble((String)cell.get(9));
						mapData.put("outFlow", outFlow);
					} catch(Exception e) {
						
					}
					if(!existedData.containsKey(bgTime + "_" + reservoirName)) {
						insertDataList.add(mapData);
					}
					
				}
			}
			gxShuiKuDao.insertRiverRegimenValue(insertDataList);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
