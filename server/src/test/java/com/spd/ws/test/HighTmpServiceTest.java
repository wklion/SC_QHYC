package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 高温日数
 * @author Administrator
 *
 */
public class HighTmpServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 高温日数
	 * @throws Exception
	 */
	@Test
	public void testHighTmpByTimes() throws Exception {
		String url = basicUrl + "/HighTmpService/";
//		testPost(url, "highTmpByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-30\", \"type\":\"AWS\"}");
//		testPost(url, "highTmpByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-30\", \"type\":\"MWS\"}");
		testPost(url, "highTmpByTimes", "para", "{\"startTimeStr\":\"2016-08-01\", \"endTimeStr\":\"2016-08-31\"}");
		
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
