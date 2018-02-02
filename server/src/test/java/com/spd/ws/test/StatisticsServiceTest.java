package com.spd.ws.test;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class StatisticsServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 * 历年同期气温查询
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemByYears", "para", "{\"startYear\":2000, \"endYear\":2010, \"startMonth\":1, \"endMonth\":1, \"startDay\":1, \"endDay\":30}");
	}
	
	/**
	 * 根据连续时段统计平均气温
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemByTimeRange", "para", "{\"startTime\":\"2016-01-01 00:00:00\", \"endTime\":\"2016-01-30 00:00:00\"}");
	}
	
	/**
	 * 根据连续时段统计高温均值
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMaxByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemMaxByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\"}");
	}
	
	/**
	 * 历年同期高温查询
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMaxByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemMaxByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 根据连续时段统计低温均值
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMinByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemMinByTimeRange", "para", "{\"startTime\":\"2016-02-10 00:00:00\", \"endTime\":\"2016-02-20 00:00:00\"}");
	}
	
	/**
	 * 历年同期低温查询
	 * @throws Exception
	 */
	@Test
	public void testQueryAvgTemMinByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryAvgTemMinByYears", "para", "{\"startYear\":2016, \"endYear\":2016, \"startMonth\":2, \"endMonth\":2, \"startDay\":10, \"endDay\":20}");
	}
	
	/**
	 * 历年同期平均风速
	 * @throws Exception
	 */
	@Test
	public void testQueryWin_s_2mi_avgByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryWin_s_2mi_avgByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":7, \"endMonth\":7, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 统计时间段内的平均风速
	 * @throws Exception
	 */
	@Test
	public void testQueryWin_s_2mi_avgByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryWin_s_2mi_avgByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\"}");
	}
	
	/**
	 * 统计时间段内的平均气压
	 * @throws Exception
	 */
	@Test
	public void testQueryPrsAvgByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryPrsAvgByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\"}");
	}
	
	/**
	 * 历年同期 平均气压
	 * @throws Exception
	 */
	@Test
	public void testQueryPrsAvgByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryPrsAvgByYears", "para", "{\"startYear\":1981, \"endYear\":2010, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 统计时段范围内的降水总量
	 * @throws Exception
	 */
	@Test
	public void testQueryPreSumByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
//		testPost(url, "queryPreSumByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\", \"type\":\"0808\"}");
		testPost(url, "queryPreSumByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\", \"type\":\"2020\"}");
	}
	
	/**
	 * 历年同期，降水总量
	 * @throws Exception
	 */
	@Test
	public void testQueryPreSumByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryPreSumByYears", "para", "{\"startYear\":1981, \"endYear\":2010, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10, \"type\":\"0808\"}");
//		testPost(url, "queryPreSumByYears", "para", "{\"startYear\":1981, \"endYear\":2010, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10, \"type\":\"2020\"}");
	}
	
	/**
	 * 统计时段范围内的日照对数
	 * @throws Exception
	 */
	@Test
	public void testQuerySSHByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "querySSHByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\"}");
	}
	
	/**
	 * 日照对数， 历年同期
	 * @throws Exception
	 */
	@Test
	public void testQuerySSHByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "querySSHByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 *  统计时段范围内相对湿度
	 * @throws Exception
	 */
	@Test
	public void testqueryRHUAvgByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryRHUAvgByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-10 00:00:00\"}");
	}
	
	/**
	 *  按年份统计相对湿度
	 * @throws Exception
	 */
	@Test
	public void testQueryRHUByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryRHUByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 按时间范围查询能见度低值
	 * @throws Exception
	 */
	@Test
	public void testQueryVisMinByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryVisMinByTimeRange", "para", "{\"startTime\":\"2014-05-01 00:00:00\", \"endTime\":\"2014-05-10 00:00:00\"}");
	}
	
	/**
	 * 历年同期查询能见度低值
	 * @throws Exception
	 */
	@Test
	public void testQueryVisMinByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryVisMinByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 按时间段，统计极端高温
	 * @throws Exception
	 */
	@Test
	public void testQueryExtMaxTmpByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryExtMaxTmpByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-25 00:00:00\"}");
	}
	
	/**
	 * 按时间段，统计极端低温
	 * @throws Exception
	 */
	@Test
	public void testQueryExtMinTmpByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryExtMinTmpByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-25 00:00:00\"}");
	}
	
	/**
	 * 历年同期，统计极端高温
	 * @throws Exception
	 */
	@Test
	public void testQueryExtMaxTmpByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryExtMaxTmpByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 历年同期，统计极端低温
	 * @throws Exception
	 */
	@Test
	public void testQueryExtMinTmpByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryExtMinTmpByYears", "para", "{\"startYear\":2010, \"endYear\":2014, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":10}");
	}
	
	/**
	 * 按时间范围统计降水日数
	 * @throws Exception
	 */
	@Test
	public void testQueryPreCntByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryPreCntByTimeRange", "para", "{\"startTime\":\"2016-02-01 00:00:00\", \"endTime\":\"2016-02-27 00:00:00\"}");
	}
	
	/**
	 * 历年同期统计降水日数
	 * @throws Exception
	 */
	@Test
	public void testQueryPreCntByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryPreCntByYears", "para", "{\"startYear\":1981, \"endYear\":2010, \"startMonth\":2, \"endMonth\":2, \"startDay\":1, \"endDay\":27}");
	}
	
	/**
	 * 按时间范围统计高温日数
	 * @throws Exception
	 */
	@Test
	public void testQueryTmpMaxCntByTimeRange() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryTmpMaxCntByTimeRange", "para", "{\"startTime\":\"2014-07-01 00:00:00\", \"endTime\":\"2014-08-01 00:00:00\"}");
	}
	
	/**
	 * 历年同期高温统计
	 * @throws Exception
	 */
	@Test
	public void testQueryTmpMaxCntByYears() throws Exception {
		String url = basicUrl + "/StatisticsService/";
		testPost(url, "queryTmpMaxCntByYears", "para", "{\"startYear\":1981, \"endYear\":2010, \"startMonth\":7, \"endMonth\":8, \"startDay\":1, \"endDay\":1}");
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
	    		result = response.getEntity(ArrayList.class);
			} catch (Exception e) {
				result = response.getEntity(HashMap.class);
			}
	    	
	    	System.out.println(result);
	    	status = 0;
	    }else if (status ==204) {
	    	System.out.println("操作成功");
	    	status = 0;
	    }		
	  
	}
}
