package com.spd.ws.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.spd.common.TmpDaysYearResult;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class CommonStatisticsServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 *平均气温
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemByYears() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryAvgTem", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-06-30 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2015-06-01 00:00:00\", \"contrastEndTime\":\"2016-06-30 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryAvgTem", "para", "{\"startTime\":\"2016-07-01 00:00:00\", \"endTime\":\"2016-07-28 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 高温均值统计
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMax() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		testPost(url, "queryAvgTemMax", "para", "{\"startTime\":\"2016-07-01 00:00:00\", \"endTime\":\"2016-07-10 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-08-10 00:00:00\", \"stationType\":\"ALL\"}");
//		testPost(url, "queryAvgTemMax", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-07-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 低温均值统计
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMin() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryAvgTemMin", "para", "{\"startTime\":\"2016-07-01 00:00:00\", \"endTime\":\"2016-08-10 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-08-10 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryAvgTemMin", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-08-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 降水总量统计
	 * @throws Exception
	 */
	@Test
	public void testQueryPreSum() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryPreSum", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-08-10 00:00:00\", \"type\":\"0808\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-08-10 00:00:00\",\"stationType\":\"AWS\"}");
//		testPost(url, "queryPreSum", "para", "{\"startTime\":\"2016-01-01 00:00:00\", \"endTime\":\"2016-12-31 00:00:00\", \"type\":\"2020\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-08-10 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryPreSum", "para", "{\"startTime\":\"2016-01-01 00:00:00\", \"endTime\":\"2016-12-31 00:00:00\", \"type\":\"2020\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 相对湿度统计
	 * @throws Exception
	 */
	@Test
	public void testQueryRHU() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryRHU", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-07-10 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-07-10 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryRHU", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-07-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	
	/**
	 * 平均风速
	 * @throws Exception
	 */
	@Test
	public void testQueryWin_s_2mi_avg() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryWin_s_2mi_avg", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-07-10 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-07-10 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryWin_s_2mi_avg", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-07-01 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 平均气压
	 * @throws Exception
	 */
	@Test
	public void testQueryPrsAvg() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		testPost(url, "queryPrsAvg", "para", "{\"startTime\":\"2016-07-01 00:00:00\", \"endTime\":\"2016-07-07 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-07-10 00:00:00\", \"stationType\":\"AWS\"}");
//		testPost(url, "queryPrsAvg", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-07-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":2010, \"endYear\":2014, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 极端气温
	 * @throws Exception
	 */
	@Test
	public void testQueryExtTmp() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		testPost(url, "queryExtTmp", "para", "{\"startTime\":\"2016-01-01 00:00:00\", \"endTime\":\"2016-03-10 00:00:00\", \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 能见度低值
	 * @throws Exception
	 */
	@Test
	public void testQueryVisMin() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		testPost(url, "queryVisMin", "para", "{\"startTime\":\"2016-01-01 00:00:00\", \"endTime\":\"2016-03-01 00:00:00\", \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 降水日数
	 * @throws Exception
	 */
	@Test
	public void testQueryPreCnt() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "queryPreCnt", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-08-01 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-07-10 00:00:00\", \"stationType\":\"AWS\"}");
		testPost(url, "queryPreCnt", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-06-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 高温日数
	 * @throws Exception
	 */
	@Test
	public void testQueryTmpMaxCnt() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		testPost(url, "queryTmpMaxCnt", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-06-30 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2012-07-01 00:00:00\", \"contrastEndTime\":\"2012-07-10 00:00:00\", \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 统计日照
	 * @throws Exception
	 */
	@Test
	public void testQuerySSH() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
//		testPost(url, "querySSH", "para", "{\"startTime\":\"2014-05-01 00:00:00\", \"endTime\":\"2014-05-10 00:00:00\", \"contrastType\":\"range\", \"contrastStartTime\":\"2010-05-01 00:00:00\", \"contrastEndTime\":\"2010-05-10 00:00:00\", \"stationType\":\"AWS\"}");
//		testPost(url, "querySSH", "para", "{\"startTime\":\"2016-06-01 00:00:00\", \"endTime\":\"2016-06-10 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
		testPost(url, "querySSH", "para", "{\"startTime\":\"2015-11-01 00:00:00\", \"endTime\":\"2015-11-30 00:00:00\", \"contrastType\":\"sameTeam\", \"startYear\":1981, \"endYear\":2010, \"stationType\":\"AWS\"}");
	}
	
	@Test
	public void queryTmpDaysByYear() throws Exception {
		String url = basicUrl + "/CommonStatisticsService/";
		//采暖
		testPost(url, "queryTmpDaysByYear", "para", "{\"type\":\"HEAT\", \"tmp\":\"10\", \"startYear\":1981, \"endYear\":2010, \"station_Id_C\":\"57516\"}");
		//降温
//		testPost(url, "queryTmpDaysByYear", "para", "{\"type\":\"COOL\", \"tmp\":\"26\", \"startYear\":1981, \"endYear\":2010, \"station_Id_C\":\"57516\"}");
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
