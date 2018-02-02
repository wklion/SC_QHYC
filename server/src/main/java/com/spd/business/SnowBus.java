package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.SnowResult;
import com.spd.common.SnowResultTotal;
import com.spd.common.SnowSequenceResult;
import com.spd.common.SnowYearsParam;
import com.spd.common.SnowYearsResult;
import com.spd.common.TimesParam;
import com.spd.common.evaluate.SnowAreaParam;
import com.spd.common.evaluate.SnowAreaResult;
import com.spd.service.ISnow;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;

public class SnowBus {

	private ISnow snowImpl = (ISnow)ContextLoader.getCurrentWebApplicationContext().getBean("SnowImpl");
	
	private static int STATIONCNT = 7;//达到20%的站数
	
	private static int LMAX = 34; //最大站数影响范围

	private static int LMIN = 7; //最小站数影响范围
	
	private static int MAXPERSISTDAYS = 23; //历史最大持续时间

	private static int MINPERSISTDAYS = 1; //历史最大持续时间
	
	private static double MAXDEPTH = 5.04;//平均最大积雪深度

	private static double MINDEPTH = 0;//平均最小积雪深度
	
	private static double MAXPROCESSDEPTH = 26;//过程最大积雪深度

	private static double MINPROCESSDEPTH = 1;//过程最小积雪深度
	
