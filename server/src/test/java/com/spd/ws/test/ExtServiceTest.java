package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ExtServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 *极值统计
	 * @throws Exception
	 */
	@Test
	public void testExt() throws Exception {
		//EleType 要素类型定义：
//		AVGTEM 平均气温
//		AVGTEMMAX 最高气温
//		AVGTEMMIN 最低气温
//		PRETIME0808 08-08降水
//		PRETIME0820 08-20降水
//		PRETIME2008 20-08降水
//		PRETIME2020 20-20降水
//		RHUAVG 相对湿度
//		WINS2MIAVG 平均风速
//		PRSAVG 平均气压
//		SSH 日照对数
//		VISMIN 能见度
		String url = basicUrl + "/ExtStatisticsService/";
		//平均气温
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2014-03-01\", \"endTime\":\"2014-03-31\",\"isHistory\":false}");
		//平均气温，跨年
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2014-12-20\", \"endTime\":\"2015-01-03\",\"isHistory\":false}");
		//平均气温，历年同期 ,不跨年
//		testPost(url, "ext", "para", "{\"EleType\":\"AVGTEM\", \"startTime\":\"2014-10-01\", \"endTime\":\"2014-12-31\",\"isHistory\":true}");
		//平均气温，历年同期 ,跨年
		testPost(url, "ext", "para", "{\"EleType\":\"PRETIME0808\", \"startTime\":\"2016-10-31\", \"endTime\":\"2016-11-29\",\"isHistory\":true, \"stationType\":\"AWS\"}");
		//能见度
//		testPost(url, "ext", "para", "{\"EleType\":\"PRETIME0808\", \"startTime\":\"2016-07-01\", \"endTime\":\"2016-07-31\",\"isHistory\":false, \"stationType\":\"AWS\"}");
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
