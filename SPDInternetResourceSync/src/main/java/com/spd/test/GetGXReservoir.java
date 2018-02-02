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
 * 获取广西水库的基本信息。
 * @author xianchao
 *
 */
public class GetGXReservoir {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GetGXReservoir getGXReservoir = new GetGXReservoir();
		getGXReservoir.getInfo();
	}

	public void getInfo() {
//		String url = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Reservoir/GetSite.ashx?encl=%E2%85%A0"; // 大型
//		String url = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Reservoir/GetSite.ashx?encl=%E2%85%A2"; // 中型
		String url = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Reservoir/GetSite.ashx?encl=%E2%85%A3"; // 小型
//		String url = "http://www.gxwater.gov.cn/Publish/Public/DataAccess/Reservoir/GetSite.ashx?encl=%E2%85%A4"; // 小型
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
//		Client client = Client.create();
//		WebResource webResource = client.resource(url); 
//		MultivaluedMap formData = null;
//		if (null !=param) {
//		    formData = new MultivaluedMapImpl();
//		    formData.add(paramName, param);
//		} 
//	    ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
//	    int status = response.getStatus();
//	    if (status == 200) {
//	    	System.out.println(response.getEntity(String.class));
//	    	status = 0;
//	    }else if (status ==204) {
//	    	System.out.println("操作成功！");
//	    	status = 0;
//	    }				
	  
	}
	
	private void creatInsertSQL(String content) throws Exception {
		Gson gson = new Gson();
		Map map = gson.fromJson(content, Map.class);
		ArrayList list = (ArrayList) map.get("data");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("d:/xiaoshuiku.txt")));
		for(int i=0; i<list.size(); i++) {
			com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) list.get(i);
//			{ennm=老虎头水库, ennmcd=B10003450923, areacode=450923, AreaName=博白县, Region=玉林市, BCode=450900, MapX=109.8808, MapY=21.8252}
			String ennm = (String) linkedTreeMap.get("ennm");
			String ennmcd = (String) linkedTreeMap.get("ennmcd");
			String areacode = (String) linkedTreeMap.get("areacode");
			String AreaName = (String) linkedTreeMap.get("AreaName");
			String Region = (String) linkedTreeMap.get("Region");
			String BCode = (String) linkedTreeMap.get("BCode");
			String MapX = (String) linkedTreeMap.get("MapX");
			String MapY = (String) linkedTreeMap.get("MapY");
			String sql = "insert into t_reservoir(ennm, ennmcd, areacode, AreaName, Region, BCode, MapX, MapY, type) values ('" + ennm + "'," +
						"'" + ennmcd + "', '" + areacode + "', '" + AreaName + "', '" + Region + "', '" + BCode + "', " + MapX + ", " + 
						MapY + ", '小型');";
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
