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

import com.spd.common.FogResult;
import com.spd.common.FogResultTotal;
import com.spd.common.FogSequenceResult;
import com.spd.common.FogYearsParam;
import com.spd.common.FogYearsResult;
import com.spd.common.MistSequenceResult;
import com.spd.common.TimesParam;
import com.spd.service.IFog;
import com.spd.tool.CommonTool;

/**
 * 雾
 * @author Administrator
 *
 */
public class FogBus {

	public FogResult fogByRange(TimesParam timesParam) {
		FogResult fogResult = new FogResult();
		List<FogSequenceResult> fogSequenceResultList = new ArrayList<FogSequenceResult>();
		List<MistSequenceResult> mistSequenceResultList = new ArrayList<MistSequenceResult>();
		List<FogResultTotal> fogResultTotalList = new ArrayList<FogResultTotal>();
		//1. 查询数据库。
		IFog fogImpl = (IFog)ContextLoader.getCurrentWebApplicationContext().getBean("FogImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> list = fogImpl.queryFogByTimes(paramMap);
		//逐次
		for(int i=0; i<list.size(); i++) {
			LinkedHashMap itemMap = list.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			String datetime = (String) itemMap.get("datetime");
			String area = (String) itemMap.get("area");
			String Fog_OTime = (String) itemMap.get("Fog_OTime");
			Double RHU_Min = (Double) itemMap.get("RHU_Min");
			Double VIS_Min = (Double) itemMap.get("VIS_Min");
			Double Fog = (Double) itemMap.get("Fog");
			Double Mist = (Double) itemMap.get("Mist");
			Double RHU_Avg = (Double) itemMap.get("RHU_Avg");
			if(Fog == 1) {
				FogSequenceResult fogSequenceResult = new FogSequenceResult();
				fogSequenceResult.setStation_Id_C(station_Id_C);
				fogSequenceResult.setStation_Name(station_Name);
				fogSequenceResult.setDatetime(datetime);
				fogSequenceResult.setArea(area);
				fogSequenceResult.setFogTime(Fog_OTime);
				fogSequenceResult.setRhu_Min(RHU_Min);
				fogSequenceResult.setVis_Min(VIS_Min);
				fogSequenceResult.setRhu_Avg(RHU_Avg);
				fogSequenceResult.setFog(Fog);
				fogSequenceResult.setMist(Mist);
				fogSequenceResultList.add(fogSequenceResult);
			}
			if(Mist == 1) {
				MistSequenceResult mistSequenceResult = new MistSequenceResult();
				mistSequenceResult.setStation_Id_C(station_Id_C);
				mistSequenceResult.setStation_Name(station_Name);
				mistSequenceResult.setDatetime(datetime);
				mistSequenceResult.setArea(area);
				mistSequenceResult.setRhu_Min(RHU_Min);
				mistSequenceResult.setVis_Min(VIS_Min);
				mistSequenceResult.setRhu_Avg(RHU_Avg);
				mistSequenceResult.setMist(Mist);
				mistSequenceResultList.add(mistSequenceResult);
			}
		}
		//合计
		Map<String, List<FogSequenceResult>> map = new HashMap<String, List<FogSequenceResult>>();
		for(int i = 0; i < fogSequenceResultList.size(); i++) {
			FogSequenceResult fogSequenceResult = fogSequenceResultList.get(i);
			String station_Id_C = fogSequenceResult.getStation_Id_C();
			List<FogSequenceResult> itemList = null;
			if(map.containsKey(station_Id_C)) {
				itemList = map.get(station_Id_C);
			} else {
				itemList = new ArrayList<FogSequenceResult>();
			}
			itemList.add(fogSequenceResult);
			map.put(station_Id_C, itemList);
		}
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			List<FogSequenceResult> itemList = map.get(key);
			FogResultTotal fogResultTotal = new FogResultTotal();
			fogResultTotal.setStation_Id_C(key);
//			fogResultTotal.setCnt(itemList.size());
			double cnt = 0, mistCnt = 0;
			double visMin = 99999999;
			String visMinDatetime = "";
			for(int i = 0; i < itemList.size(); i++) {
				FogSequenceResult fogSequenceResult = itemList.get(i);
				fogResultTotal.setArea(fogSequenceResult.getArea());
				fogResultTotal.setStation_Name(fogSequenceResult.getStation_Name());
				Double itemVisMin = fogSequenceResult.getVis_Min();
				String itemDatetime = fogSequenceResult.getDatetime();
				double fog = fogSequenceResult.getFog();
				if(fog == 1) {
					cnt++;
				}
				double mist = fogSequenceResult.getMist();
				if(mist == 1) {
					mistCnt++;
				}
				if(itemVisMin != null && itemVisMin < visMin) {
					visMin = itemVisMin;
					visMinDatetime = itemDatetime;
				}
			}
			fogResultTotal.setVis_Min(visMin);
			fogResultTotal.setVis_Min_Time(visMinDatetime);
			fogResultTotal.setCnt(cnt);
			fogResultTotal.setMistCnt(mistCnt);
			fogResultTotalList.add(fogResultTotal);
		}
		fogResult.setFogResultTotalList(fogResultTotalList);
		fogResult.setFogSequenceResultList(fogSequenceResultList);
		fogResult.setMistSequenceResultList(mistSequenceResultList);
		return fogResult;
	}
	
