package com.spd.business;

import java.lang.reflect.InvocationTargetException;
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
import com.spd.common.FrostRangeParam;
import com.spd.common.FrostResult;
import com.spd.common.FrostSequenceResult;
import com.spd.common.FrostTotalResult;
import com.spd.common.FrostYearsParam;
import com.spd.common.FrostYearsResult;
import com.spd.common.MaxWindRangeResult;
import com.spd.common.MaxWindRangeResultSequence;
import com.spd.common.TimesParam;
import com.spd.service.IFrost;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;
import com.sun.org.apache.commons.beanutils.BeanUtils;

/**
 * 霜冻
 * @author Administrator
 *
 */
public class FrostBus {

	public FrostResult frostByRangeAndStations (FrostRangeParam frostRangeParam) {
		TimesParam timesParam = frostRangeParam.getTimesParam();
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("TEM_Min", frostRangeParam.getLevel1LowTmp());
		List<String> Station_Id_Cs = new ArrayList<String>();
		String[] station_Id_CArray = frostRangeParam.getStation_Id_Cs();
		for(int i=0; i<station_Id_CArray.length; i++) {
			Station_Id_Cs.add(station_Id_CArray[i]);
		}
		paramMap.put("Station_Id_C", Station_Id_Cs);
		IFrost frostImpl = (IFrost)ContextLoader.getCurrentWebApplicationContext().getBean("FrostImpl");
		List<LinkedHashMap> resultList = frostImpl.queryFrostByRangesAndStations(paramMap);
		FrostResult frostResult = disposeForstResult(resultList, frostRangeParam);
		return frostResult;
	}
	
