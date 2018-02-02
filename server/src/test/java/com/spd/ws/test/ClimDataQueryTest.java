package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ClimDataQueryTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	@Test
	public void testGetAllStations() throws Exception {
		String url = basicUrl + "/ClimDataQuery/";
		//按业务的站号顺序
//		testPost(url, "queryClimByTimesRangeAndElement", "para", "{\"startTime\":\"2016-10-01\", \"endTime\":\"2016-10-21\", \"EleType\":\"AVGTEM\", \"orderType\":\"SEQ\"}");
		//按站点的自然顺序
		testPost(url, "queryClimByTimesRangeAndElement", "para", "{\"startTime\":\"2016-10-01\", \"endTime\":\"2016-10-21\", \"EleType\":\"AVGTEM\", \"orderType\":\"STATION\"}");
		
	}
	
	@Test
	public void testQueryClimByTime() throws Exception {
		String url = basicUrl + "/ClimDataQuery/";
		//按业务的站号顺序
//		testPost(url, "queryClimByTimesRangeAndElement", "para", "{\"startTime\":\"2016-10-01\", \"endTime\":\"2016-10-21\", \"EleType\":\"AVGTEM\", \"orderType\":\"SEQ\"}");
		//按站点的自然顺序
		testPost(url, "queryClimByTime", "para", "{\"elements\":\"Station_Id_C,Station_Name,TEM_Avg,TEM_Max,TEM_Min,PRE_Time_0808,PRE_Time_2020,SSH,RHU_Avg,VIS_Min,WIN_D_Avg_2mi_C,PRS_Avg\", \"time\":\"2016-01-01\", \"orderType\":\"SEQ\"}");
		
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
