package com.spd.sc.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.sc.pojo.PenDiMaxPreParam;
import com.spd.sc.pojo.PenDiMaxPreResult;
import com.spd.sc.pojo.PenDiMaxPreSeasonYearsParam;
import com.spd.sc.pojo.PenDiMaxPreSeasonYearsResult;
import com.spd.sc.pojo.RainySeasonResult;
import com.spd.sc.pojo.SeasonByStationYearsParam;
import com.spd.sc.pojo.SeasonByStationYearsResult;
import com.spd.sc.pojo.SeasonByYearParam;
import com.spd.sc.pojo.SeasonResult;
import com.spd.sc.pojo.SeasonResultItem;
import com.spd.service.ISeason;
import com.spd.tool.CommonTool;

public class SeasonBus {

	//滑动天数
	private static int SLIDEDAYS = 5;
	//计算季节开始时用到连续滑动天数
	private static int SEQSLIDEDAYS = 5;
	//春季温度的起始值  >
	private static double SPRINGVALUE = 10;
	//夏季温度的起始值  >
	private static double SUMMERVALUE = 22;
	//冬季温度的起始值 <
	private static double WINTERVALUE = 10;
	
	
	public SeasonResult getSeasonByStationYears(SeasonByStationYearsParam seasonByStationYearsParam) {
		//1. 查询单站历年的情况
		List<SequenceTimeValue> sequenceTimeValueList = getResultsBySeason(seasonByStationYearsParam.getStartYear(), seasonByStationYearsParam.getEndYear(),
				seasonByStationYearsParam.getSeason(), seasonByStationYearsParam.getStartMon(),
				seasonByStationYearsParam.getStartDay(), seasonByStationYearsParam.getEndMon(), seasonByStationYearsParam.getEndDay(),
				seasonByStationYearsParam.getStation_Id_C(), "YEARS");
		//2. 按照getSeasonByYear的计算方法计算
		//2. 转换成滑动平均值
		List<SequenceTimeValue> huadongList = chgHuaDong(sequenceTimeValueList);
		//3. 计算满足条件的各站的滑动序列位置
		SeasonResult seasonResult = caleHuaDong(huadongList, seasonByStationYearsParam.getSeason());
		//4. 根据滑动位置，计算真实的开始路径 得到的多站单年的结果。
		caleStationYears(seasonResult, sequenceTimeValueList, seasonByStationYearsParam.getSeason());
		Collections.sort(seasonResult.getSeasonResultItemList());
		return seasonResult;
	}
	/**
	 * 查询单年的各站的季节情况
	 * 相较重庆的，情况算是比较简单了，不需要和历年做对比，做二次判断
	 * @param seasonByYearParam
	 * @return
	 */
	public SeasonResult getSeasonByYear(SeasonByYearParam seasonByYearParam) {
		//1. 查询各站的气温的时间序列值
		int year = seasonByYearParam.getYear();
		List<SequenceTimeValue> resultList = getResultsBySeason(year, year, seasonByYearParam.getSeason(),
				seasonByYearParam.getStartMon(), seasonByYearParam.getStartDay(), seasonByYearParam.getEndMon(), 
				seasonByYearParam.getEndDay(), "5%", "TIMES");
		//2. 转换成滑动平均值
		List<SequenceTimeValue> huadongList = chgHuaDong(resultList);
		//3. 计算满足条件的各站的滑动序列位置
		SeasonResult seasonResult = caleHuaDong(huadongList, seasonByYearParam.getSeason());
		//4. 根据滑动位置，计算真实的开始路径
		cale(seasonResult, resultList, seasonByYearParam.getSeason());
		return seasonResult;
	}
	
	/**
	 * 查询盆地大雨开始季
	 * @param penDiMaxPreParam
	 * @return
	 */
	public List<PenDiMaxPreResult> pendiMaxPreSeason(PenDiMaxPreParam penDiMaxPreParam) {
		List<PenDiMaxPreResult> penDiMaxPreResultList = new ArrayList<PenDiMaxPreResult>();
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", penDiMaxPreParam.getYear() + "-01-01");
		paramMap.put("endTime", penDiMaxPreParam.getYear() + "-12-31");
		paramMap.put("minPre", penDiMaxPreParam.getMinPre());
		paramMap.put("stations", penDiMaxPreParam.getStation_Id_Cs());
		penDiMaxPreResultList = iSeason.pendiMaxPreSeason(paramMap);
		return penDiMaxPreResultList;
	}
	
	/**
	 * 查询西南雨季
	 * @param year
	 * @return
	 */
	public List<RainySeasonResult> southWestRainySeason(int year) {
		List<RainySeasonResult> rainySeasonResultList = new ArrayList<RainySeasonResult>();
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("year", year);
		rainySeasonResultList = iSeason.southWestRainySeason(paramMap);
		return rainySeasonResultList;
	}
	
