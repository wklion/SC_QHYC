package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 测试获取信息中心的预报数据
 * @author Administrator
 *
 */
public class ForecastService {

	
//	private String basicUrl = "http://172.24.176.128:7080/SPMD/YBYW/showProduct/";

	private String basicUrl = "http://172.24.176.125:8080/SPMDService/services/ForcastService/";
	
	private final  Client client = Client.create();
	
	@Test
	public void testDay() throws Exception {
//		String url = basicUrl + "/TestService/";
		testPost(basicUrl, "queryForecastNextSeven", new String[]{"para"}, new String[]{"{\"endDate\":\"2016-09-15\",\"startDate\":\"2016-09-13\",\"stationNum\":\"57516\"}"});
//		testPost(basicUrl, "showSevpForeGrid", new String[]{"time", "type", "hours"}, new String[]{"20160911000000_20160912000000", "sevp", "36"});
	}
	
	
	private void testPost(String url, String method, String[] paramName ,String[] param )throws Exception{
	    WebResource webResource = client.resource(url + method); 
		MultivaluedMap formData = null;
		if (null !=param) {
		    formData = new MultivaluedMapImpl();
		    for(int i = 0; i < param.length; i++) {
		    	formData.add(paramName[i], param[i]);
		    }
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
