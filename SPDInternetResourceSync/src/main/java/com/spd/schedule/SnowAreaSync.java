package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.cq.impl.SnowAreaDaoImpl;
import com.spd.dao.cq.impl.SnowDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 积雪区域评估
 * @author Administrator
 *
 */
public class SnowAreaSync {

	private SnowDaoImpl snowDao = new SnowDaoImpl();

	private SnowAreaDaoImpl snowAreaDao = new SnowAreaDaoImpl();
	
	private static int STATIONCNT = 7;//达到20%的站数
	
	private static int LMAX = 34; //最大站数影响范围

	private static int LMIN = 7; //最小站数影响范围
	
	private static int MAXPERSISTDAYS = 23; //历史最大持续时间

	private static int MINPERSISTDAYS = 1; //历史最大持续时间
	
	private static double MAXDEPTH = 5.04;//平均最大积雪深度

	private static double MINDEPTH = 0;//平均最小积雪深度
	
	private static double MAXPROCESSDEPTH = 26;//过程最大积雪深度

	private static double MINPROCESSDEPTH = 1;//过程最小积雪深度
	
	public void sync(String datetime) {
		String startTime = calcProcess(datetime);
		int days = CommonTool.caleDays(startTime, datetime);
		if(days < 1) return;
		cale(startTime, datetime);
	}
	
	/**
	 * 计算积雪过程
	 * @param datetime
	 * @return
	 */
	public String calcProcess(String datetime) {
		List list = snowDao.getSnowDepthByTime(datetime);
		if(list == null || list.size() == 0) {
			return CommonTool.addDays(datetime, 1);
		}
		String preDatetime = CommonTool.addDays(datetime, -1);
		return calcProcess(preDatetime);
	}

	/**
	 * 计算降雪过程的参数等
	 * @param startTime
	 * @param endTime
	 */
	public void cale(String startTime, String endTime) {
		List resultList = snowDao.getSnowDepthByTimes(startTime, endTime);
		HashMap<String, Integer> cntMap = new HashMap<String, Integer>(); // key:日期，value:站数
		if(resultList != null && resultList.size() > 0) {
			//最大影响范围（L）
			int maxStationCnt = 0;
			//最大积雪深度（M）
			double maxSnowDepth = 0.0;
			//平均积雪深度（A）
			double avgSnowDepth = 0.0;
			//持续天数（T）
			int persistDays = CommonTool.caleDays(startTime, endTime);
			
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				String datetime = (String) dataMap.get("datetime");
				Double snow_Depth = (Double) dataMap.get("Snow_Depth");
				if(snow_Depth == 999999) {
					snow_Depth = 0.0;
				}
				if(snow_Depth >= maxSnowDepth) {
					maxSnowDepth = snow_Depth;
				}
				if(cntMap.containsKey(datetime)) {
					cntMap.put(datetime, cntMap.get(datetime) + 1);
				} else {
					cntMap.put(datetime, 1);
				}
				avgSnowDepth += snow_Depth;
			}
			avgSnowDepth /= resultList.size();
			//判断是否有一天的站数达到20%以上
			boolean startAreaSnow = false;
			Iterator cntIt = cntMap.keySet().iterator();
			while(cntIt.hasNext()) {
				String key = (String) cntIt.next();
				int cnt = cntMap.get(key);
				if(cnt >= STATIONCNT) {
					startAreaSnow = true;
				}
				if(cnt >= maxStationCnt) {
					maxStationCnt = cnt;
				}
			}
			if(!startAreaSnow) return;
			//计算各种参数，结果
			double hisMaxSnowDepth = snowDao.getMaxSnowDepth();
			//TODO， 在不计算综合指数的情况下，入库。
			Double IA = (persistDays - MINPERSISTDAYS + 0.0) / (MAXPERSISTDAYS - MINPERSISTDAYS);
			Double IB = (maxStationCnt - LMIN + 0.0) / (LMAX - LMIN);
			Double IC = (avgSnowDepth - MINDEPTH) / (MAXDEPTH - MINDEPTH);
			Double ID = (maxSnowDepth - MINPROCESSDEPTH) / (MAXPROCESSDEPTH - MINPROCESSDEPTH);
			double strength = 0.1 * IA + 0.4 * IB + 0.3 * IC + 0.2 * ID; 
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", startTime + " 00:00:00");
			dataMap.put("EndTime", endTime + " 00:00:00");
			dataMap.put("MaxStations", maxStationCnt);
			dataMap.put("AvgDepth", avgSnowDepth);
			dataMap.put("MaxDepth", maxSnowDepth);
			dataMap.put("Strength", strength);
			List dataList = new ArrayList();
			dataList.add(dataMap);
			int id = snowAreaDao.getExistData(startTime);
			if(id == -1) {
				//insert
				snowAreaDao.insertSnowValue(dataList);
			} else {
				//update
				dataMap.put("id", id);
				snowAreaDao.update(dataList);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		SnowAreaSync snowAreaSync = new SnowAreaSync();
		String startTimeStr = "1975-12-14";
//		String startTimeStr = "2000-01-31";
		String endTimeStr = "2016-12-14";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			System.out.println(timeStr);
			snowAreaSync.sync(timeStr);
		}
	}

}
