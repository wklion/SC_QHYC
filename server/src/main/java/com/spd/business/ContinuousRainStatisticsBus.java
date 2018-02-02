package com.spd.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.spd.common.CommonConstant;
import com.spd.common.ContinueousRainYearsResult;
import com.spd.common.ContinuousRainContrastResult;
import com.spd.common.ContinuousRainResult;
import com.spd.common.ContinuousRainSequenceResult;
import com.spd.common.ContinuousRainsDefineParam;
import com.spd.common.ContinuousRainsParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 连阴雨统计
 * @author Administrator
 *
 */
public class ContinuousRainStatisticsBus {

	//在时间段范围内，如果开始时间满足连阴雨条件，则相应的往前推算，直到找到条件截止
	private static int TOLERANCEDAYS = 10;
	//参数传进来的时候，定义的开始时间
	private Date startDate = null;
	
	private int startMon, startDay, endMon, endDay;
	
	private StationArea stationArea = new StationArea();
	
	private int yearCnt; //历史统计，总共有多少年
	
	/**
	 * 对单个站，或者一定区域内的站，或者指定站，进行逐年的时次对比
	 * @return
	 */
	public List<ContinueousRainYearsResult> continuousRainsYearsSequnence(ContinuousRainsParam continuousRainsParam, TimesYearsParam timesYearsParam, TimesYearsParam perennialTimesYearsParam) {
		List<ContinueousRainYearsResult> resultList = new ArrayList<ContinueousRainYearsResult>();
		yearCnt = timesYearsParam.getEndYear() - timesYearsParam.getStartYear() + 1;
		TimesRangeParam timesRangeParam  = continuousRainsParam.getTimesRangeParam();
		timesRangeParam.addEndDays(TOLERANCEDAYS);
		startDate = timesRangeParam.getStartDate();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		startMon = Integer.parseInt(sdf.format(timesRangeParam.getStartDate()));
		endMon = Integer.parseInt(sdf.format(timesRangeParam.getEndDate()));
		sdf = new SimpleDateFormat("dd");
		startDay = Integer.parseInt(sdf.format(timesRangeParam.getStartDate()));
		endDay = Integer.parseInt(sdf.format(timesRangeParam.getEndDate()));
		timesYearsParam.addEndDays(TOLERANCEDAYS);
//		perennialTimesYearsParam.addEndDays(TOLERANCEDAYS);
		perennialTimesYearsParam.addEndDays(TOLERANCEDAYS);
		//添加判断是否跨年，如果是的话，则把开始日期设置为1月1号
		int startYear = timesRangeParam.getStartYear();
		int endYear = timesRangeParam.getEndYear();
		if(startYear < endYear) {
//			timesRangeParam.setStartTimeStr(endYear + "-01-01");
			timesYearsParam.setStartMon(1);
			timesYearsParam.setStartDay(1);
			perennialTimesYearsParam.setStartMon(1);
			perennialTimesYearsParam.setStartDay(1);
		}
		//封装的查询操作
		DBTable dbTable = new DBTable();
//		dbTable.queryDataByRangeTimes(timesRangeParam, continuousRainsParam.getStationIds(), "t_ssh");
//		//日照的结果序列
//		List<SequenceTimeValue> sequenceSSHTimeValueList = dbTable.getSequenceTimeValueList();
//		//降水的结果序列
//		dbTable.queryDataByRangeTimes(timesRangeParam, continuousRainsParam.getStationIds(), "t_pre_time_2020");
//		List<SequenceTimeValue> sequencePreTimeValueList = dbTable.getSequenceTimeValueList();
//		//连阴雨序列统计结果
//		List<ContinuousRainSequenceResult> continuousRainSequenceResultList = getSequenceResult(sequenceSSHTimeValueList, sequencePreTimeValueList, continuousRainsParam.getContinuousRainsDefineParam());
		//统计历年同期
		dbTable.queryDataByYears(timesYearsParam, continuousRainsParam.getStationIds(), "t_ssh");
		//日照历年同期
		List<SequenceTimeValue> sequenceYearsSSHTimeValueList = dbTable.getSequenceTimeValueList();
		dbTable.queryDataByYears(timesYearsParam, continuousRainsParam.getStationIds(), "t_pre_time_0820");
		//降水历年同期
		List<SequenceTimeValue> sequenceYearsPreTimeValueList = dbTable.getSequenceTimeValueList();
		//历年
		List<ContinuousRainSequenceResult> continuousHisRainSequenceResultList = getSequenceResult(sequenceYearsSSHTimeValueList, sequenceYearsPreTimeValueList, null, continuousRainsParam.getContinuousRainsDefineParam(), null);
		
		//统计常年同期
		dbTable.queryDataByYears(perennialTimesYearsParam, continuousRainsParam.getStationIds(), "t_ssh");
		//日照常年同期
		List<SequenceTimeValue> sequencePerennialYearsSSHTimeValueList = dbTable.getSequenceTimeValueList();
		dbTable.queryDataByYears(perennialTimesYearsParam, continuousRainsParam.getStationIds(), "t_pre_time_0820");
		//降水常年同期
		List<SequenceTimeValue> sequencePerennialYearsPreTimeValueList = dbTable.getSequenceTimeValueList();
		//常年
		List<ContinuousRainSequenceResult> perennialHisRainSequenceResultList = getSequenceResult(sequencePerennialYearsSSHTimeValueList, sequencePerennialYearsPreTimeValueList, null, continuousRainsParam.getContinuousRainsDefineParam(), null);
		
		resultList = analyst(continuousHisRainSequenceResultList, timesYearsParam, perennialHisRainSequenceResultList, perennialTimesYearsParam);
		return resultList;
	}
	
