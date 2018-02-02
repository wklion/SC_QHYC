package com.spd.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.FilterTypes;
import com.spd.common.MaxWindRangeResult;
import com.spd.common.MaxWindRangeResultSequence;
import com.spd.common.RankParam;
import com.spd.common.StatisticsTypes;
import com.spd.pojo.ItemCommon;
import com.spd.pojo.RankResult;
import com.spd.pojo.StationYearValue;
import com.spd.service.ICommon;
import com.spd.service.IRank;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.util.CommonUtil;

/**
 * 位次分析处理逻辑
 * @author Administrator
 *
 */
public class RankBus {

	private CommonUtil commonUtil = CommonUtil.getInstance();
	
	public Object rank(RankParam rankParam) {
		// 1. 构造查询的字段
		String items = createItems(rankParam);
		String stationType = rankParam.getStationType();
		// 2. 做查询。
		IRank rank = (IRank)ContextLoader.getCurrentWebApplicationContext().getBean("RankImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", rankParam.getTableName());
		paramMap.put("items", items);
		paramMap.put("startYear", rankParam.getStartYear());
		paramMap.put("endYear", rankParam.getEndYear());
		paramMap.put("currentYear", rankParam.getCurrentYear());
		if("AWS".equals(stationType)) {
			paramMap.put("StationType", stationType);
		}
		// 需要处理跨年你的问题，冬季的情况。比如2015年的冬季，期间就是：2015.12.01到2016.02.29
		boolean isSpanYears = isSpanYears(rankParam);
		List<Map> resultList = null;
		if(isSpanYears) {
			paramMap.put("startYear", rankParam.getStartYear() - 1);
			paramMap.put("lastCurrentYear", rankParam.getCurrentYear() - 1);
			resultList = rank.queryEleOverYear(paramMap);
		} else {
			resultList = rank.queryEle(paramMap);
		}
		//3. 处理查询结果
		Object result = null;
		if(isSpanYears) {
			result = disposeResultSpanYear(resultList, rankParam, items.split(",").length);
		} else {
			result = disposeResult(resultList, rankParam, items.split(",").length);
		}
		List<RankResult> listResult = (List<RankResult>)result;
		
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = listResult.size() - 1; i >= 0; i--) {
				RankResult rankResult = listResult.get(i);
				String station_Id_C = rankResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					listResult.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					listResult.remove(i);
				}
			}
		}
		//重新排列序号
		for(int i = 0; i < listResult.size(); i++) {
			RankResult rankResult = listResult.get(i);
			rankResult.setIndex(i + 1);
		}
		return listResult;
	}
	
	/**
	 * 判断是否是跨年
	 * @return
	 */
	private boolean isSpanYears(RankParam rankParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int startMon = rankParam.getStartMon();
		int endMon = rankParam.getEndMon();
		int startDay = rankParam.getStartDay();
		int endDay = rankParam.getEndDay();
		String startStr = "2000-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay) + " 00:00:00";
		String endStr = "2000-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay) + " 00:00:00";
		try {
			long startTime = sdf.parse(startStr).getTime();
			long endTime = sdf.parse(endStr).getTime();
			if(startTime > endTime) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Object disposeResultSpanYear(List<Map> resultList, RankParam rankParam, int itemLength) {
		String tableName = rankParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, StationYearValue> stationYearValueMap = new HashMap<String, StationYearValue>();
		Map<String, StationYearValue> stationYearValueMap2 = new HashMap<String, StationYearValue>();
//		Map<String, StationYearValue> stationLartYearValueMap = new HashMap<String, StationYearValue>();
		for(Map map : resultList) {
			boolean missing = missingObservData(map, rankParam, itemLength);
			if(missing) {
				continue;
			}
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer)map.get("year");
			//数据只会是属于当年,或者是下一年
			StationYearValue stationYearValue = stationYearValueMap.get(station_Id_C + "_" + (year));
			StationYearValue stationLartYearValue = stationYearValueMap.get(station_Id_C + "_" + (year + 1));
			if(stationYearValue == null) {
				stationYearValue = new StationYearValue();
				stationYearValue.setStation_Id_C(station_Id_C);
				stationYearValue.setYear(year);
				stationYearValue.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				stationYearValueMap.put(station_Id_C + "_" + (year), stationYearValue);
			}
			if(stationLartYearValue == null) {
				stationLartYearValue = new StationYearValue();
				stationLartYearValue.setStation_Id_C(station_Id_C);
				stationLartYearValue.setYear(year + 1);
				stationLartYearValue.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				stationYearValueMap.put(station_Id_C + "_" + (year + 1), stationLartYearValue);
//				stationLartYearValueMap.put(station_Id_C + "_" + year, stationLartYearValue);
			}
			Set set = map.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					boolean isFilter = filter(rankParam, value, tableName);
					if(isFilter) {
						continue;
					}
					//引用传递
					int startMon = rankParam.getStartMon();
					String currentMon = key.substring(1, 3);
					if(Integer.parseInt(currentMon) >= startMon) {
						statistics(rankParam, year + 1 , stationLartYearValue, value, key, tableName);
					} else {
						statistics(rankParam, year, stationYearValue, value, key, tableName);
					}
				}
			}
		}
		// 把stationYearValueMap中边缘的年份数据删除掉。
		Set<String> set = stationYearValueMap.keySet();
		//有效的年份
		Set<Integer> validYearSet = new HashSet<Integer>();
		int startYear = rankParam.getStartYear();
		int endYear = rankParam.getEndYear();
		for(int i = startYear; i<=endYear; i++) {
			validYearSet.add(i);
		}
		validYearSet.add(rankParam.getCurrentYear());
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			StationYearValue stationYearValue = stationYearValueMap.get(key);
			Integer keyYear = Integer.parseInt(key.split("_")[1]);
			if(validYearSet.contains(keyYear)) {
				stationYearValueMap2.put(key, stationYearValue);
			}
		}
		//过滤
		disposeResult(rankParam, stationYearValueMap2);
		Map<String, List<StationYearValue>> mapSortResult = sortResult(stationYearValueMap2, rankParam.getCurrentYear(), rankParam.getSortType());
		List<RankResult> rankResultList = analystRankResult(mapSortResult, rankParam.getCurrentYear(), rankParam.isTie());
		return rankResultList;
	}
	
	/**
	 * 处理缺测数据
	 * @return 返回 true 表示该数据不符合要求，应该舍弃，为 false 表示该数据可以继续使用。
	 */
	private boolean missingObservData(Map map, RankParam rankParam, int itemLength) {
		String tableName = rankParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		double missingRatio = rankParam.getMissingRatio();
		int validCnt = 0;
		if(missingRatio == 0) { // 不处理缺测
			return false;
		}
		Set set = map.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			if(key.matches("m\\d{2}d\\d{2}")) {
				//具体值
				Double value = null;
				Object objValue = map.get(key);
				if("BigDecimal".equals(columnType) && objValue != null) {
					value = ((BigDecimal)objValue).doubleValue();
				} else {
					value = (Double) map.get(key);
				}
				value = Eigenvalue.dispose(value);
				if(value != null) {
					validCnt ++;
				}
//				if(value > CommonConstant.MININVALID && value < CommonConstant.MAXINVALID && value != null) {
//					//表示缺测
//					validCnt ++;
//				} 
			}
		}
		if(validCnt / (itemLength + 0.0) <= missingRatio) {
			return true;
		}
		return false;
	}
	
	private Object disposeResult(List<Map> resultList, RankParam rankParam, int itemLength) {
		String tableName = rankParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		// 遍历计算
		Map<String, StationYearValue> stationYearValueMap = new HashMap<String, StationYearValue>();
		for(Map map : resultList) {
			boolean missing = missingObservData(map, rankParam, itemLength);
			if(missing) {
				continue;
			}
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer)map.get("year");
			StationYearValue stationYearValue = stationYearValueMap.get(station_Id_C + "_" + year);
			if(stationYearValue == null) {
				stationYearValue = new StationYearValue();
				stationYearValue.setStation_Id_C(station_Id_C);
				stationYearValue.setYear(year);
				stationYearValue.setStation_Name(station_Name);
				stationYearValueMap.put(station_Id_C + "_" + year, stationYearValue);
			}
			Set set = map.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					boolean isFilter = filter(rankParam, value, tableName);
					if(isFilter) {
						continue;
					}
					//引用传递
					statistics(rankParam, year, stationYearValue, value, key, tableName);
				}
			}
		}
		
		disposeResult(rankParam, stationYearValueMap);
		Map<String, List<StationYearValue>> mapSortResult = sortResult(stationYearValueMap, rankParam.getCurrentYear(), rankParam.getSortType());
		List<RankResult> rankResultList = analystRankResult(mapSortResult, rankParam.getCurrentYear(), rankParam.isTie());
		return rankResultList;
	}

	/*
	 * 简单判断，如果跨年是1951-12-01 到 1952-02-29 ,那么当前月份如果大于等于开始月份，则是当年，否则，算到头一年里
	 */
	private int getYearBelong(RankParam rankParam, String key, int year) {
		int startMon = rankParam.getStartMon();
		String currentMon = key.substring(1, 3);
		if(Integer.parseInt(currentMon) >= startMon) {
			return year;
		} else {
			return year - 1;
		}
	}
	/**
	 * 组装结果
	 * @param mapSortResult
	 * @return
	 */
	private List<RankResult> analystRankResult(Map<String, List<StationYearValue>> mapSortResult, int currentYear, boolean isTie) {
		List<RankResult> resultRankResultList = new ArrayList<RankResult>();
		Set<String> set = mapSortResult.keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			List<StationYearValue> listStationYearValueList = mapSortResult.get(key);
			RankResult rankResult = new RankResult();
			Double priValue = 0.0; //记录上一次的值,用于在并列排序的时候使用
			for(int i=0; i<listStationYearValueList.size(); i++) {
				StationYearValue stationYearValue = listStationYearValueList.get(i);
				String station_Id_C = stationYearValue.getStation_Id_C();
				if(i == 0) {
					// 第一条为极值数据
					Double value = stationYearValue.getValue();
					priValue = value;
					rankResult.setStation_Id_C(stationYearValue.getStation_Id_C());
					rankResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(stationYearValue.getStation_Id_C()));
					if(value != null && !value.isNaN()) {
						rankResult.setExtValue(CommonTool.roundDouble(value));
						rankResult.setExtYears(stationYearValue.getYear());
						rankResult.setExtDateStr(stationYearValue.getExtDay());
					} else {
						rankResult.setExtValue(null);
						rankResult.setExtYears(null);
					}
					
				}
				int year = stationYearValue.getYear();
				if(currentYear == year) {
					Double value = stationYearValue.getValue();
					if(value == null) {
						rankResult.setYearRanking(null);
						rankResult.setYearValue(null);
					} else if(!value.isNaN()) {
						double temp = CommonTool.roundDouble(value);
						rankResult.setYearValue(temp);
						rankResult.setYearRanking(i + 1);
//						if(priValue == temp && isTie) {
//							rankResult.setYearRanking(i);
//						} else {
//							rankResult.setYearRanking(i + 1);
//						}
					} else {
						rankResult.setYearRanking(null);
						rankResult.setYearValue(null);
					}
					resultRankResultList.add(rankResult);
					priValue = value;
					break;
				}
			}
			if(isTie) {
				LinkedHashSet valueSet = new LinkedHashSet();
				for(int i=0; i<listStationYearValueList.size(); i++) {
					StationYearValue stationYearValue = listStationYearValueList.get(i);
					Double value = stationYearValue.getValue();
					valueSet.add(value);
				}
				Double yearValue = 0.0;
				for(int i=0; i<listStationYearValueList.size(); i++) {
					StationYearValue stationYearValue = listStationYearValueList.get(i);
					Double value = stationYearValue.getValue();
					int year = stationYearValue.getYear();
					if(year == currentYear) {
						yearValue = value;
					}
				}
				Iterator valueIt = valueSet.iterator();
				int index = 0;
				while(valueIt.hasNext()) {
					index++;
					Double value = (Double) valueIt.next();
					if(value == yearValue) {
						rankResult.setYearRanking(index);
					}
				}
			}
		}
		
		// 添加上序号
		int i = 1;
		for(RankResult rankResult : resultRankResultList) {
//			rankResult.setArea(stationAreaMap.get(rankResult.getStation_Id_C()));
			String station_Id_C = rankResult.getStation_Id_C();
			rankResult.setArea(commonUtil.stationAreaMap.get(station_Id_C));
			rankResult.setIndex(i ++);
		}
		return resultRankResultList;
	}
	
	/**
	 * 对结果进行处理，输入数据中，求平均，求和等都是一个和值
	 * @param stationYearValueMap
	 */
	private void disposeResult(RankParam rankParam, Map<String, StationYearValue> stationYearValueMap) {
		String statistics = rankParam.getStatisticsType();
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(statistics);
		Set<String> stationYearValueMapSet = stationYearValueMap.keySet();
		Iterator<String>  stationYearValueMapIt = stationYearValueMapSet.iterator();
		while(stationYearValueMapIt.hasNext()) {
			String key = stationYearValueMapIt.next();
			String station_Id_C = key.split("_")[0];
			int year = Integer.parseInt(key.split("_")[1]);
			stationYearValueMap.get(key);
			StationYearValue stationYearValue = stationYearValueMap.get(key);
			Double value = stationYearValue.getValue();
			if(value == null) {
				continue;
			}
			int days = stationYearValue.getDays();
			switch(statisticsTypes) {
			case SUM:
				break;
			case AVG:
				stationYearValue.setValue(value / days); 
				break;
			case MAX:
				break;
			case MIN:
				break;
			case DAYS:
				break;
			default:
				break;
			}
		}
		
	}
	
	/**
	 * 排位操作
	 * @param stationYearValueMap
	 * @return
	 */
	private Map<String, List<StationYearValue>> sortResult(Map<String, StationYearValue> stationYearValueMap, int currentYear, String sortType) {
		Map<String, List<StationYearValue>> resultMap = new HashMap<String, List<StationYearValue>>();
		Map<String, List<StationYearValue>> resultMap2 = new HashMap<String, List<StationYearValue>>();
		Set<String> set = stationYearValueMap.keySet();
		Iterator<String> it = set.iterator();
		// 构造Map对象，Key为Station，value 为StationYearValue数组，方便下面的排序
		while(it.hasNext()) {
			String stationYear = it.next();
			String stationIdC = stationYear.split("_")[0];
			List<StationYearValue> stationYearValueList = resultMap.get(stationIdC);
			if(stationYearValueList == null) {
				stationYearValueList = new ArrayList<StationYearValue>();
			}
			stationYearValueList.add(stationYearValueMap.get(stationYear));
			resultMap.put(stationIdC, stationYearValueList);
		}
		// 排序
		Set<String> resultSet = resultMap.keySet();
		Iterator<String> resultIt = resultSet.iterator();
		while(resultIt.hasNext()) {
			String key = resultIt.next();
			List<StationYearValue> stationYearValueList = resultMap.get(key);
			List<StationYearValue> tempList = new ArrayList<StationYearValue>();
			
			double tempValue = CommonConstant.MININVALID;
			int markerIndex = 0;
			for(int j=stationYearValueList.size()-1; j>=0; j--) {
				for(int i=0; i<stationYearValueList.size(); i++) {
					StationYearValue stationYearValue = stationYearValueList.get(i);
					Double value = stationYearValue.getValue();
					if(value == null) {
						continue;
					}
					if(i == 0) {
						tempValue = stationYearValue.getValue();
						markerIndex = 0;
					}
					if(sortType.equals("HIGH")) {
						// 高位排在前
						if(value >= tempValue) {
							tempValue = value;
							markerIndex = i;
						}
					} else if(sortType.equals("LOW")) {
						// 低位排在前
						if(value <= tempValue) {
							tempValue = value;
							markerIndex = i;
						}
					}
				}
				try {
					tempList.add(stationYearValueList.get(markerIndex));
				} catch (Exception e) {
					System.out.println("error");
				}
				stationYearValueList.remove(markerIndex);
				markerIndex = 0;
				tempValue = CommonConstant.MININVALID;
			}
			resultMap2.put(key, tempList);
		}
		return resultMap2;
	}
	
	/**
	 * 按年， 站 来进行划分
	 * @param rankParam
	 * @param rankResult
	 * @param year
	 * @return
	 */
	private void statistics(RankParam rankParam, int year, StationYearValue stationYearValue, double mValue, String day, String tableName) {
//		System.out.println("year:" + year);
//		if("t_pre_time_2020".equalsIgnoreCase(tableName) || "t_pre_time_0820".equalsIgnoreCase(tableName)
//				||"t_pre_time_2008".equalsIgnoreCase(tableName) || "t_pre_time_0808".equalsIgnoreCase(tableName)) {
//			mValue = Eigenvalue.dispose(mValue);
//		}
		mValue = Eigenvalue.dispose(mValue);
		day = day.substring(1, 3) + "-" + day.substring(4, 6);
		stationYearValue.setYear(year);
		stationYearValue.setDays(stationYearValue.getDays() + 1);
		Double value = stationYearValue.getValue();
		String statistics = rankParam.getStatisticsType();
		StatisticsTypes statisticsTypes = StatisticsTypes.getStatisticsTypeName(statistics);
		
		switch(statisticsTypes) {
		case SUM:
		case AVG:
			if(value == null) {
				stationYearValue.setValue(mValue);
			} else {
				stationYearValue.setValue(value + mValue); 
			}
			break;
		case MAX:
			if(value == null) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue > value ) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue == value) {
				stationYearValue.setExtDay(day + "," + stationYearValue.getExtDay());
			}
			break;
		case MIN:
			if(value == null) {
				stationYearValue.setValue(mValue); 
				stationYearValue.setExtDay(day);
			} else if(mValue < value) {
				stationYearValue.setValue(mValue);
				stationYearValue.setExtDay(day);
			} else if(mValue == value) {
				stationYearValue.setExtDay(day + "," + stationYearValue.getExtDay());
			}
			break;
		case DAYS:
			if(value == null) {
				stationYearValue.setValue(1.0);
			} else {
				stationYearValue.setValue(value + 1);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 过滤值
	 * @param rankParam
	 * @param value
	 * @return false表示不过滤， true表示该值被过滤掉
	 */
	private boolean filter(RankParam rankParam, Double value, String tableName){
		boolean flag = false;
//		if("t_pre_time_2020".equalsIgnoreCase(tableName) || "t_pre_time_0820".equalsIgnoreCase(tableName)
//				||"t_pre_time_2008".equalsIgnoreCase(tableName) || "t_pre_time_0808".equalsIgnoreCase(tableName)) {
//			value = Eigenvalue.dispose(value);
//		}
		value = Eigenvalue.dispose(value);
		if(value == null) {
			flag = true;
		}
//		if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID) {
//			flag = true;
//		}
		String filterType = rankParam.getFilterType();
		if(filterType != null && !"".equals(filterType)) {
			// 进行值的过滤
			FilterTypes filterTypeEnum = FilterTypes.getFilterTypeName(filterType);
			double max = rankParam.getMax();
			double min = rankParam.getMin();
			double  contrast = rankParam.getContrast();
			switch(filterTypeEnum) {
			case GET: // >=
				if(value < contrast) {
					flag = true;
				}
				break;
			case GT: // >
				if(value <= contrast) {
					flag = true;
				}
				break;
			case LT: // <
				if(value >= contrast) {
					flag = true;
				}
				break;
			case LET: // <=
				if(value > contrast) {
					flag = true;
				}
				break;
			case BETWEEN: // between
				if(value < min || value > max) {
					flag = true;
				}
				break;
			default:
				break;
			}
		} 
		return flag;
	}
	
	private String createItems(RankParam rankParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int startMon = rankParam.getStartMon();
		int endMon = rankParam.getEndMon();
		int startDay = rankParam.getStartDay();
		int endDay = rankParam.getEndDay();
		Date startDate = null,  endDate = null;
		//考虑到2月29号的情况，所以就用闰年来构造
		String startTime = "2000-" + String.format("%02d", startMon) + "-" + String.format("%02d", startDay) + " 00:00:00";
		String endTime = "2000-" + String.format("%02d", endMon) + "-" + String.format("%02d", endDay) + " 00:00:00";
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return "参数错误，日期不对";
		}
		//构造的参数
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		return items;
	}
}
