package com.spd.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.config.CommonConfig;
import com.spd.dao.cq.impl.MCIAreaDaoImpl;
import com.spd.dao.cq.impl.MCIAreaDaysDaoImpl;
import com.spd.dao.cq.impl.MCIDaoImpl;
import com.spd.dao.cq.impl.MCIStationDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.MCIFileFilter;
import com.spd.tool.PropertiesUtil;

/**
 * MCI数据同步，解析MCI文件
 * @author Administrator
 *
 */
public class MCIExecutor {
	
	private static double RT_SPIW60Param1 = 0.5;

	private static double RT_MIParam1 = 0.6;
	
	private static double RT_SPI90Param1 = 0.2;
	
	private static double RT_SPI150Param1 = 0.1;
	
	private static double RT_SPIW60Param2 = 0.3;

	private static double RT_MIParam2 = 0.5;

	private static double RT_SPI90Param2 = 0.3;

	private static double RT_SPI150Param2 = 0.2;
	
	private static int STARTMCIDAYS = 10; //干旱结束开始的天数

	private static int ENDMCIDAYS = 10; //干旱结束满足的天数
	
	private MCIStationDaoImpl mciStationDaoImpl = new MCIStationDaoImpl();
	
	private MCIDaoImpl mciDaoImpl = new MCIDaoImpl();
	
	private MCIAreaDaysDaoImpl mciAreaDaysDaoImpl = new MCIAreaDaysDaoImpl();

	private MCIAreaDaoImpl mciAreaDaoImpl = new MCIAreaDaoImpl();
	
	/**
	 * 按时间获取到MCI文件
	 * @param datetime
	 * @return
	 */
	public String[] getMCIFileByDate(String[] datetimes) {
		File file = new File(CommonConfig.MCIPATH);
		List<String> fileList = new ArrayList<String>();
		for(String datetime : datetimes) {
			String[] mciFiles = file.list(new MCIFileFilter(datetime));
			for(String mciFile : mciFiles) {
				fileList.add(CommonConfig.MCIPATH + "/" + mciFile);
			}
		}
		String[] resultFiles = new String[fileList.size()];
		for(int i = 0; i < fileList.size(); i++) {
			resultFiles[i] = fileList.get(i);
		}
		return resultFiles;
	}
	
	public List analystFile(String filePath, HashMap<String, Object> existMap, Set<String> stations) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = "";
			while((line = reader.readLine()) != null) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				if(!line.startsWith("INSERT")) {
					continue;
				}
				int startIndex = line.indexOf("VALUES(") + "VALUES(".length();
				int endIndex = line.length() - 2;
				String[] values = line.substring(startIndex, endIndex).split(",");
				//站号
				String station_Id_C = values[0];
				if(!stations.contains(station_Id_C)) {
					continue;
				}
				int month = Integer.parseInt(values[3]);
				String datetime = values[1].substring(0, 4) + "-" + values[1].substring(4, 6) + "-" + values[1].substring(6, 8);
				if(values[29] == null || values[7] == null || values[22] == null || values[24] == null
						|| "null".equals(values[29]) || "null".equals(values[7]) 
						|| "null".equals(values[22]) || "null".equals(values[24]) 
						|| "-999".equals(values[29]) || "-999".equals(values[7]) 
						|| "-999".equals(values[22]) || "-999".equals(values[24])) {
					continue;
				}
				String key = station_Id_C + "_" + datetime;
				if(existMap.containsKey(key)) {
					continue;
				}
				double RT_SPIW60 = Double.parseDouble(values[29]);
				double RT_MI = Double.parseDouble(values[7]);
				double RT_SPI90 = Double.parseDouble(values[22]);
				double RT_SPI150 = Double.parseDouble(values[24]);
				Double MCI = 0.0;
				if(month >= 4 && month <= 10) {
					MCI = RT_SPIW60 * RT_SPIW60Param1 + RT_MI * RT_MIParam1 + RT_SPI90 * RT_SPI90Param1 + RT_SPI150 * RT_SPI150Param1;
				} else {
					MCI = RT_SPIW60 * RT_SPIW60Param2 + RT_MI * RT_MIParam2 + RT_SPI90 * RT_SPI90Param2 + RT_SPI150 * RT_SPI150Param2;
				}
				MCI = roundDouble(MCI);
				mapData.put("station_Id_C", station_Id_C);
				mapData.put("datetime", datetime + " 00:00:00");
				mapData.put("RT_SPIW60", RT_SPIW60);
				mapData.put("RT_MI", RT_MI);
				mapData.put("RT_SPI90", RT_SPI90);
				mapData.put("RT_SPI150", RT_SPI150);
				if(MCI < -10 || MCI > 10) { // 无效数据置空
					MCI = null;
				}
				mapData.put("MCI", MCI);
				dataList.add(mapData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return dataList;
	}
	
