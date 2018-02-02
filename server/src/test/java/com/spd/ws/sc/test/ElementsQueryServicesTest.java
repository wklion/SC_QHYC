package com.spd.ws.sc.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ElementsQueryServicesTest {

	private String basicUrl = "http://localhost:8080/server/scservices";
	
	private final  Client client = Client.create();
	
	/**
	 * 连续时间段内的变化
	 * @throws Exception
	 */
	@Test
	public void testQueryElementsByTimes() throws Exception {
		//EleType 要素类型定义：
//		AVGTEM 平均气温
//		AVGTEMMAX 最高气温
//		AVGTEMMIN 最低气温
//		PRETIME2020 20-20降水
//		PRETIME0808 08-08降水
//		PRETIME2008 20-08降水
//		PRETIME0820 08-20降水
		String url = basicUrl + "/ElementsQueryService/";
//		testPost(url, "queryElementsByTimes", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"1998-12-01\", \"endTime\":\"1999-02-01\", \"station_Id_Cs\":\"57516\"}");
//		testPost(url, "queryElementsByTimes", "para", "{\"EleType\":\"PRETIME2020\", \"startTime\":\"1998-12-01\", \"endTime\":\"1999-02-01\", \"station_Id_Cs\":\"57516\"}");
		testPost(url, "queryElementsByTimes", "para", "{\"EleType\":\"PRETIME2020\", \"startTime\":\"1998-11-01\", \"endTime\":\"1998-12-01\", \"station_Id_Cs\":\"57516\"}");
		//选择地区，多个站
//		testPost(url, "queryElementsByTimes", "para", "{\"EleType\":\"PRETIME2020\", \"startTime\":\"1998-11-01\", \"endTime\":\"1998-12-01\", \"station_Id_Cs\":\"57516,57513,57522\"}");
	}
	
	
	/**
	 * 历年连续时间段内的变化
	 * @throws Exception
	 */
	@Test
	public void testQueryElementsByYears() throws Exception {
		//EleType 要素类型定义：
//		AVGTEM 平均气温
//		AVGTEMMAX 最高气温
//		AVGTEMMIN 最低气温
//		PRETIME2020 20-20降水
//		PRETIME0808 08-08降水
//		PRETIME2008 20-08降水
//		PRETIME0820 08-20降水
		String url = basicUrl + "/ElementsQueryService/";
//		testPost(url, "queryElementsByYears", "para", "{\"EleType\":\"PRETIME2020\", \"startTime\":\"1998-11-01\", \"endTime\":\"1998-12-01\", \"startYear\":1981," +
//		"\"endYear\":2000,\"station_Id_Cs\":\"57516,57513\"}");
		testPost(url, "queryElementsByYears", "para", "{\"EleType\":\"AVGTEMMAX\", \"startTime\":\"1998-11-01\", \"endTime\":\"1998-12-01\", \"startYear\":1981," +
			"\"endYear\":2000,\"station_Id_Cs\":\"57516\"}");
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
