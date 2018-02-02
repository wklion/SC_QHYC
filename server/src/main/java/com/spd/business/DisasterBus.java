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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.DisasterLowTmpParam;
import com.spd.common.DisasterLowTmpResult;
import com.spd.common.DisasterRainFloodParam;
import com.spd.common.DisasterRainFloodResult;
import com.spd.common.DisasterRainStormFinResult;
import com.spd.common.DisasterRainStormParam;
import com.spd.common.DisasterRainStormResult;
import com.spd.common.DisasterRainStormTotalResult;
import com.spd.common.TimeValue;
import com.spd.service.IDisaster;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;

/**
 * 灾害统计相关
 * @author Administrator
 *
 */
public class DisasterBus {
	
	/**
	 * 暴雨
	 * @return
	 */
	public DisasterRainStormFinResult rainstorm(DisasterRainStormParam disasterRainStormParam) {
		List<DisasterRainStormResult> disasterRainStormResults = new ArrayList<DisasterRainStormResult>();
		String items = "";
		try {
			items = CommonTool.createItemStrByTimes(disasterRainStormParam.getStartYear(), disasterRainStormParam.getEndYear(),
					disasterRainStormParam.getStartMon(), disasterRainStormParam.getEndMon(),
					disasterRainStormParam.getStartDay(), disasterRainStormParam.getEndDay());
		} catch(Exception e) {
			return null;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", disasterRainStormParam.getTableName());
		paramMap.put("items", items);
		paramMap.put("startYear", disasterRainStormParam.getStartYear());
		paramMap.put("endYear", disasterRainStormParam.getEndYear());
		IDisaster disaster = (IDisaster)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterImpl");
		List<LinkedHashMap> resultList = disaster.rainstorm(paramMap);
		disasterRainStormResults = dispose(resultList, disasterRainStormParam);
		List<DisasterRainStormTotalResult> disasterRainStormTotalResultList = totalRain(disasterRainStormResults);
		DisasterRainStormFinResult disasterRainStormFinResult = new DisasterRainStormFinResult();
		disasterRainStormFinResult.setSeqResult(disasterRainStormResults);
		disasterRainStormFinResult.setTotalResult(disasterRainStormTotalResultList);
		return disasterRainStormFinResult;
	}
	
	/**
	 * 洪涝
	 * @param disasterRainFloodParam
	 * @return
	 */
	public List<DisasterRainFloodResult> flood(DisasterRainFloodParam disasterRainFloodParam) {
		long start1 = System.currentTimeMillis();
		
		List<DisasterRainFloodResult> disasterRainFloodResultList = new ArrayList<DisasterRainFloodResult>();
		// 1. 查询
		String items = "";
		try {
			items = CommonTool.createItemStrByTimes(disasterRainFloodParam.getStartYear(), disasterRainFloodParam.getEndYear(),
					disasterRainFloodParam.getStartMon(), disasterRainFloodParam.getEndMon(),
					disasterRainFloodParam.getStartDay(), disasterRainFloodParam.getEndDay());
		} catch(Exception e) {
			return null;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", disasterRainFloodParam.getTableName());
		paramMap.put("items", items);
		paramMap.put("startYear", disasterRainFloodParam.getStartYear());
		paramMap.put("endYear", disasterRainFloodParam.getEndYear());
		IDisaster disaster = (IDisaster)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterImpl");
		long start2 = System.currentTimeMillis();
		List<LinkedHashMap> resultList = disaster.rainstorm(paramMap);
		long end1 = System.currentTimeMillis();
		System.out.println("构造查询花费时间【" + (start2 - start1) +"】");
		System.out.println("查询花费时间【" + (end1 - start2) +"】");
		// 2. 处理结果
		long start3 = System.currentTimeMillis();
		disasterRainFloodResultList = dispose(resultList, disasterRainFloodParam, disasterRainFloodParam.getTableName());
		long end2 = System.currentTimeMillis();
		System.out.println("处理结果：【" + (end2 - start3) + "】");
		return disasterRainFloodResultList;
	}
	
	/**
	 * 低温阴雨
	 * @param disasterRainFloodParam
	 * @return
	 */
	public List<DisasterLowTmpResult> lowTmpRain(DisasterLowTmpParam disasterLowTmpParam) {
		List<DisasterLowTmpResult> disasterLowTmpResultList = new ArrayList<DisasterLowTmpResult>();
		//1. 查询t_tem_avg 和 t_pre_time_0820
		String temTable = "t_tem_avg";
		String preTable = "t_pre_time_0820"; //白天雨量
		String items = "";
		try {
			items = CommonTool.createItemStrByTimes(disasterLowTmpParam.getYear(), disasterLowTmpParam.getYear(),
					disasterLowTmpParam.getStartMon(), disasterLowTmpParam.getEndMon(),
					disasterLowTmpParam.getStartDay(), disasterLowTmpParam.getEndDay());
		} catch(Exception e) {
			return null;
		}
		IDisaster disaster = (IDisaster)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterImpl");
		HashMap tmpParamMap = new HashMap();
		tmpParamMap.put("tableName", temTable);
		tmpParamMap.put("items", items);
		tmpParamMap.put("startYear", disasterLowTmpParam.getYear());
		tmpParamMap.put("endYear", disasterLowTmpParam.getYear());
		//温度的查询结果
		List<LinkedHashMap> tmpResultList = disaster.rainstorm(tmpParamMap);
		HashMap rainParamMap = new HashMap();
		tmpParamMap.put("tableName", preTable);
		tmpParamMap.put("items", items);
		tmpParamMap.put("startYear", disasterLowTmpParam.getYear());
		tmpParamMap.put("endYear", disasterLowTmpParam.getYear());
		//降水的查询结果
		List<LinkedHashMap> preResultList = disaster.rainstorm(tmpParamMap);
		//2. 分析组装结果
		disasterLowTmpResultList = dispose(tmpResultList, preResultList, disasterLowTmpParam, preTable);
		//3. 返回
		return disasterLowTmpResultList;
	}
	
	/**
	 * 统计合计雨量
	 * @param disasterRainStormResults
	 * @return
	 */
	private List<DisasterRainStormTotalResult> totalRain(List<DisasterRainStormResult> disasterRainStormResults) {
		List<DisasterRainStormTotalResult> resultList = new ArrayList<DisasterRainStormTotalResult>();
		LinkedHashMap<String, DisasterRainStormTotalResult> tempMap = new LinkedHashMap<String, DisasterRainStormTotalResult>(); 
		StationArea stationArea = new StationArea();
		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Set<String> stationsSet = new HashSet<String>();
		for(int i=0; i<disasterRainStormResults.size(); i++) {
			DisasterRainStormResult disasterRainStormResult = disasterRainStormResults.get(i);
			stationsSet.add(disasterRainStormResult.getStation_Id_C());
		}
		Iterator<String> it = stationsSet.iterator();
		while(it.hasNext()) {
			String key = it.next();
			DisasterRainStormTotalResult disasterRainStormTotalResult = new DisasterRainStormTotalResult();
			tempMap.put(key, disasterRainStormTotalResult);
		}
		
		for(int i=0; i<disasterRainStormResults.size(); i++) {
			DisasterRainStormResult disasterRainStormResult = disasterRainStormResults.get(i);
			String key = disasterRainStormResult.getStation_Id_C();
			DisasterRainStormTotalResult disasterRainStormTotalResult = tempMap.get(key);
			Double extValue = disasterRainStormTotalResult.getExtValue();
			double value = disasterRainStormResult.getValue();
			if(extValue == null || 0 == extValue || value > extValue) {
				disasterRainStormTotalResult.setExtValue(disasterRainStormResult.getValue());
				disasterRainStormTotalResult.setExtDatetime(disasterRainStormResult.getDatetime());
			} 
			disasterRainStormTotalResult.setStation_Id_C(key);
			disasterRainStormTotalResult.setStation_Name(disasterRainStormResult.getStation_Name());
			String level = disasterRainStormResult.getLevel();
			if(level.equals("暴雨")) {
				disasterRainStormTotalResult.setLevel1Cnt(disasterRainStormTotalResult.getLevel1Cnt() + 1);
			} else if(level.equals("大暴雨")) {
				disasterRainStormTotalResult.setLevel2Cnt(disasterRainStormTotalResult.getLevel2Cnt() + 1);
			} else if(level.equals("特大暴雨")) {
				disasterRainStormTotalResult.setLevel2Cnt(disasterRainStormTotalResult.getLevel2Cnt() + 1);
			}
			disasterRainStormTotalResult.setSum(disasterRainStormTotalResult.getSum() + 1);
		}
		Set<String> set = tempMap.keySet();
		Iterator<String> it2 = set.iterator();
		while(it2.hasNext()) {
			String key = it2.next();
			DisasterRainStormTotalResult disasterRainStormTotalResult = tempMap.get(key);
			disasterRainStormTotalResult.setArea(stationAreaMap.get(disasterRainStormTotalResult.getStation_Id_C()));
			resultList.add(disasterRainStormTotalResult);
		}
		return resultList;
	}
	/**
	 * 低温阴雨的统计
	 * @param tmpResultList
	 * @param preResultList
	 * @return
	 */
	private List<DisasterLowTmpResult> dispose(List<LinkedHashMap> tmpResultList, List<LinkedHashMap> preResultList, DisasterLowTmpParam disasterLowTmpParam, String tableName) {
		List<DisasterLowTmpResult> disasterLowTmpResultList = new ArrayList<DisasterLowTmpResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
		String startTimeStr = disasterLowTmpParam.getYear() + "-" + String.format("%02d", disasterLowTmpParam.getStartMon()) + "-"
							+ String.format("%02d", disasterLowTmpParam.getStartDay());
		String endTimeStr = disasterLowTmpParam.getYear() + "-" + String.format("%02d", disasterLowTmpParam.getEndMon()) + "-"
							+ String.format("%02d", disasterLowTmpParam.getEndDay());
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		long start = startDate.getTime();
		long end = endDate.getTime();
		// 1. 分析连续低温。
		for(LinkedHashMap map : tmpResultList) {
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer) map.get("year");
			DisasterLowTmpResult disasterLowTmpResult = new DisasterLowTmpResult();
			disasterLowTmpResult.setStation_Id_C(station_Id_C);
			disasterLowTmpResult.setStation_Name(station_Name);
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(current < start || current > end) {
						continue;
					}
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					if(disasterLowTmpResult.getStartDate() == null) {
						disasterLowTmpResult.setStartDatetime(currentTimeStr);
						disasterLowTmpResult.setStartDate(date);
						disasterLowTmpResult.setEndDate(date);
						if(value < disasterLowTmpParam.getAvgTmp()){
							disasterLowTmpResult.setAvgTmp(value);
							disasterLowTmpResult.setPersistDays(1);
							disasterLowTmpResult.setNoSunDays(1);
						}
						continue;
					}
					//1. 温度满足条件，并且连续
					Date startItemDate = disasterLowTmpResult.getEndDate();
					if(value < disasterLowTmpParam.getAvgTmp() && date.getTime() - startItemDate.getTime() == CommonConstant.DAYTIMES){
						disasterLowTmpResult.setEndDatetime(currentTimeStr);
						disasterLowTmpResult.setEndDate(date);
						disasterLowTmpResult.setAvgTmp(disasterLowTmpResult.getAvgTmp() + value);
						disasterLowTmpResult.setPersistDays(disasterLowTmpResult.getPersistDays() + 1);
						disasterLowTmpResult.setNoSunDays(disasterLowTmpResult.getPersistDays() + 1);
						
					} else if(disasterLowTmpResult.getPersistDays() >= disasterLowTmpParam.getSequenceDays()){
						disasterLowTmpResultList.add(disasterLowTmpResult);
						disasterLowTmpResult = new DisasterLowTmpResult();
						disasterLowTmpResult.setStation_Id_C(station_Id_C);
						disasterLowTmpResult.setStation_Name(station_Name);
					}
				}
			}
			//如果最后一个连续，并且满足条件，不能漏掉
			if(disasterLowTmpResult.getPersistDays() >= disasterLowTmpParam.getSequenceDays()){
				disasterLowTmpResultList.add(disasterLowTmpResult);
			}
		}
		//结果的值再求一次平均
		for(DisasterLowTmpResult disasterLowTmpResult : disasterLowTmpResultList) {
			disasterLowTmpResult.setAvgTmp(CommonTool.roundDouble(disasterLowTmpResult.getAvgTmp() / disasterLowTmpResult.getPersistDays()));
		}
		// 对应的白天雨日数再算出来。
		for(DisasterLowTmpResult disasterLowTmpResult : disasterLowTmpResultList) {
			String station_Id_C = disasterLowTmpResult.getStation_Id_C();
			for(LinkedHashMap map : preResultList) {
				String tempStation = (String) map.get("Station_Id_C");
				if(tempStation.equals(station_Id_C)) {
					Date startTempDate = disasterLowTmpResult.getStartDate();
					Date endTempDate = disasterLowTmpResult.getEndDate();
					for(long i=startTempDate.getTime(); i<=endTempDate.getTime(); i+=CommonConstant.DAYTIMES) {
						Date tempDate = new Date(i);
						String tempStr = sdf2.format(tempDate);
						String tempKey = "m" + tempStr.substring(0, 2) + "d" + tempStr.substring(2, 4);
						Double value = (Double) map.get(tempKey);
						if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
							continue;
						}
						if(value > 0) {
							disasterLowTmpResult.setDayRainTimes(disasterLowTmpResult.getDayRainTimes() + 1);
							disasterLowTmpResult.setPreValue(value + disasterLowTmpResult.getPreValue());
						}
					}
					break;
				}
			}
		}
		
		//白天有雨日数 
