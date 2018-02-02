package com.spd.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.DisasterRainStormFinResult;
import com.spd.common.DisasterRainStormResult;
import com.spd.common.DisasterRainStormTotalResult;
import com.spd.common.RainStormRangeParam;
import com.spd.common.RainStormYearsParam;
import com.spd.common.RainStormYearsResult;
import com.spd.common.TimesParam;
import com.spd.service.IFrost;
import com.spd.service.IRainStorm;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 暴雨
 * @author Administrator
 *
 */
public class RainStormBus {

	/**
	 * 按时间段统计暴雨
	 * @param rainStormRangeParam
	 * @return
	 */
	public DisasterRainStormFinResult rainStormByRange(RainStormRangeParam rainStormRangeParam) {
//		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap =  stationArea.getStationAreaMap();
		DisasterRainStormFinResult disasterRainStormFinResult = new DisasterRainStormFinResult();
		double level1 = rainStormRangeParam.getLevel1();
		double level2 = rainStormRangeParam.getLevel2();
		double level3 = rainStormRangeParam.getLevel3();
		TimesParam timesParam = rainStormRangeParam.getTimesParam();
		IRainStorm rainStorm = (IRainStorm)ContextLoader.getCurrentWebApplicationContext().getBean("RainStormImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("tableName", rainStormRangeParam.getTableName());
		paramMap.put("level1", level1);
		List<LinkedHashMap> resultList = null;
		if(rainStormRangeParam.getStation_Id_Cs() == null) {
			String areaType = rainStormRangeParam.getStationType();
			if(areaType.equals("AWS")) {
				paramMap.put("station_Id_C", "5%");
			} else {
				paramMap.put("station_Id_C", "%");
			}
			resultList = rainStorm.queryRainStormByTimes(paramMap);
		} else {
			List<String> Station_Id_Cs = new ArrayList<String>();
			String[] station_Id_CArray = rainStormRangeParam.getStation_Id_Cs();
			for(int i=0; i<station_Id_CArray.length; i++) {
				Station_Id_Cs.add(station_Id_CArray[i]);
			}
			paramMap.put("Station_Id_C", Station_Id_Cs);
			resultList = rainStorm.queryRainStormByTimesAndStations(paramMap);
		}
		List<DisasterRainStormResult> disasterRainStormResultList = new ArrayList<DisasterRainStormResult>();
		Map<String, List<DisasterRainStormResult>> map = new HashMap<String, List<DisasterRainStormResult>>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			DisasterRainStormResult disasterRainStormResult = new DisasterRainStormResult();
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			String datetime = (String) itemMap.get("datetime");
			Double pre = (Double) itemMap.get("Pre");
			String area = (String) itemMap.get("area");
			String level = "";
			if(pre >= level1 && pre < level2) {
				level = "暴雨";
			} else if(pre > level2 && pre < level3) {
				level = "大暴雨";
			} else if(pre >= level3) {
				level = "特大暴雨";
			}
			disasterRainStormResult.setDatetime(datetime);
			disasterRainStormResult.setLevel(level);
			disasterRainStormResult.setStation_Id_C(station_Id_C);
			disasterRainStormResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			disasterRainStormResult.setValue(pre);
//			disasterRainStormResult.setArea(area);
			disasterRainStormResult.setArea(CommonUtil.getInstance().stationAreaMap.get(station_Id_C));
			disasterRainStormResultList.add(disasterRainStormResult);
			List<DisasterRainStormResult> itemList = map.get(station_Id_C);
			if(itemList == null) {
				itemList = new ArrayList<DisasterRainStormResult>();
			}
			itemList.add(disasterRainStormResult);
			map.put(station_Id_C, itemList);
		}
		//逐次
		disasterRainStormFinResult.setSeqResult(disasterRainStormResultList);
		//合计
		List<DisasterRainStormTotalResult> totalResult = new ArrayList<DisasterRainStormTotalResult>();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<DisasterRainStormResult> itemList = map.get(key);
			DisasterRainStormTotalResult disasterRainStormTotalResult = new DisasterRainStormTotalResult();
			disasterRainStormTotalResult.setStation_Id_C(key);
//			disasterRainStormTotalResult.setArea(stationAreaMap.get(key));
			disasterRainStormTotalResult.setSum(itemList.size());
			int level1Cnt = 0, level2Cnt = 0, level3Cnt = 0;
			double extValue = 0;
			String extDatetime = "";
			for(int i = 0 ; i < itemList.size(); i++) {
				DisasterRainStormResult disasterRainStormResult = itemList.get(i);
//				disasterRainStormTotalResult.setArea(disasterRainStormResult.getArea());
				disasterRainStormTotalResult.setArea(CommonUtil.getInstance().stationAreaMap.get(key));
				disasterRainStormTotalResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
				String level = disasterRainStormResult.getLevel();
				if("暴雨".equals(level)) {
					level1Cnt++;
				} else if("大暴雨".equals(level)) {
					level2Cnt++;
				} else if("特大暴雨".equals(level)) {
					level3Cnt++;
				}
				double value = disasterRainStormResult.getValue();
				if(value > extValue) {
					extValue = value;
				}
			}
			for(int i = 0 ; i < itemList.size(); i++) {
				DisasterRainStormResult disasterRainStormResult = itemList.get(i);
				double value = disasterRainStormResult.getValue();
				String datetime = disasterRainStormResult.getDatetime();
				if(value == extValue) {
					extDatetime = extDatetime + datetime + " "; 
				}
			}
			extDatetime.trim();
			disasterRainStormTotalResult.setLevel1Cnt(level1Cnt);
			disasterRainStormTotalResult.setLevel2Cnt(level2Cnt);
			disasterRainStormTotalResult.setLevel3Cnt(level3Cnt);
			disasterRainStormTotalResult.setExtValue(extValue);
			disasterRainStormTotalResult.setExtDatetime(extDatetime);
			totalResult.add(disasterRainStormTotalResult);
		}
		disasterRainStormFinResult.setTotalResult(totalResult);
		return disasterRainStormFinResult;
	}
	
	/**
	 * 新的
	 * @param rainStormYearsParam
	 * @return
	 */
	public List<RainStormYearsResult> rainstormByYears(RainStormYearsParam rainStormYearsParam) {
		List<RainStormYearsResult>  rainStormYearsResultList = new ArrayList<RainStormYearsResult>();
		//历年
		List<RainStormYearsResult> overListResult = getRainstormByYears(rainStormYearsParam);
		//常年
		RainStormYearsParam rainStormYearsParam2 = new RainStormYearsParam();
		rainStormYearsParam2.setEndYear(rainStormYearsParam.getPerennialEndYear());
		rainStormYearsParam2.setLevel1(rainStormYearsParam.getLevel1());
		rainStormYearsParam2.setLevel2(rainStormYearsParam.getLevel2());
		rainStormYearsParam2.setLevel3(rainStormYearsParam2.getLevel3());
		rainStormYearsParam2.setStartYear(rainStormYearsParam.getPerennialStartYear());
		rainStormYearsParam2.setStation_Id_Cs(rainStormYearsParam.getStation_Id_Cs());
		rainStormYearsParam2.setTableName(rainStormYearsParam.getTableName());
		rainStormYearsParam2.setTimesParam(rainStormYearsParam.getTimesParam());
		List<RainStormYearsResult> yearsListResult =  getRainstormByYears(rainStormYearsParam2);
		double cnt = 0, level1Cnt = 0, level2Cnt = 0, level3Cnt = 0;
		for(int i = 0; i < yearsListResult.size(); i++) {
			RainStormYearsResult rainStormYearsResult = yearsListResult.get(i);
			cnt += rainStormYearsResult.getCnt();
			level1Cnt += rainStormYearsResult.getLevel1Cnt();
			level2Cnt += rainStormYearsResult.getLevel2Cnt();
			level3Cnt += rainStormYearsResult.getLevel3Cnt();
		}
		cnt = CommonTool.roundDouble(cnt / (rainStormYearsParam.getPerennialEndYear() - rainStormYearsParam.getPerennialStartYear() + 1));
		level1Cnt = CommonTool.roundDouble(level1Cnt / (rainStormYearsParam.getPerennialEndYear() - rainStormYearsParam.getPerennialStartYear() + 1));
		level2Cnt = CommonTool.roundDouble(level2Cnt / (rainStormYearsParam.getPerennialEndYear() - rainStormYearsParam.getPerennialStartYear() + 1));
		level3Cnt = CommonTool.roundDouble(level3Cnt / (rainStormYearsParam.getPerennialEndYear() - rainStormYearsParam.getPerennialStartYear() + 1));
		//对比历年和常年
		for(int i = rainStormYearsParam.getStartYear(); i <= rainStormYearsParam.getEndYear(); i++) {
			boolean flag = false;
			for(int j = 0; j < overListResult.size(); j++) {
				RainStormYearsResult rainStormYearsResult = overListResult.get(j);
				int year = rainStormYearsResult.getYear();
				if(i == year) {
					rainStormYearsResult.setYearsCnt(cnt);
					rainStormYearsResult.setYearsLevel1Cnt(level1Cnt);
					rainStormYearsResult.setYearsLevel2Cnt(level2Cnt);
					rainStormYearsResult.setYearsLevel3Cnt(level3Cnt);
					rainStormYearsResult.setCntAnomaly(CommonTool.roundDouble(rainStormYearsResult.getCnt() - cnt));
					if(cnt != 0) {
						rainStormYearsResult.setCntAnomalyRate(CommonTool.roundDouble((rainStormYearsResult.getCnt() - cnt ) / cnt * 100.0));
					} else {
						rainStormYearsResult.setCntAnomalyRate(0);
					}
					rainStormYearsResultList.add(rainStormYearsResult);
					flag = true;
					break;
				}
			}
			if(!flag) {
				RainStormYearsResult rainStormYearsResult = new RainStormYearsResult();
				rainStormYearsResult.setYear(i);
				rainStormYearsResult.setYearsCnt(cnt);
				rainStormYearsResult.setYearsLevel1Cnt(level1Cnt);
				rainStormYearsResult.setYearsLevel2Cnt(level2Cnt);
				rainStormYearsResult.setYearsLevel3Cnt(level3Cnt);
				rainStormYearsResultList.add(rainStormYearsResult);
			}
		}
		return rainStormYearsResultList;
	}
	
	private List<RainStormYearsResult> getRainstormByYears(RainStormYearsParam rainStormYearsParam) {
		IRainStorm rainStormImpl = (IRainStorm)ContextLoader.getCurrentWebApplicationContext().getBean("RainStormImpl");
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		TimesParam timesParam = rainStormYearsParam.getTimesParam();
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>();
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
		if(isOverYear) {
			HashMap paramMap = new HashMap();
			paramMap.put("startTime", mmddSDF.format(timesParam.getStartDate()));
			paramMap.put("endTime", mmddSDF.format(timesParam.getEndDate()));
			paramMap.put("level1", rainStormYearsParam.getLevel1());
			paramMap.put("startYear", rainStormYearsParam.getStartYear() - 1);
			paramMap.put("endYear", rainStormYearsParam.getEndYear());
			paramMap.put("tableName", rainStormYearsParam.getTableName());
			List<String> Station_Id_Cs = new ArrayList<String>();
			String[] station_Id_CArray = rainStormYearsParam.getStation_Id_Cs();
			for(int i=0; i<station_Id_CArray.length; i++) {
				Station_Id_Cs.add(station_Id_CArray[i]);
			}
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> resultList = rainStormImpl.queryRainStormByOverYearAndStations(paramMap);
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
			paramMap.put("startTime", mmddSDF.format(timesParam.getStartDate()));
			paramMap.put("endTime", mmddSDF.format(timesParam.getEndDate()));
			paramMap.put("level1", rainStormYearsParam.getLevel1());
			paramMap.put("startYear", rainStormYearsParam.getStartYear());
			paramMap.put("endYear", rainStormYearsParam.getEndYear());
			paramMap.put("tableName", rainStormYearsParam.getTableName());
			List<String> Station_Id_Cs = new ArrayList<String>();
			String[] station_Id_CArray = rainStormYearsParam.getStation_Id_Cs();
			for(int i=0; i<station_Id_CArray.length; i++) {
				Station_Id_Cs.add(station_Id_CArray[i]);
			}
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> resultList = rainStormImpl.queryRainStormBySameYearAndStations(paramMap);
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
		List<RainStormYearsResult> resultList = new ArrayList<RainStormYearsResult>();
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()) {
			Integer key = it.next();
			RainStormYearsResult rainStormYearsResult = new RainStormYearsResult();
			rainStormYearsResult.setYear(key);
			int level1Cnt = 0, level2Cnt = 0, level3Cnt = 0;
			double extValue = 0;
			List<LinkedHashMap> tempList = map.get(key);
			rainStormYearsResult.setCnt(tempList.size());
			for(int i = 0; i < tempList.size(); i++) {
				LinkedHashMap itemMap  = tempList.get(i);
				double preValue = (Double) itemMap.get("Pre");
				if(preValue >= rainStormYearsParam.getLevel1() && preValue < rainStormYearsParam.getLevel2()) {
					level1Cnt++;
				} else if(preValue >= rainStormYearsParam.getLevel2() && preValue < rainStormYearsParam.getLevel3()) {
					level2Cnt++;
				} else if(preValue >= rainStormYearsParam.getLevel3()) {
					level3Cnt++;
				}
				if(preValue > extValue) {
					extValue = preValue;
				}
			}
			String extDattime = "";
			for(int i = 0; i < tempList.size(); i++) {
				LinkedHashMap itemMap  = tempList.get(i);
				double preValue = (Double) itemMap.get("Pre");
				String datetime = (String) itemMap.get("datetime");
				if(preValue == extValue) {
					extDattime = extDattime + datetime + " ";
				}
			}
			rainStormYearsResult.setLevel1Cnt(level1Cnt);
			rainStormYearsResult.setLevel2Cnt(level2Cnt);
			rainStormYearsResult.setLevel3Cnt(level3Cnt);
			rainStormYearsResult.setExtValue(extValue);
			rainStormYearsResult.setExtDatetime(extDattime);
			resultList.add(rainStormYearsResult);
		}
		return resultList;
	}
	
	private RainStormRangeParam chgRainStormYears2RangeParam(RainStormYearsParam rainStormYearsParam) {
		RainStormRangeParam rainStormRangeParam = new RainStormRangeParam();
		rainStormRangeParam.setLevel1(rainStormYearsParam.getLevel1());
		rainStormRangeParam.setLevel2(rainStormYearsParam.getLevel2());
		rainStormRangeParam.setLevel3(rainStormYearsParam.getLevel3());
		rainStormRangeParam.setStation_Id_Cs(rainStormYearsParam.getStation_Id_Cs());
		rainStormRangeParam.setTableName(rainStormYearsParam.getTableName());
		rainStormRangeParam.setTimesParam(rainStormYearsParam.getTimesParam());
		return rainStormRangeParam;
	}
}
