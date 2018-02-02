package com.spd.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.ExtResult;
import com.spd.common.FirstDayParam;
import com.spd.common.FirstDayResult;
import com.spd.service.IExtStatistics;
import com.spd.service.IFirstDay;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.tool.LogTool;
import com.spd.util.CommonUtil;

/**
 * 初日统计
 * @author Administrator
 *
 */
public class FirstDayBus {

	/**
	 * 初日的降水、气温统计
	 * @param firstDayParam
	 * @return
	 */
	public List<FirstDayResult> rainTmpFirst(FirstDayParam firstDayParam) {
		//1. 查询1981年到2010年的历史数据。查询指定年的数据
		IFirstDay firstDayService = (IFirstDay)ContextLoader.getCurrentWebApplicationContext().getBean("FirstDayImpl");
		String items = CommonTool.getAllItems();
		String currentYearItem = CommonTool.createItemStrByTimes(firstDayParam.getYear(), firstDayParam.getYear(), firstDayParam.getStartMon(),
				firstDayParam.getEndMon(), firstDayParam.getStartDay(), firstDayParam.getEndDay());
		//过滤时间，先不考虑历史中的开始，结束年月。
		HashMap hisParamMap = new HashMap();
		hisParamMap.put("startYear", firstDayParam.getConstatStartYear());
		hisParamMap.put("endYear", firstDayParam.getConstatEndYear());
		hisParamMap.put("value", firstDayParam.getValue());
		hisParamMap.put("tableName", firstDayParam.getTableName());
		hisParamMap.put("items", items);
		List<Map> hisDataList = firstDayService.getRainTmpByTimeRange(hisParamMap);
		HashMap yearParamMap = new HashMap(); //对比年的参数
		yearParamMap.put("year", firstDayParam.getYear());
		yearParamMap.put("value", firstDayParam.getValue());
		yearParamMap.put("tableName", firstDayParam.getTableName());
		yearParamMap.put("items", currentYearItem);
		//指定年的结果。
		List<Map> yearDataList = firstDayService.getRainTmpByYear(yearParamMap);
		List<FirstDayResult> resultList = analyst(items, firstDayParam, hisDataList, yearDataList);
		int index = 1;
		for(FirstDayResult firstDayResult:resultList) {
			firstDayResult.setIndex(index++);
		}
		return resultList;
	}
	
