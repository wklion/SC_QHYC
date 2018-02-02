package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.cq.impl.AreaHighTmpAlertDao;
import com.spd.dao.cq.impl.AreaHighTmpDao;
import com.spd.dao.cq.impl.ForecastDataDao;
import com.spd.dao.cq.impl.HighTmpDaoImpl;
import com.spd.dao.cq.impl.StationHighTmpAlertDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 区域高温预警
 * @author Administrator
 *
 */
public class AreaHighTmpAlertSync {

	private StationHighTmpAlertDao stationHighTmpAlertDao = new StationHighTmpAlertDao();
	
	private AreaHighTmpAlertDao areaHighTmpAlertDao = new AreaHighTmpAlertDao();
	
	private HighTmpDaoImpl highTmpDaoImpl = new HighTmpDaoImpl();

	private ForecastDataDao forecastDataDao = new ForecastDataDao();
	
	private static double HIGHTMP = 35; //满足高温的温度
	
	private static int MINAREAHIGHTMPSTATIONS = 7;//满足高温的最低站数

	private static int MAXAREAHIGHTMPSTATIONS = 17;//满足高温的最高站数

	private static int CHONGQINGSTATIONCNT = 34;//重庆自动站个数
	
	public void sync(String datetime) {
		//1. 查询实况的高温
		List realResultList = getHighTmpByDatetime(datetime);
		//2. 满足区域高温的情况下，查询预报的高温
		if(realResultList == null || realResultList.size() <= MINAREAHIGHTMPSTATIONS) return;
		List forecastResultList = getForecastHighTmpByTime(datetime);
		//3. 判断预报的数据是否符合要求，如果预报的符合要求，则往回查找实况数据，直到找到不符合要求
		String maxForecastDate = analystForecast(datetime, forecastResultList);
		if(maxForecastDate.equals(datetime)) return; //没有预报数据，或者预报的数据里没有满足高温条件的
		//4. 往回查找实况，直到找到最早的一天数据
		String minObservDate = analystObservt(datetime);
		//5. 在minObservDate和datetime之间，查找实况值，datetime和maxForecastDate 之间，查找预报值，如果其中有一天的天数 >=MAXAREAHIGHTMPSTATIONS,则满足条件
		boolean isObservMaxDays = queryObservMaxDays(minObservDate, datetime);
		boolean isForecastMaxDays = queryForecastMaxDays(maxForecastDate, datetime);
		if(isObservMaxDays || isForecastMaxDays) {
			//6 满足高温， 根据开始，结束时间，计算指标
			//6.1 计算实况的SI
			HashMap<String, Integer> observSIMap = caleObservSI(minObservDate, datetime);
			//6.2 计算预报的SI，6.3 计算实况的G，计算预报的G，6.4计算实况的Ri，6.5计算预报的RI
			int hourspan = caleHoursBetweenTimes(datetime, maxForecastDate);
			HashMap<String, Integer> forecastSIMap = caleForecastSI(datetime, hourspan);
			// 6.3 合并实况和预报的SI
			HashMap<String, Integer> resultSIMap = addSIMap(observSIMap, forecastSIMap);
			// 6.4 计算出G
			HashMap<String, Integer> gMap  = caleGMap(resultSIMap);
			//单站高温入库
			insertStationTmp(resultSIMap, gMap, minObservDate, maxForecastDate, datetime);
			// 6.5 计算RI
			Double RI = caleG2RI(gMap);
			// 6.6 计算DI
			Integer DI = chgRI2DI(RI);
			// 6.7 计算级别
			String level = getLevelByRI(RI);
			// 6.8 入库
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", minObservDate + " 00:00:00");
			dataMap.put("EndTime", maxForecastDate + " 00:00:00");
			dataMap.put("ForecastDate", datetime + " 00:00:00");
			dataMap.put("RI", RI);
			dataMap.put("DI", DI);
			dataMap.put("level", level);
			List dataList = new ArrayList();
			dataList.add(dataMap);
			areaHighTmpAlertDao.insertTemAvgHouValue(dataList, datetime);
		}
	}
	
