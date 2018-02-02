package com.spd.ws.test;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 灾害统计测试代码
 * @author Administrator
 *
 */
public class DisasterServiceTest {

	private String basicUrl = "http://localhost:8080/server/services";
	//重庆服务器
//	private String basicUrl = "http://172.24.176.84:8080/server/services";
	//外网地址
//	private String basicUrl = "http://123.57.233.58:8090/server/services";
	
	private final  Client client = Client.create();
	
	
	/**
	 * 低温阴雨，历年同期,返回当前逐次的连阴雨序列，以及历年同期统计对比结果。结果对象：ContinuousRainResult
	 * @throws Exception
	 */
	@Test
	public void testContinuousRainsByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "continuousRainsByRange", "para", "{\"startTimeStr\":\"1986-05-04\", \"endTimeStr\":\"1986-06-19\", " +
				" \"station_Id_Cs\":\"57523\",\"slightNoSSHDays\":6, \"slightPreDays\":4, \"slightMinValue\":0.1, \"severityNoSSHDays\":10, " +
				"\"severityPreDays\":7, \"severityMinValue\":0.1, \"terminPreDays\":3, \"terminValue\":0.0}");
	}
	
	/**
	 * 连阴雨 对单个站，或者一定区域内的站，或者指定站，进行逐年的时次对比，返回逐年的结果。结果对象：List<ContinueousRainYearsResult>
	 * @throws Exception
	 */
	@Test
	public void testContinuousRainsYearsSequnence() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "continuousRainsYearsSequnence", "para", "{\"startTimeStr\":\"2010-01-13\", \"endTimeStr\":\"2010-06-20\", \"startYear\":1981, " +
//				"\"endYear\":2010, \"stationIds\":\"57516,57513\",\"slightNoSSHDays\":6, \"slightPreDays\":4, \"slightMinValue\":0.1, \"severityNoSSHDays\":10, " +
//				"\"severityPreDays\":7, \"severityMinValue\":0.1, \"terminPreDays\":3, \"terminValue\":0.0}");
		
		testPost(url, "continuousRainsYearsSequnence", "para", "{\"startTimeStr\":\"2016-11-01\", \"endTimeStr\":\"2016-11-30\", \"startYear\":1951, " +
				"\"endYear\":2010, \"station_Id_Cs\":\"57516,57518,57511,57513,57409,57502,57505,57506,57510,57512,57514,57509,57517,57519,57612,57425,57520,57522,57523,57525,57536,57537,57633,57635,57438,57333,57338,57339,57345,57348,57349,57426,57431,57432,57437\",\"slightNoSSHDays\":6, \"slightPreDays\":4, \"slightMinValue\":0.1, \"severityNoSSHDays\":10, " +
				"\"severityPreDays\":7, \"severityMinValue\":0.1, \"terminPreDays\":3, \"terminValue\":0.0, \"perennialStartYear\":1981, \"perennialEndYear\":2010}");
	}
	/**
	 * 低温，时间段查询
	 * @throws Exception
	 */
	@Test
	public void testlowTmpByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "lowTmpByRange", "para", "{\"startTimeStr\":\"2016-11-01\", \"endTimeStr\":\"2016-11-27\", \"level1SequenceSeason\":2," +
				"\"level1SequenceTmp\":2,\"level1ExceptMonthes\":\"7,8\",\"level2SequenceSeason\":3, \"level2SequenceTmp\":2, \"level2ExceptMonthes\":\"7,8\", \"constatStartYear\":1981, \"constatEndYear\":2010}");