	public List<PenDiMaxPreSeasonYearsResult> pendiYearsMaxPreSeason(PenDiMaxPreSeasonYearsParam penDiMaxPreSeasonYearsParam) {
		List<PenDiMaxPreSeasonYearsResult> resultList = new ArrayList<PenDiMaxPreSeasonYearsResult>();
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startYear", penDiMaxPreSeasonYearsParam.getStartYear());
		paramMap.put("endYear", penDiMaxPreSeasonYearsParam.getEndYear());
		paramMap.put("station_Id_C", penDiMaxPreSeasonYearsParam.getStation_Id_C());
		resultList = iSeason.pendiYearsMaxPreSeason(paramMap);
		return resultList;
	}
	
	private void caleStationYears(SeasonResult seasonResult, List<SequenceTimeValue> resultList, String season) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SeasonResultItem> seasonResultItemList = seasonResult.getSeasonResultItemList();
		for(int i = 0; i < resultList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = resultList.get(i);
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			int iYear = sequenceTimeValue.getYear();
			for(int j = 0; j < seasonResultItemList.size(); j++) {
				SeasonResultItem seasonResultItem = seasonResultItemList.get(j);
				int jYear = seasonResultItem.getYear();
				if(iYear == jYear) {
					int huadongStartId = seasonResultItem.getHuadongStartId();
					for(int k = huadongStartId; k < huadongStartId + SEQSLIDEDAYS; k++) {
						TimeValue timeValue = timeValueList.get(k);
						Double value = timeValue.getValue();
						if("SPRING".equals(season)) {
							if(value >= SPRINGVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						} else if("SUMMER".equals(season)) {
							if(value >= SUMMERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						} else if("AUTUMN".equals(season)) {
							if(value < SUMMERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						}else if("WINTER".equals(season)) {
							if(value < WINTERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	private void cale(SeasonResult seasonResult, List<SequenceTimeValue> resultList, String season) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SeasonResultItem> seasonResultItemList = seasonResult.getSeasonResultItemList();
		for(int i = 0; i < resultList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = resultList.get(i);
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			String istation_Id_C = sequenceTimeValue.getStation_Id_C();
			for(int j = 0; j < seasonResultItemList.size(); j++) {
				SeasonResultItem seasonResultItem = seasonResultItemList.get(j);
				String jStation_Id_C = seasonResultItem.getStation_Id_C();
				if(istation_Id_C.equals(jStation_Id_C)) {
					int huadongStartId = seasonResultItem.getHuadongStartId();
					for(int k = huadongStartId; k < huadongStartId + SEQSLIDEDAYS; k++) {
						TimeValue timeValue = timeValueList.get(k);
						Double value = timeValue.getValue();
						if("SPRING".equals(season)) {
							if(value >= SPRINGVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						} else if("SUMMER".equals(season)) {
							if(value >= SUMMERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						} else if("AUTUMN".equals(season)) {
							if(value < SUMMERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						}else if("WINTER".equals(season)) {
							if(value < WINTERVALUE) {
								seasonResultItem.setStartTime(sdf.format(timeValue.getDate()));
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 计算满足条件的各站的滑动序列位置
	 * @param huadongList
	 * @param seasonByYearParam
	 * @return
	 */
	private SeasonResult caleHuaDong(List<SequenceTimeValue> huadongList, String season) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		SeasonResult seasonResult = new SeasonResult();
		List<SeasonResultItem> seasonResultItemList = new ArrayList<SeasonResultItem>();
		for(int i = 0; i < huadongList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = huadongList.get(i);
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			SeasonResultItem seasonResultItem = new SeasonResultItem(); 
			seasonResultItem.setStation_Id_C(station_Id_C);
			seasonResultItem.setStation_Name(sequenceTimeValue.getStation_Name());
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			int preIndex = -1; //记录上一次的索引位置，判断连续
			int size = 0; // 记录连续满足条件的天数
			for(int j = 0; j < timeValueList.size(); j++) {
				TimeValue timeValue = timeValueList.get(j);
				Date date = timeValue.getDate();
				int year = Integer.parseInt(sdf.format(date));
				seasonResultItem.setYear(year);
				Double value = timeValue.getValue();
				if("SPRING".equals(season)) {
					//春
					if(value >= SPRINGVALUE) {
						if(j - preIndex == 1 || size == 0) {
							//满足连续
							preIndex = j;
							size++;
							if(size >= SEQSLIDEDAYS) {
								//满足季节开始
								seasonResultItem.setHuadongStartId(j - SEQSLIDEDAYS + 1);
								seasonResultItemList.add(seasonResultItem);
								break;
							}
						}
					} else {
						size = 0;
					}
				} else if("SUMMER".equals(season)) {
					//夏
					if(value >= SUMMERVALUE) {
						if(j - preIndex == 1 || size == 0) {
							//满足连续
							preIndex = j;
							size++;
							if(size >= SEQSLIDEDAYS) {
								//满足季节开始
								seasonResultItem.setHuadongStartId(j - SEQSLIDEDAYS + 1);
								seasonResultItemList.add(seasonResultItem);
								break;
							}
						}
					} else {
						size = 0;
					}
				} else if("AUTUMN".equals(season)) {
					//秋
					if(value < SUMMERVALUE ) {
						if(j - preIndex == 1 || size == 0) {
							//满足连续
							preIndex = j;
							size++;
							if(size >= SEQSLIDEDAYS) {
								//满足季节开始
								seasonResultItem.setHuadongStartId(j - SEQSLIDEDAYS + 1);
								seasonResultItemList.add(seasonResultItem);
								break;
							}
						}
					} else {
						size = 0;
					}
				} else if("WINTER".equals(season)) {
					//冬
					if(value < WINTERVALUE) {
						if(j - preIndex == 1 || size == 0) {
							//满足连续
							preIndex = j;
							size++;
							if(size >= SEQSLIDEDAYS) {
								//满足季节开始
								seasonResultItem.setHuadongStartId(j - SEQSLIDEDAYS + 1);
								seasonResultItemList.add(seasonResultItem);
								break;
							}
						}
					} else {
						size = 0;
					}
				}
			}
		}
		seasonResult.setSeasonResultItemList(seasonResultItemList);
		return seasonResult;
	}
	/**
	 * 计算滑动平均
	 * @param resultList
	 * @return
	 */
	private List<SequenceTimeValue> chgHuaDong(List<SequenceTimeValue> seqTimeDataList) {
		List<SequenceTimeValue> resultTimeDataList = new ArrayList<SequenceTimeValue>();
		if(seqTimeDataList == null) {
			return null;
		}
		for(int i = 0; i < seqTimeDataList.size(); i++) {
			//每个站
			SequenceTimeValue itemSequenceTimeValue = seqTimeDataList.get(i);
			List<TimeValue> timeValueList = itemSequenceTimeValue.getTimeValues();
			if(timeValueList == null || timeValueList.size() < SLIDEDAYS) {
				continue;
			}
			
			SequenceTimeValue resultSequenceTimeValue = new SequenceTimeValue();
			resultSequenceTimeValue.setStation_Id_C(seqTimeDataList.get(i).getStation_Id_C());
			resultSequenceTimeValue.setStation_Name(seqTimeDataList.get(i).getStation_Name());
			List<TimeValue> resultTimeValueList = new ArrayList<TimeValue>();
			//每个站对应的序列
			for(int j = 0; j < timeValueList.size() - SLIDEDAYS + 1; j++) {
				TimeValue resultTimeValue = new TimeValue();
				Double resultValue = 0.0;
				for(int k = 0; k < SLIDEDAYS; k++) {
					TimeValue itemTimeValue = timeValueList.get(j + k);
					Double value = itemTimeValue.getValue();
					if(value != null) {
						resultValue += value;
					}
				}
				resultTimeValue.setValue(CommonTool.roundDouble(resultValue / SLIDEDAYS));
				resultTimeValue.setDate(timeValueList.get(j + SLIDEDAYS - 1).getDate());
				resultTimeValueList.add(resultTimeValue);
			}
			resultSequenceTimeValue.setTimeValues(resultTimeValueList);
			resultTimeDataList.add(resultSequenceTimeValue);
		}
		return resultTimeDataList;
	}
	
	private List<SequenceTimeValue> getResultsBySeason(int startYear, int endYear, String season, int startMon, int startDay, 
			int endMon, int endDay, String station_Id_C, String type) {
		String startTimeStr = startYear + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay); 
		if("WINTER".equals(season)) {
			endYear++;
		}
		DBTable dbTable = new DBTable();
		if("TIMES".equals(type)) {
			String endTimeStr = endYear + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay); 
			TimesRangeParam timesRangeParam = new TimesRangeParam();
			timesRangeParam.setStartTimeStr(startTimeStr);
			timesRangeParam.setEndTimeStr(endTimeStr);
			//查询当年 
			dbTable.queryDataByRangeTimes(timesRangeParam, station_Id_C, "t_tem_avg");
		} else if("YEARS".equals(type)) {
			TimesYearsParam timeYearsParam = new TimesYearsParam(startMon, startDay, endMon, endDay, startYear, endYear);
			//查询历年
			dbTable.queryDataByYears(timeYearsParam, station_Id_C, "t_tem_avg");
		}
		List<SequenceTimeValue> sequenceTemAvgValueList = dbTable.getSequenceTimeValueList();
		return sequenceTemAvgValueList;
	}
	
}
