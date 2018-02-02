package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 灾害评估测试
 * @author Administrator
 *
 */
public class DisasterEvaluateTest {

	private String basicUrl = "http://localhost:8080/server/services";
//	private String basicUrl = "http://192.168.0.116:8080/server/services";
	
	private final  Client client = Client.create();
	
	
	/**
	 * 暴雨评估
	 * @throws Exception
	 */
	@Test
	public void testContinuousRainsByRange() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "rainstormByRange", "para", "{\"startTimeStr\":\"2014-07-01\", \"endTimeStr\":\"2014-09-01\"," +
				"\"minDayPre\":50, \"minDayStations\":4, \"weight1\":0.25, \"weight2\":0.4, \"weight3\":0.25, \"weight4\":0.1}");
	}
	
	/**
	 * 高温时间段评估
	 * @throws Exception
	 */
	@Test
	public void testhighTmpByRange() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "highTmpByRange", "para", "{\"startTimeStr\":\"2016-07-19\", \"endTimeStr\":\"2016-07-26\"}");
	}
	
	/**
	 * 高温按年评估
	 * @throws Exception
	 */
	@Test
	public void testHighTmpByYears() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "highTmpByYears", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-08-01\", \"startYear\": 1961, \"endYear\":2016, " +
				"\"perennialStartYear\":1981, \"perennialEndYear\":2010, \"YHILevel1\":0.6, \"YHILevel2\":0.8, \"YHILevel3\":0.95}");
	}
	
	/**
	 * 秋雨查询计算
	 * @throws ExceptionautumnRains
	 */
	@Test
	public void testAutumnRains() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "autumnRains", "para", "{\"level1\":1.5, \"level2\":0.5, \"level3\":-0.5, \"level4\":-1.5}");
	}
	
	/**
	 * 按时间段查询秋雨对应的雨量等信息
	 * @throws Exception
	 */
	@Test
	public void testAutumnRainsByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "autumnRainsByTimes", "para", "{\"startTimeStr\": \"2016-09-01\", \"endTimeStr\": \"2016-09-07\"}");
	}
	
	/**
	 * 年度查询
	 * @throws Exception
	 */
	@Test
	public void testAutumnRainsByYear() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "autumnRainsByYear", "para", "{\"year\": 2015}");
	}
	
	/**
	 * 单站干旱过程，按时间段查询
	 * @throws Exception
	 */
	@Test
	public void testMCIStationByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "mciStationByTimes", "para", "{\"startTimeStr\":\"2015-01-01\", \"endTimeStr\":\"2015-12-31\"}");
	}
	
	/**
	 * 单站干旱过程，年度查询
	 * @throws Exception
	 */
	@Test
	public void testMCIStationByYears() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "mciStationByYears", "para", "{\"startYear\":1951, \"endYear\":2016}");
	}
	
	/**
	 * 区域干旱过程，按时间段查询
	 * @throws Exception
	 */
	@Test
	public void testMCIAreaByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "mciAreaByTimes", "para", "{\"startTimeStr\":\"2001-01-01\", \"endTimeStr\":\"2011-12-31\"}");
	}
	
	/**
	 * 区域干旱过程，年度查询
	 * @throws Exception
	 */
	@Test
	public void testMCIAreaByYears() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "mciAreaByYears", "para", "{\"startYear\":2010, \"endYear\":2016}");
	}
	
	/**
	 * 按时间段查询区域暴雨过程
	 * @throws Exception
	 */
	@Test
	public void testAreaStormByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "areaStormByTimes", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-30\"," +
				"\"maxPre\":30528, \"maxSignalPre\":306.9,\"maxPersistDays\":7,\"maxStationCnt\":31," +
				"\"minPre\":2175, \"minSignalPre\":50,\"minPersistDays\":1,\"minStationCnt\":4, \"type\":\"2020\"," +
				"\"weight1\":0.25,\"weight2\":0.25,\"weight3\":0.4,\"weight4\":0.1," +
				"\"level1\":0,\"level2\":0.15,\"level3\":0.25,\"level4\":0.4}");
	}
	
	/**
	 * 按历年查询暴雨
	 * @throws Exception
	 */
	@Test
	public void testRainstormByYears() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