//		testPost(url, "lowTmpByRange", "para", "{\"startTimeStr\":\"2014-01-01\", \"endTimeStr\":\"2014-04-30\", \"level1SequenceSeason\":2," +
//				"\"level1SequenceTmp\":2,\"level1ExceptMonthes\":\"7,8\",\"level2SequenceSeason\":3, \"level2SequenceTmp\":2, \"level2ExceptMonthes\":\"7,8\"}");
//		testPost(url, "lowTmpByRange", "para", "{\"startTimeStr\":\"2016-05-23\",\"endTimeStr\":\"2016-05-26\",\"level1SequenceSeason\":2,\"level1SequenceTmp\":2," +
//				"\"level1ExceptMonthes\":\"7,8\",\"level2SequenceSeason\":2,\"level2SequenceTmp\":2,\"level2ExceptMonthes\":\"7,8\"}");
	}
	/**
	 * 低温，历年同期
	 * @throws Exception
	 */
	@Test
	public void testlowTmpByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "lowTmpByYear", "para", "{\"startTimeStr\":\"2014-01-01\", \"endTimeStr\":\"2014-04-30\", \"startYear\":1951,\"endYear\":2017,\"level1SequenceSeason\":2," +
				"\"level1SequenceTmp\":2,\"level1ExceptMonthes\":\"7,8\",\"level2SequenceSeason\":3, \"level2SequenceTmp\":2, \"level2ExceptMonthes\":\"7,8\", \"constatStartYear\":1981, \"constatEndYear\":2010}");
	}
	
	/**
	 * 大风，查询逐次和合计结果。
	 * @throws Exception
	 */
	@Test
	public void testMaxWindByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "maxWindByRange", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-05-30\", \"stationType\":\"AWS\"}");
		testPost(url, "maxWindByRange", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-05-30\", \"stationType\":\"ALL\"}");
	}
	
	
	/**
	 * 大风，历年统计结果
	 * @throws Exception
	 */
	@Test
	public void testMaxWindByYear() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "maxWindByYear", "para", "{\"startTimeStr\":\"2014-01-01\", \"endTimeStr\":\"2014-09-30\", \"startYear\":1981, \"endYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
		testPost(url, "maxWindByYear", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-31\", \"startYear\":1981, \"endYear\":2010, \"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57510\"}");
//		{"startTimeStr":"2016-01-01","endTimeStr":"2016-12-31","stationType":"AWS","_service_":"maxWindByYear","station_Id_Cs":"57510","startYear":1951,"endYear":2017,"perennialStartYear":1981,"perennialEndYear":2010}
	}
	
	/**
	 * 积雪，查询逐次和合计结果。
	 * @throws Exception
	 */
	@Test
	public void testSnowByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "snowByRange", "para", "{\"startTimeStr\":\"1968-06-01\", \"endTimeStr\":\"2016-06-14\"}");
	}
	
	/**
	 * 积雪，查询历年同期
	 * @throws Exception
	 */
	@Test
	public void testSnowByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "snowByYears", "para", "{\"startTimeStr\":\"2014-01-01\", \"endTimeStr\":\"2014-05-30\", \"startYear\":2000, \"endYear\":2010, \"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
		testPost(url, "snowByYears", "para", "{\"startTimeStr\":\"2014-12-01\", \"endTimeStr\":\"2015-05-30\", \"startYear\":1981, \"endYear\":2010, \"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
	}
	
	/**
	 * 雾，查询逐次和合计结果。
	 * @throws Exception
	 */
	@Test
	public void testFogByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "fogByRange", "para", "{\"startTimeStr\":\"2014-12-01\", \"endTimeStr\":\"2015-05-30\"}");
	}
	
	/**
	 * 雾，历年同期。
	 * @throws Exception
	 */
	@Test
	public void testFogByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "fogByYears", "para", "{\"startTimeStr\":\"2014-12-01\",\"endTimeStr\":\"2015-02-28\",\"station_Id_Cs\":\"57516,57513\",\"startYear\":2010,\"endYear\":2016,\"perennialStartYear\":1981,\"perennialEndYear\":2010}");
//		testPost(url, "fogByYears", "para", "{\"startTimeStr\":\"2014-12-01\", \"endTimeStr\":\"2015-02-28\", \"startYear\":1951, \"endYear\":2010, \"perennialStartYear\":1951, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
//		testPost(url, "fogByYears", "para", "{\"startTimeStr\":\"2014-12-01\", \"endTimeStr\":\"2014-03-30\", \"startYear\":1981, \"endYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
	}
	
