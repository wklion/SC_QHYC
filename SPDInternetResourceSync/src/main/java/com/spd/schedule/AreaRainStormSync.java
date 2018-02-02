package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.cq.impl.T_RainStormAreaDaoImpl;
import com.spd.dao.cq.impl.T_RainStormDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 区域暴雨同步，分两个，一个是08-08，一个是20-20
 * @author Administrator
 *
 */
public class AreaRainStormSync {

	//达到标准要满足的的指定的区县数目
	public static int STDCNT = 6;
	//旧的指标中，一天内满足暴雨条件的站数
	public static int AREACNT = 4;
	
	/**
	 * 按新的方式统计区域暴雨
	 * @param datetimeStr
	 */
	
	public Object[] syncRainStormAreaNew(String datetimeStr, String tableName) {
//		String[] tableName = new String[]{"t_rainstorm2020", "t_rainstorm0808"};
		T_RainStormDaoImpl rainStormDaoImpl = new T_RainStormDaoImpl(tableName);
		List resultList = rainStormDaoImpl.getRainStormByTime(datetimeStr, "ALL");
		HashMap<String, List<String>> stationMap = rainStormDaoImpl.getRainStationGroupByArea(); //地区对应的雨量站集合
		HashMap<String, Integer> rainStormCnt = new HashMap<String, Integer>(); // 保留结果，区域对应的达到暴雨的数目
		HashMap<String, Double> rainStormMaxMap = new HashMap<String, Double>(); // 保留结果，区域对应的暴雨的最大值
		for(int i = 0; i < resultList.size(); i++) {
			HashMap itemMap = (HashMap) resultList.get(i);
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
			Double Pre = (Double) itemMap.get("Pre");
			Iterator<String> it = stationMap.keySet().iterator();
			boolean flag = false;
			while(it.hasNext()) {
				String areaStation_Id_C = it.next();
				List<String> itemStation_Id_Cs = stationMap.get(areaStation_Id_C);
				for(int j = 0; j < itemStation_Id_Cs.size(); j++) {
					String itemStation_Id_C = itemStation_Id_Cs.get(j);
					if(itemStation_Id_C.equals(Station_Id_C)) {
						Integer cnt = rainStormCnt.get(areaStation_Id_C);
						if(cnt == null) {
							cnt = 0;
						}
						cnt++;
						rainStormCnt.put(areaStation_Id_C, cnt);
						Double rainStormMax = rainStormMaxMap.get(areaStation_Id_C);
						if(rainStormMax == null) {
							rainStormMax = 0.0;
						}
						if(rainStormMax < Pre) {
							rainStormMaxMap.put(areaStation_Id_C, Pre);
						}
						flag = true;
						break;
					}
				}
				if(flag) {
					break;
				}
			}
		}
		return new Object[]{rainStormCnt, rainStormMaxMap};
		//查询区县雨量站数基数
//		HashMap<String, Integer> rainStationStdMap = rainStormDaoImpl.getRainStationStandard();
//		Iterator<String> it = rainStormCnt.keySet().iterator();
//		int resultCnt = 0;
//		while(it.hasNext()) {
//			String station_Id_C = it.next();
//			Integer currentCnt = rainStormCnt.get(station_Id_C);
//			Integer stdCnt = rainStationStdMap.get(station_Id_C);
//			if(currentCnt >= stdCnt) {
//				resultCnt += 1;
//			}
//		}
//		
//		if(resultCnt >= STDCNT) { 
//			//除了当前日期的结果入库，还要继续往前追溯，找到符合条件的
//			//入库 t_rainstormarea
////			List dataList = new ArrayList();
////			Iterator<String> it2 = rainStormCnt.keySet().iterator();
////			while(it2.hasNext()) {
////				String key = it2.next();
////				HashMap dataMap = new HashMap();
////				dataMap.put("Station_Id_C", key);
////				dataMap.put("datetime", datetimeStr + " 00:00:00");
////				dataMap.put("type", type);
////				dataMap.put("extPre", rainStormMaxMap.get(key));
////				dataList.add(dataMap);
////			}
////			
////			T_RainStormAreaDaoImpl rainStormAreaDaoImpl = new T_RainStormAreaDaoImpl();
////			rainStormAreaDaoImpl.insert(dataList, datetimeStr, type);
//			addDataList(datetimeStr, rainStormCnt, rainStormMaxMap, type);
//		} 
	}
	