	private List<ContinueousRainYearsResult> analyst(List<ContinuousRainSequenceResult> continuousHisRainSequenceResultList, 
			TimesYearsParam timesYearsParam, List<ContinuousRainSequenceResult> perennialHisRainSequenceResultList, TimesYearsParam perennialTimesYearsParam) {
		int years = perennialTimesYearsParam.getEndYear() - perennialTimesYearsParam.getStartYear() + 1;
		List<ContinueousRainYearsResult> resultList = new ArrayList<ContinueousRainYearsResult>();
		//计算常年值
		int total = 0, level1Cnt = 0, level2Cnt = 0;
		for(int i = 0; i < perennialHisRainSequenceResultList.size(); i++) {
			ContinuousRainSequenceResult continuousRainSequenceResult = perennialHisRainSequenceResultList.get(i);
			String level = continuousRainSequenceResult.getLevel();
			if("一般".equals(level)) {
				level2Cnt ++;
			} else if("重度".equals(level)) {
				level1Cnt++;
			}
		}
		total = level1Cnt + level2Cnt;
		double totalDouble = CommonTool.roundDouble(total / years), level1CntDouble = CommonTool.roundDouble(level1Cnt / years), level2CntDouble = CommonTool.roundDouble(level2Cnt / years);
		Map<Integer, List<ContinuousRainSequenceResult>> map = new HashMap<Integer, List<ContinuousRainSequenceResult>>();
		for(int i = 0; i < continuousHisRainSequenceResultList.size(); i++) {
			ContinuousRainSequenceResult continuousRainSequenceResult = continuousHisRainSequenceResultList.get(i);
			int year = continuousRainSequenceResult.getYear();
			List<ContinuousRainSequenceResult> list = map.get(year);
			if(list == null) {
				list = new ArrayList<ContinuousRainSequenceResult>();
			}
			list.add(continuousRainSequenceResult);
			map.put(year, list);
		}
		for(int i = timesYearsParam.getStartYear(); i <= timesYearsParam.getEndYear(); i++) {
			List<ContinuousRainSequenceResult> list = map.get(i);
			if(list == null) {
				ContinueousRainYearsResult continueousRainYearsResult = new ContinueousRainYearsResult();
				continueousRainYearsResult.setYear(i);
				continueousRainYearsResult.setContrastCnt(totalDouble);
				continueousRainYearsResult.setContrastSlightCnt(level2CntDouble);
				continueousRainYearsResult.setContrastSeverityCnt(level1CntDouble);
				continueousRainYearsResult.setCnt(0.0);
				continueousRainYearsResult.setSeverityCnt(0.0);
				continueousRainYearsResult.setSlightCnt(0.0);
				continueousRainYearsResult.setCntAnomalyRatio(0.0);
				continueousRainYearsResult.setSeverityCntAnomalyRatio(0.0);
				continueousRainYearsResult.setSlightCntAnomalyRatio(0.0);
				resultList.add(continueousRainYearsResult);
			} else {
				double yearTotal = 0, yearLevel1Cnt = 0, yearLevel2Cnt = 0;
				for(int j = 0; j < list.size(); j++) {
					ContinuousRainSequenceResult continuousRainSequenceResult = list.get(j);
					String level = continuousRainSequenceResult.getLevel();
					if("一般".equals(level)) {
						yearLevel2Cnt ++;
					} else if("重度".equals(level)) {
						yearLevel1Cnt++;
					}
				}
				yearTotal = yearLevel2Cnt + yearLevel1Cnt;
				ContinueousRainYearsResult continueousRainYearsResult = new ContinueousRainYearsResult();
				continueousRainYearsResult.setYear(i);
				continueousRainYearsResult.setContrastCnt(totalDouble);
				continueousRainYearsResult.setContrastSlightCnt(level2CntDouble);
				continueousRainYearsResult.setContrastSeverityCnt(level1CntDouble);
				continueousRainYearsResult.setCnt(yearTotal);
				continueousRainYearsResult.setSeverityCnt(yearLevel1Cnt);
				continueousRainYearsResult.setSlightCnt(yearLevel2Cnt);
				if(totalDouble == 0) {
					continueousRainYearsResult.setCntAnomalyRatio(0.0);
				} else {
					continueousRainYearsResult.setCntAnomalyRatio(CommonTool.roundDouble(((yearTotal - totalDouble) / totalDouble * 100)));
				}
				if(level1CntDouble == 0) {
					continueousRainYearsResult.setSeverityCntAnomalyRatio(0.0);
				} else {
					continueousRainYearsResult.setSeverityCntAnomalyRatio(CommonTool.roundDouble(((yearLevel1Cnt - level1CntDouble) / level1CntDouble * 100)));
				}
				if(level2CntDouble == 0) {
					continueousRainYearsResult.setSlightCntAnomalyRatio(0.0);
				} else {
					continueousRainYearsResult.setSlightCntAnomalyRatio(CommonTool.roundDouble(((yearLevel2Cnt - level2CntDouble) / level2CntDouble * 100)));
				}
				resultList.add(continueousRainYearsResult);
			}
		}
		return resultList;
	}

