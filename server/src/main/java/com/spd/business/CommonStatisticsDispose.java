package com.spd.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.Station;
import com.spd.pojo.AvgMaxTmp;
import com.spd.pojo.AvgMaxTmpResult;
import com.spd.pojo.AvgMinTmp;
import com.spd.pojo.AvgMinTmpResult;
import com.spd.pojo.AvgTmp;
import com.spd.pojo.ExtTmp;
import com.spd.pojo.ExtTmpMaxItem;
import com.spd.pojo.ExtTmpMinItem;
import com.spd.pojo.ExtTmpResult;
import com.spd.pojo.PreCnt;
import com.spd.pojo.PreCntItem;
import com.spd.pojo.PreCntResult;
import com.spd.pojo.PreSum;
import com.spd.pojo.PreSumResult;
import com.spd.pojo.PreTimeItem;
import com.spd.pojo.PrsAvg;
import com.spd.pojo.PrsAvgItem;
import com.spd.pojo.PrsAvgResult;
import com.spd.pojo.RHUItem;
import com.spd.pojo.RHU;
import com.spd.pojo.RHUResult;
import com.spd.pojo.ResultDesc;
import com.spd.pojo.AvgTmpResult;
import com.spd.pojo.SSH;
import com.spd.pojo.SSHItem;
import com.spd.pojo.SSHResult;
import com.spd.pojo.TmpAvgItem;
import com.spd.pojo.TmpMaxAvgItem;
import com.spd.pojo.TmpMaxCnt;
import com.spd.pojo.TmpMaxCntItem;
import com.spd.pojo.TmpMaxCntResult;
import com.spd.pojo.TmpMinAvgItem;
import com.spd.pojo.VisMin;
import com.spd.pojo.VisMinItem;
import com.spd.pojo.VisMinResult;
import com.spd.pojo.Win_s_2mi_avgItem;
import com.spd.pojo.Win_s_2min_avg;
import com.spd.pojo.Win_s_2min_avgResult;
import com.spd.service.ICommon;
import com.spd.service.IStatistics;
import com.spd.util.CommonUtil;

/**
 * 查询统计的结果的处理类。
 * @author Administrator
 *
 */
public class CommonStatisticsDispose {

	private static List<Station> awsStations = new ArrayList<Station>();
	
	public CommonStatisticsDispose() {
		if(awsStations.size() > 0) return;
		ICommon common = (ICommon) ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap commonParamMap = new HashMap();
		commonParamMap.put("ZoomLevel", 1);
		// 自动站
		awsStations = common.getStationsByLevel(commonParamMap);
	}
	