//	 /**
//	 *  暴雨，历年同期。
//	 * @throws Exception
//	 */
//	@Test
//	public void testRainstorm() throws Exception {
//		String url = basicUrl + "/DisasterService/";
//		testPost(url, "rainstorm", "para", "{\"startTime\":\"2014-03-01\", \"endTime\":\"2014-08-30\", \"EleType\":\"PRETIME0808\", \"level1\":50, \"level2\":100, \"level3\":250}");
//	}
	 
	/**
	 * 高温，时间段
	 * @throws Exception
	 */
	@Test
	public void testHighTmpByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "highTmpByRange", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-06-30\", \"level1HighTmp\":30,\"level2HighTmp\":37,\"level3HighTmp\":40, \"station_Id_Cs\":\"57333,57338,57339,57345,57348,57349,57409,57425,57426,57431,57432,57437,57438,57502,57505,57506,57509,57510,57511,57512,57513,57514,57516,57517,57518,57519,57520,57522,57523,57525,57536,57537,57612,57633,57635\"}");
	}
	
	/**
	 * 高温，历年同期
	 * @throws Exception
	 */
	@Test
	public void testHighTmpByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "highTmpByYears", "para", "{\"startTimeStr\":\"2016-06-01\", \"endTimeStr\":\"2016-09-01\"," +
				" \"startYear\":1961,\"endYear\":2016,\"perennialStartYear\":1981, \"perennialEndYear\":2010, \"level1HighTmp\":35,\"level2HighTmp\":37," +
				"\"level3HighTmp\":40, \"station_Id_Cs\":\"57333,57338,57339,57345,57348,57349,57409,57425,57426,57431,57432,57437,57438,57502,57505,57506,57509,57510,57511,57512,57513,57514,57516,57517,57518,57519,57520,57522,57523,57525,57536,57537,57612,57633,57635\"}");
	}
	
	/**
	 * 强降温、时间段
	 * @throws Exception
	 */
	@Test
	public void testStrongCoolingByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "strongCoolingByRange", "para", "{\"startTimeStr\":\"2014-02-01\", \"endTimeStr\":\"2014-04-25\", \"level1WinterTmp\": 6, \"level1springAutumnTmp\":8, \"level1SummerTmp\":8, \"level2WinterTmp\": 8, \"level2springAutumnTmp\":10, \"level2SummerTmp\":10}");