	private List<FirstDayResult> analyst(String items, FirstDayParam firstDayParam, List<Map> hisDataList, List<Map> yearDataList) {
		List<FirstDayResult> resultFirstDayResultList = new ArrayList<FirstDayResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfDDD = new SimpleDateFormat("yyyyddd");
		SimpleDateFormat sdfMMDD = new SimpleDateFormat("MM-dd");
		//遍历历史的结果
		Map<String, List<FirstDayResult>> firstDayResultMap = analystHis(items, firstDayParam, hisDataList);
		//遍历指定年的结果
		Map<String, FirstDayResult> yearDayResultMap = analystYear(items, firstDayParam, yearDataList);
		//对比组装结果
		Set<String> set = firstDayResultMap.keySet();
		Iterator<String> it = set.iterator();
		StationArea stationArea = new StationArea();
		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		while(it.hasNext()) {
			String key = it.next();
			List<FirstDayResult> firstDayResultList = firstDayResultMap.get(key);
			int firstDaysSum = 0, lastDaysSum = 0; // 记录距离当年的1月1号的差
			FirstDayResult firstDayResult = new FirstDayResult();
			int extEarlyFirstDay = 999, extLateFirstDay = 0, extEarlyLastDay = 999, extLateLastDay = 0; //记录极端的日期
			int extEarlyFirstYear = 0, extLateFirstYear = 0, extEarlyLastYear = 0, extLateLastYear = 0; //记录极端的年份
			int firstDayCnt = 0, lastDayCnt = 0;
			for(int i=0; i<firstDayResultList.size(); i++) {
				FirstDayResult item = firstDayResultList.get(i);
				String station_Id_C = item.getStation_Id_C();
				firstDayResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				String firstDateStr = item.getFirstDate();
				if(firstDateStr == null) {
					continue;
				} 
				firstDayCnt++;
				String lastDateStr = item.getLastDate();
				if(lastDateStr == null) {
					continue;
				}  
//				System.out.println("first:" + firstDateStr + ",last:" + lastDateStr);
				lastDayCnt++;
				String firstYearDateStr = firstDateStr.substring(0, 4) + "-01-01";
				long firstYearTime = 0L, firstTime = 0L, lastTime = 0L;
				try {
					firstYearTime = sdf.parse(firstYearDateStr).getTime();
					firstTime = sdf.parse(firstDateStr).getTime();
					lastTime = sdf.parse(lastDateStr).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				int tempFirstTime = (int) Math.round((firstTime - firstYearTime + 0.0) / CommonConstant.DAYTIMES) + 1;
				firstDaysSum += tempFirstTime;
				int tempLastTime = (int) Math.round((lastTime - firstYearTime + 0.0) / CommonConstant.DAYTIMES) + 1;
				lastDaysSum += tempLastTime;
				if(tempFirstTime < extEarlyFirstDay) {
					extEarlyFirstDay = tempFirstTime; 
					extEarlyFirstYear = item.getYear();
				}
				if(tempFirstTime > extLateFirstDay) {
					extLateFirstDay = tempFirstTime;
					extLateFirstYear = item.getYear();
				}
				if(tempLastTime < extEarlyLastDay) {
					extEarlyLastDay = tempLastTime;
					extEarlyLastYear = item.getYear();
				}
				if(tempLastTime > extLateLastDay) {
					extLateLastDay = tempLastTime;
					extLateLastYear = item.getYear();
				}
			}
			FirstDayResult yearDayResult = yearDayResultMap.get(key);
			if(yearDayResult == null) {
				continue;
			}
			firstDayResult.setStation_Id_C(key);
			firstDayResult.setArea(stationAreaMap.get(key));
			int year = firstDayParam.getYear();
			boolean isLeapYear = CommonTool.isLeapYear(year);
			String firstDaySDF = year + String.format("%03d", firstDaysSum / (firstDayCnt == 0 ? 1 : firstDayCnt));
			String lastDaySDF = year + String.format("%03d", lastDaysSum / (lastDayCnt == 0 ? 1 : lastDayCnt));
			firstDayResult.setFirstDate(yearDayResult.getFirstDate());
			firstDayResult.setFirstValue(yearDayResult.getFirstValue());
			firstDayResult.setLastDate(yearDayResult.getLastDate());
			firstDayResult.setLastValue(yearDayResult.getLastValue());
			//计算极端的日期
			String extEarlyFirstStr = extEarlyFirstYear + String.format("%03d", extEarlyFirstDay);
			String extLateFirstStr = extLateFirstYear + String.format("%03d", extLateFirstDay);
			String extEarlyLastStr = extEarlyLastYear + String.format("%03d", extEarlyLastDay);
			String extLateLastStr = extLateLastYear + String.format("%03d", extLateLastDay);
			if(extEarlyFirstStr != null) {
				try {
					Date extEarlyFirstDate = sdfDDD.parse(extEarlyFirstStr);
					String extEarlyFirstDateStr = sdf.format(extEarlyFirstDate);
					firstDayResult.setExtEarlyFirstDay(extEarlyFirstDateStr);
				} catch (ParseException e) {
					firstDayResult.setExtEarlyFirstDay(null);
				}
			}
			
			if(extLateFirstStr != null) {
				try {
					Date extLateFirstDate = sdfDDD.parse(extLateFirstStr);
					String extLateFirstDateStr = sdf.format(extLateFirstDate);
					firstDayResult.setExtLateFirstDay(extLateFirstDateStr);
				} catch (ParseException e) {
					firstDayResult.setExtLateFirstDay(null);
				}
			}
			if(extLateLastStr != null) {
				try {
					Date extLateLastDate = sdfDDD.parse(extLateLastStr);
					String extLateLastDateStr = sdf.format(extLateLastDate);
					firstDayResult.setExtLateLastDay(extLateLastDateStr);
				} catch (ParseException e) {
					firstDayResult.setExtLateLastDay(null);
				}
			}
			
			if(extEarlyLastStr != null) {
				try {
					Date extEarlyLastate = sdfDDD.parse(extEarlyLastStr);
					String extEarlyLastateStr = sdf.format(extEarlyLastate);
					firstDayResult.setExtEarlyLastDay(extEarlyLastateStr);
				} catch (ParseException e) {
					firstDayResult.setExtEarlyLastDay(null);
				}
			}
			//计算距平
			Date yearDateStart = null, yearDateEnd = null, norYearDateStart = null, norYearDateEnd = null;
			try {
				String temp = yearDayResult.getFirstDate();
				if(temp != null && firstDaySDF != null) {
					yearDateStart = sdf.parse(temp);
					norYearDateStart = sdfDDD.parse(firstDaySDF);
					int firstAnomaly = (int) ((yearDateStart.getTime() - norYearDateStart.getTime()) / CommonConstant.DAYTIMES) + 1; 
					if(isLeapYear) {
						firstAnomaly -= 1;
					}
					firstDayResult.setFirstAnomaly(firstAnomaly);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				String temp = yearDayResult.getLastDate();
				if(temp != null && lastDaySDF != null) {
					yearDateEnd = sdf.parse(temp);
					norYearDateEnd = sdfDDD.parse(lastDaySDF);
					int lastAnomaly = (int) ((yearDateEnd.getTime() - norYearDateEnd.getTime()) / CommonConstant.DAYTIMES) + 1; 
					if(isLeapYear) {
						lastAnomaly -= 1;
					}
//					firstDayResult.setNormalLastDate(sdfMMDD.format(norYearDateEnd));
					firstDayResult.setLastAnomaly(lastAnomaly);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				norYearDateEnd = sdfDDD.parse(lastDaySDF);
				norYearDateStart = sdfDDD.parse(firstDaySDF);
				if(isLeapYear) {
					norYearDateEnd = new Date(norYearDateEnd.getTime() + CommonConstant.DAYTIMES);
					norYearDateStart = new Date(norYearDateStart.getTime() + CommonConstant.DAYTIMES);
				}
				firstDayResult.setNormalLastDate(sdfMMDD.format(norYearDateEnd));
				firstDayResult.setNormalFirstDate(sdfMMDD.format(norYearDateStart));
			} catch (Exception e) {
				e.printStackTrace();
			}
			resultFirstDayResultList.add(firstDayResult);
		}
		return resultFirstDayResultList;
	}
	
	private Map<String, FirstDayResult> analystYear(String items, FirstDayParam firstDayParam,  List<Map> yearDataList) {
		Map<String, FirstDayResult> firstDayResultMap = new HashMap<String, FirstDayResult>();
		for(Map map : yearDataList) {
			int year = (Integer) map.get("year");
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			FirstDayResult firstDayResult = new FirstDayResult();
			firstDayResult.setStation_Id_C(station_Id_C);
			firstDayResult.setStation_Name(station_Name);
			firstDayResultMap.put(station_Id_C, firstDayResult);
			String[] itemArray = items.split(",");
			//遍历取初日
			for(int i=0; i<itemArray.length; i++) {
				String item = itemArray[i];
				Double value = (Double) map.get(item);
				value = Eigenvalue.dispose(value);
				if(value == null) {
					continue;
				}
//				if(value == null || value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID ) {
//					continue;
//				}
				if(value >= firstDayParam.getValue()) {
					firstDayResult.setFirstValue(value);
					firstDayResult.setFirstDate(year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6));
					break;
				}
			}
			//遍历取终日
			for(int i=itemArray.length-1; i>=0; i--) {
				String item = itemArray[i];
				Double value = (Double) map.get(item);
				if(value == null || value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID) {
					continue;
				}
				if(value >= firstDayParam.getValue()) {
					firstDayResult.setLastValue(value);
					firstDayResult.setLastDate(year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6));
					break;
				}
			}
		}
		return firstDayResultMap;
	}
	/**
	 * 遍历历史记录
	 * @param items
	 * @param firstDayParam
	 * @param hisDataList
	 * @param yearDataList
	 * @return
	 */
	private Map<String, List<FirstDayResult>> analystHis(String items, FirstDayParam firstDayParam, List<Map> hisDataList) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, List<FirstDayResult>> firstDayResultMap = new HashMap<String, List<FirstDayResult>>();
		for(Map map : hisDataList) {
			int year = (Integer) map.get("year");
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			List<FirstDayResult> firstDayResultList = firstDayResultMap.get(station_Id_C);
			if(firstDayResultList == null || firstDayResultList.size() == 0) {
				firstDayResultList = new ArrayList<FirstDayResult>();
				firstDayResultMap.put(station_Id_C, firstDayResultList);
			}
			FirstDayResult firstDayResult = new FirstDayResult();
			firstDayResult.setStation_Id_C(station_Id_C);
			firstDayResult.setStation_Name(station_Name);
			firstDayResult.setYear(year);
			String[] itemArray = items.split(",");
			//遍历取初日
			for(int i=0; i<itemArray.length; i++) {
				String item = itemArray[i];
				Double value = (Double) map.get(item);
				value = Eigenvalue.dispose(value);
				if(value == null) {
					continue;
				}
//				if(value == null || value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID ) {
//					continue;
//				}
				if(value >= firstDayParam.getValue()) {
					firstDayResult.setFirstValue(value);
					firstDayResult.setFirstDate(year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6));
					break;
				}
			}
			//遍历取终日
			for(int i=itemArray.length-1; i>=0; i--) {
				String item = itemArray[i];
				Double value = (Double) map.get(item);
				value = Eigenvalue.dispose(value);
				if(value == null) {
					continue;
				}
//				if(value == null || value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID ) {
//					continue;
//				}
				if(value >= firstDayParam.getValue()) {
					firstDayResult.setLastValue(value);
					firstDayResult.setLastDate(year + "-" + item.substring(1, 3) + "-" + item.substring(4, 6));
					break;
				}
			}
			firstDayResultList.add(firstDayResult);
		}
		return firstDayResultMap;
	}
}
