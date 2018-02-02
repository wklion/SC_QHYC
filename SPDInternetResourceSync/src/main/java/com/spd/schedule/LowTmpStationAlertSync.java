package com.spd.schedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.spd.dao.cq.impl.ForecastDataDao;
import com.spd.dao.cq.impl.HouTmpAvgDao;
import com.spd.dao.cq.impl.LowTmpDaoImpl;
import com.spd.dao.cq.impl.LowTmpStationAlertDaoImpl;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 低温单站预警分析
 * @author Administrator
 *
 */
public class LowTmpStationAlertSync {

	private ForecastDataDao forecastDataDao = new ForecastDataDao();
	
	private HouTmpAvgDao houTmpAvgDao = new HouTmpAvgDao();
	
	private LowTmpDaoImpl lowTmpDaoImpl = new LowTmpDaoImpl();

	private LowTmpStationAlertDaoImpl lowTmpStationAlertDaoImpl = new LowTmpStationAlertDaoImpl();
	
	//常年开始年
	private static int STARTYEAR = 1981;
	//常年结束年
	private static int ENDYEAR = 2010;
	
	//低于常年平均值的量，就算是低温
	private static double LOWTMP = -2;
	
	public void sync(String datetime) {
		// 1. 判断预报时间，预报的日期等，是否有包含候结束日的，如果有的话，则可以继续判断低温标准。如果没有，退出程序
		boolean isInLastDayHou = isContainEndHouInForecast(datetime);
		if(!isInLastDayHou) return;
		// 2. 取到当前实况所在的候，比如：当前：2016-09-02，预报的为2016-09-03， 2016-09-04, 2016-09-05， 为2016年9月1候。实况的就应该是2016年8月6候
		String maxFutureDateStr = getMaxFutureDateByForecastDate(datetime);
		if(maxFutureDateStr == null) return;
		int[] realTimeYearMonthHou = getRealTimeHouByFutureDate(maxFutureDateStr);
		//3. 判断当前候是否满足低温的要求，如果满足的话，就一直往前查找，直到找到最早的日期为止
		HashMap<String, int[]> resultRealTimeStationYearMonthHou = analystMinTmpStartHou(realTimeYearMonthHou);
		if(resultRealTimeStationYearMonthHou == null) return;
		//4. 根据预报的时间，找到该候对应的实况数据和预测数据，结合起来计算一个候的值，判断低温标准
		Object[] resultForecastStationYearMonthHou = analystMinTmpForecastHou(datetime);
		//5. 如果预报的满足低温要求，则把结果和实况的组合起来，一起计算单站的低温过程。
		List dataList = analystLowTmp(resultRealTimeStationYearMonthHou, resultForecastStationYearMonthHou, realTimeYearMonthHou, datetime);
		//结果入库
		lowTmpStationAlertDaoImpl.insertLowTmpStationValue(dataList, datetime);
	}
	