//		testPost(url, "strongCoolingByRange", "para", "{\"startTimeStr\":\"2013-01-01\", \"endTimeStr\":\"2014-05-31\", \"level1SummerFlag\":\"true\",\"level2SummerFlag\":\"true\",\"level1WinterTmp\": 6, \"level1springAutumnTmp\":8, \"level1SummerTmp\":8, \"level2WinterTmp\": 8, \"level2springAutumnTmp\":10, \"level2SummerTmp\":10}");
//		testPost(url, "strongCoolingByRange", "para", "{\"startTimeStr\":\"2016-02-01\", \"endTimeStr\":\"2016-01-10\", \"level1SummerFlag\":\"true\",\"level2SummerFlag\":\"true\",\"level1WinterTmp\": 6, \"level1springAutumnTmp\":8, \"level1SummerTmp\":8, \"level2WinterTmp\": 8, \"level2springAutumnTmp\":10, \"level2SummerTmp\":10, \"stationType\":\"ALL\"}");
		testPost(url, "strongCoolingByRange", "para", "{\"startTimeStr\":\"2017-02-19\", \"endTimeStr\":\"2017-02-21\", \"level1SummerFlag\":\"true\",\"level2SummerFlag\":\"true\",\"level1WinterTmp\": 6, \"level1springAutumnTmp\":8, \"level1SummerTmp\":8, \"level2WinterTmp\": 8, \"level2springAutumnTmp\":10, \"level2SummerTmp\":10, \"station_Id_Cs\":\"57635\"}");
	}
	
	/**
	 * 强降温、历年同期
	 * @throws Exception
	 */
	@Test
	public void testStrongCoolingByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "strongCoolingByYears", "para", "{\"startTimeStr\":\"2016-11-01\", \"endTimeStr\":\"2016-12-01\", " +
				" \"level1SummerFlag\":\"true\",\"level2SummerFlag\":\"true\", \"startYear\":1991, \"endYear\":2016," +
				" \"perennialStartYear\":1981, \"perennialEndYear\":2010,\"level1WinterTmp\": 6, \"level1springAutumnTmp\":8," +
				" \"level1SummerTmp\":8, \"level2WinterTmp\": 8, \"level2springAutumnTmp\":10, \"level2SummerTmp\":10, " +
				"\"station_Id_Cs\":\"57516,57513\"}");
	}
	
	/**
	 * 雷暴，按时间段统计
	 * @throws Exception
	 */
	@Test
	public void testThundByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "thundByRange", "para", "{\"startTimeStr\":\"2013-01-01\", \"endTimeStr\":\"2013-09-25\"}");
	}
	
	/**
	 * 雷暴，历年统计
	 * @throws Exception
	 */
	@Test
	public void testThundByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "thundByYears", "para", "{\"startTimeStr\":\"2013-01-01\", \"endTimeStr\":\"2013-09-25\", \"startYear\":1981, \"endYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
		testPost(url, "thundByYears", "para", "{\"startTimeStr\":\"2016-11-01\", \"endTimeStr\":\"2016-06-01\", \"startYear\":1951, \"endYear\":2010, \"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506\"}");
	}
	
	/**
	 * 霜冻，按时间段统计
	 * @throws Exception
	 */
	@Test
	public void testFrostByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "frostByRange", "para", "{\"startTimeStr\":\"2016-11-20\", \"endTimeStr\":\"2016-11-29\", \"level1PersistDays\":5, \"level1LowTmp\":2," +
				"\"level1LTLowTmpDays\":3, \"level1LTLowTmp\":0, \"level2PersistDays\":7, \"level2LowTmp\":2, \"level2LTLowTmpDays\":5, \"level2LTLowTmp\":0, \"stationType\":\"AWS\"}");
	}
	
	/**
	 * 霜冻，历年统计
	 * @throws Exception
	 */
	@Test
	public void testFrostByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "frostByYears", "para", "{\"startTimeStr\":\"2014-09-01\", \"endTimeStr\":\"2014-12-31\", \"level1PersistDays\":5, \"level1LowTmp\":2," +
//				"\"level1LTLowTmpDays\":3, \"level1LTLowTmp\":0, \"level2PersistDays\":7, \"level2LowTmp\":2, \"level2LTLowTmpDays\":5, \"level2LTLowTmp\":0, " +
//				"\"startYear\":1981, \"endYear\":2010, \"station_Id_Cs\":\"57516,57513,57522,57506,57333\"}");
		
		testPost(url, "frostByYears", "para", "{\"startTimeStr\":\"2016-01-01\", \"endTimeStr\":\"2016-12-31\", \"level1PersistDays\":5, \"level1LowTmp\":2," +
				"\"level1LTLowTmpDays\":3, \"level1LTLowTmp\":0, \"level2PersistDays\":7, \"level2LowTmp\":2, \"level2LTLowTmpDays\":5, \"level2LTLowTmp\":0, " +
				"\"startYear\":1951, \"endYear\":2016,\"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57516\"}");
	}
	
	/**
	 * 暴雨，按时间段统计
	 * @throws Exception
	 */
	@Test
	public void testRainstormByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "rainstormByRange", "para", "{\"startTimeStr\":\"2014-04-01\", \"endTimeStr\":\"2014-07-01\", \"level1\":50, \"level2\":100," +
				"\"level3\":250, \"type\":\"RAINSTORM2020\", \"stationType\":\"ALL\"}");
	}
	
	/**
	 * 暴雨，按年份统计
	 * @throws Exception
	 */
	@Test
	public void testRainstormByYears() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "rainstormByYears", "para", "{\"startTimeStr\":\"2016-05-01\", \"endTimeStr\":\"2016-07-31\", \"level1\":50, \"level2\":100," +
				"\"level3\":250, \"type\":\"RAINSTORM2020\", \"startYear\":1951, \"endYear\":2016, \"perennialStartYear\":1981, \"perennialEndYear\":2010, \"station_Id_Cs\":\"57333,57338,57339,57345,57348,57349,57409,57425,57426,57432,57437,57438,57502,57505,57506,57509,57510,57511,57512,57513,57514,57516,57517,57518,57519,57520,57522,57523,57525,57536,57537,57612,57633,57635\"}");
		