	public void analyst(Object[] obj, String tableName, String datetimeStr){
		T_RainStormDaoImpl rainStormDaoImpl = new T_RainStormDaoImpl(tableName);
		String type = "";
		if(tableName.equals("t_rainstorm2020")) {
			type =  "2020";
		} else if(tableName.equals("t_rainstorm0808")) {
			type =  "0808";
		}
		HashMap<String, Integer> rainStormCnt = (HashMap<String, Integer>) obj[0];
		HashMap<String, Double> rainStormMaxMap = (HashMap<String, Double>) obj[1];
		//中间结果
		HashMap<String, Integer> rainStormCnt2 = new HashMap<String, Integer>();
		HashMap<String, Double> rainStormMaxMap2 = new HashMap<String, Double>();
		HashMap<String, Integer> rainStationStdMap = rainStormDaoImpl.getRainStationStandard();
		Iterator<String> it = rainStormCnt.keySet().iterator();
		int resultCnt = 0;
		while(it.hasNext()) {
			String station_Id_C = it.next();
			Integer currentCnt = rainStormCnt.get(station_Id_C);
			Integer stdCnt = rainStationStdMap.get(station_Id_C);
			if(currentCnt >= stdCnt) {
				resultCnt += 1;
				rainStormCnt2.put(station_Id_C, rainStormCnt.get(station_Id_C));
				rainStormMaxMap2.put(station_Id_C, rainStormMaxMap.get(station_Id_C));
			}
		}
		
		if(resultCnt >= STDCNT) {
			//当前日期满足条件,当前的入库，然后依次往前追溯，找到全部符合条件的
			addDataList(datetimeStr, rainStormCnt2, rainStormMaxMap2, type);
			String preDateTimeStr = datetimeStr;
			while(true) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date date = sdf.parse(preDateTimeStr);
					Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
					preDateTimeStr = sdf.format(preDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Object[] objItem = syncRainStormAreaNew(preDateTimeStr, tableName);
				//
				HashMap<String, Integer> itemRainStormCnt = (HashMap<String, Integer>) objItem[0];
				HashMap<String, Double> itemRainStormMaxMap = (HashMap<String, Double>) objItem[1];
				//中间结果
				HashMap<String, Integer> itemRainStormCnt2 = new HashMap<String, Integer>();
				HashMap<String, Double> itemRainStormMaxMap2 = new HashMap<String, Double>();
				Iterator<String> it2 = itemRainStormCnt.keySet().iterator();
				int resultCnt2 = 0;
				boolean flag = false;
				while(it2.hasNext()) {
					String station_Id_C = it2.next();
					Integer currentCnt = itemRainStormCnt.get(station_Id_C);
					Integer stdCnt = rainStationStdMap.get(station_Id_C);
					if(currentCnt >= stdCnt) {
						resultCnt2 += 1;
						itemRainStormCnt2.put(station_Id_C, itemRainStormCnt.get(station_Id_C));
						itemRainStormMaxMap2.put(station_Id_C, itemRainStormMaxMap.get(station_Id_C));
						flag = true;
					}
				}
				addDataList(preDateTimeStr, itemRainStormCnt2, itemRainStormMaxMap2, type);
				if(!flag) {
					break;
				}
				//
			}
		} else if(resultCnt > 0 && resultCnt < STDCNT) {
			//判断头一天是否满足条件
			T_RainStormAreaDaoImpl rainStormAreaDaoImpl = new T_RainStormAreaDaoImpl();
			String preDateTimeStr = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = sdf.parse(datetimeStr);
				Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
				preDateTimeStr = sdf.format(preDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			HashMap hashMap = rainStormAreaDaoImpl.getExistDataTmp(preDateTimeStr);
			if(hashMap.size() > 0) {
				addDataList(datetimeStr, rainStormCnt2, rainStormMaxMap2, type);
			}
		}
	}
	
	private void addDataList(String datetimeStr, HashMap<String, Integer> rainStormCnt, HashMap<String, Double> rainStormMaxMap, String type) {
		T_RainStormAreaDaoImpl rainStormAreaDaoImpl = new T_RainStormAreaDaoImpl();
		List dataList = new ArrayList();
		Iterator<String> it2 = rainStormCnt.keySet().iterator();
		while(it2.hasNext()) {
			String key = it2.next();
			HashMap dataMap = new HashMap();
			dataMap.put("Station_Id_C", key);
			dataMap.put("datetime", datetimeStr + " 00:00:00");
			dataMap.put("type", type);
			dataMap.put("extPre", rainStormMaxMap.get(key));
			dataList.add(dataMap);
		}
		rainStormAreaDaoImpl.insert(dataList, datetimeStr, type);
	}
	
	private void addDataList(String datetimeStr, List resultList, T_RainStormAreaDaoImpl rainStormAreaDaoImpl) {
		List dataList = new ArrayList();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap resultMap = (HashMap) resultList.get(i);
			HashMap dataMap = new HashMap(); 
			String station_Id_C = (String) resultMap.get("Station_Id_C");
			Double extPre = (Double) resultMap.get("Pre");
			dataMap.put("Station_Id_C", station_Id_C);
			dataMap.put("datetime", datetimeStr + " 00:00:00");
			dataMap.put("type", "PRE");
			dataMap.put("extPre", extPre);
			dataList.add(dataMap);
		}
		rainStormAreaDaoImpl.insert(dataList, datetimeStr, "PRE");
	}
	