	/**
	 * 保留5位小数
	 * @param in
	 * @return
	 */
	public static double roundDouble(double in) {
		int tempInt = (int) (in * 1000000);
		in = Math.round(tempInt / 10.0);
		return in / 100000.0;
	}
	
	public void syncMCI() {
		//获取所有的站点
		Set<String> stations = mciDaoImpl.getStations();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//连续两天的值，用以保证数据能尽可能的入库
		Date date1 = new Date(new Date().getTime() - CommonConstant.DAYTIMES);
		Date date2 = new Date(new Date().getTime() - CommonConstant.DAYTIMES * 2);
		String dateTimeStr1 = sdf.format(date1);
		String dateTimeStr2 = sdf.format(date2);
		String[] dateTimes = new String[]{dateTimeStr1, dateTimeStr2};
		String[] filePaths = getMCIFileByDate(dateTimes);
		if(null == filePaths) {
			return;
		}
//		for(String filePath : filePaths) {
//			System.out.println("filePath:" + filePath);
//		}
		for(int i = 0; i < filePaths.length; i++) {
			String filePath = filePaths[i];
			HashMap<String, Object> existMap = mciDaoImpl.getExistMCI(dateTimes[i]);
			List dataList = analystFile(filePath, existMap, stations);
			mciDaoImpl.insertMCIValue(dataList);
		}
	}
	
	/**
	 * t_MCIStation入库
	 */
	public void syncMCIStation(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		HashMap unEndMap = mciStationDaoImpl.getUnEndMCIStation();
		Set unEndSet = unEndMap.keySet();
		HashMap<String, Integer> mciMap = mciDaoImpl.getStartMCIByTimes(startTime, endTime); //满足开始条件的单站干旱序列
		Iterator<String> it = mciMap.keySet().iterator();
		List dataList = new ArrayList();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			int cnt = mciMap.get(station_Id_C);
			if(!unEndSet.contains(station_Id_C) && cnt == ENDMCIDAYS) { //10天
				//符合开始条件，入库记录
				HashMap dataMap = new HashMap();
				dataMap.put("StartTime", startTime + " 00:00:00");
				dataMap.put("Station_Id_C", station_Id_C);
				dataList.add(dataMap);
			}
		}
		mciStationDaoImpl.insertMCIValue(dataList);
		//判断结束，有结束的话，就计算指数值，并且update记录。
		HashMap<String, Integer> endMCIMap = mciDaoImpl.getEndMCIByTimes(startTime, endTime); //满足结束条件的单站干旱序列
		Iterator<String> endMCIIt =  endMCIMap.keySet().iterator();
		while(endMCIIt.hasNext()) {
			String station_Id_C = endMCIIt.next();
			if(unEndMap.containsKey(station_Id_C) && endMCIMap.get(station_Id_C) == 10) {
				//找到了开始和结束
				String itemStartTime = (String) unEndMap.get(station_Id_C);
				Date endDate = null;
				try {
					endDate = sdf.parse(endTime);
					endDate = new Date(endDate.getTime() - 10 * CommonConstant.DAYTIMES); //往前推算10天的头一天，满足条件
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String itemEndTime = sdf.format(endDate);
				HashMap mciStationResultMap = mciDaoImpl.querySumMCI(itemStartTime, itemEndTime, station_Id_C);
				if(mciStationResultMap == null) continue;
				mciStationDaoImpl.updateData(mciStationResultMap);
			}
		}
	}
	