//		testPost(url, "rainstormByYears", "para", "{\"startTimeStr\":\"2013-12-01\", \"endTimeStr\":\"2014-07-01\", \"level1\":50, \"level2\":100," +
//				"\"level3\":250, \"type\":\"RAINSTORM2020\", \"startYear\":1981, \"endYear\":2010,  \"station_Id_Cs\":\"57516,57513,57522,57506,57333\"}");
	}
	
	/**
	 * 干旱，统计时间点
	 * @throws Exception
	 */
	@Test
	public void testMCIByTime() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "mciByTime", "para", "{\"datetime\":\"2014-02-28\",\"level1\":-1,\"level2\":-1.5,\"level3\":-2,\"level4\":-2.5,}");
	}
	
	/**
	 * 干旱，站次统计
	 * @throws Exception
	 */
	@Test
	public void testMCIStatisticsByTime() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "mciStatisticsByTime", "para", "{\"startTimeStr\":\"2014-01-01\",\"endTimeStr\":\"2014-05-01\",\"level1\":-1,\"level2\":-1.5,\"level3\":-2,\"level4\":-2.5,}");
	}
	
	/**
	 * 土壤湿度
	 * @throws Exception
	 */
	@Test
	public void testAgmesoilStatisticsByTime() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "agmesoilStatisticsByTime", "para", "{\"datetime\":\"2017-05-01\"}");
	}
	
	/**
	 * 小时雨量	降水极值
	 * @throws Exception
	 */
	@Test
	public void testHourRainExt() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainExt", "para", "{\"startTimeStr\":\"2015-01-05 00:00:00\",\"endTimeStr\":\"2015-01-07 10:00:00\", \"type\":\"AWS\"}");
//		testPost(url, "hourRainExt", "para", "{\"startTimeStr\":\"2014-12-28 12:00:00\",\"endTimeStr\":\"2014-12-29 10:00:00\", \"type\":\"MWS\"}");
//		testPost(url, "hourRainExt", "para", "{\"startTimeStr\":\"2014-12-26 12:00:00\",\"endTimeStr\":\"2014-12-28 10:00:00\", \"type\":\"ALL\"}");
		testPost(url, "hourRainExt", "para", "{\"startTimeStr\":\"2014-12-28 12:00:00\",\"endTimeStr\":\"2014-12-29 12:00:00\", \"type\":\"AREA\", \"areaCode\":\"500106\"}");
	}
	
	/**
	 * 小时雨量	累积降水
	 * @throws Exception
	 */
	@Test
	public void testHourRainAccumulate() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainAccumulate", "para", "{\"startTimeStr\":\"2014-06-01 01:00:00\",\"endTimeStr\":\"2014-06-01 05:00:00\", \"type\":\"AWS\"}");
//		testPost(url, "hourRainAccumulate", "para", "{\"startTimeStr\":\"2014-12-29 00:00:00\",\"endTimeStr\":\"2015-01-05 10:00:00\", \"type\":\"AREA\", \"areaCode\":\"500106\"}");
		testPost(url, "hourRainAccumulate", "para", "{\"startTimeStr\":\"2014-12-29 00:00:00\",\"endTimeStr\":\"2014-12-31 10:00:00\", \"type\":\"MWS\", \"areaCode\":\"500106\"}");