//		if(disasterLowTmpParam.isFilterRainDays()) {
		int rainDays = disasterLowTmpParam.getRainDays();
		for(int i = disasterLowTmpResultList.size() - 1; i>=0 ;i--) {
			DisasterLowTmpResult disasterLowTmpResult = disasterLowTmpResultList.get(i);
			if(disasterLowTmpResult.getDayRainTimes() < rainDays) {
				disasterLowTmpResultList.remove(i);
			}
		}
//		}
		//计算程度 TODO xianchao ，算法不确定是否正确。
		for(int i = 0; i < disasterLowTmpResultList.size(); i++) {
			DisasterLowTmpResult disasterLowTmpResult = disasterLowTmpResultList.get(i);
			int persistDays = disasterLowTmpResult.getPersistDays();
			if(persistDays >= 10) {
				disasterLowTmpResult.setLevel("严重");
			} else {
				disasterLowTmpResult.setLevel("轻度");
			}
		}
		return disasterLowTmpResultList;
	}
	
	private List<DisasterRainFloodResult> dispose(List<LinkedHashMap> resultList, DisasterRainFloodParam disasterRainFloodParam, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<DisasterRainFloodResult> disasterRainFloodResultList = new ArrayList<DisasterRainFloodResult>();
		Date startDate = null, endDate = null;
		long start = 0L, end = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTimeStr = disasterRainFloodParam.getStartTime();
		String endTimeStr = disasterRainFloodParam.getEndTime();
		//1 .  key :station_id_c vaule : TimeValue的数组
		Map<String, List<TimeValue>> timeValueListMap = new HashMap<String, List<TimeValue>>();
		try {
			startDate = sdf.parse(startTimeStr);
			start = startDate.getTime();
			endDate = sdf.parse(endTimeStr);
			end = endDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		for(int i=0; i<resultList.size(); i++) {
			Map map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer) map.get("year");
			List<TimeValue> timeValueList = timeValueListMap.get(station_Id_C + "_" + station_Name);
			if(timeValueList == null) {
				timeValueList = new ArrayList<TimeValue>();
				timeValueListMap.put(station_Id_C + "_" + station_Name, timeValueList);
			}
			
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(current < start || current > end) {
						continue;
					}
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					TimeValue timeValue = new TimeValue();
					timeValue.setDate(date);
					timeValue.setValue(value);
					timeValue.setDateStr(currentTimeStr);
					timeValueList.add(timeValue);
				}
			}
		}
		//2. 遍历timeValueListMap，找到符合结果的数据
		Set<String> set = timeValueListMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			String station_Id_C = key.split("_")[0];
			String station_Name = key.split("_")[1];
			List<TimeValue> timeValueList = timeValueListMap.get(key);
			double value2Days = 0, value3Days = 0; // 2日雨量， 3日雨量
			//处理一天的情况，必须要第二天不满足条件的
			for(int i=0; i< timeValueList.size(); i++) {
				TimeValue timeValue = timeValueList.get(i);
				Date date = timeValue.getDate();
				double value = timeValue.getValue();
				//1天的处理
				DisasterRainFloodResult disasterRainFloodResult1 = new DisasterRainFloodResult();
				disasterRainFloodResult1.setStation_Id_C(station_Id_C);
				disasterRainFloodResult1.setStation_Name(station_Name);
				disasterRainFloodResult1.setPersist(1);
				disasterRainFloodResult1.setSum(value);
				disasterRainFloodResult1.setStartDatetime(timeValue.getDateStr());
				disasterRainFloodResult1.setEndDatetime(timeValue.getDateStr());
				if(value >= disasterRainFloodParam.getLevel11DayRain() && value < disasterRainFloodParam.getLevel21DayRain()) {
					//一般
					disasterRainFloodResult1.setLevel("一般");
					disasterRainFloodResult1.setExt1DayValue(value);
					disasterRainFloodResultList.add(disasterRainFloodResult1);
				} else if(value >= disasterRainFloodParam.getLevel21DayRain() && value < disasterRainFloodParam.getLevel31DayRain()) {
					//中度
					disasterRainFloodResult1.setLevel("中度");
					disasterRainFloodResult1.setExt1DayValue(value);
					disasterRainFloodResultList.add(disasterRainFloodResult1);
				} else if(value >= disasterRainFloodParam.getLevel31DayRain()) {
					//严重
					disasterRainFloodResult1.setLevel("严重");
					disasterRainFloodResult1.setExt1DayValue(value);
					disasterRainFloodResultList.add(disasterRainFloodResult1);
				}
			}
			//2. 处理两天的情况，必须要三天不满足条件的
			for(int i=1; i< timeValueList.size(); i++) {
				DisasterRainFloodResult disasterRainFloodResult2 = new DisasterRainFloodResult();
				disasterRainFloodResult2.setStation_Id_C(station_Id_C);
				disasterRainFloodResult2.setStation_Name(station_Name);
				TimeValue timeValue = timeValueList.get(i);
				double value = timeValue.getValue();
				TimeValue preTimeValue = timeValueList.get(i - 1);
				disasterRainFloodResult2.setStartDatetime(preTimeValue.getDateStr());
				disasterRainFloodResult2.setEndDatetime(timeValue.getDateStr());
				disasterRainFloodResult2.setPersist(2);
				double preValue = preTimeValue.getValue();
				disasterRainFloodResult2.setSum(preValue + value); 
				disasterRainFloodResult2.setExt2DayValue(preValue + value); 
				if(timeValue.getDate().getTime() - preTimeValue.getDate().getTime() == CommonConstant.DAYTIMES) {
					//刚好相隔一天
					double sum = value + preValue;
					if(sum >= disasterRainFloodParam.getLevel12DayRain()) {
						if(value > preValue) {
							disasterRainFloodResult2.setExt1DayValue(value);
						} else {
							disasterRainFloodResult2.setExt1DayValue(preValue);
						}
					}
					if(sum >= disasterRainFloodParam.getLevel12DayRain() && sum < disasterRainFloodParam.getLevel22DayRain()) {
						//一般
						disasterRainFloodResult2.setLevel("一般");
						disasterRainFloodResultList.add(disasterRainFloodResult2);
					} else if(sum >= disasterRainFloodParam.getLevel22DayRain() && sum < disasterRainFloodParam.getLevel32DayRain()) {
						//中度
						disasterRainFloodResult2.setLevel("中度");
						disasterRainFloodResultList.add(disasterRainFloodResult2);
					} else if(sum >= disasterRainFloodParam.getLevel32DayRain()) {
						//严重
						disasterRainFloodResult2.setLevel("严重");
						disasterRainFloodResultList.add(disasterRainFloodResult2);
					}
				}
			}
			//3. 处理三天以上的情况
			// 第1天降水量≥5毫米，3日以后连续出现的≥10毫米的日雨量可记入降水总量统计
			for(int i=2; i< timeValueList.size(); i++) {
				DisasterRainFloodResult disasterRainFloodResult3 = new DisasterRainFloodResult();
				disasterRainFloodResult3.setStation_Id_C(station_Id_C);
				disasterRainFloodResult3.setStation_Name(station_Name);
				//3
				TimeValue timeValue = timeValueList.get(i);
				double value = timeValue.getValue();
				//1
				TimeValue preTimeValue = timeValueList.get(i - 1);
				double preValue = preTimeValue.getValue();
				//2
				TimeValue firTimeValue = timeValueList.get(i - 2);
				double firValue = firTimeValue.getValue();
				if(firValue < 5) {
					continue;
				}
				disasterRainFloodResult3.setStartDatetime(firTimeValue.getDateStr());
				disasterRainFloodResult3.setEndDatetime(timeValue.getDateStr());
				disasterRainFloodResult3.setPersist(3);
				disasterRainFloodResult3.setSum(value + preValue + firValue);
				if(timeValue.getDate().getTime() - preTimeValue.getDate().getTime() == CommonConstant.DAYTIMES
						&& preTimeValue.getDate().getTime() - firTimeValue.getDate().getTime() == CommonConstant.DAYTIMES) {
					// 3天连续
					double sum = value + preValue + firValue;
					//满足条件的话，继续往后查找，把后面的都计入到总的结果。
					int sumCnt = 3;
					for(int j=i+1; j<timeValueList.size(); j++) {
						TimeValue itemTimeValue = timeValueList.get(j);
						Date itemDate = itemTimeValue.getDate();
						double itemValue = itemTimeValue.getValue();
						if(itemValue >= 10 && itemDate.getTime() - timeValue.getDate().getTime() == CommonConstant.DAYTIMES) {
							sum += itemTimeValue.getValue();
							disasterRainFloodResult3.setEndDatetime(itemTimeValue.getDateStr());
							disasterRainFloodResult3.setPersist(disasterRainFloodResult3.getPersist() + 1);
							sumCnt++;
						}
					}
					if(sum >= disasterRainFloodParam.getLevel13DayRain()) {
						//遍历找到3天的极值
						String flood3StartDateResult = disasterRainFloodResult3.getStartDatetime();
						String flood3EndDateResult = disasterRainFloodResult3.getEndDatetime();
						Date flood3StartDate = null, flood3EndDate = null; 
						try {
							flood3StartDate = sdf.parse(flood3StartDateResult);
							flood3EndDate = sdf.parse(flood3EndDateResult);
						} catch (ParseException e) {
							e.printStackTrace();
							continue;
						}
						//计算连续3天的极值
						double tempSum = 0;
						for(int k = 0; k < sumCnt - 2; k++) {
							int index = (i - 2) + k;
							TimeValue tempTimeValue1 = timeValueList.get(index);
							TimeValue tempTimeValue2 = timeValueList.get(index+1);
							TimeValue tempTimeValue3 = timeValueList.get(index+2);
							double tempSum2 = tempTimeValue1.getValue() + tempTimeValue2.getValue() + tempTimeValue3.getValue();
							if(tempSum2 > tempSum) {
								tempSum = tempSum2;
							}
						}
						disasterRainFloodResult3.setExt3DayValue(CommonTool.roundDouble(tempSum));
						//计算连续2天的极值
						tempSum = 0;
						for(int k = 0; k < sumCnt - 1; k++) {
							int index = (i - 2) + k;
							TimeValue tempTimeValue1 = timeValueList.get(index);
							TimeValue tempTimeValue2 = timeValueList.get(index+1);
							double tempSum2 = tempTimeValue1.getValue() + tempTimeValue2.getValue();
							if(tempSum2 > tempSum) {
								tempSum = tempSum2;
							}
						}
						disasterRainFloodResult3.setExt2DayValue(CommonTool.roundDouble(tempSum));
						//计算一天的极值
						tempSum = 0;
						for(int k = 0; k < sumCnt; k++) {
							int index = (i - 2) + k;
							TimeValue tempTimeValue1 = timeValueList.get(index);
							double tempSum2 = tempTimeValue1.getValue() ;
							if(tempSum2 > tempSum) {
								tempSum = tempSum2;
							}
						}
						disasterRainFloodResult3.setExt1DayValue(CommonTool.roundDouble(tempSum));
					}
					if(sum >= disasterRainFloodParam.getLevel13DayRain() && sum < disasterRainFloodParam.getLevel23DayRain()) {
						//一般
						disasterRainFloodResult3.setLevel("一般");
						disasterRainFloodResultList.add(disasterRainFloodResult3);
					} else if(sum >= disasterRainFloodParam.getLevel23DayRain() && sum < disasterRainFloodParam.getLevel33DayRain()) {
						//中度
						disasterRainFloodResult3.setLevel("中度");
						disasterRainFloodResultList.add(disasterRainFloodResult3);
					} else if(sum >= disasterRainFloodParam.getLevel33DayRain()) {
						//严重
						disasterRainFloodResult3.setLevel("严重");
						disasterRainFloodResultList.add(disasterRainFloodResult3);
					}
				}
			}
		}
		// 把一天包含在两天，或者1天，两天包含在三天中的删除掉。结果中有日期包含的，把子集的删除。
		List<DisasterRainFloodResult> disasterRainFloodResultList2 = new ArrayList<DisasterRainFloodResult>();
		for(int i=0; i<disasterRainFloodResultList.size(); i++) {
			DisasterRainFloodResult disasterRainFloodResult = disasterRainFloodResultList.get(i);
			String startDateTime = disasterRainFloodResult.getStartDatetime();
			String endDateTime = disasterRainFloodResult.getEndDatetime();
			int days1 = disasterRainFloodResult.getPersist();
			Date startDate1 = null, endDate1 = null;
			try {
				startDate1 = sdf.parse(startDateTime);
				endDate1 = sdf.parse(endDateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			boolean flag = false;
			for(int j=0; j<disasterRainFloodResultList.size(); j++) {
				DisasterRainFloodResult disasterRainFloodResult2 = disasterRainFloodResultList.get(j);
				int days2 = disasterRainFloodResult2.getPersist();
				if(days2 <= days1) {
					continue;
				}
				String startDateTime2 = disasterRainFloodResult2.getStartDatetime();
				String endDateTime2 = disasterRainFloodResult2.getEndDatetime();
				Date startDate2 = null, endDate2 = null;
				try {
					startDate2 = sdf.parse(startDateTime2);
					endDate2 = sdf.parse(endDateTime2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(startDate2.getTime() <= startDate1.getTime() && endDate2.getTime() >= endDate1.getTime()) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				disasterRainFloodResultList2.add(disasterRainFloodResult);
			}
		}
		return disasterRainFloodResultList2;
	}
	
	
	private List<DisasterRainStormResult> dispose(List<LinkedHashMap> resultList, DisasterRainStormParam disasterRainStormParam) {
		String columnType = CommonTable.getInstance().getTypeByTableName(disasterRainStormParam.getTableName());
		List<DisasterRainStormResult> disasterRainStormResultList = new ArrayList<DisasterRainStormResult>();
		Date startDate = null, endDate = null;
		long start = 0L, end = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTimeStr = disasterRainStormParam.getStartTime();
		String endTimeStr = disasterRainStormParam.getEndTime();
		try {
			startDate = sdf.parse(startTimeStr);
			start = startDate.getTime();
			endDate = sdf.parse(endTimeStr);
			end = endDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		for(int i=0; i<resultList.size(); i++) {
			Map map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			int year = (Integer) map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(current < start || current > end) {
						continue;
					}
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					DisasterRainStormResult disasterRainStormResult = new DisasterRainStormResult();
					disasterRainStormResult.setDatetime(currentTimeStr);
					disasterRainStormResult.setStation_Id_C(station_Id_C);
					disasterRainStormResult.setStation_Name(station_Name);
					disasterRainStormResult.setValue(value);
					//判断暴雨
					if(value >= disasterRainStormParam.getLevel1() && value < disasterRainStormParam.getLevel2()) {
						// 暴雨
						disasterRainStormResult.setLevel("暴雨");
					} else if(value >= disasterRainStormParam.getLevel2() && value < disasterRainStormParam.getLevel3()) {
						//大暴雨
						disasterRainStormResult.setLevel("大暴雨");
					} else if(value >= disasterRainStormParam.getLevel3()) {
						//特大暴雨
						disasterRainStormResult.setLevel("特大暴雨");
					}
					if(value >= disasterRainStormParam.getLevel1()) {
						disasterRainStormResultList.add(disasterRainStormResult);
					}
				}
			}
		}
		return disasterRainStormResultList;
	}
}
