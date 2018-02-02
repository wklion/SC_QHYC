package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RankServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 *平均气温
	 * @throws Exception
	 */
	@Test
	public void testRank() throws Exception {
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
		//StatisticsType 统计方式定义：
//		AVG 求平均 , MAX 求最大, MIN 求最小, SUM 求和, DAYS 统计日数
//		FilterType 过滤方式
//		GET >=
//		GT >
//		LT <
//		LET <=
//		BETWEEN 介于
		// Tie 是否处理并列位次
		//MissingRatio 是否处理缺测日数
		String url = basicUrl + "/RankServices/";
		//平均气温 求平均，从高往低
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":6, \"endMon\":6, \"startDay\":1, \"endDay\":1, \"startYear\":1981, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"HIGH\", \"Tie\":false, \"stationType\":\"AWS\"}");
		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":6, \"endMon\":6, \"startDay\":1, \"endDay\":1, \"startYear\":1981, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"HIGH\", \"Tie\":false, \"stationType\":\"ALL\"}");
		//平均气温 求平均，从高往低, 并列位次
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":1, \"endMon\":1, \"startDay\":1, \"endDay\":10, \"startYear\":2005, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"HIGH\", \"Tie\":true}");
		//平均气温 求平均，从低往高
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":1, \"endMon\":1, \"startDay\":1, \"endDay\":10, \"startYear\":2005, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"LOW\"}");
		//平均气温 求平均，从低往高 过滤条件 < 9 
		// TODO 返回数字如果为null，则表示无值，在显示的时候如何处理
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":1, \"endMon\":1, \"startDay\":1, \"endDay\":10, \"startYear\":2005, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"LOW\", \"FilterType\":\"LT\", \"contrast\":9}");
		//平均气温大于35
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":1, \"endMon\":1, \"startDay\":1, \"endDay\":10, \"startYear\":2005, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"LOW\", \"FilterType\":\"GT\", \"contrast\":35}");
		//跨年
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":12, \"endMon\":1, \"startDay\":20, \"endDay\":2, \"startYear\":2008, \"endYear\":2010, \"currentYear\":2011, \"sortType\":\"LOW\"}");
		// 缺测比例
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":11, \"endMon\":12, \"startDay\":20, \"endDay\":10, \"startYear\":2012, \"endYear\":2012, \"currentYear\":2012, \"sortType\":\"HIGH\", \"MissingRatio\":0.1}");
		//最低气温. 这个里面包含很多相同值的,可以测试 并列位次
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"MIN\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"LOW\"}");
		//最低气温中 求最大,并统计
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"MAX\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"LOW\"}");
		//最低气温，求最大，高位排序
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"MAX\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//最低气温，求最小，高位排序
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"MIN\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//最低气温，求最小，高位排序, 条件，最低大于3
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"MIN\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\", \"FilterType\":\"LT\", \"contrast\":3}");
		//TODO 最低气温，求最小，高位排序, 条件，最低小于3
		//最低气温，求日数，高位排序. 没有具体含义
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"DAYS\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//最低气温，求和
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEMMIN\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//日雨量，求和
//		testPost(url, "rank", "para", "{\"EleType\":\"PRETIME2020\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//日雨量，求和，过滤条件 > 3
//		testPost(url, "rank", "para", "{\"EleType\":\"PRETIME2020\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\", \"FilterType\":\"GT\", \"contrast\":3}");
		//日雨量，求和，过滤条件 > 3 && < 50
//		testPost(url, "rank", "para", "{\"EleType\":\"PRETIME2020\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\", \"FilterType\":\"BETWEEN\", \"min\":3, \"max\":50}");
		//日雨量，求和，过滤条件 > 3 && < 50 冬季，跨年
//		testPost(url, "rank", "para", "{\"EleType\":\"PRETIME2020\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":12, \"endMon\":2, \"startDay\":1, \"endDay\":29, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\", \"FilterType\":\"BETWEEN\", \"min\":3, \"max\":50}");
		//相对湿度
//		testPost(url, "rank", "para", "{\"EleType\":\"RHUAVG\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//平均风速
//		testPost(url, "rank", "para", "{\"EleType\":\"WINS2MIAVG\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//日照对数
//		testPost(url, "rank", "para", "{\"EleType\":\"SSH\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//平均气压
//		testPost(url, "rank", "para", "{\"EleType\":\"PRSAVG\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
		//能见度
//		testPost(url, "rank", "para", "{\"EleType\":\"VISMIN\", \"StatisticsType\":\"SUM\", \"FilterType\":\"\", \"startMon\":3, \"endMon\":5, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2014, \"currentYear\":2010, \"sortType\":\"HIGH\"}");
//		testPost(url, "rank", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\", \"startMon\":2, \"endMon\":2, \"startDay\":1, \"endDay\":7, \"startYear\":1951, \"endYear\":2010, \"currentYear\":2016, \"sortType\":\"HIGH\"}");
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
