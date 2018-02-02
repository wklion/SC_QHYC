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
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.ActiveAccumulatedTemp;
import com.spd.common.CommonConstant;
import com.spd.common.HighTmpDaysResult;
import com.spd.common.HighTmpRangeParam;
import com.spd.common.HighTmpRangeResult;
import com.spd.common.HighTmpRangeResultSequence;
import com.spd.common.HighTmpResult;
import com.spd.common.HighTmpSequence;
import com.spd.common.HighTmpTotal;
import com.spd.common.HighTmpYearsParam;
import com.spd.common.HighTmpYearsResult;
import com.spd.common.Station;
import com.spd.common.StationType;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.ValidAccumulatedTemp;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.service.ICommon;
import com.spd.service.IHighTmp;
import com.spd.tool.CommonTool;

/**
 * 高温统计处理
 * @author Administrator
 *
 */
public class HighTmpBus {
	
	private static double LEVEL1HIGHTMP = 35;

	private static double LEVEL2HIGHTMP = 37;
	
	private static double LEVEL3HIGHTMP = 40;
	
	private static List<Station> resultStations = new ArrayList<Station>();
	
	/**
	 * 按时间段统计高温
	 * @param timesParam
	 * @return
	 */
	public HighTmpDaysResult highTmpByTimes(TimesParam timesParam, String type) {
		HighTmpDaysResult highTmpDaysResult = new HighTmpDaysResult();
		List<HighTmpTotal> highTmpTotalList = new ArrayList<HighTmpTotal>();
		List<HighTmpSequence> highTmpSequenceList = new ArrayList<HighTmpSequence>();
		StationType stationType = StationType.getStationType(type);
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("level1HighTmp", LEVEL1HIGHTMP);
//		switch(stationType) {
//		case ALL:
//			paramMap.put("station", "%");
//			break;
//		case AWS:
//			paramMap.put("station", "5%");
//			break;
//		case MWS:
//			paramMap.put("station", "A%");
//			break;
//		default:
//			paramMap.put("station", "%");
//			break;
//		}
		List<Station> listStations = getHighTmpStation();
		List<String> Station_Id_Cs = new ArrayList<String>();
		for(int i = 0; i < listStations.size(); i++) {
			Station_Id_Cs.add(listStations.get(i).getStation_Id_C());
		}
		paramMap.put("Station_Id_Cs", Station_Id_Cs);
		IHighTmp highTmp = (IHighTmp)ContextLoader.getCurrentWebApplicationContext().getBean("HighTmpImpl");
		List<LinkedHashMap> resultList = highTmp.queryHighTmpByStation(paramMap);
//		HighTmpTotal
		LinkedHashMap<String, HighTmpTotal> resultMap = new LinkedHashMap<String, HighTmpTotal>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
//			String station_Name = (String) itemMap.get("Station_Name");
			Double tem_Max = (Double) itemMap.get("TEM_Max");
			HighTmpTotal highTmpTotal = resultMap.get(station_Id_C);
			if(highTmpTotal == null) {
				highTmpTotal = new HighTmpTotal();
				highTmpTotal.setStation_Id_C(station_Id_C);
//				highTmpTotal.setStation_Name(station_Name);
				resultMap.put(station_Id_C, highTmpTotal);
			}
			if(tem_Max >= LEVEL1HIGHTMP) {
				highTmpTotal.setGt35Days(highTmpTotal.getGt35Days() + 1);
			}
			if(tem_Max >= LEVEL2HIGHTMP) {
				highTmpTotal.setGt37Days(highTmpTotal.getGt37Days() + 1);
			}
			if(tem_Max >= LEVEL1HIGHTMP && tem_Max < LEVEL2HIGHTMP) {
				highTmpTotal.setGt35lt37Days(highTmpTotal.getGt35lt37Days() + 1);
			} else if(tem_Max >= LEVEL2HIGHTMP && tem_Max < LEVEL3HIGHTMP) {
				highTmpTotal.setGt37lt39Days(highTmpTotal.getGt37lt39Days() + 1);
			} else if(tem_Max >= LEVEL3HIGHTMP) {
				highTmpTotal.setGt40Days(highTmpTotal.getGt40Days() + 1);
			}
			resultMap.put(station_Id_C, highTmpTotal);
		  }
		for(int i = 0; i < listStations.size(); i++) {
			Station station = listStations.get(i);
			String station_id_C = station.getStation_Id_C();
			if(resultMap.get(station_id_C) != null) {
				HighTmpTotal highTmpTotal = resultMap.get(station_id_C);
				highTmpTotal.setStation_Name(station.getStation_Name());
				highTmpTotalList.add(highTmpTotal);
			} else {
				HighTmpTotal highTmpTotal = new HighTmpTotal();
				highTmpTotal.setStation_Id_C(station_id_C);
				highTmpTotal.setStation_Name(station.getStation_Name());
				highTmpTotalList.add(highTmpTotal);
			}
		}
//		Iterator<String> it = resultMap.keySet().iterator();
//		while(it.hasNext()) {
//			String key = it.next();
//			highTmpTotalList.add(resultMap.get(key));
//		}
		highTmpDaysResult.setHighTmpTotalList(highTmpTotalList);
		//计算高温日期
		TimesRangeParam timesRangeParam = new TimesRangeParam();
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		DBTable dbTable = new DBTable();
		//查询当年 
		SimpleDateFormat mmdd = new SimpleDateFormat("MMdd");
//		switch(stationType) {
//		case ALL:
//			dbTable.queryDataByRangeTimes(timesRangeParam, "%", "t_tem_max");
//			break;
//		case AWS:
//			dbTable.queryDataByRangeTimes(timesRangeParam, "5%", "t_tem_max");
//			break;
//		case MWS:
//			dbTable.queryDataByRangeTimes(timesRangeParam, "A%", "t_tem_max");
//			break;
//		default:
//			dbTable.queryDataByRangeTimes(timesRangeParam, "%", "t_tem_max");
//			break;
//		}
		String station_Id_CArrays = "";
		for(int i = 0; i < listStations.size(); i++) {
			station_Id_CArrays += listStations.get(i).getStation_Id_C();
			if(i != listStations.size() - 1) {
				station_Id_CArrays += ",";
			}
		}
		dbTable.queryDataByRangeTimes(timesRangeParam, station_Id_CArrays, "t_tem_max");
		
