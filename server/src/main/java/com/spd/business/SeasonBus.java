package com.spd.business;

import java.math.BigDecimal;
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

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.FirstSeasonDayItem;
import com.spd.common.SeasonResult;
import com.spd.common.SeasonYearsResult;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.service.ISeason;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

public class SeasonBus {

	//滑动天数
	private static int SLIDEDAYS = 5;
	//计算季节开始时用到连续滑动天数
	private static int SEQSLIDEDAYS = 5;
	//计算的开始年
	private static int STARTYEAR = 1981;
	//计算的结束年
	private static int ENDYEAR = 2010;
	//春季温度的起始值  >
	private static double SPRINGVALUE = 10;
	//夏季温度的起始值  >
	private static double SUMMERVALUE = 22;
	//秋季温度的起始值 <
	private static double AUTUMNVALUE = 10;
	//冬季温度的起始值 <
	private static double WINTERVALUE = 10;
	//二次判定常数日期
	private static int SECONDDAYS = 15;
	//季节
	private static String[] SEASONS = new String[]{"SPRING", "SUMMER", "AUTUMN", "WINTER"};
	//春、夏、秋、冬分别对应的开始月
	private static int[] STARTMONTH = new int[]{1, 4, 7, 10};
	//春、夏、秋、冬分别对应的结束月
	private static int[] ENDMONTH = new int[]{6, 9, 12, 3};
	//春、夏、秋、冬分别对应的开始日
	private static int[] STARTDAY = new int[]{1, 1, 1, 1};
	//春、夏、秋、冬分别对应的结束日
	private static int[] ENDDAY = new int[]{30, 30, 31, 1};
	//春季有效起止月日
//	private static int SPRINGSTARTMON = 1, SPRINGSTARTDAY = 1, SPRINGENDMON = 6, SPRINGENDDAY = 30;
//	//夏季有效起止月日
//	private static int SUMMERSTARTMON = 4, SUMMERSTARTDAY = 1, SUMMERENDMON = 9, SUMMERENDDAY = 30;
//	//秋季有效起止月日
//	private static int AUTUMNSTARTMON = 7, AUTUMNSTARTDAY = 1, AUTUMNENDMON = 12, AUTUMNENDDAY = 31;
//	//冬季有效起止月日
//	private static int WINTERSTARTMON = 10, WINTERSTARTDAY = 1, WINTERENDMON = 3, WINTERENDDAY = 1;
	
//	public List<SeasonResult> getSeasonByYear(int year, String season, int startMon, int startDay, int endMon, int endDay) {
//		//考虑到冬季
//		int endYear = year;
//		List<LinkedHashMap> resultList = getResultsBySeason(year, endYear, season, startMon, startDay, endMon, endDay);
//		Map<String, String> mapResult = dispose(resultList, season, "t_tem_avg");
//		//查询过去30年的季节开始时间
//		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
//		HashMap paramMap = new HashMap();
//		List<LinkedHashMap> hisSeasonList = iSeason.queryHistorySeason(paramMap);
//		//对比
//		List<SeasonResult> seasonResultList = compare(hisSeasonList, mapResult, season, year);
//		return seasonResultList;
//	}
//	
	public String[] calcNextSeason(int year, String season) {
		String[] result = new String[6]; // 对应的结果为：季节、开始月、开始日、结束月、结束日、年
		int seasonIndex = 0;
		for(int i = 0; i < SEASONS.length; i++) {
			if(season.equals(SEASONS[i])) {
				int nextIndex = (i + 1) % SEASONS.length;
				result[0] = SEASONS[nextIndex];
				seasonIndex = nextIndex;
				break;
			}
		}
		if(result[0].equals("SPRING")) {
			result[5] = (year + 1) + "";
		} else {
			result[5] = year + "";
		}
		result[1] = STARTMONTH[seasonIndex] + "";
		result[2] = ENDMONTH[seasonIndex] + "";
		result[3] = STARTDAY[seasonIndex] + "";
		result[4] = ENDDAY[seasonIndex] + "";
		return result;
	}
	
	public List<SeasonResult> getSeasonResult(List<SequenceTimeValue> resultList, String season, int year) {
		//2. 把气温序列修改成滑动序列
		List<SequenceTimeValue> huadongList = chgHuaDong(resultList);
		//3. 根据定义，计算初日
		List<HashMap> chuzhongList = chgChuZhongRi(huadongList, resultList, season);
		//4. 查询常年的结果
		//查询过去30年的季节开始时间
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> hisSeasonList = iSeason.queryHistorySeason(paramMap);
		//5. 对比计算结果、常年结果，如果比常年的早15天以上，则重新计算初日（此时的初日滑动序列，需要从这次的结果中截断）
		chuzhongList = calcEarlyDays(chuzhongList, hisSeasonList, huadongList, resultList, year, season);
		//6. 计算对比后的结果，组装结果，返回
		List<SeasonResult> seasonResultList = compare(hisSeasonList, chuzhongList, season, year);
		//7. 对于没有查找记录的，补齐结果
		addAllRecord(seasonResultList, resultList);
		//8. 整理结果序号
		sortIndex(seasonResultList);
		return seasonResultList;
	}
	
	public List<SeasonResult> getSeasonByYear2(int year, String season, int startMon, int startDay, int endMon, int endDay) {
		//1. 查询气温的序列
		//考虑到冬季
		int endYear = year;
		List<SequenceTimeValue> resultList = getResultsBySeason(year, endYear, season, startMon, startDay, endMon, endDay, "5%", "TIMES");
		return getSeasonResult(resultList, season, year);
	}
	
	private List<LinkedHashMap> getHisSeasonList() {
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> hisSeasonList = iSeason.queryHistorySeason(paramMap);
		return hisSeasonList;
	}
	
