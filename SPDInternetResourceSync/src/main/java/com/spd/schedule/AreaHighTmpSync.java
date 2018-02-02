package com.spd.schedule;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.spd.dao.cq.impl.AreaHighAreaResultDao;
import com.spd.dao.cq.impl.AreaHighTmpDao;
import com.spd.dao.cq.impl.AreaHighTmpProcessDao;
import com.spd.dao.cq.impl.AreaHighTmpSIDao;
import com.spd.dao.cq.impl.AreaHighTmpYearResultDao;
import com.spd.dao.cq.impl.HighTmpDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;

/**
 * 区域高温数据同步
 * @author Administrator
 *
 */
public class AreaHighTmpSync {

	private int STATIONSUM = 34; // 定义自动站个数为34
	/**
	 * 根据高温日期，统计改天高温的数
	 */
	public boolean syncAreaHighTmp(String datetime) {
		//t_AreaHighTmp
		AreaHighTmpDao areaHighTmpDao = new AreaHighTmpDao();
		HighTmpDaoImpl highTmpDaoImpl = new HighTmpDaoImpl();
		List list = highTmpDaoImpl.getDataByTime(datetime);
		if(list != null && list.size() > 0) {
			HashMap data = (HashMap) list.get(0);
			Long cnt = (Long) data.get("cnt");
			HashMap dataMap = new HashMap();
			dataMap.put("datetime", datetime + " 00:00:00");
			dataMap.put("stationCnt", cnt.intValue());
			List dataList = new ArrayList();
			dataList.add(dataMap);
			//判断t_AreaHighTmp中是否已经存在
			int id = areaHighTmpDao.isExist(datetime);
			if(-1 == id) {
				//入库
				areaHighTmpDao.insertTemAvgHouValue(dataList);
				return true;
			} else {
				dataMap.put("id", id);
				return areaHighTmpDao.updateData(dataMap);
			}
		}
		return false;
	}
	