	public List<FogYearsResult> fogByYears(FogYearsParam fogYearsParam) {
		List<FogYearsResult> resultList = new ArrayList<FogYearsResult>();
		//历年
		Map<Integer, List<LinkedHashMap>> overMap = queryFogByTimes(fogYearsParam, fogYearsParam.getStartYear(), fogYearsParam.getEndYear());
		//常年
		Map<Integer, List<LinkedHashMap>> yearsMap = queryFogByTimes(fogYearsParam, fogYearsParam.getPerennialStartYear(), fogYearsParam.getPerennialEndYear());
		//计算常年雾日
		Double fogDays = 0.0, mistDays = 0.0;
		Iterator<Integer> it = yearsMap.keySet().iterator();
		while(it.hasNext()) {
			Integer key = it.next();
			List<LinkedHashMap> list = yearsMap.get(key);
			for(int i = 0; i < list.size(); i++) {
				LinkedHashMap item = list.get(i);
				Double fog = (Double) item.get("Fog");
				Double mist = (Double) item.get("Mist");
				if(fog == 1) {
					fogDays++;
				}
				if(mist == 1) {
					mistDays++;
				}
			}
		}
		fogDays = CommonTool.roundDouble(fogDays / (fogYearsParam.getPerennialEndYear() - fogYearsParam.getPerennialStartYear() + 1));
		mistDays = CommonTool.roundDouble(mistDays / (fogYearsParam.getPerennialEndYear() - fogYearsParam.getPerennialStartYear() + 1));
		for(int i = fogYearsParam.getStartYear(); i <= fogYearsParam.getEndYear(); i++) {
			List<LinkedHashMap> itemList = overMap.get(i);
			if(itemList == null) {
				FogYearsResult fogYearsResult = new FogYearsResult();
				fogYearsResult.setYear(i);
				fogYearsResult.setYearsFogDays(fogDays);
				resultList.add(fogYearsResult);
			} else {
				FogYearsResult fogYearsResult = new FogYearsResult();
				fogYearsResult.setYear(i);
				Double yearFogDays = 0.0, yearMistDays = 0.0;
				for(int j = 0; j < itemList.size(); j++) {
					LinkedHashMap item = itemList.get(j);
					Double fog = (Double) item.get("Fog");
					Double mist = (Double) item.get("Mist");
					if(fog == 1) {
						yearFogDays++;
					}
					if(mist == 1) {
						yearMistDays++;
					}
				}
				fogYearsResult.setFogDays(yearFogDays);
				fogYearsResult.setMistCnt(yearMistDays);
				fogYearsResult.setYearsFogDays(fogDays);
				if(fogDays != 0) {
					fogYearsResult.setAnomalyRate(CommonTool.roundDouble((itemList.size() - fogDays) / fogDays * 100));
				}
				double tempVisMin = 9999999;
				for(int j = 0; j < itemList.size(); j++) {
					LinkedHashMap itemMap = itemList.get(j);
					Double vis_Min = (Double) itemMap.get("VIS_Min");
					if(vis_Min < tempVisMin) {
						tempVisMin = vis_Min;
					}
				}
				fogYearsResult.setVis_Min(tempVisMin);
				resultList.add(fogYearsResult);
			}
		}
		return resultList;
	}
	
