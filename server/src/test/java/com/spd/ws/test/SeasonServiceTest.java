package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 季节服务
 * @author Administrator
 *
 */
public class SeasonServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 统计历史的季节
	 * @throws Exception
	 */
//	@Test
//	public void testExt() throws Exception {
//		//SPRING 
//		String url = basicUrl + "/SeasonService/";
////		春
////		testPost(url, "getHisSeason", "para", "{\"season\":\"SPRING\"}");
////		夏
////		testPost(url, "getHisSeason", "para", "{\"season\":\"SUMMER\"}");
////		秋
////		testPost(url, "getHisSeason", "para", "{\"season\":\"AUTUMN\"}");
////		冬
//		testPost(url, "getHisSeason", "para", "{\"season\":\"WINTER\"}");
//	}
	
	/**
	 * 统计季节
	 * @throws Exception
	 */
	@Test
	public void testGetSeasonByYear() throws Exception {
		String url = basicUrl + "/SeasonService/";
		// 春 startMon = 0, startDay = 0, endMon = 0, endDay 
		testPost(url, "getSeasonByYear", "para", "{\"season\":\"SPRING\", \"year\":1969, \"startMon\":1, \"startDay\":1, \"endMon\":6, \"endDay\":30}");
		// 夏
//		testPost(url, "getSeasonByYear", "para", "{\"season\":\"SUMMER\", \"year\":2014, \"startMon\":4, \"startDay\":1, \"endMon\":9, \"endDay\":30}");
		// 秋
//		testPost(url, "getSeasonByYear", "para", "{\"season\":\"AUTUMN\", \"year\":2014, \"startMon\":7, \"startDay\":1, \"endMon\":12, \"endDay\":31}");
		//冬
//		testPost(url, "getSeasonByYear", "para", "{\"season\":\"WINTER\", \"year\":2015, \"startMon\":10, \"startDay\":1, \"endMon\":3, \"endDay\":1}");
	}
	
	/**
	 * 统计单站历年季节
	 * @throws Exception
	 */
	@Test
	public void testGetSeasonByStationAndYears() throws Exception {
		String url = basicUrl + "/SeasonService/";
		// 春 startMon = 0, startDay = 0, endMon = 0, endDay 
//		testPost(url, "getSeasonByStationAndYears", "para", "{\"season\":\"SPRING\", \"startYear\":1981, \"endYear\":2010, \"startMon\":1, \"startDay\":1, \"endMon\":6, \"endDay\":30, \"station_Id_C\":\"57516\"}");
		// 夏
//		testPost(url, "getSeasonByYear", "para", "{\"season\":\"SUMMER\", \"year\":2014, \"startMon\":4, \"startDay\":1, \"endMon\":9, \"endDay\":30}");
		// 秋
//		testPost(url, "getSeasonByYear", "para", "{\"season\":\"AUTUMN\", \"year\":2014, \"startMon\":7, \"startDay\":1, \"endMon\":12, \"endDay\":31}");
		//冬
		testPost(url, "getSeasonByStationAndYears", "para", "{\"season\":\"WINTER\", \"startYear\":1981, \"endYear\":2010, \"startMon\":10, \"startDay\":1, \"endMon\":3, \"endDay\":1, \"station_Id_C\":\"57516\"}");
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
