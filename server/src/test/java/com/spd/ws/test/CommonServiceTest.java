package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class CommonServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 获取站点
	 * @throws Exception
	 */
	@Test
	public void testDay() throws Exception {
		String url = basicUrl + "/CommonService/";
		testPost(url, "getStationsByLevel", "para", "{\"level\":1}");
	}
	
	/**
	 * 获取站点
	 * @throws Exception
	 */
	@Test
	public void testTest() throws Exception {
		String url = basicUrl + "/CommonService/";
		testPost(url, "test", null, null);
		
	}
	
	/**
	 * 获取全部的国家城市站
	 * @throws Exception
	 */
	@Test
	public void testGetAllNationCityStations() throws Exception {
		String url = basicUrl + "/CommonService/";
		testPost(url, "getAllNationCityStations", null, null);
		
	}
	
	/**
	 * 获取全部气象站（包括国家站、区域站）
	 * @throws Exception
	 */
	@Test
	public void testGetAllStations() throws Exception {
		String url = basicUrl + "/CommonService/";
		testPost(url, "getAllStations", null, null);
		
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
