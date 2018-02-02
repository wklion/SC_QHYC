package com.spd.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.ExtHisResult;
import com.spd.common.ExtParam;
import com.spd.common.ExtResult;
import com.spd.pojo.RankResult;
import com.spd.service.IExtStatistics;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.tool.LogTool;
import com.spd.util.CommonUtil;

public class ExtBus {

	/**
	 * 统计顺序时间范围的结果
	 * @param extParam
	 * @return
	 */
	public List<ExtResult> statisticsRangTime(ExtParam extParam) {
		//构造查询的要素
		String stationType = extParam.getStationType();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(extParam.getStartDateTime());
			endDate = sdf.parse(extParam.getEndDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		// 2. 做查询。
		IExtStatistics extStatistics = (IExtStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("ExtStatisticsImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("items", items);
		paramMap.put("tableName", extParam.getTableName());
		paramMap.put("startYear", extParam.getStartYear());
		paramMap.put("endYear", extParam.getEndYear());
		if("AWS".equals(stationType)) {
			paramMap.put("stations", "5%");
		} else if("MWS".equals(stationType)) {
			paramMap.put("stations", "A%");
		}
		List<Map> resultList = extStatistics.statisticsRangTime(paramMap);
		//3. 结果封装
		List<ExtResult> extResultList = disExtResult(resultList, extParam, startDate, endDate);
//		if(null != stationType && !"".equals(stationType)) {
//			//过滤
//			for(int i = extResultList.size() - 1; i >= 0; i--) {
//				ExtResult extResult = extResultList.get(i);
//				String station_Id_C = extResult.getStation_Id_C();
//				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
//					extResultList.remove(i);
//				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
//					extResultList.remove(i);
//				}
//			}
//		}
		//如果是降水，把降水为0的过滤掉，如果过滤完后，结果长度为0，则添加一条空的记录，平均风速最小的也过滤掉
		String tableName = extParam.getTableName();
		if(tableName.equals("t_pre_time_0808") || tableName.equals("t_pre_time_0820") ||
				tableName.equals("t_pre_time_2008") || tableName.equals("t_pre_time_2020") ||
				tableName.equals("t_win_s_2mi_avg")) {
			for(int i = extResultList.size() - 1; i >= 0; i--) {
				ExtResult extResult = extResultList.get(i);
				Double lowValue = extResult.getLowValue();
				if(lowValue == 0) {
					extResult.setLowDate("");
				}
				
				Double highValue = extResult.getHighValue();
				if(highValue == 0) {
					extResult.setHighDate("");
				}
			}
		}
		//重置索引
		for(int i = 0; i < extResultList.size(); i++) {
			ExtResult extResult = extResultList.get(i);
			extResult.setArea(CommonUtil.getInstance().stationAreaMap.get(extResult.getStation_Id_C()));
			extResult.setIndex(i + 1);
		}
		return extResultList;
	}
	
	/**
	 * 处理结果
	 * @param resultList
	 * @return
	 */
	private List<ExtResult> disExtResult(List<Map> resultList, ExtParam extParam, Date startDate, Date endDate) {
		String columnType = CommonTable.getInstance().getTypeByTableName(extParam.getTableName());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		Map<String, ExtResult> resultMap = new HashMap<String, ExtResult>();
		List<ExtResult> extResultList = new ArrayList<ExtResult>();
		Map<String, Integer> cntMap = new HashMap<String, Integer>();//计数器
		for(Map map : resultList) {
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			ExtResult extResult = resultMap.get(station_Id_C);
			if(extResult == null) {
				extResult = new ExtResult();
				extResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				extResult.setStation_Id_C(station_Id_C);
				resultMap.put(station_Id_C, extResult);
			}
			Integer cnt = cntMap.get(station_Id_C);
			if(cnt == null) {
				cnt = 0;
				cntMap.put(station_Id_C, cnt);
			}
			Set set = map.keySet();
			int year = (Integer) map.get("year");
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					String currentDateStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					Date currentDate = null;
					try {
						currentDate = sdf.parse(currentDateStr);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					long currentTime = currentDate.getTime();
					if(currentTime >= startTime && currentTime <= endTime) {
						cnt++;
						//低值
						Double lowValue = extResult.getLowValue();
						if(lowValue == null) {
							extResult.setLowValue(value);
							extResult.setLowDate(currentDateStr);
						} else if(lowValue > value) {
							extResult.setLowValue(value);
							extResult.setLowDate(currentDateStr);
						} else if(lowValue.doubleValue() == value) {
							extResult.setLowDate(extResult.getLowDate() + "," + currentDateStr);
						}
						//高值
						Double highValue = extResult.getHighValue();
						if(highValue == null) {
							extResult.setHighValue(value);
							extResult.setHighDate(currentDateStr);
						} else if(highValue < value) {
							extResult.setHighValue(value);
							extResult.setHighDate(currentDateStr);
						} else if(highValue.doubleValue() == value) {
							extResult.setHighDate(extResult.getHighDate() + "," + currentDateStr);
						}
						Double avgValue = extResult.getAvgValue();
						if(avgValue != null) {
							extResult.setAvgValue(avgValue + value);
							cntMap.put(station_Id_C, cnt);
						} else {
							extResult.setAvgValue(value);
						}
					}
				}
			}
		}
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		int index = 1;
		while(it.hasNext()) {
			String key = it.next();
			ExtResult extResult = resultMap.get(key);
			//处理平均值
			Double sumValue = extResult.getAvgValue();
			if(sumValue == null) {
				continue;
			}
			String station_Id_C = extResult.getStation_Id_C();
			int cnt = cntMap.get(station_Id_C);
			//保留一位小数
			double avg = sumValue / cnt;
			int avgInt = (int)(avg * 100);
			avg = Math.round(avgInt / 10.0);
			avg /= 10.0;
			extResult.setAvgValue(avg);
			extResult.setIndex(index++);
			extResultList.add(extResult);
		}
		return extResultList;
	}
	
	/**
	 * 处理历史的结果
	 * @param resultList
	 * @param extParam
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<ExtResult> disHisExtResult(List<Map> resultList, ExtParam extParam) {
		String columnType = CommonTable.getInstance().getTypeByTableName(extParam.getTableName());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		extParam.getStartDateTime();
		Map<String, ExtResult> resultMap = new HashMap<String, ExtResult>();
		List<ExtResult> extResultList = new ArrayList<ExtResult>();
		for(Map map : resultList) {
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			ExtResult extResult = resultMap.get(station_Id_C);
			if(extResult == null) {
				extResult = new ExtResult();
				extResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				extResult.setStation_Id_C(station_Id_C);
				resultMap.put(station_Id_C, extResult);
			}
			Set set = map.keySet();
			int year = (Integer) map.get("year");
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					String currentDateStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					Date currentDate = null;
					try {
						currentDate = sdf.parse(currentDateStr);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					//低值
					if(extResult.getLowValue() == null) {
						extResult.setLowValue(value);
					} else if(extResult.getLowValue() > value) {
						extResult.setLowValue(value);
						extResult.setLowDate(currentDateStr);
					} else if(extResult.getLowValue().doubleValue() == value) {
						extResult.setLowDate(extResult.getLowDate() + "," + currentDateStr);
					}
					//高值
					if(extResult.getHighValue() == null) {
						extResult.setHighValue(value);
					} else if(extResult.getHighValue() < value) {
						extResult.setHighValue(value);
						extResult.setHighDate(currentDateStr);
					} else if(extResult.getHighValue().doubleValue() == value) {
						extResult.setHighDate(extResult.getHighDate() + "," + currentDateStr);
					}
				}
			}
		}
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		int index = 1;
		while(it.hasNext()) {
			String key = it.next();
			ExtResult extResult = resultMap.get(key);
			extResult.setIndex(index++);
			extResultList.add(extResult);
		}
		return extResultList;
	}
	
	/**
	 * 统计历史同期时段的结果
	 * @param extParam
	 * @return
	 */
	public List<ExtHisResult> statisticsHisRangTime(ExtParam extParam) {
		//构造查询的要素
		String stationType = extParam.getStationType();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(extParam.getStartDateTime());
			endDate = sdf.parse(extParam.getEndDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		// 2. 做查询。
		IExtStatistics extStatistics = (IExtStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("ExtStatisticsImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("items", items);
		paramMap.put("tableName", extParam.getTableName());
		if("AWS".equals(stationType)) {
			paramMap.put("stations", "5%");
		} else if("MWS".equals(stationType)) {
			paramMap.put("stations", "A%");
		}
		List<Map> resultList = extStatistics.statisticsHisRangTime(paramMap);
		// 3. 处理结果
		// 历史结果
		List<ExtResult> hisExtResultList = disHisExtResult(resultList, extParam);
		//当年结果
		List<ExtResult> curExtResultList = disExtResult(resultList, extParam, startDate, endDate);
		List<ExtHisResult> resultExtHisResultList = disposeComResult(curExtResultList, hisExtResultList);
		
//		if(null != stationType && !"".equals(stationType)) {
//			//过滤
//			for(int i = resultExtHisResultList.size() - 1; i >= 0; i--) {
//				ExtHisResult extHisResult = resultExtHisResultList.get(i);
//				String station_Id_C = extHisResult.getStation_Id_C();
//				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
//					resultExtHisResultList.remove(i);
//				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
//					resultExtHisResultList.remove(i);
//				}
//			}
//		}
		//如果是降水，把降水为0的过滤掉，如果过滤完后，结果长度为0，则添加一条空的记录
		String tableName = extParam.getTableName();
		if(tableName.equals("t_pre_time_0808") || tableName.equals("t_pre_time_0820") ||
				tableName.equals("t_pre_time_2008") || tableName.equals("t_pre_time_2020") ||
				tableName.equals("t_win_s_2mi_avg")) {
			for(int i = resultExtHisResultList.size() - 1; i >= 0; i--) {
				ExtHisResult extHisResult = resultExtHisResultList.get(i);
				Double lowValue = extHisResult.getLowValue();
				if(lowValue == 0) {
					extHisResult.setLowDate("");
				}
				if(extHisResult.getHisLowValue() == 0) {
					extHisResult.setHisLowDate("");
				}
				
				Double highValue = extHisResult.getHighValue();
				if(highValue == 0) {
					extHisResult.setHighDate("");
				}
			}
		}
		//重置索引，加上地区
		for(int i = 0; i < resultExtHisResultList.size(); i++) {
			ExtHisResult extHisResult = resultExtHisResultList.get(i);
			extHisResult.setArea(CommonUtil.getInstance().stationAreaMap.get(extHisResult.getStation_Id_C()));
			extHisResult.setIndex(i + 1);
		}
		return resultExtHisResultList;
	}
	
	/**
	 * 处理对比的结果
	 * @param curExtResultList
	 * @param hisExtResultList
	 * @return
	 */
	private List<ExtHisResult> disposeComResult(List<ExtResult> curExtResultList, List<ExtResult> hisExtResultList) {
		List<ExtHisResult> extHisResultList = new ArrayList<ExtHisResult>();
		Map<String, ExtHisResult> extHisResultMap = new HashMap<String, ExtHisResult>();
		for(ExtResult extResult : curExtResultList) {
			String station_id_C = extResult.getStation_Id_C();
			ExtHisResult extHisResult = new ExtHisResult();
			extHisResult.setStation_Id_C(station_id_C);
			extHisResult.setStation_Name(extResult.getStation_Name());
			extHisResult.setIndex(extResult.getIndex());
			extHisResult.setHighDate(extResult.getHighDate());
			extHisResult.setHighValue(extResult.getHighValue());
			extHisResult.setLowDate(extResult.getLowDate());
			extHisResult.setLowValue(extResult.getLowValue());
			extHisResult.setAvgValue(extResult.getAvgValue());
			extHisResultMap.put(station_id_C, extHisResult);
		}
		for(ExtResult hisExtResult : hisExtResultList) {
			String station_Id_C = hisExtResult.getStation_Id_C();
			ExtHisResult curExtHisResult = extHisResultMap.get(station_Id_C);
			if(curExtHisResult != null) {
				curExtHisResult.setHisHighDate(hisExtResult.getHighDate());
				curExtHisResult.setHisHighValue(hisExtResult.getHighValue());
				curExtHisResult.setHisLowDate(hisExtResult.getLowDate());
				curExtHisResult.setHisLowValue(hisExtResult.getLowValue());
				extHisResultList.add(curExtHisResult);
			}
		}
		return extHisResultList;
	}
}