	private Map<Integer, List<LinkedHashMap>> queryFogByTimes(FogYearsParam fogYearsParam, int startYear, int endYear) {
		TimesParam timesParam = fogYearsParam.getTimesParam();
		List<String> Station_Id_Cs = new ArrayList<String>();
		String[] station_Id_CArray = fogYearsParam.getStation_Id_Cs();
		for(int i=0; i<station_Id_CArray.length; i++) {
			Station_Id_Cs.add(station_Id_CArray[i]);
		}
		IFog fogImpl = (IFog)ContextLoader.getCurrentWebApplicationContext().getBean("FogImpl");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat mmddSDF = new SimpleDateFormat("MMdd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(timesParam.getStartTimeStr());
			endDate = sdf.parse(timesParam.getEndTimeStr());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		boolean isOverYear = CommonTool.isOverYear(timesParam.getStartMon(), timesParam.getStartDay(), timesParam.getEndMon(), timesParam.getEndDay());
		Map<Integer, List<LinkedHashMap>> map = new HashMap<Integer, List<LinkedHashMap>>(); 
		if(isOverYear) {
			HashMap paramMap = new HashMap();
			String startTimeStr = timesParam.getStartTimeStr();
			int startTimeInt = Integer.parseInt(mmddSDF.format(timesParam.getStartDate()));
			String endTimeStr = timesParam.getEndTimeStr();
			try {
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)) - 1);
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			paramMap.put("startYear", startYear - 1);
			paramMap.put("endYear", endYear - 1);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = fogImpl.queryFogByOverYears(paramMap);
			for(LinkedHashMap itemMap : listResult) {
				Integer year = (Integer) itemMap.get("year");
				String mmdd = (String) itemMap.get("MMDD");
				int mmddInt = Integer.parseInt(mmdd);
				
				List<LinkedHashMap> tempList = null;//map.get(year);
				if(mmddInt >= startTimeInt) { //年底的算在第二年
					tempList = map.get(year + 1);
					if(tempList == null) {
						tempList = new ArrayList<LinkedHashMap>();
					}
					tempList.add(itemMap);
					map.put(year + 1, tempList);
				} else {
					tempList = map.get(year);
					if(tempList == null) {
						tempList = new ArrayList<LinkedHashMap>();
					}
					tempList.add(itemMap);
					map.put(year, tempList);
				}
			}
		} else {
			HashMap paramMap = new HashMap();
			try {
				paramMap.put("startTime", Integer.parseInt(mmddSDF.format(startDate)));
				paramMap.put("endTime", Integer.parseInt(mmddSDF.format(endDate)));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			paramMap.put("startYear", startYear);
			paramMap.put("endYear", endYear);
			paramMap.put("Station_Id_C", Station_Id_Cs);
			List<LinkedHashMap> listResult = fogImpl.queryFogBySameYears(paramMap);
			for(LinkedHashMap itemMap : listResult) {
				Integer year = (Integer) itemMap.get("year");
				List<LinkedHashMap> tempList = map.get(year);
				if(tempList == null) {
					tempList = new ArrayList<LinkedHashMap>();
				}
				tempList.add(itemMap);
				map.put(year, tempList);
			}
		}
		return map;
	}
	
}
