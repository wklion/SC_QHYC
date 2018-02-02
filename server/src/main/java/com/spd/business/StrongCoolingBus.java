package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.CommonConstant;
import com.spd.common.StrongCoolingParam;
import com.spd.common.StrongCoolingResult;
import com.spd.common.StrongCoolingSequenceResult;
import com.spd.common.StrongCoolingTotalResult;
import com.spd.common.StrongCoolingYearsParam;
import com.spd.common.StrongCoolingYearsResult;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 强降温
 * @author Administrator
 *
 */
public class StrongCoolingBus {

	private double[] getLevelTmpByMonth(StrongCoolingParam strongCoolingParam, int month) {
		double level1LowerTmp = 0.0, level2LowerTmp = 0.0;  
		int[] winterMonthes = strongCoolingParam.getWinterMonthes();
		int[] springAutumnMonthes = strongCoolingParam.getSpringAutumnMonthes();
		int[] summerMonthes = strongCoolingParam.getSummerMonthes();
		for(int winterMonth : winterMonthes) {
			if(month == winterMonth) {
				//冬季
				level1LowerTmp = strongCoolingParam.getLevel1WinterTmp();
				level2LowerTmp = strongCoolingParam.getLevel2WinterTmp();
				break;
			}
		}
		for(int springAutumnMonth : springAutumnMonthes) {
			if(month == springAutumnMonth) {
				//春秋
				level1LowerTmp = strongCoolingParam.getLevel1springAutumnTmp();
				level2LowerTmp = strongCoolingParam.getLevel2springAutumnTmp();
				break;
			}
		}
		for(int summerMonth : summerMonthes) {
			if(month == summerMonth) {
				//夏季
				level1LowerTmp = strongCoolingParam.getLevel1SummerTmp();
				level2LowerTmp = strongCoolingParam.getLevel2SummerTmp();
				break;
			}
		}
		return new double[]{level1LowerTmp, level2LowerTmp};
	}
	