	/**
	 * 根据当前日期，同步高温过程
	 * @param endDatetime
	 */
	public String[] syncAreaHighTmpProcess(String endDatetime) {
		//AreaHighTmpProcess
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//默认把开始时间设置在当前的第一天。不可能有高温跨年都持续的
		AreaHighTmpProcessDao areaHighTmpProcessDao = new AreaHighTmpProcessDao();
		AreaHighTmpDao areaHighTmpDao = new AreaHighTmpDao(); 
		String startDateTime = endDatetime.substring(0, 4) + "-01-01";
		List resultList = areaHighTmpDao.getTmpAvgByTimes(startDateTime, endDatetime);
		Date endDate = null, tempDate = null, startDate = null;
		int maxStationCnt = 0, persistDays = 1;
		for(int i = 0; i < resultList.size(); i++) {
			HashMap dataMap = (HashMap) resultList.get(i);
			Integer stationCnt = (Integer) dataMap.get("stationCnt");
			Timestamp datetime = (Timestamp) dataMap.get("datetime");
			String endTime = (String) dataMap.get("endTime");
			if(i == 0) {
				if(!endDatetime.equals(endTime)) {
					return null;
				} else {
					endDate = new Date(datetime.getTime());
					startDate = new Date(datetime.getTime());
					maxStationCnt = stationCnt;
				}
			} else {
				if(startDate.getTime() - datetime.getTime() == CommonConstant.DAYTIMES) {
					//连续
					startDate = new Date(datetime.getTime());
					persistDays++;
					if(stationCnt > maxStationCnt) {
						maxStationCnt = stationCnt;
					}
				} else {
					//结束
					if(persistDays >=2 && maxStationCnt >= 17) {
						//过程达到了标准
						List existDataList = areaHighTmpProcessDao.getTmpByStartTime(sdf.format(startDate));
						if(existDataList != null && existDataList.size() > 0) {
							boolean updateFlag = areaHighTmpProcessDao.updateTmpByStartTime(sdf.format(startDate), sdf.format(endDate), persistDays);
							if(!updateFlag) {
								return null;
							} else {
								return new String[]{sdf.format(startDate), sdf.format(endDate)};
							}
						} else {
							List dataList = new ArrayList();
							HashMap addDataMap = new HashMap();
							addDataMap.put("StartTime", sdf.format(startDate) + " 00:00:00");
							addDataMap.put("EndTime", sdf.format(endDate) + " 00:00:00");
							addDataMap.put("persistDays", persistDays);
							dataList.add(addDataMap);
							areaHighTmpProcessDao.insertTemAvgHouValue(dataList);
							return new String[]{sdf.format(startDate), sdf.format(endDate)};
						}
					}
					break;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据过程计算SI，G值等(单站)
	 * @param startDateTime
	 * @param endDateTime
	 */
	public void syncAreaHighTmpSI(String startDateTime, String endDateTime) {
		//t_areahightmpsi
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		AreaHighTmpProcessDao areaHighTmpProcessDao = new AreaHighTmpProcessDao();
		//高温过程
		List allHighTmpProcessDataList = areaHighTmpProcessDao.getTmpByStartTime(startDateTime);
		//高温数据
		HighTmpDaoImpl highTmpDaoImpl = new HighTmpDaoImpl();
		//SI
		AreaHighTmpSIDao areaHighTmpSIDao = new AreaHighTmpSIDao();
		
		List highTmpDataList = highTmpDaoImpl.getDataByTimeRange(startDateTime, endDateTime);
		List dataList = new ArrayList();
		for(int i = 0; i < allHighTmpProcessDataList.size(); i++) {
			HashMap itemMap = (HashMap) allHighTmpProcessDataList.get(i);
			Timestamp startTimeStamp = (Timestamp) itemMap.get("StartTime");
			Timestamp endTimeStamp = (Timestamp) itemMap.get("EndTime");
			HashMap<String, List> result = new HashMap<String, List>();
			for(int j = 0; j < highTmpDataList.size(); j++) {
				HashMap itemJMap = (HashMap)highTmpDataList.get(j);
				Timestamp jTimeStamp = (Timestamp) itemJMap.get("Datetime");
				if(jTimeStamp.getTime() >= startTimeStamp.getTime() && jTimeStamp.getTime() <= endTimeStamp.getTime()) {
					String station_Id_C = (String) itemJMap.get("Station_Id_C");
					List tempList = result.get(station_Id_C);
					if(tempList == null) {
						tempList = new ArrayList();
					}
					tempList.add(itemJMap);
					result.put(station_Id_C, tempList);
				} else if(jTimeStamp.getTime() > endTimeStamp.getTime()) {
					break;
				}
			}
			
			
			//处理结果
			
			Iterator<String> it = result.keySet().iterator();
			while(it.hasNext()) {
				Map dataMap = new HashMap();
				String key = it.next();
				dataMap.put("Station_Id_C", key);
				List list = result.get(key);
				double SI = 0;
				int level1Days = 0, level2Days = 0, level3Days = 0;
				for(int j = 0; j < list.size(); j++) {
					HashMap tempMap = (HashMap) list.get(j);
					if(j == 0) {
						Timestamp startTime = (Timestamp) tempMap.get("Datetime");
						dataMap.put("StartTime", sdf.format(new Date(startTime.getTime())) + " 00:00:00");
					}
					if(j == list.size() - 1) {
						Timestamp endTime = (Timestamp) tempMap.get("Datetime");
						dataMap.put("EndTime", sdf.format(new Date(endTime.getTime())) + " 00:00:00");
					}
					Double TEM_Max = (Double) tempMap.get("TEM_Max");
					String Station_Name = (String) tempMap.get("Station_Name");
					dataMap.put("Station_Name", Station_Name);
					if(TEM_Max >= 35 && TEM_Max < 37) {
						level1Days ++;
					} else if(TEM_Max >= 37 && TEM_Max < 40) {
						level2Days ++;
					} else if(TEM_Max >= 40) {
						level3Days++;
					}
				}
				SI = level1Days * 1 + level2Days * 2 + level3Days * 3;
				Integer G = 0;
				String level = "";
				if(SI  == 0) {
					G = 5;
					level = "无高温";
				} else if(SI >= 1 && SI < 4) {
					G = 4;
					level = "轻度";
				} else if(SI >=4 && SI < 11) {
					G = 3;
					level = "中度";
				} else if(SI >= 11 && SI < 23) {
					G = 2;
					level = "重度";
				} else if(SI >= 23) {
					G = 1;
					level = "特重";
				}
				dataMap.put("SI", SI);
				dataMap.put("G", G);
				dataMap.put("level", level);
				dataList.add(dataMap);
			}
		}
		boolean flag = areaHighTmpSIDao.deleteByTime(startDateTime);
		if(flag) {
			areaHighTmpSIDao.insertTemAvgHouValue(dataList);
		}
	}
	
	/**
	 * 根据过程计算区域过程的RI、DI值（区域）
	 * @param startDateTime
	 * @param endDateTime
	 */
	public void syncAreaHighAreaResult(String startDateTime, String endDateTime) {
		// 1. 先从t_areahightmpprocess查询过程
		// 2. 从t_areahightmpsi 查询高温的站数
		// 3. 计算RI，入库t_AreaHighAreaResult
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//高温过程
		AreaHighTmpProcessDao areaHighTmpProcessDao = new AreaHighTmpProcessDao();
		List allHighTmpProcessDataList = areaHighTmpProcessDao.getTmpAvgHou(startDateTime, endDateTime);
		//SI
		AreaHighTmpSIDao areaHighTmpSIDao = new AreaHighTmpSIDao();
		//结果
		AreaHighAreaResultDao areaHighAreaResultDao = new AreaHighAreaResultDao();
		//定义站点总数 34
		for(int i = 0; i < allHighTmpProcessDataList.size(); i++) {
			HashMap itemMap = (HashMap) allHighTmpProcessDataList.get(i);
			Timestamp startTimeStamp = (Timestamp) itemMap.get("StartTime");
			Timestamp endTimeStamp = (Timestamp) itemMap.get("EndTime");
			int persistDays = (Integer) itemMap.get("persistDays");
			String startTimeStr = sdf.format(new Date(startTimeStamp.getTime()));
			String endTimeStr = sdf.format(new Date(endTimeStamp.getTime()));
			List groupDataList = areaHighTmpSIDao.getLevelGroup(startTimeStr, endTimeStr);
			Double RI = 0.0;
			Integer DI = 0;
//			for(int j = 0; j < groupDataList.size(); j++) {
//				HashMap dataItem = (HashMap) groupDataList.get(j);
//				Long cnt = (Long) dataItem.get("cnt");
//				sum += cnt;
//			}
			for(int j = 0; j < groupDataList.size(); j++) {
				HashMap dataItem = (HashMap) groupDataList.get(j);
				Long cnt = (Long) dataItem.get("cnt");
				Integer level = (Integer) dataItem.get("G");
				RI += level * ((cnt + 0.0) / STATIONSUM);
			}
			if(RI >= 1 && RI < 2) {
				DI = 4;
			} else if(RI >= 2 && RI < 3) {
				DI = 3;
			} else if(RI >= 3 && RI < 4) {
				DI = 2;
			} else if(RI >= 4) {
				DI = 1;
			}
			String level = "";
			if(DI >= 1 && DI < 2) {
				level = "轻度";
			} else if(DI >= 2 && DI < 3) {
				level = "中度";
			} if(DI >= 3 && DI < 4) {
				level = "重度";
			} if(DI >= 4) {
				level = "特重";
			}  
			areaHighAreaResultDao.updateOrInsertData(startTimeStr + " 00:00:00", endTimeStr + " 00:00:00", persistDays, RI, level, DI);
		}
	}
	
	/**
	 * 计算年份的高温值
	 * @param year
	 */
	public void syncAreaHighTmpYearResult(int year) {
		AreaHighAreaResultDao areaHighAreaResultDao = new AreaHighAreaResultDao();
		List resultList = areaHighAreaResultDao.getTmpAvgByYear(year);
		Double YHI = 0.0;
		for(int i = 0; i < resultList.size(); i++) {
			HashMap itemMap = (HashMap) resultList.get(i);
			Double RI = (Double) itemMap.get("RI");
			Integer persistDays = (Integer) itemMap.get("persistDays");
			YHI += (persistDays + 0.0) / RI;
		}
		AreaHighTmpYearResultDao areaHighTmpYearResultDao = new AreaHighTmpYearResultDao();
		HashMap dataMap = new HashMap();
		dataMap.put("year", year);
		dataMap.put("YHI", YHI);
		areaHighTmpYearResultDao.updateOrInsert(dataMap, year);
	}
	
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		LogTool.logger.info("AreaHigTmp start..");
//		PropertiesUtil.loadSysCofing();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
////		String dateTimeStr = sdf.format(date);
//		String dateTimeStr = "2016-07-31";
//		AreaHighTmpSync areaHighTmpSync = new AreaHighTmpSync();
//		
//		boolean flag = areaHighTmpSync.syncAreaHighTmp(dateTimeStr);
//		if(!flag) return;
//		String[] timesRange = areaHighTmpSync.syncAreaHighTmpProcess(dateTimeStr);
//		if(timesRange == null) return;
////		String[] timesRange = new String[]{"2016-07-20", "2016-07-26"};
//		areaHighTmpSync.syncAreaHighTmpSI(timesRange[0], timesRange[1]);
//		areaHighTmpSync.syncAreaHighAreaResult(timesRange[0], timesRange[1]);
//		
//		areaHighTmpSync.syncAreaHighTmpYearResult(2016);
//	}

}