//		testPost(url, "rainstormByYears", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-08-30\"," +
//				"\"maxPre\":30528, \"maxSignalPre\":306.9,\"maxPersistDays\":7,\"maxStationCnt\":31," +
//				"\"minPre\":2175, \"minSignalPre\":50,\"minPersistDays\":1,\"minStationCnt\":4, \"type\":\"PRE\"," +
//				"\"weight1\":0.25,\"weight2\":0.25,\"weight3\":0.4,\"weight4\":0.1," +
//				"\"level1\":0,\"level2\":0.15,\"level3\":0.25,\"level4\":0.4,\"startYear\":1971, \"endYear\":2016," +
//				"\"perennialStartYear\":1981, \"perennialEndYear\":2010}");
		//跨年的情况
		testPost(url, "rainstormByYears", "para", "{\"startTimeStr\":\"2016-08-30\", \"endTimeStr\":\"2016-05-01\"," +
				"\"maxPre\":30528, \"maxSignalPre\":306.9,\"maxPersistDays\":7,\"maxStationCnt\":31," +
				"\"minPre\":2175, \"minSignalPre\":50,\"minPersistDays\":1,\"minStationCnt\":4, \"type\":\"PRE\"," +
				"\"weight1\":0.25,\"weight2\":0.25,\"weight3\":0.4,\"weight4\":0.1," +
				"\"level1\":0,\"level2\":0.15,\"level3\":0.25,\"level4\":0.4,\"startYear\":1971, \"endYear\":2016," +
				"\"perennialStartYear\":1981, \"perennialEndYear\":2010}");
	}
	
	/**
	 * 按时间段查询单点暴雨过程
	 * @throws Exception
	 */
	@Test
	public void testStationStormByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "stationStormByTimes", "para", "{\"startTimeStr\":\"1962-01-01\", \"endTimeStr\":\"1962-12-31\"," +
				"\"maxStationPreTotal\":3463.9, \"minStationPreTotal\":800,\"maxStationCntTotal\":43,\"minStationCntTotal\":15," +
				"\"level1\":0,\"level2\":0.4,\"level3\":0.55,\"level4\":0.7}");
	}
	
	/**
	 * 单站连阴雨按时间段查询
	 * @throws Exception
	 */
	@Test
	public void testContinueRainStatiionByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "continueRainStatiionByTimes", "para", "{\"startTimeStr\":\"1959-01-01\", \"endTimeStr\":\"1961-01-01\"," +
				"\"maxSingleDays\":47, \"maxSingleRainDays\":18,\"maxSinglePre\":262.8," +
				"\"minSingleDays\":6, \"minSingleRainDays\":4,\"minSinglePre\":0.4," +
				"\"persistDaysIndex\":0.5,\"preDaysIndex\":0.4,\"preIndex\":0.1," +
				"\"strengthIndex1\":0,\"strengthIndex2\":0.1,\"strengthIndex3\":0.19,\"strengthIndex4\":0.29}");
	}
	
	/**
	 * 区域连阴雨按时间段查询
	 * @throws Exception
	 */
	@Test
	public void testContinueRainAreaByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "continueRainAreaByTimes", "para", "{\"startTimeStr\":\"1959-01-01\", \"endTimeStr\":\"1961-01-01\"," +
				"\"maxPersistDays\":42,\"maxSumStations\":51,\"maxRainDays\":689,\"maxSumPres\":3047.5," +
				"\"minPersistDays\":6,\"minSumStations\":7,\"minRainDays\":5,\"minSumPres\":11.7," +
				"\"strengthIndex1\":0,\"strengthIndex2\":0.18,\"strengthIndex3\":0.3,\"strengthIndex4\":0.44," +
				"\"index1\":0.5,\"index2\":0.4,\"index3\":0.05,\"index4\":0.05}");
	}
	
	/**
	 * 区域连阴雨按年度查询
	 * @throws Exception
	 */
	@Test
	public void testContinueRainByYear() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "continueRainByYear", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-06-01\"," +
				"\"maxSingleDays\":47, \"maxSingleRainDays\":18,\"maxSinglePre\":262.8," +
				"\"minSingleDays\":6, \"minSingleRainDays\":4,\"minSinglePre\":0.4," +
				"\"persistDaysIndex\":0.5,\"preDaysIndex\":0.4,\"preIndex\":0.1," +
				"\"maxPersistDays\":42,\"maxSumStations\":51,\"maxRainDays\":689,\"maxSumPres\":3047.5," +
				"\"minPersistDays\":6,\"minSumStations\":7,\"minRainDays\":5,\"minSumPres\":11.7," +
				"\"maxStationStrength\":32.053, \"maxAreaStrength\":2.132, \"minStationStrength\":3.051, \"minAreaStrength\":0.072, " + 
				"\"strengthIndex1\":0,\"strengthIndex2\":0.18,\"strengthIndex3\":0.3,\"strengthIndex4\":0.44," + 
				"\"yearStrengthIndex1\":0,\"yearStrengthIndex2\":0.47,\"yearStrengthIndex3\":0.66,\"yearStrengthIndex4\":0.77," +
				"\"startYear\":1961,\"endYear\":2016,\"perennialStartYear\":1981,\"perennialEndYear\":2010}");
	}
	
	
	/**
	 * 单站强降温，时间段查询
	 * @throws Exception
	 */
	@Test
	public void testStrongCoolingStationByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "strongCoolingStationByTimes", "para", "{\"startTimeStr\":\"2015-01-01\", \"endTimeStr\":\"2015-12-31\"}");
	}
	
	/**
	 * 区域强降温，时间段查询
	 * @throws Exception
	 */
	@Test
	public void testStrongCoolingAreaByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "strongCoolingAreaByTimes", "para", "{\"startTimeStr\":\"2015-01-01\", \"endTimeStr\":\"2015-12-31\"," +
				"\"maxStations\":34, \"minStations\":7, \"maxPersistDays\":8, \"minPersistDays\":1, \"maxCoolingTmp\":19.4," +
				"\"minCoolingTmp\":6, \"weight1\":0.4,\"weight2\":0.15, \"weight3\":0.4, \"weight4\":0.05, \"level1\":0.51, \"level2\":0.63, \"level3\":0.74}");
	}
	
	/**
	 * 单站低温，时间段查询
	 * @throws Exception
	 */
	@Test
	public void testLowTmpStationByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "lowTmpStationByTimes", "para", "{\"startTimeStr\":\"1951-02-26\", \"endTimeStr\":\"1951-04-01\"}");
	}
	
	/**
	 * 年度强降温评估
	 * @throws Exception
	 */
	@Test
	public void testStrongCoolingByYear() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "strongCoolingByYear", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-31\"," +
				"\"maxStations\":34, \"minStations\":7, \"maxPersistDays\":8, \"minPersistDays\":1, \"maxCoolingTmp\":19.4," +
				"\"minCoolingTmp\":6, \"weight1\":0.4,\"weight2\":0.15, \"weight3\":0.4, \"weight4\":0.05, \"level1\":0.51, \"level2\":0.63, \"level3\":0.74," +
				"\"startYear\":1961,\"endYear\":2016,\"perennialStartYear\":1981,\"perennialEndYear\":2010}");
	}
	
	/**
	 * 区域低温，时间段查询
	 * @throws Exception
	 */
	@Test
	public void testLowTmpAreaByTimes() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "lowTmpAreaByTimes", "para", "{\"startTimeStr\":\"1954-12-01\", \"endTimeStr\":\"1954-12-15\"," +
				"\"maxPersistDays\":36, \"minPersistDays\":5, \"maxSumStation\": 34, \"minSumStation\": 7, \"maxSumAnomaly\":206.2," +
				"\"minSumAnomaly\":3.1, \"persistDayWeight\":0.5, \"sumStationWeight\":0.3, \"anomalyWeight\": 0.2, " +
				"\"level1\": 0, \"level2\": 0.45, \"level3\": 0.6, \"level4\": 0.75}");
	}
	
	/**
	 * 年度低温
	 * @throws Exception
	 */
	@Test
	public void testLowTmpAreaByYear() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