	private FrostResult disposeForstResult(List<LinkedHashMap> resultList, FrostRangeParam frostRangeParam) {
		FrostResult frostResult = new FrostResult();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, List<LinkedHashMap>> resultMap = new HashMap<String, List<LinkedHashMap>>();
		List<FrostSequenceResult> frostSequenceResultList = new ArrayList<FrostSequenceResult>();
		for(int i = 0; i < resultList.size(); i++) {
			FrostTotalResult frostSequenceResult = new FrostTotalResult();
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			List<LinkedHashMap> list = resultMap.get(station_Id_C);
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(itemMap);
			resultMap.put(station_Id_C, list);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> list = resultMap.get(key);
			Date startDate = null;
			FrostSequenceResult frostSequenceResult = new FrostSequenceResult();
			double extLowTmp = 999;
			int level1LowTmpCnt = 0, level2LowTmpCnt = 0; //  一般和严重两种情况下低于持续天数的天数和
			int level1LTLowTmpDays = 0, level2LTLowTmpDays = 0; //  一般和严重两种情况下低于持续天数的天数和
			for(int i = 0; i < list.size(); i++) {
				LinkedHashMap item = list.get(i);
				String dateTimeStr = (String) item.get("datetime");
				Date date = null;
				try {
					date = sdf.parse(dateTimeStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(startDate == null) {
					//开始
					frostSequenceResult.setStation_Id_C(key);
					frostSequenceResult.setArea(CommonUtil.getInstance().areaCodeMap.get(key));
					frostSequenceResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
					String datetime = (String) item.get("datetime");
					frostSequenceResult.setStartDatetime(datetime);
					frostSequenceResult.setPersistDays(1);
					double minTmp = (Double) item.get("TEM_Min");
					if(minTmp < extLowTmp) {
						extLowTmp = minTmp;
					}
					frostSequenceResult.setExtLowTmp(extLowTmp);
					if(minTmp <= frostRangeParam.getLevel1LowTmp()) {
						level1LowTmpCnt++;
					}
					if(minTmp <= frostRangeParam.getLevel2LowTmp()) {
						level2LowTmpCnt++;
					}
					if(minTmp <= frostRangeParam.getLevel1LTLowTmp()) {
						level1LTLowTmpDays++;
					}
					if(minTmp <= frostRangeParam.getLevel2LTLowTmp()) {
						level2LTLowTmpDays++;
					}
					startDate = date;
				}  else {
					String datetime = (String) item.get("datetime");
					Date endDate = null;
					try {
						endDate = sdf.parse(datetime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(endDate.getTime() - startDate.getTime() == CommonConstant.DAYTIMES) {
						//持续
						frostSequenceResult.setEndDatetime(datetime);
						double minTmp = (Double) item.get("TEM_Min");
						if(minTmp < extLowTmp) {
							extLowTmp = minTmp;
						}
						if(minTmp <= frostRangeParam.getLevel1LowTmp()) {
							level1LowTmpCnt++;
						}
						if(minTmp <= frostRangeParam.getLevel2LowTmp()) {
							level2LowTmpCnt++;
						}
						if(minTmp <= frostRangeParam.getLevel1LTLowTmp()) {
							level1LTLowTmpDays++;
						}
						if(minTmp <= frostRangeParam.getLevel2LTLowTmp()) {
							level2LTLowTmpDays++;
						}
						frostSequenceResult.setExtLowTmp(extLowTmp);
						frostSequenceResult.setPersistDays(frostSequenceResult.getPersistDays() + 1);
						startDate = date;
						if(i == list.size() - 1) {
							addFrostSequenceResultList(frostSequenceResult, frostRangeParam, level1LowTmpCnt, level2LowTmpCnt, 
									level1LTLowTmpDays, level2LTLowTmpDays, frostSequenceResultList, extLowTmp);
							level1LowTmpCnt = 0;
							level2LowTmpCnt = 0;
							level1LTLowTmpDays = 0;
							level2LTLowTmpDays = 0;
							startDate = null;
							i--;
						}
					} else {
						//结束了，判断是否符合要求。
						addFrostSequenceResultList(frostSequenceResult, frostRangeParam, level1LowTmpCnt, level2LowTmpCnt, 
								level1LTLowTmpDays, level2LTLowTmpDays, frostSequenceResultList, extLowTmp);
						level1LowTmpCnt = 0;
						level2LowTmpCnt = 0;
						level1LTLowTmpDays = 0;
						level2LTLowTmpDays = 0;
						startDate = null;
						i--;
					}
				}
			}
		}
		
		//合计
		List<FrostTotalResult> frostTotalResultList = new ArrayList<FrostTotalResult>();
		Map<String, List<FrostSequenceResult>> forstTotalResultListMap = new HashMap<String, List<FrostSequenceResult>>();
		for(int i = 0; i < frostSequenceResultList.size(); i++) {
			FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(i);
			String key = frostSequenceResult.getStation_Id_C();
			List<FrostSequenceResult> itemList = forstTotalResultListMap.get(key);
			if(itemList == null) {
				itemList = new ArrayList<FrostSequenceResult>();
			}
			itemList.add(frostSequenceResult);
			forstTotalResultListMap.put(key, itemList);
		}
		Iterator<String> it2 = forstTotalResultListMap.keySet().iterator();
		while(it2.hasNext()) {
			String key = it2.next();
			List<FrostSequenceResult> list = forstTotalResultListMap.get(key);
			FrostTotalResult frostTotalResult = new FrostTotalResult();
			frostTotalResult.setStation_Id_C(key);
			double extLowTmp = 999;
			String maxLevel = "一般";
			for(int j = 0; j < list.size(); j++) {
				FrostSequenceResult frostSequenceResult = list.get(j);
				frostTotalResult.setStation_Name(frostSequenceResult.getStation_Name());
				frostTotalResult.setArea(frostSequenceResult.getArea());
				double itemLowTmp = frostSequenceResult.getExtLowTmp();
				if(itemLowTmp < extLowTmp) {
					extLowTmp = itemLowTmp;
				}
				String level = frostSequenceResult.getLevel();
				if("严重".equals(level)) {
					maxLevel = level;
				} 
			}
			FrostSequenceResult frostSequenceResultLast = list.get(list.size() - 1);
			frostTotalResult.setExtLowTmp(extLowTmp);
			frostTotalResult.setMaxLevel(maxLevel);
			frostTotalResult.setCnt(list.size());
			frostTotalResult.setStartDatetimeLast(frostSequenceResultLast.getStartDatetime());
			frostTotalResult.setEndDatetimeLast(frostSequenceResultLast.getEndDatetime());
			frostTotalResult.setPersistDaysLast(frostSequenceResultLast.getPersistDays());
			frostTotalResult.setLevelLast(frostSequenceResultLast.getLevel());
			frostTotalResultList.add(frostTotalResult);
		}

		String stationType = frostRangeParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = frostSequenceResultList.size() - 1; i >= 0; i--) {
				FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(i);
				String station_Id_C = frostSequenceResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					frostSequenceResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					frostSequenceResultList.remove(i);
				}
			}
			for(int i = frostTotalResultList.size() - 1; i >= 0; i--) {
				FrostTotalResult frostTotalResult = frostTotalResultList.get(i);
				String station_Id_C = frostTotalResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					frostTotalResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					frostTotalResultList.remove(i);
				}
			}
		}
		
		frostResult.setFrostSequenceResultList(frostSequenceResultList);
		frostResult.setFrostTotalResultList(frostTotalResultList);
		return frostResult;
	}
	
