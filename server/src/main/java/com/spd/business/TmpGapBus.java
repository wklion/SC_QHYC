package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.TimesParam;
import com.spd.common.TmpGapByYearsResult;
import com.spd.common.TmpGapTimesParam;
import com.spd.common.TmpGapTimesResult;
import com.spd.pojo.TmpGapAvgItem;
import com.spd.pojo.TmpGapAvgYearResult;
import com.spd.service.ITmpGap;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 气温日较差，年较差
 * @author Administrator
 *
 */
public class TmpGapBus {

	/**
	 * 计算日较差
	 * @param tmpGapTimesParam
	 * @return
	 */
	public List<TmpGapAvgItem> getTmpByTimes(TmpGapTimesParam tmpGapTimesParam) {
		//1. 查询指定时间段的结果
		TimesParam timesParam = tmpGapTimesParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		int startYear = Integer.parseInt(startTimeStr.substring(0, 4));
		int endYear = Integer.parseInt(endTimeStr.substring(0, 4));
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTimeStr);
		paramMap.put("endTime", endTimeStr);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("StationType", tmpGapTimesParam.getStationType());
		String columns = CommonTool.createItemStrByRange(timesParam.getStartDate(), timesParam.getEndDate());
		paramMap.put("items", columns);
		ITmpGap tmpGap = (ITmpGap)ContextLoader.getCurrentWebApplicationContext().getBean("TmpGapImpl");
		List<Map> gapTmpList = tmpGap.getGapTmpByTimes(paramMap);
		
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		List<TmpGapAvgItem> tmpGapAvgList = resultDataDispose.avgTmpGapDis(gapTmpList, timesParam.getStartDate(), timesParam.getEndDate());
		return tmpGapAvgList;
	}
	
	/**
	 * 计算历年日较差
	 * @param tmpGapTimesParam
	 * @return
	 */
	public List<TmpGapAvgItem> getTmpByYear(TmpGapTimesParam tmpGapTimesParam) {
		TimesParam timesParam = tmpGapTimesParam.getTimesParam();
		String startTimeStr = timesParam.getStartTimeStr();
		String endTimeStr = timesParam.getEndTimeStr();
		int startYear = Integer.parseInt(startTimeStr.substring(0, 4));
		int endYear = Integer.parseInt(endTimeStr.substring(0, 4));
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTimeStr);
		paramMap.put("endTime", endTimeStr);
		paramMap.put("startYear", tmpGapTimesParam.getStartYear());
		paramMap.put("endYear", tmpGapTimesParam.getEndYear());
		paramMap.put("StationType", tmpGapTimesParam.getStationType());
		String columns = CommonTool.createItemStrByRange(timesParam.getStartDate(), timesParam.getEndDate());
		paramMap.put("items", columns);
		ITmpGap tmpGap = (ITmpGap)ContextLoader.getCurrentWebApplicationContext().getBean("TmpGapImpl");
		List<Map> gapTmpList = tmpGap.getGapTmpByYears(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		List<TmpGapAvgItem> yearsList = resultDataDispose.tmpGapMulYearDis(gapTmpList);
		return yearsList;
	}
	
	public List<TmpGapTimesResult> compareTmpGaps(List<TmpGapAvgItem> tmpGapAvgList, List<TmpGapAvgItem> compareTmpGapAvgList) {
		List<TmpGapTimesResult> resultList = new ArrayList<TmpGapTimesResult>();
		//对比结果
		if(tmpGapAvgList == null || tmpGapAvgList.size() == 0 || 
				compareTmpGapAvgList == null || compareTmpGapAvgList.size() == 0) {
			return resultList;
		}
		for(int i = 0; i < tmpGapAvgList.size(); i++) {
			TmpGapAvgItem iTmpGapAvgItem = tmpGapAvgList.get(i);
			String iStation_Id_C = iTmpGapAvgItem.getStation_Id_C();
			Double iTemGap = iTmpGapAvgItem.getTEM_Gap();
			for(int j = 0; j < compareTmpGapAvgList.size(); j++) {
				TmpGapAvgItem jTmpGapAvgItem = compareTmpGapAvgList.get(j);
				String jStation_Id_C = jTmpGapAvgItem.getStation_Id_C();
				if(iStation_Id_C.equals(jStation_Id_C)) {
					Double jTemGap = jTmpGapAvgItem.getTEM_Gap();
					TmpGapTimesResult tmpGapTimesResult = new TmpGapTimesResult();
					tmpGapTimesResult.setStation_Id_C(iStation_Id_C);
					tmpGapTimesResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(iStation_Id_C));
					tmpGapTimesResult.setArea(CommonUtil.getInstance().stationAreaMap.get(iStation_Id_C));
					tmpGapTimesResult.setTmpGap(CommonTool.roundDouble(iTemGap));
					tmpGapTimesResult.setContrastTmpGap(CommonTool.roundDouble(jTemGap));
					tmpGapTimesResult.setAnomaly(CommonTool.roundDouble(iTemGap - jTemGap));
					resultList.add(tmpGapTimesResult);
					break;
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 计算年较差
	 * @param year
	 * @param stationType
	 * @return
	 */
	public List<TmpGapAvgYearResult> getTmpByYear(int year, String stationType) {
		//1. 构造参数，查询
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", year);
		paramMap.put("StationType", stationType);
		ITmpGap tmpGap = (ITmpGap)ContextLoader.getCurrentWebApplicationContext().getBean("TmpGapImpl");
		List<Map> resultList = tmpGap.getTmpByYear(paramMap);
		//2. 转换成结果类
		List<TmpGapAvgYearResult> tmpGapAvgYearResultList = dispose(resultList);
		//3. 统计查询结果，如果有月份值是一样的，则都显示出来
		tmpGapAvgYearResultList = merge(tmpGapAvgYearResultList);
		return tmpGapAvgYearResultList;
	}
	
	/**
	 * 合并，当有结果月份相同的时候
	 * @param dataList
	 * @return
	 */
	private List<TmpGapAvgYearResult> merge(List<TmpGapAvgYearResult> dataList) {
		List<TmpGapAvgYearResult> resultList = new ArrayList<TmpGapAvgYearResult>();
		for(int i = 0; i < dataList.size() - 1; i++) {
			TmpGapAvgYearResult iItem = dataList.get(i);
			String iStation_Id_C = iItem.getStation_Id_C();
			String iMaxMonth = iItem.getMaxMonth();
			String iMinMonth = iItem.getMinMonth();
			for(int j = i + 1; j < dataList.size(); j++) {
				TmpGapAvgYearResult jItem = dataList.get(j);
				String jStation_Id_C = jItem.getStation_Id_C();
				if(jStation_Id_C.equals(iStation_Id_C)) {
					String jMaxMonth = jItem.getMaxMonth();
					String jMinMonth = jItem.getMinMonth();
					//去掉重复的情况
					if(!iMaxMonth.equals(jMaxMonth)) {
						boolean flag = false;
						String[] tempMaxMonth = iMaxMonth.split(",");
						for(int k = 0; k < tempMaxMonth.length; k++) {
							if(tempMaxMonth[k].equals(jMaxMonth)) {
								flag = true;
								break;
							}
						}
						if(!flag) {
							iMaxMonth += ",";
							iMaxMonth += jMaxMonth;
						}
					}
					if(!iMinMonth.equals(jMinMonth)) {
						boolean flag = false;
						String[] tempMinMonth = iMinMonth.split(",");
						for(int k = 0; k < tempMinMonth.length; k++) {
							if(tempMinMonth[k].equals(jMinMonth)) {
								flag = true;
								break;
							}
						}
						if(!flag) {
							iMinMonth += ",";
							iMinMonth += jMinMonth;
						}
					}
					i++;
				} else {
					break;
				}
			}
			TmpGapAvgYearResult item = new TmpGapAvgYearResult();
			item.setMaxMonth(iMaxMonth);
			item.setMaxMonthAvgTmp(iItem.getMaxMonthAvgTmp());
			item.setMinMonth(iMinMonth);
			item.setMinMonthAvgTmp(iItem.getMinMonthAvgTmp());
			item.setMonthTmpGap(iItem.getMonthTmpGap());
			item.setStation_Id_C(iItem.getStation_Id_C());
			item.setStation_Name(iItem.getStation_Name());
			item.setYear(iItem.getYear());
			item.setMonthTmpGap(iItem.getMonthTmpGap());
			resultList.add(item);
		}
		return resultList;
	}
	
	private List<TmpGapAvgYearResult> dispose(List<Map> dataList) {
		List<TmpGapAvgYearResult> resultList = new ArrayList<TmpGapAvgYearResult>();
		if(dataList == null || dataList.size() == 0) return resultList;
		for(int i = 0; i < dataList.size(); i++) {
			TmpGapAvgYearResult tmpGapAvgYearResult = new TmpGapAvgYearResult();
			Map dataMap = dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			tmpGapAvgYearResult.setStation_Id_C(station_Id_C);
			String station_Name = (String) dataMap.get("Station_Name");
			tmpGapAvgYearResult.setStation_Name(station_Name);
			int year = (Integer) dataMap.get("year");
			tmpGapAvgYearResult.setYear(year);
			Double maxAvgTmp = (Double) dataMap.get("maxAvgTmp");
			tmpGapAvgYearResult.setMaxMonthAvgTmp(maxAvgTmp);
			Double minAvgTmp = (Double) dataMap.get("minAvgTmp");
			tmpGapAvgYearResult.setMinMonthAvgTmp(minAvgTmp);
			int maxMonth = (Integer) dataMap.get("maxMonth");
			tmpGapAvgYearResult.setMaxMonth(maxMonth + "");
			int minMonth = (Integer) dataMap.get("minMonth");
			tmpGapAvgYearResult.setMinMonth(minMonth + "");
			tmpGapAvgYearResult.setMonthTmpGap(maxAvgTmp - minAvgTmp);
			resultList.add(tmpGapAvgYearResult);
		}
		return resultList;
	}
	
	/**
	 * 计算历年年较差
	 * @return
	 */
	public List<TmpGapByYearsResult> getTmpGapByYears(int startYear, int endYear, int standardStartYear, int standardEndYear, String station_Id_C) {
		ITmpGap tmpGap = (ITmpGap)ContextLoader.getCurrentWebApplicationContext().getBean("TmpGapImpl");
		//1. 查询历年的结果
		HashMap paramMap = new HashMap();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("Station_Id_C", station_Id_C);
		List<Map> yearsResultList = tmpGap.getTmpGapByYears(paramMap);
		//2. 查询常年结果
		HashMap standardParamMap = new HashMap();
		standardParamMap.put("startYear", standardStartYear);
		standardParamMap.put("endYear", standardEndYear);
		standardParamMap.put("Station_Id_C", station_Id_C);
		List<Map> avgGapResultList = tmpGap.getAvgTmpGapByYears(standardParamMap);
		//3. 对比结果值，组装结果
		if(yearsResultList == null || yearsResultList.size() == 0 || avgGapResultList == null || avgGapResultList.size() == 0) return null;
		Double avgValue = CommonTool.roundDouble((Double) avgGapResultList.get(0).get("value"));
		List<TmpGapByYearsResult> tmpGapByYearsResultList = new ArrayList<TmpGapByYearsResult>();
		for(int i = 0; i < yearsResultList.size(); i++) {
			Map yearsResultMap = yearsResultList.get(i);
			Double value = CommonTool.roundDouble((Double) yearsResultMap.get("value"));
			Integer year = (Integer) yearsResultMap.get("year");
			TmpGapByYearsResult tmpGapByYearsResult = new TmpGapByYearsResult();
			tmpGapByYearsResult.setYear(year);
			tmpGapByYearsResult.setValue(value);
			Double anomaly = CommonTool.roundDouble(value - avgValue);
			Double anomalyRate = CommonTool.roundDouble(anomaly / avgValue * 100);
			tmpGapByYearsResult.setAnomaly(anomaly);
			tmpGapByYearsResult.setAnomalyRate(anomalyRate);
			tmpGapByYearsResult.setAvgValue(avgValue);
			tmpGapByYearsResultList.add(tmpGapByYearsResult);
		}
		
		return tmpGapByYearsResultList;
	}
}