	/**
	 * 单站高温预警入库
	 * @param dataList
	 */
	private void insertStationTmp(HashMap<String, Integer> resultSIMap, HashMap<String, Integer> gMap, String startTime, String endTime, String forecastTime) {
		Iterator<String> it = resultSIMap.keySet().iterator();
		List dataList = new ArrayList();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			Integer SI = resultSIMap.get(station_Id_C);
			HashMap dataMap = new HashMap();
			dataMap.put("Station_Id_C", station_Id_C);
			dataMap.put("SI", SI);
			Integer g = gMap.get(station_Id_C);
			if(g == null) continue;
			dataMap.put("G", g);
			dataMap.put("StartTime", startTime + " 00:00:00");
			dataMap.put("EndTime", endTime + " 00:00:00");
			dataMap.put("ForecastDate", forecastTime + " 00:00:00");
			dataMap.put("level", getLevelByG(g));
			dataList.add(dataMap);
		}
		stationHighTmpAlertDao.insertTemAvgHouValue(dataList, forecastTime);
	}
	
	private int caleHoursBetweenTimes(String datetime, String maxDatetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null, maxDate = null;
		try {
			date = sdf.parse(datetime);
			maxDate = sdf.parse(maxDatetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int hoursSpan = ((Long)((maxDate.getTime() - date.getTime()) / CommonConstant.HOURTIMES)).intValue();
		return hoursSpan;
	}
	
	private String getLevelByRI(double RI) {
		if(RI >= 4) return "轻度";
		if(RI >= 3 && RI < 4) return "中度";
		if(RI >= 2 && RI < 3) return "重度";
		if(RI >= 1 && RI < 2) return "特重";
		return null;
	}
	
	private String getLevelByG(double G) {
		if(G == 5) return "无高温";
		if(G == 4) return "轻度";
		if(G == 3) return "中度";
		if(G == 2) return "重度";
		if(G == 1) return "特重";
		return null;
	}
	
	private Integer chgRI2DI(Double RI) {
		if(RI >= 1 && RI < 2) return 4;
		if(RI >= 2 && RI < 3) return 3;
		if(RI >=3 && RI < 4) return 2;
		if(RI >= 4) return 1;
		return -1;
	}
	
	/**
	 * 根据g计算RI
	 * @param gMap
	 * @return
	 */
	private Double caleG2RI(HashMap<String, Integer> gMap) {
		HashMap<Integer, Integer> gCntMap = new HashMap<Integer, Integer>();
		Iterator<String> it = gMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Integer si = gMap.get(key);
			if(gCntMap.containsKey(si)) {
				gCntMap.put(si, gCntMap.get(si) + 1);
			} else {
				gCntMap.put(si, 1);
			}
		}
		Double RI = 0.0;
		Iterator<Integer> gIt = gCntMap.keySet().iterator();
		while(gIt.hasNext()) {
			Integer g = gIt.next();
			Integer gCnt = gCntMap.get(g);
			RI += g  * (gCnt / (CHONGQINGSTATIONCNT + 0.0));
		}
		return RI;
	}
	
	private HashMap<String, Integer> caleGMap(HashMap<String, Integer> siMap) {
		HashMap<String, Integer> gMap = new HashMap<String, Integer>();
		Iterator<String> it = siMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Integer si = siMap.get(key);
			Integer g = chgSI2G(si);
			gMap.put(key, g);
		}
		return gMap;
	}
	
	private Integer chgSI2G(Integer si) {
		Integer g = 0;
		if(si == 0) {
			g = 5;
		} else if(si >= 1 && si < 4) {
			g = 4;
		} else if(si >= 4 && si < 11) {
			g = 3;
		} else if(si >= 11 && si < 23) {
			g = 2;
		} else if(si >= 23) {
			g = 1;
		}
		return g;
	}
	
	private HashMap<String, Integer> addSIMap(HashMap<String, Integer> observSIMap, HashMap<String, Integer> forecastSIMap) {
		HashMap<String, Integer> resultSIMap = new HashMap<String, Integer>();
		Iterator<String> observIt = observSIMap.keySet().iterator();
		while(observIt.hasNext()) {
			String key = observIt.next();
			Integer si = observSIMap.get(key);
			if(resultSIMap.containsKey(key)) {
				si += resultSIMap.get(key);
			} 
			resultSIMap.put(key, si);
		}
		
		Iterator<String> forecastIt = observSIMap.keySet().iterator();
		while(forecastIt.hasNext()) {
			String key = forecastIt.next();
			Integer si = forecastSIMap.get(key);
			if(resultSIMap.containsKey(key)) {
				si += resultSIMap.get(key);
			} 
			resultSIMap.put(key, si);
		}
		return resultSIMap;
	}
	/**
	 * 计算预测的SI
	 * @param datetime
	 * @param hourspan
	 * @return
	 */
	private HashMap<String, Integer> caleForecastSI(String datetime, int hourspan) {
		HashMap<String, Integer> SIMap = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel1Days = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel2Days = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel3Days = new HashMap<String, Integer>();
		List list = forecastDataDao.getDataListByTimeAndHourspan(datetime, hourspan);
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Double TEM_Max = (Double) dataMap.get("MaxTmp");
			
			if(TEM_Max >= 35 && TEM_Max < 37) {
				Integer days = stationHighTmpLevel1Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel1Days.put(station_Id_C, days + 1);
			} else if (TEM_Max >= 37 && TEM_Max < 40) {
				Integer days = stationHighTmpLevel2Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel2Days.put(station_Id_C, days + 1);
			} else if (TEM_Max >= 40) {
				Integer days = stationHighTmpLevel3Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel3Days.put(station_Id_C, days + 1);
			}
		}
		//计算SI
		Set<String> set1 = stationHighTmpLevel1Days.keySet();
		Iterator it1 = set1.iterator();
		while(it1.hasNext()) {
			String key = (String) it1.next();
			Integer cnt = stationHighTmpLevel1Days.get(key);
			Integer temp1 = cnt * 1;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp1);
			} else {
				SIMap.put(key, SI + temp1);
			}
		}
		Set<String> set2 = stationHighTmpLevel2Days.keySet();
		Iterator it2 = set2.iterator();
		while(it2.hasNext()) {
			String key = (String) it2.next();
			Integer cnt = stationHighTmpLevel2Days.get(key);
			Integer temp2 = cnt * 2;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp2);
			} else {
				SIMap.put(key, SI + temp2);
			}
		}
		Set<String> set3 = stationHighTmpLevel3Days.keySet();
		Iterator it3 = set3.iterator();
		while(it3.hasNext()) {
			String key = (String) it3.next();
			Integer cnt = stationHighTmpLevel3Days.get(key);
			Integer temp3 = cnt * 3;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp3);
			} else {
				SIMap.put(key, SI + temp3);
			}
		}
		return SIMap;
	}
	
	private HashMap<String, Integer> caleObservSI(String startTime, String endTime) {
		HashMap<String, Integer> SIMap = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel1Days = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel2Days = new HashMap<String, Integer>();
		Map<String, Integer> stationHighTmpLevel3Days = new HashMap<String, Integer>();
		List list = highTmpDaoImpl.getDataByTimeRange(startTime, endTime);
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Double TEM_Max = (Double) dataMap.get("TEM_Max");
			
			if(TEM_Max >= 35 && TEM_Max < 37) {
				Integer days = stationHighTmpLevel1Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel1Days.put(station_Id_C, days + 1);
			} else if (TEM_Max >= 37 && TEM_Max < 40) {
				Integer days = stationHighTmpLevel2Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel2Days.put(station_Id_C, days + 1);
			} else if (TEM_Max >= 40) {
				Integer days = stationHighTmpLevel3Days.get(station_Id_C);
				if(days == null) {
					days = 0;
				}
				stationHighTmpLevel3Days.put(station_Id_C, days + 1);
			}
		}
		//计算SI
		Set<String> set1 = stationHighTmpLevel1Days.keySet();
		Iterator it1 = set1.iterator();
		while(it1.hasNext()) {
			String key = (String) it1.next();
			Integer cnt = stationHighTmpLevel1Days.get(key);
			Integer temp1 = cnt * 1;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp1);
			} else {
				SIMap.put(key, SI + temp1);
			}
		}
		Set<String> set2 = stationHighTmpLevel2Days.keySet();
		Iterator it2 = set2.iterator();
		while(it2.hasNext()) {
			String key = (String) it2.next();
			Integer cnt = stationHighTmpLevel2Days.get(key);
			Integer temp2 = cnt * 2;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp2);
			} else {
				SIMap.put(key, SI + temp2);
			}
		}
		Set<String> set3 = stationHighTmpLevel3Days.keySet();
		Iterator it3 = set3.iterator();
		while(it3.hasNext()) {
			String key = (String) it3.next();
			Integer cnt = stationHighTmpLevel3Days.get(key);
			Integer temp3 = cnt * 3;
			Integer SI = SIMap.get(key);
			if(SI == null) {
				SIMap.put(key, temp3);
			} else {
				SIMap.put(key, SI + temp3);
			}
		}
		return SIMap;
	}
	
	/**
	 * 查找指定时间范围内，观测数据有没有 >=MAXAREAHIGHTMPSTATIONS的一天
	 * @param minObservDate
	 * @param datetime
	 * @return
	 */
	private boolean queryObservMaxDays(String minObservDate, String datetime) {
		List observList = highTmpDaoImpl.getCntListByTimes(minObservDate, datetime, MAXAREAHIGHTMPSTATIONS);
		if(observList != null && observList.size() > 0) return true;
		return false;
	}
	
	/**
	 * 查找指定时间范围内，预测数据中有没有 >=MAXAREAHIGHTMPSTATIONS的一天
	 * @param minObservDate
	 * @param datetime
	 * @return
	 */
	private boolean queryForecastMaxDays(String maxForecastDate, String datetime) {
		int days = caleDays(maxForecastDate, datetime);
		List list = forecastDataDao.getCntListByTimes(datetime, days, MAXAREAHIGHTMPSTATIONS);
		if(list != null && list.size() > 0) return true;
		return false;
	}
	
	/**
	 * 计算两个日期相差的小时
	 * @param maxForecastDate
	 * @param datetime
	 * @return
	 */
	private int caleDays(String maxForecastDateStr, String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date maxForecastDate = null, date = null;
		try {
			maxForecastDate = sdf.parse(maxForecastDateStr);
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long times = maxForecastDate.getTime() - date.getTime();
		int days = ((Long)(times / CommonConstant.DAYTIMES)).intValue();
		int hours = days * 24;
		return hours;
	}
	/**
	 * 计算实况中的，满足条件的高温
	 * @param datetime
	 * @return
	 */
	private String analystObservt(String datetime) {
		String preDatetime = addDatetime(datetime, -1);
		List preDataList = highTmpDaoImpl.getDataListByTime(preDatetime);
		if(preDataList.size() < MINAREAHIGHTMPSTATIONS) {
			return datetime;
		} else {
			return analystObservt(preDatetime);
		}
	}
	
	/**
	 * 计算预报中最大的日期天数
	 * @param datetime
	 * @param forecastResultList
	 * @return
	 */
	private String analystForecast(String datetime, List forecastResultList) {
		// 判断预报的是否满足条件
		String maxDatetime = "";
		Integer maxHourSpan = 0;
		for(int i = 0; i < forecastResultList.size(); i++) {
			HashMap dataMap = (HashMap) forecastResultList.get(i);
			Integer cnt = ((Long) dataMap.get("cnt")).intValue();
			Integer hourSpan = (Integer) dataMap.get("HourSpan");
			if(cnt >= MINAREAHIGHTMPSTATIONS) {
				maxHourSpan = hourSpan;
			} else {
				break;
			}
		}
		maxDatetime = addDatetime(datetime, maxHourSpan);
		return maxDatetime;
	}
	
	private String addDatetime(String datetime, int hourspan) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long time = date.getTime() + hourspan * CommonConstant.HOURTIMES;
		return sdf.format(new Date(time));
	}
	
	
	/**
	 * 查询预报的高温数据
	 * @param datetime
	 * @return
	 */
	private List getForecastHighTmpByTime(String datetime) {
		List list = forecastDataDao.getDataCountByDatetime(datetime);
		return list;
	}
	
	private List getHighTmpByDatetime(String datetime) {
		List resultList = highTmpDaoImpl.getDataListByTime(datetime);
		return resultList;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		AreaHighTmpAlertSync areaHighTmpAlertSync = new AreaHighTmpAlertSync(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String datetime = sdf.format(new Date());
//		areaHighTmpAlertSync.sync(datetime);
		// 测试
		areaHighTmpAlertSync.sync("2016-08-20");
	}
}