	/**
	 * 统计时间段内的降雪
	 * @param timesParam
	 * @return
	 */
	public SnowResult snowByRange(TimesParam timesParam) {
		StationArea stationArea = new StationArea();
		Map<String, String> stationMap = stationArea.getStationAreaMap();
		SnowResult snowResult = new SnowResult();
		//合计
		List<SnowResultTotal> snowResultTotalList = new ArrayList<SnowResultTotal>();
		Map<String, List<SnowSequenceResult>> stationSnowMap = new HashMap<String, List<SnowSequenceResult>>();
		//逐次
		List<SnowSequenceResult> snowSequenceResultList = new ArrayList<SnowSequenceResult>();
		//1. 查询数据库。
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> list = snowImpl.querySnowByTimes(paramMap);
		for(int i=0; i<list.size(); i++) {
			SnowSequenceResult snowSequenceResult = new SnowSequenceResult();
			LinkedHashMap itemMap = list.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			String datetime = (String) itemMap.get("datetime");
			Integer GSS = (Integer) itemMap.get("GSS");
			Integer snow = (Integer) itemMap.get("Snow");
			Double snow_Depth = (Double) itemMap.get("Snow_Depth");
			snowSequenceResult.setStation_Id_C(station_Id_C);
			snowSequenceResult.setStation_Name(station_Name);
			snowSequenceResult.setGSS(GSS);
			snowSequenceResult.setSnow(snow);
			snowSequenceResult.setSnow_Depth(snow_Depth);
			snowSequenceResult.setArea(stationMap.get(station_Id_C));
			snowSequenceResult.setDatetime(datetime);
			snowSequenceResultList.add(snowSequenceResult);
			
			List<SnowSequenceResult> sequenceList = stationSnowMap.get(station_Id_C);
			if(sequenceList == null) {
				sequenceList = new ArrayList<SnowSequenceResult>();
			}
			sequenceList.add(snowSequenceResult);
			stationSnowMap.put(station_Id_C, sequenceList);
		}
		snowResult.setSnowSequenceResultList(snowSequenceResultList);
		//2. 统计分析
		Set<String> set = stationSnowMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String station_Id_CItem = it.next();
			List<SnowSequenceResult> snowSequenceResultListItem = stationSnowMap.get(station_Id_CItem);
			SnowResultTotal snowResultTotal = new SnowResultTotal();
			int gssCnt = 0, snowDays = 0;
			double maxSnow_Depth = 0, sumSnow_Depth = 0, snowCnt = 0;
			String stationName = "";
			for(int i = 0; i < snowSequenceResultListItem.size(); i++) {
				SnowSequenceResult snowSequenceResultItem = snowSequenceResultListItem.get(i);
				if(snowSequenceResultItem == null) {
					System.out.println();
				}
				Integer gssItem = snowSequenceResultItem.getGSS();
				stationName = snowSequenceResultItem.getStation_Name();
				if(gssItem != null && gssItem == 1) {
					gssCnt += 1;
				}
				Integer snowItem = snowSequenceResultItem.getSnow();
				if(snowItem != null && snowItem == 1) {
					snowDays += 1;
				}
				Double snowDepthItem = snowSequenceResultItem.getSnow_Depth();
				snowDepthItem = Eigenvalue.dispose(snowDepthItem);
				if(snowDepthItem != null) {
					sumSnow_Depth += snowDepthItem;
					snowCnt ++;
				}
//				if(snowDepthItem != null && snowDepthItem >= 0 && snowDepthItem < CommonConstant.MAXINVALID) {
//					sumSnow_Depth += snowDepthItem;
//					snowCnt ++;
//				}
				if(snowDepthItem != null && snowDepthItem > maxSnow_Depth) {
					maxSnow_Depth = snowDepthItem;
				}
//				if(snowDepthItem != null && snowDepthItem < CommonConstant.MAXINVALID && snowDepthItem > maxSnow_Depth) {
//					maxSnow_Depth = snowDepthItem;
//				}
			}
			snowResultTotal.setGssDays(gssCnt);
			snowResultTotal.setMaxSnow_Depth(maxSnow_Depth);
			if(snowCnt != 0) {
				snowResultTotal.setAvgSnow_Depth(CommonTool.roundDouble(sumSnow_Depth / snowCnt));
			} else {
				snowResultTotal.setAvgSnow_Depth(0.0);
			}
			snowResultTotal.setSnowDays(snowDays);
			snowResultTotal.setStation_Id_C(station_Id_CItem);
			snowResultTotal.setArea(stationMap.get(station_Id_CItem));
			snowResultTotal.setStation_Name(stationName);
			snowResultTotalList.add(snowResultTotal);
		}
		snowResult.setSnowResultTotalList(snowResultTotalList);
		return snowResult;
	}
	
	public List<SnowYearsResult> snowByYears(SnowYearsParam snowYearsParam) {
		List<SnowYearsResult> resultList = new ArrayList<SnowYearsResult>();
		//历年
		List<String> Station_Id_Cs = new ArrayList<String>();
		String[] station_Id_CArray = snowYearsParam.getStation_Id_Cs();
		for(int i=0; i<station_Id_CArray.length; i++) {
			Station_Id_Cs.add(station_Id_CArray[i]);
		}
		Map<Integer, List<LinkedHashMap>> overMap = querySnow(snowYearsParam.getTimesParam(), Station_Id_Cs, snowYearsParam.getStartYear(), snowYearsParam.getEndYear());
		//常年
		Map<Integer, List<LinkedHashMap>> yearsMap = querySnow(snowYearsParam.getTimesParam(), Station_Id_Cs, snowYearsParam.getPerennialStartYear(), snowYearsParam.getPerennialEndYear());
		//计算常年结果
		double gssDaysCnt = 0;
		Set<Integer> yearSet = yearsMap.keySet();
		Iterator<Integer> yearIt = yearSet.iterator();
		while(yearIt.hasNext()) {
			Integer year = yearIt.next();
			List<LinkedHashMap> list = overMap.get(year);
			if(list == null) continue;
			for(int i=0; i<list.size(); i++) {
				LinkedHashMap itemMap = list.get(i);
				Integer gss = (Integer) itemMap.get("GSS");
				if(gss != null && gss > 0) {
					gssDaysCnt++;
				}
			}
		}
		gssDaysCnt = CommonTool.roundDouble(gssDaysCnt /  (snowYearsParam.getPerennialEndYear() - snowYearsParam.getPerennialStartYear() + 1));
		//遍历map对象，统计结果
		Map<Integer, Integer> gssMap = new HashMap<Integer, Integer>(); // 积雪日数
		Map<Integer, Double> snowDepthMap = new HashMap<Integer, Double>(); //积雪深度
		Map<Integer, Integer> snowMap = new HashMap<Integer, Integer>(); //降雪日数
		
		Set<Integer> set = overMap.keySet();
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List<LinkedHashMap> list = overMap.get(year);
			for(int i=0; i<list.size(); i++) {
				LinkedHashMap itemMap = list.get(i);
				Integer GSS = (Integer) itemMap.get("GSS");
				if(GSS != null && GSS == 1) {
					Integer gssCnt = gssMap.get(year);
					if(gssCnt == null) {
						gssMap.put(year, 1);
					} else {
						gssMap.put(year, gssMap.get(year) + 1);
					}
				}
				Integer snow = (Integer) itemMap.get("Snow");
				if(snow != null && snow == 1) {
					Integer snowCnt = snowMap.get(year);
					if(snowCnt == null) {
						snowMap.put(year, 1);
					} else {
						snowMap.put(year, snowMap.get(year) + 1);
					}
				}
				Double snow_Depth = (Double) itemMap.get("Snow_Depth");
				if(snow_Depth != null && snow_Depth >= 0 && snow_Depth < CommonConstant.MAXINVALID) {
					Double snowDepthItem = snowDepthMap.get(year);
					if(snowDepthItem == null) {
						snowDepthMap.put(year, snow_Depth);
					} else {
						if(snowDepthItem < snow_Depth) {
							snowDepthMap.put(year, snow_Depth);
						}
					}
				}
			}
		}
		for(int i = snowYearsParam.getStartYear(); i <= snowYearsParam.getEndYear(); i++) {
			SnowYearsResult snowYearsResult = new SnowYearsResult();
			snowYearsResult.setYear(i);
			Integer snowDays = snowMap.get(i);
			snowYearsResult.setGssYearsDays(gssDaysCnt);
			if(snowDays != null) {
				snowYearsResult.setSnowDays(snowDays);
			}
			Integer gss = gssMap.get(i);
			if(gss != null) {
				snowYearsResult.setGssDays(gss);
				snowYearsResult.setGssDaysAnomalyRate(CommonTool.roundDouble((gss - gssDaysCnt) / gssDaysCnt * 100));
			}
			Double snowDepth = snowDepthMap.get(i);
			if(snowDepth != null) {
				snowYearsResult.setMaxSnowDepth(snowDepth);
			}
			resultList.add(snowYearsResult);
		}
		return resultList;
	}
	
	/**
	 * 降雪区域评估
	 * @return
	 */
	public List<SnowAreaResult> snowArea(SnowAreaParam snowAreaParam) {
		List<SnowAreaResult> snowAreaResultList = new ArrayList<SnowAreaResult>();
		HashMap paramMap = new HashMap();
		TimesParam timesParam = snowAreaParam.getTimesParam();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> resultList = snowImpl.snowArea(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = resultList.get(i);
				SnowAreaResult snowAreaResult = new SnowAreaResult();
				double avgSnowDepth = (Double) dataMap.get("AvgDepth");
				snowAreaResult.setAvgDepth(CommonTool.roundDouble(avgSnowDepth));
				snowAreaResult.setEndTime((String) dataMap.get("EndTime"));
				snowAreaResult.setStartTime((String) dataMap.get("StartTime"));
				double maxSnowDepth = (Double) dataMap.get("MaxDepth");
				snowAreaResult.setMaxDepth(CommonTool.roundDouble(maxSnowDepth));
				int maxStations = (Integer) dataMap.get("MaxStations");
				snowAreaResult.setMaxStations(maxStations);
				int persistDays = ((Long) dataMap.get("PersistDays")).intValue();
				snowAreaResult.setPersistDays(persistDays);
				//TODO， 在不计算综合指数的情况下，入库。
				Double IA = (persistDays - MINPERSISTDAYS + 0.0) / (MAXPERSISTDAYS - MINPERSISTDAYS);
				Double IB = (maxStations - LMIN + 0.0) / (LMAX - LMIN);
				Double IC = (avgSnowDepth - MINDEPTH) / (MAXDEPTH - MINDEPTH);
				Double ID = (maxSnowDepth - MINPROCESSDEPTH) / (MAXPROCESSDEPTH - MINPROCESSDEPTH);
				double strength = snowAreaParam.getIA() * IA + snowAreaParam.getIB() * IB + snowAreaParam.getIC() * IC + snowAreaParam.getID() * ID; 
				snowAreaResult.setStrength(CommonTool.roundDouble2(strength));
				if(strength > snowAreaParam.getLevel1() && strength < snowAreaParam.getLevel2()) {
					snowAreaResult.setLevel("轻度");
				} else if(strength >= snowAreaParam.getLevel2() && strength < snowAreaParam.getLevel3()) {
					snowAreaResult.setLevel("中度");
				} else if(strength >= snowAreaParam.getLevel3() && strength < snowAreaParam.getLevel4()) {
					snowAreaResult.setLevel("重度");
				} else if(strength >= snowAreaParam.getLevel4()) {
					snowAreaResult.setLevel("特重");
				}
				snowAreaResultList.add(snowAreaResult);
			}
		}
		return snowAreaResultList;
	}
	
	private Map<Integer, List<LinkedHashMap>> querySnow(TimesParam timesParam, List<String> Station_Id_Cs, int startYear, int endYear) {
		ISnow snowImpl = (ISnow)ContextLoader.getCurrentWebApplicationContext().getBean("SnowImpl");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(timesParam.getStartTimeStr());
			endDate = sdf.parse(timesParam.getEndTimeStr());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>(); 
		if(isOverYear) {
			HashMap paramMap = new HashMap();
			String startTimeStr = timesParam.getStartTimeStr();
			int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
			String endTimeStr = timesParam.getEndTimeStr();
			try {
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)) - 1);
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			paramMap.put("startYear", startYear - 1);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = snowImpl.querySnowBySameYears(paramMap);
			for(LinkedHashMap itemMap : listResult) {
				Integer year = (Integer) itemMap.get("year");
				String mmdd = (String) itemMap.get("MMDD");
				int mmddInt = Integer.parseInt(mmdd);
				
				List<LinkedHashMap> tempList = map.get(year);
				if(tempList == null) {
					tempList = new ArrayList<LinkedHashMap>();
				}
				tempList.add(itemMap);
				if(mmddInt >= startTimeInt) { //年底的算在第二年
					map.put(year + 1, tempList);
				} else {
					map.put(year, tempList);
				}
			}
		} else {
			HashMap paramMap = new HashMap();
			try {
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = snowImpl.querySnowBySameYears(paramMap);
			for(LinkedHashMap itemMap : listResult) {
				Integer year = (Integer) itemMap.get("year");
				List<LinkedHashMap> tempList = map.get(year);
				if(tempList == null) {
					tempList = new ArrayList<LinkedHashMap>();
				}
				tempList.add(itemMap);
				map.put(year, tempList);
			}
		}
		return map;
	}
}
