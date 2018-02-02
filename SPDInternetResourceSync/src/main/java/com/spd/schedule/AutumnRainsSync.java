package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.cq.impl.AutumnRainsDaoImpl;
import com.spd.dao.cq.impl.AutumnRainsSeqDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 秋雨指数
 * @author Administrator
 *
 */
public class AutumnRainsSync {
	
	//定义指标中的站点数
	private static int STATIONLEVELCNT = 17;
	
	private AutumnRainsDaoImpl autumnRainsDaoImpl = new AutumnRainsDaoImpl();
	/**
	 * 计算秋雨的开始日期
	 */
	public void caleStartTime(String datetime) {
		//1. 查询从8-21开始到现在的每天的有雨的站数
		
		String yearStr = datetime.substring(0, 4);
		String startTime = yearStr + "-08-21";
		//如果开始日期已经确定的话，就不再查找开始日期
		int year = Integer.parseInt(yearStr);
		//TODO 
		boolean isYearExist = autumnRainsDaoImpl.isExistByYear(year);
		if(isYearExist) {
			return;
		}
		List rainsCntList = autumnRainsDaoImpl.getRainsCntByTimes(startTime, datetime);
		String resultStartTime = null, resultEndTime = null;// 结果的开始，结束日期
		for(int i = 0; i < rainsCntList.size() - 4; i++) {
			int start = i, end = i + 4; // 开始结束的索引
			HashMap<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
			for(int j = i; j < i + 5; j++) {
				HashMap dataMap = (HashMap) rainsCntList.get(j);
				int cnt = ((Long) dataMap.get("cnt")).intValue();
				if(cnt >= STATIONLEVELCNT) {
					//满足条件
					resultMap.put(j, 1);
				} else {
					resultMap.put(j, 0);
				}
			}
			//判断数目是否满足 >= 4，然后判断是否第一天，最后一天也满足条件
			boolean flag = (resultMap.get(start) == 1) && (resultMap.get(end) == 1);
			if(flag) {
				Iterator<Integer> it = resultMap.keySet().iterator();
				int cnt = 0;
				while(it.hasNext()) {
					Integer index = it.next();
					Integer value = resultMap.get(index);
					if(value > 0) {
						cnt++;
					}
				}
				if(cnt >= 4){
					//满足条件
					HashMap dataMap = (HashMap) rainsCntList.get(i);
					resultStartTime = (String) dataMap.get("datetime");
					dataMap = (HashMap) rainsCntList.get(i + 4);
					resultEndTime = (String) dataMap.get("datetime");
					break;
				}
			}
		}
		//
		if(resultStartTime != null) {
			//入库
			List dataList = new ArrayList();
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", resultStartTime + " 00:00:00");
			dataMap.put("year", Integer.parseInt(yearStr));
			dataList.add(dataMap);
			autumnRainsDaoImpl.insert(dataList);
		}
		
	}
	