	/**
	 * 计算干旱发生过程日期
	 * @param datetime
	 * @param station_Id_C
	 */
	public String syncStartMCIStation(String datetime, String station_Id_C) {
		Double mci = mciDaoImpl.getMCIByTimeStation(datetime, station_Id_C);
		if(mci <= -1.0) {
			String preDatetime = CommonTool.addDays(datetime, -1);
			return syncStartMCIStation(preDatetime, station_Id_C);
		} else {
			return CommonTool.addDays(datetime, 1);
		}
	}
	
	/**
	 * 计算干旱结束过程日期
	 * @param datetime
	 * @param station_Id_C
	 */
	public String syncEndMCIStation(String datetime, String station_Id_C) {
		Double mci = mciDaoImpl.getMCIByTimeStation(datetime, station_Id_C);
		if(mci > -1.0) {
			String preDatetime = CommonTool.addDays(datetime, -1);
			return syncEndMCIStation(preDatetime, station_Id_C);
		} else {
			return CommonTool.addDays(datetime, 1);
		}
	}
	
	/**
	 * 查询在指定时间发生干旱的站
	 * @param datetime
	 * @return
	 */
	public List<String> getAllOCCurMCIStations(String datetime) {
		return mciDaoImpl.getAllOCCurMCIStations(datetime);
	}
	
	/**
	 * 查询在指定时间未发生干旱的站
	 * @param datetime
	 * @return
	 */
	public List<String> getAllUnOCCurMCIStations(String datetime) {
		return mciDaoImpl.getAllUnOCCurMCIStations(datetime);
	}
	
	/**
	 * 计算已经开始，但还没有结束的过程
	 * @param datetime
	 * @return
	 */
	public List getAllUnEndMCIStations() {
		return mciStationDaoImpl.getAllUnEndMCIStations();
	}
	
	public void syncMCIStart(String datetime) {
		//1. 计算所有发生了干旱的站
		List<String> occurMCIStations = getAllOCCurMCIStations(datetime);
		//2. 根据发生的站，计算对应的开始结束日期
		List dataList = new ArrayList();
		for(String station_Id_C : occurMCIStations) {
			String startTime = syncStartMCIStation(datetime, station_Id_C);
			//判断是否满足过程，如果满足的话，入库，如果没有满足的话，跳过
			int persistDays = CommonTool.caleDays(startTime, datetime);
			if(persistDays < STARTMCIDAYS) continue;
			boolean flag = mciStationDaoImpl.getStartMCIStation(station_Id_C, startTime);
			if(flag) continue;
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", startTime + " 00:00:00");
			dataMap.put("Station_Id_C", station_Id_C);
			dataList.add(dataMap);
		}
		//开始的干旱过程入库
		mciStationDaoImpl.insertMCIValue(dataList);
	}
	
	/**
	 * 计算MCI结束
	 * @param datetime
	 */
	public void syncMCIEnd(String datetime) {
		//1. 计算所有没有发生干旱的站
		List unOccurMCIStations = getAllUnEndMCIStations();
		if(unOccurMCIStations == null) return;
		//2. 根据发生的站，计算对应的开始、结束日期
		for(int i = 0; i < unOccurMCIStations.size(); i++) {
			HashMap dataMap = (HashMap) unOccurMCIStations.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			String MCIStartTime = (String) dataMap.get("StartTime");
			if(CommonTool.compareTimes(MCIStartTime, datetime) >= 0) continue;//如果结束时间比当前查询的时间还要晚的话，就跳过。
			String startTime = syncEndMCIStation(datetime, station_Id_C);
			int persistDays = CommonTool.caleDays(startTime, datetime);
			if(persistDays < ENDMCIDAYS) continue;
			Integer startId = mciStationDaoImpl.getUnEndMCIIdByStation(station_Id_C, datetime);
			if(startId == null) continue;
			//计算结果入库
			HashMap mciStationResultMap = mciDaoImpl.querySumMCI(MCIStartTime, datetime, station_Id_C);
			if(mciStationResultMap == null) continue;
			mciStationDaoImpl.updateData(mciStationResultMap);
		}
	}
	
