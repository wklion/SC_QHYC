package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TestServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	
	@Test
	public void testDay() throws Exception {
		String url = basicUrl + "/TestService/";
		//日
//		testPost(url, "testRange", "para", "{\"startStr\":\"20150601\", \"endStr\":\"20150803\", \"climTypeStr\":\"DAY\"}");
		//年
//		testPost(url, "testRange", "para", "{\"startStr\":\"20130601\", \"endStr\":\"20150803\", \"climTypeStr\":\"YEAR\"}");
		//月
//		testPost(url, "testRange", "para", "{\"startStr\":\"20160504\", \"endStr\":\"20160803\", \"climTypeStr\":\"MONTH\"}");
		//候
//		testPost(url, "testRange", "para", "{\"startStr\":\"20160504\", \"endStr\":\"20160803\", \"climTypeStr\":\"FIVEDAYS\"}");
//		testPost(url, "testRange", "para", "{\"startStr\":\"20151204\", \"endStr\":\"20160108\", \"climTypeStr\":\"FIVEDAYS\"}");
		//旬
		testPost(url, "testRange", "para", "{\"startStr\":\"20160504\", \"endStr\":\"20160803\", \"climTypeStr\":\"TENDAYS\"}");
		//季
//		testPost(url, "testRange", "para", "{\"startStr\":\"20160104\", \"endStr\":\"20160803\", \"climTypeStr\":\"SEASON\"}");
//		testPost(url, "testRange", "para", "{\"startStr\":\"20140104\", \"endStr\":\"20160803\", \"climTypeStr\":\"SEASON\"}");
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
