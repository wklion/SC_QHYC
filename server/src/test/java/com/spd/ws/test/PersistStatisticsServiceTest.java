package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class PersistStatisticsServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 持续统计
	 * @throws Exception
	 */
	@Test
	public void testPersist() throws Exception {
		//EleType 要素类型定义：
//		AVGTEM 平均气温
//		AVGTEMMAX 最高气温
//		AVGTEMMIN 最低气温
//		PRETIME0808 08-08降水
//		PRETIME0820 08-20降水
//		PRETIME2008 20-08降水
//		PRETIME2020 20-20降水
//		RHUAVG 相对湿度
//		WINS2MIAVG 平均风速
//		PRSAVG 平均气压
//		SSH 日照对数
//		VISMIN 能见度
		
		//FilterType
//		GET >=
//		GT >
//		LT <
//		LET <=
//		BETWEEN between and
		
		String url = basicUrl + "/PersistStatisticsService/";
		//平均气温
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2016-01-01\", \"endTime\":\"2016-03-02\",\"FilterType\":\"LT\", \"contrast\":10, \"stationType\":\"AWS\"}");
		//平均气温
		testPost(url, "persist", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2016-01-01\", \"endTime\":\"2016-01-31\",\"FilterType\":\"GT\", \"contrast\":10, \"station_Id_Cs\":\"*\", \"stationType\":\"ALL\"}");
		//平均气温，跨年
//		testPost(url, "persist", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2013-11-01\", \"endTime\":\"2014-01-31\",\"FilterType\":\"GT\", \"contrast\":10, \"stationIdCs\":\"*\"}");
		//平均气温，跨年，指定站
//		testPost(url, "persist", "para", "{\"EleType\":\"PRETIME0808\", \"startTime\":\"2014-03-01\", \"endTime\":\"2014-03-15\",\"FilterType\":\"GET\", \"contrast\":10, \"stationIdCs\":\"*\"}");
		// equals
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2014-03-01\", \"endTime\":\"2014-03-31\",\"FilterType\":\"EQUALS\", \"contrast\":10}");
		//跨年
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2013-12-20\", \"endTime\":\"2014-01-02\",\"FilterType\":\"LT\", \"contrast\":10}");
		
	}
	
	@Test
	public void testTmp() throws Exception {
		// type类型 UP（升温），DOWN（降温）
		String url = basicUrl + "/PersistStatisticsService/";
		//持续降温统计
		testPost(url, "tmp", "para", "{\"startTime\":\"2016-07-01\", \"endTime\":\"2016-07-31\", \"type\":\"DOWN\", \"stationType\":\"ALL\", \"stationType\":\"AWS\"}");
		
	}
	
	@Test
	public void testRain() throws Exception {
		String url = basicUrl + "/PersistStatisticsService/";
		//晴雨统计 PRETIME0808，PRETIME2020 . changeType:RAIN,SUN 统计降水,晴天
//		testPost(url, "rain", "para", "{\"startTime\":\"2013-12-01\", \"endTime\":\"2014-01-31\", \"EleType\":\"PRETIME2020\", \"changeType\":\"RAIN\"}");
		//晴统计 PRETIME0808，PRETIME2020 . changeType:RAIN,SUN 统计降水,晴天
		testPost(url, "rain", "para", "{\"startTime\":\"2016-07-01\", \"endTime\":\"2016-07-31\", \"EleType\":\"PRETIME2020\", \"changeType\":\"SUN\", \"stationType\":\"AWS\"}");
		
	}
	
	private void testPost(String url, String method, String paramName ,String param )throws Exception{
	    WebResource webResource = client.resource(url + method); 
		MultivaluedMap formData = null;
		if (null !=param) {
		    formData = new MultivaluedMapImpl();
		    formData.add(paramName, param);
		} 
	    ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
	    int status = response.getStatus();
	    if (status == 200) {
	    	Object result = null;
	    	try {
	    		result = response.getEntity(Object.class);
			} catch (Exception e) {
				result = response.getEntity(ArrayList.class);
			}
	    	
	    	System.out.println(result);
	    	status = 0;
	    }else if (status ==204) {
	    	System.out.println("操作成功");
	    	status = 0;
	    }		
	  
	}
}