	/**
	 * 平均气温统计
	 * @return
	 */
	public List<AvgTmp> avgTmpAnomaly(List<TmpAvgItem> resultList, List<TmpAvgItem> contrastList, String type, String stationType) {
		List<AvgTmp> avgTmpResultList = new ArrayList<AvgTmp>();
//		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, AvgTmp> resultMap = new HashMap<String, AvgTmp>();
		for(TmpAvgItem tmpAvgItem : resultList) {
			AvgTmp avgTmpResult = new AvgTmp();
			avgTmpResult.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			avgTmpResult.setStation_Name(tmpAvgItem.getStation_Name());
			avgTmpResult.setLon(tmpAvgItem.getLon());
			avgTmpResult.setLat(tmpAvgItem.getLat());
			avgTmpResult.setTEM_Avg(tmpAvgItem.getTEM_Avg());
			avgTmpResult.setArea(tmpAvgItem.getCnty());
			resultMap.put(tmpAvgItem.getStation_Id_C(), avgTmpResult);
		}
		for(TmpAvgItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			AvgTmp avgTmpResultOri = resultMap.get(station_Id_C);
			if(avgTmpResultOri == null) {
				continue;
			}
			double tempAvgOri = avgTmpResultOri.getTEM_Avg();
			avgTmpResultOri.setContrastTMP_Avg(tmpAvgItem.getTEM_Avg());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getTEM_Avg()) * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			avgTmpResultOri.setAnomaly(doubleTmpAvg / 10.0);
			resultMap.put(station_Id_C, avgTmpResultOri);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			avgTmpResultList.add(resultMap.get(key));
		}
//		AvgTmpResult avgTmpResult = new AvgTmpResult();
//		ResultDesc avgTmpDesc = null;//(ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("avgTmpDesc");
//		if("range".equals(type)) {
//			avgTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("avgTmpDescRange");
//		} else if("sameTeam".equals(type)) {
//			avgTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("avgTmpDescYears");
//		}
//		avgTmpResult.setDesc(avgTmpDesc);
//		avgTmpResult.setList(avgTmpResultList);
//		return avgTmpResult;
		if("AWS".equals(stationType)) {
			//过滤
			List<AvgTmp> avgTmpResultList2 = new ArrayList<AvgTmp>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < avgTmpResultList.size(); j++) {
					AvgTmp itemAvgTmp = avgTmpResultList.get(j);
					String itemStation_Id_C = itemAvgTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemAvgTmp.setStation_Name(station_Name);
						avgTmpResultList2.add(itemAvgTmp);
						break;
					}
				}
			}
			return avgTmpResultList2;
		}
		return avgTmpResultList;
	}
	
	/**
	 * 高温均值统计
	 * @param resultList
	 * @param contrastList
	 * @return
	 */
	public List<AvgMaxTmp> avgTmpMaxAnomaly(List<TmpMaxAvgItem> resultList, List<TmpMaxAvgItem> contrastList, String type, String stationType) {
		List<AvgMaxTmp> avgMaxTmpResultList = new ArrayList<AvgMaxTmp>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, AvgMaxTmp> resultMap = new HashMap<String, AvgMaxTmp>();
		for(TmpMaxAvgItem tmpAvgItem : resultList) {
			AvgMaxTmp avgTmpResult = new AvgMaxTmp();
			avgTmpResult.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			avgTmpResult.setStation_Name(tmpAvgItem.getStation_Name());
			avgTmpResult.setLon(tmpAvgItem.getLon());
			avgTmpResult.setLat(tmpAvgItem.getLat());
			avgTmpResult.setTEM_Max(tmpAvgItem.getTEM_Max());
			avgTmpResult.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), avgTmpResult);
		}
		for(TmpMaxAvgItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			AvgMaxTmp avgTmpResultOri = resultMap.get(station_Id_C);
			if(avgTmpResultOri == null) {
				continue;
			}
			double tempAvgOri = avgTmpResultOri.getTEM_Max();
			avgTmpResultOri.setContrastTEM_Max(tmpAvgItem.getTEM_Max());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getTEM_Max()) * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			avgTmpResultOri.setAnomaly(doubleTmpAvg / 10.0);
			resultMap.put(station_Id_C, avgTmpResultOri);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			avgMaxTmpResultList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			List<AvgMaxTmp> avgMaxTmpResultList2 = new ArrayList<AvgMaxTmp>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < avgMaxTmpResultList.size(); j++) {
					AvgMaxTmp itemMaxTmp = avgMaxTmpResultList.get(j);
					String itemStation_Id_C = itemMaxTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemMaxTmp.setStation_Name(station_Name);
						avgMaxTmpResultList2.add(itemMaxTmp);
						break;
					}
				}
			}
			return avgMaxTmpResultList2;
		}
//		AvgMaxTmpResult avgMaxTmpResult = new AvgMaxTmpResult();
//		ResultDesc maxTmpDesc = null;
//		if("range".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("maxTmpDescRange");
//		} else if("sameTeam".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("maxTmpDescYears");
//		}
//		avgMaxTmpResult.setDesc(maxTmpDesc);
//		avgMaxTmpResult.setList(avgMaxTmpResultList);
		return avgMaxTmpResultList;
	}
	
	/**
	 * 低温均值统计
	 * @param resultList
	 * @param contrastList
	 * @return
	 */
	public List<AvgMinTmp> avgTmpMinAnomaly(List<TmpMinAvgItem> resultList, List<TmpMinAvgItem> contrastList, String type, String stationType) {
		List<AvgMinTmp> avgMinTmpResultList = new ArrayList<AvgMinTmp>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, AvgMinTmp> resultMap = new HashMap<String, AvgMinTmp>();
		for(TmpMinAvgItem tmpAvgItem : resultList) {
			AvgMinTmp avgTmpResult = new AvgMinTmp();
			avgTmpResult.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			avgTmpResult.setStation_Name(tmpAvgItem.getStation_Name());
			avgTmpResult.setLon(tmpAvgItem.getLon());
			avgTmpResult.setLat(tmpAvgItem.getLat());
			avgTmpResult.setTEM_Min(tmpAvgItem.getTEM_Min());
			avgTmpResult.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), avgTmpResult);
		}
		for(TmpMinAvgItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			AvgMinTmp avgTmpResultOri = resultMap.get(station_Id_C);
			if(avgTmpResultOri == null) {
				continue;
			}
			double tempAvgOri = avgTmpResultOri.getTEM_Min();
			avgTmpResultOri.setContrastTEM_Min(tmpAvgItem.getTEM_Min());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getTEM_Min()) * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			avgTmpResultOri.setAnomaly(doubleTmpAvg / 10.0);
			resultMap.put(station_Id_C, avgTmpResultOri);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			avgMinTmpResultList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			List<AvgMinTmp> avgMinTmpResultList2 = new ArrayList<AvgMinTmp>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < avgMinTmpResultList.size(); j++) {
					AvgMinTmp itemMinTmp = avgMinTmpResultList.get(j);
					String itemStation_Id_C = itemMinTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemMinTmp.setStation_Name(station_Name);
						avgMinTmpResultList2.add(itemMinTmp);
						break;
					}
				}
			}
			return avgMinTmpResultList2;
		}
