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
import com.spd.common.MaxWindRangeParam;
import com.spd.common.MaxWindRangeResult;
import com.spd.common.MaxWindRangeResultSequence;
import com.spd.common.MaxWindResult;
import com.spd.common.MaxWindYearsParam;
import com.spd.common.MaxWindYearsResult;
import com.spd.common.StrongCoolingSequenceResult;
import com.spd.common.StrongCoolingTotalResult;
import com.spd.common.TimesParam;
import com.spd.service.IMaxWind;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 大风统计相关
 * @author Administrator
 *
 */
public class MaxWindBus {

	/**
	 * 按时间段统计大风，返回合计和逐次统计结果
	 * @param timesParam
	 * @return
	 */
	public MaxWindResult maxWindByRange(MaxWindRangeParam maxWindRangeParam) {
		TimesParam timesParam = maxWindRangeParam.getTimesParam();
		MaxWindResult maxWindResult = new MaxWindResult();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		MaxWindRangeResult 合计
//		MaxWindRangeResultSequence 逐次
		IMaxWind maxWind = (IMaxWind)ContextLoader.getCurrentWebApplicationContext().getBean("MaxWindImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> listResult = maxWind.queryMaxWindByRanges(paramMap);
		List<MaxWindRangeResult> maxWindResultList = new ArrayList<MaxWindRangeResult>();
 		//统计逐次结果
		List<MaxWindRangeResultSequence> maxWindRangeResultSequenceList = new ArrayList<MaxWindRangeResultSequence>();
		for(int i=0; i<listResult.size(); i++) {
			LinkedHashMap linkedHashMap = listResult.get(i);
			String station_Id_C = (String) linkedHashMap.get("Station_Id_C");
			String station_Name = (String) linkedHashMap.get("Station_Name");
			Double wIN_S_Inst_Max = (Double) linkedHashMap.get("WIN_S_Inst_Max");
			Double wIN_D_INST_Max = (Double) linkedHashMap.get("WIN_D_INST_Max");
			java.sql.Date date = (java.sql.Date) linkedHashMap.get("Datetime");
			String datetime = sdf.format(new Date(date.getTime()));
			Integer level = (Integer) linkedHashMap.get("Level");
			MaxWindRangeResultSequence maxWindRangeResultSequence = new MaxWindRangeResultSequence();
			maxWindRangeResultSequence.setStation_Id_C(station_Id_C);
			maxWindRangeResultSequence.setStation_Name(station_Name);
			maxWindRangeResultSequence.setwIN_D_INST_Max(wIN_D_INST_Max);
			maxWindRangeResultSequence.setwIN_S_Inst_Max(wIN_S_Inst_Max);
			maxWindRangeResultSequence.setMaxWindTime(datetime);
			maxWindRangeResultSequence.setLevel(level);
			maxWindRangeResultSequence.setArea((String) linkedHashMap.get("area"));
			maxWindRangeResultSequenceList.add(maxWindRangeResultSequence);
		}
		
		//合计
		Map<String, List<LinkedHashMap>> maxWindRangeResultMap = new HashMap<String, List<LinkedHashMap>>();
		for(int i=0; i<listResult.size(); i++) {
			LinkedHashMap linkedHashMap = listResult.get(i);
			String station_Id_C = (String) linkedHashMap.get("Station_Id_C");
			List<LinkedHashMap> list = maxWindRangeResultMap.get(station_Id_C);
			if(list == null) {
				list = new ArrayList<LinkedHashMap>();
			}
			list.add(linkedHashMap);
			maxWindRangeResultMap.put(station_Id_C, list);
		}
		Set<String> set = maxWindRangeResultMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> list = maxWindRangeResultMap.get(key);
			MaxWindRangeResult maxWindRangeResult = new MaxWindRangeResult();
			maxWindRangeResult.setMildCnt(0); //轻度次数
			maxWindRangeResult.setModerateCnt(0);//中度次数
			maxWindRangeResult.setSeverityCnt(0);//严重次数
			double max_WIN_D_INST_Max = 0;
			double max_WIN_S_INST_Max = 0;
			int maxLevel = 1;
			maxWindRangeResult.setMaxWindTimes("");
			for(int i=0; i<list.size(); i++) {
				LinkedHashMap map = list.get(i);
				String station_Id_C = (String) map.get("Station_Id_C");
				String station_Name = (String) map.get("Station_Name");
				Double wIN_S_Inst_Max = (Double) map.get("WIN_S_Inst_Max");
				Double wIN_D_INST_Max = (Double) map.get("WIN_D_INST_Max");
				if(wIN_S_Inst_Max > max_WIN_S_INST_Max) {
					max_WIN_D_INST_Max = wIN_D_INST_Max;
					max_WIN_S_INST_Max = wIN_S_Inst_Max;
				}
//				String datetime = (String) map.get("Datetime");
				java.sql.Date date = (java.sql.Date) map.get("Datetime");
				String datetime = sdf.format(new Date(date.getTime()));
				Integer level = (Integer) map.get("Level");
				maxWindRangeResult.setStation_Id_C(station_Id_C);
				maxWindRangeResult.setStation_Name(station_Name);
				maxWindRangeResult.setArea((String) map.get("area"));
				if(level == 1) {
					maxWindRangeResult.setMildCnt(maxWindRangeResult.getMildCnt() + 1);
				} else if (level == 2) {
					maxWindRangeResult.setModerateCnt(maxWindRangeResult.getModerateCnt() + 1);
				} else if (level == 3) {
					maxWindRangeResult.setSeverityCnt(maxWindRangeResult.getSeverityCnt() + 1);
				}
				if(level > maxLevel) {
					maxLevel = level;
				}
			}
			if(maxLevel == 1) {
				maxWindRangeResult.setMaxLevel("一级");
			} else if(maxLevel == 2) {
				maxWindRangeResult.setMaxLevel("二级");
			} else if(maxLevel == 3) {
				maxWindRangeResult.setMaxLevel("三级");
			}
			maxWindRangeResult.setCnt(list.size());
//			maxWindRangeResult.setMaxWindD(max_WIN_D_INST_Max);
			maxWindRangeResult.setMaxWindS(max_WIN_S_INST_Max);
			for(int i=0; i<list.size(); i++) {
				LinkedHashMap map = list.get(i);
				Double wIN_S_INST_Max = (Double) map.get("WIN_S_Inst_Max");
				Double wIN_D_INST_Max = (Double) map.get("WIN_D_INST_Max");
				if(wIN_S_INST_Max != null &&
						max_WIN_S_INST_Max == wIN_S_INST_Max) {
					//TODO 可以要转换格式
//					String datetime = (String) map.get("Datetime");
					java.sql.Date date = (java.sql.Date) map.get("Datetime");
					String datetime = sdf.format(new Date(date.getTime()));
					maxWindRangeResult.setMaxWindTimes(maxWindRangeResult.getMaxWindTimes() + " " + datetime);
					String tempWin_D_INST_Max = maxWindRangeResult.getMaxWindD();
					if(tempWin_D_INST_Max == null) {
						maxWindRangeResult.setMaxWindD(wIN_D_INST_Max + "");
					} else {
						maxWindRangeResult.setMaxWindD(tempWin_D_INST_Max + " " + wIN_D_INST_Max);
					}
				}
			}
//			maxWindRangeResult.setArea(stationAreaMap.get(key));
			maxWindResultList.add(maxWindRangeResult);
		}
		
		String stationType = maxWindRangeParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = maxWindRangeResultSequenceList.size() - 1; i >= 0; i--) {
				MaxWindRangeResultSequence maxWindRangeResultSequence = maxWindRangeResultSequenceList.get(i);
				String station_Id_C = maxWindRangeResultSequence.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					maxWindRangeResultSequenceList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					maxWindRangeResultSequenceList.remove(i);
				}
			}
			for(int i = maxWindResultList.size() - 1; i >= 0; i--) {
				MaxWindRangeResult maxWindRangeResult = maxWindResultList.get(i);
				String station_Id_C = maxWindRangeResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					maxWindResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					maxWindResultList.remove(i);
				}
			}
		}
		maxWindResult.setMaxWindRangeResultSequenceList(maxWindRangeResultSequenceList);
		maxWindResult.setMaxWindRangeResultList(maxWindResultList);
		return maxWindResult;
	}
	
	public List<MaxWindYearsResult> maxWindByYear(MaxWindYearsParam maxWindYearsParam) {
		List<MaxWindYearsResult> resultList = new ArrayList<MaxWindYearsResult>();
		//历年
		Map<Integer, List<LinkedHashMap>> overMap = queryByYears(maxWindYearsParam, maxWindYearsParam.getStartYear(), maxWindYearsParam.getEndYear());
		//常年
		Map<Integer, List<LinkedHashMap>> yearsMap = queryByYears(maxWindYearsParam, maxWindYearsParam.getPerennialStartYear(), maxWindYearsParam.getPerennialEndYear());
		double yearsCnt = 0;
		Iterator<Integer> it = yearsMap.keySet().iterator();
		while(it.hasNext()) {
			Integer key = it.next();
			List<LinkedHashMap> list = yearsMap.get(key);
			yearsCnt += list.size();
		}
		yearsCnt = CommonTool.roundDouble(yearsCnt / (maxWindYearsParam.getEndYear() - maxWindYearsParam.getStartYear() + 1));
		for(int i = maxWindYearsParam.getStartYear(); i <= maxWindYearsParam.getEndYear(); i++) {
			List<LinkedHashMap> list = overMap.get(i);
			if(list == null) {
				MaxWindYearsResult maxWindYearsResult = new MaxWindYearsResult();
				maxWindYearsResult.setYear(i);
				maxWindYearsResult.setYearsCnt(yearsCnt);
				resultList.add(maxWindYearsResult);
			} else {
				MaxWindYearsResult maxWindYearsResult = new MaxWindYearsResult();
				maxWindYearsResult.setYear(i);
				maxWindYearsResult.setYearsCnt(yearsCnt);
				int cnt = list.size();
				int mildCnt = 0, moderateCnt = 0, severityCnt = 0;
				for(int j = 0; j < list.size(); j++) {
					LinkedHashMap itemMap = list.get(j);
					int level = (Integer) itemMap.get("Level");
					if(level == 1) {
						mildCnt++;
					} else if(level == 2) {
						moderateCnt++;
					} else if(level == 3) {
						severityCnt++;
					}
				}
				maxWindYearsResult.setMildCnt(mildCnt);
				maxWindYearsResult.setModerateCnt(moderateCnt);
				maxWindYearsResult.setSeverityCnt(severityCnt);
				maxWindYearsResult.setCnt(cnt);
				if(yearsCnt != 0) {
					maxWindYearsResult.setAnomalyRate(CommonTool.roundDouble((cnt - yearsCnt) / yearsCnt * 100));
				} else {
					maxWindYearsResult.setAnomalyRate(null);
				}
				maxWindYearsResult.setYearsCnt(yearsCnt);
				resultList.add(maxWindYearsResult);
			}
		}
		return resultList;
	}
	
	private Map<Integer, List<LinkedHashMap>> queryByYears(MaxWindYearsParam maxWindYearsParam, int startYear, int endYear) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		List<String> Station_Id_Cs = new ArrayList<String>();
		String[] station_Id_CArray = maxWindYearsParam.getStation_Id_Cs();
		for(int i=0; i<station_Id_CArray.length; i++) {
			Station_Id_Cs.add(station_Id_CArray[i]);
		}
		TimesParam timesParam = maxWindYearsParam.getTimesParam();
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		IMaxWind maxWind = (IMaxWind)ContextLoader.getCurrentWebApplicationContext().getBean("MaxWindImpl");
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>(); 
		if(isOverYear) {
			HashMap paramMap = new HashMap();
			String startTimeStr = timesParam.getStartTimeStr();
			int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
			String endTimeStr = timesParam.getEndTimeStr();
			try {
				Date startDate = sdf.parse(timesParam.getStartTimeStr());
				Date endDate = sdf.parse(timesParam.getEndTimeStr());
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			paramMap.put("startYear", startYear - 1);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = maxWind.queryMaxWindBySameYear(paramMap);
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
				Date startDate = sdf.parse(timesParam.getStartTimeStr());
				Date endDate = sdf.parse(timesParam.getEndTimeStr());
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = maxWind.queryMaxWindBySameYear(paramMap);
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
