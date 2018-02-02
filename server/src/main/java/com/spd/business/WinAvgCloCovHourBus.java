package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CloCovParam;
import com.spd.common.CloCovResult;
import com.spd.common.TimesParam;
import com.spd.common.WinAvg2MinParam;
import com.spd.common.WinAvg2MinResult;
import com.spd.service.IWinAvgColcov;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 小时的瞬时风、云量
 * @author Administrator
 *
 */
public class WinAvgCloCovHourBus {
	
	private HashMap winMap;
	
	public void init() {
		if(winMap == null) {
			winMap = new HashMap();
			winMap.put("NNE", "11.25,33.75");
			winMap.put("NE", "33.75,56.25");
			winMap.put("ENE", "56.25,78.75");
			winMap.put("E", "78.75,101.25");
			winMap.put("ESE", "101.25,123.75");
			winMap.put("SE", "123.75,146.25");
			winMap.put("SSE", "146.25,168.75");
			winMap.put("S", "168.75,191.25");
			winMap.put("SSW", "191.25,213.75");
			winMap.put("SW", "213.75,236.25");
			winMap.put("WSW", "236.25,258.75");
			winMap.put("W", "258.75,281.25");
			winMap.put("WNW", "281.25,303.75");
			winMap.put("NW", "303.75,326.25");
			winMap.put("N", "326.25,11.25");
		}
	}
	
	public List<WinAvg2MinResult> queryWinAvg2MinByTimeRange(WinAvg2MinParam winAvg2MinParam) {
		List<WinAvg2MinResult> winAvg2MinResultList = new ArrayList<WinAvg2MinResult>();
		//1. 查询数据库
		TimesParam timesParam = winAvg2MinParam.getTimesParam();
		IWinAvgColcov winAvgColcovImpl = (IWinAvgColcov)ContextLoader.getCurrentWebApplicationContext().getBean("WinAvgColcovImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("StationType", winAvg2MinParam.getStationType());
		List<LinkedHashMap> list = winAvgColcovImpl.queryWinAvg2MinByTimeRange(paramMap);
		
		HashMap<String, List<LinkedHashMap>> resultMap = new HashMap<String, List<LinkedHashMap>>();
		//key:station_Id_C datetime
		for(int i = 0; i < list.size(); i++) {
			LinkedHashMap itemMap = list.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String datetime = (String) itemMap.get("datetime");
			String key = station_Id_C + "_" + datetime;
			List<LinkedHashMap> itemList = resultMap.get(key);
			if(itemList == null) {
				itemList = new ArrayList<LinkedHashMap>();
			}
			itemList.add(itemMap);
			resultMap.put(key, itemList);
		}
		
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> itemList = resultMap.get(key);
			WinAvg2MinResult winAvg2MinResult = new WinAvg2MinResult();
			for(int i = 0; i < itemList.size(); i++) {
				LinkedHashMap itemMap = itemList.get(i);
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				winAvg2MinResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				winAvg2MinResult.setStation_Id_C(station_Id_C);
				String datetime = (String) itemMap.get("datetime");
				winAvg2MinResult.setDatetime(datetime);
				int win_D_Avg_2mi = (Integer) itemMap.get("WIN_D_Avg_2mi");
				Double win_S_Avg_2mi = (Double) itemMap.get("WIN_S_Avg_2mi");
				int hours = (Integer) itemMap.get("Hours");
				if(hours == 2) {
					winAvg2MinResult.setWinDAvg1(win_D_Avg_2mi);
					winAvg2MinResult.setWinSAvg1(win_S_Avg_2mi);
					winAvg2MinResult.setWinDCode1(transWinD(win_D_Avg_2mi));
				} else if(hours == 8) {
					winAvg2MinResult.setWinDAvg2(win_D_Avg_2mi);
					winAvg2MinResult.setWinSAvg2(win_S_Avg_2mi);
					winAvg2MinResult.setWinDCode2(transWinD(win_D_Avg_2mi));
				} else if(hours == 14) {
					winAvg2MinResult.setWinDAvg3(win_D_Avg_2mi);
					winAvg2MinResult.setWinSAvg3(win_S_Avg_2mi);
					winAvg2MinResult.setWinDCode3(transWinD(win_D_Avg_2mi));
				} else if(hours == 20) {
					winAvg2MinResult.setWinDAvg4(win_D_Avg_2mi);
					winAvg2MinResult.setWinSAvg4(win_S_Avg_2mi);
					winAvg2MinResult.setWinDCode4(transWinD(win_D_Avg_2mi));
				}
			}
			winAvg2MinResultList.add(winAvg2MinResult);
		}
		return winAvg2MinResultList;
	}
	
