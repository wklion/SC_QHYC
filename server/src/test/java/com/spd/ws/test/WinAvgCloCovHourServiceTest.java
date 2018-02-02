package com.spd.ws.test;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class WinAvgCloCovHourServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 历年同期风查询
	 * @throws Exception
	 */
	@Test
	public void testQueryWinAvg2MinByTimeRange() throws Exception {
		String url = basicUrl + "/WinAvgCloCovHourService/";
		testPost(url, "queryWinAvg2MinByTimeRange", "para", "{\"startTime\":\"2017-02-15\", \"endTime\":\"2017-02-16\", \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 历年同期云量查询
	 * @throws Exception
	 */
	@Test
	public void testQueryCloCovByTimeRange() throws Exception {
		String url = basicUrl + "/WinAvgCloCovHourService/";
		testPost(url, "queryCloCovByTimeRange", "para", "{\"startTime\":\"2017-02-15\", \"endTime\":\"2017-02-16\", \"stationType\":\"AWS\"}");
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
	    		result = response.getEntity(ArrayList.class);
			} catch (Exception e) {
				result = response.getEntity(HashMap.class);
			}
	    	
	    	System.out.println(result);
	    	status = 0;
	    }else if (status ==204) {
	    	System.out.println("操作成功");
	    	status = 0;
	    }		
	  
	}
}
