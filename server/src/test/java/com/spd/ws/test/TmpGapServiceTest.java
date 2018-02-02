package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 气温较差
 * @author Administrator
 *
 */
public class TmpGapServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	
	/**
	 * 统计日较差
	 * @throws Exception
	 */
	@Test
	public void testGetTmpByTimes1() throws Exception {
		String url = basicUrl + "/TmpGapService/";
		testPost(url, "getTmpByTimes", "para", "{\"startTime\":\"2017-03-01\", \"endTime\": \"2017-03-03\", \"stationType\": \"AWS\", \"contrastType\": \"range\"," +
				"\"contrastStartTime\":\"2017-02-01\", \"contrastEndTime\":\"2017-02-05\"}");
	}
	
	/**
	 * 统计历年日较差
	 * @throws Exception
	 */
	@Test
	public void testGetTmpByTimes2() throws Exception {
		String url = basicUrl + "/TmpGapService/";
		testPost(url, "getTmpByTimes", "para", "{\"startTime\":\"2017-03-01\", \"endTime\": \"2017-03-03\", \"stationType\": \"AWS\", \"contrastType\": \"sameTeam\"," +
				"\"startYear\":1981, \"endYear\":2010}");
	}
	
	/**
	 * 多站单年年较差
	 * @throws Exception
	 */
	@Test
	public void testGetTmpByYear() throws Exception {
		String url = basicUrl + "/TmpGapService/";
		testPost(url, "getTmpByYear", "para", "{\"stationType\":\"AWS\", \"year\": 2016}");
	}
	
	
	/**
	 * 多站单年年较差
	 * @throws Exception
	 */
	@Test
	public void testGetTmpGapByYears() throws Exception {
		String url = basicUrl + "/TmpGapService/";
		//Station_Id_C:5%, *, 57516
		testPost(url, "getTmpGapByYears", "para", "{\"Station_Id_C\":\"5%\", \"startYear\": 1981, \"endYear\":2016, \"standardStartYear\":1981, \"standardEndYear\":2010}");
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
