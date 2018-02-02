package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SequenceChangeServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";

//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	
	private final  Client client = Client.create();
	
	/**
	 *日数统计
	 * @throws Exception
	 */
	@Test
	public void testDay() throws Exception {
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
		//statisticsType 定义: 平均:AVG, 最大:MAX, 最小:MIN, 求和：SUM
		String url = basicUrl + "/SequenceChangService/";
		//climTimeType定义：DAY:日, FIVEDAYS:候, TENDAYS:旬, MONTH:月, SEASON:季, YEAR:年
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"MAX\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"MIN\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"SUM\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//候 平均
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-05-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"FIVEDAYS\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//旬平均
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-05-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"TENDAYS\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//月
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-05-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"MONTH\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//季
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-02-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"SEASON\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//年
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-02-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"YEAR\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//候 最大
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-05-01\", \"endTimeStr\":\"2016-06-06\", \"station_Id_C\":\"57516\", \"statisticsType\":\"MAX\", \"climTimeType\":\"FIVEDAYS\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		//白天降水
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-04\", \"station_Id_C\":\"57516\", \"statisticsType\":\"SUM\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"PRETIME0820\"}");
		//测试所有站
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-04\", \"station_Id_C\":\"5%\", \"statisticsType\":\"SUM\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"PRETIME0820\"}");
//		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2016-09-01\", \"endTimeStr\":\"2016-09-12\", \"station_Id_C\":\"5%\", \"statisticsType\":\"AVG\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		testPost(url, "sequenceChangByTimes", "para", "{\"startTimeStr\":\"2017-01-01\", \"endTimeStr\":\"2017-12-31\", \"station_Id_C\":\"57516\", \"statisticsType\":\"AVG\", \"climTimeType\":\"MONTH\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"PRETIME2020\"}");
		
	}
	
	
	@Test
	public void testSequenceChangeStationsByTimes() throws Exception {
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
		//statisticsType 定义: 平均:AVG, 最大:MAX, 最小:MIN, 求和：SUM
		//climTimeType定义：DAY:日, FIVEDAYS:候, TENDAYS:旬, MONTH:月, SEASON:季, YEAR:年
		String url = basicUrl + "/SequenceChangService/";
//		testPost(url, "sequenceChangeStationsByTimes", "para", "{\"startTimeStr\":\"2016-09-01\", \"endTimeStr\":\"2016-09-12\", \"station_Id_C\":\"5%\", \"statisticsType\":\"AVG\", \"climTimeType\":\"DAY\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
//		testPost(url, "sequenceChangeStationsByTimes", "para", "{\"startTimeStr\":\"2016-09-01\", \"endTimeStr\":\"2016-09-15\", \"station_Id_C\":\"5%\", \"statisticsType\":\"AVG\", \"climTimeType\":\"FIVEDAYS\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
//		testPost(url, "sequenceChangeStationsByTimes", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-31\", \"station_Id_C\":\"57516\", \"statisticsType\":\"SUM\", \"climTimeType\":\"MONTH\", \"standardStartYear\":1981, \"standardEndYear\":2010,\"eleTypes\":\"PRETIME2020\"}");
		testPost(url, "sequenceChangeStationsByTimes", "para", "{\"startTimeStr\":\"2017-01-01\",\"endTimeStr\":\"2017-02-16\",\"station_Id_C\":\"5%\",\"statisticsType\":\"AVG\",\"climTimeType\":\"MONTH\",\"standardStartYear\":1981,\"standardEndYear\":2010,\"eleTypes\":\"AVGTEM\"}");
		
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