//		AvgMinTmpResult avgMinTmpResult = new AvgMinTmpResult();
//		ResultDesc minTmpDesc = null;
//		if("range".equals(type)) {
//			minTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("minTmpDescRange");
//		} else if("sameTeam".equals(type)) {
//			minTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("minTmpDescYears");
//		}
//		avgMinTmpResult.setDesc(minTmpDesc);
//		avgMinTmpResult.setList(avgMinTmpResultList);
		return avgMinTmpResultList;
	}
	
	/**
	 * 降水对比
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<PreSum> preSumMaxAnomaly(List<PreTimeItem> resultList, List<PreTimeItem> contrastList, String type, String stationType) {
		List<PreSum> avgMaxTmpResultList = new ArrayList<PreSum>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, PreSum> resultMap = new HashMap<String, PreSum>();
		for(PreTimeItem tmpAvgItem : resultList) {
			PreSum avgTmpResult = new PreSum();
			avgTmpResult.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			avgTmpResult.setStation_Name(tmpAvgItem.getStation_Name());
			avgTmpResult.setLon(tmpAvgItem.getLon());
			avgTmpResult.setLat(tmpAvgItem.getLat());
			avgTmpResult.setPRE_Time(tmpAvgItem.getPRE_Time());
			avgTmpResult.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), avgTmpResult);
		}
		for(PreTimeItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			PreSum avgTmpResultOri = resultMap.get(station_Id_C);
			if(avgTmpResultOri == null) {
				continue;
			}
			double tempAvgOri = avgTmpResultOri.getPRE_Time();
			avgTmpResultOri.setPRE_Time(tempAvgOri);
			avgTmpResultOri.setContrastPRE_Time(tmpAvgItem.getPRE_Time());
			double anomaly = tempAvgOri - tmpAvgItem.getPRE_Time();
			int anomalyInt = (int)(anomaly * 100);
			anomaly = Math.round(anomalyInt / 10.0);
			avgTmpResultOri.setAnomaly(anomaly / 10.0);
			//保留小数一位
			int tmpAvg = (int)Math.round((tempAvgOri - tmpAvgItem.getPRE_Time()) / tmpAvgItem.getPRE_Time() * 1000);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			avgTmpResultOri.setAnomalyRate(doubleTmpAvg);
			resultMap.put(station_Id_C, avgTmpResultOri);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			avgMaxTmpResultList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			List<PreSum> avgMinTmpResultList2 = new ArrayList<PreSum>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < avgMaxTmpResultList.size(); j++) {
					PreSum itemMinTmp = avgMaxTmpResultList.get(j);
					String itemStation_Id_C = itemMinTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemMinTmp.setStation_Name(station_Name);
						avgMinTmpResultList2.add(itemMinTmp);
						break;
					}
				}
			}
			return avgMinTmpResultList2;
		}
//		PreSumResult avgMaxTmpResult = new PreSumResult();
//		ResultDesc maxTmpDesc = null;
//		if("range".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("preSumDescRange");
//		} else if("sameTeam".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("preSumDescYears");
//		}
//		avgMaxTmpResult.setDesc(maxTmpDesc);
//		avgMaxTmpResult.setList(avgMaxTmpResultList);
		return avgMaxTmpResultList;
	}
	
	/**
	 * 降水日数
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<PreCnt> queryPreCntAnomaly(List<PreCntItem> resultList, List<PreCntItem> contrastList, String type, String stationType) {
		List<PreCnt> preCntResultList = new ArrayList<PreCnt>();
		Map<String, PreCnt> resultMap = new HashMap<String, PreCnt>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		for(PreCntItem preCntItem : resultList) {
			PreCnt preCnt = new PreCnt();
			preCnt.setStation_Id_C(preCntItem.getStation_Id_C());
			preCnt.setStation_Name(preCntItem.getStation_Name());
			preCnt.setLon(preCntItem.getLon());
			preCnt.setLat(preCntItem.getLat());
			preCnt.setAlti(preCntItem.getAlti());
			preCnt.setCity(preCntItem.getCity());
			preCnt.setCnty(preCntItem.getCnty());
			preCnt.setCnt(preCntItem.getCnt());
			preCnt.setGet25lt50cnt(preCntItem.getGet25lt50cnt());
			preCnt.setGet50lt100cnt(preCntItem.getGet50lt100cnt());
			preCnt.setGet100(preCntItem.getGet100());
			preCnt.setProvince(preCntItem.getProvince());
			preCnt.setStation_Id_d(preCntItem.getStation_Id_d());
			preCnt.setArea(CommonUtil.getInstance().stationAreaMap.get(preCntItem.getStation_Id_C()));
			resultMap.put(preCntItem.getStation_Id_C(), preCnt);
		}
		for(PreCntItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			PreCnt preCnt = resultMap.get(station_Id_C);
			if(preCnt == null) {
				continue;
			}
			double tempAvgOri = tmpAvgItem.getCnt();
			preCnt.setContrastCnt(tempAvgOri);
			
			int tmpAvg = (int)((preCnt.getCnt() - tempAvgOri) * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			preCnt.setAnomaly(doubleTmpAvg / 10.0);
			resultMap.put(station_Id_C, preCnt);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			preCntResultList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			List<PreCnt> preCntResultList2 = new ArrayList<PreCnt>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < preCntResultList.size(); j++) {
					PreCnt itemPreCnt = preCntResultList.get(j);
					String itemStation_Id_C = itemPreCnt.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemPreCnt.setStation_Name(station_Name);
						preCntResultList2.add(itemPreCnt);
						break;
					}
				}
			}
			return preCntResultList2;
		}
//		PreCntResult preCntResult = new PreCntResult();
//		ResultDesc maxTmpDesc = null;
//		if("range".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("preCntDescRange");
//		} else if("sameTeam".equals(type)) {
//			maxTmpDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("preCntDescYears");
//		}
//		preCntResult.setDesc(maxTmpDesc);
//		preCntResult.setList(avgMaxTmpResultList);
		return preCntResultList;
	}
	
	/**
	 * 相对湿度统计
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<RHU> rHUAnomaly(List<RHUItem> resultList, List<RHUItem> contrastList, String type, String stationType) {
		List<RHU> rhuList = new ArrayList<RHU>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, RHU> resultMap = new HashMap<String, RHU>();
		for(RHUItem tmpAvgItem : resultList) {
			RHU rhu = new RHU();
			rhu.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			rhu.setStation_Name(tmpAvgItem.getStation_Name());
			rhu.setLon(tmpAvgItem.getLon());
			rhu.setLat(tmpAvgItem.getLat());
			rhu.setRHU_Avg(tmpAvgItem.getRHU_Avg());
			rhu.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), rhu);
		}
		for(RHUItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			RHU rhu = resultMap.get(station_Id_C);
			if(rhu == null) {
				continue;
			}
			double tempAvgOri = rhu.getRHU_Avg();
			rhu.setRHU_Avg(tempAvgOri);
			rhu.setContrastRHU_Avg(tmpAvgItem.getRHU_Avg());
			rhu.setAnomaly(tempAvgOri - tmpAvgItem.getRHU_Avg());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getRHU_Avg()) * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			rhu.setAnomaly(doubleTmpAvg / 10);
			resultMap.put(station_Id_C, rhu);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			rhuList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<RHU> rhuList2 = new ArrayList<RHU>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < rhuList.size(); j++) {
					RHU itemRHU = rhuList.get(j);
					String itemStation_Id_C = itemRHU.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemRHU.setStation_Name(station_Name);
						rhuList2.add(itemRHU);
						break;
					}
				}
			}
			return rhuList2;
		}
//		RHUResult rhuResult = new RHUResult();
//		ResultDesc resultDesc = null;
//		if("range".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("rHURange");
//		} else if("sameTeam".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("rHUYears");
//		}
//		rhuResult.setDesc(resultDesc);
//		rhuResult.setList(rhuList);
		return rhuList;
	}
	
	/**
	 * 平均风速
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<Win_s_2min_avg> win_s_2mi_avgItemAnomaly(List<Win_s_2mi_avgItem> resultList, List<Win_s_2mi_avgItem> contrastList, String type, String stationType) {
		List<Win_s_2min_avg> winList = new ArrayList<Win_s_2min_avg>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, Win_s_2min_avg> resultMap = new HashMap<String, Win_s_2min_avg>();
		for(Win_s_2mi_avgItem tmpAvgItem : resultList) {
			Win_s_2min_avg win_s_2min_avg = new Win_s_2min_avg();
			win_s_2min_avg.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			win_s_2min_avg.setStation_Name(tmpAvgItem.getStation_Name());
			win_s_2min_avg.setLon(tmpAvgItem.getLon());
			win_s_2min_avg.setLat(tmpAvgItem.getLat());
			win_s_2min_avg.setWIN_S_2mi_Avg(tmpAvgItem.getWIN_S_2mi_Avg());
			win_s_2min_avg.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), win_s_2min_avg);
		}
		for(Win_s_2mi_avgItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			Win_s_2min_avg win_s_2min_avg = resultMap.get(station_Id_C);
			if(win_s_2min_avg == null) {
				continue;
			}
			double tempAvgOri = win_s_2min_avg.getWIN_S_2mi_Avg();
			win_s_2min_avg.setWIN_S_2mi_Avg(tempAvgOri);
			win_s_2min_avg.setContrastWIN_S_2mi_Avg(tmpAvgItem.getWIN_S_2mi_Avg());
			win_s_2min_avg.setAnomaly(tempAvgOri - tmpAvgItem.getWIN_S_2mi_Avg());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getWIN_S_2mi_Avg()) * 100);
			double win_s_2min = Math.round(tmpAvg / 10.0);
			win_s_2min_avg.setAnomaly(win_s_2min / 10);
			resultMap.put(station_Id_C, win_s_2min_avg);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			winList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<Win_s_2min_avg> winList2 = new ArrayList<Win_s_2min_avg>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < winList.size(); j++) {
					Win_s_2min_avg itemWin = winList.get(j);
					String itemStation_Id_C = itemWin.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemWin.setStation_Name(station_Name);
						winList2.add(itemWin);
						break;
					}
				}
			}
			return winList2;
		}
//		Win_s_2min_avgResult win_s_2min_avg = new Win_s_2min_avgResult();
//		ResultDesc resultDesc = null;
//		if("range".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("Win_s_2mi_avgRange");
//		} else if("sameTeam".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("Win_s_2mi_avgYears");
//		}
//		win_s_2min_avg.setDesc(resultDesc);
//		win_s_2min_avg.setList(winList);
		return winList;
	}
	
	/**
	 * 平均气压
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<PrsAvg> prsAvgItemAnomaly(List<PrsAvgItem> resultList, List<PrsAvgItem> contrastList, String type, String stationType) {
		List<PrsAvg> prsList = new ArrayList<PrsAvg>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		Map<String, PrsAvg> resultMap = new HashMap<String, PrsAvg>();
		for(PrsAvgItem tmpAvgItem : resultList) {
			PrsAvg win_s_2min_avg = new PrsAvg();
			win_s_2min_avg.setStation_Id_C(tmpAvgItem.getStation_Id_C());
			win_s_2min_avg.setStation_Name(tmpAvgItem.getStation_Name());
			win_s_2min_avg.setLon(tmpAvgItem.getLon());
			win_s_2min_avg.setLat(tmpAvgItem.getLat());
			win_s_2min_avg.setPRS_Avg(tmpAvgItem.getPRS_Avg());
			win_s_2min_avg.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			resultMap.put(tmpAvgItem.getStation_Id_C(), win_s_2min_avg);
		}
		for(PrsAvgItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			PrsAvg win_s_2min_avg = resultMap.get(station_Id_C);
			if(win_s_2min_avg == null) {
				continue;
			}
			double tempAvgOri = win_s_2min_avg.getPRS_Avg();
			win_s_2min_avg.setPRS_Avg(tempAvgOri);
			win_s_2min_avg.setContrastPRS_Avg(tmpAvgItem.getPRS_Avg());
			win_s_2min_avg.setAnomaly(tempAvgOri - tmpAvgItem.getPRS_Avg());
			//保留小数一位
			int tmpAvg = (int)((tempAvgOri - tmpAvgItem.getPRS_Avg()) * 100);
			double win_s_2min = Math.round(tmpAvg / 10.0);
			win_s_2min_avg.setAnomaly(win_s_2min / 10);
			resultMap.put(station_Id_C, win_s_2min_avg);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			prsList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<PrsAvg> prsAvgList2 = new ArrayList<PrsAvg>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < prsList.size(); j++) {
					PrsAvg itemPrsAvg = prsList.get(j);
					String itemStation_Id_C = itemPrsAvg.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemPrsAvg.setStation_Name(station_Name);
						prsAvgList2.add(itemPrsAvg);
						break;
					}
				}
			}
			return prsAvgList2;
		}
//		PrsAvgResult win_s_2min_avg = new PrsAvgResult();
//		ResultDesc resultDesc = null;
//		if("range".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("prsAvgRange");
//		} else if("sameTeam".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("prsAvgYears");
//		}
//		win_s_2min_avg.setDesc(resultDesc);
//		win_s_2min_avg.setList(winList);
		return prsList;
	}
	
	/**
	 * 日照统计
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<SSH> sshAnomaly(List<SSHItem> resultList, Date startDate, Date endDate, List<SSHItem> contrastList, String type, String stationType) {
		List<SSH> winList = new ArrayList<SSH>();
		Map<String, SSH> resultMap = new HashMap<String, SSH>();
		
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		List<Map> sshTime = statistics.querySSHTime(paramMap);
		
		for(SSHItem tmpAvgItem : resultList) {
			SSH ssh = new SSH();
			String  station_Id_C = tmpAvgItem.getStation_Id_C(); 
			ssh.setStation_Id_C(station_Id_C);
			ssh.setStation_Name(tmpAvgItem.getStation_Name());
			ssh.setLon(tmpAvgItem.getLon());
			ssh.setLat(tmpAvgItem.getLat());
			ssh.setSSH(tmpAvgItem.getSSH());
			ssh.setCity(tmpAvgItem.getCity());
			ssh.setCnty(tmpAvgItem.getCnty());
			ssh.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpAvgItem.getStation_Id_C()));
			//日照百分率
			long dayTime = 24 * 60 * 60 * 1000;
			double sshSum = 0;
			for(long start = startDate.getTime(); start <= endDate.getTime(); start += dayTime) {
				Date date = new Date(start);
				String key = station_Id_C + "_" + (date.getMonth() + 1) + "_"+ date.getDate();
				for(Map map : sshTime) {
					String mapKey = (String)map.get("Station_Id_C") + "_" + map.get("Mon") + "_" + map.get("Day");
					if(key.equals(mapKey)) {
						sshSum += (Double)map.get("SunTime");
						break;
					}
				}
			}
			double sshRate = tmpAvgItem.getSSH() * 60 / sshSum;
			int tempSSHRate = (int)Math.round(sshRate * 1000);
			sshRate = tempSSHRate / 10.0;
			ssh.setSshRate(sshRate);
			resultMap.put(tmpAvgItem.getStation_Id_C(), ssh);
		}
		for(SSHItem tmpAvgItem : contrastList) {
			String station_Id_C = tmpAvgItem.getStation_Id_C();
			SSH ssh = resultMap.get(station_Id_C);
			if(ssh == null) {
				continue;
			}
			ssh.setContrastSSH(tmpAvgItem.getSSH());
			double sshTemp = ssh.getSSH() - tmpAvgItem.getSSH();
			int sshTempInt = (int)(sshTemp * 100);
			sshTemp = Math.round(sshTempInt / 10.0);
			ssh.setAnomaly(sshTemp / 10);
			//保留小数一位
			int tmpAvg = (int)Math.round((ssh.getSSH() - tmpAvgItem.getSSH()) / tmpAvgItem.getSSH() * 1000);
			double win_s_2min = tmpAvg / 10.0;
			ssh.setAnomalyRate(win_s_2min);
			resultMap.put(station_Id_C, ssh);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			winList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<SSH> sshList2 = new ArrayList<SSH>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < winList.size(); j++) {
					SSH ssh = winList.get(j);
					String itemStation_Id_C = ssh.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						ssh.setStation_Name(station_Name);
						sshList2.add(ssh);
						break;
					}
				}
			}
			return sshList2;
		}
//		SSHResult sshResult = new SSHResult();
//		ResultDesc resultDesc = null;
//		if("range".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("sshRange");
//		} else if("sameTeam".equals(type)) {
//			resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("sshYears");
//		}
//		sshResult.setDesc(resultDesc);
//		sshResult.setList(winList);
		return winList;
	}
	
	/**
	 * 极端气温统计
	 * @param resultList
	 * @param contrastList
	 * @param type
	 * @return
	 */
	public List<ExtTmp> extTmpResultAnomaly(List<ExtTmpMaxItem> resultList, List<ExtTmpMinItem> contrastList, String stationType) {
		List<ExtTmp> winList = new ArrayList<ExtTmp>();
		Map<String, ExtTmp> resultMap = new HashMap<String, ExtTmp>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		for(ExtTmpMaxItem extTmpMaxItem : resultList) {
			ExtTmp extTmp = new ExtTmp();
			extTmp.setAlti(extTmpMaxItem.getAlti());
			extTmp.setCity(extTmpMaxItem.getCity());
			extTmp.setCnty(extTmpMaxItem.getCnty());
			extTmp.setStation_Id_d(extTmpMaxItem.getStation_Id_d());
			extTmp.setStation_Id_C(extTmpMaxItem.getStation_Id_C());
			extTmp.setStation_Name(extTmpMaxItem.getStation_Name());
			extTmp.setLon(extTmpMaxItem.getLon());
			extTmp.setLat(extTmpMaxItem.getLat());
			extTmp.setTEM_Max(extTmpMaxItem.getTEM_Max());
			extTmp.setTEM_Max_OTime(extTmpMaxItem.getTEM_Max_OTime());
			extTmp.setProvince(extTmpMaxItem.getProvince());
			extTmp.setArea(CommonUtil.getInstance().stationAreaMap.get(extTmpMaxItem.getStation_Id_C()));
			resultMap.put(extTmpMaxItem.getStation_Id_C(), extTmp);
		}
		for(ExtTmpMinItem extTmpMinItem : contrastList) {
			String station_Id_C = extTmpMinItem.getStation_Id_C();
			ExtTmp extTmp = resultMap.get(station_Id_C);
			if(extTmp == null) {
				continue;
			}
			double temMin = extTmpMinItem.getTEM_Min();
			String temMinOTime = extTmpMinItem.getTEM_Min_OTime();
			extTmp.setTEM_Min(temMin);
			extTmp.setTEM_Min_OTime(temMinOTime);
			resultMap.put(station_Id_C, extTmp);
		}
		Iterator<String> it = resultMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			winList.add(resultMap.get(key));
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<ExtTmp> winList2 = new ArrayList<ExtTmp>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < winList.size(); j++) {
					ExtTmp itemExtTmp = winList.get(j);
					String itemStation_Id_C = itemExtTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemExtTmp.setStation_Name(station_Name);
						winList2.add(itemExtTmp);
						break;
					}
				}
			}
			return winList2;
		}