	/**
	 * t_mciareadays的计算，每天计算一次，有数据的就update，没有的话，insert
	 * @param datetime
	 */
	public void caleMCIAreaDays(String datetime) {
		HashMap dataMap = mciDaoImpl.queryMCIAreaDays(datetime);
		HashMap existDataMap = mciAreaDaysDaoImpl.getExistMCI(datetime);
		if(existDataMap.size() == 0) {
			List dataList = new ArrayList();
			dataList.add(dataMap);
			mciAreaDaysDaoImpl.insertMCIValue(dataList);
			return;
		}
		String dataKey = dataMap.get("cnt") + "_" + dataMap.get("datetime");
		String existKey = existDataMap.get("cnt") + "_" + existDataMap.get("datetime");
		if(dataKey.equals(existKey)) {
			return;
		}
		if(dataMap.get("datetime").equals(existDataMap.get("datetime")) && dataMap.get("cnt") != existDataMap.get("cnt")) {
			//update
			mciAreaDaysDaoImpl.updateMCIValue(dataMap);
		}
	}
	
	/**
	 * 计算t_MCIArea中的开始日期
	 */
	public void syncStartMCIArea(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//按照四种方式计算开始时间，然后找到开始最早的那一个,继续往前追溯
		MCIAreaDaoImpl mciAreaDaoImpl = new MCIAreaDaoImpl();
		Date[] dates = mciAreaDaoImpl.getExtTimes();
		try {
			Date dateCurrent = sdf.parse(datetime);
			if(dates != null && dateCurrent.getTime() < dates[1].getTime()) {
				return;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		//1. 2天内，超过7个站，允许0天中断
		int days = 2, stationCnt = 7, noDataDays = 0;
		String startTime = caleStartTime(days, datetime);
		List<String[]> timesList = new ArrayList<String[]>();
		String[] times1 = mciAreaDaoImpl.getStartTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
		timesList.add(times1);
		//3-6天允许两天中断
		for(int i = 3; i <= 6; i++) {
			days = i; stationCnt = 7; noDataDays = 2;
			startTime = caleStartTime(days, datetime);
			times1 = mciAreaDaoImpl.getStartTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
			timesList.add(times1);
		}
		//7-10天允许5天中断
		for(int i = 7; i <= 10; i++) {
			days = i; stationCnt = 7; noDataDays = 5;
			startTime = caleStartTime(days, datetime);
			times1 = mciAreaDaoImpl.getStartTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
			timesList.add(times1);
		}
		//11天以上允许6天中断
		days = 11; stationCnt = 7; noDataDays = 6;
		startTime = caleStartTime(days, datetime);
		String[] timesLast = mciAreaDaoImpl.getStartTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
		timesList.add(timesLast);
		//遍历timesList，找到最早的那一天
		long startTmpTimes = 0L, endTmpTimes = 0L;
		for(int i = 0; i < timesList.size(); i++) {
			String[] times = timesList.get(i);
			if(times == null) continue;
			Date date1 = null, date2 = null;
			try {
				date1 = sdf.parse(times[0]);
				date2 = sdf.parse(times[1]);
				long time1 = date1.getTime();
				long time2 = date2.getTime();
				if(startTmpTimes == 0) {
					startTmpTimes = time1;
					endTmpTimes = time1;
				} 
				if(startTmpTimes > time1) {
					startTmpTimes = time1;
				}
				if(endTmpTimes < time2) {
					endTmpTimes = time2;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//找到第一天后，再继续往前遍历，直到找到不符合条件的
//		if(timesLast != null) {
			int startDays = 12;
			while(true) {
				stationCnt = 7; noDataDays = 6;
				startTime = caleStartTime(startDays, datetime);
				startDays++;
				times1 = mciAreaDaoImpl.getStartTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
				if(times1 != null) {
					Date date = null, endDate = null;
					try {
						date = sdf.parse(times1[0]);
						endDate = sdf.parse(times1[1]);
						endTmpTimes = endDate.getTime();
						startTmpTimes = date.getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
//		}
		//然后对比表中t_mciarea中最新一条数据，如果有开始日期 >=该日期的，则修改该记录，否则插入数据。
		String startTimeStr = sdf.format(new Date(startTmpTimes)); //开始时间 需要在t_MCIStation中，按照startTmpTimes往下查询，第7条记录所在的位置
		if("1970-01-01".equals(startTimeStr)) {
			return;
		}
		String startTmpTime1 = startTimeStr;
		startTimeStr = mciAreaDaoImpl.queryStartTime(startTimeStr, 7);
		String endTimeStr = sdf.format(new Date(endTmpTimes)); //开始时间 datetime为结束日期
		String startTmpTime2 = endTimeStr;
		mciAreaDaoImpl.addMCIAreaStartTime(startTimeStr, startTmpTime1, startTmpTime2);
	}
	
	/**
	 * 计算MCI的结束
	 * @param datetime
	 */
	public void syncEndMCIArea(String datetime) {
		//先决条件，比最大的开始时间要大
		MCIAreaDaoImpl mciAreaDaoImpl = new MCIAreaDaoImpl();
		Date[] dates = mciAreaDaoImpl.getExtTimes();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 每次判断结束，如果时间靠后的有一次结束，就取最后的一次为准。
//		try {
//			Date dateCurrent = sdf.parse(datetime);
//			if(dates != null && dateCurrent.getTime() < dates[0].getTime()) {
//				return;
//			}
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		//按照四种方式计算开始时间，然后找到开始最早的那一个,继续往前追溯
		//1. 2天内，超过7个站，允许0天中断
		int days = 2, stationCnt = 7, noDataDays = 0;
		String startTime = caleStartTime(days, datetime);
		List<String[]> timesList = new ArrayList<String[]>();
		String[] times1 = mciAreaDaoImpl.getEndTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
		timesList.add(times1);
		//3-6天允许两天中断
		for(int i = 3; i <= 6; i++) {
			days = i; stationCnt = 7; noDataDays = 2;
			startTime = caleStartTime(days, datetime);
			times1 = mciAreaDaoImpl.getEndTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
			timesList.add(times1);
		}
		//7-10天允许5天中断
		for(int i = 7; i <= 10; i++) {
			days = i; stationCnt = 7; noDataDays = 5;
			startTime = caleStartTime(days, datetime);
			times1 = mciAreaDaoImpl.getEndTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
			timesList.add(times1);
		}
		//11天以上允许6天中断
		days = 11; stationCnt = 7; noDataDays = 6;
		startTime = caleStartTime(days, datetime);
		String[] timesLast = mciAreaDaoImpl.getEndTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
		timesList.add(timesLast);
		//遍历timesList，找到最早的那一天
		long startTmpTimes = 0L, endTmpTimes = 0L;
		for(int i = 0; i < timesList.size(); i++) {
			String[] times = timesList.get(i);
			if(times == null) continue;
			Date date1 = null, date2 = null;
			try {
				date1 = sdf.parse(times[0]);
				date2 = sdf.parse(times[1]);
				long time1 = date1.getTime();
				long time2 = date2.getTime();
				if(startTmpTimes == 0) {
					startTmpTimes = time1;
					endTmpTimes = time1;
				} 
				if(startTmpTimes > time1) {
					startTmpTimes = time1;
				}
				if(endTmpTimes < time2) {
					endTmpTimes = time2;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//找到第一天后，再继续往前遍历，直到找到不符合条件的
//		if(timesLast != null) {
			int startDays = 12;
			while(true) {
				stationCnt = 7; noDataDays = 6;
				startTime = caleStartTime(startDays, datetime);
				startDays++;
				times1 = mciAreaDaoImpl.getEndTimeListByTimes(startTime, datetime, stationCnt, noDataDays);
				if(times1 != null) {
					Date date = null, date2 = null;
					try {
						date = sdf.parse(times1[0]);
						date2 = sdf.parse(times1[1]);
						startTmpTimes = date.getTime();
						endTmpTimes = date2.getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
//		}
		//然后对比表中t_mciarea中最新一条数据，如果有开始日期 >=该日期的，则修改该记录，否则插入数据。
		String startTimeStr = sdf.format(new Date(startTmpTimes)); //开始时间 datetime为结束日期
		if("1970-01-01".equals(startTimeStr)) {
			return;
		}
		String endTmpTime1 = startTimeStr;
		String endTimeStr = sdf.format(new Date(endTmpTimes)); //开始时间 datetime为结束日期
		String endTmpTime2 = endTimeStr; 
		String endTime = mciAreaDaoImpl.queryEndTime(endTimeStr, 7);
		mciAreaDaoImpl.updateMCIAreaEndTime(endTime, endTmpTime1, endTmpTime2);
	}
	private String caleStartTime(int days, String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		Date startDate = new Date(date.getTime() - (days - 1) * CommonConstant.DAYTIMES);
		String startTime = sdf.format(startDate);
		return startTime;
	}
	
	/**
	 * 计算t_mciarea
	 */
//	public void caleMCIArea(String endTime) {
//		//查询t_mciareadays，然后计算结果，计算的时候，参数开始时间为 >= max(t_mciarea.EndTime)
//		String startTime = mciAreaDaoImpl.getMaxStartTime(); //最迟的时间作为现在的开始时间
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		List resultList = mciAreaDaysDaoImpl.getMCIAreaDaysByTimes(startTime, endTime);
//		Date minDate = null, maxDate = null;
//		if(resultList != null && resultList.size() > 0) {
//			for(int i = 0; i < resultList.size(); i++) {
//				HashMap dataMap = (HashMap) resultList.get(i);
//				String datetime = (String) dataMap.get("datetime");
//				int cnt = ((Long) dataMap.get("cnt")).intValue();
//				if(i == 0) {
//					try {
//						minDate = sdf.parse(datetime);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				} else if (i == resultList.size() - 1) {
//					try {
//						maxDate = sdf.parse(datetime);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		
//		int days = (int) ((maxDate.getTime() - minDate.getTime()) / CommonConstant.DAYTIMES);
//		if(days == 2) {
//			
//		} else if(days >= 3 && days <= 6) {
//			
//		} else if(days >= 7 && days <= 10) {
//			
//		} else if(days >= 11) {
//			
//		}
//		//如果结果中有重复的，则update，否则insert
//	}
	
	public static void record() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTimeStr = "2016-08-10";
		String endTimeStr = "2016-12-12";
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		MCIExecutor mciExecutor = new MCIExecutor();
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			Date date = new Date(i);
			System.out.println(sdf.format(date));
//			mciExecutor.syncMCIStart(sdf.format(i));
//			mciExecutor.syncMCIEnd(sdf.format(i));
			mciExecutor.syncStartMCIArea(sdf.format(date));
			mciExecutor.syncEndMCIArea(sdf.format(date));
		}
		
	}
	
	public void syncMCIs() {
		//////////注释开始
		//t_MCI表的入库
		syncMCI();
		//t_MCIStation表入库
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis() - CommonConstant.DAYTIMES); //从头一天开始计算 
		String dateStr = sdf.format(date);
		syncMCIStart(dateStr);
		syncMCIEnd(dateStr);
		//t_MCIArea
		syncStartMCIArea(dateStr);
		syncEndMCIArea(dateStr);
		//////////注释结束
//		mciExecutor.record(); // 补录
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		MCIExecutor mciExecutor = new MCIExecutor();
		mciExecutor.syncMCIs();
	}

}
