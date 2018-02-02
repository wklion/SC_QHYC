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

import org.springframework.web.context.ContextLoader;

import com.spd.common.ThundResult;
import com.spd.common.ThundSequenceResult;
import com.spd.common.ThundTotalResult;
import com.spd.common.ThundYearResult;
import com.spd.common.ThundYearsParam;
import com.spd.common.TimesParam;
import com.spd.service.IThund;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 雷暴
 * @author Administrator
 *
 */
public class ThundBus {

	/**
	 * 按时间段统计雷暴
	 * @param timesParam
	 * @return
	 */
	public ThundResult thundByRange(TimesParam timesParam) {
		StationArea stationArea = new StationArea();
		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		ThundResult thundResult = new ThundResult();
		IThund thundImpl = (IThund)ContextLoader.getCurrentWebApplicationContext().getBean("ThundImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> resultList = thundImpl.queryThundByRange(paramMap);
		//逐次
		List<ThundSequenceResult> thundSequenceResultList = new ArrayList<ThundSequenceResult>();
		for(int i = 0; i < resultList.size(); i++) {
			ThundSequenceResult thundSequenceResult = new ThundSequenceResult();
			LinkedHashMap map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String datetime = (String) map.get("datetime");
			thundSequenceResult.setStation_Id_C(station_Id_C);
			thundSequenceResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			thundSequenceResult.setDatetime(datetime);
			thundSequenceResultList.add(thundSequenceResult);
		}
		thundResult.setThundSequenceResultList(thundSequenceResultList);
		//合计
		List<ThundTotalResult> thundTotalResultList = new ArrayList<ThundTotalResult>();
		Map<String, List<LinkedHashMap>> resultMap = new HashMap<String, List<LinkedHashMap>>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			List<LinkedHashMap> list = resultMap.get(station_Id_C);
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(map);
			resultMap.put(station_Id_C, list);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			ThundTotalResult thundTotalResult = new ThundTotalResult();
			String key = it.next();
			List<LinkedHashMap> list = resultMap.get(key);
			String station_Name = CommonUtil.getInstance().stationNameMap.get(key);
			thundTotalResult.setStation_Id_C(key);
			thundTotalResult.setCnt(list.size());
			thundTotalResult.setStation_Name(station_Name);
			thundTotalResult.setArea(stationAreaMap.get(key));
			thundTotalResultList.add(thundTotalResult);
		}
		thundResult.setThundTotalResultList(thundTotalResultList);
		return thundResult;
	}
	
	public List<ThundYearResult> thundByYears(ThundYearsParam thundYearsParam) {
		List<ThundYearResult> resultList = new ArrayList<ThundYearResult>();
		//历年
		List<ThundYearResult> overYearResult = queryByYears(thundYearsParam, thundYearsParam.getStartYear(), thundYearsParam.getEndYear());
		//常年
		List<ThundYearResult> yearResult = queryByYears(thundYearsParam, thundYearsParam.getPerennialStartYear(), thundYearsParam.getPerennialEndYear());
		double yearsCnt = 0;
		for(int i = 0; i < yearResult.size(); i++) {
			ThundYearResult thundYearResult = yearResult.get(i);
			double itemYearsCnt = thundYearResult.getYearsCnt();
			yearsCnt += itemYearsCnt;
		}
		//常年次数
		yearsCnt = CommonTool.roundDouble(yearsCnt / (thundYearsParam.getEndYear() - thundYearsParam.getStartYear() + 1));
		//对比结果
		for(int i = thundYearsParam.getStartYear(); i <= thundYearsParam.getEndYear(); i++) {
			boolean flag = false;
			for(int j = 0; j < overYearResult.size(); j++) {
				ThundYearResult thundYearResult = overYearResult.get(j);
				int year = thundYearResult.getYear();
				if(i == year) {
					ThundYearResult resultThundYearResult = new ThundYearResult();
					double curYearsCnt = thundYearResult.getYearsCnt();
					resultThundYearResult.setYear(i);
					resultThundYearResult.setYearsCnt(yearsCnt);
					resultThundYearResult.setCurrentCnt(curYearsCnt);
					resultThundYearResult.setAnomaly(CommonTool.roundDouble((curYearsCnt - yearsCnt) / yearsCnt * 100));
					resultList.add(resultThundYearResult);
					flag = true;
					break;
				} 
			}
			if(!flag) {
				ThundYearResult resultThundYearResult = new ThundYearResult();
				resultThundYearResult.setYear(i);
				resultThundYearResult.setYearsCnt(yearsCnt);
				resultList.add(resultThundYearResult);
			}
		}
		return resultList;
	}
	
	private List<ThundYearResult> queryByYears(ThundYearsParam thundYearsParam, int startYear, int endYear) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		IThund thundImpl = (IThund)ContextLoader.getCurrentWebApplicationContext().getBean("ThundImpl");
		TimesParam timesParam = thundYearsParam.getTimesParam();
		List<ThundYearResult> thundYearResultList = new ArrayList<ThundYearResult>();
		String[] stations = thundYearsParam.getStation_Id_Cs();
		List<String> station_Id_Cs = new ArrayList<String>();
		for(int i = 0; i < stations.length; i++) {
		  station_Id_Cs.add(stations[i]);
		}
		
		//1. 统计当年的
		HashMap currentMap = new HashMap();
		currentMap.put("startTime", timesParam.getStartTimeStr());
		currentMap.put("endTime", timesParam.getEndTimeStr());
		currentMap.put("Station_Id_Cs", station_Id_Cs);
		List<LinkedHashMap> currentThundCntList = thundImpl.queryThundCntByRange(currentMap);
		Long currentCnt = (Long) currentThundCntList.get(0).get("count");
		//2 .统计历年的
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>(); 
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(timesParam.getStartTimeStr());
			endDate = sdf.parse(timesParam.getEndTimeStr());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		if(isOverYear) {
			int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
			HashMap paramMap = new HashMap();
			paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
			paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			List<LinkedHashMap> resultList = thundImpl.queryThundByOverYears(paramMap);
			for(LinkedHashMap itemMap : resultList) {
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
			paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
			paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			List<LinkedHashMap> resultList = thundImpl.queryThundBySameYears(paramMap);
			for(LinkedHashMap itemMap : resultList) {
				Integer year = (Integer) itemMap.get("year");
				List<LinkedHashMap> tempList = map.get(year);
				if(tempList == null) {
					tempList = new ArrayList<LinkedHashMap>();
				}
				tempList.add(itemMap);
				map.put(year, tempList);
			}
		}
		//3. 对比结果
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List<LinkedHashMap> list = map.get(year);
			ThundYearResult thundYearResult = new ThundYearResult();
			thundYearResult.setYear(year);
			thundYearResult.setYearsCnt(list.size());
			thundYearResult.setCurrentCnt(currentCnt);
			thundYearResult.setAnomaly(list.size() - currentCnt);
			thundYearResultList.add(thundYearResult);
		}
		return thundYearResultList;
	}
	
}