	public List<CloCovResult> queryCloCovByTimeRange(CloCovParam cloCovParam) {
		List<CloCovResult> cloCovResultList = new ArrayList<CloCovResult>();
		//1. 查询数据库
		TimesParam timesParam = cloCovParam.getTimesParam();
		IWinAvgColcov winAvgColcovImpl = (IWinAvgColcov)ContextLoader.getCurrentWebApplicationContext().getBean("WinAvgColcovImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		paramMap.put("StationType", cloCovParam.getStationType());
		List<LinkedHashMap> list = winAvgColcovImpl.queryCloCovByTimeRange(paramMap);
		HashMap<String, List<LinkedHashMap>> resultMap = new HashMap<String, List<LinkedHashMap>>();
		//key:station_Id_C datetime
		for(int i = 0; i < list.size(); i++) {
			LinkedHashMap itemMap = list.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String datetime = (String) itemMap.get("datetime");
			String key = station_Id_C + "_" + datetime;
			List<LinkedHashMap> itemList = resultMap.get(key);
			if(itemList == null) {
				itemList = new ArrayList<LinkedHashMap>();
			}
			itemList.add(itemMap);
			resultMap.put(key, itemList);
		}
		//2. 计算平均值
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<LinkedHashMap> itemList = resultMap.get(key);
			CloCovResult cloCovResult = new CloCovResult();
			int cloCovCnt = 0, cloCovLowCnt = 0, cloCovSum = 0, cloCovLowSum = 0;
			String station_Id_C = "";
			for(int i = 0; i < itemList.size(); i++) {
				LinkedHashMap item = itemList.get(i);
				Integer clo_Cov = (Integer) item.get("CLO_Cov");
				String datetime = (String) item.get("datetime");
				cloCovResult.setDatetime(datetime);
				Integer clo_Cov_Low = (Integer) item.get("CLO_Cov_Low");
				Integer hours = (Integer) item.get("Hours");
				if(hours == 2) {
					cloCovResult.setCloCov1(clo_Cov);
					cloCovResult.setCloCovLow1(clo_Cov_Low);
				} else if(hours == 8) {
					cloCovResult.setCloCov2(clo_Cov);
					cloCovResult.setCloCovLow2(clo_Cov_Low);
				} else if(hours == 14) {
					cloCovResult.setCloCov3(clo_Cov);
					cloCovResult.setCloCovLow3(clo_Cov_Low);
				} else if(hours == 20) {
					cloCovResult.setCloCov4(clo_Cov);
					cloCovResult.setCloCovLow4(clo_Cov_Low);
				}
				station_Id_C = (String) item.get("Station_Id_C");
				if(clo_Cov != 999999) {
					cloCovSum += clo_Cov;
					cloCovCnt++;
				}
				if(clo_Cov_Low != 999999) {
					cloCovLowSum += clo_Cov_Low;
					cloCovLowCnt++;
				}
			}
			cloCovResult.setStation_Id_C(station_Id_C);
			cloCovResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			if(cloCovCnt != 0) {
				cloCovResult.setAvgCloCov(CommonTool.roundDouble(cloCovSum / cloCovCnt));
			}
			if(cloCovLowCnt != 0) {
				cloCovResult.setAvgCloCovLow(CommonTool.roundDouble(cloCovLowSum / cloCovLowCnt));
			}
			cloCovResultList.add(cloCovResult);
		}
		return cloCovResultList;
	}
	
	private String transWinD(int winD) {
		init();
		Iterator<String> it = winMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = (String) winMap.get(key);
			Double start = Double.parseDouble(value.split(",")[0]);
			Double end = Double.parseDouble(value.split(",")[1]);
			if("N".equals(key)) {
				if(winD >= start || winD <= end) {
					return key;
				}
			} else {
				if(winD >= start && winD <= end) {
					return key;
				}
			}
			
		}
		return "";
	}
}