	private List analystLowTmp(HashMap<String, int[]> resultRealTimeStationYearMonthHou, Object[] resultForecastStationYearMonthHou,
			int[] currentYearMonthHou,  String datetime) {
		HashMap<String, int[]> forecastYearMonthHou = (HashMap<String, int[]>) resultForecastStationYearMonthHou[0]; //预报的日数
		HashMap<String, Double> forecastHouTmpMap = (HashMap<String, Double>)resultForecastStationYearMonthHou[1]; // 预报的平均气温
		HashMap<String, String[]> forecastResultYearMonthHou = new HashMap<String, String[]>(); //站，以及对应的开始时间、结束时间
		List resultList = new ArrayList();
		Iterator<String> it = forecastYearMonthHou.keySet().iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			int[] startYearMonthHou = resultRealTimeStationYearMonthHou.get(station_Id_C);
			if(startYearMonthHou == null) continue;
			int[] endYearMonthHou = forecastYearMonthHou.get(station_Id_C);
			String startTime = CommonTool.chgStartTimeByHou(startYearMonthHou);
			String endTime = CommonTool.chgEndTimeByHou(endYearMonthHou);
			//1. 计算实况的平均值。
			Double resultValue = cale(startYearMonthHou, currentYearMonthHou, station_Id_C);
			//2. 加上预报的平均值
			Double forecastValue = forecastHouTmpMap.get(station_Id_C);
			if(forecastValue == null) continue;
			Double avgValue = CommonTool.roundDouble((resultValue + forecastValue) / 2);
			//3. 计算历年的平均值
			Double yearsAvgValue = CommonTool.roundDouble(caleYearsAvg(startYearMonthHou, currentYearMonthHou, station_Id_C));
			//4. 计算距平
			Double anomaly = CommonTool.roundDouble(avgValue - yearsAvgValue);
			int hous = CommonTool.minusHous(endYearMonthHou, startYearMonthHou);
			HashMap dataMap = new HashMap();
			dataMap.put("Station_Id_C", station_Id_C);
			dataMap.put("StartTime", CommonTool.chgStartTimeByHou(startYearMonthHou) + " 00:00:00");
			dataMap.put("EndTime", CommonTool.chgEndTimeByHou(endYearMonthHou) + " 00:00:00");
			dataMap.put("AvgTmp", avgValue);
			dataMap.put("Anomaly", anomaly);
			dataMap.put("PersistHous", hous);
			dataMap.put("ForecastDate", datetime + " 00:00:00");
			resultList.add(dataMap);
		}
		return resultList;
	}
	
	
	/**
	 * 计算从开始时间到结束时间，当年的平均气温
	 * @param startHous
	 * @param endHous
	 * @return
	 */
	private Double cale(int[] startHous, int[] endHous, String station_Id_C) {
		Double sumAvgTmp = 0.0;
		int cnt = 0;
		while(CommonTool.compare(startHous, endHous) <= 0) {
			Double avgTmp = houTmpAvgDao.getHouAvgTmpByStation(station_Id_C, startHous[0], startHous[1], startHous[2]);
			if(avgTmp == null) continue;
			sumAvgTmp += avgTmp;
			cnt++;
			startHous = CommonTool.addHou(startHous[0], startHous[1], startHous[2], 1);
		}
		Double avgTmp = CommonTool.roundDouble(sumAvgTmp / cnt);
		return avgTmp;
	}
	
	/**
	 * 计算从开始时间到结束时间，历年的平均气温
	 * @param startHous
	 * @param endHous
	 * @return
	 */
	private Double caleYearsAvg(int[] startHous, int[] endHous, String station_Id_C) {
		Double  sumYearAvgTmp = 0.0;
		int cnt = 0;
		while(CommonTool.compare(startHous, endHous) <= 0) {
			Double yearAvgTmp = houTmpAvgDao.getAvgTmpByStationAndYears(STARTYEAR, ENDYEAR, startHous[1], new int[]{startHous[2]}, station_Id_C);
			if(yearAvgTmp == null) continue;
			sumYearAvgTmp += yearAvgTmp;
			cnt++;
			startHous = CommonTool.addHou(startHous[0], startHous[1], startHous[2], 1);
		}
		Double avgYearTmp = CommonTool.roundDouble(sumYearAvgTmp / cnt);
		return avgYearTmp;
	}
	/**
	 * 根据当前时间，往前找到候开始日。找到实况的数据。当前时间第二天，到预报的候最后一天。找到预报数据，结合起来计算结果
	 * @param datetime
	 * @return
	 */
	private Object[] analystMinTmpForecastHou(String datetime) {
		HashMap<String, int[]> resultMap = new HashMap<String, int[]>();
		//1. 计算日期所在候
		int[] yearMonthHou = CommonTool.getYearMonthHou(datetime);
		//2. 根据候计算候开始日期
		String startRealHouTime = CommonTool.chgStartTimeByHou(yearMonthHou);
		//3. 实况的开始，结束日期分别为：startRealHouTime、datetime
		//4. 计算候的最后一天
		String endForecastHoutTime = CommonTool.chgEndTimeByHou(yearMonthHou);
		String startForecastHouTime = CommonTool.addDays(datetime, 1);
		//5. 预报的开始，结束时间分别：startForecastHouTime、endForecastHoutTime
		//6. 计算，当前候的平均气温，计算历年的，对比
		HashMap<String, Double> currentHouTmpMap = lowTmpDaoImpl.getRealForecastHouAvgTmp(startRealHouTime, datetime, startForecastHouTime, endForecastHoutTime);
		// 计算历年
		HashMap<String, Double> yearsHouTmpMap = houTmpAvgDao.getHouAvgTmps(STARTYEAR, ENDYEAR, yearMonthHou[0], yearMonthHou[1], yearMonthHou[2]);
		// 对比当年和历年的情况
		Iterator<String> currentIt = currentHouTmpMap.keySet().iterator();
		while(currentIt.hasNext()) {
			String station_Id_C = currentIt.next();
			Double currentTmp = currentHouTmpMap.get(station_Id_C);
			Double yearsDouble = yearsHouTmpMap.get(station_Id_C);
			if(yearsDouble == null) continue;
			if(currentTmp - yearsDouble <= LOWTMP) {
				resultMap.put(station_Id_C, yearMonthHou);
			}
		}
		return new Object[]{resultMap, currentHouTmpMap};
	}
	
	
	/**
	 * 根据当前候，一直往前查找，直到找到最早的满足低温候定义的时间
	 * @param datetime
	 */
	private HashMap<String, int[]> analystMinTmpStartHou(int[] realTimeYearMonthHou) {
		HashMap<String, int[]> resultStationYearMonthHou = new HashMap<String, int[]>();
		Set<String> stationSet = getHouAvgByTimes(realTimeYearMonthHou[0], realTimeYearMonthHou[1], realTimeYearMonthHou[2]);
		if(stationSet == null) return null;
		Iterator<String> it = stationSet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			int[] resultYearMonthHou = analyst(station_Id_C, realTimeYearMonthHou[0], realTimeYearMonthHou[1], realTimeYearMonthHou[2],
					realTimeYearMonthHou[0], realTimeYearMonthHou[1], realTimeYearMonthHou[2]);
			if(resultYearMonthHou == null) continue;
			resultYearMonthHou = CommonTool.addHou(resultYearMonthHou[0], resultYearMonthHou[1], resultYearMonthHou[2], 1);
			resultStationYearMonthHou.put(station_Id_C, resultYearMonthHou);
		}
		return resultStationYearMonthHou;
	}
	
	
	private int[] analyst(String station_Id_C, int year, int month, int hou, int oriYear, int oriMonth, int oriHou) {
		Double avgTmp = houTmpAvgDao.getHouAvgTmpByStation(station_Id_C, year, month, hou);
		if(avgTmp == null) {
			return null;
		}
		int[] hous = new int[1];
		hous[0] = hou;
		Double yearsAvgTmp = CommonTool.roundDouble(getYearsHouAvgTmp(month, hous, station_Id_C));
		BigDecimal avgTmpDecimal = new BigDecimal(avgTmp + "");
		BigDecimal yearAvgTmpDecimal = new BigDecimal(yearsAvgTmp + "");
		if(avgTmpDecimal.subtract(yearAvgTmpDecimal).doubleValue() <= LOWTMP) {
			int[] yearMonthHou = CommonTool.addHou(year, month, hou, -1);
			return analyst(station_Id_C, yearMonthHou[0], yearMonthHou[1], yearMonthHou[2], oriYear, oriMonth, oriHou);
		} else if(year == oriYear && month == oriMonth && hou == oriHou){
			return null;
		} else {
			return new int[]{year, month, hou};
		}
	}
	
	
	/**
	 * 计算常年的单站候平均值
	 * @param month
	 * @param hou
	 * @return
	 */
	private Double getYearsHouAvgTmp(int month, int[] hous, String station_Id_C) {
		Double result = houTmpAvgDao.getAvgTmpByStationAndYears(STARTYEAR, ENDYEAR, month, hous, station_Id_C);
		return result;
	}
	
	/**
	 * 根据年月候，计算候平均气温
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private Set<String> getHouAvgByTimes(int year, int month, int hou) {
		Set<String> stationSet = new HashSet<String>();
		List resultList = houTmpAvgDao.getHouAvgTmp(year, month, hou);
		if(resultList == null || resultList.size() == 0) return null;
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			stationSet.add(station_Id_C);
		}
		return stationSet;
	}
	
	/**
	 * 根据预报的最大日期，计算对应的候，然后减1，变成当前的候
	 * @param futureDate
	 * @return
	 */
	private int[] getRealTimeHouByFutureDate(String futureDate) {
		int[] futureYearMonthHou = CommonTool.getYearMonthHou(futureDate);
		int[] realTimeYearMonthHou = CommonTool.addHou(futureYearMonthHou[0], futureYearMonthHou[1], futureYearMonthHou[2], -1);
		return realTimeYearMonthHou;
	}
	
	private String getMaxFutureDateByForecastDate(String datetime) {
		String maxFutureDateStr = forecastDataDao.getMaxFutureDateByForecastDate(datetime);
		return maxFutureDateStr;
	}
	/**
	 * 判断datetime的预报时效中是否包含候的结束日
	 * @param datetime
	 * @return
	 */
	private boolean isContainEndHouInForecast(String datetime) {
		List list = forecastDataDao.getFutureDateByForecastDate(datetime);
		if(list == null || list.size() == 0) return false;
		boolean isInLastDayHou = false;
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			String futureDate = (String) dataMap.get("FutureDate");
			isInLastDayHou = CommonTool.isLastDayInHou(futureDate);
			if(isInLastDayHou) {
				break;
			}
		}
		return isInLastDayHou;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		LowTmpStationAlertSync lowTmpStationAlertSync = new LowTmpStationAlertSync();
		String datetime = "2016-09-03";
		lowTmpStationAlertSync.sync(datetime);
	}
}
