package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class FirstDayServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 初日统计
	 * @throws Exception
	 */
	@Test
	public void testFirstDay() throws Exception {
		//EleType 要素类型定义：
//		AVGTEMMAX 最高气温
//		PRETIME0808 08-08降水
//		PRETIME2020 20-20降水
		String url = basicUrl + "/FirstDayService/";
		// 08-08
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"PRETIME0808\", \"value\":25, \"year\":2014}");
		// 20-20
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"PRETIME2020\", \"value\":25, \"year\":2014}");
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"PRETIME2020\", \"value\":50, \"year\":2014, \"startMon\":1, \"startDay\":1, \"endMon\":12, \"endDay\":31}");
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"PRETIME2020\", \"value\":50, \"year\":2014, \"startMon\":3, \"startDay\":22, \"endMon\":12, \"endDay\":31}");
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"AVGTEMMAX\", \"value\":50, \"year\":2016, \"startMon\":1, \"startDay\":1, \"endMon\":12, \"endDay\":31, \"constatStartYear\":1981, \"constatEndYear\":2010}");
		testPost(url, "rainTmpFirst", "para", "{\"type\":\"AVGTEMMAX\", \"value\":50, \"year\":2016, \"startMon\":1, \"startDay\":1, \"endMon\":12, \"endDay\":31}");
//		高温统计
//		testPost(url, "rainTmpFirst", "para", "{\"type\":\"AVGTEMMAX\", \"value\":35, \"year\":2014}");
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
