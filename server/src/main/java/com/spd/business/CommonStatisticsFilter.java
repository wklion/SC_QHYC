package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.spd.common.CloCovResult;
import com.spd.common.DataCompleteResult;
import com.spd.common.TmpGapByYearsResult;
import com.spd.common.TmpGapTimesResult;
import com.spd.common.WinAvg2MinResult;
import com.spd.pojo.AvgMaxTmp;
import com.spd.pojo.AvgMinTmp;
import com.spd.pojo.AvgTmp;
import com.spd.pojo.ExtTmp;
import com.spd.pojo.PreCnt;
import com.spd.pojo.PreSum;
import com.spd.pojo.PrsAvg;
import com.spd.pojo.RHU;
import com.spd.pojo.SSH;
import com.spd.pojo.TmpGapAvgYearResult;
import com.spd.pojo.TmpMaxCnt;
import com.spd.pojo.VisMin;
import com.spd.pojo.Win_s_2min_avg;

/**
 * 过滤结果
 * @author Administrator
 *
 */
public class CommonStatisticsFilter {

	private Set<String> station_Id_Cs = new HashSet<String>();
	
	public CommonStatisticsFilter(Set<String> station_Id_Cs) {
		this.station_Id_Cs = station_Id_Cs;
	}
	
	
	public List<TmpGapAvgYearResult> filterTmpGapResult(List<TmpGapAvgYearResult> tmpGapTimesResult) {
		if(station_Id_Cs.size() == 0) {
			return tmpGapTimesResult;
		}
		List<TmpGapAvgYearResult> dataCompleteResult2 = new ArrayList<TmpGapAvgYearResult>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < tmpGapTimesResult.size(); i++) {
				TmpGapAvgYearResult avgMinTmp = tmpGapTimesResult.get(i);
				String itemStation_Id_C = avgMinTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					dataCompleteResult2.add(avgMinTmp);
				}
			}
		}
		return dataCompleteResult2;
	}
	
	public List<TmpGapTimesResult> filterTmpGapTimesResult(List<TmpGapTimesResult> tmpGapTimesResult) {
		if(station_Id_Cs.size() == 0) {
			return tmpGapTimesResult;
		}
		List<TmpGapTimesResult> dataCompleteResult2 = new ArrayList<TmpGapTimesResult>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < tmpGapTimesResult.size(); i++) {
				TmpGapTimesResult avgMinTmp = tmpGapTimesResult.get(i);
				String itemStation_Id_C = avgMinTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					dataCompleteResult2.add(avgMinTmp);
				}
			}
		}
		return dataCompleteResult2;
	}
	
	public List<DataCompleteResult> filterDataCompleteResult(List<DataCompleteResult> dataCompleteResult) {
		if(station_Id_Cs.size() == 0) {
			return dataCompleteResult;
		}
		List<DataCompleteResult> dataCompleteResult2 = new ArrayList<DataCompleteResult>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < dataCompleteResult.size(); i++) {
				DataCompleteResult avgMinTmp = dataCompleteResult.get(i);
				String itemStation_Id_C = avgMinTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					dataCompleteResult2.add(avgMinTmp);
				}
			}
		}
		return dataCompleteResult2;
	}
	
	public List<AvgTmp> filterAvgTem(List<AvgTmp> avgTmpResult) {
		if(station_Id_Cs.size() == 0) {
			return avgTmpResult;
		}
		List<AvgTmp> avgTmpResult2 = new ArrayList<AvgTmp>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < avgTmpResult.size(); i++) {
				AvgTmp avgTmp = avgTmpResult.get(i);
				String itemStation_Id_C = avgTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					avgTmpResult2.add(avgTmp);
				}
			}
		}
		return avgTmpResult2;
	}
	
	public List<AvgMinTmp> filterAvgMinTmp(List<AvgMinTmp> avgMinTmpResult) {
		if(station_Id_Cs.size() == 0) {
			return avgMinTmpResult;
		}
		List<AvgMinTmp> avgMinTmpResult2 = new ArrayList<AvgMinTmp>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < avgMinTmpResult.size(); i++) {
				AvgMinTmp avgMinTmp = avgMinTmpResult.get(i);
				String itemStation_Id_C = avgMinTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					avgMinTmpResult2.add(avgMinTmp);
				}
			}
		}
		return avgMinTmpResult2;
	}
	
	public List<AvgMaxTmp> filterAvgTemMax(List<AvgMaxTmp> avgMaxTmpResult) {
		if(station_Id_Cs.size() == 0) {
			return avgMaxTmpResult;
		}
		List<AvgMaxTmp> avgTmpResult2 = new ArrayList<AvgMaxTmp>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < avgMaxTmpResult.size(); i++) {
				AvgMaxTmp avgMaxTmp = avgMaxTmpResult.get(i);
				String itemStation_Id_C = avgMaxTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					avgTmpResult2.add(avgMaxTmp);
				}
			}
		}
		
		return avgTmpResult2;
	}
	
	public List<PreSum> filterSumPre(List<PreSum> preSumResult) {
		if(station_Id_Cs.size() == 0) {
			return preSumResult;
		}
		List<PreSum> preSumResult2 = new ArrayList<PreSum>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < preSumResult.size(); i++) {
				PreSum preSum = preSumResult.get(i);
				String itemStation_Id_C = preSum.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					preSumResult2.add(preSum);
				}
			}
		}
		
		return preSumResult2;
	}
	
	public List<RHU> filterRHU(List<RHU> rhuResult) {
		if(station_Id_Cs.size() == 0) {
			return rhuResult;
		}
		List<RHU> rhuResult2 = new ArrayList<RHU>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < rhuResult.size(); i++) {
				RHU rhu = rhuResult.get(i);
				String itemStation_Id_C = rhu.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					rhuResult2.add(rhu);
				}
			}
		}
		
		return rhuResult2;
	}
	
	public List<Win_s_2min_avg> filterWin_s_2min_avg(List<Win_s_2min_avg> win_2_2min_avgResult) {
		if(station_Id_Cs.size() == 0) {
			return win_2_2min_avgResult;
		}
		List<Win_s_2min_avg> win_2_2min_avgResult2 = new ArrayList<Win_s_2min_avg>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < win_2_2min_avgResult.size(); i++) {
				Win_s_2min_avg win_S_2min_avg = win_2_2min_avgResult.get(i);
				String itemStation_Id_C = win_S_2min_avg.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					win_2_2min_avgResult2.add(win_S_2min_avg);
				}
			}
		}
		
		return win_2_2min_avgResult2;
	}
	
	public List<PrsAvg> filterPrsAvg(List<PrsAvg> prsAvgResult) {
		if(station_Id_Cs.size() == 0) {
			return prsAvgResult;
		}
		List<PrsAvg> prsAvgResult2 = new ArrayList<PrsAvg>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < prsAvgResult.size(); i++) {
				PrsAvg prsAvg = prsAvgResult.get(i);
				String itemStation_Id_C = prsAvg.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					prsAvgResult2.add(prsAvg);
				}
			}
		}
		
		return prsAvgResult2;
	}
	
	public List<ExtTmp> filterExtTmp(List<ExtTmp> extTmpResult) {
		if(station_Id_Cs.size() == 0) {
			return extTmpResult;
		}
		List<ExtTmp> extTmpResult2 = new ArrayList<ExtTmp>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < extTmpResult.size(); i++) {
				ExtTmp extTmp = extTmpResult.get(i);
				String itemStation_Id_C = extTmp.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					extTmpResult2.add(extTmp);
				}
			}
		}
		
		return extTmpResult2;
	}
	
	public List<VisMin> filterVisMin(List<VisMin> visMinResult) {
		if(station_Id_Cs.size() == 0) {
			return visMinResult;
		}
		List<VisMin> visMinResult2 = new ArrayList<VisMin>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < visMinResult.size(); i++) {
				VisMin visMin = visMinResult.get(i);
				String itemStation_Id_C = visMin.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					visMinResult2.add(visMin);
				}
			}
		}
		
		return visMinResult2;
	}
	
	public List<PreCnt> filterPreCnt(List<PreCnt> preCntResult) {
		if(station_Id_Cs.size() == 0) {
			return preCntResult;
		}
		List<PreCnt> preCntResult2 = new ArrayList<PreCnt>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < preCntResult.size(); i++) {
				PreCnt preCnt = preCntResult.get(i);
				String itemStation_Id_C = preCnt.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					preCntResult2.add(preCnt);
				}
			}
		}
		
		return preCntResult2;
	}
	
	public List<TmpMaxCnt> filterTmpMaxCnt(List<TmpMaxCnt> tmpMaxCntResult) {
		if(station_Id_Cs.size() == 0) {
			return tmpMaxCntResult;
		}
		List<TmpMaxCnt> tmpMaxCntResult2 = new ArrayList<TmpMaxCnt>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < tmpMaxCntResult.size(); i++) {
				TmpMaxCnt tmpMaxCnt = tmpMaxCntResult.get(i);
				String itemStation_Id_C = tmpMaxCnt.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					tmpMaxCntResult2.add(tmpMaxCnt);
				}
			}
		}
		
		return tmpMaxCntResult2;
	}
	
	public List<SSH> filterSSH(List<SSH> sshtResult) {
		if(station_Id_Cs.size() == 0) {
			return sshtResult;
		}
		List<SSH> sshResult2 = new ArrayList<SSH>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < sshtResult.size(); i++) {
				SSH ssh = sshtResult.get(i);
				String itemStation_Id_C = ssh.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					sshResult2.add(ssh);
				}
			}
		}
		
		return sshResult2;
	}
	
	public List<LinkedHashMap> filterClimData(List<LinkedHashMap> dataList) {
		if(station_Id_Cs.size() == 0) {
			return dataList;
		}
		List<LinkedHashMap> dataList2 = new ArrayList();
		for(int i = 0; i < dataList.size(); i++) {
			LinkedHashMap itemMap = dataList.get(i);
			Set set = itemMap.keySet();
			Iterator it = set.iterator();
			LinkedHashMap resultMap = new LinkedHashMap();
			while(it.hasNext()) {
				String key = (String) it.next();
				String station_Id_C = key.split("_")[0];
				if(station_Id_Cs.contains(station_Id_C) || "Date".equals(key)) {
					Object result = itemMap.get(key);
					resultMap.put(key, result);
				}
			}
			dataList2.add(resultMap);
		}
		return dataList2;
	}
	
	public List filterClimDataByTimes(List dataList) {
		if(station_Id_Cs.size() == 0) {
			return dataList;
		}
		List result2 = new ArrayList();
		Iterator<String> it = station_Id_Cs.iterator();
//		while(it.hasNext()) {
		for(int i = 0; i < dataList.size(); i++) {
			HashMap itemMap = (HashMap) dataList.get(i);
			String station_id_C = (String) itemMap.get("Station_Id_C");
			if(station_Id_Cs.contains(station_id_C)) {
				result2.add(itemMap);
				break;
			}
		}
//		}
		
		return result2;
	}
	
	public List<WinAvg2MinResult> filterWinAvg(List<WinAvg2MinResult> winAvgtResult) {
		if(station_Id_Cs.size() == 0) {
			return winAvgtResult;
		}
		List<WinAvg2MinResult> winAvgResult2 = new ArrayList<WinAvg2MinResult>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < winAvgtResult.size(); i++) {
				WinAvg2MinResult winAvg2MinResult = winAvgtResult.get(i);
				String itemStation_Id_C = winAvg2MinResult.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					winAvgResult2.add(winAvg2MinResult);
				}
			}
		}
		return winAvgResult2;
	}
	
	public List<CloCovResult> filterCloCov(List<CloCovResult> cloCovResult) {
		if(station_Id_Cs.size() == 0) {
			return cloCovResult;
		}
		List<CloCovResult> cloCovResult2 = new ArrayList<CloCovResult>();
		Iterator<String> it = station_Id_Cs.iterator();
		while(it.hasNext()) {
			String station_id_C = it.next();
			for(int i = 0; i < cloCovResult.size(); i++) {
				CloCovResult winAvg2MinResult = cloCovResult.get(i);
				String itemStation_Id_C = winAvg2MinResult.getStation_Id_C();
				if(station_id_C.equals(itemStation_Id_C)) {
					cloCovResult2.add(winAvg2MinResult);
				}
			}
		}
		return cloCovResult2;
	}
	
}
