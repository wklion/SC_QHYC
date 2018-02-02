package com.spd.ws.sc.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SeasonServicesTest {

	private String basicUrl = "http://localhost:8080/server/scservices";
	
	private final  Client client = Client.create();
	
	/**
	 * 季节查询
	 * @throws Exception
	 */
	@Test
	public void testQueryElementsByTimes() throws Exception {
		//season取值类型：SPRING,SUMMER,AUTUMN,WINTER 分别表示春、夏、秋、冬
		String url = basicUrl + "/SeasonService/";
//		testPost(url, "getSeasonByYear", "para", "{\"year\":2000, \"startMon\":1, \"startDay\":1, \"endMon\":6, \"endDay\":30, \"season\":\"SPRING\"}");
		testPost(url, "getSeasonByYear", "para", "{\"year\":2017, \"startMon\":1, \"startDay\":1, \"endMon\":6, \"endDay\":30, \"season\":\"SUMMER\"}");
	}
	
	/**
	 * 季节单站历年查询
	 * @throws Exception
	 */
	@Test
	public void testGetSeasonByStationYears() throws Exception {
		//season取值类型：SPRING,SUMMER,AUTUMN,WINTER 分别表示春、夏、秋、冬
		String url = basicUrl + "/SeasonService/";
		testPost(url, "getSeasonByStationYears", "para", "{\"station_Id_C\":\"56186\",\"year\":2016, \"startYear\":1981, \"endYear\":2010, \"startMon\":1, \"startDay\":1, \"endMon\":6, \"endDay\":30, \"season\":\"SPRING\"}");
	}
	
	/**
	 * 查询盆地大雨开始季
	 * @throws Exception
	 */
	@Test
	public void testPendiMaxPreSeason() throws Exception {
		//season取值类型：SPRING,SUMMER,AUTUMN,WINTER 分别表示春、夏、秋、冬
		String url = basicUrl + "/SeasonService/";
		testPost(url, "pendiMaxPreSeason", "para", "{\"year\":2000, \"minPre\":25, \"station_Id_Cs\":\"56186,56187,56190,56197\"}");
	}
	
	/**
	 * 查询盆地大雨单站历年开始季
	 * @throws Exception
	 */
	@Test
	public void testPendiYearsMaxPreSeason() throws Exception {
		//season取值类型：SPRING,SUMMER,AUTUMN,WINTER 分别表示春、夏、秋、冬
		String url = basicUrl + "/SeasonService/";
		testPost(url, "pendiYearsMaxPreSeason", "para", "{\"startYear\":1981, \"endYear\":2010, \"station_Id_C\":\"56186\"}");
	}
	
	/**
	 * 查询西南雨季
	 * @throws Exception
	 */
	@Test
	public void testSouthWestRainySeason() throws Exception {
		String url = basicUrl + "/SeasonService/";
		testPost(url, "southWestRainySeason", "para", "{\"year\":1981}");
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
