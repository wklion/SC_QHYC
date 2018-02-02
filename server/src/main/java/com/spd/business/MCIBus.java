package com.spd.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.AgmeSoilSequenctResult;
import com.spd.common.CommonConstant;
import com.spd.common.MCILevelParam;
import com.spd.common.MCISequenceResult;
import com.spd.common.MCIStationSequenceResult;
import com.spd.common.MCIStatisticsParam;
import com.spd.common.TimesParam;
import com.spd.service.IMCI;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 干旱
 * @author Administrator
 *
 */
public class MCIBus {

	public List<MCISequenceResult> mciByTime(MCILevelParam mciLevelParam, String dateTime) {
//		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		List<MCISequenceResult> mciSequenceResultList = new ArrayList<MCISequenceResult>();
		//1. 查询结果
		IMCI mciImpl = (IMCI)ContextLoader.getCurrentWebApplicationContext().getBean("MCIImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("datetime", dateTime);
		List<LinkedHashMap> resultList = mciImpl.queryMCIByTime(paramMap);
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap map = resultList.get(i);
			MCISequenceResult mciSequenceResult = new MCISequenceResult();
			String station_Id_C = (String) map.get("station_Id_C");
			mciSequenceResult.setStation_Id_C(station_Id_C);
			mciSequenceResult.setStation_Name((String) map.get("station_Name"));
			Double mi = (Double) map.get("RT_MI");
//			if(mi == null) continue;
			Double mci = (Double) map.get("MCI");
//			if(mci == null) continue;
			mciSequenceResult.setMCI(mci);
			mciSequenceResult.setMI(mi);
			Double spi150 = (Double) map.get("RT_SPI150");
//			if(spi150 == null) continue;
			mciSequenceResult.setSPI150(spi150);
			Double spi90 = (Double) map.get("RT_SPI90");
//			if(spi90 == null) continue;
			mciSequenceResult.setSPI90(spi90);
			Double spi60 = (Double) map.get("RT_SPIW60");
//			if(spi60 == null) continue;
			mciSequenceResult.setSPIW60(spi60);
			mciSequenceResult.setArea(CommonUtil.getInstance().stationAreaMap.get(station_Id_C));
			mciSequenceResult.setAreaCode(CommonUtil.getInstance().areaCodeMap.get(station_Id_C));
			if(mci == null) {
				mciSequenceResult.setLevel("无");
			} else if(mci <= mciLevelParam.getLevel1() && mci > mciLevelParam.getLevel2()) {
				mciSequenceResult.setLevel("轻旱");
			} else if(mci <= mciLevelParam.getLevel2() && mci > mciLevelParam.getLevel3()) {
				mciSequenceResult.setLevel("中旱");
			} else if(mci <= mciLevelParam.getLevel3() && mci > mciLevelParam.getLevel4()) {
				mciSequenceResult.setLevel("重旱");
			} else if(mci <= mciLevelParam.getLevel4()) {
				mciSequenceResult.setLevel("特旱");
			} else {
				mciSequenceResult.setLevel("无");
			}
			mciSequenceResultList.add(mciSequenceResult);
		}
		//2. 对比等级
		return mciSequenceResultList;
	}
	