	/**
	 * 查询第一次满足的条件
	 * @param oriResultList 原始序列
	 * @param huadongList 滑动序列
	 * @param hisSeasonList 历史日期
	 * @return int[]{滑动开始id, 滑动结束id, 原始序列的id}
	 */
	private List<FirstSeasonDayItem> getFirstSeasonDay(List<SequenceTimeValue> oriResultList, List<SequenceTimeValue> huadongList, 
			List<LinkedHashMap> hisSeasonList, String season, int year) {
		//1. 找到第一个连续SEQSLIDEDAYS天满足条件的序列
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<FirstSeasonDayItem> firstSeasonDayList = new ArrayList<FirstSeasonDayItem>();
		for(int i = 0; i < huadongList.size(); i++) {
			FirstSeasonDayItem firstSeasonDayItem = new FirstSeasonDayItem();
			SequenceTimeValue huadongItem = huadongList.get(i);
			String station_Id_C = huadongItem.getStation_Id_C();
			String station_Name = huadongItem.getStation_Name();
			firstSeasonDayItem.setStation_Id_C(station_Id_C);
			firstSeasonDayItem.setStation_Name(station_Name);
			// 计算常年日期对应到现在的日期
			Date startDate = null;
			for(int k = 0; k < hisSeasonList.size(); k++) {
				LinkedHashMap hisSeasonItem = hisSeasonList.get(k);
				String hisStation_Id_C = (String) hisSeasonItem.get("Station_Id_C"); 
				if(!station_Id_C.equals(hisStation_Id_C)) continue;
				String springStart = (String) hisSeasonItem.get("SpringStart"); 
				String summerStart = (String) hisSeasonItem.get("SummerStart"); 
				String autumnStart = (String) hisSeasonItem.get("AutumnStart"); 
				String winterStart = (String) hisSeasonItem.get("WinterStart"); 
				String startTimeStr = "";
				if("SPRING".equals(season)) {
					//春
					startTimeStr = year + "-" + springStart;
				} else if("SUMMER".equals(season)) {
					//夏
					startTimeStr = year + "-" + summerStart;
				} else if("AUTUMN".equals(season)) {
					//秋
					startTimeStr = year + "-" + autumnStart;
				} else if("WINTER".equals(season)) {
					//冬
					startTimeStr = year + "-" + winterStart;
				}
				firstSeasonDayItem.setHisStartDate(startTimeStr);
				try {
					startDate = sdf.parse(startTimeStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			}
			//根据常年的日期，和当年的滑动序列，计算第一次的日期
			List<TimeValue> timeValues = huadongItem.getTimeValues();
			int cnt = 0; // 计算过程中满足条件的个数
			boolean flag = false;
			Date endDate = null;//timeValues.get(timeValues.size() - 1).getDate();
			for(int j = 0; j < timeValues.size(); j++) {
				TimeValue timeValue = timeValues.get(j);
				Date itemDate = timeValue.getDate();
				Double value = timeValue.getValue();
				if("SPRING".equals(season)) {
					//春
					if(value < SPRINGVALUE || value >= SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("SUMMER".equals(season)) {
					//夏
					if(value < SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("AUTUMN".equals(season)) {
					//秋
					if(value < AUTUMNVALUE || value >= SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("WINTER".equals(season)) {
					//冬
					if(value > WINTERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				}
				//判断序列是否满足
				if(flag || j == timeValues.size() - 1) {
					if(cnt >= SEQSLIDEDAYS) {
						//计算开始、结束索引
						int startIndex = 0;
						if(j == timeValues.size() - 1) {
							firstSeasonDayItem.setHuadongEndId(j);
							startIndex = j - cnt + 1;
						} else {
							firstSeasonDayItem.setHuadongEndId(j - 1);
							startIndex = j - cnt;
						}
						firstSeasonDayItem.setHuadongStartId(startIndex);
						//原始序列的开始
						SequenceTimeValue oriItem = null;
						for(int k = 0; k <oriResultList.size(); k++) {
							SequenceTimeValue tempOriItem = oriResultList.get(k);
							if(station_Id_C.equals(tempOriItem.getStation_Id_C())) {
								oriItem = tempOriItem;
								break;
							}
						}
						int oriStartId = 0;
						List<TimeValue> oriTimeValues = oriItem.getTimeValues();
						for(int k = startIndex; k < SEQSLIDEDAYS + startIndex; k++) {
							Double oriValue = oriTimeValues.get(k).getValue();
							if("SPRING".equals(season)) {
								//春
								if(oriValue >= SPRINGVALUE) {
									oriStartId = k;
									break;
								}
							} else if("SUMMER".equals(season)) {
								//夏
								if(oriValue >= SUMMERVALUE) {
									oriStartId = k;
									break;
								}
							} else if("AUTUMN".equals(season)) {
								//秋
								if(oriValue < SUMMERVALUE ) {
									oriStartId = k;
									break;
								}
							} else if("WINTER".equals(season)) {
								//冬
								if(oriValue < WINTERVALUE) {
									oriStartId = k;
									break;
								}
									
							}
						}
						//如果此索引和结束索引差距 <= 15, 则isContinue 为false，否则为true
						//如果 > 15天，但到后面持续满足条件，也为true
						Date currentStartDate = oriItem.getTimeValues().get(oriStartId).getDate();
						if(startDate.getTime() - currentStartDate.getTime() <= SECONDDAYS * CommonConstant.DAYTIMES) {
							firstSeasonDayItem.setContinue(true);
						} else {
							//判断从开始，到常年，是否一直满足条件
							boolean GT15DAYS = true;
							for(int k = oriStartId; k < timeValues.size(); k++) {
								TimeValue  itemTimeValue = timeValues.get(k);
								Date kItemDate = itemTimeValue.getDate();
								if(kItemDate.getTime() > startDate.getTime()) {
									break;
								}
								Double kItemValue = itemTimeValue.getValue();
								if("SPRING".equals(season)) {
									//春
									if(kItemValue < SPRINGVALUE) {
										GT15DAYS = false;
										break;
									}
								} else if("SUMMER".equals(season)) {
									//夏
									if(kItemValue < SUMMERVALUE) {
										GT15DAYS = false;
										break;
									}
								} else if("AUTUMN".equals(season)) {
									//秋
									if(kItemValue >= SUMMERVALUE ) {
										GT15DAYS = false;
										break;
									}
								} else if("WINTER".equals(season)) {
									//冬
									if(kItemValue >= WINTERVALUE) {
										GT15DAYS = false;
										break;
									}
										
								}
							}
							if(GT15DAYS) {
								firstSeasonDayItem.setContinue(true);
							} else {
								firstSeasonDayItem.setContinue(false);
							}
						}
						//计算对应的原始序列的开始日期
						firstSeasonDayItem.setOriStartId(oriStartId);
						firstSeasonDayList.add(firstSeasonDayItem);
						break;
					} else {
						cnt = 0;
						flag = false;
						continue;
					}
				}
			}
		}
		return firstSeasonDayList;
	}
	
	private List<FirstSeasonDayItem> getSecondSeasonDay(List<SequenceTimeValue> oriResultList, List<SequenceTimeValue> huadongList, 
			List<LinkedHashMap> hisSeasonList, String season, int year, List<FirstSeasonDayItem> firstSeasonDayList) {
		//1. 找到第一个连续SEQSLIDEDAYS天满足条件的序列
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<FirstSeasonDayItem> secondSeasonDayList = new ArrayList<FirstSeasonDayItem>();
		for(int i = 0; i < huadongList.size(); i++) {
			FirstSeasonDayItem firstSeasonDayItem = new FirstSeasonDayItem();
			SequenceTimeValue huadongItem = huadongList.get(i);
			//判断是否是需要二次判断的站
			String station_Id_C = huadongItem.getStation_Id_C();
			boolean isStationIn = false;
			for(int j = 0; j < firstSeasonDayList.size(); j++) {
				String secondStation_Id_C = firstSeasonDayList.get(j).getStation_Id_C();
				if(station_Id_C.equals(secondStation_Id_C)) {
					isStationIn = true;
					break;
				}
			}
			if(!isStationIn) continue;
			
			String station_Name = huadongItem.getStation_Name();
			firstSeasonDayItem.setStation_Id_C(station_Id_C);
			firstSeasonDayItem.setStation_Name(station_Name);
			// 计算常年日期对应到现在的日期
			Date startDate = null;
			for(int k = 0; k < hisSeasonList.size(); k++) {
				LinkedHashMap hisSeasonItem = hisSeasonList.get(k);
				String hisStation_Id_C = (String) hisSeasonItem.get("Station_Id_C"); 
				if(!station_Id_C.equals(hisStation_Id_C)) continue;
				String springStart = (String) hisSeasonItem.get("SpringStart"); 
				String summerStart = (String) hisSeasonItem.get("SummerStart"); 
				String autumnStart = (String) hisSeasonItem.get("AutumnStart"); 
				String winterStart = (String) hisSeasonItem.get("WinterStart"); 
				String startTimeStr = "";
				if("SPRING".equals(season)) {
					//春
					startTimeStr = year + "-" + springStart;
				} else if("SUMMER".equals(season)) {
					//夏
					startTimeStr = year + "-" + summerStart;
				} else if("AUTUMN".equals(season)) {
					//秋
					startTimeStr = year + "-" + autumnStart;
				} else if("WINTER".equals(season)) {
					//冬
					startTimeStr = year + "-" + winterStart;
				}
				firstSeasonDayItem.setHisStartDate(startTimeStr);
				try {
					startDate = sdf.parse(startTimeStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			}
			//根据常年的日期，和当年的滑动序列，计算第一次的日期
			List<TimeValue> timeValues = huadongItem.getTimeValues();
			//取到二次判断的开始索引位置
			int secondStart = 0;
			for(int j = 0; j < firstSeasonDayList.size(); j++) {
				FirstSeasonDayItem FirstSeasonDayItem = firstSeasonDayList.get(j);
				String secondStation_Id_C = FirstSeasonDayItem.getStation_Id_C();
				if(station_Id_C.equals(secondStation_Id_C)) {
					secondStart = FirstSeasonDayItem.getHuadongEndId();
					break;
				}
			}
			int cnt = 0; // 计算过程中满足条件的个数
			boolean flag = false;
			for(int j = secondStart; j < timeValues.size(); j++) {
				TimeValue timeValue = timeValues.get(j);
				Double value = timeValue.getValue();
				Date itemDate = timeValue.getDate();
				if("SPRING".equals(season)) {
					//春
					if(value < SPRINGVALUE || value >= SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("SUMMER".equals(season)) {
					//夏
					if(value < SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("AUTUMN".equals(season)) {
					//秋
					if(value < AUTUMNVALUE || value >= SUMMERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				} else if("WINTER".equals(season)) {
					//冬
					if(value > WINTERVALUE) {
						flag = true;
					} else {
						cnt ++;
					}
				}
				//判断序列是否满足
				if(flag || j == timeValues.size() - 1) {
					if(cnt >= SEQSLIDEDAYS) {
						//计算开始、结束索引
						int startIndex = 0;
						if(j == timeValues.size() - 1) {
							firstSeasonDayItem.setHuadongEndId(j);
							startIndex = j - cnt + 1;
						} else {
							firstSeasonDayItem.setHuadongEndId(j - 1);
							startIndex = j - cnt;
						}
						firstSeasonDayItem.setHuadongStartId(startIndex);
						//原始序列的开始
						SequenceTimeValue oriItem = null;
						for(int k = 0; k <oriResultList.size(); k++) {
							SequenceTimeValue tempOriItem = oriResultList.get(k);
							if(station_Id_C.equals(tempOriItem.getStation_Id_C())) {
								oriItem = tempOriItem;
								break;
							}
						}
						int oriStartId = 0;
						List<TimeValue> oriTimeValues = oriItem.getTimeValues();
						for(int k = startIndex; k < SEQSLIDEDAYS + startIndex; k++) {
							Double oriValue = oriTimeValues.get(k).getValue();
							if("SPRING".equals(season)) {
								//春
								if(oriValue >= SPRINGVALUE) {
									oriStartId = k;
									break;
								}
							} else if("SUMMER".equals(season)) {
								//夏
								if(oriValue >= SUMMERVALUE) {
									oriStartId = k;
									break;
								}
							} else if("AUTUMN".equals(season)) {
								//秋
								if(oriValue < SUMMERVALUE ) {
									oriStartId = k;
									break;
								}
							} else if("WINTER".equals(season)) {
								//冬
								if(oriValue < WINTERVALUE) {
									oriStartId = k;
									break;
								}
									
							}
						}
						//计算对应的原始序列的开始日期
						firstSeasonDayItem.setOriStartId(oriStartId);
						secondSeasonDayList.add(firstSeasonDayItem);
						break;
					} else {
						cnt = 0;
						flag = false;
						continue;
					}
				}
			}
		}
		return secondSeasonDayList;
	}
	/**
	 * 把需要进行二次判断的序列挑选出来
	 * @param firstSeasonDayList
	 * @return
	 */
	private List<FirstSeasonDayItem> getSecondList(List<FirstSeasonDayItem> firstSeasonDayList) {
		List<FirstSeasonDayItem> resultList = new ArrayList<FirstSeasonDayItem>();
		for(int i = 0; i < firstSeasonDayList.size(); i++) {
			FirstSeasonDayItem firstSeasonDayItem = firstSeasonDayList.get(i);
			boolean isContinue = firstSeasonDayItem.isContinue();
			if(!isContinue) {
				resultList.add(firstSeasonDayItem);
			}
		}
		return resultList;
	}
	
	/**
	 * 第一次就满足条件的
	 * @param firstSeasonDayList
	 * @return
	 */
	private List<FirstSeasonDayItem> getFirstList(List<FirstSeasonDayItem> firstSeasonDayList) {
		List<FirstSeasonDayItem> resultList = new ArrayList<FirstSeasonDayItem>();
		for(int i = 0; i < firstSeasonDayList.size(); i++) {
			FirstSeasonDayItem firstSeasonDayItem = firstSeasonDayList.get(i);
			boolean isContinue = firstSeasonDayItem.isContinue();
			if(isContinue) {
				resultList.add(firstSeasonDayItem);
			}
		}
		return resultList;
	}
	
	private List<SeasonResult> createSeasonReulst(List<FirstSeasonDayItem> firstSeasonDayList, List<SequenceTimeValue> resultList, int year) {
		List<SeasonResult> seasonResultList = new ArrayList<SeasonResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < firstSeasonDayList.size(); i++) {
			FirstSeasonDayItem firstSeasonDayItem = firstSeasonDayList.get(i);
			String station_Id_C = firstSeasonDayItem.getStation_Id_C();
			SeasonResult seasonResult = new SeasonResult();
			seasonResult.setYear(year);
			//计算开始日期
			String startTime = "";
			for(int j = 0; j < resultList.size(); j++) {
				SequenceTimeValue sequenceTimeValue = resultList.get(j);
				List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
				String itemStation_Id_C = sequenceTimeValue.getStation_Id_C();
				if(itemStation_Id_C.equals(station_Id_C)) {
					TimeValue itemTimeValue = timeValues.get(firstSeasonDayItem.getOriStartId());
					Date date = itemTimeValue.getDate();
					seasonResult.setStartDate(sdf.format(date));
					break;
				}
			}
			seasonResult.setStation_Id_C(station_Id_C);
			seasonResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			seasonResult.setArea(CommonUtil.getInstance().stationAreaMap.get(station_Id_C));
			seasonResult.setHisStartDate(firstSeasonDayItem.getHisStartDate());
			
			int anomaly = 0;
			Date hisDate = null, curDate = null;
			try {
				hisDate = sdf.parse(firstSeasonDayItem.getHisStartDate());
				curDate = sdf.parse(seasonResult.getStartDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long hisTime = hisDate.getTime();
			long curTime = curDate.getTime();
			anomaly = (int) ((curTime - hisTime) / (24 * 60 * 60 * 1000));
			String desc = CommonTool.calcTimes(anomaly);
			seasonResult.setAnomaly(anomaly);
			seasonResult.setDescription(desc);
			seasonResultList.add(seasonResult);
		}
		return seasonResultList;
	}
	
	/**
	 * 根据5.1.2.3 ，判断第一次或者第二次取哪一次的为准
	 * @param oriSecondSeasonDayItemResultList
	 * @param secondSeasonDayItemResultList
	 * @return
	 */
	private List<FirstSeasonDayItem> sumDays(List<FirstSeasonDayItem> oriSecondSeasonDayItemResultList, 
			List<FirstSeasonDayItem> secondSeasonDayItemResultList, List<SequenceTimeValue> huadongList, String season) {
		List<FirstSeasonDayItem> resultFirstSeasonDayList = new ArrayList<FirstSeasonDayItem>();
		//1. 遍历secondSeasonDayItemResultList，找到二次计算的结果
		for(int i = 0; i < secondSeasonDayItemResultList.size(); i++) {
			//2. 找到对应的第一次的结果
			for(int j = 0; j < oriSecondSeasonDayItemResultList.size(); j++) {
				FirstSeasonDayItem secondeFirstSeasonDayItem = secondSeasonDayItemResultList.get(i);
				String station_Id_C = secondeFirstSeasonDayItem.getStation_Id_C();
				int satisfyCnt = 0, unSatisfyCnt = 0;
				FirstSeasonDayItem oriSecondeFirstSeasonDayItem = oriSecondSeasonDayItemResultList.get(j);
				String oriStation_Id_C = oriSecondeFirstSeasonDayItem.getStation_Id_C();
				if(station_Id_C.equals(oriStation_Id_C)) {
//					int start = oriSecondeFirstSeasonDayItem.getHuadongEndId() + 1;
					//计算第一次后第6天作为开始，也就是说过程5天为一个过程。
					int start = oriSecondeFirstSeasonDayItem.getHuadongStartId() + SLIDEDAYS;
					int end = secondeFirstSeasonDayItem.getHuadongStartId() - 1;
					//3. 取中间的滑动的结果，判断
					for(int k = 0; k < huadongList.size(); k++) {
						SequenceTimeValue sequenceTimeValue = huadongList.get(k);
						String sequenceStation_Id_C = sequenceTimeValue.getStation_Id_C();
						List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
						if(station_Id_C.equals(sequenceStation_Id_C)) {
							for(int t = start; t <= end; t++) {
								Double value = timeValueList.get(t).getValue();
								if("SPRING".equals(season)) {
									//春
									if(value < SPRINGVALUE || value >= SUMMERVALUE) {
										unSatisfyCnt++;
									} else {
										satisfyCnt++;
									}
								} else if("SUMMER".equals(season)) {
									//夏
									if(value < SUMMERVALUE) {
										unSatisfyCnt++;
									} else {
										satisfyCnt++;
									}
								} else if("AUTUMN".equals(season)) {
									//秋
									if(value < AUTUMNVALUE || value >= SUMMERVALUE) {
										unSatisfyCnt++;
									} else {
										satisfyCnt++;
									}
								} else if("WINTER".equals(season)) {
									//冬
									if(value > WINTERVALUE) {
										unSatisfyCnt++;
									} else {
										satisfyCnt++;
									}
								}
							}
							break;
						}
					}
					//判断满足天数和不满足天数的对比
					if(satisfyCnt >= unSatisfyCnt) {
						resultFirstSeasonDayList.add(oriSecondeFirstSeasonDayItem);
					} else {
						resultFirstSeasonDayList.add(secondeFirstSeasonDayItem);
					}
					break;
				}
				
			}
		}
		
		return resultFirstSeasonDayList;
	}
	
	public List<SeasonResult> getSeasonByYear(int year, String season, int startMon, int startDay, int endMon, int endDay, String stations) {
		//1. 查询时段的序列值
		int endYear = year;
		List<SequenceTimeValue> resultList = getResultsBySeason(year, endYear, season, startMon, startDay, endMon, endDay, stations, "TIMES");
		//2. 转换为滑动序列
		List<SequenceTimeValue> huadongList = chgHuaDong(resultList);
		//3. 查询过去30年的季节开始时间
		List<LinkedHashMap> hisSeasonList = getHisSeasonList();
		//4. 找到第一次满足条件的季节开始时间
		//5. 判断第一次是否满足条件，偏早低于15天，或者后15天同时都满足条件的
		List<FirstSeasonDayItem> firstSeasonDayList = getFirstSeasonDay(resultList, huadongList, hisSeasonList, season, endYear);
		//6. 如果第一次不满足，则查找第二次满足的日期
		List<FirstSeasonDayItem> firstSeasonDayItemResultList = getFirstList(firstSeasonDayList);
		List<FirstSeasonDayItem> oriSecondSeasonDayItemResultList = getSecondList(firstSeasonDayList);
		//7. 判断第一次和第二次，两者挑选一个满足条件的作为结果
		List<FirstSeasonDayItem> secondSeasonDayItemResultList = getSecondSeasonDay(resultList, huadongList, hisSeasonList, season, endYear, oriSecondSeasonDayItemResultList);
		//TODO 第一次，二次结果再对比5.1.2.3
		List<FirstSeasonDayItem> finalSecondeSeasonDayResultList = sumDays(oriSecondSeasonDayItemResultList, secondSeasonDayItemResultList, huadongList, season);
		//8. 整理结果
		List<SeasonResult> firstSeasonResultList = createSeasonReulst(firstSeasonDayItemResultList, resultList, year);
		List<SeasonResult> secondSeasonResultList = createSeasonReulst(finalSecondeSeasonDayResultList, resultList, year);
		//8. 对于没有查找到记录的，补齐结果
		firstSeasonResultList.addAll(secondSeasonResultList);
		addAllRecord(firstSeasonResultList, resultList);
		//8. 整理结果序号
		sortIndex(firstSeasonResultList);
		//9. 返回结果序列
		return firstSeasonResultList;
	}
	public List<SeasonYearsResult> getSeasonByStationAndYears(String season, int startMon, int startDay, int endMon, int endDay, 
									int startYear, int endYear, String station_Id_C) {
		List<SeasonYearsResult> yearsSeasonResultList2 = new ArrayList<SeasonYearsResult>(); 
		//1. 查询气温的序列
		//考虑到冬季
		List<SequenceTimeValue> resultList = getResultsBySeason(startYear, endYear, season, startMon, startDay, endMon, endDay, station_Id_C, "YEARS");
		HashMap<Integer, List<SequenceTimeValue>> sequenceTimeValueListMap = new HashMap<Integer, List<SequenceTimeValue>>();
		for(int i = 0; i < resultList.size(); i++) {
			SequenceTimeValue item = resultList.get(i);
			int year = item.getYear();
			List<SequenceTimeValue> sequenceTimeValueList = sequenceTimeValueListMap.get(year);
			if(sequenceTimeValueList == null) {
				sequenceTimeValueList = new ArrayList<SequenceTimeValue>();
			}
			sequenceTimeValueList.add(item);
			sequenceTimeValueListMap.put(year, sequenceTimeValueList);
		}
		//2. 每年分别做计算
		Iterator<Integer> it = sequenceTimeValueListMap.keySet().iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List<SeasonResult> yearsSeasonResultList = getSeasonResult(sequenceTimeValueListMap.get(year), season, year);
			SeasonYearsResult seasonYearsResult = new SeasonYearsResult();
			if(yearsSeasonResultList != null && yearsSeasonResultList.size() > 0) {
				SeasonResult seasonResult = yearsSeasonResultList.get(0);
				seasonYearsResult.setAnomaly(seasonResult.getAnomaly());
				seasonYearsResult.setArea(seasonResult.getArea());
				seasonYearsResult.setDescription(seasonResult.getDescription());
				seasonYearsResult.setHisStartDate(seasonResult.getHisStartDate());
				seasonYearsResult.setIndex(seasonResult.getIndex());
				seasonYearsResult.setPersistDays(seasonResult.getPersistDays());
				seasonYearsResult.setStartDate(seasonResult.getStartDate());
				seasonYearsResult.setStation_Id_C(seasonResult.getStation_Id_C());
				seasonYearsResult.setStation_Name(seasonResult.getStation_Name());
				seasonYearsResult.setYear(year);
				yearsSeasonResultList2.add(seasonYearsResult);
			}
		}
		return yearsSeasonResultList2;
	}
	
	public void sortIndex(List<SeasonResult> seasonResultList) {
		if(seasonResultList == null) return;
		for(int i = 0; i < seasonResultList.size(); i++) {
			SeasonResult seasonResult = seasonResultList.get(i);
			seasonResult.setIndex(i + 1);
		}
	}
	
	public void addAllRecord(List<SeasonResult> seasonResultList, List<SequenceTimeValue> resultList) {
		if(seasonResultList != null && seasonResultList.size() == resultList.size()) return ;
		for(int i = 0; i < resultList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = resultList.get(i);
			String iStation_Id_C = sequenceTimeValue.getStation_Id_C();
			boolean flag = false;
			for(int j = 0; j < seasonResultList.size(); j++) {
				SeasonResult seasonResult = seasonResultList.get(j);
				String jStation_Id_C = seasonResult.getStation_Id_C();
				if(iStation_Id_C.equals(jStation_Id_C)) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				SeasonResult item = new SeasonResult();
				item.setStation_Id_C(iStation_Id_C);
				item.setStation_Name(sequenceTimeValue.getStation_Name());
				seasonResultList.add(item);
			}
		}
	}
	/**
	 * 对比结果，构造成最终的对象，返回
	 * @param chuzhongList
	 * @param hisSeasonList
	 * @return
	 */
	private List<SeasonResult> compare(List<HashMap> chuzhongList, List<LinkedHashMap> hisSeasonList) {
		return null;
	}
	
	/**
	 * 递归计算日期，当比常年的时间过早的时候
	 * @param chuzhongList
	 * @param hisSeasonList
	 */
	private List<HashMap> calcEarlyDays(List<HashMap> chuzhongList, List<LinkedHashMap> hisSeasonList, List<SequenceTimeValue> huadongList, 
			List<SequenceTimeValue> resultList, int year, String season) {
		List<HashMap> inValidList = compareEarly(chuzhongList, hisSeasonList, huadongList, year, season);
		if(inValidList == null || inValidList.size() == 0) {
			return chuzhongList;
		} else {
			//1.截断数组，然后再重新调用
			subList(huadongList, inValidList);
			chuzhongList = chgChuZhongRi(huadongList, resultList, season);
			return calcEarlyDays(chuzhongList, hisSeasonList,huadongList, resultList, year, season);
		}
	}
	/**
	 * 截断数组，根据hisSeasonList做判定，至少截断5天以上
	 * @param chuzhongList
	 * @param hisSeasonList
	 * @return
	 */
	private void subList(List<SequenceTimeValue> huadongList, List<HashMap> inValidList) {
		List<SequenceTimeValue> resultHuaDongList = new ArrayList<SequenceTimeValue>();
		for(int i = 0; i < huadongList.size(); i++) {
			SequenceTimeValue itemSequenceTimeValue = huadongList.get(i);
			String station_Id_C = itemSequenceTimeValue.getStation_Id_C();
			for(int j = 0; j < inValidList.size(); j++) {
				HashMap itemMap = inValidList.get(j);
				String itemStation_Id_C = (String) itemMap.get("Station_Id_C");
				if(station_Id_C.equals(itemStation_Id_C)) {
					List<TimeValue> timeValueList = itemSequenceTimeValue.getTimeValues();
					timeValueList.subList(0, SLIDEDAYS).clear();
				}
			}
		}
	}
	
	/**
	 * 和历史的对比，找到 超过15天的
	 * @param chuzhongList
	 * @param hisSeasonList
	 * @return
	 */
	private List<HashMap> compareEarly(List<HashMap> chuzhongList, List<LinkedHashMap> hisSeasonList, List<SequenceTimeValue> huadongList, int year, String season) {
		//1. 把hisSeasonList中的09-29月日格式，转换成具体的日期，要注意跨年的问题（处理成第一年，历史不会超过1月才入冬）
		List<HashMap> resultChuZhongList = new ArrayList<HashMap>();
		List<HashMap> hisSeasonDateList = new ArrayList<HashMap>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < hisSeasonList.size(); i++) {
			HashMap hisSeasonDateMap = new HashMap();
			LinkedHashMap hisSeasonItem = hisSeasonList.get(i);
			String station_Id_C = (String) hisSeasonItem.get("Station_Id_C"); 
			hisSeasonDateMap.put("Station_Id_C", station_Id_C);
			String springStart = (String) hisSeasonItem.get("SpringStart"); 
			String summerStart = (String) hisSeasonItem.get("SummerStart"); 
			String autumnStart = (String) hisSeasonItem.get("AutumnStart"); 
			String winterStart = (String) hisSeasonItem.get("WinterStart"); 
			String startTimeStr = "";
			if("SPRING".equals(season)) {
				//春
				startTimeStr = year + "-" + springStart;
			} else if("SUMMER".equals(season)) {
				//夏
				startTimeStr = year + "-" + summerStart;
			} else if("AUTUMN".equals(season)) {
				//秋
				startTimeStr = year + "-" + autumnStart;
			} else if("WINTER".equals(season)) {
				//冬
				startTimeStr = year + "-" + winterStart;
			}
			try {
				Date date = sdf.parse(startTimeStr);
				hisSeasonDateMap.put("StartTime", startTimeStr);
				hisSeasonDateMap.put("StartDate", date);
				hisSeasonDateList.add(hisSeasonDateMap);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//2. 比较日期
		for(int i = chuzhongList.size() - 1; i >= 0; i--) {
			HashMap chuzhongMap = chuzhongList.get(i);
			String station_Id_C = (String) chuzhongMap.get("Station_Id_C");
			Date startDate = (Date) chuzhongMap.get("StartTime");
			for(int j = 0; j < hisSeasonDateList.size(); j++) {
				HashMap hisSeasonDateMap = hisSeasonDateList.get(j);
				String itemStation_Id_C = (String) hisSeasonDateMap.get("Station_Id_C");
				if(itemStation_Id_C.equals(station_Id_C)) {
					Date hisStartDate = (Date) hisSeasonDateMap.get("StartDate");
					if(hisStartDate.getTime() - startDate.getTime() > SECONDDAYS * CommonConstant.DAYTIMES) {
						//如果 >= SECONDDAYS,则判断从当前时间，到常年时间的所有序列结果是否满足条件，如果满足的话，则整体满足
						boolean flag = false;
						for(int k = 0; k < huadongList.size(); k++) {
							SequenceTimeValue sequenceTimeValue = huadongList.get(k);
							String huaDongStation_Id_C = sequenceTimeValue.getStation_Id_C();
							List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
							//添加一个条件判断，如果现在的序列最后一天还没有达到常年日期的头一天，则不满足
							TimeValue lastTimeValue = timeValueList.get(timeValueList.size() - 1);
							if(lastTimeValue.getDate().getTime() <= hisStartDate.getTime()) {
								flag = true;
								break;
							}
							if(huaDongStation_Id_C.equals(station_Id_C)) {
								for(int t = 0; t < timeValueList.size(); t++) {
									TimeValue timeValue = timeValueList.get(t);
									if(timeValue.getDate().getTime() > startDate.getTime()) {
										Double value = timeValue.getValue();
										if("SPRING".equals(season)) {
											//春
											if(value < SPRINGVALUE || value >= SUMMERVALUE) {
												flag = true;
												break;
											}
										} else if("SUMMER".equals(season)) {
											//夏
											if(value < SUMMERVALUE) {
												flag = true;
												break;
											}
										} else if("AUTUMN".equals(season)) {
											//秋
											if(value < AUTUMNVALUE || value >= SUMMERVALUE) {
												flag = true;
												break;
											}
										} else if("WINTER".equals(season)) {
											//冬
											if(value > WINTERVALUE) {
												flag = true;
												break;
											}
										}
									}
								}
								break;
							}
						}
						if(flag) {
							resultChuZhongList.add(hisSeasonDateMap);
						}
					}
					break;
				}
			}
		}
		return resultChuZhongList;
	}
	
	/**
	 * 计算初终日
	 * @param huadongList 滑动结果序列
	 * @param temAvgList 原始序列
	 * @param season 季节
	 * @return
	 */
	private List<HashMap> chgChuZhongRi(List<SequenceTimeValue> huadongList, List<SequenceTimeValue> temAvgList, String season) {
		//1. 计算连续SLIDEDAYS满足季节要求的日期。
		List<HashMap> resultList = new ArrayList<HashMap>();
		for(int i = 0; i < huadongList.size(); i++) {
			SequenceTimeValue itemSequenceTimeValue = huadongList.get(i);
			String station_Id_C = itemSequenceTimeValue.getStation_Id_C();
			List<TimeValue> timeValueList = itemSequenceTimeValue.getTimeValues();
			for(int j = 0; j < timeValueList.size() - SLIDEDAYS + 1; j++) {
				int cnt  = 0; //计数器
				for(int k = 0; k < SLIDEDAYS; k++) {
					TimeValue timeValue = timeValueList.get(j + k);
					Double value = timeValue.getValue();
					if("SPRING".equals(season)) {
						//春
						if(value >= SPRINGVALUE && value < SUMMERVALUE) {
							cnt++;
						}
					} else if("SUMMER".equals(season)) {
						//夏
						if(value >= SUMMERVALUE) {
							cnt++;
						}
					} else if("AUTUMN".equals(season)) {
						//秋
						if(value >= AUTUMNVALUE && value < SUMMERVALUE) {
							cnt++;
						}
					} else if("WINTER".equals(season)) {
						//冬
						if(value <= WINTERVALUE) {
							cnt++;
						}
					}
				}
				//满足滑动的条件
				if(cnt >= SEQSLIDEDAYS) {
					HashMap resultMap = new HashMap();
					resultMap.put("Station_Id_C", station_Id_C);
					//滑动的日期开始，往前，找原始序列，找到第一个，即为开始日期
					TimeValue timeValue = timeValueList.get(j + SLIDEDAYS - 1);
					Date date = timeValue.getDate();
					for(int k = 0; k < temAvgList.size(); k++){
						SequenceTimeValue sequenceTimeValue = temAvgList.get(k);
						String itemStation_Id_C = sequenceTimeValue.getStation_Id_C();
						if(itemStation_Id_C.equals(station_Id_C)) {
							List<TimeValue> itemTimeValues = sequenceTimeValue.getTimeValues();
							for(int t = 0; t < itemTimeValues.size(); t++) {
								TimeValue itemTimeValue = itemTimeValues.get(t);
								Date itemDate = itemTimeValue.getDate();
								if(date.getTime() - itemDate.getTime() == CommonConstant.DAYTIMES * (SLIDEDAYS - 1)) {
									//从开始的第4天开始
									for(int l = t - SLIDEDAYS + 1; l < t; l++) {
										TimeValue itemLTimeValue = itemTimeValues.get(l);
										Double itemLValue = itemLTimeValue.getValue();
										Date itemLDate = itemLTimeValue.getDate();
										if("SPRING".equals(season)) {
											//春
											if(itemLValue >= SPRINGVALUE && itemLValue < SUMMERVALUE) {
												resultMap.put("StartTime", itemLDate);
												resultList.add(resultMap);
												break;
											}
										} else if("SUMMER".equals(season)) {
											//夏
											if(itemLValue >= SUMMERVALUE) {
												resultMap.put("StartTime", itemLDate);
												resultList.add(resultMap);
												break;
											}
										} else if("AUTUMN".equals(season)) {
											//秋
											if(itemLValue >= AUTUMNVALUE && itemLValue < SUMMERVALUE) {
												resultMap.put("StartTime", itemLDate);
												resultList.add(resultMap);
												break;
											}
										} else if("WINTER".equals(season)) {
											//冬
											if(itemLValue <= WINTERVALUE) {
												resultMap.put("StartTime", itemLDate);
												resultList.add(resultMap);
												break;
											} 
										}
									}
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}
		}
		//2. 根据日期找到对应的原始序列，查找开始日期
		return resultList;
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
	
	private List<SeasonResult> compare(List<LinkedHashMap> hisSeasonList, List<HashMap> chuzhongList, String season, int year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SeasonResult> resultList = new ArrayList<SeasonResult>();
		int index = 1;
		//添加上站号对应的地区
		StationArea stationArea = new StationArea();
		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		for(int i=0; i<hisSeasonList.size(); i++) {
			LinkedHashMap map = hisSeasonList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			String startTime = "";
			Date curDate = null;
			if("SPRING".equals(season)) {
				//春
				startTime = (String) map.get("SpringStart");
			} else if("SUMMER".equals(season)) {
				//夏
				startTime = (String) map.get("SummerStart"); 
			} else if("AUTUMN".equals(season)) {
				// 秋
				startTime = (String) map.get("AutumnStart");
			} else if("WINTER".equals(season)) {
				startTime = (String) map.get("WinterStart");
			}
			for(int j = 0; j < chuzhongList.size(); j++) {
				HashMap chuzhongMap = chuzhongList.get(j);
				String itemStation_Id_C = (String) chuzhongMap.get("Station_Id_C");
				if(itemStation_Id_C.equals(station_Id_C)) {
					curDate = (Date) chuzhongMap.get("StartTime");
					break;
				}
			}
			if(null == curDate) {
				continue;
			}
			//计算距平
			String hisDateStr = year + "-" + startTime;
			Date hisDate = null;
			try {
				hisDate = sdf.parse(hisDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int anomaly = 0;
			long hisTime = hisDate.getTime();
			long curTime = curDate.getTime();
			anomaly = (int) ((curTime - hisTime) / (24 * 60 * 60 * 1000));
			String desc = CommonTool.calcTimes(anomaly);
			SeasonResult seasonResult = new SeasonResult();
			seasonResult.setStation_Id_C(station_Id_C);
			seasonResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			seasonResult.setIndex(index++);
			seasonResult.setAnomaly(anomaly);
			seasonResult.setDescription(desc);
			seasonResult.setHisStartDate(hisDateStr);
			seasonResult.setStartDate(sdf.format(curDate));
			seasonResult.setArea(stationAreaMap.get(station_Id_C));
			resultList.add(seasonResult);
		}
		return resultList;
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
//		String items = "";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		HashMap paramMap = new HashMap();
//		paramMap.put("startYear", startYear);
//		paramMap.put("endYear", endYear);
//		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
//		List<LinkedHashMap> resultList = null;
//		if("SPRING".equals(season)) {
//			//春
//			items = CommonTool.createItemStrByTimes(startYear, endYear, startMon, endMon, startDay, endDay);
//			paramMap.put("items", items);
//			resultList = iSeason.querySpringSeason(paramMap);
//		} else if("SUMMER".equals(season)) {
//			//夏
//			items = CommonTool.createItemStrByTimes(startYear, endYear, startMon, endMon, startDay, endDay);
//			paramMap.put("items", items);
//			resultList = iSeason.querySpringSeason(paramMap);
//		} else if("AUTUMN".equals(season)) {
//			// 秋
//			items = CommonTool.createItemStrByTimes(startYear, endYear, startMon, endMon, startDay, endDay);
//			paramMap.put("items", items);
//			resultList = iSeason.querySpringSeason(paramMap);
//		} else if("WINTER".equals(season)) {
//			//冬
//			String startStr = startYear + "-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
//			String endStr = endYear + "-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
//			Date startDate = null, endDate = null;
//			try {
//				startDate = sdf.parse(startStr);
//				endDate = sdf.parse(endStr);
//			} catch (ParseException e) {
//				e.printStackTrace();
//				return null;
//			}
//			items = CommonTool.createItemStrByRange(startDate, endDate);
//			paramMap.put("items", items);
//			resultList = iSeason.querySpringSeason(paramMap);
//		}
//		return resultList;
	}
	
	public Object getHisSeason(String season, int startMon, int startDay, int endMon, int endDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ISeason iSeason = (ISeason)ContextLoader.getCurrentWebApplicationContext().getBean("SeasonImpl");
		int tempStartYear = 2000, tempEndYear = 2001;
		String items = "";
		HashMap paramMap = new HashMap();
		paramMap.put("startYear", STARTYEAR);
		paramMap.put("endYear", ENDYEAR);
		List<LinkedHashMap> resultList = null;
		if("SPRING".equals(season)) {
			//春
			items = CommonTool.createItemStrByTimes(tempStartYear, tempEndYear,  startMon, endMon, startDay, endDay);
			paramMap.put("items", items);
			resultList = iSeason.querySpringSeason(paramMap);
		} else if("SUMMER".equals(season)) {
			//夏
			items = CommonTool.createItemStrByTimes(tempStartYear, tempEndYear,  startMon, endMon, startDay, endDay);
			paramMap.put("items", items);
			resultList = iSeason.querySummerSeason(paramMap);
		} else if("AUTUMN".equals(season)) {
			// 秋
			items = CommonTool.createItemStrByTimes(tempStartYear, tempEndYear,  startMon, endMon, startDay, endDay);
			paramMap.put("items", items);
			resultList = iSeason.queryAutumnSeason(paramMap);
		} else if("WINTER".equals(season)) {
			//冬
			String startStr = "1999-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay);
			String endStr = "2000-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay);
			Date startDate = null, endDate = null;
			try {
				startDate = sdf.parse(startStr);
				endDate = sdf.parse(endStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return "";
			}
			items = CommonTool.createItemStrByRange(startDate, endDate);
			paramMap.put("items", items);
			paramMap.put("endYear", ENDYEAR + 1);
			resultList = iSeason.queryWinderSeason(paramMap);
		}
		Map<String, String> mapResult = dispose(resultList, season, "t_tem_avg");
		return mapResult;
	}
	
	private Map<String, String> dispose(List<LinkedHashMap> resultList, String season, String tableName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		//key : station_Id_C_year value:yyyy-MM-dd
		Map<String, String> startDayMap = new HashMap<String, String>();
		int cnt = 0; // 计数器
		for(int i=0; i<resultList.size(); i++) {
			if("SPRING".equals(season) || "SUMMER".equals(season) || "AUTUMN".equals(season)) {
				cnt = 0;
			}
			Map map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			String startDayStr = "";
			long startDayTime = 0L;
			String startDateStr = "";
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					String dateStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					int mon = Integer.parseInt(key.substring(1, 3));
					if("WINTER".equals(season)) {
						if(mon < 10) {
							dateStr = (year - 1) + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
						}
					}
					
					Date timeDate = null;
					long tempTime = 0L;
					try {
						timeDate =  sdf.parse(dateStr);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					tempTime = timeDate.getTime();
					if("SPRING".equals(season)) {
						//春
						if(value >= SPRINGVALUE) {
							if(tempTime - startDayTime == 24 * 60 * 60 * 1000) {
								//连续的时间达到了
								cnt++;
							} else {
								startDateStr = dateStr;
								cnt = 1;
							}
							startDayTime = tempTime;
						}
					} else if("SUMMER".equals(season)) {
						//夏
						if(value >= SUMMERVALUE) {
							if(tempTime - startDayTime == 24 * 60 * 60 * 1000) {
								//连续的时间达到了
								cnt++;
							} else {
								startDateStr = dateStr;
								cnt = 1;
							}
							startDayTime = tempTime;
						}
					} else if("AUTUMN".equals(season)) {
						// 秋
						if(value <= AUTUMNVALUE) {
							if(tempTime - startDayTime == 24 * 60 * 60 * 1000) {
								//连续的时间达到了
								cnt++;
							} else {
								startDateStr = dateStr;
								cnt = 1;
							}
							startDayTime = tempTime;
						}
					} else if("WINTER".equals(season)) {
						//冬
						if(mon < 10) {
							//计算到头一年
							if(year == STARTYEAR) {
								continue;
							}
						}
						if(mon >= 10) {
							if(year == ENDYEAR + 1) {
								continue;
							}
						}
						
						if(value <= WINTERVALUE) {
							if(tempTime - startDayTime == 24 * 60 * 60 * 1000) {
								//连续的时间达到了
								cnt++;
							} else {
								startDateStr = dateStr;
								cnt = 1;
							}
							startDayTime = tempTime;
						}
					}
					if(cnt == SLIDEDAYS) {
						cnt = 0;
						break;
					}
				}
			}
			startDayMap.put(station_Id_C + "_" + year, startDateStr);
		}
		
		// 遍历startDayMap，找到结果
		Map<String, Integer> stationDaysMap = new HashMap<String, Integer>();
		//记录有效的日期的值
		Map<String, Integer> stationValidDaysCntMap = new HashMap<String, Integer>();
		
		Set<String> set = startDayMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			String dateStr = startDayMap.get(key);
			String station_iD_C = key.split("_")[0];
			int year = Integer.parseInt(key.split("_")[1]);
			Integer sum = stationDaysMap.get(station_iD_C);
			Integer cnt2 = stationValidDaysCntMap.get(station_iD_C);
			try {
				if("".equals(dateStr)) {
					continue;
				}
				if(sum == null) {
					stationDaysMap.put(station_iD_C, CommonTool.calcDaysInYear(dateStr));
				} else {
					stationDaysMap.put(station_iD_C, CommonTool.calcDaysInYear(dateStr) + sum);
				}
				if(cnt2 == null) {
					stationValidDaysCntMap.put(station_iD_C, 1);
				} else {
					stationValidDaysCntMap.put(station_iD_C, ++cnt2);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
		}
		//换算成日期
		Map<String, String> stationDateMap = new HashMap<String, String>();
		Set<String> setStationDate = stationDaysMap.keySet();
		Iterator<String> itStationDate = setStationDate.iterator();
		while(itStationDate.hasNext()) {
			String key = itStationDate.next();
			int days = stationDaysMap.get(key);
			String dateStr = CommonTool.calcDateStr(Math.round((days + 0.0) / stationValidDaysCntMap.get(key)) , 1999); // 找一个非闰年的年
			stationDateMap.put(key, dateStr);
		}
		return stationDateMap;
	}
	
	/**
	 * 计算持续时间
	 * @param starts
	 * @param ends
	 */
	public void calcPersistDays(List<SeasonResult> starts, List<SeasonResult> ends) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < starts.size(); i++) {
			SeasonResult startResult = starts.get(i); 
			String station_Id_C = startResult.getStation_Id_C();
			String startDateStr = startResult.getStartDate();
			if(startDateStr == null) continue;
			Date startDate = null;
			try {
				startDate = sdf.parse(startDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(startDate == null) continue;
			for(int j = 0; j < ends.size(); j++) {
				String jStation_Id_C = ends.get(j).getStation_Id_C();
				String jStartDateStr = ends.get(j).getStartDate();
				if(station_Id_C.equals(jStation_Id_C)) {
					if(jStartDateStr == null) break;
					Date jStartDate = null;
					try {
						jStartDate = sdf.parse(jStartDateStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(jStartDate == null) break;
					int persistDays = CommonTool.caleDays(startDateStr, jStartDateStr);
					startResult.setPersistDays(persistDays);
					break;
				}
			}
		}
	}
	
	public void calcPersistDays2(List<SeasonYearsResult> list1, List<SeasonYearsResult> list2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < list1.size(); i++) {
			SeasonYearsResult seasonYearsResult = list1.get(i);
			int year = seasonYearsResult.getYear();
			String startDateStr = seasonYearsResult.getStartDate();
			if(startDateStr == null) continue;
			Date startDate = null;
			try {
				startDate = sdf.parse(startDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(startDate == null) continue;
			for(int j = 0; j < list2.size(); j++) {
				SeasonYearsResult jSeasonYearsResult = list1.get(j);
				int jYear = jSeasonYearsResult.getYear();
				if(year == jYear) {
					String jStartDateStr = jSeasonYearsResult.getStartDate();
					if(jStartDateStr == null) break;
					Date jStartDate = null;
					try {
						jStartDate = sdf.parse(jStartDateStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(jStartDate == null) {
						break;
					}
					int persistDays = CommonTool.caleDays(startDateStr, jStartDateStr);
					seasonYearsResult.setPersistDays(persistDays);
					break;
				}
			}
		}
	}
}