//		testPost(url, "lowTmpByYear", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-31\"," +
//				"\"maxPersistDays\":36, \"minPersistDays\":5, \"maxSumStation\": 34, \"minSumStation\": 7, \"maxSumAnomaly\":206.2," +
//				"\"minSumAnomaly\":3.1, \"persistDayWeight\":0.5, \"sumStationWeight\":0.3, \"anomalyWeight\": 0.2, " +
//				"\"level1\": 0.86, \"level2\": 1.19, \"level3\": 1.34, \"startYear\":1954, \"endYear\":2016}");
		
		testPost(url, "lowTmpByYear", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-07-31\"," +
				"\"maxPersistDays\":36, \"minPersistDays\":5, \"maxSumStation\": 34, \"minSumStation\": 7, \"maxSumAnomaly\":206.2," +
				"\"minSumAnomaly\":3.1, \"persistDayWeight\":0.5, \"sumStationWeight\":0.3, \"anomalyWeight\": 0.2, " +
				"\"level1\": 0.86, \"level2\": 1.19, \"level3\": 1.34, \"startYear\":1954, \"endYear\":2016,\"standardStartYear\":1981,\"standardEndYear\":2010}");
	}
	
	@Test
	public void testSnowArea() throws Exception {
		String url = basicUrl + "/DisasterEvaluateService/";
		testPost(url, "snowArea", "para", "{\"startTimeStr\":\"1951-01-01\", \"endTimeStr\":\"2016-12-31\",\"IA\":0.1, \"IB\":0.4,\"IC\":0.3,\"ID\":0.2," +
				"\"level1\":0,\"level2\":0.3,\"level3\":0.4,\"level4\":0.6}");
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
