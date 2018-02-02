package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
/**
 * 历年同期测试代码
 * @author Administrator
 *
 */
public class SameCalendarServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 *平均气温
	 * @throws Exception
	 */
	@Test
	public void testSame() throws Exception {
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
//		TEMGAP 温度日较差
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
		String url = basicUrl + "/SameCalendarService/";
		//平均气温 求平均
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57345\", \"startMon\":1, \"endMon\":1, \"startDay\":1, \"endDay\":31, \"startYear\":1956, \"endYear\":1956, \"standardStartYear\":1956, \"standardEndYear\":1956, \"groupByStation\":\"true\"}");
		testPost(url, "same", "para", "{\"EleType\":\"TEMGAP\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57516,57518,57511,57513,57409,57502,57505,57506,57510,57512,57514,57509,57517,57519,57612,57425,57520,57522,57523,57525,57536,57537,57633,57635,57438,57333,57338,57339,57345,57348,57349,57426,57432,57437\", \"startMon\":2, \"endMon\":2, \"startDay\":1, \"endDay\":7, \"startYear\":1951, \"endYear\":2017, \"standardStartYear\":1981, \"standardEndYear\":2010}");
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57348\", \"startMon\":3, \"endMon\":3, \"startDay\":1, \"endDay\":21, \"startYear\":1951, \"endYear\":1960, \"standardStartYear\":1951, \"standardEndYear\":1960}");
		//按月份过滤
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"5%\", \"monthes\":\"1,2,3\", \"startYear\":1951, \"endYear\":2010, \"standardStartYear\":1981, \"standardEndYear\":2010}");
		//按年代的方式：resultDisplayType = 2 表示按年代。 resultDisplayType = 1 或者不加resultDisplayType参数，表示默认，按年的方式
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"*\", \"startMon\":3, \"endMon\":3, \"startDay\":1, \"endDay\":21, \"startYear\":1951, \"endYear\":2015, \"standardStartYear\":1981, \"standardEndYear\":2010, \"resultDisplayType\":2}");
		// 过滤条件
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"GT\", \"contrast\":5, \"station_Id_C\":\"*\", \"startMon\":3, \"endMon\":3, \"startDay\":1, \"endDay\":21, \"startYear\":1951, \"endYear\":2010, \"standardStartYear\":1981, \"standardEndYear\":2010}");
		//平均气温,指定站定
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57516\", \"startMon\":3, \"endMon\":3, \"startDay\":1, \"endDay\":21, \"startYear\":1951, \"endYear\":2010, \"standardStartYear\":1981, \"standardEndYear\":2010}");
//		testPost(url, "same", "para", "{\"EleType\":\"PRETIME2020\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57516\", \"startMon\":3, \"endMon\":3, \"startDay\":1, \"endDay\":1, \"startYear\":1951, \"endYear\":2010, \"standardStartYear\":1981, \"standardEndYear\":2010}");
//		跨年，平均气温 求平均
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"*\", \"startMon\":12, \"endMon\":1, \"startDay\":30, \"endDay\":5, \"startYear\":1951, \"endYear\":2010, \"standardStartYear\":1981, \"standardEndYear\":2010}");
		//年代
//		testPost(url, "same", "para", "{\"startYear\":1951,\"endYear\":2016,\"FilterType\":\"\",\"EleType\":\"AVGTEM\",\"StatisticsType\":\"AVG\",\"standardStartYear\":1981,\"standardEndYear\":2010,\"station_Id_C\":\"5%\",\"startDay\":1,\"startMon\":1,\"endDay\":31,\"endMon\":12,\"resultDisplayType\":2, \"groupByStation\":\"true\"}");
//		testPost(url, "same", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57516\", \"startMon\":1, \"endMon\":12, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2016, \"standardStartYear\":1981, \"standardEndYear\":2010}");
	}
	
	/**
	 * 重现期用到的历年同期，每个站分别计算历年同期
	 * @throws Exception
	 */
	@Test
	public void testSameBatch() throws Exception {
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
//		TEMGAP 温度日较差
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
		String url = basicUrl + "/SameCalendarService/";
		//平均气温 求平均
		testPost(url, "sameBatch", "para", "{\"EleType\":\"TEMGAP\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"57516,57518,57511,57513,57409,57502,57505,57506,57510,57512,57514,57509,57517,57519,57612,57425,57520,57522,57523,57525,57536,57537,57633,57635,57438,57333,57338,57339,57345,57348,57349,57426,57432,57437\"," +
				" \"startMon\":2, \"endMon\":2, \"startDay\":1, \"endDay\":7, \"startYear\":1951, \"endYear\":2017, \"standardStartYear\":1981, \"standardEndYear\":2010}");
	}
	
	/**
	 * 按照站号分组，进行历年同期对比
	 * @throws Exception
	 */
	@Test
	public void testSameByStation() throws Exception {
		String url = basicUrl + "/SameCalendarService/";
		testPost(url, "sameByStation", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"5%\", \"startMon\":1, \"endMon\":12, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2016, \"standardStartYear\":1981, \"standardEndYear\":2010}");
	}
	
	
	/**
	 * 按照站号分组，用于线性趋势分析
	 * @throws Exception
	 */
	@Test
	public void testLinearByStation() throws Exception {
		String url = basicUrl + "/SameCalendarService/";
//		testPost(url, "linearByStation", "para", "{\"EleType\":\"AVGTEM\", \"StatisticsType\":\"AVG\", \"FilterType\":\"\",\"station_Id_C\":\"5%\", \"startMon\":1, \"endMon\":12, \"startDay\":1, \"endDay\":31, \"startYear\":1951, \"endYear\":2016, \"standardStartYear\":1981, \"standardEndYear\":2010}");
//		testPost(url, "linearByStation", "para", "{\"startYear\":1951,\"endYear\":2016,\"FilterType\":\"\",\"StatisticsType\":\"AVG\",\"EleType\":\"AVGTEM\",\"standardStartYear\":1981,\"standardEndYear\":2010,\"station_Id_C\":\"57516,57518,57511,57513,57409,57502,57505,57506,57510,57512,57514,57509,57517,57519,57612,57425,57520,57522,57523,57525,57536,57537,57633,57635,57438,57333,57338,57339,57345,57348,57349,57426,57432,57437\",\"startDay\":1,\"endDay\":15,\"startMon\":11,\"endMon\":11}");
		testPost(url, "linearByStation", "para", "{\"startYear\":1951,\"endYear\":2016,\"FilterType\":\"\",\"StatisticsType\":\"AVG\",\"EleType\":\"AVGTEM\",\"standardStartYear\":1981,\"standardEndYear\":2010,\"station_Id_C\":\"5%\",\"startDay\":1,\"endDay\":15,\"startMon\":11,\"endMon\":11}");
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