	/**
	 * 历年统计，单独的站进行统计
	 * @param continuousRainsYearsParam
	 * @return
	 */
	public ContinuousRainResult continuousRainsByRange(ContinuousRainsParam continuousRainsParam) {
//		yearCnt = timesYearsParam.getEndYear() - timesYearsParam.getStartYear() + 1;
		ContinuousRainResult continuousRainResult = new ContinuousRainResult();
		TimesRangeParam timesRangeParam  = continuousRainsParam.getTimesRangeParam();
		String startTime = timesRangeParam.getStartTimeStr();
		startDate = timesRangeParam.getStartDate();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		startMon = Integer.parseInt(sdf.format(timesRangeParam.getStartDate()));
		endMon = Integer.parseInt(sdf.format(timesRangeParam.getEndDate()));
		sdf = new SimpleDateFormat("dd");
		startDay = Integer.parseInt(sdf.format(timesRangeParam.getStartDate()));
		endDay = Integer.parseInt(sdf.format(timesRangeParam.getEndDate()));
		timesRangeParam.addEndDays(TOLERANCEDAYS);
//		timesYearsParam.addDays(TOLERANCEDAYS);
		//封装的查询操作
		DBTable dbTable = new DBTable();
		dbTable.queryDataByRangeTimes(timesRangeParam, continuousRainsParam.getStationIds(), "t_ssh");
		//日照的结果序列
		List<SequenceTimeValue> sequenceSSHTimeValueList = dbTable.getSequenceTimeValueList();
		//降水的结果序列
		dbTable.queryDataByRangeTimes(timesRangeParam, continuousRainsParam.getStationIds(), "t_pre_time_0820");
		List<SequenceTimeValue> sequencePreTimeValueList = dbTable.getSequenceTimeValueList();
		//最大日雨量
		dbTable.queryDataByRangeTimes(timesRangeParam, continuousRainsParam.getStationIds(), "t_pre_time_2020");
		List<SequenceTimeValue> sequenceDayPreTimeValueList = dbTable.getSequenceTimeValueList();
		//连阴雨序列统计结果
//		List<ContinuousRainSequenceResult> continuousRainSequenceResultList = getSequenceResult(sequenceSSHTimeValueList, sequencePreTimeValueList, sequenceDayPreTimeValueList, continuousRainsParam.getContinuousRainsDefineParam(),
//				continuousRainsParam.getTimesRangeParam());
		List<ContinuousRainSequenceResult> continuousRainSequenceResultList = getTimesRange(sequenceSSHTimeValueList, sequencePreTimeValueList, sequenceDayPreTimeValueList, continuousRainsParam.getContinuousRainsDefineParam(), timesRangeParam);
		//统计历年同期
//		dbTable.queryDataByYears(timesYearsParam, continuousRainsParam.getStationIds(), "t_ssh");
		//日照历年同期
//		List<SequenceTimeValue> sequenceYearsSSHTimeValueList = dbTable.getSequenceTimeValueList();
//		dbTable.queryDataByYears(timesYearsParam, continuousRainsParam.getStationIds(), "t_pre_time_0820");
		//降水历年同期
//		List<SequenceTimeValue> sequenceYearsPreTimeValueList = dbTable.getSequenceTimeValueList();
//		List<ContinuousRainSequenceResult> continuousHisRainSequenceResultList = getSequenceResult(sequenceYearsSSHTimeValueList, sequenceYearsPreTimeValueList, continuousRainsParam.getContinuousRainsDefineParam());
		filter(continuousRainSequenceResultList, startTime);
		//对比指定年份的序列，历年统计的序列，得到结果。
		continuousRainResult.setSequenceList(continuousRainSequenceResultList);
//		List<ContinuousRainContrastResult> continuousRainContrastResultList = getComSequenctList(continuousRainSequenceResultList, continuousHisRainSequenceResultList);
//		continuousRainResult.setContrastList(continuousRainContrastResultList);
		List<ContinuousRainContrastResult> continuousRainContrastResultList = getComSequenctList(continuousRainSequenceResultList);
		continuousRainResult.setContrastList(continuousRainContrastResultList);
		return continuousRainResult;
	}
	
	/**
	 * 过滤
	 * @param continuousRainSequenceResultList
	 */
	private void filter(List<ContinuousRainSequenceResult> continuousRainSequenceResultList, String startTime) {
		for(int i = continuousRainSequenceResultList.size() - 1; i >= 0; i--) {
			ContinuousRainSequenceResult continuousRainSequenceResult = continuousRainSequenceResultList.get(i);
			String endTime = continuousRainSequenceResult.getEndDatetime();
			if(CommonTool.compareDates(startTime, endTime) == 1) {
				continuousRainSequenceResultList.remove(i);
			}
		}
	}
	