		List<SequenceTimeValue> sequenceTemAvgValueList = dbTable.getSequenceTimeValueList();
		for(int k = 0; k < listStations.size(); k++) {
			String itemStation_Id_C = listStations.get(k).getStation_Id_C();
			boolean flag = false;
			for(int i = 0; i < sequenceTemAvgValueList.size(); i++) {
				HighTmpSequence highTmpSequence = new HighTmpSequence();
				SequenceTimeValue sequenceTimeValue = sequenceTemAvgValueList.get(i);
				String station_Id_C = sequenceTimeValue.getStation_Id_C();
				if(itemStation_Id_C.equals(station_Id_C)) {
					highTmpSequence.setStation_Id_C(station_Id_C);
					highTmpSequence.setStation_Name(listStations.get(k).getStation_Name());
					LinkedHashMap valueMap = new LinkedHashMap();
					List<TimeValue> timeValues = sequenceTimeValue.getTimeValues();
					for(int j = 0; j < timeValues.size(); j++) {
						TimeValue timeValue = timeValues.get(j);
						Date date = timeValue.getDate();
						String str = mmdd.format(date);
						Double value = timeValue.getValue();
						valueMap.put(str, value);
					}
					highTmpSequence.setValueMap(valueMap);
					highTmpSequenceList.add(highTmpSequence);
					flag = true;
					break;
				}
			}
			if(!flag) {
				HighTmpSequence highTmpSequence = new HighTmpSequence();
				highTmpSequence.setStation_Id_C(listStations.get(k).getStation_Id_C());
				highTmpSequence.setStation_Name(listStations.get(k).getStation_Name());
				LinkedHashMap valueMap = new LinkedHashMap();
				for(long time = timesParam.getStartDate().getTime(); time <= timesParam.getEndDate().getTime(); time += CommonConstant.DAYTIMES) {
					valueMap.put(mmdd.format(new Date(time)), 0);
				}
				highTmpSequence.setValueMap(valueMap);
				highTmpSequenceList.add(highTmpSequence);
			}
		}
		highTmpDaysResult.setHighTmpSequenceList(highTmpSequenceList);
		return highTmpDaysResult;
	}

	  public Object highTmpByRange(HighTmpRangeParam highTmpRangeParam) {
//		  StationArea stationArea = new StationArea();
//		  Map<String, String> stationMap = stationArea.getStationAreaMap();
		  double level1HighTmp = highTmpRangeParam.getLevel1HighTmp();
		  double level2HighTmp = highTmpRangeParam.getLevel2HighTmp();
		  double level3HighTmp = highTmpRangeParam.getLevel3HighTmp();
		  TimesParam timesParam = highTmpRangeParam.getTimesParam();
		  String startTime = timesParam.getStartTimeStr();
		  String endTime = timesParam.getEndTimeStr();
		  String[] stations = highTmpRangeParam.getStations();
		  HashMap paramMap = new HashMap();
		  paramMap.put("startTime", startTime);
		  paramMap.put("endTime", endTime);
		  paramMap.put("level1HighTmp", level1HighTmp);
		  List<String> station_Id_Cs = new ArrayList<String>();
		  for(int i = 0; i < stations.length; i++) {
			  station_Id_Cs.add(stations[i]);
		  }
		  
		  //增加替代站处理
		  ICommon common = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		  HashMap commonParamMap = new HashMap();
		  commonParamMap.put("Station_Id_C", station_Id_Cs);
		  List<LinkedHashMap> contrastStationResult = common.queryContrastByStation_Id_C(commonParamMap);
		  for(int i = 0; i < contrastStationResult.size(); i++) {
				LinkedHashMap itemMap = contrastStationResult.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				station_Id_Cs.add(station_Id_C);
		  }
		  paramMap.put("Station_Id_Cs", station_Id_Cs);
		  
		  IHighTmp highTmp = (IHighTmp)ContextLoader.getCurrentWebApplicationContext().getBean("HighTmpImpl");
		  List<LinkedHashMap> resultList = highTmp.queryHighTmpByRange(paramMap);
		  HighTmpResult highTmpResult = new HighTmpResult();
		  List<HighTmpRangeResultSequence> hghTmpRangeResultSequenceList = new ArrayList<HighTmpRangeResultSequence>();
		  //逐次
		  for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			String country = (String) itemMap.get("Country");
			String datetime = (String) itemMap.get("datetime");
			Double tem_Max = (Double) itemMap.get("TEM_Max");
			HighTmpRangeResultSequence highTmpRangeResultSequence = new HighTmpRangeResultSequence();
			highTmpRangeResultSequence.setDatetime(datetime);
			highTmpRangeResultSequence.setStation_Id_C(station_Id_C);
			highTmpRangeResultSequence.setStation_Name(station_Name);
			highTmpRangeResultSequence.setTem_Max(tem_Max);
			highTmpRangeResultSequence.setCountry(country);
			if(tem_Max >= level1HighTmp && tem_Max < level2HighTmp) {
				highTmpRangeResultSequence.setLevel("一般");
			} else if(tem_Max >= level2HighTmp && tem_Max < level3HighTmp) {
				highTmpRangeResultSequence.setLevel("中等");
			} else if(tem_Max >= level3HighTmp) {
				highTmpRangeResultSequence.setLevel("严重");
			}
			
			hghTmpRangeResultSequenceList.add(highTmpRangeResultSequence);
		  }
		  highTmpResult.setHighTmpRangeResultSequenceList(hghTmpRangeResultSequenceList);
		  //合计
		  LinkedHashMap<String, List<LinkedHashMap>> map = new LinkedHashMap<String, List<LinkedHashMap>>();
		  for(int i = 0; i < resultList.size(); i++) {
				LinkedHashMap itemMap = resultList.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				List<LinkedHashMap> itemList = map.get(station_Id_C);
				if(itemList == null || itemList.size() == 0) {
					itemList = new ArrayList<LinkedHashMap>();
				}
				itemList.add(itemMap);
				map.put(station_Id_C, itemList);
		  }
		  List<HighTmpRangeResult> highTmpRangeResultList = new ArrayList<HighTmpRangeResult>();
		  Set<String> set = map.keySet();
		  Iterator<String> it = set.iterator();
		  while(it.hasNext()) {
			  String key = it.next();
			  List<LinkedHashMap> itemList = map.get(key);
			  if(itemList == null || itemList.size() == 0) {
				  continue;
			  }
			  HighTmpRangeResult highTmpRangeResult = new HighTmpRangeResult();
			  int level1Cnt = 0, level2Cnt = 0, level3Cnt = 0, totalCnt = itemList.size();
			  double extHighTmp = 0;
			  String extHighTmpTime = "";
			  for(int i = 0; i < itemList.size(); i++) {
				  LinkedHashMap itemMap = itemList.get(i);
				  String station_Id_C = (String) itemMap.get("Station_Id_C");
				  String station_Name = (String) itemMap.get("Station_Name");
				  String area = (String) itemMap.get("area");
				  highTmpRangeResult.setStation_Id_C(station_Id_C);
				  highTmpRangeResult.setStation_Name(station_Name);
				  highTmpRangeResult.setArea(area);
				  Double tem_Max = (Double) itemMap.get("TEM_Max");
				  if(tem_Max > extHighTmp) {
					  extHighTmp = tem_Max;
				  }
				  if (tem_Max >= level1HighTmp && tem_Max < level2HighTmp) {
					  level1Cnt++;
				  } else if (tem_Max >= level2HighTmp && tem_Max < level3HighTmp) {
					  level2Cnt++;
				  } else if (tem_Max >= level3HighTmp) {
					  level3Cnt++;
				  }
			  }
			  for(int i = 0; i < itemList.size(); i++) {
				  LinkedHashMap itemMap = itemList.get(i);
				  Double tem_Max = (Double) itemMap.get("TEM_Max");
				  String datetime = (String) itemMap.get("datetime");
				  if(tem_Max == extHighTmp) {
					  extHighTmpTime += datetime;
					  extHighTmpTime += " ";
				  }
			  }
			  highTmpRangeResult.setLevel1Cnt(level1Cnt);
			  highTmpRangeResult.setLevel2Cnt(level2Cnt);
			  highTmpRangeResult.setLevel3Cnt(level3Cnt);
			  highTmpRangeResult.setTotalCnt(totalCnt);
			  highTmpRangeResult.setExtHighTmp(extHighTmp);
			  highTmpRangeResult.setExtHighTmpTime(extHighTmpTime.trim());
			  
			  highTmpRangeResultList.add(highTmpRangeResult);
		  }
		  highTmpResult.setHighTmpRangeResultList(highTmpRangeResultList);
		  return highTmpResult;
	  }
	  
	  /**
	   * 高温历年统计
	   * @param highTmpYearsParam
	   * @return
	   */
	  public List<HighTmpYearsResult> highTmpByYears(HighTmpYearsParam highTmpYearsParam) {
		  //添加替代站
		  String[] stations = highTmpYearsParam.getStation_Id_Cs();
		  List<String> station_Id_Cs = new ArrayList<String>();
		  for(int i = 0; i < stations.length; i++) {
			  station_Id_Cs.add(stations[i]);
		  }
		  ICommon common = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		  HashMap commonParamMap = new HashMap();
		  commonParamMap.put("Station_Id_C", station_Id_Cs);
		  List<LinkedHashMap> contrastStationResult = common.queryContrastByStation_Id_C(commonParamMap);
		  for(int i = 0; i < contrastStationResult.size(); i++) {
				LinkedHashMap itemMap = contrastStationResult.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				station_Id_Cs.add(station_Id_C);
		  }
		  stations = new String[station_Id_Cs.size()];
		  for(int i = 0; i < station_Id_Cs.size(); i++) {
			  stations[i] = station_Id_Cs.get(i);
		  }
		  highTmpYearsParam.setStation_Id_Cs(stations);
		  //暂时不考虑跨年的问题
		  List<HighTmpYearsResult> highTmpYearResultList = new ArrayList<HighTmpYearsResult>();
		  //历年的结果
		  List<HighTmpYearsResult> overList = getHighTmpRange(highTmpYearsParam);
		  //常年结果
		  TimesParam timesParam = highTmpYearsParam.getTimesParam();
		  timesParam.setStartTimeStr(highTmpYearsParam.getStartYear() + "-" + timesParam.getStartTimeStr().substring(5));
		  timesParam.setEndTimeStr(highTmpYearsParam.getEndYear() + "-" + timesParam.getEndTimeStr().substring(5));
		  HighTmpYearsParam perennialHighTmpYearsParam = highTmpYearsParam.copy();
		  perennialHighTmpYearsParam.setStartYear(highTmpYearsParam.getPerennialStartYear());
		  perennialHighTmpYearsParam.setEndYear(highTmpYearsParam.getPerennialEndYear());
		  List<HighTmpYearsResult> perennialList = getHighTmpRange(perennialHighTmpYearsParam);
		  //对比常年和当年的结果
		//計算常年結果
		  int totalCnt = 0, level1Cnt = 0, level2Cnt = 0;
		  double level3Cnt = 0;
		  for(int i = 0; i < perennialList.size(); i++) {
			  HighTmpYearsResult highTmpYearsResult = perennialList.get(i);
			  totalCnt += highTmpYearsResult.getYearsTotalCnt();
			  level1Cnt += highTmpYearsResult.getYearsLevel1Cnt();
			  level2Cnt += highTmpYearsResult.getYearsLevel2Cnt();
			  level3Cnt += highTmpYearsResult.getYearsLevel3Cnt();
		  }
		  double totalCntDouble = 0, level1CntDouble = 0, level2CntDouble = 0, level3CntDouble = 0;
		  int perennialYears = highTmpYearsParam.getPerennialEndYear() - highTmpYearsParam.getPerennialStartYear() + 1;
		  totalCntDouble = CommonTool.roundDouble(totalCnt / perennialYears);
		  level1CntDouble = CommonTool.roundDouble(level1Cnt / perennialYears);
		  level2CntDouble = CommonTool.roundDouble(level2Cnt / perennialYears);
		  level3CntDouble = CommonTool.roundDouble(level3Cnt / perennialYears);
		  
		  for(int i = highTmpYearsParam.getStartYear(); i <= highTmpYearsParam.getEndYear(); i++) {
			  boolean flag = false; // 结果中是否包含该年份
			  for(int j = 0; j < overList.size(); j++) {
				  HighTmpYearsResult overHighTmpYearsResult = overList.get(j);
				  if(overHighTmpYearsResult.getYear() == i) {
//					  overHighTmpYearsResult.setYearsLevel1Cnt(level1CntDouble);
//					  overHighTmpYearsResult.setYearsLevel2Cnt(level2CntDouble);
//					  overHighTmpYearsResult.setYearsLevel3Cnt(level3CntDouble);
//					  overHighTmpYearsResult.setYearsTotalCnt(totalCntDouble);
					  double overLevel1Cnt = overHighTmpYearsResult.getYearsLevel1Cnt();
					  overHighTmpYearsResult.setLevel1Cnt(overLevel1Cnt);
					  double overLevel2Cnt = overHighTmpYearsResult.getYearsLevel2Cnt();
					  overHighTmpYearsResult.setLevel2Cnt(overLevel2Cnt);
					  double overLevel3Cnt = overHighTmpYearsResult.getYearsLevel3Cnt();
					  overHighTmpYearsResult.setLevel3Cnt(overLevel3Cnt);
					  double overTotalCnt = overHighTmpYearsResult.getYearsTotalCnt();
					  overHighTmpYearsResult.setTotalCnt(overTotalCnt);
					  overHighTmpYearsResult.setYearsLevel1Cnt(level1CntDouble);
					  overHighTmpYearsResult.setYearsLevel2Cnt(level2CntDouble);
					  overHighTmpYearsResult.setYearsLevel3Cnt(level3CntDouble);
					  overHighTmpYearsResult.setYearsTotalCnt(totalCntDouble);
					  if(overLevel1Cnt != 0) {
						  overHighTmpYearsResult.setLevel1CntAnomaly(CommonTool.roundDouble((overLevel1Cnt - level1CntDouble) / overLevel1Cnt * 100));
					  }
					  if(overLevel2Cnt != 0) {
						  overHighTmpYearsResult.setLevel2CntAnomaly(CommonTool.roundDouble((overLevel2Cnt - level2CntDouble) / overLevel2Cnt * 100));
					  }
					  if(overLevel3Cnt != 0) {
						  overHighTmpYearsResult.setLevel3CntAnomaly(CommonTool.roundDouble((overLevel3Cnt - level2CntDouble) / overLevel3Cnt * 100));
					  }
					  if(overTotalCnt != 0) {
						  overHighTmpYearsResult.setTotalCntAnomaly(CommonTool.roundDouble((overTotalCnt - totalCntDouble) / overTotalCnt * 100));
					  }
					  highTmpYearResultList.add(overHighTmpYearsResult);
					  flag = true;
				  }
			  }
			  if(!flag) {
				  HighTmpYearsResult overHighTmpYearsResult = new HighTmpYearsResult();
				  overHighTmpYearsResult.setYear(i);
				  overHighTmpYearsResult.setYearsLevel1Cnt(level1CntDouble);
				  overHighTmpYearsResult.setYearsLevel2Cnt(level2CntDouble);
				  overHighTmpYearsResult.setYearsLevel3Cnt(level3CntDouble);
				  overHighTmpYearsResult.setYearsTotalCnt(totalCntDouble);
				  highTmpYearResultList.add(overHighTmpYearsResult);
			  }
		  }
		  return highTmpYearResultList;
	  }
	  
	  /**
	   * 按年份查询
	   * @param highTmpYearsParam
	   * @return
	   */
	  private List<HighTmpYearsResult> getHighTmpRange(HighTmpYearsParam highTmpYearsParam) {
		TimesParam timesParam = highTmpYearsParam.getTimesParam();
		Double level1HighTmp = highTmpYearsParam.getLevel1HighTmp();
		Double level2HighTmp = highTmpYearsParam.getLevel2HighTmp();
		Double level3HighTmp = highTmpYearsParam.getLevel3HighTmp();
		Date startDate = null, endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = sdf.parse(timesParam.getStartTimeStr());
			endDate = sdf.parse(timesParam.getEndTimeStr());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		HashMap yearParamMap = new HashMap();
		yearParamMap.put("startTime", Integer.parseInt(mmddSDF
				.format(startDate)));
		yearParamMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
		yearParamMap.put("level1HighTmp", level1HighTmp);
		String[] stations = highTmpYearsParam.getStation_Id_Cs();
		List<String> station_Id_Cs = new ArrayList<String>();
		for (int i = 0; i < stations.length; i++) {
			station_Id_Cs.add(stations[i]);
		}
		yearParamMap.put("Station_Id_Cs", station_Id_Cs);
		yearParamMap.put("startYear", highTmpYearsParam.getStartYear());
		yearParamMap.put("endYear", highTmpYearsParam.getEndYear());
		IHighTmp highTmp = (IHighTmp) ContextLoader
				.getCurrentWebApplicationContext().getBean("HighTmpImpl");
		List<LinkedHashMap> yearResultList = highTmp
				.queryHighTmpByYears(yearParamMap);
		Map<Integer, List<LinkedHashMap>> yearsMap = new HashMap<Integer, List<LinkedHashMap>>();
		for(int i = 0; i < yearResultList.size(); i++) {
			LinkedHashMap itemMap = yearResultList.get(i);
			Integer year = (Integer) itemMap.get("year");
			List<LinkedHashMap> yearsLinkedMap = yearsMap.get(year);
			if(yearsLinkedMap == null) {
				yearsLinkedMap = new ArrayList<LinkedHashMap>();
			}
			yearsLinkedMap.add(itemMap);
			yearsMap.put(year, yearsLinkedMap);
		}
		List<HighTmpYearsResult> highTmpYearResultList = new ArrayList<HighTmpYearsResult>();
		for(int j = highTmpYearsParam.getStartYear(); j <= highTmpYearsParam.getEndYear(); j++) {
			HighTmpYearsResult highTmpYearsResult = new HighTmpYearsResult();
			int yearLevel1Cnt = 0, yearLevel2Cnt = 0, yearLevel3Cnt = 0;
			highTmpYearsResult.setYear(j);
			List<LinkedHashMap> yearList = yearsMap.get(j);
			if(yearList == null) {
				continue;
			}
			for(int i = 0; i < yearList.size(); i++) {
				LinkedHashMap itemMap = yearList.get(i);
				Double tem_Max = (Double) itemMap.get("TEM_Max");
				if(tem_Max >= level1HighTmp && tem_Max < level2HighTmp) {
					yearLevel1Cnt++;
				} else if(tem_Max >= level2HighTmp && tem_Max < level3HighTmp) {
					yearLevel2Cnt++;
				} else if(tem_Max >= level3HighTmp) {
					yearLevel3Cnt++;
				}
			}
			highTmpYearsResult.setYearsLevel1Cnt(yearLevel1Cnt);
			highTmpYearsResult.setYearsLevel2Cnt(yearLevel2Cnt);
			highTmpYearsResult.setYearsLevel3Cnt(yearLevel3Cnt);
			highTmpYearsResult.setYearsTotalCnt(yearList.size());
			highTmpYearResultList.add(highTmpYearsResult);
		}
		return highTmpYearResultList;
	  }
	  
	  /**
	   * 查询高温对应的国家站、城市站。
	   * @return
	   */
	  private List<Station> getHighTmpStation() {
		  if(resultStations.size() > 0) {
			  return resultStations;
		  }
		  ICommon common = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		  HashMap commonParamMap = new HashMap();
		  commonParamMap.put("ZoomLevel", 1);
		  //自动站
		  List<Station> listStation = common.getStationsByLevel(commonParamMap);
		  //对比展
		  HashMap contrastParamMap = new HashMap();
		  List<LinkedHashMap> contrastStationList = common.getAllContrastStations(contrastParamMap);
		  for(int i = 0; i < listStation.size(); i++) {
			  Station itemStation = listStation.get(i);
			  String station_Id_C = itemStation.getStation_Id_C();
			  resultStations.add(itemStation);
			  for(int j = 0; j < contrastStationList.size(); j++) {
				  LinkedHashMap itemMap = contrastStationList.get(j);
				  String contrastStation_Id_C = (String) itemMap.get("ContrastStation_Id_C");
				  if(contrastStation_Id_C.equals(station_Id_C)) {
					  Station station = new Station();
					  station.setStation_Id_C((String) itemMap.get("Station_Id_C"));
					  station.setStation_Name((String) itemMap.get("Station_Name"));
					  station.setAlti((Double) itemMap.get("Alti"));
					  station.setAreaCode((String) itemMap.get("AreaCode"));
					  station.setCountry((String) itemMap.get("Country"));
					  resultStations.add(station);
					  break;
				  }
			  }
		  }
		  return resultStations;
	  }
}
