package com.spd.schedule;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.spd.dao.cq.impl.HouTmpAvgDao;
import com.spd.dao.cq.impl.LowTmpStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 低温单站统计
 * @author Administrator
 *
 */
public class LowTmpStationSync {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private HouTmpAvgDao houTmpAvgDao = new HouTmpAvgDao();
	//常年开始年
	private static int STARTYEAR = 1981;
	//常年结束年
	private static int ENDYEAR = 2010;
	//低于常年平均值的量，就算是低温
	private static double LOWTMP = -2;

	private LowTmpStationDaoImpl lowTmpStationDaoImpl = new LowTmpStationDaoImpl();
	
	/**
	 * 计算常年的单站候平均值
	 * @param month
	 * @param hou
	 * @return
	 */
	private Double getYearsHouAvgTmp(int month, int[] hous, String station_Id_C) {
		Double result = houTmpAvgDao.getAvgTmpByStationAndYears(STARTYEAR, ENDYEAR, month, hous, station_Id_C);
		return result;
	}
	
	/**
	 * 根据年月候，计算候平均气温
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private Set<String> getHouAvgByTimes(int year, int month, int hou) {
		Set<String> stationSet = new HashSet<String>();
		List resultList = houTmpAvgDao.getHouAvgTmp(year, month, hou);
		if(resultList == null || resultList.size() == 0) return null;
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			stationSet.add(station_Id_C);
		}
		return stationSet;
	}
	
	private int[] analyst(String station_Id_C, int year, int month, int hou) {
		Double avgTmp = houTmpAvgDao.getHouAvgTmpByStation(station_Id_C, year, month, hou);
		if(avgTmp == null) {
			return new int[]{year, month, hou};
		}
		int[] hous = new int[1];
		hous[0] = hou;
		Double yearsAvgTmp = CommonTool.roundDouble(getYearsHouAvgTmp(month, hous, station_Id_C));
		BigDecimal avgTmpDecimal = new BigDecimal(avgTmp + "");
		BigDecimal yearAvgTmpDecimal = new BigDecimal(yearsAvgTmp + "");
		if(avgTmpDecimal.subtract(yearAvgTmpDecimal).doubleValue() <= LOWTMP) {
			int[] yearMonthHou = CommonTool.addHou(year, month, hou, -1);
			return analyst(station_Id_C, yearMonthHou[0], yearMonthHou[1], yearMonthHou[2]);
		} else {
			return new int[]{year, month, hou};
		}
	}
	
	/**
	 * 计算从开始时间到结束时间，当年的平均气温，历年的平均气温，然后计算距平
	 * @param startHous
	 * @param endHous
	 * @return
	 */
	private Double[] cale(int[] startHous, int[] endHous, String station_Id_C) {
		Double sumAvgTmp = 0.0, sumYearAvgTmp = 0.0;
		int cnt = 0;
		while(CommonTool.compare(startHous, endHous) <= 0) {
			Double avgTmp = houTmpAvgDao.getHouAvgTmpByStation(station_Id_C, startHous[0], startHous[1], startHous[2]);
			if(avgTmp == null) continue;
			sumAvgTmp += avgTmp;
			Double yearAvgTmp = houTmpAvgDao.getAvgTmpByStationAndYears(STARTYEAR, ENDYEAR, startHous[1], new int[]{startHous[2]}, station_Id_C);
			if(yearAvgTmp == null) continue;
			sumYearAvgTmp += yearAvgTmp;
			cnt++;
			startHous = CommonTool.addHou(startHous[0], startHous[1], startHous[2], 1);
		}
		Double avgTmp = CommonTool.roundDouble(sumAvgTmp / cnt);
		Double avgYearTmp = CommonTool.roundDouble(sumYearAvgTmp / cnt);
		Double anomaly = CommonTool.roundDouble(avgTmp - avgYearTmp);
		return new Double[]{avgTmp, anomaly};
	}
	
	public void sync(String datetime) {
		List dataList = new ArrayList();
		boolean isLastDayInHou = CommonTool.isLastDayInHou(datetime);
		if(!isLastDayInHou) return;
		int[] yearMonthHou = CommonTool.getYearMonthHou(datetime);
		Set<String> stationSet = getHouAvgByTimes(yearMonthHou[0], yearMonthHou[1], yearMonthHou[2]);
		if(stationSet == null) return;
		Iterator<String> it = stationSet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			int[] resultYearMonthHou = analyst(station_Id_C, yearMonthHou[0], yearMonthHou[1], yearMonthHou[2]);
			if(resultYearMonthHou == null) continue;
			resultYearMonthHou = CommonTool.addHou(resultYearMonthHou[0], resultYearMonthHou[1], resultYearMonthHou[2], 1);
			int hous = CommonTool.minusHous(yearMonthHou, resultYearMonthHou);
			if(hous >= 2) {
				//一般低温或者重度低温
				HashMap dataMap = new HashMap();
				dataMap.put("Station_Id_C", station_Id_C);
				dataMap.put("EndTime", datetime + " 00:00:00");
				dataMap.put("StartTime", CommonTool.chgStartTimeByHou(resultYearMonthHou) + " 00:00:00");
				dataMap.put("PersistHous", hous);
				Double[] tmps = cale(resultYearMonthHou, yearMonthHou, station_Id_C);
				dataMap.put("AvgTmp", tmps[0]);
				dataMap.put("Anomaly", tmps[1]);
				dataList.add(dataMap);
			} 
		}
		//入库
		lowTmpStationDaoImpl.insertLowTmpStationValue(dataList);
	}
	
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		LowTmpStationSync lowTmpStationSync = new LowTmpStationSync();
//		lowTmpStationSync.sync("1954-12-25");
		//测试开始
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "2016-09-10";
		String endTime = "2016-09-10";
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
				String time = sdf.format(new Date(i));
				System.out.println(time);
				lowTmpStationSync.sync(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//测试结束
	}
}
