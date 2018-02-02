package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.cimiss.CIMISSRest;
import com.spd.common.HourRain;
import com.spd.dao.cq.impl.AWSHourDataDaoImpl;
import com.spd.dao.cq.impl.CQAWSStation;
import com.spd.hourrain.HourRainBus;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;

/**
 * 小时雨量数据同步
 * @author Administrator
 *
 */
public class HourRainExecutor {

	private static Map<String, String> columnMap = new HashMap<String, String>();
	
	private static int PREHOURS = 3;
	
	public static void init() {
		columnMap.put("Datetime","datetime");
		columnMap.put("Station_Id_C","varchar(20)");
		columnMap.put("PRE_1h","double");
		columnMap.put("PRE_3h","double");
		columnMap.put("PRE_6h","double");
		columnMap.put("PRE_12h","double");
		columnMap.put("PRE_24h","double");
	}
	
	public List syncHourRain(String result, String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CIMISSRest cimissRest = new CIMISSRest();
		AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
//		HashMap<String, Object> existData = awsHourDataDaoImpl.getExistRain(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return null;
		}
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			String datetimeStr = (String) tempMap.get("Datetime");
			//转化成北京时
			Date date = null;
			try {
				date = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
			date = new Date(date.getTime() + 8 * 60 * 60 * 1000);
			datetimeStr = sdf.format(date);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			Double R1 = (Double) tempMap.get("PRE_1h");
			if(R1 > 9999) R1 = 0.0;
			Double R3 = (Double) tempMap.get("PRE_3h");
			if(R3 > 9999) R3 = 0.0;
			Double R6 = (Double) tempMap.get("PRE_6h");
			if(R6 > 9999) R6 = 0.0;
			Double R12 = (Double) tempMap.get("PRE_12h");
			if(R12 > 9999) R12 = 0.0;
			Double R24 = (Double) tempMap.get("PRE_24h");
			if(R24 > 9999) R24 = 0.0;
//			if((R1 != null && R1 !=0) || (R3 != null && R3 !=0) || (R6 != null && R6 !=0) || (R12 != null && R12 !=0) || (R24 != null && R24 !=0)) {
			Map<String, Object> mapData = new HashMap<String, Object>();
//				String key = (String) tempMap.get("Station_Id_C") + "_" + datetimeStr;
//				if(existData.containsKey(key)) {
//					continue;
//				}
			mapData.put("datetime", datetimeStr);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("R1", R1);
			mapData.put("R3", R3);
			mapData.put("R6", R6);
			mapData.put("R12", R12);
			mapData.put("R24", R24);
			dataList.add(mapData);
//			}
		}
//		awsHourDataDaoImpl.insertValue(dataList);
		return dataList;
	}
	
	public static String getCIMISSData(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = null;
		try {
			date = sdf.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		date = new Date(date.getTime() - 8 * 60 * 60 * 1000); //世界时
		timeStr = sdf.format(date);
		String url = "http://10.230.89.17:8008/cimiss-web/api?userId=BECQ_QHZX_byy&pwd=qhzxbyy&interfaceId=getSurfEleInRegionByTime&dataCode=SURF_CHN_MUL_HOR&times=" + timeStr
		+ "&adminCodes=500000&elements=";
		Set<String> keySet = columnMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			url = url + key + ",";
		}
		url += "&staLevels=011,012,013&dataFormat=json";
		System.out.println(url);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	
	public List syncHourRain2(String result, String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CIMISSRest cimissRest = new CIMISSRest();
		AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
		HashMap<String, Object> existData = awsHourDataDaoImpl.getExistRain(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			String datetimeStr = (String) tempMap.get("Datetime");
			//转化成北京时
			Date date = null;
			try {
				date = sdf.parse(datetimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
			date = new Date(date.getTime() + 8 * 60 * 60 * 1000);
			datetimeStr = sdf.format(date);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			Double R1 = (Double) tempMap.get("PRE_1h");
			if((R1 != null && R1 !=0)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				String key = (String) tempMap.get("Station_Id_C") + "_" + datetimeStr;
				if(existData.containsKey(key)) {
					continue;
				}
				mapData.put("datetime", datetimeStr);
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("R1", R1);
				dataList.add(mapData);
			}
		}
		return dataList;
	}
	
	public void dispose(Map<String, List> data, Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
		AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
		String endDateStr = sdf.format(endDate);
		HashMap<String, Object> existData = awsHourDataDaoImpl.getExistRain(endDateStr);
		
		List<HourRain> list = new ArrayList<HourRain>();
		
		List itemList = data.get(endDateStr);
		if(itemList == null) {
			return;
		}
		//R1
		Map<String, HourRain> hourRainMap = new HashMap<String, HourRain>();
		for(int i=0; i<itemList.size(); i++) {
			HourRain hourRain = new HourRain();
			Map<String, Object> mapData = (Map<String, Object>) itemList.get(i);
			String Station_Id_C = (String) mapData.get("Station_Id_C");
			String datetime = (String) mapData.get("datetime");
			hourRain.setStation_Id_C(Station_Id_C);
			hourRain.setDateTime(datetime);
			Double R1 = (Double) mapData.get("R1");
			hourRain.setR1(R1);
			hourRainMap.put(Station_Id_C, hourRain);
//			list.add(hourRain);
		}
		int index = 1;
		Map<String, Double> valueMap = new HashMap<String, Double>();
		for(long i = endDate.getTime(); i >= startDate.getTime(); i -= 60 * 60 * 1000) {
			String timeStr = sdf.format(new Date(i));
			List listi = data.get(timeStr);
			for(int j = 0; j < listi.size(); j++) {
				Map<String, Object> mapData = (Map<String, Object>) listi.get(j);
				String Station_Id_C = (String) mapData.get("Station_Id_C");
				Double R1 = ((Double) mapData.get("R1"));
				Double existR1 = valueMap.get(Station_Id_C);
				if(existR1 == null) {
					valueMap.put(Station_Id_C, R1);
				} else {
					valueMap.put(Station_Id_C, R1 + existR1);
				}
				if(3 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					hourRain.setR3(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} else if(6 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					hourRain.setR6(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} else if(12 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					hourRain.setR12(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} if(24 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					hourRain.setR24(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				}
			}
			index++;
		}
		
		List dataList = new ArrayList();
		Iterator<String> it = hourRainMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			HourRain hourRain = hourRainMap.get(key);
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("Station_Id_C", key);
			mapData.put("datetime", hourRain.getDateTime());
			Double R1 = hourRain.getR1();
			if(R1 != null) {
				R1 = CommonTool.roundDouble(R1);
			}
			Double R3 = hourRain.getR3();
			if(R3 != null) {
				R3 = CommonTool.roundDouble(R3);
			}
			Double R6 = hourRain.getR6();
			if(R6 != null) {
				R6 = CommonTool.roundDouble(R6);
			}
			Double R12 = hourRain.getR12();
			if(R12 != null) {
				R12 = CommonTool.roundDouble(R12);
			}
			Double R24 = hourRain.getR24();
			if(R24 != null) {
				R24 = CommonTool.roundDouble(R24);
			}
			mapData.put("R1", R1);
			mapData.put("R3", R3);
			mapData.put("R6", R6);
			mapData.put("R12", R12);
			mapData.put("R24", R24);
			String existKey = key + "_" + hourRain.getDateTime();
			if(existData.containsKey(existKey)) {
				continue;
			}
			if((R1 == null || R1 ==0) && (R3 == null || R3 ==0) && (R6 == null || R6 ==0)
					&&(R12 == null || R12 ==0) && (R24 == null || R24 ==0)) {
				continue;
			}
			dataList.add(mapData);
		}
		awsHourDataDaoImpl.insertValue(dataList);
	}
	
	/**
	 * 默认往前推算4个小时。
	 */
	public void record(Date date) {
		HourRainBus hourRainBus = new HourRainBus();
		SimpleDateFormat yearSDF = new SimpleDateFormat("yyyy");
		String yearStr = yearSDF.format(date);
		hourRainBus.init(Integer.parseInt(yearStr));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
		
		String timeStr = sdf.format(date); // 当前小时
		Date preDayDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
		Map<String, List> listData = new HashMap<String, List>();
		Map<String, List> awsData = new HashMap<String, List>();
		Map<String, List> mwsData = new HashMap<String, List>();
		for(long i = preDayDate.getTime(); i <= date.getTime(); i+= 60 * 60 * 1000) {
			String itemTimeStr = sdf.format(new Date(i));
			String currentResult = hourRainBus.getCIMISSData(itemTimeStr);
			List data = hourRainBus.syncHourRain(currentResult, itemTimeStr);
			if(data == null) {
				return;
			}
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> mwsDataList = new ArrayList<Map<String, Object>>();
			for(int j = 0; j < data.size(); j++) {
				Map<String, Object> mapData = (Map<String, Object>) data.get(j);
				String Station_Id_C = (String) mapData.get("Station_Id_C");
				boolean isCQStation = CQAWSStation.isCQStation(Station_Id_C);
				if(!isCQStation) continue;
				if(Station_Id_C.startsWith("5")) {
					dataList.add(mapData);
				} else if(Station_Id_C.startsWith("A")) {
					mwsDataList.add(mapData);
				}
			}
			awsData.put(itemTimeStr, dataList);
			mwsData.put(itemTimeStr, mwsDataList);
		}
		
		hourRainBus.dispose(awsData, preDayDate, date, "aws");
		hourRainBus.dispose(mwsData, preDayDate, date, "mws");
		hourRainBus.dispose(mwsData, preDayDate, date, "mwsyears");
	}
	
	public void sync() {
		
	}
	
	public static void main(String[] args) {
		
//		PropertiesUtil.loadSysCofing();
//		HourRainExecutor hourRainExecutor = new HourRainExecutor();
//		hourRainExecutor.init();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
//		Date date = new Date();
//		String timeStr = sdf.format(date); // 当前小时
//		Date preDayDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
//		Map<String, List> listData = new HashMap<String, List>();
//		for(long i = preDayDate.getTime(); i <= date.getTime(); i+= 60 * 60 * 1000) {
//			String itemTimeStr = sdf.format(new Date(i));
//			String currentResult = hourRainExecutor.getCIMISSData(itemTimeStr);
//			List data = hourRainExecutor.syncHourRain(currentResult, itemTimeStr);
//			if(data == null) {
//				return;
//			}
//			listData.put(itemTimeStr, data);
//		}
//		hourRainExecutor.dispose(listData, preDayDate, date);
//		LogTool.logger.info(timeStr);
		//////////
		PropertiesUtil.loadSysCofing();
		//默认往前推算4个小时
		HourRainExecutor hourRainExecutor = new HourRainExecutor();
		Date currentDate = new Date();
		Date preDate = new Date(currentDate.getTime() - PREHOURS * 60 * 60 * 1000);
		for(long i = preDate.getTime(); i <= currentDate.getTime(); i += 60 * 60 * 1000) {
			hourRainExecutor.record(new Date(i));
		}
//		HourRainBus hourRainBus = new HourRainBus();
//		Date date = new Date();
//		SimpleDateFormat yearSDF = new SimpleDateFormat("yyyy");
//		String yearStr = yearSDF.format(date);
//		hourRainBus.init(Integer.parseInt(yearStr));
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
//		
//		String timeStr = sdf.format(date); // 当前小时
//		Date preDayDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
//		Map<String, List> listData = new HashMap<String, List>();
//		Map<String, List> awsData = new HashMap<String, List>();
//		Map<String, List> mwsData = new HashMap<String, List>();
//		for(long i = preDayDate.getTime(); i <= date.getTime(); i+= 60 * 60 * 1000) {
//			String itemTimeStr = sdf.format(new Date(i));
//			String currentResult = hourRainBus.getCIMISSData(itemTimeStr);
//			List data = hourRainBus.syncHourRain(currentResult, itemTimeStr);
//			if(data == null) {
//				return;
//			}
//			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
//			List<Map<String, Object>> mwsDataList = new ArrayList<Map<String, Object>>();
//			for(int j = 0; j < data.size(); j++) {
//				Map<String, Object> mapData = (Map<String, Object>) data.get(j);
//				String Station_Id_C = (String) mapData.get("Station_Id_C");
//				if(Station_Id_C.startsWith("5")) {
//					dataList.add(mapData);
//				} else if(Station_Id_C.startsWith("A")) {
//					mwsDataList.add(mapData);
//				}
//			}
//			awsData.put(itemTimeStr, dataList);
//			mwsData.put(itemTimeStr, mwsDataList);
//		}
//		
//		hourRainBus.dispose(awsData, preDayDate, date, "aws");
//		hourRainBus.dispose(mwsData, preDayDate, date, "mws");
//		hourRainBus.dispose(mwsData, preDayDate, date, "mwsyears");
		/////////
//		String preTimeStr = sdf.format(new Date(date.getTime() - 60 * 60 * 1000));//上一小时
//		String preResult = hourRainExecutor.getCIMISSData(preTimeStr);
//		hourRainExecutor.syncHourRain(preResult, preTimeStr);
//		LogTool.logger.info(preTimeStr);
		//补录
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		String startTimeStr = "20160511060000";
//		String endTimeStr = "20160511140000";
//		Date startDate = null, endDate = null;
//		try {
//			startDate = sdf.parse(startTimeStr);
//			endDate = sdf.parse(endTimeStr);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		for(long i = startDate.getTime(); i <= endDate.getTime(); i += 60 * 60 * 1000) {
//			String timeStr = sdf.format(new Date(i));
//			LogTool.logger.info(timeStr);
//			String currentResult = hourRainExecutor.getCIMISSData(timeStr);
//			hourRainExecutor.syncHourRain(currentResult, timeStr);
//		}
	}

}