//		testPost(url, "hourRainAccumulate", "para", "{\"startTimeStr\":\"2014-12-30 17:00:00\",\"endTimeStr\":\"2015-01-01 17:00:00\", \"type\":\"ALL\"}");
	}
	
	/**
	 * 小时雨量	过程降水
	 * @throws Exception
	 */
	@Test
	public void testHourRainSequence() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainSequence", "para", "{\"startTimeStr\":\"2015-01-05 00:00:00\",\"endTimeStr\":\"2015-01-05 10:00:00\", \"type\":\"AWS\"}");
		testPost(url, "hourRainSequence", "para", "{\"startTimeStr\":\"2014-12-29 00:00:00\",\"endTimeStr\":\"2015-01-02 05:00:00\", \"type\":\"AREA\", \"areaCode\":\"500106\"}");
//		testPost(url, "hourRainSequence", "para", "{\"startTimeStr\":\"2014-12-29 00:00:00\",\"endTimeStr\":\"2015-01-02 05:00:00\", \"type\":\"MWS\"}");
//		testPost(url, "hourRainSequence", "para", "{\"startTimeStr\":\"2014-12-29 00:00:00\",\"endTimeStr\":\"2015-01-02 05:00:00\", \"type\":\"ALL\"}");
	}
	
	/**
	 * 小时雨量	时段位次
	 * @throws Exception
	 */
	@Test
	public void testHourRainRankTimesStatistics() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainRankTimesStatistics", "para", "{\"extStartTimeStr\":\"2014-05-05 00:00:00\",\"extEndTimeStr\":\"2014-05-05 10:00:00\"," +
//				"\"rankStartTimeStr\":\"2014-05-05 00:00:00\", \"rankEndTimeStr\":\"2014-05-06 00:00:00\", \"hour\":24, \"type\":\"AREA\", \"areaCode\":\"500106\"}");
		testPost(url, "hourRainRankTimesStatistics", "para", "{\"extStartTimeStr\":\"2014-12-31 00:00:00\",\"extEndTimeStr\":\"2015-01-01 00:00:00\"," +
				"\"rankStartTimeStr\":\"2014-05-05 00:00:00\", \"rankEndTimeStr\":\"2014-05-06 00:00:00\", \"type\":\"MWS\", \"hour\":24}");
	}
	
	/**
	 * 小时雨量	同期位次
	 * @throws Exception
	 */
	@Test
	public void testHourRainRankYearsStatistics() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainRankYearsStatistics", "para", "{\"startTimeStr\":\"2014-01-08 00:00:00\",\"endTimeStr\":\"2014-01-08 10:00:00\"," +
//					"\"startYear\":2000, \"endYear\":2016, \"hour\":1, \"type\":\"AREA\", \"areaCode\":\"500106\"}");
//		testPost(url, "hourRainRankYearsStatistics", "para", "{\"startTimeStr\":\"2014-06-25 10:00:00\",\"endTimeStr\":\"2014-06-26 10:00:00\"," +
//					"\"startYear\":2000, \"endYear\":2016, \"hour\":1, \"type\":\"MWS\"}");
//		testPost(url, "hourRainRankYearsStatistics", "para", "{\"startTimeStr\":\"2014-06-25 10:00:00\",\"endTimeStr\":\"2014-06-26 10:00:00\"," +
//					"\"startYear\":2000, \"endYear\":2016, \"hour\":1, \"type\":\"AWS\"}");
		testPost(url, "hourRainRankYearsStatistics", "para", "{\"startTimeStr\":\"2016-06-27 17:00:00\",\"endTimeStr\":\"2016-06-28 17:00:00\"," +
					"\"startYear\":2000, \"endYear\":2016, \"hour\":1, \"type\":\"ALL\"}");
	}
	
	/**
	 * 小时雨量	历年极值
	 * @throws Exception
	 */
	@Test
	public void testHourRainExtYearsStatistics() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "hourRainExtYearsStatistics", "para", "{\"startTimeStr\":\"2014-06-01 00:00:00\",\"endTimeStr\":\"2014-06-01 10:00:00\"," +
					"\"startYear\":2007, \"endYear\":2014, \"hour\":1, \"Station_Id_C\":\"57516\"}");
