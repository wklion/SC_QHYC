package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.RainStormAreaParam;
import com.spd.common.RainStormAreaResult;
import com.spd.common.RainStormEvaluateResult;
import com.spd.common.TimesParam;
import com.spd.service.IRainStorm;

/**
 * 暴雨评估
 * @author Administrator
 *
 */
public class RainStormEvaluateBus {

	/**
	 * 指定时间段统计暴雨
	 * @param rainStormAreaParam
	 * @return
	 */
	public RainStormAreaResult rainstormByRange(RainStormAreaParam rainStormAreaParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RainStormAreaResult rainStormAreaResult = new RainStormAreaResult();
		IRainStorm rainStorm = (IRainStorm)ContextLoader.getCurrentWebApplicationContext().getBean("RainStormImpl");
		//1. 查询在指定的时间段内的暴雨过程。
		HashMap paramMap = new HashMap();
		paramMap.put("minDayPre", rainStormAreaParam.getMinDayPre());
		paramMap.put("startTime", rainStormAreaParam.getTimesParam().getStartTimeStr());
		paramMap.put("endTime", rainStormAreaParam.getTimesParam().getEndTimeStr());
		paramMap.put("minDayStations", rainStormAreaParam.getMinDayStations());
		List<LinkedHashMap> resultList = rainStorm.rainstormByRange(paramMap);
		List<String> dateTimeStrList = new ArrayList<String>();
		for(LinkedHashMap itemMap : resultList) {
			String datetime = (String) itemMap.get("datetime");
			dateTimeStrList.add(datetime);
		}
		Map<String, List<String>> stationDateMap = new HashMap<String, List<String>>();
		//2. 查询指定时间里，单日满足条件的站
		for(String datetime : dateTimeStrList) {
			HashMap stationsRainStormParamMap = new HashMap();
			stationsRainStormParamMap.put("minDayPre", rainStormAreaParam.getMinDayPre());
			stationsRainStormParamMap.put("datetime", datetime);
			List<LinkedHashMap> stationsRainStormList = rainStorm.queryRainStormStationsByTime(stationsRainStormParamMap);
			List<String> stationList = new ArrayList<String>();
			for(LinkedHashMap itemMap : stationsRainStormList) {
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				stationList.add(station_Id_C);
			}
			stationDateMap.put(datetime, stationList);
		}
		//3. 查询结果站，前后时间段推算10天，判断连续的日期
		List<LinkedHashMap> rainstormList = new ArrayList<LinkedHashMap>();
		Iterator<String> it = stationDateMap.keySet().iterator();
		while(it.hasNext()) {
			String datetime = it.next();
			List<String> station_Id_Cs = stationDateMap.get(datetime);
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
				break;
			}
			Date startDate = new Date(date.getTime() - 10 * CommonConstant.DAYTIMES);
			Date endDate = new Date(date.getTime() + 10 * CommonConstant.DAYTIMES);
			String startStr = sdf.format(startDate);
			String endStr = sdf.format(endDate);
			HashMap stationsAndDatetimeParamMap = new HashMap();
			stationsAndDatetimeParamMap.put("startTime", startStr);
			stationsAndDatetimeParamMap.put("endTime", endStr);
			stationsAndDatetimeParamMap.put("Station_Id_C", station_Id_Cs);
			stationsAndDatetimeParamMap.put("minDayPre", rainStormAreaParam.getMinDayPre());
			List<LinkedHashMap> resultListItem = rainStorm.queryRainStormByTimeAndStations(stationsAndDatetimeParamMap);
			//1。求满足条件的站，日期，值。
			Map<String, List<LinkedHashMap>> resultMap = new HashMap<String, List<LinkedHashMap>>();
			for(LinkedHashMap itemMap : resultListItem) {
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				List<LinkedHashMap> list = resultMap.get(station_Id_C);
				if(list == null) {
					list = new ArrayList<LinkedHashMap>();
				}
				list.add(itemMap);
				resultMap.put(station_Id_C, list);
			}
			//满足暴雨条件的List<LinkedHashMap>
			Iterator<String> it2 = resultMap.keySet().iterator();
//			2016-05-21 2016-05-22 2016-05-23 2016-05-24 2016-05-25 2016-05-26 2016-05-27
			while(it2.hasNext()) {
				String station_Id_C = it2.next();
				List<LinkedHashMap> list = resultMap.get(station_Id_C);
				Date tempDate = date;
				// 往前推算，找到全部连续，且满足条件的结果
				for(int i = list.size() - 1; i >=0 ; i--) {
					LinkedHashMap itemMap = list.get(i);
					String itemDatetime = (String) itemMap.get("datetime");
					Date itemDate = null;
					try {
						itemDate = sdf.parse(itemDatetime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(itemDate.getTime() > tempDate.getTime()) {
						continue;
					}
					if(itemDate.getTime() == tempDate.getTime()) {
						rainstormList.add(itemMap);
						continue;
					}
					if(tempDate.getTime() - itemDate.getTime() == CommonConstant.DAYTIMES) {
						rainstormList.add(itemMap);
						tempDate = itemDate;
					} else {
						break;
					}
				}
				// 往后推算，找到全部连续，且满足条件的结果
				tempDate = date; // 重置
				for(int i = 0; i < list.size(); i++) {
					LinkedHashMap itemMap = list.get(i);
					String itemDatetime = (String) itemMap.get("datetime");
					Date itemDate = null;
					try {
						itemDate = sdf.parse(itemDatetime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(itemDate.getTime() < tempDate.getTime()) {
						continue;
					}
					if(itemDate.getTime() - tempDate.getTime() == CommonConstant.DAYTIMES) {
						rainstormList.add(itemMap);
						tempDate = itemDate;
					}
				}
			}
		}
		
		//rainstormList 是满足条件的全部结果。
		double R = calcR(rainstormList, rainStormAreaParam);
		return rainStormAreaResult;
	}
	

	private double calcR(List<LinkedHashMap> rainstormList, RainStormAreaParam rainStormAreaParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		double sumRain = 0, maxRain = 0;
		int ic = 0, presistTime = 0;
		Date startDate = null, endDate = null;
		long startTime = Long.MAX_VALUE, endTime = 0;
		for(int i = 0; i < rainstormList.size(); i++) {
			LinkedHashMap itemMap = rainstormList.get(i);
			double pre = (Double) itemMap.get("Pre");
			String datetimeStr = (String) itemMap.get("datetime");
			Date date = null;
			try {
				date = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long time = date.getTime();
			if(time < startTime) {
				startTime = time;
			}
			if(time > endTime) {
				endTime = time;
			}
			sumRain += pre;
			if(pre > maxRain) {
				maxRain = pre;
			}
		}
		startDate = new Date(startTime);
		endDate = new Date(endTime);
		ic = rainstormList.size();
		Long presistTimeLong = (endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES;
		presistTime = presistTimeLong.intValue();
		//R=0.25*IA+0.4*IB+0.25*IC+0.1*ID
		double R = rainStormAreaParam.getWeight1() * sumRain + rainStormAreaParam.getWeight2() * maxRain +
				rainStormAreaParam.getWeight3() * ic + rainStormAreaParam.getWeight4() * presistTime;
		return R;
	}
}
