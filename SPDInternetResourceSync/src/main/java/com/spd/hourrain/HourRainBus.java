package com.spd.hourrain;

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
import com.spd.dao.cq.impl.MWSHourDataDaoImpl;
import com.spd.dao.cq.impl.MWSYearHourDataDaoImpl;
import com.spd.tool.CommonTool;

public class HourRainBus {

	private static Map<String, String> columnMap = new HashMap<String, String>();
	
	private static AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();

	private static MWSHourDataDaoImpl mwsHourDataDaoImpl = new MWSHourDataDaoImpl();
	
	private static MWSYearHourDataDaoImpl mwsYearHourDataDaoImpl = new MWSYearHourDataDaoImpl();
	
	public static void init(int year) {
		columnMap.put("Datetime","datetime");
		columnMap.put("Station_Id_C","varchar(20)");
		columnMap.put("PRE_1h","double");
		columnMap.put("PRE_3h","double");
		columnMap.put("PRE_6h","double");
		columnMap.put("PRE_12h","double");
		columnMap.put("PRE_24h","double");
		mwsYearHourDataDaoImpl.init(year);
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
		url += "&staLevels=011,012,013,014&dataFormat=json";
		System.out.println(url);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
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
	
	public void dispose(Map<String, List> data, Date startDate, Date endDate, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH0000");
		String endDateStr = sdf.format(endDate);
//		HashMap<String, Object> existData = awsHourDataDaoImpl.getExistRain(endDateStr);
		HashMap<String, Object> existData = null;
		if("aws".equals(type)) {
			existData = awsHourDataDaoImpl.getExistRain(endDateStr);
		} else if("mws".equals(type)) {
			existData = mwsHourDataDaoImpl.getExistRain(endDateStr);
		} else if("mwsyears".equals(type)) {
			existData = mwsYearHourDataDaoImpl.getExistRain(endDateStr);
		}
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
					if(hourRain == null) continue;
					hourRain.setR3(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} else if(6 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					if(hourRain == null) continue;
					hourRain.setR6(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} else if(12 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					if(hourRain == null) continue;
					hourRain.setR12(valueMap.get(Station_Id_C));
					hourRainMap.put(Station_Id_C, hourRain);
				} if(24 == index) {
					HourRain hourRain = hourRainMap.get(Station_Id_C);
					if(hourRain == null) continue;
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
		if("aws".equals(type)) {
			awsHourDataDaoImpl.insertValue(dataList);
		} else if("mws".equals(type)) {
			mwsHourDataDaoImpl.insertValue(dataList);
		} else if("mwsyears".equals(type)) {
			mwsYearHourDataDaoImpl.insertValue(dataList);
		}
	}
}