//		testPost(url, "hourRainExtYearsStatistics", "para", "{\"startTimeStr\":\"2014-03-01 00:00:00\",\"endTimeStr\":\"2014-03-02 05:00:00\"," +
//					"\"startYear\":2007, \"endYear\":2014, \"hour\":1, \"type\":\"AREA\", \"areaCode\":\"500106\"}");
	}
	
	/**
	 * 指定时间段内查询极值，以及极值对应的日期
	 * @throws Exception
	 */
	@Test
	public void testHourRainExtByTimes() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainExtByTimes", "para", "{\"startTimeStr\":\"2015-01-05 00:00:00\",\"endTimeStr\":\"2015-01-06 00:00:00\", \"type\":\"ALL\"}");
		testPost(url, "hourRainExtByTimes", "para", "{\"startTimeStr\":\"2014-06-26 17:00:00\",\"endTimeStr\":\"2014-06-27 17:00:00\", \"type\":\"AREA\", \"areaCode\":\"500106\"}");
//		testPost(url, "hourRainExtByTimes", "para", "{\"startTimeStr\":\"2015-01-05 00:00:00\",\"endTimeStr\":\"2015-01-06 00:00:00\", \"type\":\"AWS\"}");
//		testPost(url, "hourRainExtByTimes", "para", "{\"startTimeStr\":\"2015-01-05 00:00:00\",\"endTimeStr\":\"2015-01-06 00:00:00\", \"type\":\"MWS\"}");
	}
	
	/**
	 * 小时降水，逐时演变
	 * @throws Exception
	 */
	@Test
	public void testHourRainChange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "hourRainChange", "para", "{\"startTimeStr\":\"2014-12-28 14:00:00\",\"endTimeStr\":\"2015-01-02 14:00:00\", \"Station_Id_C\":\"A7083\"}");
	}
	
	/**
	 * 查询小时雨量建站时间
	 * @throws Exception
	 */
	@Test
	public void testHourRainStation() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "hourRainStation", null, null);
	}
	
	/**
	 * 小时雨量,排位
	 * @throws Exception
	 */
	@Test
	public void testHourRainSortByStation() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "hourRainSortByStation", "para", "{\"Station_Id_C\":\"A7246\", \"limit\":20, \"type\":\"R1\"}");
		testPost(url, "hourRainSortByStation", "para", "{\"Station_Id_C\":\"57516\", \"limit\":20, \"type\":\"R1\"}");
	}
	
	/**
	 * 冰雹，按时间段统计
	 * @throws Exception
	 */
	@Test
	public void testHailByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
		testPost(url, "hailByRange", "para", "{\"startTimeStr\":\"2002-02-01\",\"endTimeStr\":\"2002-05-01\"}");
	}
	
	/**
	 * 天气现象，按时间段统计
	 * @throws Exception
	 */
	@Test
	public void testWepByRange() throws Exception {
		String url = basicUrl + "/DisasterService/";
//		testPost(url, "wepByRange", "para", "{\"weps\":\"*\",\"startTimeStr\":\"2017-01-01\",\"endTimeStr\":\"2017-01-01\", \"station_Id_Cs\":\"57333,57338,57339,57345,57348,57349,57409,57425,57426,57431,57432,57437,57438,57502,57505,57506,57509,57510,57511,57512,57513,57514,57516,57517,57518,57519,57520,57522,57523,57525,57536,57537,57612,57633,57635\"}");
//		testPost(url, "wepByRange", "para", "{\"weps\":\"10\",\"startTimeStr\":\"2017-03-01\",\"endTimeStr\":\"2017-03-02\", \"station_Id_Cs\":\"57333,57338,57339,57345,57348,57349,57409,57425,57426,57431,57432,57437,57438,57502,57505,57506,57509,57510,57511,57512,57513,57514,57516,57517,57518,57519,57520,57522,57523,57525,57536,57537,57612,57633,57635\"}");
		testPost(url, "wepByRange", "para", "{\"weps\":\"42\",\"startTimeStr\":\"2016-01-01\",\"endTimeStr\":\"2016-01-31\", \"station_Id_Cs\":\"57516\"}");
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
