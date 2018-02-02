package com.spd.ws;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.common.DisasterRainStormFinResult;
import com.spd.common.DisasterRainStormResult;
import com.spd.common.DisasterRainStormTotalResult;
import com.spd.common.FogResult;
import com.spd.common.FogResultTotal;
import com.spd.common.FogSequenceResult;
import com.spd.common.FrostResult;
import com.spd.common.FrostSequenceResult;
import com.spd.common.FrostTotalResult;
import com.spd.common.HailSequenceResult;
import com.spd.common.LowTmpResult;
import com.spd.common.LowTmpResultHous;
import com.spd.common.LowTmpResultTimes;
import com.spd.common.LowTmpSequenceResult;
import com.spd.common.MCISequenceResult;
import com.spd.common.MCIStationSequenceResult;
import com.spd.common.MaxWindRangeResult;
import com.spd.common.MaxWindRangeResultSequence;
import com.spd.common.MaxWindResult;
import com.spd.common.SnowResult;
import com.spd.common.SnowResultTotal;
import com.spd.common.SnowSequenceResult;
import com.spd.common.StrongCoolingResult;
import com.spd.common.StrongCoolingSequenceResult;
import com.spd.common.StrongCoolingTotalResult;
import com.spd.common.ThundResult;
import com.spd.common.ThundSequenceResult;
import com.spd.common.ThundTotalResult;

public class DisasterFilter {