	/**
	 * 按之前的方式统计暴雨
	 * @param datetimeStr
	 */
	public void syncRainStormAreaPre(String datetimeStr) {
		T_RainStormDaoImpl rainStormDaoImpl = new T_RainStormDaoImpl("t_rainstorm2020");
		T_RainStormAreaDaoImpl rainStormAreaDaoImpl = new T_RainStormAreaDaoImpl();
		List resultList = rainStormDaoImpl.getRainStormByTime(datetimeStr, "AWS");
		if(resultList != null && resultList.size() >= AREACNT) {
			addDataList(datetimeStr, resultList, rainStormAreaDaoImpl);
			//循环遍历，往前查找，如果有满足暴雨的站，则添加到数据中
			String preDateTimeStr = "";
			while(true) {
				if(preDateTimeStr.equals("")) {
					preDateTimeStr = datetimeStr;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date date = sdf.parse(preDateTimeStr);
					Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
					preDateTimeStr = sdf.format(preDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				List resultList2 = rainStormDaoImpl.getRainStormByTime(preDateTimeStr, "AWS");
				if(resultList2 != null && resultList2.size() < AREACNT && resultList2.size() > 0) {
					//头一天满足条件
					addDataList(preDateTimeStr, resultList2, rainStormAreaDaoImpl);
				} else {
					break;
				}
			}
		} else if (resultList != null && resultList.size() < AREACNT && resultList.size() > 0) {
			//判断它的头一天是否已经在结果表中，如果在的话，则把它也添加到结果表中。
			String preDateTimeStr = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = sdf.parse(datetimeStr);
				Date preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
				preDateTimeStr = sdf.format(preDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			HashMap hashMap = rainStormAreaDaoImpl.getExistDataTmp(preDateTimeStr);
			if(hashMap.size() > 0) {
				addDataList(datetimeStr, resultList, rainStormAreaDaoImpl);
			}
		}
	}
	
	public void sync(String datetimeStr) {
		AreaRainStormSync areaRainStormSync = new AreaRainStormSync();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		String datetimeStr = sdf.format(date);
//		System.out.println(datetimeStr);
		//新的统计方式
		Object[] objs = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm2020");
		areaRainStormSync.analyst(objs, "t_rainstorm2020", datetimeStr);
		Object[] objs2 = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm0808");
		areaRainStormSync.analyst(objs2, "t_rainstorm0808", datetimeStr);
		//旧的方式
		areaRainStormSync.syncRainStormAreaPre(datetimeStr);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		AreaRainStormSync sync = new AreaRainStormSync();
		sync.sync("2016-08-15");
//		AreaRainStormSync areaRainStormSync = new AreaRainStormSync();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		String datetimeStr = sdf.format(date);
//		System.out.println(datetimeStr);
//		//新的统计方式
//		Object[] objs = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm2020");
//		areaRainStormSync.analyst(objs, "t_rainstorm2020", datetimeStr);
//		Object[] objs2 = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm0808");
//		areaRainStormSync.analyst(objs2, "t_rainstorm0808", datetimeStr);
//		//旧的方式
//		areaRainStormSync.syncRainStormAreaPre(datetimeStr);
		//测试
////		areaRainStormSync.syncRainStormAreaPre("2011-09-14");
//		/////
//		String startTimeStr = "2016-01-01";
//		String endTimeStr = "2016-08-15";
//		Date startDate = null, endDate = null;
//		try {
//			startDate = sdf.parse(startTimeStr);
//			endDate = sdf.parse(endTimeStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		String tableName = "t_rainstorm2020";
//		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
//			String datetimeStr = sdf.format(new Date(i));
//			System.out.println(datetimeStr);
////			areaRainStormSync.syncRainStormAreaPre(datetimeStr);
//			Object[] objs = areaRainStormSync.syncRainStormAreaNew(datetimeStr, tableName);
//			areaRainStormSync.analyst(objs, tableName, datetimeStr);
////			areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm0808");
//		}
	}

}