//		ExtTmpResult extTmpResult = new ExtTmpResult();
//		ResultDesc resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("extTmp");
//		extTmpResult.setDesc(resultDesc);
//		extTmpResult.setList(winList);
		return winList;
	}
	
	/**
	 * 高温日数统计
	 * @param tmpMaxCntList
	 * @return
	 */
	public List<TmpMaxCnt> tmpMaxCnt(List<TmpMaxCntItem> tmpMaxCntList, String stationType) {
		TmpMaxCntResult tmpMaxCntResult = new TmpMaxCntResult();
		List<TmpMaxCnt> resultList = new ArrayList<TmpMaxCnt>();
		StationArea stationArea = new StationArea();
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		for(TmpMaxCntItem tmpMaxCntItem : tmpMaxCntList) {
			TmpMaxCnt tmpMaxCnt = new TmpMaxCnt();
			tmpMaxCnt.setAlti(tmpMaxCntItem.getAlti());
			tmpMaxCnt.setArea(CommonUtil.getInstance().stationAreaMap.get(tmpMaxCntItem.getStation_Id_C()));
			tmpMaxCnt.setCity(tmpMaxCntItem.getCity());
			tmpMaxCnt.setCnty(tmpMaxCntItem.getCnty());
			tmpMaxCnt.setGte35(tmpMaxCntItem.getGte35());
			tmpMaxCnt.setGte35lt37(tmpMaxCntItem.getGte35lt37());
			tmpMaxCnt.setGte37(tmpMaxCntItem.getGte37());
			tmpMaxCnt.setGte37lt40(tmpMaxCntItem.getGte37lt40());
			tmpMaxCnt.setGte40(tmpMaxCntItem.getGte40());
			tmpMaxCnt.setLat(tmpMaxCntItem.getLat());
			tmpMaxCnt.setLon(tmpMaxCntItem.getLon());
			tmpMaxCnt.setProvince(tmpMaxCntItem.getProvince());
			tmpMaxCnt.setStation_Id_C(tmpMaxCntItem.getStation_Id_C());
			tmpMaxCnt.setStation_Id_d(tmpMaxCntItem.getStation_Id_d());
			tmpMaxCnt.setStation_Name(tmpMaxCntItem.getStation_Name());
			resultList.add(tmpMaxCnt);
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<TmpMaxCnt> winList2 = new ArrayList<TmpMaxCnt>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < resultList.size(); j++) {
					TmpMaxCnt itemExtTmp = resultList.get(j);
					String itemStation_Id_C = itemExtTmp.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemExtTmp.setStation_Name(station_Name);
						winList2.add(itemExtTmp);
						break;
					}
				}
			}
			return winList2;
		}