	public StrongCoolingResult strongCoolingByRange(StrongCoolingParam strongCoolingParam) {
//		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		StrongCoolingResult strongCoolingResult = new StrongCoolingResult();
		SimpleDateFormat sdfMM = new SimpleDateFormat("MM");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		TimesParam timesParam = strongCoolingParam.getTimesParam();
		TimesRangeParam timesRangeParam = new TimesRangeParam(); 
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		String station_Id_Cs =  strongCoolingParam.getStation_id_Cs();
		if(station_Id_Cs == null || "".equals(station_Id_Cs)) {
			station_Id_Cs = "5%";
		}
		long start1 = System.currentTimeMillis();
		//封装的查询操作
		DBTable dbTable = new DBTable();
		dbTable.queryDataByRangeTimes(timesRangeParam, station_Id_Cs, "t_tem_avg");
		//温度的结果序列
		List<SequenceTimeValue> sequenceTemAvgValueList = dbTable.getSequenceTimeValueList();
		List<StrongCoolingSequenceResult> strongCoolingSequenceResultList = new ArrayList<StrongCoolingSequenceResult>();
		List<StrongCoolingTotalResult> strongCoolingTotalResultList = new ArrayList<StrongCoolingTotalResult>();
		long start2 = System.currentTimeMillis();
		for(int i = 0; i < sequenceTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = sequenceTemAvgValueList.get(i);
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			String station_Name = sequenceTimeValue.getStation_Name();
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			for(int j = 0; j < timeValueList.size(); j++) {
				TimeValue iTimeValue = timeValueList.get(j);
				double iValue = iTimeValue.getValue();
				int persistDays = 0;
				double lowerTmp = 0, lower72HourTmp = 0;
				Date iDate = iTimeValue.getDate();
				int month = Integer.parseInt(sdfMM.format(iDate));
				double[] levelTmps = getLevelTmpByMonth(strongCoolingParam, month); //计算标准
				Date endDate = null;
				for(int k = j + 1; k < timeValueList.size(); k++) {
					TimeValue kTimeValue = timeValueList.get(k);
					double kValue = kTimeValue.getValue();
					endDate = kTimeValue.getDate();
					boolean endTime = false; //判断是否是最后一个要素
					boolean timeFlag = kValue <= iValue && endDate.getTime() - iDate.getTime() == CommonConstant.DAYTIMES;
					if(timeFlag) {
						persistDays++;
//						lowerTmp += (iValue - kValue);
//						if(persistDays == 3) {
//							lower72HourTmp = lowerTmp;
//						}
						iValue = kValue;
						if(k == timeValueList.size() - 1) {
							endTime = true;
							k++;
						}
					} 
					if(!timeFlag || endTime){
						//重新计算lower72HourTmp
//						double lower72HourTmp2 = 0;
						StrongCoolingSequenceResult strongCoolingSequenceResult = new StrongCoolingSequenceResult();
						int startIndex = k - persistDays - 1;
						if(persistDays < 3) {
							double tValue1 = timeValueList.get(startIndex).getValue();
							double tValue2 = timeValueList.get(startIndex + persistDays).getValue();
							lower72HourTmp = tValue1 - tValue2;
							Date itemStartDate = timeValueList.get(startIndex).getDate();
							Date itemEndDate = timeValueList.get(startIndex + persistDays).getDate();
							lowerTmp = timeValueList.get(startIndex).getValue() - timeValueList.get(startIndex + persistDays).getValue();
							strongCoolingSequenceResult.setStartDatetime(sdf.format(itemStartDate));
							strongCoolingSequenceResult.setEndDatetime(sdf.format(itemEndDate));
							Long dates = (itemEndDate.getTime() - itemStartDate.getTime()) / CommonConstant.DAYTIMES;
							strongCoolingSequenceResult.setPersistDays(dates.intValue());
						} else {
							List<StrongCoolingSequenceResult> validList = new ArrayList<StrongCoolingSequenceResult>();
							for(int t = 0; t <= persistDays - 3; t++) {
								double tValue1 = timeValueList.get(startIndex + t).getValue();
								double tValue3 = timeValueList.get(startIndex + t + 3).getValue();
								double tempValue = tValue1 - tValue3;
								if(tempValue >= levelTmps[0]) {
									if(tempValue >= lower72HourTmp) {
										lower72HourTmp = tempValue;
									}
									StrongCoolingSequenceResult item = new StrongCoolingSequenceResult();
									Date itemStartDate = timeValueList.get(startIndex + t).getDate();
									Date itemEndDate = timeValueList.get(startIndex + t + 3).getDate();
									item.setStartDatetime(sdf.format(itemStartDate));
									item.setEndDatetime(sdf.format(itemEndDate));
									Long dates = (itemEndDate.getTime() - itemStartDate.getTime()) / CommonConstant.DAYTIMES;
									item.setPersistDays(dates.intValue());
									item.setTemp1(timeValueList.get(startIndex + t).getValue());
									item.setTemp2(timeValueList.get(startIndex + t + 3).getValue());
									validList.add(item);
								}
							}
							if(validList.size() > 0) {
								StrongCoolingSequenceResult item1 =  validList.get(0);
								StrongCoolingSequenceResult item2 =  validList.get(validList.size() - 1);
								Date itemStartDate = null, itemEndDate = null;
								try {
									itemStartDate = sdf.parse(item1.getStartDatetime());
									itemEndDate = sdf.parse(item2.getEndDatetime());
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								strongCoolingSequenceResult.setStartDatetime(item1.getStartDatetime());
								strongCoolingSequenceResult.setEndDatetime(item2.getEndDatetime());
								lowerTmp = item1.getTemp1() - item2.getTemp2();
								Long dates = (itemEndDate.getTime() - itemStartDate.getTime()) / CommonConstant.DAYTIMES;
								strongCoolingSequenceResult.setPersistDays(dates.intValue());
							}
						}
						
						strongCoolingSequenceResult.setStation_Id_C(station_Id_C);
						strongCoolingSequenceResult.setStation_Name(station_Name);
						strongCoolingSequenceResult.setTotalLowerTmp(CommonTool.roundDouble(lowerTmp));
						if(lower72HourTmp == 0) {
							strongCoolingSequenceResult.setHours72LowerTmp(CommonTool.roundDouble(lowerTmp));
						} else {
							strongCoolingSequenceResult.setHours72LowerTmp(CommonTool.roundDouble(lower72HourTmp));
						}
						
						//判断强降温，不知道是不是要判断persistDays > 3
						if(lower72HourTmp >= levelTmps[0] && lower72HourTmp < levelTmps[1]) {
							//强降温
							j = k - 1;
							strongCoolingSequenceResult.setLevel("强");
							//判断是否要加到结果里
							boolean flag = strongCoolingParam.isLevel1SummerFlag();
							if(!flag) {
								String startDatetimeStr = strongCoolingSequenceResult.getStartDatetime();
								String monthStr = startDatetimeStr.split("-")[1];
								int itemMonth = Integer.parseInt(monthStr);
								if(itemMonth < 5 || itemMonth > 9) {
									strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
								}
							} else {
								strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
							}
							
						} else if(lower72HourTmp >= levelTmps[1]) {
							//特强降温
							j = k - 1;
							strongCoolingSequenceResult.setLevel("特强");
							boolean flag = strongCoolingParam.isLevel2SummerFlag();
							if(!flag) {
								String startDatetimeStr = strongCoolingSequenceResult.getStartDatetime();
								String monthStr = startDatetimeStr.split("-")[1];
								int itemMonth = Integer.parseInt(monthStr);
								if(itemMonth < 5 || itemMonth > 9) {
									strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
								}
							} else {
								strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
							}
						}
						break;
					}
					iDate = endDate;
				}
			}
		}
//		long start3 = System.currentTimeMillis();
//		System.out.println("统计逐次：" + (start3 - start2));
			//统计合计
		if(strongCoolingSequenceResultList != null && strongCoolingSequenceResultList.size() > 0) {
			Set<String> station_Id_CSets = new HashSet<String>();
			for(int i = 0; i < strongCoolingSequenceResultList.size(); i++) {
				StrongCoolingSequenceResult item = strongCoolingSequenceResultList.get(i);
				String station_Id_C = item.getStation_Id_C();
				station_Id_CSets.add(station_Id_C);
			}
			Iterator<String> it = station_Id_CSets.iterator();
			while(it.hasNext()) {
				String key = it.next();
				StrongCoolingTotalResult strongCoolingTotalResult = new StrongCoolingTotalResult();
				strongCoolingTotalResult.setMostLevel("强");
				strongCoolingTotalResult.setStation_Id_C(key);
				strongCoolingTotalResult.setArea(CommonUtil.getInstance().stationAreaMap.get(key));
				strongCoolingTotalResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
				int cnt = 0;
				for(int i = 0; i < strongCoolingSequenceResultList.size(); i++) {
					StrongCoolingSequenceResult strongCoolingSequenceResult = strongCoolingSequenceResultList.get(i);
					String itemStation_Id_C = strongCoolingSequenceResult.getStation_Id_C();
					if(key.equals(itemStation_Id_C)) {
						cnt++;
						String level = strongCoolingSequenceResult.getLevel();
						if(level.equals("特强")) {
							strongCoolingTotalResult.setMostLevel(level);
						}
						strongCoolingTotalResult.setLevelLast(level);
						String startTime = strongCoolingSequenceResult.getStartDatetime();
						strongCoolingTotalResult.setStartDatetimeLast(startTime);
						String endTime = strongCoolingSequenceResult.getEndDatetime();
						strongCoolingTotalResult.setEndDatetimeLast(endTime);
						strongCoolingTotalResult.setPersistDaysLast(CommonTool.caleDays(startTime, endTime));
					}
				}
				strongCoolingTotalResult.setCnt(cnt);
				strongCoolingTotalResultList.add(strongCoolingTotalResult);
			}
//			StrongCoolingSequenceResult item = strongCoolingSequenceResultList.get(strongCoolingSequenceResultList.size() - 1);
//			StrongCoolingTotalResult strongCoolingTotalResult = new StrongCoolingTotalResult();
//			strongCoolingTotalResult.setStation_Id_C(item.getStation_Id_C());
//			strongCoolingTotalResult.setStation_Name(item.getStation_Name());
//			strongCoolingTotalResult.setCnt(strongCoolingSequenceResultList.size());
//			strongCoolingTotalResult.setArea(CommonUtil.getInstance().stationAreaMap.get(item.getStation_Id_C()));
//			strongCoolingTotalResult.setEndDatetimeLast(item.getEndDatetime());
//			strongCoolingTotalResult.setLevelLast(item.getLevel());
//			strongCoolingTotalResult.setPersistDaysLast(item.getPersistDays());
//			strongCoolingTotalResult.setStartDatetimeLast(item.getStartDatetime());
//			strongCoolingTotalResult.setMostLevel("强");
//			for(int j = 0; j < strongCoolingSequenceResultList.size(); j++) {
//				StrongCoolingSequenceResult itemTmp = strongCoolingSequenceResultList.get(j);
//				String level = itemTmp.getLevel();
//				if(level.equals("特强")) {
//					strongCoolingTotalResult.setMostLevel("特强");
//					break;
//				}
//			}
//			strongCoolingTotalResultList.add(strongCoolingTotalResult);
		}
		long start4 = System.currentTimeMillis();
//		System.out.println("统计合计：" + (start4 - start3));
//		}
		//
		String stationType = strongCoolingParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = strongCoolingSequenceResultList.size() - 1; i >= 0; i--) {
				StrongCoolingSequenceResult strongCoolingSequenceResult = strongCoolingSequenceResultList.get(i);
				String station_Id_C = strongCoolingSequenceResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					strongCoolingSequenceResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					strongCoolingSequenceResultList.remove(i);
				}
			}
			for(int i = strongCoolingTotalResultList.size() - 1; i >= 0; i--) {
				StrongCoolingTotalResult strongCoolingTotalResult = strongCoolingTotalResultList.get(i);
				String station_Id_C = strongCoolingTotalResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					strongCoolingTotalResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					strongCoolingTotalResultList.remove(i);
				}
			}
		}
		strongCoolingResult.setStrongCoolingSequenceResultList(strongCoolingSequenceResultList);
		strongCoolingResult.setStrongCoolingTotalResultList(strongCoolingTotalResultList);
		return strongCoolingResult;
	}
	
	public Object strongCoolingByYears(StrongCoolingYearsParam strongCoolingYearsParam) {
		List<StrongCoolingYearsResult> resultList = new ArrayList<StrongCoolingYearsResult>();
		// 1. 历年
		TimesParam overTimesParam = strongCoolingYearsParam.getTimesParam();
		TimesYearsParam overTimesYearsParam = new TimesYearsParam(overTimesParam.getStartMon(), overTimesParam.getStartDay(), overTimesParam.getEndMon(),
				overTimesParam.getEndDay(), strongCoolingYearsParam.getStartYear(), strongCoolingYearsParam.getEndYear());
		Map<Integer, List<StrongCoolingSequenceResult>> overMap = queryStrongCoolingByTimesYearsParam(strongCoolingYearsParam, overTimesYearsParam);
		// 2. 常年
		TimesYearsParam yearsTimesYearsParam = new TimesYearsParam(overTimesParam.getStartMon(), overTimesParam.getStartDay(), overTimesParam.getEndMon(),
				overTimesParam.getEndDay(), strongCoolingYearsParam.getPerennialStartYear(), strongCoolingYearsParam.getPerennialEndYear());
		Map<Integer, List<StrongCoolingSequenceResult>> yearsMap = queryStrongCoolingByTimesYearsParam(strongCoolingYearsParam, yearsTimesYearsParam);
		//计算常年值
		double yearsCnt = 0, yearsLevel1LowerTmpCnt = 0; //常年降温次数，常年特强降温次数
		Iterator<Integer> it = yearsMap.keySet().iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List<StrongCoolingSequenceResult> itemList = yearsMap.get(year);
			yearsCnt += itemList.size();
			for(int i = 0; i < itemList.size(); i++) {
				StrongCoolingSequenceResult strongCoolingSequenceResult = itemList.get(i);
				String level = strongCoolingSequenceResult.getLevel();
				if("特强".equals(level)) {
					yearsLevel1LowerTmpCnt += 1;
				}
			}
		}
		yearsCnt = CommonTool.roundDouble(yearsCnt / (strongCoolingYearsParam.getEndYear() - strongCoolingYearsParam.getStartYear() + 1));
		yearsLevel1LowerTmpCnt = CommonTool.roundDouble(yearsLevel1LowerTmpCnt / (strongCoolingYearsParam.getEndYear() - strongCoolingYearsParam.getStartYear() + 1));
		//历年
		for(int i = strongCoolingYearsParam.getStartYear(); i <= strongCoolingYearsParam.getEndYear(); i++) {
			List<StrongCoolingSequenceResult> overList = overMap.get(i);
			if(overList != null) {
//				System.out.println("年：" + i + ", 次数：" + overList.size());
				StrongCoolingYearsResult strongCoolingYearsResult = new StrongCoolingYearsResult();
				strongCoolingYearsResult.setYear(i);
				strongCoolingYearsResult.setYearsCnt(yearsCnt);  // 常年总次数
				strongCoolingYearsResult.setYearsLevel1LowerTmpCnt(yearsLevel1LowerTmpCnt); //常年特强降温次数
				strongCoolingYearsResult.setCnt(overList.size()); // 历年总次数
				strongCoolingYearsResult.setAnomalyRate(CommonTool.roundDouble((overList.size() - yearsCnt) / yearsCnt * 100)); //次数距平率
				double level1LowerTmpCnt = 0;
				double hours72LowerTmp = 0;
				for(int j = 0; j < overList.size(); j++) {
					StrongCoolingSequenceResult itemStrongCoolingSequenceResult = overList.get(j);
					String level = itemStrongCoolingSequenceResult.getLevel();
					if("特强".equals(level)) {
						level1LowerTmpCnt += 1;
					}
					double tempHour72LowerTmp = itemStrongCoolingSequenceResult.getHours72LowerTmp();
					if(tempHour72LowerTmp > hours72LowerTmp) {
						hours72LowerTmp = tempHour72LowerTmp;
					}
				}
				strongCoolingYearsResult.setLevel1LowerTmpCnt(level1LowerTmpCnt); // 特强降温次数
				strongCoolingYearsResult.setLevel1AnomalyRate(CommonTool.roundDouble((level1LowerTmpCnt - yearsLevel1LowerTmpCnt) / yearsLevel1LowerTmpCnt * 100));
				strongCoolingYearsResult.setMostLowerTmp72Hours(CommonTool.roundDouble(hours72LowerTmp)); // 最强72小时降温
				resultList.add(strongCoolingYearsResult);
			} else {
				StrongCoolingYearsResult strongCoolingYearsResult = new StrongCoolingYearsResult();
				strongCoolingYearsResult.setYear(i);
				strongCoolingYearsResult.setYearsCnt(yearsCnt);  // 常年总次数
				strongCoolingYearsResult.setYearsLevel1LowerTmpCnt(yearsLevel1LowerTmpCnt); //常年特强降温次数
				resultList.add(strongCoolingYearsResult);
			}
		}
		return resultList;
 	}
	
	private Map<Integer, List<StrongCoolingSequenceResult>> queryStrongCoolingByTimesYearsParam(StrongCoolingYearsParam strongCoolingYearsParam, TimesYearsParam timesYearsParam) {
		SimpleDateFormat sdfMM = new SimpleDateFormat("MM");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DBTable dbTable = new DBTable();
		String stationType = strongCoolingYearsParam.getStationType();
//		String station_Id_Cs = "";
//		if("AWS".equals(stationType)) {
//			station_Id_Cs = "5%";
//		} else if("MWS".equals(stationType)) {
//			station_Id_Cs = "%";
//		}
		String station_Id_Cs = strongCoolingYearsParam.getStation_Id_Cs();
//		if(station_Id_Cs == null || "".equals(station_Id_Cs)) {
//			station_Id_Cs = "%";
//		}
		dbTable.queryDataByYears(timesYearsParam, station_Id_Cs, "t_tem_avg");
		//温度的结果序列
		List<SequenceTimeValue> sequenceTemAvgValueList = dbTable.getSequenceTimeValueList();
		//List<StrongCoolingSequenceResult> strongCoolingSequenceResultList = new ArrayList<StrongCoolingSequenceResult>();
		Map<Integer, List<StrongCoolingSequenceResult>> map = new HashMap<Integer, List<StrongCoolingSequenceResult>>();
		for(int i = 0; i < sequenceTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = sequenceTemAvgValueList.get(i);
			int year = sequenceTimeValue.getYear();
			List<StrongCoolingSequenceResult> strongCoolingSequenceResultList = map.get(year);
			if(strongCoolingSequenceResultList == null) {
				strongCoolingSequenceResultList = new ArrayList<StrongCoolingSequenceResult>();
			}
			String station_Id_C = sequenceTimeValue.getStation_Id_C();
			String station_Name = sequenceTimeValue.getStation_Name();
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			for(int j = 0; j < timeValueList.size(); j++) {
				TimeValue iTimeValue = timeValueList.get(j);
				double iValue = iTimeValue.getValue();
				int persistDays = 0;
				double lowerTmp = 0, lower72HourTmp = 0;
				Date iDate = iTimeValue.getDate();
				int month = Integer.parseInt(sdfMM.format(iDate));
				Date endDate = null;
				for(int k = j + 1; k < timeValueList.size(); k++) {
					TimeValue kTimeValue = timeValueList.get(k);
					double kValue = kTimeValue.getValue();
					endDate = kTimeValue.getDate();
					if(kValue <= iValue && endDate.getTime() - iDate.getTime() == CommonConstant.DAYTIMES) {
						persistDays++;
						lowerTmp += (iValue - kValue);
						if(persistDays == 3) {
							lower72HourTmp = lowerTmp;
						}
						iValue = kValue;
					} else {
						StrongCoolingSequenceResult strongCoolingSequenceResult = new StrongCoolingSequenceResult();
						strongCoolingSequenceResult.setStation_Id_C(station_Id_C);
						strongCoolingSequenceResult.setStation_Name(station_Name);
						strongCoolingSequenceResult.setStartDatetime(sdf.format(iTimeValue.getDate()));
						strongCoolingSequenceResult.setEndDatetime(sdf.format(new Date(endDate.getTime() - CommonConstant.DAYTIMES)));
						strongCoolingSequenceResult.setPersistDays(persistDays);
						strongCoolingSequenceResult.setTotalLowerTmp(lowerTmp);
						if(lower72HourTmp == 0) {
							strongCoolingSequenceResult.setHours72LowerTmp(lowerTmp);
						} else {
							strongCoolingSequenceResult.setHours72LowerTmp(lower72HourTmp);
						}
						double level1LowerTmp = 0.0, level2LowerTmp = 0.0;  
						int[] winterMonthes = strongCoolingYearsParam.getWinterMonthes();
						int[] springAutumnMonthes = strongCoolingYearsParam.getSpringAutumnMonthes();
						int[] summerMonthes = strongCoolingYearsParam.getSummerMonthes();
						for(int winterMonth : winterMonthes) {
							if(month == winterMonth) {
								//冬季
								level1LowerTmp = strongCoolingYearsParam.getLevel1WinterTmp();
								level2LowerTmp = strongCoolingYearsParam.getLevel2WinterTmp();
								break;
							}
						}
						for(int springAutumnMonth : springAutumnMonthes) {
							if(month == springAutumnMonth) {
								//春秋
								level1LowerTmp = strongCoolingYearsParam.getLevel1springAutumnTmp();
								level2LowerTmp = strongCoolingYearsParam.getLevel2springAutumnTmp();
								break;
							}
						}
						for(int summerMonth : summerMonthes) {
							if(month == summerMonth) {
								//夏季
								level1LowerTmp = strongCoolingYearsParam.getLevel1SummerTmp();
								level2LowerTmp = strongCoolingYearsParam.getLevel2SummerTmp();
								break;
							}
						}
						//判断强降温
						if(lowerTmp >= level1LowerTmp && lowerTmp < level2LowerTmp) {
							//强降温
							j = k - 1;
							strongCoolingSequenceResult.setLevel("强");
							boolean flag = strongCoolingYearsParam.isLevel1SummerFlag();
							if(flag && month >= 5 && month <= 9) {
								continue;
							}
							strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
							map.put(year, strongCoolingSequenceResultList);
						} else if(lowerTmp >= level2LowerTmp) {
							//特强降温
							j = k - 1;
							strongCoolingSequenceResult.setLevel("特强");
							boolean flag = strongCoolingYearsParam.isLevel2SummerFlag();
							if(flag && month >= 5 && month <= 9) {
								continue;
							}
							strongCoolingSequenceResultList.add(strongCoolingSequenceResult);
							map.put(year, strongCoolingSequenceResultList);
						}
						break;
					}
					iDate = endDate;
				}
			}
		}
		return map;
	}
}