	public List<ContinuousRainContrastResult> getComSequenctList(List<ContinuousRainSequenceResult> continuousRainSequenceResultList) {
		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		List<ContinuousRainContrastResult> resultList = new ArrayList<ContinuousRainContrastResult>();
		//按站号做好分组
		Map<String, List<ContinuousRainSequenceResult>> map = new HashMap<String, List<ContinuousRainSequenceResult>>();
		for(int i = 0; i < continuousRainSequenceResultList.size(); i++) {
			ContinuousRainSequenceResult continuousRainSequenceResult = continuousRainSequenceResultList.get(i);
			String station_Id_C = continuousRainSequenceResult.getStation_Id_C();
			List<ContinuousRainSequenceResult> tempList = map.get(station_Id_C);
			if(tempList == null) {
				tempList = new ArrayList<ContinuousRainSequenceResult>();
			}
			tempList.add(continuousRainSequenceResult);
			map.put(station_Id_C, tempList);
		}
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			ContinuousRainContrastResult continuousRainContrastResult = new ContinuousRainContrastResult();
			String key = it.next();
			continuousRainContrastResult.setStation_Id_C(key);
			List<ContinuousRainSequenceResult> list = map.get(key);
			continuousRainContrastResult.setCnt(list.size());
			String mostLevel = "一般";
			for(int i = 0; i < list.size(); i++) {
				ContinuousRainSequenceResult continuousRainSequenceResult = list.get(i);
				String level = continuousRainSequenceResult.getLevel();
				if("重度".equals(level)) {
					mostLevel = level;
				} 
			}
			continuousRainContrastResult.setMostLevel(mostLevel);
			for(int i = list.size() - 1; i >= 0; i--) {
				ContinuousRainSequenceResult continuousRainSequenceResult = list.get(i);
				String level = continuousRainSequenceResult.getLevel();
				if(level.equals(mostLevel)) {
					continuousRainContrastResult.setStartTime(continuousRainSequenceResult.getStartDatetime());
					continuousRainContrastResult.setEndTime(continuousRainSequenceResult.getEndDatetime());
					continuousRainContrastResult.setArea(stationAreaMap.get(key));
					continuousRainContrastResult.setStation_Name(continuousRainSequenceResult.getStation_Name());
					continuousRainContrastResult.setPersist(continuousRainSequenceResult.getPersistDays());
					resultList.add(continuousRainContrastResult);
					break;
				}
			}
		}
		return resultList;
	}
	
	private List<ContinuousRainSequenceResult> getTimesRange(
			List<SequenceTimeValue> sequenceSSHTimeValueList,
			List<SequenceTimeValue> sequencePreTimeValueList,
			List<SequenceTimeValue> sequenceDayPreTimeValueList,
			ContinuousRainsDefineParam continuousRainsDefineParam,
			TimesRangeParam timesRangeParam) {
		sequenceSSHTimeValueList = addSequenceTimeValue(sequenceSSHTimeValueList, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
		sequencePreTimeValueList = addSequenceTimeValue(sequencePreTimeValueList, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
		sequenceDayPreTimeValueList = addSequenceTimeValue(sequenceDayPreTimeValueList, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
		List<ContinuousRainSequenceResult> resultList = new ArrayList<ContinuousRainSequenceResult>();
		for(int i = 0; i < sequenceSSHTimeValueList.size(); i++) {
			SequenceTimeValue sshSequenceTimeValue = sequenceSSHTimeValueList.get(i);
			String station_Id_C = sshSequenceTimeValue.getStation_Id_C();
			List<TimeValue> sshList = sshSequenceTimeValue.getTimeValues();
//			sshList = addList(sshList, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
			for(int j = 0; j < sequencePreTimeValueList.size(); j++) {
				SequenceTimeValue preSequenceTimeValue = sequencePreTimeValueList.get(j);
				String itemStation_Id_C = preSequenceTimeValue.getStation_Id_C();
				for(int k = 0; k < sequenceDayPreTimeValueList.size(); k++) {
					SequenceTimeValue dayPreSequenceTimeValue = sequenceDayPreTimeValueList.get(k);
					String itemDayStation_Id_C = preSequenceTimeValue.getStation_Id_C();
					List<TimeValue> dayTimeValues = dayPreSequenceTimeValue.getTimeValues();
//					dayTimeValues = addList(dayTimeValues, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
					if(itemStation_Id_C.equals(station_Id_C) && itemDayStation_Id_C.equals(itemStation_Id_C)) {
						List<TimeValue> preList = preSequenceTimeValue.getTimeValues();
//						preList = addList(preList, timesRangeParam.getStartDate(), timesRangeParam.getEndDate());
						//开始对比ssh，pre
						analyst(sshList, preList, station_Id_C, continuousRainsDefineParam, resultList, dayTimeValues);
						break;
					}
				}
			}
		}
		return resultList;
	}
	
	private List<SequenceTimeValue> addSequenceTimeValue(List<SequenceTimeValue> sequenceTimeValueList, Date startDate, Date endDate) {
		List<SequenceTimeValue> resultSequenceTimeValueList = new ArrayList<SequenceTimeValue>();
		for(int i = 0; i < sequenceTimeValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = sequenceTimeValueList.get(i);
			SequenceTimeValue resultSequenceTimeValue = new SequenceTimeValue();
			resultSequenceTimeValue.setStation_Id_C(sequenceTimeValue.getStation_Id_C());
			resultSequenceTimeValue.setLat(sequenceTimeValue.getLat());
			resultSequenceTimeValue.setLon(sequenceTimeValue.getLon());
			resultSequenceTimeValue.setStation_Name(sequenceTimeValue.getStation_Name());
			resultSequenceTimeValue.setYear(sequenceTimeValue.getYear());
			List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
			List<TimeValue> resultTimeValues = addList(timeValues, startDate, endDate);
			resultSequenceTimeValue.setTimeValues(resultTimeValues);
			resultSequenceTimeValueList.add(resultSequenceTimeValue);
		}
		return resultSequenceTimeValueList;
	}
	/**
	 * 把数据填充完整
	 * @param dataList
	 * @return
	 */
	private List<TimeValue> addList(List<TimeValue> dataList, Date startDate, Date endDate) {
		List<TimeValue> resultList = new ArrayList<TimeValue>();
		Calendar startCa = Calendar.getInstance();
		startCa.setTime(startDate);
		Calendar endCa = Calendar.getInstance();
		endCa.setTime(endDate);
		while(startCa.compareTo(endCa) <= 0) {
			for(int j = 0; j < dataList.size(); j++) {
				TimeValue timeValue = dataList.get(j);
				Date date = timeValue.getDate();
				Calendar currentCa = Calendar.getInstance();
				currentCa.setTime(date);
				if(startCa.compareTo(currentCa) == 0) {
					resultList.add(timeValue);
					break;
				}
				if(startCa.compareTo(currentCa) < 0) {
					TimeValue resultTimeValue = new TimeValue();
					resultTimeValue.setDate(startCa.getTime());
					resultTimeValue.setValue(null);
					resultList.add(resultTimeValue);
					break;
				}
			}
			startCa.add(Calendar.DATE, 1);
		}
//		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
//			for(int j = 0; j < dataList.size(); j++) {
//				TimeValue timeValue = dataList.get(j);
//				Date date = timeValue.getDate();
//				long itemTime = date.getTime();
//				if(itemTime == i) {
//					resultList.add(timeValue);
//					break;
//				}
//				if(i < itemTime) {
//					TimeValue resultTimeValue = new TimeValue();
//					resultTimeValue.setDate(new Date(i));
//					resultTimeValue.setValue(null);
//					resultList.add(resultTimeValue);
//					break;
//				}
//			}
//		}
		return resultList;
	}
	
	private void analyst(List<TimeValue> sshList, List<TimeValue> preList, String station_Id_C,
			ContinuousRainsDefineParam continuousRainsDefineParam, List<ContinuousRainSequenceResult> resultList,
			List<TimeValue> dayTimeValues) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
		Integer startIndex = null, endIndex = 0;
		int preDays = 0;// 记录降水日数
		int sshDays = 0;//日照日数
		Double totalPreValue = 0.0, maxDayPreValue = 0.0;//降水量，最大降水量。
		for(int i = 0; i < sshList.size(); i++) {
			TimeValue sshValue = sshList.get(i);
			Double dayPre = null;
			if(dayTimeValues.size() > i) {
				dayPre = dayTimeValues.get(i).getValue();
			}
			Double pre = preList.get(i).getValue();
			Double ssh = sshValue.getValue();
			if(ssh != null && ssh == 0) {
				//无日照
				if(startIndex == null) {
					startIndex = i;
				}
				endIndex = i;
				if(pre != null && pre > 0) {
					preDays ++;
					totalPreValue += pre;
				}
				sshDays ++;
				
				if(dayPre != null && maxDayPreValue < dayPre) {
					maxDayPreValue = dayPre;
				}
			}
			//判断结束，1：ssh > 0 。2：连续3天降水为0。前提条件是降水要满足>=4天
			if(startIndex != null && endIndex - startIndex + 1 >= continuousRainsDefineParam.getSlightNoSSHDays()) {
				//1. 满足连阴雨的日数
				Date sshEndDate = sshList.get(endIndex).getDate();
				if(preDays >= continuousRainsDefineParam.getSlightPreDays()
						&& (i == sshList.size() - 1 || ssh == null || ssh > 0 || 
								isConitnuePres(preList, sshEndDate, continuousRainsDefineParam.getTerminPreDays()))) {
					ContinuousRainSequenceResult continuousRainSequenceResult = new ContinuousRainSequenceResult();
					continuousRainSequenceResult.setStation_Id_C(station_Id_C);
					continuousRainSequenceResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
					continuousRainSequenceResult.setNoSunDays(endIndex - startIndex + 1);
					continuousRainSequenceResult.setPreDays(preDays);
					continuousRainSequenceResult.setPersistDays(endIndex - startIndex + 1);
					Date startDate = sshList.get(startIndex).getDate();
					continuousRainSequenceResult.setStartDate(startDate);
					continuousRainSequenceResult.setStartDatetime(sdf.format(startDate));
					Date endDate = sshList.get(endIndex).getDate();
					continuousRainSequenceResult.setEndDate(endDate);
					continuousRainSequenceResult.setEndDatetime(sdf.format(endDate));
					continuousRainSequenceResult.setYear(Integer.parseInt(sdf2.format(endDate)));
					continuousRainSequenceResult.setPreValue(CommonTool.roundDouble(totalPreValue));
					continuousRainSequenceResult.setMaxDayPreValue(maxDayPreValue);
					
					if(sshDays >= continuousRainsDefineParam.getSeverityNoSSHDays()&&
							preDays >= continuousRainsDefineParam.getSeverityPreDays()) {
						//重度连阴雨
						continuousRainSequenceResult.setLevel("重度");
					} else if(sshDays >= continuousRainsDefineParam.getSlightNoSSHDays() &&
							preDays >= continuousRainsDefineParam.getSlightPreDays()) {
						continuousRainSequenceResult.setLevel("一般");
					} 
					
					resultList.add(continuousRainSequenceResult);
					startIndex = null;
					totalPreValue = 0.0;
					maxDayPreValue = 0.0;
					sshDays = 0;
					preDays = 0;
				}
			}
			if(ssh == null || ssh != 0) {
				//当日照不再为0，过程结束，不管是否满足了连阴雨
				startIndex = null;
				totalPreValue = 0.0;
				maxDayPreValue = 0.0;
				sshDays = 0;
				preDays = 0;
			}
		}
	}
	
	private boolean isConitnuePres(List<TimeValue> preList, Date endDate, int terminPreDays) {
		boolean flag = true;
		Date startDate = new Date(endDate.getTime() - (terminPreDays - 1 )* CommonConstant.DAYTIMES);
		for(int i = 0; i < preList.size(); i++) {
			TimeValue itemTimeValue = preList.get(i);
			Date itemDate = itemTimeValue.getDate();
			if(itemDate.getTime() >= startDate.getTime() && itemDate.getTime() <= endDate.getTime()) {
				Double preValue = preList.get(i).getValue();
				if(preValue != null && preValue == 0) {
					flag = flag && true;
				} else {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}
	/**
	 * 统计连阴雨的序列
	 * @param sequenceSSHTimeValueList 日照序列
	 * @param sequencePreTimeValueList 降水序列
	 * @param continuousRainsDefineParam 连阴雨参数类
	 * @return 
	 */
	private List<ContinuousRainSequenceResult> getSequenceResult(
			List<SequenceTimeValue> sequenceSSHTimeValueList,
			List<SequenceTimeValue> sequencePreTimeValueList,
			List<SequenceTimeValue> sequenceDayPreTimeValueList,
			ContinuousRainsDefineParam continuousRainsDefineParam,
			TimesRangeParam timesRangeParam) {
		List<ContinuousRainSequenceResult> resultList = new ArrayList<ContinuousRainSequenceResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//比较日照
		for(SequenceTimeValue sshSequenceTimeValue : sequenceSSHTimeValueList) {
			List<TimeValue> listTimeValue = sshSequenceTimeValue.getTimeValues();
//			for(int i=0; i<listTimeValue.size()-1; i++) {
//				TimeValue tempTimeValue1= listTimeValue.get(i);
//				TimeValue tempTimeValue2= listTimeValue.get(i+1);
//				if(tempTimeValue1.getDate().getTime() > tempTimeValue2.getDate().getTime()) {
//					System.out.println("TimeValue:" + tempTimeValue1);
//				}
//			}
			if(listTimeValue == null || listTimeValue.size() == 0) {
				continue;
			}
			ContinuousRainSequenceResult continuousRainSequenceResult = new ContinuousRainSequenceResult();
			continuousRainSequenceResult.setYear(sshSequenceTimeValue.getYear());
			continuousRainSequenceResult.setStation_Id_C(sshSequenceTimeValue.getStation_Id_C());
			continuousRainSequenceResult.setStation_Name(sshSequenceTimeValue.getStation_Name());
			int continueNoSSH = 0;
			TimeValue tempTimeValue = null;
			Date startDate = null;
			for(int i=0; i<listTimeValue.size(); i++) {
				TimeValue timeValue = listTimeValue.get(i);
				if(tempTimeValue == null) {
					double value = timeValue.getValue();
					if(value == 0) {
						// 无日照
						startDate = timeValue.getDate();
						tempTimeValue = timeValue;
						continueNoSSH = 1;
					} else {
						continue;
					}
				} else {
					if(timeValue.getDate().getTime() - tempTimeValue.getDate().getTime() == CommonConstant.DAYTIMES
							&& timeValue.getValue() == 0) {
						//连续无日照
						continueNoSSH += 1;
						tempTimeValue = timeValue;
						continuousRainSequenceResult.setEndDate(timeValue.getDate());
						//不能漏掉最后一次情况
						if(i == listTimeValue.size() - 1) {
							continuousRainSequenceResult.setStartDate(startDate);
							continuousRainSequenceResult.setNoSunDays(continueNoSSH);
							continuousRainSequenceResult.setPersistDays(continueNoSSH);
							if(continuousRainSequenceResult.getEndDate() == null) {
								continuousRainSequenceResult.setEndDate(startDate);
							}
							ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
							resultList.add(copy);
							continueNoSSH = 0;
							startDate = null;
							tempTimeValue = null;
							
							continuousRainSequenceResult = new ContinuousRainSequenceResult();
							continuousRainSequenceResult.setYear(sshSequenceTimeValue.getYear());
							continuousRainSequenceResult.setStation_Id_C(sshSequenceTimeValue.getStation_Id_C());
							continuousRainSequenceResult.setStation_Name(sshSequenceTimeValue.getStation_Name());
						}
					} else {
						continuousRainSequenceResult.setStartDate(startDate);
						continuousRainSequenceResult.setNoSunDays(continueNoSSH);
						continuousRainSequenceResult.setPersistDays(continueNoSSH);
						if(continuousRainSequenceResult.getEndDate() == null) {
							continuousRainSequenceResult.setEndDate(startDate);
						}
						ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
						resultList.add(copy);
						continueNoSSH = 0;
						startDate = null;
						tempTimeValue = null;
						
						continuousRainSequenceResult = new ContinuousRainSequenceResult();
						continuousRainSequenceResult.setYear(sshSequenceTimeValue.getYear());
						continuousRainSequenceResult.setStation_Id_C(sshSequenceTimeValue.getStation_Id_C());
						continuousRainSequenceResult.setStation_Name(sshSequenceTimeValue.getStation_Name());
					}
				}
			}
		}
		//比较降水
		List<ContinuousRainSequenceResult> resultList2 = analystPre(resultList, sequencePreTimeValueList, continuousRainsDefineParam);
//		List<ContinuousRainSequenceResult> resultList2 = new ArrayList<ContinuousRainSequenceResult>();
//		for(int i=0; i<resultList.size(); i++) {
//			ContinuousRainSequenceResult continuousRainSequenceResult = resultList.get(i);
//			int year = continuousRainSequenceResult.getYear();
//			Date startDate = continuousRainSequenceResult.getStartDate();
//			Date endDate = continuousRainSequenceResult.getEndDate();
//			continuousRainSequenceResult.setStartDatetime(sdf.format(startDate));
//			continuousRainSequenceResult.setEndDatetime(sdf.format(endDate));
//			String station_Id_C = continuousRainSequenceResult.getStation_Id_C();
//			for(int j=0; j<sequencePreTimeValueList.size(); j++) {
//				int cnt = 0; // 降水的日数
//				double preValue = 0;//降水值
//				SequenceTimeValue preSequenceTimeValue = sequencePreTimeValueList.get(j);
//				String itemStation_Id_C = preSequenceTimeValue.getStation_Id_C();
//				if(!station_Id_C.equals(itemStation_Id_C)) {
//					continue;
//				}
//				int itemYear = preSequenceTimeValue.getYear();
//				if(itemYear != year) {
//					continue;
//				}
//				List<TimeValue> timeValueList = preSequenceTimeValue.getTimeValues();
//				for(int k=0; k<timeValueList.size(); k++) {
//					TimeValue timeValue = timeValueList.get(k);
//					Date itemDate = timeValue.getDate();
////					if(itemDate == null || endDate == null) {
////						System.out.println(timeValue);
////					}
//					if(itemDate.getTime() > endDate.getTime() || itemDate.getTime() < startDate.getTime()) {
//						continue;
//					}
////					if(itemDate.getTime() >= startDate.getTime() && itemDate.getTime() <= endDate.getTime()) {
////						if(timeValue.getValue() >= continuousRainsDefineParam.getSlightMinValue()) {
////							cnt++;
////							preValue += timeValue.getValue();
////						}
////					}
//					if(timeValue.getValue() >= continuousRainsDefineParam.getSlightMinValue()) {
//						cnt++;
//						preValue += timeValue.getValue();
//					}
//				}
//				continuousRainSequenceResult.setPreDays(cnt);
//				continuousRainSequenceResult.setPreValue(CommonTool.roundDouble(preValue));
//				ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
//				resultList2.add(copy);
//				cnt = 0;
//			}
//			
//		}
		//对比指标，看是否符合连阴雨的定义
		for(int i = resultList2.size() - 1; i >= 0; i--) {
			ContinuousRainSequenceResult continuousRainSequenceResult = resultList2.get(i);
			int persistDays = continuousRainSequenceResult.getPersistDays(); //无日照日数
			int preDays = continuousRainSequenceResult.getPreDays(); // 下雨日数
			if(persistDays >= continuousRainsDefineParam.getSeverityNoSSHDays()&&
					preDays >= continuousRainsDefineParam.getSeverityPreDays()) {
				//重度连阴雨
				continuousRainSequenceResult.setLevel("重度");
			} else if(persistDays >= continuousRainsDefineParam.getSlightNoSSHDays() &&
					preDays >= continuousRainsDefineParam.getSlightPreDays()) {
				continuousRainSequenceResult.setLevel("一般");
			} else {
				resultList2.remove(i);
			}
//			if(persistDays >= continuousRainsDefineParam.getSlightNoSSHDays() &&
//					persistDays <= 	continuousRainsDefineParam.getSeverityNoSSHDays() &&
//					preDays >= continuousRainsDefineParam.getSlightPreDays()) {
//				//一般连阴雨
//				continuousRainSequenceResult.setLevel("一般");
//			} else if(persistDays >= continuousRainsDefineParam.getSeverityNoSSHDays()&&
//					preDays >= continuousRainsDefineParam.getSeverityPreDays()) {
//				//重度连阴雨
//				continuousRainSequenceResult.setLevel("重度");
//			} else {
//				resultList2.remove(i);
//			}
		}
		//判断连阴雨的结束时间是否在定义查询的开始时间之前，如果在的话，则不满足要求，需要删除
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		for(int i = resultList2.size() - 1; i >= 0; i--) {
			ContinuousRainSequenceResult continuousRainSequenceResult = resultList2.get(i);
			Date endDate = continuousRainSequenceResult.getEndDate();
			int curMon = Integer.parseInt(sdfMon.format(endDate));
			int curDay = Integer.parseInt(sdfDay.format(endDate));
			if(timesRangeParam != null) {
				long currentTimes = endDate.getTime();
				long startTimes = timesRangeParam.getStartDate().getTime();
				long endTimes = timesRangeParam.getEndDate().getTime();
				if(currentTimes < startTimes || currentTimes > endTimes) {
					resultList2.remove(i);
				}
			} else {
				boolean flag = CommonTool.isCurTimeInRanges(curMon, curDay, startMon, startDay, endMon, endDay);
				if(!flag) {
					resultList2.remove(i);
				}
			}
		}
		//计算最大日雨量
		if(sequenceDayPreTimeValueList != null) {
			for(int i = 0; i < resultList2.size(); i++) {
				ContinuousRainSequenceResult continuousRainSequenceResult = resultList2.get(i);
				Date startDate = continuousRainSequenceResult.getStartDate();
				Date endDate = continuousRainSequenceResult.getEndDate();
				long startTime = startDate.getTime(), endTime = endDate.getTime();
				String station_Id_C = continuousRainSequenceResult.getStation_Id_C();
				double maxValue = 0L;
				for(int j = 0; j < sequenceDayPreTimeValueList.size(); j++) {
					SequenceTimeValue timeValue = sequenceDayPreTimeValueList.get(j);
					List<TimeValue> timeValueList = timeValue.getTimeValues();
					String tempStation_Id_C = timeValue.getStation_Id_C();
					if(tempStation_Id_C.equals(station_Id_C)) {
						for(int k = 0; k < timeValueList.size(); k++) {
							TimeValue itemTimeValue = timeValueList.get(k);
							long tempTime = itemTimeValue.getDate().getTime();
							if(tempTime >= startTime && tempTime <= endTime) {
								double value = itemTimeValue.getValue();
								if(value > maxValue) {
									maxValue = value;
								}
							}
						}
						break;
					}
				}
				continuousRainSequenceResult.setMaxDayPreValue(maxValue);
			}
		}
		return resultList2;
	}
	
	private List<ContinuousRainSequenceResult> analystPre(List<ContinuousRainSequenceResult> resultList, List<SequenceTimeValue> sequencePreTimeValueList,
			ContinuousRainsDefineParam continuousRainsDefineParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<ContinuousRainSequenceResult> resultList2 = new ArrayList<ContinuousRainSequenceResult>();
		for(int i=0; i<resultList.size(); i++) {
			ContinuousRainSequenceResult continuousRainSequenceResult = resultList.get(i);
			continuousRainSequenceResult.setNoSunDays(0);
			continuousRainSequenceResult.setMaxDayPreValue(0.0);
			int year = continuousRainSequenceResult.getYear();
			Date startDate = continuousRainSequenceResult.getStartDate();
			Date endDate = continuousRainSequenceResult.getEndDate();
			continuousRainSequenceResult.setStartDatetime(sdf.format(startDate));
			continuousRainSequenceResult.setEndDatetime(sdf.format(endDate));
			String station_Id_C = continuousRainSequenceResult.getStation_Id_C();
			Date endFirstDay = null, endPreDay = null;//判断满足结束条件的日期开始
			int noPreDays = 0;
			for(int j=0; j<sequencePreTimeValueList.size(); j++) {
				int cnt = 0; // 降水的日数
				double preValue = 0;//降水值
				SequenceTimeValue preSequenceTimeValue = sequencePreTimeValueList.get(j);
				String itemStation_Id_C = preSequenceTimeValue.getStation_Id_C();
				String station_Id_Name = preSequenceTimeValue.getStation_Name();
				if(!station_Id_C.equals(itemStation_Id_C)) {
					continue;
				}
				int itemYear = preSequenceTimeValue.getYear();
				if(itemYear != year) {
					continue;
				}
				List<TimeValue> timeValueList = preSequenceTimeValue.getTimeValues();
				for(int k=0; k<timeValueList.size(); k++) {
					TimeValue timeValue = timeValueList.get(k);
					Date itemDate = timeValue.getDate();
					if(itemDate.getTime() < startDate.getTime() || itemDate.getTime() > endDate.getTime()) {
						continue;
					} 
					if(continuousRainSequenceResult == null) {
						continuousRainSequenceResult = new ContinuousRainSequenceResult();
						continuousRainSequenceResult.setStartDate(itemDate);
						continuousRainSequenceResult.setStartDatetime(sdf.format(itemDate));
						continuousRainSequenceResult.setEndDate(endDate);
						continuousRainSequenceResult.setEndDatetime(sdf.format(endDate));
						continuousRainSequenceResult.setStation_Id_C(itemStation_Id_C);
						continuousRainSequenceResult.setStation_Name(station_Id_Name);
						continuousRainSequenceResult.setYear(year);
					}
					continuousRainSequenceResult.setNoSunDays(continuousRainSequenceResult.getNoSunDays() + 1);
					if(timeValue.getValue() >= continuousRainsDefineParam.getSlightMinValue()) {
						cnt++;
						preValue += timeValue.getValue();
						if(timeValue.getValue() > continuousRainSequenceResult.getMaxDayPreValue()) {
							continuousRainSequenceResult.setMaxDayPreValue(preValue);
						}
						continuousRainSequenceResult.setPersistDays(CommonTool.caleDays(continuousRainSequenceResult.getStartDatetime(), continuousRainSequenceResult.getEndDatetime()));
						continuousRainSequenceResult.setPreValue(preValue);
					}
					//如果已经满足条件了，则开始下一次的判断
					if(cnt >= continuousRainsDefineParam.getSlightPreDays()) {
//						if(itemDate.getTime() == endDate.getTime() && continuousRainSequenceResult.getPersistDays() >= continuousRainsDefineParam.getSlightNoSSHDays()) {
//							//终止连阴雨
//							ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
//							copy.setPersistDays(CommonTool.caleDays(copy.getStartDatetime(), copy.getEndDatetime()));
//							copy.setPreDays(cnt);
//							copy.setPreValue(CommonTool.roundDouble(preValue));
//							copy.setEndDate(itemDate);
//							copy.setEndDatetime(sdf.format(itemDate));
//							resultList2.add(copy);
//							//重置
//							continuousRainSequenceResult = null;
//							noPreDays = 0;
//							cnt = 0;
//							preValue = 0;
//						} 
						if(timeValue.getValue() == 0) {
							if(endFirstDay == null) {
								endFirstDay = itemDate;
								endPreDay = itemDate;
								noPreDays = 1;
							} else if(itemDate.getTime() - endPreDay.getTime() == CommonConstant.DAYTIMES) {
								noPreDays++;
								endPreDay = itemDate;
							} else {
								noPreDays = 0;
								endFirstDay = itemDate;
								endPreDay = itemDate;
							}
							if(noPreDays >= continuousRainsDefineParam.getTerminPreDays()) {
								//终止连阴雨
								ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
								copy.setPersistDays(CommonTool.caleDays(copy.getStartDatetime(), copy.getEndDatetime()));
								copy.setPreDays(cnt);
								copy.setPreValue(CommonTool.roundDouble(preValue));
								copy.setEndDate(itemDate);
								copy.setEndDatetime(sdf.format(itemDate));
								resultList2.add(copy);
								//重置
								continuousRainSequenceResult = null;
								noPreDays = 0;
								cnt = 0;
								preValue = 0;
							}
						}
					} 
					
				}
				if(continuousRainSequenceResult != null) {
					continuousRainSequenceResult.setPreDays(cnt);
					continuousRainSequenceResult.setPreValue(CommonTool.roundDouble(preValue));
					ContinuousRainSequenceResult copy = continuousRainSequenceResult.copy();
					resultList2.add(copy);
					cnt = 0;
				}
			}
			
		}
		return resultList2;
	}
}