	/**
	 * 计算历史的秋雨期
	 */
	public void caleHisRangeByTimes(int year) {
		String startTime = year + "-08-21";
		String endTime = year + "-11-30";
		Date date = new Date();
		Date endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			endDate = sdf.parse(endTime);
			//TODO
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if(date.getTime() > endDate.getTime()) return;
//		if(!str.equals(endTime)) return;
		List rainsCntList = autumnRainsDaoImpl.getMDRainsCntByTimes(startTime, endTime);
		String resultStartTime = null, resultEndTime = null;// 结果的开始，结束日期
		List<String> startTimeList = new ArrayList();
		List<String> endTimeList = new ArrayList();
		LinkedHashMap<Integer, Integer> resultMap = new LinkedHashMap<Integer, Integer>();
		for(int i = 0; i < rainsCntList.size(); i++) {
			HashMap dataMap = (HashMap) rainsCntList.get(i);
			int cnt = (Integer) dataMap.get("cnt");
			if(cnt >= STATIONLEVELCNT) {
				//满足条件
				resultMap.put(i, 1);
			} else {
				resultMap.put(i, 0);
			}
		}
		//计算开始期
		int index = 0;
		while(index < rainsCntList.size() - 4) {
			int value1 = resultMap.get(index);
			int value2 = resultMap.get(index + 1);
			int value3 = resultMap.get(index + 2);
			int value4 = resultMap.get(index + 3);
			int value5 = resultMap.get(index + 4);
			if(value1 == 1 && value5 == 1) {
				if(value2 + value3 + value4 >= 2) {
					//满足条件
					startTimeList.add((String)((HashMap) rainsCntList.get(index)).get("datetime"));
					if(rainsCntList.size() == index + 5) break;
					for(int i = index + 5; i < rainsCntList.size(); i++) {
						int valueI = resultMap.get(i);
						if(valueI == 0 || i == rainsCntList.size() - 1) {
							//跳出循环
							index = i;
							break;
						}
					}
				} else {
					index++;
				}
			} else {
				index++;
			}
		}
		//计算结束期
		index = 0;
		while(index < rainsCntList.size() - 4) {
			int value1 = resultMap.get(index);
			int value2 = resultMap.get(index + 1);
			int value3 = resultMap.get(index + 2);
			int value4 = resultMap.get(index + 3);
			int value5 = resultMap.get(index + 4);
			if(value1 == 0 && value5 == 0) {
				if(value2 + value3 + value4 <= 1) {
					//满足条件
					endTimeList.add((String)((HashMap) rainsCntList.get(index)).get("datetime"));
					if(rainsCntList.size() == index + 5) break;
					for(int i = index + 5; i < rainsCntList.size(); i++) {
						int valueI = resultMap.get(i);
						if(valueI == 1 || i == rainsCntList.size() - 1) {
							//跳出循环
							index = i;
							break;
						}
					}
				} else {
					index++;
				}
			} else {
				index++;
			}
		}
		System.out.println(startTimeList);
		System.out.println(endTimeList);
		long start = Long.MIN_VALUE;
		List dataList = new ArrayList();
		for(int i = 0; i < startTimeList.size(); i++) {
			long time = 0L;
			try {
				time = sdf.parse(startTimeList.get(i)).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(time < start) continue;
			boolean flag = true;
			for(int j = 0; j < endTimeList.size(); j++) {
				long time2 = 0L;
				try {
					time2 = sdf.parse(endTimeList.get(j)).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(time2 > time) {
					HashMap dataMap = new HashMap();
					dataMap.put("year", year);
					dataMap.put("StartTime", startTimeList.get(i) + " 00:00:00");
					dataMap.put("EndTime", endTimeList.get(j) + " 00:00:00");
					dataList.add(dataMap);
					System.out.println(startTimeList.get(i) + "," + endTimeList.get(j));
					start = time2;
					flag = false;
					break;
				}
			}
			if(flag) {
				System.out.println(startTimeList.get(i) + "," + year + "-11-30");
				HashMap dataMap = new HashMap();
				dataMap.put("year", year);
				dataMap.put("StartTime", startTimeList.get(i) + " 00:00:00");
				dataMap.put("EndTime", year + "-11-30 00:00:00");
				dataList.add(dataMap);
				break;
			}
		}
		
		AutumnRainsSeqDaoImpl autumnRainsSeqDaoImpl = new AutumnRainsSeqDaoImpl();
		autumnRainsSeqDaoImpl.update(dataList, year);
	}
	
	/**
	 * 根据时间段，计算开始期，结束期
	 * @param startTime
	 * @param endTime
	 */
	public String[] caleRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] startEndTimes = new String[2];
		List rainsCntList = autumnRainsDaoImpl.getRainsCntByTimes(startTime, endTime);
		String resultStartTime = null, resultEndTime = null;// 结果的开始，结束日期
		List<String> startTimeList = new ArrayList();
		List<String> endTimeList = new ArrayList();
		LinkedHashMap<Integer, Integer> resultMap = new LinkedHashMap<Integer, Integer>();
		for(int i = 0; i < rainsCntList.size(); i++) {
			HashMap dataMap = (HashMap) rainsCntList.get(i);
			int cnt = ((Long) dataMap.get("cnt")).intValue();
			if(cnt >= STATIONLEVELCNT) {
				//满足条件
				resultMap.put(i, 1);
			} else {
				resultMap.put(i, 0);
			}
		}
		//计算开始期
		int index = 0;
		while(index < rainsCntList.size() - 4) {
			int value1 = resultMap.get(index);
			int value2 = resultMap.get(index + 1);
			int value3 = resultMap.get(index + 2);
			int value4 = resultMap.get(index + 3);
			int value5 = resultMap.get(index + 4);
			if(value1 == 1 && value5 == 1) {
				if(value2 + value3 + value4 >= 2) {
					//满足条件
					startTimeList.add((String)((HashMap) rainsCntList.get(index)).get("datetime"));
					for(int i = index + 5; i < rainsCntList.size(); i++) {
						int valueI = resultMap.get(i);
						if(valueI == 0 || i == rainsCntList.size() - 1) {
							//跳出循环
							index = i;
							break;
						}
					}
				} else {
					index++;
				}
			} else {
				index++;
			}
		}
		//计算结束期
		index = 0;
		while(index < rainsCntList.size() - 5) {
			int value1 = resultMap.get(index);
			int value2 = resultMap.get(index + 1);
			int value3 = resultMap.get(index + 2);
			int value4 = resultMap.get(index + 3);
			int value5 = resultMap.get(index + 4);
			if(value1 == 0 && value5 == 0) {
				if(value2 + value3 + value4 <= 1) {
					//满足条件
					endTimeList.add((String)((HashMap) rainsCntList.get(index)).get("datetime"));
					for(int i = index + 5; i < rainsCntList.size(); i++) {
						int valueI = resultMap.get(i);
						if(valueI == 1 || i == rainsCntList.size() - 1) {
							//跳出循环
							index = i;
							break;
						}
					}
				} else {
					index++;
				}
			} else {
				index++;
			}
		}
		System.out.println(startTimeList);
		System.out.println(endTimeList);
		startEndTimes[0] = startTimeList.get(0);
		String endStartTime = startTimeList.get(startTimeList.size() - 1);
		String endEndTime = endTimeList.get(endTimeList.size() - 1);
		Date endStartTimeDate = null, endEndTimeDate = null;
		try {
			endStartTimeDate = sdf.parse(endStartTime);
			endEndTimeDate = sdf.parse(endEndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(endEndTimeDate.getTime() > endStartTimeDate.getTime()) {
			startEndTimes[1] = endEndTime;
		} else {
			//最后一天
			startEndTimes[1] = startTime.substring(0, 4) + "-11-30";
		}
		return startEndTimes;
	}
	
	/**
	 * 计算指数
	 * @param startTime
	 * @param endTime
	 */
	public void calcIndex(String startTime, String endTime) {
		int year = Integer.parseInt(startTime.substring(0, 4));
		//1. 雨量和 Pre
		Double pre = autumnRainsDaoImpl.getRainsSumtByTimes(startTime, endTime);
		String type = "1981-2010";
		List indexList = autumnRainsDaoImpl.queryIndex(type);
		//指数常量：
		int Length = 0;
		Double LengthVariance = 0.0, Rain = 0.0, RainVariance = 0.0;
		if(indexList != null && indexList.size() > 0) {
			HashMap dataMap = (HashMap) indexList.get(0);
			Length = (Integer) dataMap.get("Length");
			LengthVariance = (Double) dataMap.get("LengthVariance");
			Rain = (Double) dataMap.get("Rain");
			RainVariance = (Double) dataMap.get("RainVariance");
		}
		//2. 长度指数I1 LengthIndexI
		int days = caleDays(startTime, endTime);
		if(days == 0) return;
		Double LengthIndexI = (days -  Length) / LengthVariance;
		//3. 雨量指数I2 PreIndex
		Double PreIndex = (pre - Rain) / RainVariance;
		//4. 综合强度指数  IntensityIndex
		Double IntensityIndex = 0.5 * LengthIndexI + 0.5 * PreIndex;
		HashMap dataMap = new HashMap();
		dataMap.put("year", year);
		dataMap.put("StartTime", startTime + " 00:00:00");
		dataMap.put("EndTime", endTime + " 00:00:00");
		dataMap.put("LengthIndexI", CommonTool.roundDouble2(LengthIndexI));
		dataMap.put("Pre", CommonTool.roundDouble(pre));
		dataMap.put("PreIndex", CommonTool.roundDouble2(PreIndex));
		dataMap.put("IntensityIndex", IntensityIndex);
		List dataList = new ArrayList();
		dataList.add(dataMap);
		autumnRainsDaoImpl.update(dataList, year);
	}
	
	private int caleDays(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			int days = ((Long) ((endDate.getTime() - startDate.getTime()) / CommonConstant.DAYTIMES)).intValue();
			return days;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void sync(Date date) {
//		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String timeStr = sdf.format(date);
		//测试
//		timeStr = "2015-11-30";
//		try {
//			date = sdf.parse(timeStr);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		//测试结束
		
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		String yearStr = sdfYear.format(date);
		
		String startTimeStr = yearStr + "-08-25";
		String endTimeStr = yearStr + "-11-30";
		long time = date.getTime();
		long start = 0, end = 0;
		try {
			start = sdf.parse(startTimeStr).getTime();
			end = sdf.parse(endTimeStr).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(time >= start && time <= end) {
			caleStartTime(timeStr);
		}
		if(timeStr.equals(endTimeStr)) {
			String[] timesStr = caleRange(startTimeStr, endTimeStr);
			calcIndex(timesStr[0], timesStr[1]);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		AutumnRainsSync autumnRainsSync = new AutumnRainsSync();
		autumnRainsSync.caleHisRangeByTimes(2016);
//		for(int i = 1962; i <= 1970; i++) {
//			autumnRainsSync.caleHisRangeByTimes(i);
//		}
//		autumnRainsSync.sync();
	}

}
