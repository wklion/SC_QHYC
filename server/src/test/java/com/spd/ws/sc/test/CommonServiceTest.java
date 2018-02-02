package com.spd.ws.sc.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class CommonServiceTest {

	private String basicUrl = "http://localhost:8080/server/scservices";
	
	private final  Client client = Client.create();
	
	/**
	 * 盆地站点查询
	 * @throws Exception
	 */
	@Test
	public void testQueryElementsByTimes() throws Exception {
		//season取值类型：SPRING,SUMMER,AUTUMN,WINTER 分别表示春、夏、秋、冬
		String url = basicUrl + "/CommonService/";
		testPost(url, "queryPenDiStations", null, null);
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