	/**
	 * 雾过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public FogResult filterFogResult(FogResult fogResult, JSONObject jsonObject) {
		FogResult fogResult2 = new FogResult();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			List<FogResultTotal> fogResultTotalList = fogResult.getFogResultTotalList();
			List<FogSequenceResult> fogSequenceResultList = fogResult.getFogSequenceResultList();
			List<FogResultTotal> fogResultTotalList2 = new ArrayList<FogResultTotal>();
			List<FogSequenceResult> fogSequenceResultList2 = new ArrayList<FogSequenceResult>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < fogResultTotalList.size(); j++) {
					FogResultTotal fogResultTotal = fogResultTotalList.get(j);
					String itemStation_Id_C = fogResultTotal.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						fogResultTotalList2.add(fogResultTotal);
					}
				}
				for(int j = 0; j < fogSequenceResultList.size(); j++) {
					FogSequenceResult fogSequenceResult = fogSequenceResultList.get(j);
					String itemStation_Id_C = fogSequenceResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						fogSequenceResultList2.add(fogSequenceResult);
					}
				}
			}
			fogResult2.setFogResultTotalList(fogResultTotalList2);
			fogResult2.setFogSequenceResultList(fogSequenceResultList2);
			return fogResult2;
		} else {
			return fogResult;
		}
	}
	
	/**
	 * 霜冻过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public FrostResult filterFrostResult(FrostResult frostResult, JSONObject jsonObject) {
		FrostResult frostResult2 = new FrostResult();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			List<FrostSequenceResult> frostSequenceResultList = frostResult.getFrostSequenceResultList();
			List<FrostTotalResult> frostTotalResultList = frostResult.getFrostTotalResultList();
			List<FrostSequenceResult> frostSequenceResultList2 = new ArrayList<FrostSequenceResult>();
			List<FrostTotalResult> frostTotalResultList2 = new ArrayList<FrostTotalResult>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < frostSequenceResultList.size(); j++) {
					FrostSequenceResult frostSequenceResult = frostSequenceResultList.get(j);
					String itemStation_Id_C = frostSequenceResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						frostSequenceResultList2.add(frostSequenceResult);
					}
				}
				for(int j = 0; j < frostTotalResultList.size(); j++) {
					FrostTotalResult frostTotalResult = frostTotalResultList.get(j);
					String itemStation_Id_C = frostTotalResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						frostTotalResultList2.add(frostTotalResult);
					}
				}
			}
			frostResult2.setFrostSequenceResultList(frostSequenceResultList2);
			frostResult2.setFrostTotalResultList(frostTotalResultList2);
			return frostResult2;
		} else {
			return frostResult;
		}
	}
	
	/**
	 * 大风过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public SnowResult filterSnowResult(SnowResult snowResult, JSONObject jsonObject) {
		SnowResult snowResult2 = new SnowResult();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			List<SnowResultTotal> snowResultTotalList = snowResult.getSnowResultTotalList();
			List<SnowSequenceResult> snowSequenceResultList = snowResult.getSnowSequenceResultList();
			List<SnowResultTotal> snowResultTotalList2 = new ArrayList<SnowResultTotal>();
			List<SnowSequenceResult> snowSequenceResultList2 = new ArrayList<SnowSequenceResult>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < snowResultTotalList.size(); j++) {
					SnowResultTotal snowResultTotal = snowResultTotalList.get(j);
					String itemStation_Id_C = snowResultTotal.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						snowResultTotalList2.add(snowResultTotal);
					}
				}
				for(int j = 0; j < snowSequenceResultList.size(); j++) {
					SnowSequenceResult snowSequenceResult = snowSequenceResultList.get(j);
					String itemStation_Id_C = snowSequenceResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						snowSequenceResultList2.add(snowSequenceResult);
					}
				}
			}
			snowResult2.setSnowResultTotalList(snowResultTotalList2);
			snowResult2.setSnowSequenceResultList(snowSequenceResultList2);
			return snowResult2;
		} else {
			return snowResult;
		}
	}
	
	/**
	 * 冰雹过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public List<HailSequenceResult> filterHailSequenceResult(List<HailSequenceResult> hailSequenceResultList, JSONObject jsonObject) {
		List<HailSequenceResult> hailSequenceResultList2 = new ArrayList<HailSequenceResult>();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < hailSequenceResultList.size(); j++) {
					HailSequenceResult item = hailSequenceResultList.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						hailSequenceResultList2.add(item);
					}
				}
			}
			return hailSequenceResultList2;
		} else {
			return hailSequenceResultList;
		}
	}
	
	/**
	 * 大风过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public MaxWindResult filterMaxWindResult(MaxWindResult maxWindResult, JSONObject jsonObject) {
		MaxWindResult maxWindResult2 = new MaxWindResult();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			List<MaxWindRangeResult> maxWindRangeResultList = maxWindResult.getMaxWindRangeResultList();
			List<MaxWindRangeResultSequence> maxWindRangeResultSequenceList = maxWindResult.getMaxWindRangeResultSequenceList();
			List<MaxWindRangeResult> maxWindRangeResultList2 = new ArrayList<MaxWindRangeResult>();
			List<MaxWindRangeResultSequence> maxWindRangeResultSequenceList2 = new ArrayList<MaxWindRangeResultSequence>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < maxWindRangeResultList.size(); j++) {
					MaxWindRangeResult maxWindRangeResult = maxWindRangeResultList.get(j);
					String itemStation_Id_C = maxWindRangeResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						maxWindRangeResultList2.add(maxWindRangeResult);
					}
				}
				for(int j = 0; j < maxWindRangeResultSequenceList.size(); j++) {
					MaxWindRangeResultSequence maxWindRangeResultSequence = maxWindRangeResultSequenceList.get(j);
					String itemStation_Id_C = maxWindRangeResultSequence.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						maxWindRangeResultSequenceList2.add(maxWindRangeResultSequence);
					}
				}
			}
			maxWindResult2.setMaxWindRangeResultList(maxWindRangeResultList2);
			maxWindResult2.setMaxWindRangeResultSequenceList(maxWindRangeResultSequenceList2);
			return maxWindResult2;
		} else {
			return maxWindResult;
		}
	}
	
	/**
	 * 雷暴过滤
	 * @param MaxWindResult
	 * @param jsonObject
	 * @return
	 */
	public ThundResult filterThundResult(ThundResult thundResult, JSONObject jsonObject) {
		ThundResult thundResult2 = new ThundResult();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			List<ThundSequenceResult> thundSequenceResult = thundResult.getThundSequenceResultList();
			List<ThundTotalResult> thundTotalResult = thundResult.getThundTotalResultList();
			List<ThundSequenceResult> thundSequenceResult2 = new ArrayList<ThundSequenceResult>();
			List<ThundTotalResult> thundTotalResult2 = new ArrayList<ThundTotalResult>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < thundSequenceResult.size(); j++) {
					ThundSequenceResult item = thundSequenceResult.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						thundSequenceResult2.add(item);
					}
				}
				for(int j = 0; j < thundTotalResult.size(); j++) {
					ThundTotalResult item = thundTotalResult.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						thundTotalResult2.add(item);
					}
				}
			}
			thundResult2.setThundSequenceResultList(thundSequenceResult2);
			thundResult2.setThundTotalResultList(thundTotalResult2);
			return thundResult2;
		} else {
			return thundResult;
		}
	}
	
	/**
	 * 低温统计结果过滤
	 * @param lowTmpResult
	 * @param jsonObject
	 * @return
	 */
	public LowTmpResult filterLowTmpResult(LowTmpResult lowTmpResult, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		LowTmpResult lowTmpResult2 = new LowTmpResult();
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			//过滤
			List<LowTmpResultHous> lowTmpResultHousList = lowTmpResult.getLowTmpResultHousResult();
			List<LowTmpResultTimes> lowTmpResultTimesList = lowTmpResult.getLowTmpResultTimesListResult();
			List<LowTmpSequenceResult> lowTmpSequenceResultList =lowTmpResult.getLowTmpSequenceResult();
			
			List<LowTmpResultHous> lowTmpResultHousList2 = new ArrayList<LowTmpResultHous>();
			List<LowTmpResultTimes> lowTmpResultTimesList2 = new ArrayList<LowTmpResultTimes>();
			List<LowTmpSequenceResult> lowTmpSequenceResultList2 = new ArrayList<LowTmpSequenceResult>();
			
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < lowTmpResultHousList.size(); j++) {
					LowTmpResultHous item = lowTmpResultHousList.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						lowTmpResultHousList2.add(item);
					}
				}
				for(int j = 0; j < lowTmpResultTimesList.size(); j++) {
					LowTmpResultTimes item = lowTmpResultTimesList.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						lowTmpResultTimesList2.add(item);
					}
				}
				for(int j = 0; j < lowTmpSequenceResultList.size(); j++) {
					LowTmpSequenceResult item = lowTmpSequenceResultList.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						lowTmpSequenceResultList2.add(item);
					}
				}
			}
			lowTmpResult2.setLowTmpResultHousResult(lowTmpResultHousList2);
			lowTmpResult2.setLowTmpResultTimesListResult(lowTmpResultTimesList2);
			lowTmpResult2.setLowTmpSequenceResult(lowTmpSequenceResultList2);
			return lowTmpResult2;
		} else {
			return lowTmpResult;
		}
	}
	
	private LinkedList<String>  getStationList(JSONObject jsonObject) {
		LinkedList<String> stationList = new LinkedList<String>();
		try {
			String station_Id_Cs = (String) jsonObject.get("station_Id_Cs");
			String[] station_id_CItems = station_Id_Cs.split(",");
			for(int i = 0; i < station_id_CItems.length; i++) {
				stationList.add(station_id_CItems[i]);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stationList;
	}
	
	public List<MCISequenceResult> filterMCISequenceResult(List<MCISequenceResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		List<MCISequenceResult> list2 = new ArrayList<MCISequenceResult>();
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			//过滤
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < list.size(); j++) {
					MCISequenceResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.endsWith(itemStation_Id_C)) {
						list2.add(item);
					}
				}
			}
			return list2;
		} else {
			return list;
		}
	}
	
	
	public DisasterRainStormFinResult filterDisasterRainStormFinResult(DisasterRainStormFinResult disasterRainStormFinResult, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		DisasterRainStormFinResult disasterRainStormFinResult2 = new DisasterRainStormFinResult();
		if(flag) {
			LinkedList<String> stationList = getStationList(jsonObject);
			//过滤
			List<DisasterRainStormResult> disasterRainStormResultList = disasterRainStormFinResult.getSeqResult();
			List<DisasterRainStormTotalResult> disasterRainStormTotalResultList = disasterRainStormFinResult.getTotalResult();
			List<DisasterRainStormResult> disasterRainStormResultList2 = new ArrayList<DisasterRainStormResult>();
			List<DisasterRainStormTotalResult> disasterRainStormTotalResultList2 = new ArrayList<DisasterRainStormTotalResult>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < disasterRainStormResultList.size(); j++) {
					DisasterRainStormResult itemDisasterRainStormResult = disasterRainStormResultList.get(j);
					String itemStation_id_C = itemDisasterRainStormResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_id_C)) {
						disasterRainStormResultList2.add(itemDisasterRainStormResult);
					}
				}
				for(int j = 0; j < disasterRainStormTotalResultList.size(); j++) {
					DisasterRainStormTotalResult itemDisasterRainStormTotalResult = disasterRainStormTotalResultList.get(j);
					String itemStation_id_C = itemDisasterRainStormTotalResult.getStation_Id_C();
					if(station_Id_C.equals(itemStation_id_C)) {
						disasterRainStormTotalResultList2.add(itemDisasterRainStormTotalResult);
					}
				}
				disasterRainStormFinResult2.setSeqResult(disasterRainStormResultList2);
				disasterRainStormFinResult2.setTotalResult(disasterRainStormTotalResultList2);
			}
			return disasterRainStormFinResult2;
		} else {
			return disasterRainStormFinResult;
		}
	}
	
}