	private void addFrostSequenceResultList(FrostSequenceResult frostSequenceResult, FrostRangeParam frostRangeParam,
			int level1LowTmpCnt, int level2LowTmpCnt,
			int level1LTLowTmpDays , int level2LTLowTmpDays, List<FrostSequenceResult> frostSequenceResultList, double extLowTmp) {
		//也表示结束了
		int persistDays = frostSequenceResult.getPersistDays();
		boolean flag = true;
		if(persistDays >= frostRangeParam.getLevel1PersistDays()) {
			//一般
			if(level1LowTmpCnt >= frostRangeParam.getLevel1PersistDays() && level1LTLowTmpDays >= frostRangeParam.getLevel1LTLowTmpDays()) {
				frostSequenceResult.setLevel("一般");
				frostSequenceResult.setExtLowTmp(extLowTmp);
				frostSequenceResult.setLowTmpDays(level1LowTmpCnt);
				FrostSequenceResult resultFrostSequenceResult = new FrostSequenceResult();
				try {
					BeanUtils.copyProperties(resultFrostSequenceResult, frostSequenceResult);
				} catch (Exception e) {
					e.printStackTrace();
				}
				frostSequenceResultList.add(resultFrostSequenceResult);
				flag = false;
			}
		} 
		if(flag && persistDays >= frostRangeParam.getLevel2PersistDays() && level2LTLowTmpDays >= frostRangeParam.getLevel2LTLowTmpDays()) {
			//严重
			if(level2LowTmpCnt >= frostRangeParam.getLevel2LTLowTmpDays()) {
				frostSequenceResult.setLevel("严重");
				frostSequenceResult.setExtLowTmp(extLowTmp);
				frostSequenceResult.setLowTmpDays(level2LowTmpCnt);
				frostSequenceResultList.add(frostSequenceResult);
			}
		}
	}
	/**
	 * 按时间段统计霜冻
	 * @param FrostRangeParam
	 * @return
	 */
	public FrostResult frostByRange(FrostRangeParam frostRangeParam) {
		TimesParam timesParam = frostRangeParam.getTimesParam();
		IFrost frostImpl = (IFrost)ContextLoader.getCurrentWebApplicationContext().getBean("FrostImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("TEM_Min", frostRangeParam.getLevel1LowTmp());
		paramMap.put("StationType", frostRangeParam.getStationType());
		List<LinkedHashMap> resultList = frostImpl.queryFrostByTimes(paramMap);
		FrostResult frostResult = disposeForstResult(resultList, frostRangeParam);
		return frostResult;
	}
	
	public List<FrostYearsResult> frostByYears(FrostYearsParam frostYearsParam) {
		List<FrostYearsResult> resultList = new ArrayList<FrostYearsResult>();
		//1. 历年
		Map<Integer, FrostResult> overMap = getFrostByTimes(frostYearsParam, frostYearsParam.getStartYear(), frostYearsParam.getEndYear());
		//2. 常年
		Map<Integer, FrostResult> yearsMap = getFrostByTimes(frostYearsParam, frostYearsParam.getPerennialStartYear(), frostYearsParam.getPerennialEndYear());
		Double yearCnt = 0.0, yearsLevel2Cnt = 0.0;
		Iterator<Integer> it = yearsMap.keySet().iterator();
		while(it.hasNext()) {
			Integer key = it.next();
			FrostResult FrostResult = yearsMap.get(key);
			List<FrostTotalResult> frostTotalResultList = FrostResult.getFrostTotalResultList();
			List<FrostSequenceResult> frostSequenceResultList = FrostResult.getFrostSequenceResultList();
			for(int i = 0; i < frostTotalResultList.size(); i++) {
				FrostTotalResult frostTotalResult = frostTotalResultList.get(i);
				yearCnt += frostTotalResult.getCnt();
			}
			for(int i = 0; i < frostSequenceResultList.size(); i++) {
				FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(i);
				String level = frostSequenceResult.getLevel();
				if("严重".equals(level)) {
					yearsLevel2Cnt++;
				}
			}
		}
		yearCnt = CommonTool.roundDouble(yearCnt / (frostYearsParam.getPerennialEndYear() - frostYearsParam.getPerennialStartYear() + 1));
		yearsLevel2Cnt = CommonTool.roundDouble(yearsLevel2Cnt / (frostYearsParam.getPerennialEndYear() - frostYearsParam.getPerennialStartYear() + 1));
		for(int i = frostYearsParam.getStartYear(); i <= frostYearsParam.getEndYear(); i++) {
			FrostResult frostResult = overMap.get(i);
			FrostYearsResult frostYearsResult = new FrostYearsResult();
			frostYearsResult.setYearCnt(yearCnt);
			frostYearsResult.setYear(i);
			frostYearsResult.setYearsLevel2Cnt(yearsLevel2Cnt);
			if(frostResult != null) {
				List<FrostTotalResult> frostTotalResultList = frostResult.getFrostTotalResultList();
				List<FrostSequenceResult> frostSequenceResultList = frostResult.getFrostSequenceResultList();
				Double cnt = 0.0, level2Cnt = 0.0;
				for(int j = 0; j < frostTotalResultList.size(); j++) {
					FrostTotalResult frostTotalResult = frostTotalResultList.get(j);
					cnt += frostTotalResult.getCnt();
				}
				for(int j = 0; j < frostSequenceResultList.size(); j++) {
					FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(j);
					String level = frostSequenceResult.getLevel();
					if("严重".equals(level)) {
						level2Cnt++;
					}
				}
				frostYearsResult.setCnt(cnt);
				frostYearsResult.setLevel2Cnt(level2Cnt);
				frostYearsResult.setAnomalyRate(CommonTool.roundDouble((cnt - yearCnt) / yearCnt * 100));
				frostYearsResult.setLevel2AnomalyRate(CommonTool.roundDouble((level2Cnt - yearsLevel2Cnt) / yearsLevel2Cnt * 100));
				resultList.add(frostYearsResult);
			} 
		}
		return resultList;
	}
	
	private Map<Integer, FrostResult> getFrostByTimes(FrostYearsParam frostYearsParam, int startYear, int endYear) {
		FrostRangeParam frostRangeParam = createRangeParamByYearsParm(frostYearsParam);
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		TimesParam timesParam = frostYearsParam.getTimesParam();
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>();
		IFrost frostImpl = (IFrost)ContextLoader.getCurrentWebApplicationContext().getBean("FrostImpl");
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
		if(isOverYear) {
			HashMap paramMap = new HashMap();
			paramMap.put("startTime", mmddSDF.format(timesParam.getStartDate()));
			paramMap.put("endTime", mmddSDF.format(timesParam.getEndDate()));
			paramMap.put("TEM_Min", frostYearsParam.getLevel1LowTmp());
			paramMap.put("startYear", startYear - 1);
			paramMap.put("endYear", endYear);
			List<String> Station_Id_Cs = new ArrayList<String>();
			String[] station_Id_CArray = frostYearsParam.getStation_Id_Cs();
			for(int i=0; i<station_Id_CArray.length; i++) {
				Station_Id_Cs.add(station_Id_CArray[i]);
			}
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> resultList = frostImpl.queryFrostByOverYears(paramMap);
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
			paramMap.put("TEM_Min", frostYearsParam.getLevel1LowTmp());
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			List<String> Station_Id_Cs = new ArrayList<String>();
			String[] station_Id_CArray = frostYearsParam.getStation_Id_Cs();
			for(int i=0; i<station_Id_CArray.length; i++) {
				Station_Id_Cs.add(station_Id_CArray[i]);
			}
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> resultList = frostImpl.queryFrostBySameYears(paramMap);
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
		Iterator<Integer> it = map.keySet().iterator();
		Map<Integer, FrostResult> mapYears = new HashMap<Integer, FrostResult>();
		while(it.hasNext()) {
			Integer year = it.next();
			List<LinkedHashMap> tempList = map.get(year);
			FrostResult frostResultYears = disposeForstResult(tempList, frostRangeParam);
			mapYears.put(year, frostResultYears);
		}
		return mapYears;
	}
	
	/**
	 * 历年同期统计
	 * @param frostYearsParam
	 * @return
	 */
//	public List<FrostYearsResult> frostByYears2(FrostYearsParam frostYearsParam) {
//		List<FrostYearsResult> frostYearsResultList = new ArrayList<FrostYearsResult>();
//		//1. 查询当年
//		FrostRangeParam frostRangeParam = createRangeParamByYearsParm(frostYearsParam);
//		FrostResult frostResult = frostByRangeAndStations(frostRangeParam);
//		//2. 查询历年
//		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
//		TimesParam timesParam = frostYearsParam.getTimesParam();
//		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>();
//		IFrost frostImpl = (IFrost)ContextLoader.getCurrentWebApplicationContext().getBean("FrostImpl");
//		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
//		int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
//		if(isOverYear) {
//			HashMap paramMap = new HashMap();
//			paramMap.put("startTime", mmddSDF.format(timesParam.getStartDate()));
//			paramMap.put("endTime", mmddSDF.format(timesParam.getEndDate()));
//			paramMap.put("TEM_Min", frostRangeParam.getLevel1LowTmp());
//			paramMap.put("startYear", frostYearsParam.getStartYear() - 1);
//			paramMap.put("endYear", frostYearsParam.getEndYear());
//			List<String> Station_Id_Cs = new ArrayList<String>();
//			String[] station_Id_CArray = frostRangeParam.getStation_Id_Cs();
//			for(int i=0; i<station_Id_CArray.length; i++) {
//				Station_Id_Cs.add(station_Id_CArray[i]);
//			}
//			paramMap.put("Station_Id_C", Station_Id_Cs);
//			List<LinkedHashMap> resultList = frostImpl.queryFrostByOverYears(paramMap);
//			for(LinkedHashMap itemMap : resultList) {
//				Integer year = (Integer) itemMap.get("year");
//				String mmdd = (String) itemMap.get("MMDD");
//				int mmddInt = Integer.parseInt(mmdd);
//				
//				List<LinkedHashMap> tempList = map.get(year);
//				if(tempList == null) {
//					tempList = new ArrayList<LinkedHashMap>();
//				}
//				tempList.add(itemMap);
//				if(mmddInt >= startTimeInt) { //年底的算在第二年
//					map.put(year + 1, tempList);
//				} else {
//					map.put(year, tempList);
//				}
//			}
//		} else {
//			HashMap paramMap = new HashMap();
//			paramMap.put("startTime", mmddSDF.format(timesParam.getStartDate()));
//			paramMap.put("endTime", mmddSDF.format(timesParam.getEndDate()));
//			paramMap.put("TEM_Min", frostRangeParam.getLevel1LowTmp());
//			paramMap.put("startYear", frostYearsParam.getStartYear());
//			paramMap.put("endYear", frostYearsParam.getEndYear());
//			List<String> Station_Id_Cs = new ArrayList<String>();
//			String[] station_Id_CArray = frostRangeParam.getStation_Id_Cs();
//			for(int i=0; i<station_Id_CArray.length; i++) {
//				Station_Id_Cs.add(station_Id_CArray[i]);
//			}
//			paramMap.put("Station_Id_C", Station_Id_Cs);
//			List<LinkedHashMap> resultList = frostImpl.queryFrostBySameYears(paramMap);
//			for(LinkedHashMap itemMap : resultList) {
//				Integer year = (Integer) itemMap.get("year");
//				List<LinkedHashMap> tempList = map.get(year);
//				if(tempList == null) {
//					tempList = new ArrayList<LinkedHashMap>();
//				}
//				tempList.add(itemMap);
//				map.put(year, tempList);
//			}
//		}
//		Iterator<Integer> it = map.keySet().iterator();
//		Map<Integer, FrostResult> mapYears = new HashMap<Integer, FrostResult>();
//		while(it.hasNext()) {
//			Integer year = it.next();
//			List<LinkedHashMap> tempList = map.get(year);
//			FrostResult frostResultYears = disposeForstResult(tempList, frostRangeParam);
//			mapYears.put(year, frostResultYears);
//		}
//		//当年
//		List<FrostSequenceResult> currentFrostSequenceResultList = frostResult.getFrostSequenceResultList();
//		double extLowTmpCurrent = 999;
//		int lowTmpDaysCurrent = 0, level1LowTmpDaysCurrent = 0, level2LowTmpDaysCurrent = 0;
//		String maxLevelCurrent = "";
//		for(int i = 0; i < currentFrostSequenceResultList.size(); i++) {
//			FrostSequenceResult frostSequenceResult = currentFrostSequenceResultList.get(i);
//			double extLowTmpItem = frostSequenceResult.getExtLowTmp();
//			if(extLowTmpItem < extLowTmpCurrent) {
//				extLowTmpCurrent = extLowTmpItem;
//			}
//			int lowTmpDaysItem = frostSequenceResult.getLowTmpDays();
//			lowTmpDaysCurrent += lowTmpDaysItem;
//			String level = frostSequenceResult.getLevel();
//			if("一般".equals(level)) {
//				level1LowTmpDaysCurrent += lowTmpDaysItem;
//				if("".equals(maxLevelCurrent)) {
//					maxLevelCurrent = level;
//				}
//			} else if("严重".equals(level)) {
//				level2LowTmpDaysCurrent += lowTmpDaysItem;
//				maxLevelCurrent = level;
//			}
//		}
//		
//		//3. 对比结果，遍历mapYears，取到结果，进行对比
//		Iterator<Integer> itYear = mapYears.keySet().iterator();
//		while(itYear.hasNext()) {
//			int year = itYear.next();
//			FrostResult frostResultYears = mapYears.get(year);
//			List<FrostSequenceResult> frostSequenceResultList = frostResultYears.getFrostSequenceResultList();
//			FrostYearsResult frostYearsResult = new FrostYearsResult();
//			frostYearsResult.setYear(year);
//			double extLowTmpYears = 999;
//			int lowTmpDaysYears = 0, level1LowTmpDaysYears = 0, level2LowTmpDaysYears = 0;
//			String maxLevelYears = "";
//			for(int i = 0; i < frostSequenceResultList.size(); i++) {
//				FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(i);
//				double extLowTmpItem = frostSequenceResult.getExtLowTmp();
//				if(extLowTmpItem < extLowTmpYears) {
//					extLowTmpYears = extLowTmpItem;
//				}
//				int lowTmpDaysItem = frostSequenceResult.getLowTmpDays();
//				lowTmpDaysYears += lowTmpDaysItem;
//				String level = frostSequenceResult.getLevel();
//				if("一般".equals(level)) {
//					level1LowTmpDaysYears += lowTmpDaysItem;
//					if("".equals(maxLevelYears)) {
//						maxLevelYears = level;
//					}
//				} else if("严重".equals(level)) {
//					level2LowTmpDaysYears += lowTmpDaysItem;
//					maxLevelYears = level;
//				}
//			}
//			frostYearsResult.setExtLowTmpYears(CommonTool.roundDouble(extLowTmpYears));
//			frostYearsResult.setLevel1LowTmpDaysYears(level1LowTmpDaysYears);
//			frostYearsResult.setLevel2LowTmpDaysYears(level2LowTmpDaysYears);
//			frostYearsResult.setLowTmpDaysYears(lowTmpDaysYears);
//			frostYearsResult.setExtLowTmpCurrent(extLowTmpCurrent);
//			frostYearsResult.setLevel1LowTmpDaysCurrent(level1LowTmpDaysCurrent);
//			frostYearsResult.setLevel2LowTmpDaysCurrent(level2LowTmpDaysCurrent);
//			frostYearsResult.setLowTmpDaysCurrent(lowTmpDaysCurrent);
//			frostYearsResult.setExtLowTmpAnomaly(CommonTool.roundDouble(extLowTmpYears - extLowTmpCurrent));
//			frostYearsResult.setLevel1LowTmpDaysAnomaly(CommonTool.roundDouble(level1LowTmpDaysYears - level1LowTmpDaysCurrent));
//			frostYearsResult.setLevel2LowTmpDaysAnomaly(CommonTool.roundDouble(level2LowTmpDaysYears - level2LowTmpDaysCurrent));
//			frostYearsResult.setLowTmpDaysAnomaly(CommonTool.roundDouble(lowTmpDaysYears - lowTmpDaysCurrent));
//			frostYearsResult.setMaxLevelCurrent(maxLevelCurrent);
//			frostYearsResult.setMaxLevelYears(maxLevelYears);
//			frostYearsResultList.add(frostYearsResult);
//		}
//		//排序
//		FrostYearsResult[] frostYearsResultArray = new FrostYearsResult[frostYearsResultList.size()];
//		for(int i = 0; i < frostYearsResultList.size(); i++) {
//			frostYearsResultArray[i] = frostYearsResultList.get(i);
//		}
//		Arrays.sort(frostYearsResultArray, new FrostYearsResult());
//		List<FrostYearsResult> frostYearsResultList2 = new ArrayList<FrostYearsResult>();
//		for(int i = 0; i < frostYearsResultArray.length; i++) {
//			int year = frostYearsResultArray[i].getYear();
//			if(year >= frostYearsParam.getStartYear() && year <= frostYearsParam.getEndYear()) {
//				frostYearsResultList2.add(frostYearsResultArray[i]);
//			}
//		}
//		return frostYearsResultList2;
//	}
	
	
	private FrostRangeParam createRangeParamByYearsParm(FrostYearsParam frostYearsParam) {
		FrostRangeParam frostRangeParam = new FrostRangeParam();
		frostRangeParam.setLevel1LowTmp(frostYearsParam.getLevel1LowTmp());
		frostRangeParam.setLevel1LTLowTmp(frostYearsParam.getLevel1LTLowTmp());
		frostRangeParam.setLevel1LTLowTmpDays(frostYearsParam.getLevel1LTLowTmpDays());
		frostRangeParam.setLevel1PersistDays(frostYearsParam.getLevel1PersistDays());
		frostRangeParam.setLevel2LowTmp(frostYearsParam.getLevel2LowTmp());
		frostRangeParam.setLevel2LTLowTmp(frostYearsParam.getLevel2LTLowTmp());
		frostRangeParam.setLevel2LTLowTmpDays(frostYearsParam.getLevel2LTLowTmpDays());
		frostRangeParam.setLevel2PersistDays(frostYearsParam.getLevel2PersistDays());
		frostRangeParam.setStation_Id_Cs(frostYearsParam.getStation_Id_Cs());
		frostRangeParam.setTimesParam(frostYearsParam.getTimesParam());
		return frostRangeParam;
	}
}
