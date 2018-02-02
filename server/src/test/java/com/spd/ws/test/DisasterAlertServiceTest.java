package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 灾害预警查询
 * @author Administrator
 *
 */
public class DisasterAlertServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";
	//重庆服务器
//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	//外网地址
//	private String basicUrl = "http://123.57.233.58:8090/server/services";
	
	private final  Client client = Client.create();
	
	
	/**
	 *  查询当前日期的气候预警
	 * @throws Exception
	 */
	@Test
	public void testHailByRange() throws Exception {
		String url = basicUrl + "/DisasterAlertService/";
		testPost(url, "getCurrentAreaAlert", null, null);
	}
	
	/**
	 * 查询对应的单站预警
	 * @throws Exception
	 */
	@Test
	public void testGetStationAlert() throws Exception {
		String url = basicUrl + "/DisasterAlertService/";
//		testPost(url, "getStationAlert", "para", "{\"type\":\"LowTmp\", \"ForecastDate\":\"2016-09-03\"}");
		testPost(url, "getStationAlert", "para", "{\"type\":\"MCIArea\", \"ForecastDate\":\"2017-02-15\"}");
	}
	
	/**
	 * 根据预报时间，查询预报数据
	 * @throws Exception
	 */
	@Test
	public void testGetForecastByForecastTime() throws Exception {
		String url = basicUrl + "/DisasterAlertService/";
		testPost(url, "getForecastByForecastTime", "para", "{\"ForecastDate\":\"2016-02-15\"}");
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