//		ResultDesc resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("tmpMax");
//		tmpMaxCntResult.setDesc(resultDesc);
//		tmpMaxCntResult.setList(resultList);
		return resultList;
	}
	
	/**
	 * 能见度低值统计
	 * @param visMinItems
	 * @return
	 */
	public List<VisMin> visMin(List<VisMinItem> visMinItems, String stationType) {
		List<VisMin> visMinList = new ArrayList<VisMin>();
		StationArea stationArea = new StationArea();
		//
//		Map<String, String> stationAreaMap = stationArea.getStationAreaMap();
		for(VisMinItem visMinItem : visMinItems) {
			VisMin visMin = new VisMin();
			visMin.setAlti(visMinItem.getAlti());
			visMin.setArea(CommonUtil.getInstance().stationAreaMap.get(visMinItem.getStation_Id_C()));
			visMin.setCity(visMinItem.getCity());
			visMin.setCnty(visMinItem.getCnty());
			visMin.setLat(visMinItem.getLat());
			visMin.setLon(visMinItem.getLon());
			visMin.setProvince(visMinItem.getProvince());
			visMin.setStation_Id_C(visMinItem.getStation_Id_C());
			visMin.setStation_Id_d(visMinItem.getStation_Id_d());
			visMin.setStation_Name(visMinItem.getStation_Name());
			visMin.setVIS_Min(visMinItem.getVIS_Min());
			visMin.setVIS_Min_OTime(visMinItem.getVIS_Min_OTime());
			visMinList.add(visMin);
		}
		if("AWS".equals(stationType)) {
			//过滤
			List<VisMin> visMinList2 = new ArrayList<VisMin>();
			for(int i = 0; i < awsStations.size(); i++) {
				Station station = awsStations.get(i);
				String station_Id_C = station.getStation_Id_C();
				String station_Name = station.getStation_Name();
				for(int j = 0; j < visMinList.size(); j++) {
					VisMin itemVisMin = visMinList.get(j);
					String itemStation_Id_C = itemVisMin.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						itemVisMin.setStation_Name(station_Name);
						visMinList2.add(itemVisMin);
						break;
					}
				}
			}
			return visMinList2;
		}
//		VisMinResult visMinResult = new VisMinResult();
//		ResultDesc resultDesc = (ResultDesc)ContextLoader.getCurrentWebApplicationContext().getBean("visMin");
//		visMinResult.setDesc(resultDesc);
//		visMinResult.setList(visMinList);
		return visMinList;
	}
}
