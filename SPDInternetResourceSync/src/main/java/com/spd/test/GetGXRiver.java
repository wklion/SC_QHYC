package com.spd.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

/**
 * 获取广西水流的基本信息。
 * @author xianchao
 *
 */
public class GetGXRiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GetGXRiver getGXReservoir = new GetGXRiver();
		getGXReservoir.getInfo();
	}

	public void getInfo() {
		String url = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Water/WaterDataProvider.ashx?type=Last&sttp=RR,ZZ,DD,ZQ&start=2015%2D11%2D09%2008%3A00%3A00&end=2015%2D11%2D09%2017%3A00%3A00"; 
		try {
			testPost(url, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testPost(String urlStr, String paramName ,Object param )throws Exception{
		Document doc = null;
		try {
			URL url = new URL(urlStr);
			doc = Jsoup.parse(url, 60*1000);
			Elements eles = doc.getElementsByTag("body");
			for(int i=0; i<eles.size(); i++) {
				String content = eles.get(i).text();
//				System.out.println(content);
				creatInsertSQL(content);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void creatInsertSQL(String content) throws Exception {
		Gson gson = new Gson();
		Map map = gson.fromJson(content, Map.class);
		ArrayList list = (ArrayList) map.get("data");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("d:/xiaoshuiku.txt")));
		for(int i=0; i<list.size(); i++) {
			com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) list.get(i);
//			{ennm=老虎头水库, ennmcd=B10003450923, areacode=450923, AreaName=博白县, Region=玉林市, BCode=450900, MapX=109.8808, MapY=21.8252}
			String STNM = (String) linkedTreeMap.get("STNM");
			String STCD = (String) linkedTreeMap.get("STCD");
			String X = (String) linkedTreeMap.get("X");
			String Y = (String) linkedTreeMap.get("Y");
			String STLC = (String) linkedTreeMap.get("STLC");
			String sql = "insert into t_river(STNM, STCD, X, Y, STLC, Province) values ('" +
						STNM + "', '" + STCD + "', " + X + ", " + Y + ", '" + STLC + "', '广西');";
			bw.write(sql);
			bw.newLine();
//			System.out.println(sql);
//			System.out.println(linkedTreeMap);
//			Gson itemGson = new Gson();
//			Map itemMap = itemGson.fromJson(str, Map.class);
//			System.out.println(itemMap);
		}
		bw.close();
//		System.out.println(map);
	}
	
	
}