	/**
	 * 站次统计
	 * @param mciStatisticsParam
	 * @return
	 */
	public List<MCIStationSequenceResult> mciStatisticsByTime(MCIStatisticsParam mciStatisticsParam) {
		List<MCIStationSequenceResult> mciStationSequenceResultList = new ArrayList<MCIStationSequenceResult>();
		IMCI mciImpl = (IMCI)ContextLoader.getCurrentWebApplicationContext().getBean("MCIImpl");
		HashMap paramMap = new HashMap();
		TimesParam timesParam = mciStatisticsParam.getTimesParam();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<String> station_Id_Cs =  mciStatisticsParam.getStation_id_Cs();
		List<LinkedHashMap> resultList = null;
		if(station_Id_Cs != null && station_Id_Cs.size() > 0) {
			paramMap.put("Station_Id_Cs", station_Id_Cs);
			resultList = mciImpl.mciStatisticsByTimeAndStation(paramMap);
		} else {
			resultList = mciImpl.mciStatisticsByTime(paramMap);
		}
		Map<String, List<LinkedHashMap>> map = new HashMap<String, List<LinkedHashMap>>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String datetime = (String) itemMap.get("datetime");
			List<LinkedHashMap> itemList = map.get(datetime);
			if(itemList == null) {
				itemList = new ArrayList<LinkedHashMap>();
			}
			itemList.add(itemMap);
			map.put(datetime, itemList);
		}
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String datetime = it.next();
			System.out.println(datetime);
			if(datetime.equals("2014-05-01")) {
				System.out.println();
			}
			List<LinkedHashMap> list = map.get(datetime);
			int level1 = 0, level2 = 0, level3 = 0, level4 = 0, existDays = 0, noDays = 0; 
			for(int i = 0; i < list.size(); i++) {
				LinkedHashMap itemMap = list.get(i);
				Double mci = (Double) itemMap.get("MCI");
				if(mci == null) {
					continue;
				}
				if(mci <= mciStatisticsParam.getLevel1() && mci > mciStatisticsParam.getLevel2()) {
					level1++;
				} else if(mci <= mciStatisticsParam.getLevel2() && mci > mciStatisticsParam.getLevel3()) {
					level2++;
				} else if(mci <= mciStatisticsParam.getLevel3() && mci > mciStatisticsParam.getLevel4()) {
					level3++;
				} else if(mci <= mciStatisticsParam.getLevel4()) {
					level4++;
				} else {
					noDays++;
				}
			}
			existDays = level1 + level2 + level3 + level4;
			MCIStationSequenceResult mciStationSequenceResult = new MCIStationSequenceResult();
			mciStationSequenceResult.setDatetime(datetime);
			mciStationSequenceResult.setLevel1(level1);
			mciStationSequenceResult.setLevel2(level2);
			mciStationSequenceResult.setLevel3(level3);
			mciStationSequenceResult.setLevel4(level4);
			mciStationSequenceResult.setExistDays(existDays);
			mciStationSequenceResult.setNoDays(noDays);
			mciStationSequenceResultList.add(mciStationSequenceResult);
		}
		//排序
		MCIStationSequenceResult[] mciStationSequenceResultArray = new MCIStationSequenceResult[mciStationSequenceResultList.size()];
		for(int i = 0; i < mciStationSequenceResultList.size(); i++) {
			mciStationSequenceResultArray[i] = mciStationSequenceResultList.get(i);
		}
		Arrays.sort(mciStationSequenceResultArray, new MCIStationSequenceResult());
		List<MCIStationSequenceResult> mciStationSequenceResultList2 = new ArrayList<MCIStationSequenceResult>();
		for(int i = 0; i < mciStationSequenceResultList.size(); i++) {
			mciStationSequenceResultList2.add(mciStationSequenceResultArray[i]);
		}
		return mciStationSequenceResultList2;
	}
	
	public List<AgmeSoilSequenctResult> agmesoilStatisticsByTime(String datetime) {
		//查询结果
		IMCI mciImpl = (IMCI)ContextLoader.getCurrentWebApplicationContext().getBean("MCIImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("datetime", datetime);
		List<LinkedHashMap> resultList = mciImpl.agmesoilStatisticsByTime(paramMap);
		//返回结果
		List<AgmeSoilSequenctResult> agmeSoilSequenctResultList = new ArrayList<AgmeSoilSequenctResult>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap dataMap = resultList.get(i);
			AgmeSoilSequenctResult agmeSoilSequenctResult = new AgmeSoilSequenctResult();
			String station_id_C = (String)dataMap.get("Station_Id_C");
			agmeSoilSequenctResult.setStation_Id_C(station_id_C);
			agmeSoilSequenctResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_id_C));
			agmeSoilSequenctResult.setArea(CommonUtil.getInstance().stationAreaMap.get(station_id_C));
			agmeSoilSequenctResult.setSoil_Depth_BelS((Double)dataMap.get("Soil_Depth_BelS"));
			agmeSoilSequenctResult.setSRHU((Double)dataMap.get("SRHU"));
			agmeSoilSequenctResult.setSVMS((Double) dataMap.get("SVMS"));
			agmeSoilSequenctResult.setSWWC((Double) dataMap.get("SWWC"));
			agmeSoilSequenctResult.setSVWC((Double) dataMap.get("SVWC"));
			agmeSoilSequenctResultList.add(agmeSoilSequenctResult);
		}
		return agmeSoilSequenctResultList;
	}
}
