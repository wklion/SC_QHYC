package com.spd.schedule.test;

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
import com.spd.tool.PropertiesUtil;

/**
 * 生成高温历史过程
 * @author Administrator
 *
 */
public class AreaHighTmpHis {
	
	private List areaHighTmpList = new ArrayList();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private AreaHighTmpDao areaHighTmpDao = new AreaHighTmpDao();

	private AreaHighTmpProcessDao areaHighTmpProcessDao = new AreaHighTmpProcessDao();
	
	public AreaHighTmpHis() {
		areaHighTmpList = areaHighTmpDao.getTmpAvgHou();
	}
	
	public void disList() {
		Date startDate = null, endDate = null;
		int persistDays = 0;
		int maxStationCnt = 0;
		for(int i = 0; i < areaHighTmpList.size() - 1; i++) {
			HashMap itemMap = (HashMap) areaHighTmpList.get(i);
			System.out.println(itemMap);
			if(itemMap.toString().equals("{stationCnt=10, datetime=2008-08-05 00:00:00.0}")) {
				System.out.println("");
			}
			Date iDate = new Date(((java.sql.Timestamp) itemMap.get("datetime")).getTime());
			maxStationCnt = (Integer) itemMap.get("stationCnt");
			persistDays = 1;
			startDate = iDate;
			for(int j = i + 1; j < areaHighTmpList.size(); j++) {
				HashMap jItemMap = (HashMap) areaHighTmpList.get(j);
				Date jDate = new Date(((java.sql.Timestamp) jItemMap.get("datetime")).getTime());
				if(jDate.getTime() - iDate.getTime() == CommonConstant.DAYTIMES) {
					//连续的天
					int tempMaxStationCnt = (Integer) jItemMap.get("stationCnt");
					if(tempMaxStationCnt > maxStationCnt) {
						maxStationCnt = tempMaxStationCnt;
					}
					
					iDate = jDate;
					endDate = jDate;
					persistDays++;
				} else {
					if(persistDays != 1 && maxStationCnt >= 17) {
						//过程结束
						List dataList = new ArrayList();
						HashMap dataMap = new HashMap();
						dataMap.put("StartTime", sdf.format(startDate) + " 00:00:00");
						dataMap.put("EndTime", sdf.format(endDate) + " 00:00:00");
						dataMap.put("persistDays", persistDays);
						dataList.add(dataMap);
						areaHighTmpProcessDao.insertTemAvgHouValue(dataList);
						persistDays = 0;
						i = j - 1;
					}
					break;
				}
				
			}
		}
	}
	
	/**
	 * 计算SI的值
	 */
	public void disSI() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		AreaHighTmpProcessDao areaHighTmpProcessDao = new AreaHighTmpProcessDao();
		//高温过程
		List allHighTmpProcessDataList = areaHighTmpProcessDao.getALLData();
		//高温数据
		HighTmpDaoImpl highTmpDaoImpl = new HighTmpDaoImpl();
		//SI
		AreaHighTmpSIDao areaHighTmpSIDao = new AreaHighTmpSIDao();
		List highTmpDataList = highTmpDaoImpl.getAllData();
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
				List dataList = new ArrayList();
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
				dataMap.put("SI", SI);
				dataList.add(dataMap);
				areaHighTmpSIDao.insertTemAvgHouValue(dataList);
			}
			
		}
	}
	
	/**
	 * 计算区域高温指数
	 */
	public void disRI() {
		// 1. 先从t_areahightmpprocess查询过程
		// 2. 从t_areahightmpsi 查询高温的站数
		// 3. 计算RI，入库t_AreaHighAreaResult
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//高温过程
		List allHighTmpProcessDataList = areaHighTmpProcessDao.getALLData();
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
			int sum = 0;
			for(int j = 0; j < groupDataList.size(); j++) {
				HashMap dataItem = (HashMap) groupDataList.get(j);
				Long cnt = (Long) dataItem.get("cnt");
				sum += cnt;
			}
			for(int j = 0; j < groupDataList.size(); j++) {
				HashMap dataItem = (HashMap) groupDataList.get(j);
				Long cnt = (Long) dataItem.get("cnt");
				Integer level = (Integer) dataItem.get("G");
				RI += level * ((cnt + 0.0) / sum);
			}
			HashMap dataMap = new HashMap();
			dataMap.put("StartTime", startTimeStr + " 00:00:00");
			dataMap.put("EndTime", endTimeStr + " 00:00:00");
			dataMap.put("persistDays", persistDays);
			dataMap.put("RI", RI);
			List dataList = new ArrayList();
			dataList.add(dataMap);
			areaHighAreaResultDao.insertTemAvgHouValue(dataList);
		}
	}
	
	/**
	 * 年度综合指数
	 */
	public void disYHI() {
		//1. t_areahigharearesult中查询结果
		AreaHighAreaResultDao areaHighAreaResultDao = new AreaHighAreaResultDao();
		AreaHighTmpYearResultDao areaHighTmpYearResultDao = new AreaHighTmpYearResultDao();
		List resultList = areaHighAreaResultDao.getTmpAvgHouGroupByYear();
		HashMap<Integer, List> yearDataMap = new HashMap<Integer, List>();
		for(int i = 0; i < resultList.size(); i++) {
			HashMap itemMap = (HashMap) resultList.get(i);
			Integer year = Integer.parseInt((String) itemMap.get("year"));
			List list = yearDataMap.get(year);
			if(list == null) {
				list = new ArrayList();
			}
			list.add(itemMap);
			yearDataMap.put(year, list);
		}
		Iterator<Integer> it = yearDataMap.keySet().iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List yearList = yearDataMap.get(year);
			Double YHI = 0.0;
			for(int i = 0; i < yearList.size(); i++) {
				HashMap iteMap = (HashMap) yearList.get(i);
				Double RI = (Double) iteMap.get("RI");
				Integer persistDays = (Integer) iteMap.get("persistDays");
				YHI += (persistDays + 0.0) / RI;
			}
			List dataList = new ArrayList();
			HashMap dataMap = new HashMap();
			dataMap.put("year", year);
			dataMap.put("YHI", YHI);
			dataList.add(dataMap);
			areaHighTmpYearResultDao.insertTemAvgHouValue(dataList);
		}
		//2. 计算，结果入库到t_AreaHighTmpYearResult
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		AreaHighTmpHis areaHighTmpHis = new AreaHighTmpHis();
		areaHighTmpHis.disYHI();
//		areaHighTmpHis.disRI();
//		areaHighTmpHis.disSI();
//		areaHighTmpHis.disList();
	}

}
